// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.multiplayer;

import net.minecraft.network.packet.ContainerClickPacket;
import net.minecraft.network.packet.InteractPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.packet.UseItemPacket;
import net.minecraft.network.packet.SetCarriedItemPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PlayerActionPacket;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gamemode.GameMode;

public class MultiPlayerGameMode extends GameMode
{
    private int xDestroyBlock;
    private int yDestroyBlock;
    private int zDestroyBlock;
    private float destroyProgress;
    private float oDestroyProgress;
    private float destroyTicks;
    private int destroyDelay;
    private boolean isDestroying;
    private ClientConnection connection;
    private int carriedItem;
    
    public MultiPlayerGameMode(final Minecraft minecraft, final ClientConnection connection) {
        super(minecraft);
        this.xDestroyBlock = -1;
        this.yDestroyBlock = -1;
        this.zDestroyBlock = -1;
        this.destroyProgress = 0.0f;
        this.oDestroyProgress = 0.0f;
        this.destroyTicks = 0.0f;
        this.destroyDelay = 0;
        this.isDestroying = false;
        this.carriedItem = 0;
        this.connection = connection;
    }
    
    @Override
    public void initPlayer(final Player player) {
        player.yRot = -180.0f;
    }
    
    @Override
    public boolean destroyBlock(final int x, final int y, final int z, final int face) {
        final int tile = this.minecraft.level.getTile(x, y, z);
        final boolean destroyBlock = super.destroyBlock(x, y, z, face);
        final ItemInstance selectedItem = this.minecraft.player.getSelectedItem();
        if (selectedItem != null) {
            selectedItem.mineBlock(tile, x, y, z, this.minecraft.player);
            if (selectedItem.count == 0) {
                selectedItem.snap(this.minecraft.player);
                this.minecraft.player.removeSelectedItem();
            }
        }
        return destroyBlock;
    }
    
    @Override
    public void startDestroyBlock(final int x, final int y, final int z, final int face) {
        if (!this.isDestroying || x != this.xDestroyBlock || y != this.yDestroyBlock || z != this.zDestroyBlock) {
            this.connection.send(new PlayerActionPacket(0, x, y, z, face));
            final int tile = this.minecraft.level.getTile(x, y, z);
            if (tile > 0 && this.destroyProgress == 0.0f) {
                Tile.tiles[tile].attack(this.minecraft.level, x, y, z, this.minecraft.player);
            }
            if (tile > 0 && Tile.tiles[tile].getDestroyProgress(this.minecraft.player) >= 1.0f) {
                this.destroyBlock(x, y, z, face);
            }
            else {
                this.isDestroying = true;
                this.xDestroyBlock = x;
                this.yDestroyBlock = y;
                this.zDestroyBlock = z;
                this.destroyProgress = 0.0f;
                this.oDestroyProgress = 0.0f;
                this.destroyTicks = 0.0f;
            }
        }
    }
    
    @Override
    public void stopDestroyBlock() {
        this.destroyProgress = 0.0f;
        this.isDestroying = false;
    }
    
    @Override
    public void continueDestroyBlock(final int x, final int y, final int z, final int face) {
        if (!this.isDestroying) {
            return;
        }
        this.ensureHasSentCarriedItem();
        if (this.destroyDelay > 0) {
            --this.destroyDelay;
            return;
        }
        if (x == this.xDestroyBlock && y == this.yDestroyBlock && z == this.zDestroyBlock) {
            final int tile = this.minecraft.level.getTile(x, y, z);
            if (tile == 0) {
                this.isDestroying = false;
                return;
            }
            final Tile tile2 = Tile.tiles[tile];
            this.destroyProgress += tile2.getDestroyProgress(this.minecraft.player);
            if (this.destroyTicks % 4.0f == 0.0f && tile2 != null) {
                this.minecraft.soundEngine.play(tile2.soundType.getStepSound(), x + 0.5f, y + 0.5f, z + 0.5f, (tile2.soundType.getVolume() + 1.0f) / 8.0f, tile2.soundType.getPitch() * 0.5f);
            }
            ++this.destroyTicks;
            if (this.destroyProgress >= 1.0f) {
                this.isDestroying = false;
                this.connection.send(new PlayerActionPacket(2, x, y, z, face));
                this.destroyBlock(x, y, z, face);
                this.destroyProgress = 0.0f;
                this.oDestroyProgress = 0.0f;
                this.destroyTicks = 0.0f;
                this.destroyDelay = 5;
            }
        }
        else {
            this.startDestroyBlock(x, y, z, face);
        }
    }
    
    @Override
    public void render(final float partialTick) {
        if (this.destroyProgress <= 0.0f) {
            this.minecraft.gui.progress = 0.0f;
            this.minecraft.levelRenderer.destroyProgress = 0.0f;
        }
        else {
            final float n = this.oDestroyProgress + (this.destroyProgress - this.oDestroyProgress) * partialTick;
            this.minecraft.gui.progress = n;
            this.minecraft.levelRenderer.destroyProgress = n;
        }
    }
    
    @Override
    public float getPickRange() {
        return 4.0f;
    }
    
    @Override
    public void initLevel(final Level level) {
        super.initLevel(level);
    }
    
    @Override
    public void tick() {
        this.ensureHasSentCarriedItem();
        this.oDestroyProgress = this.destroyProgress;
        this.minecraft.soundEngine.playMusicTick();
    }
    
    private void ensureHasSentCarriedItem() {
        final int selected = this.minecraft.player.inventory.selected;
        if (selected != this.carriedItem) {
            this.carriedItem = selected;
            this.connection.send(new SetCarriedItemPacket(this.carriedItem));
        }
    }
    
    @Override
    public boolean useItemOn(final Player player, final Level level, final ItemInstance item, final int x, final int y, final int z, final int face) {
        this.ensureHasSentCarriedItem();
        this.connection.send(new UseItemPacket(x, y, z, face, player.inventory.getSelected()));
        return super.useItemOn(player, level, item, x, y, z, face);
    }
    
    @Override
    public boolean useItem(final Player player, final Level level, final ItemInstance item) {
        this.ensureHasSentCarriedItem();
        this.connection.send(new UseItemPacket(-1, -1, -1, 255, player.inventory.getSelected()));
        return super.useItem(player, level, item);
    }
    
    @Override
    public Player createPlayer(final Level level) {
        return new MultiplayerLocalPlayer(this.minecraft, level, this.minecraft.user, this.connection);
    }
    
    @Override
    public void attack(final Player player, final Entity entity) {
        this.ensureHasSentCarriedItem();
        this.connection.send(new InteractPacket(player.entityId, entity.entityId, 1));
        player.attack(entity);
    }
    
    @Override
    public void interact(final Player player, final Entity entity) {
        this.ensureHasSentCarriedItem();
        this.connection.send(new InteractPacket(player.entityId, entity.entityId, 0));
        player.interact(entity);
    }
    
    @Override
    public ItemInstance handleInventoryMouseClick(final int containerId, final int slotNum, final int buttonNum, final boolean quickKeyHeld, final Player player) {
        final short backup = player.containerMenu.backup(player.inventory);
        final ItemInstance handleInventoryMouseClick = super.handleInventoryMouseClick(containerId, slotNum, buttonNum, quickKeyHeld, player);
        this.connection.send(new ContainerClickPacket(containerId, slotNum, buttonNum, quickKeyHeld, handleInventoryMouseClick, backup));
        return handleInventoryMouseClick;
    }
    
    @Override
    public void handleCloseInventory(final int containerId, final Player player) {
        if (containerId == -9999) {
            return;
        }
    }
}
