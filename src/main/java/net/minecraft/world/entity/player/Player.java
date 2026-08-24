// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.player;

import net.minecraft.Direction;
import net.minecraft.SharedConstants;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.item.Boat;
import net.minecraft.stats.Achievements;
import net.minecraft.world.entity.item.Minecart;
import net.minecraft.stats.Stat;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.tile.BedTile;
import net.minecraft.world.level.tile.entity.SignTileEntity;
import net.minecraft.world.level.tile.entity.DispenserTileEntity;
import net.minecraft.world.level.tile.entity.FurnaceTileEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.Container;
import com.mojang.nbt.ListTag;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.Item;
import java.util.List;
import net.minecraft.world.entity.Entity;
import util.Mth;
import net.minecraft.stats.Stats;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.Pos;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.Mob;

public abstract class Player extends Mob
{
    public static final int MAX_NAME_LENGTH = 16; // Useless - Was "16 + 4" in LCE leak, however the numeric values where the constant was used in b1.7.3 show that it was actually 16
    public static final int MAX_HEALTH = 20;
    public static final int SWING_DURATION = 6;
    public static final int SLEEP_DURATION = 100;
    public static final int WAKE_UP_DURATION = 10;

    public static final int CHAT_VISIBILITY_FULL = 0;
    public static final int CHAT_VISIBILITY_SYSTEM = 1;
    public static final int CHAT_VISIBILITY_HIDDEN = 2;

    private static final int DATA_PLAYER_FLAGS_ID = 16;

    public Inventory inventory = new Inventory(this);
    public AbstractContainerMenu inventoryMenu;
    public AbstractContainerMenu containerMenu;
    public byte userType = 0;
    public int score = 0;
    public float oBob, bob;
    public boolean swinging = false;
    public int swingTime = 0;
    public String name;
    public int dimension;
    public String cloakTexture;
    public double xCloakO, yCloakO, zCloakO;
    public double xCloak, yCloak, zCloak;
    protected boolean isSleeping;
    public Pos bedPosition;
    private int sleepCounter;
    public float bedOffsetX, bedOffsetY, bedOffsetZ;
    private Pos respawnPosition;
    private Pos minecartAchievementPos;
    public int changingDimensionDelay = 20;
    protected boolean isInsidePortal = false;
    public float portalTime, oPortalTime;
    private int dmgSpill = 0;
    public FishingHook fishing = null;
    
    public Player(final Level level) {
        super(level);
        this.inventoryMenu = new InventoryMenu(this.inventory, !level.isClientSide);
        this.containerMenu = this.inventoryMenu;
        this.heightOffset = 1.62f;
        final Pos spawnPos = level.getSharedSpawnPos();
        this.moveTo(spawnPos.x + 0.5, spawnPos.y + 1, spawnPos.z + 0.5, 0.0f, 0.0f);
        this.health = MAX_HEALTH;
        this.modelName = "humanoid";
        this.rotOffs = 180.0f;
        this.flameTime = 20;
        this.textureName = "/mob/char.png";
    }
    
    @Override
    protected void definedSynchedData() {
        super.definedSynchedData();
        this.entityData.define(DATA_PLAYER_FLAGS_ID, 0);
    }
    
    @Override
    public void tick() {
        if (this.isSleeping()) {
            this.sleepCounter++;
            if (this.sleepCounter > SLEEP_DURATION) {
                this.sleepCounter = SLEEP_DURATION;
            }

            if (!this.level.isClientSide) {
                if (!this.checkBed()) {
                    this.stopSleepInBed(true, true, false);
                }
                else if (this.level.isDay()) {
                    this.stopSleepInBed(false, true, true);
                }
            }
        }
        else if (this.sleepCounter > 0) {
            this.sleepCounter++;
            if (this.sleepCounter >= (SLEEP_DURATION + WAKE_UP_DURATION)) {
                this.sleepCounter = 0;
            }
        }

        super.tick();

        if (!this.level.isClientSide) {
            if (this.containerMenu != null && !this.containerMenu.stillValid(this)) {
                this.closeContainer();
                this.containerMenu = this.inventoryMenu;
            }
        }
        this.xCloakO = this.xCloak;
        this.yCloakO = this.yCloak;
        this.zCloakO = this.zCloak;

        final double xca = this.x - this.xCloak;
        final double yca = this.y - this.yCloak;
        final double zca = this.z - this.zCloak;

        final double m = 10.0;
        if (xca > m) this.xCloakO = this.xCloak = this.x;
        if (zca > m) this.zCloakO = this.zCloak = this.z;
        if (yca > m) this.yCloakO = this.yCloak = this.y;
        if (xca < -m) this.xCloakO = this.xCloak = this.x;
        if (zca < -m) this.zCloakO = this.zCloak = this.z;
        if (yca < -m) this.yCloakO = this.yCloak = this.y;

        this.xCloak += xca * 0.25;
        this.zCloak += zca * 0.25;
        this.yCloak += yca * 0.25;

        this.awardStat(Stats.playOneMinute, 1);

        if (this.riding == null) {
            this.minecartAchievementPos = null;
        }
    }
    
    @Override
    protected boolean isImmobile() {
        return this.health <= 0 || this.isSleeping();
    }
    
    protected void closeContainer() {
        this.containerMenu = this.inventoryMenu;
    }
    
    @Override
    public void prepareCustomTextures() {
        this.cloakTexture = "http://s3.amazonaws.com/MinecraftCloaks/" + this.name + ".png";
        this.customTextureUrl2 = this.cloakTexture;
    }
    
    @Override
    public void rideTick() {
        final double preX = this.x, preY = this.y, preZ = this.z;

        super.rideTick();
        this.oBob = this.bob;
        this.bob = 0.0f;
        this.checkRidingStatistiscs(this.x - preX, this.y - preY, this.z - preZ);
    }
    
    public void resetPos() {
        this.heightOffset = 1.62f;
        this.setSize(0.6f, 1.8f);
        super.resetPos();
        this.health = MAX_HEALTH;
        this.deathTime = 0;
    }
    
    @Override
    protected void updateAi() {
        if (this.swinging) {
            ++this.swingTime;
            if (this.swingTime >= 8) {
                this.swingTime = 0;
                this.swinging = false;
            }
        }
        else {
            this.swingTime = 0;
        }
        this.attackAnim = this.swingTime / 8.0f;
    }
    
    @Override
    public void aiStep() {
        if (this.level.difficulty == Difficulty.PEACEFUL && this.health < MAX_HEALTH) {
            if (this.tickCount % 20 * 12 == 0) this.heal(1);
        }
        this.inventory.tick();
        this.oBob = this.bob;

        super.aiStep();

        float tBob = Mth.sqrt(this.xd * this.xd + this.zd * this.zd);
        float tTilt = (float)Math.atan(-this.yd * 0.2f) * 15.0f;
        if (tBob > 0.1f) tBob = 0.1f;
        if (!this.onGround || this.health <= 0) tBob = 0.0f;
        if (this.onGround || this.health <= 0) tTilt = 0.0f;

        this.bob += (tBob - this.bob) * 0.4f;

        this.tilt += (tTilt - this.tilt) * 0.8f;

        if (this.health > 0) {
            final List<Entity> entities = this.level.getEntities(this, this.bb.grow(1.0, 0.0, 1.0));
            if (entities != null) {
                for (int i = 0; i < entities.size(); ++i) {
                    final Entity e = entities.get(i);
                    if (!e.removed) {
                        this.touch(e);
                    }
                }
            }
        }
    }
    
    private void touch(final Entity entity) {
        entity.playerTouch(this);
    }

    // Useless - existed in b1.2 leak and was commented out of LCE leak (removed in 1.0.1 according to LCE so would be here in b1.7.3)
    public boolean addResource(int resource) {
        return this.inventory.add(new ItemInstance(resource, 1, 0));
    }
    public int getScore() {
        return this.score;
    }
    
    @Override
    public void die(final Entity source) {
        super.die(source);
        this.setSize(0.2f, 0.2f);
        this.setPos(this.x, this.y, this.z);
        this.yd = 0.1f;

        if (this.name.equals("Notch")) {
            this.drop(new ItemInstance(Item.apple, 1), true);
        }
        this.inventory.dropAll();

        if (source != null) {
            this.xd = -Mth.cos((this.hurtDir + this.yRot) * Mth.DEGRAD) * 0.1f;
            this.zd = -Mth.sin((this.hurtDir + this.yRot) * Mth.DEGRAD) * 0.1f;
        }
        else {
            this.xd = this.zd = 0;
        }
        this.heightOffset = 0.1f;
        this.awardStat(Stats.deaths, 1);
    }
    
    @Override
    public void awardKillScore(final Entity victim, final int score) {
        this.score += score;
        if (victim instanceof Player) {
            this.awardStat(Stats.playerKills, 1);
        }
        else {
            this.awardStat(Stats.mobKills, 1);
        }
    }

    @Override
    public boolean isShootable() // Useless - in b1.2 and LCE leaks
    {
        return true;
    }

    @Override
    public boolean isCreativeModeAllowed() { // Useless - In b1.2 and LCE leaks
        return true;
    }

    public void drop() {
        this.drop(this.inventory.removeItem(this.inventory.selected, 1), false);
    }
    
    public void drop(final ItemInstance item) {
        this.drop(item, false);
    }
    
    public void drop(final ItemInstance item, final boolean randomly) {
        if (item == null) return;

        final ItemEntity thrownItem = new ItemEntity(this.level, this.x, this.y - 0.3f + this.getHeadHeight(), this.z, item);
        thrownItem.throwTime = SharedConstants.TICKS_PER_SECOND * 2;

        float pow = 0.1f;
        if (randomly) {
            final float _pow = this.random.nextFloat() * 0.5f;
            final float dir = this.random.nextFloat() * Mth.PI * 2.0f;
            thrownItem.xd = -Mth.sin(dir) * _pow;
            thrownItem.zd = Mth.cos(dir) * _pow;
            thrownItem.yd = 0.2f;
        }
        else {
            pow = 0.3f;
            thrownItem.xd = -Mth.sin(this.yRot / 180.0f * Mth.PI) * Mth.cos(this.xRot / 180.0f * Mth.PI) * pow;
            thrownItem.zd = Mth.cos(this.yRot / 180.0f * Mth.PI) * Mth.cos(this.xRot / 180.0f * Mth.PI) * pow;
            thrownItem.yd = -Mth.sin(this.xRot / 180.0f * Mth.PI) * pow + 0.1f;
            pow = 0.02f;

            final float dir = this.random.nextFloat() * Mth.PI * 2.0f;
            pow *= this.random.nextFloat();
            thrownItem.xd += Math.cos(dir) * pow;
            thrownItem.yd += (this.random.nextFloat() - this.random.nextFloat()) * 0.1f;
            thrownItem.zd += Math.sin(dir) * pow;
        }
        this.reallyDrop(thrownItem);

        this.awardStat(Stats.itemsDropped, 1);
    }
    
    protected void reallyDrop(final ItemEntity itemEntity) {
        this.level.addEntity(itemEntity);
    }
    
    public float getDestroySpeed(final Tile tile) {
        float speed = this.inventory.getDestroySpeed(tile);

        if (this.isUnderLiquid(Material.water)) speed /= 5.0f;
        if (!this.onGround) speed /= 5.0f;

        return speed;
    }
    
    public boolean canDestroy(final Tile tile) {
        return this.inventory.canDestroy(tile);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag entityTag) {
        super.readAdditionalSaveData(entityTag);
        this.inventory.load((ListTag<CompoundTag>) entityTag.getList("Inventory"));
        this.dimension = entityTag.getInt("Dimension");
        this.isSleeping = entityTag.getBoolean("Sleeping");
        this.sleepCounter = entityTag.getShort("SleepTimer");

        if (this.isSleeping) {
            this.bedPosition = new Pos(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z));
            this.stopSleepInBed(true, true, false);
        }

        if (entityTag.contains("SpawnX") && entityTag.contains("SpawnY") && entityTag.contains("SpawnZ")) {
            this.respawnPosition = new Pos(entityTag.getInt("SpawnX"), entityTag.getInt("SpawnY"), entityTag.getInt("SpawnZ"));
        }
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.put("Inventory", this.inventory.save(new ListTag<>()));
        compoundTag.putInt("Dimension", this.dimension);
        compoundTag.putBoolean("Sleeping", this.isSleeping);
        compoundTag.putShort("SleepTimer", (short)this.sleepCounter);

        if (this.respawnPosition != null) {
            compoundTag.putInt("SpawnX", this.respawnPosition.x);
            compoundTag.putInt("SpawnY", this.respawnPosition.y);
            compoundTag.putInt("SpawnZ", this.respawnPosition.z);
        }
    }
    
    public void openContainer(final Container container) {
    }
    
    public void startCrafting(final int x, final int y, final int z) {
    }
    
    public void take(final Entity e, final int orgCount) {
    }
    
    @Override
    public float getHeadHeight() {
        return 0.12f;
    }
    
    protected void setDefaultHeadHeight() {
        this.heightOffset = 1.62f;
    }
    
    @Override
    public boolean hurt(final Entity source, int dmg) {
        this.noActionTime = 0;
        if (this.health <= 0) return false;

        if (this.isSleeping() && !this.level.isClientSide) {
            this.stopSleepInBed(true, true, false);
        }

        if (source instanceof Monster || source instanceof Arrow) {
            if (this.level.difficulty == Difficulty.PEACEFUL) dmg = 0;
            if (this.level.difficulty == Difficulty.EASY) dmg = dmg / 3 + 1;
            if (this.level.difficulty == Difficulty.HARD) dmg = dmg * 3 / 2;
        }

        if (dmg == 0) return false;

        Entity attacker = source;
        if (attacker instanceof Arrow) {
            if (((Arrow) attacker).owner != null) {
                attacker = ((Arrow) attacker).owner;
            }
        }
        if (attacker instanceof Mob) {
            // aggreviate all pet wolves nearby
            this.directAllTameWolvesOnTarget((Mob)attacker, false);
        }

        this.awardStat(Stats.damageTaken, dmg);
        return super.hurt(source, dmg);
    }
    
    protected boolean isPlayerVersusPlayer() {
        return false;
    }
    
    protected void directAllTameWolvesOnTarget(final Mob target, final boolean skipSitting) {
        // filter un-attackable mobs
        if (target instanceof Creeper || target instanceof Ghast) {
            return;
        }

        // never target wolves that has this player as owner
        if (target instanceof Wolf) {
            final Wolf wolfTarget = (Wolf)target;
            if (wolfTarget.isTame() && this.name.equals(wolfTarget.getOwner())) {
                return;
            }
        }
        if (target instanceof Player && !this.isPlayerVersusPlayer()) {
            // pvp is off
            return;
        }

        List<Wolf> nearbyWolves = this.level.getEntitiesOfClass(Wolf.class, AABB.newTemp(this.x, this.y, this.z, this.x + 1.0, this.y + 1.0, this.z + 1.0).grow(16.0, 4.0, 16.0));
        for (final Wolf wolf : nearbyWolves) {
            if (wolf.isTame() && wolf.getAttackTarget() == null && this.name.equals(wolf.getOwner()))
                if (!skipSitting || !wolf.isSitting()) {
                    wolf.setSitting(false);
                    wolf.setAttackTarget(target);
                }
        }
    }
    
    @Override
    protected void actuallyHurt(int dmg) {
        final int absorb = 25 - this.inventory.getArmorValue();
        final int v = dmg * absorb + this.dmgSpill;
        this.inventory.hurtArmor(dmg);
        dmg = v / 25;
        this.dmgSpill = v % 25;
        super.actuallyHurt(dmg);
    }
    
    public void openFurnace(final FurnaceTileEntity furnace) {
    }
    
    public void openTrap(final DispenserTileEntity trap) {
    }
    
    public void openTextEdit(final SignTileEntity sign) {
    }
    
    public void interact(final Entity entity) {
        if (entity.interact(this)) return;
        final ItemInstance item = this.getSelectedItem();
        if (item != null && entity instanceof Mob) {
            item.interactEnemy((Mob)entity);
            if (item.count <= 0) {
                item.snap(this);
                this.removeSelectedItem();
            }
        }
    }
    
    public ItemInstance getSelectedItem() {
        return this.inventory.getSelected();
    }
    
    public void removeSelectedItem() {
        this.inventory.setItem(this.inventory.selected, null);
    }
    
    @Override
    public double getRidingHeight() {
        return this.heightOffset - 0.5f;
    }
    
    public void swing() {
        this.swingTime = -1;
        this.swinging = true;
    }
    
    public void attack(final Entity entity) {
        int dmg = this.inventory.getAttackDamage(entity);
        if (dmg > 0) {
            if (this.yd < 0.0) {
                ++dmg;
            }

            entity.hurt(this, dmg);
            final ItemInstance item = this.getSelectedItem();
            if (item != null && entity instanceof Mob) {
                item.hurtEnemy((Mob)entity, this);
                if (item.count <= 0) {
                    item.snap(this);
                    this.removeSelectedItem();
                }
            }
            if (entity instanceof Mob) {
                if (entity.isAlive()) {
                    this.directAllTameWolvesOnTarget((Mob)entity, true);
                }
                this.awardStat(Stats.damageDealt, dmg);
            }
        }
    }

    // Useless - In b1.2 and LCE leaks
    public Slot getInventorySlot(int slotId) {
        return null;
    }
    
    public void respawn() {
    }
    
    public abstract void animateRespawn();

    protected static void animateRespawn(Player player, Level level) { // Useless - this seems like it should have existed given comment out java code inside of the LCE leaked codebase
        for (int i = 0; i < 45; i++)
        {
            float angle = i * Mth.PI * 4.0f / 25.0f;
            float xo = Mth.cos(angle) * 0.7f;
            float zo = Mth.sin(angle) * 0.7f;

            level.addParticle("portal", player.x + xo, player.y - player.heightOffset + 1.62f - i * .05f, player.z + zo, 0, 0, 0);
        }
    }
    
    public void handleCollectItem(final ItemInstance carried) {
    }
    
    @Override
    public void remove() {
        super.remove();
        this.inventoryMenu.removed(this);
        if (this.containerMenu != null) {
            this.containerMenu.removed(this);
        }
    }
    
    @Override
    public boolean isInWall() {
        return !this.isSleeping && super.isInWall();
    }
    
    public BedSleepingResult startSleepInBed(final int x, final int y, final int z) {
        if (!this.level.isClientSide) {
            if (this.isSleeping() || !this.isAlive()) {
                return BedSleepingResult.OTHER_PROBLEM;
            }

            if (this.level.dimension.foggy) {
                return BedSleepingResult.NOT_POSSIBLE_HERE;
            }

            if (this.level.isDay()) {
                return BedSleepingResult.NOT_POSSIBLE_NOW;
            }

            if (Math.abs(this.x - x) > 3.0 || Math.abs(this.y - y) > 2.0 || Math.abs(this.z - z) > 3.0) {
                return BedSleepingResult.TOO_FAR_AWAY;
            }
        }

        this.setSize(0.2f, 0.2f);
        this.heightOffset = 0.2f;
        if (this.level.hasChunkAt(x, y, z)) {
            int data = this.level.getData(x, y, z);
            final int direction = BedTile.getDirection(data);
            float xo = 0.5f, zo = 0.5f;

            switch (direction) {
                case Direction.SOUTH: {
                    zo = 0.9f;
                    break;
                }
                case Direction.NORTH: {
                    zo = 0.1f;
                    break;
                }
                case Direction.WEST: {
                    xo = 0.1f;
                    break;
                }
                case Direction.EAST: {
                    xo = 0.9f;
                    break;
                }
            }
            this.setBedOffset(direction);
            this.setPos(x + xo, y + 15.0f / 16.0f, z + zo);
        }
        else {
            this.setPos(x + 0.5f, y + 15.0f / 16.0f, z + 0.5f);
        }
        this.isSleeping = true;
        this.sleepCounter = 0;
        this.bedPosition = new Pos(x, y, z);
        this.xd = this.zd = this.yd = 0.0;

        if (!this.level.isClientSide) {
            this.level.updateSleepingPlayerList();
        }

        return BedSleepingResult.OK;
    }
    
    private void setBedOffset(final int bedDirection) {
        // place position on pillow and feet at bottom
        this.bedOffsetX = 0.0f;
        this.bedOffsetZ = 0.0f;
        switch (bedDirection) {
            case Direction.SOUTH: {
                this.bedOffsetZ = -1.8f;
                break;
            }
            case Direction.NORTH: {
                this.bedOffsetZ = 1.8f;
                break;
            }
            case Direction.WEST: {
                this.bedOffsetX = 1.8f;
                break;
            }
            case Direction.EAST: {
                this.bedOffsetX = -1.8f;
                break;
            }
        }
    }
    
    public void stopSleepInBed(final boolean forcefulWakeUp, final boolean updateLevelList, final boolean saveRespawnPoint) {
        this.setSize(0.6f, 1.8f);
        this.setDefaultHeadHeight();

        Pos pos = this.bedPosition;
        Pos standUp = this.bedPosition;
        if (pos != null && this.level.getTile(pos.x, pos.y, pos.z) == Tile.bed.id) {
            BedTile.setOccupied(this.level, pos.x, pos.y, pos.z, false);

            standUp = BedTile.findStandUpPosition(this.level, pos.x, pos.y, pos.z, 0);
            if (standUp == null) {
                standUp = new Pos(pos.x, pos.y + 1, pos.z);
            }
            this.setPos(standUp.x + 0.5f, standUp.y + this.heightOffset + 0.1f, standUp.z + 0.5f);
        }

        this.isSleeping = false;
        if (!this.level.isClientSide && updateLevelList) {
            this.level.updateSleepingPlayerList();
        }
        if (forcefulWakeUp) {
            this.sleepCounter = 0;
        }
        else {
            this.sleepCounter = SLEEP_DURATION;
        }
        if (saveRespawnPoint) {
            this.setRespawnPosition(this.bedPosition);
        }
    }
    
    private boolean checkBed() {
        return this.level.getTile(this.bedPosition.x, this.bedPosition.y, this.bedPosition.z) == Tile.bed.id;
    }
    
    public static Pos checkBedValidRespawnPosition(final Level level, final Pos pos) {
        // make sure the chunks around the bed exist
        final ChunkSource chunkSource = level.getChunkSource();
        chunkSource.create(pos.x - 3 >> 4, pos.z - 3 >> 4);
        chunkSource.create(pos.x + 3 >> 4, pos.z - 3 >> 4);
        chunkSource.create(pos.x - 3 >> 4, pos.z + 3 >> 4);
        chunkSource.create(pos.x + 3 >> 4, pos.z + 3 >> 4);

        // make sure the bed is still standing
        if (level.getTile(pos.x, pos.y, pos.z) != Tile.bed.id) {
            return null;
        }
        // make sure the bed still has a stand-up position
        return BedTile.findStandUpPosition(level, pos.x, pos.y, pos.z, 0);
    }
    
    public float getSleepRotation() {
        if (this.bedPosition != null) {
            int data = this.level.getData(this.bedPosition.x, this.bedPosition.y, this.bedPosition.z);
            int direction = BedTile.getDirection(data);
            switch (direction) {
                case Direction.SOUTH: return 90.0f;
                case Direction.WEST: return 0.0f;
                case Direction.NORTH: return 270.0f;
                case Direction.EAST: return 180.0f;
            }
        }
        return 0.0f;
    }
    
    @Override
    public boolean isSleeping() {
        return this.isSleeping;
    }
    
    public boolean isSleepingLongEnough() {
        return this.isSleeping && this.sleepCounter >= SLEEP_DURATION;
    }
    
    public int getSleepTimer() {
        return this.sleepCounter;
    }
    
    public void displayClientMessage(final String message) {
    }
    
    public Pos getRespawnPosition() {
        return this.respawnPosition;
    }
    
    public void setRespawnPosition(final Pos respawnPosition) {
        if (respawnPosition != null) {
            this.respawnPosition = new Pos(respawnPosition);
        }
        else {
            this.respawnPosition = null;
        }
    }
    
    public void awardStat(final Stat stat) {
        this.awardStat(stat, 1);
    }
    
    public void awardStat(final Stat stat, final int count) {
    }
    
    @Override
    protected void jumpFromGround() {
        super.jumpFromGround();
        this.awardStat(Stats.timesJumped, 1);
    }
    
    @Override
    public void travel(final float xa, final float ya) {
        final double preX = this.x, preY = this.y, preZ = this.z;

        super.travel(xa, ya);

        this.checkMovementStatistiscs(this.x - preX, this.y - preY, this.z - preZ);
    }
    
    private void checkMovementStatistiscs(final double dx, final double dy, final double dz) {
        if (this.riding != null) return;

        if (this.isUnderLiquid(Material.water)) {
            final int distance = Math.round(Mth.sqrt(dx * dx + dy * dy + dz * dz) * 100.0f);
            if (distance > 0) {
                this.awardStat(Stats.diveOneCm, distance);
            }
        }
        else if (this.isInWater()) {
            final int horizontalDistance = Math.round(Mth.sqrt(dx * dx + dz * dz) * 100.0f);
            if (horizontalDistance > 0) {
                this.awardStat(Stats.swimOneCm, horizontalDistance);
            }
        }
        else if (this.onLadder()) {
            if (dy > 0.0) {
                this.awardStat(Stats.climbOneCm, (int)Math.round(dy * 100.0));
            }
        }
        else if (this.onGround) {
            final int horizontalDistance = Math.round(Mth.sqrt(dx * dx + dz * dz) * 100.0f);
            if (horizontalDistance > 0) {
                this.awardStat(Stats.walkOneCm, horizontalDistance);
            }
        }
        else {
            final int horizontalDistance = Math.round(Mth.sqrt(dx * dx + dz * dz) * 100.0f);
            if (horizontalDistance > 25) {
                this.awardStat(Stats.flyOneCm, horizontalDistance);
            }
        }
    }
    
    private void checkRidingStatistiscs(final double dx, final double dy, final double dz) {
        if (this.riding != null) {
            final int distance = Math.round(Mth.sqrt(dx * dx + dy * dy + dz * dz) * 100.0f);
            if (distance > 0) {
                if (this.riding instanceof Minecart) {
                    this.awardStat(Stats.minecartOneCm, distance);
                    if (this.minecartAchievementPos == null) {
                        this.minecartAchievementPos = new Pos(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z));
                    }
                    else if (this.minecartAchievementPos.dist(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z)) >= 1000.0) {
                        this.awardStat(Achievements.onARail, 1);
                    }
                }
                else if (this.riding instanceof Boat) {
                    this.awardStat(Stats.boatOneCm, distance);
                }
                else if (this.riding instanceof Pig) {
                    this.awardStat(Stats.pigOneCm, distance);
                }
            }
        }
    }
    
    @Override
    protected void causeFallDamage(final float distance) {
        if (distance >= 2.0f) {
            this.awardStat(Stats.fallOneCm, (int)Math.round(distance * 100.0));
        }
        super.causeFallDamage(distance);
    }
    
    @Override
    public void killed(final Mob mob) {
        if (mob instanceof Monster) {
            this.awardStat(Achievements.killEnemy);
        }
    }
    
    @Override
    public int getItemInHandIcon(final ItemInstance item) {
        int icon = super.getItemInHandIcon(item);
        if (item.id == Item.fishingRod.id && this.fishing != null) {
            icon = item.getIcon() + 16;
        }
        return icon;
    }
    
    @Override
    public void handleInsidePortal() {
        if (this.changingDimensionDelay > 0) {
            this.changingDimensionDelay = 10;
            return;
        }
        this.isInsidePortal = true;
    }

    public enum BedSleepingResult
    {
        OK,
        NOT_POSSIBLE_HERE,
        NOT_POSSIBLE_NOW,
        TOO_FAR_AWAY,
        OTHER_PROBLEM;
    }
}
