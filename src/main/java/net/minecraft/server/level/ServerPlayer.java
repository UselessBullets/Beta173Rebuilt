// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.level;

import net.minecraft.network.packet.ChatPacket;
import net.minecraft.locale.language.Language;
import net.minecraft.network.packet.AwardStatPacket;
import net.minecraft.stats.Stat;
import net.minecraft.network.packet.ContainerClosePacket;
import net.minecraft.network.packet.ContainerSetDataPacket;
import net.minecraft.network.packet.ContainerSetContentPacket;
import net.minecraft.network.packet.ContainerSetSlotPacket;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.TrapMenu;
import net.minecraft.world.level.tile.entity.DispenserTileEntity;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.level.tile.entity.FurnaceTileEntity;
import net.minecraft.world.inventory.ContainerMenu;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.network.packet.ContainerOpenPacket;
import net.minecraft.network.packet.SetRidingPacket;
import net.minecraft.network.packet.EntityActionAtPositionPacket;
import net.minecraft.network.packet.AnimatePacket;
import net.minecraft.server.EntityTracker;
import net.minecraft.network.packet.TakeItemEntityPacket;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.network.packet.SetHealthPacket;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.network.packet.BlockRegionUpdatePacket;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.item.ComplexItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.packet.SetEquippedItemPacket;
import net.minecraft.Pos;
import java.util.HashSet;
import java.util.LinkedList;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemInstance;
import java.util.Set;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.entity.player.Player;

public class ServerPlayer extends Player implements ContainerListener
{
    public PlayerConnection connection;
    public MinecraftServer server;
    public ServerPlayerGameMode gameMode;
    public double lastMoveX;
    public double lastMoveZ;
    public List<ChunkPos> chunksToSend;
    public Set<ChunkPos> seenChunks;
    private int lastSentHealth;
    private int invulnerableTime2;
    private ItemInstance[] lastCarried;
    private int containerCounter;
    public boolean ignoreSlotUpdateHack;
    
    public ServerPlayer(final MinecraftServer server, final Level level, final String name, final ServerPlayerGameMode gameMode) {
        super(level);
        this.chunksToSend = new LinkedList<>();
        this.seenChunks = new HashSet<>();
        this.lastSentHealth = -99999999;
        this.invulnerableTime2 = 60;
        this.lastCarried = new ItemInstance[] { null, null, null, null, null };
        this.containerCounter = 0;
        gameMode.player = this;
        this.gameMode = gameMode;
        final Pos sharedSpawnPos = level.getSharedSpawnPos();
        int x = sharedSpawnPos.x;
        int z = sharedSpawnPos.z;
        int n = sharedSpawnPos.y;
        if (!level.dimension.hasCeiling) {
            x += this.random.nextInt(20) - 10;
            n = level.getTopSolidBlock(x, z);
            z += this.random.nextInt(20) - 10;
        }
        this.moveTo(x + 0.5, n, z + 0.5, 0.0f, 0.0f);
        this.server = server;
        this.footSize = 0.0f;
        this.name = name;
        this.heightOffset = 0.0f;
    }
    
    @Override
    public void setLevel(final Level level) {
        super.setLevel(level);
        this.gameMode = new ServerPlayerGameMode((ServerLevel)level);
        this.gameMode.player = this;
    }
    
    public void initMenu() {
        this.containerMenu.addSlotListener(this);
    }
    
    @Override
    public ItemInstance[] getEquipmentSlots() {
        return this.lastCarried;
    }
    
    @Override
    protected void setDefaultHeadHeight() {
        this.heightOffset = 0.0f;
    }
    
    @Override
    public float getHeadHeight() {
        return 1.62f;
    }
    
    @Override
    public void tick() {
        this.gameMode.tick();
        --this.invulnerableTime2;
        this.containerMenu.broadcastChanges();
        for (int i = 0; i < 5; ++i) {
            final ItemInstance carried = this.getCarried(i);
            if (carried != this.lastCarried[i]) {
                this.server.getTracker(this.dimension).broadcast(this, new SetEquippedItemPacket(this.entityId, i, carried));
                this.lastCarried[i] = carried;
            }
        }
    }
    
    public ItemInstance getCarried(final int slot) {
        if (slot == 0) {
            return this.inventory.getSelected();
        }
        return this.inventory.armor[slot - 1];
    }
    
    @Override
    public void die(final Entity source) {
        this.inventory.dropAll();
    }
    
    @Override
    public boolean hurt(final Entity source, final int dmg) {
        if (this.invulnerableTime2 > 0) {
            return false;
        }
        if (!this.server.pvp) {
            if (source instanceof Player) {
                return false;
            }
            if (source instanceof Arrow && ((Arrow)source).owner instanceof Player) {
                return false;
            }
        }
        return super.hurt(source, dmg);
    }
    
    @Override
    protected boolean isPlayerVersusPlayer() {
        return this.server.pvp;
    }
    
    @Override
    public void heal(final int heal) {
        super.heal(heal);
    }
    
    public void doTick(final boolean sendChunks) {
        super.tick();
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            final ItemInstance item = this.inventory.getItem(i);
            if (item != null && Item.items[item.id].isComplex() && this.connection.countDelayedPackets() <= 2) {
                final Packet updatePacket = ((ComplexItem)Item.items[item.id]).getUpdatePacket(item, this.level, this);
                if (updatePacket != null) {
                    this.connection.send(updatePacket);
                }
            }
        }
        if (sendChunks && !this.chunksToSend.isEmpty()) {
            final ChunkPos chunkPos = this.chunksToSend.get(0);
            if (chunkPos != null) {
                boolean b = false;
                if (this.connection.countDelayedPackets() < 4) {
                    b = true;
                }
                if (b) {
                    final ServerLevel level = this.server.getLevel(this.dimension);
                    this.chunksToSend.remove(chunkPos);
                    this.connection.send(new BlockRegionUpdatePacket(chunkPos.x * 16, 0, chunkPos.z * 16, 16, 128, 16, level));
                    final List tileEntitiesInRegion = level.getTileEntitiesInRegion(chunkPos.x * 16, 0, chunkPos.z * 16, chunkPos.x * 16 + 16, 128, chunkPos.z * 16 + 16);
                    for (int j = 0; j < tileEntitiesInRegion.size(); ++j) {
                        this.broadcast((TileEntity)tileEntitiesInRegion.get(j));
                    }
                }
            }
        }
        if (this.isInsidePortal) {
            if (this.server.settings.getBoolean("allow-nether", true)) {
                if (this.containerMenu != this.inventoryMenu) {
                    this.closeContainer();
                }
                if (this.riding != null) {
                    this.ride(this.riding);
                }
                else {
                    this.portalTime += 0.0125f;
                    if (this.portalTime >= 1.0f) {
                        this.portalTime = 1.0f;
                        this.changingDimensionDelay = 10;
                        this.server.players.toggleDimension(this);
                    }
                }
                this.isInsidePortal = false;
            }
        }
        else {
            if (this.portalTime > 0.0f) {
                this.portalTime -= 0.05f;
            }
            if (this.portalTime < 0.0f) {
                this.portalTime = 0.0f;
            }
        }
        if (this.changingDimensionDelay > 0) {
            --this.changingDimensionDelay;
        }
        if (this.health != this.lastSentHealth) {
            this.connection.send(new SetHealthPacket(this.health));
            this.lastSentHealth = this.health;
        }
    }
    
    private void broadcast(final TileEntity te) {
        if (te != null) {
            final Packet updatePacket = te.getUpdatePacket();
            if (updatePacket != null) {
                this.connection.send(updatePacket);
            }
        }
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
    }
    
    @Override
    public void take(final Entity e, final int orgCount) {
        if (!e.removed) {
            final EntityTracker tracker = this.server.getTracker(this.dimension);
            if (e instanceof ItemEntity) {
                tracker.broadcast(e, new TakeItemEntityPacket(e.entityId, this.entityId));
            }
            if (e instanceof Arrow) {
                tracker.broadcast(e, new TakeItemEntityPacket(e.entityId, this.entityId));
            }
        }
        super.take(e, orgCount);
        this.containerMenu.broadcastChanges();
    }
    
    @Override
    public void swing() {
        if (!this.swinging) {
            this.swingTime = -1;
            this.swinging = true;
            this.server.getTracker(this.dimension).broadcast(this, new AnimatePacket(this, 1));
        }
    }

    @Override
    public void animateRespawn() {

    }

    public void x() { // TODO find proper name
    }
    
    @Override
    public BedSleepingResult startSleepInBed(final int x, final int y, final int z) {
        final BedSleepingResult startSleepInBed = super.startSleepInBed(x, y, z);
        if (startSleepInBed == BedSleepingResult.OK) {
            final EntityTracker tracker = this.server.getTracker(this.dimension);
            final EntityActionAtPositionPacket entityActionAtPositionPacket = new EntityActionAtPositionPacket(this, EntityActionAtPositionPacket.START_SLEEP, x, y, z);
            tracker.broadcast(this, entityActionAtPositionPacket);
            this.connection.teleport(this.x, this.y, this.z, this.yRot, this.xRot);
            this.connection.send(entityActionAtPositionPacket);
        }
        return startSleepInBed;
    }
    
    @Override
    public void stopSleepInBed(final boolean forcefulWakeUp, final boolean updateLevelList, final boolean saveRespawnPoint) {
        if (this.isSleeping()) {
            this.server.getTracker(this.dimension).broadcastAndSend(this, new AnimatePacket(this, 3));
        }
        super.stopSleepInBed(forcefulWakeUp, updateLevelList, saveRespawnPoint);
        if (this.connection != null) {
            this.connection.teleport(this.x, this.y, this.z, this.yRot, this.xRot);
        }
    }
    
    @Override
    public void ride(final Entity e) {
        super.ride(e);
        this.connection.send(new SetRidingPacket(this, this.riding));
        this.connection.teleport(this.x, this.y, this.z, this.yRot, this.xRot);
    }
    
    @Override
    protected void checkFallDamage(final double ya, final boolean onGround) {
    }
    
    public void doCheckFallDamage(final double ya, final boolean onGround) {
        super.checkFallDamage(ya, onGround);
    }
    
    private void nextContainerCounter() {
        this.containerCounter = this.containerCounter % 100 + 1;
    }
    
    @Override
    public void startCrafting(final int x, final int y, final int z) {
        this.nextContainerCounter();
        this.connection.send(new ContainerOpenPacket(this.containerCounter, 1, "Crafting", 9));
        this.containerMenu = new CraftingMenu(this.inventory, this.level, x, y, z);
        this.containerMenu.containerId = this.containerCounter;
        this.containerMenu.addSlotListener(this);
    }
    
    @Override
    public void openContainer(final Container container) {
        this.nextContainerCounter();
        this.connection.send(new ContainerOpenPacket(this.containerCounter, 0, container.getName(), container.getContainerSize()));
        this.containerMenu = new ContainerMenu(this.inventory, container);
        this.containerMenu.containerId = this.containerCounter;
        this.containerMenu.addSlotListener(this);
    }
    
    @Override
    public void openFurnace(final FurnaceTileEntity furnace) {
        this.nextContainerCounter();
        this.connection.send(new ContainerOpenPacket(this.containerCounter, 2, furnace.getName(), furnace.getContainerSize()));
        this.containerMenu = new FurnaceMenu(this.inventory, furnace);
        this.containerMenu.containerId = this.containerCounter;
        this.containerMenu.addSlotListener(this);
    }
    
    @Override
    public void openTrap(final DispenserTileEntity trap) {
        this.nextContainerCounter();
        this.connection.send(new ContainerOpenPacket(this.containerCounter, 3, trap.getName(), trap.getContainerSize()));
        this.containerMenu = new TrapMenu(this.inventory, trap);
        this.containerMenu.containerId = this.containerCounter;
        this.containerMenu.addSlotListener(this);
    }
    
    public void slotChanged(final AbstractContainerMenu container, final int slotIndex, final ItemInstance item) {
        if (container.getSlot(slotIndex) instanceof ResultSlot) {
            return;
        }
        if (this.ignoreSlotUpdateHack) {
            return;
        }
        this.connection.send(new ContainerSetSlotPacket(container.containerId, slotIndex, item));
    }
    
    public void refreshContainer(final AbstractContainerMenu menu) {
        this.refreshContainer(menu, menu.getItems());
    }
    
    public void refreshContainer(final AbstractContainerMenu container, final List<ItemInstance> items) {
        this.connection.send(new ContainerSetContentPacket(container.containerId, items));
        this.connection.send(new ContainerSetSlotPacket(-1, -1, this.inventory.getCarried()));
    }
    
    public void setContainerData(final AbstractContainerMenu container, final int id, final int value) {
        this.connection.send(new ContainerSetDataPacket(container.containerId, id, value));
    }
    
    @Override
    public void handleCollectItem(final ItemInstance carried) {
    }
    
    public void closeContainer() {
        this.connection.send(new ContainerClosePacket(this.containerMenu.containerId));
        this.doCloseContainer();
    }
    
    public void broadcastCarriedItem() {
        if (this.ignoreSlotUpdateHack) {
            return;
        }
        this.connection.send(new ContainerSetSlotPacket(-1, -1, this.inventory.getCarried()));
    }
    
    public void doCloseContainer() {
        this.containerMenu.removed(this);
        this.containerMenu = this.inventoryMenu;
    }
    
    public void setPlayerInput(final float xa, final float ya, final boolean jumping, final boolean sneaking, final float xRot, final float yRot) {
        this.xxa = xa;
        this.yya = ya;
        this.jumping = jumping;
        this.setSneaking(sneaking);
        this.xRot = xRot;
        this.yRot = yRot;
    }
    
    @Override
    public void awardStat(final Stat stat, int count) {
        if (stat == null) {
            return;
        }
        if (!stat.awardLocallyOnly) {
            while (count > 100) {
                this.connection.send(new AwardStatPacket(stat.id, 100));
                count -= 100;
            }
            this.connection.send(new AwardStatPacket(stat.id, count));
        }
    }
    
    public void disconnect() {
        if (this.riding != null) {
            this.ride(this.riding);
        }
        if (this.rider != null) {
            this.rider.ride(this);
        }
        if (this.isSleeping) {
            this.stopSleepInBed(true, false, false);
        }
    }
    
    public void resetSentInfo() {
        this.lastSentHealth = -99999999;
    }
    
    @Override
    public void displayClientMessage(final String message) {
        this.connection.send(new ChatPacket(Language.getInstance().getElement(message)));
    }
}
