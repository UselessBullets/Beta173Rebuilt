// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.player;

import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.item.Boat;
import net.minecraft.stats.Achievements;
import net.minecraft.world.entity.item.Minecart;
import net.minecraft.stats.Stat;
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
    public static final int MAX_NAME_LENGTH = 16 + 4;
    public static final int MAX_HEALTH = 20;
    public static final int SWING_DURATION = 6;
    public static final int SLEEP_DURATION = 100;
    public static final int WAKE_UP_DURATION = 10;

    public static final int CHAT_VISIBILITY_FULL = 0;
    public static final int CHAT_VISIBILITY_SYSTEM = 1;
    public static final int CHAT_VISIBILITY_HIDDEN = 2;

    public Inventory inventory;
    public AbstractContainerMenu inventoryMenu;
    public AbstractContainerMenu containerMenu;
    public byte userType;
    public int score;
    public float oBob;
    public float bob;
    public boolean swinging;
    public int swingTime;
    public String name;
    public int dimension;
    public String cloakTexture;
    public double xCloakO;
    public double yCloakO;
    public double zCloakO;
    public double xCloak;
    public double yCloak;
    public double zCloak;
    protected boolean isSleeping;
    public Pos bedPosition;
    private int sleepCounter;
    public float bedOffsetX;
    public float bedOffsetY;
    public float bedOffsetZ;
    private Pos respawnPosition;
    private Pos minecartAchievementPos;
    public int changingDimensionDelay;
    protected boolean isInsidePortal;
    public float portalTime;
    public float oPortalTime;
    private int dmgSpill;
    public FishingHook fishing;
    
    public Player(final Level level) {
        super(level);
        this.inventory = new Inventory(this);
        this.userType = 0;
        this.score = 0;
        this.swinging = false;
        this.swingTime = 0;
        this.changingDimensionDelay = 20;
        this.isInsidePortal = false;
        this.dmgSpill = 0;
        this.fishing = null;
        this.inventoryMenu = new InventoryMenu(this.inventory, !level.isClientSide);
        this.containerMenu = this.inventoryMenu;
        this.heightOffset = 1.62f;
        final Pos sharedSpawnPos = level.getSharedSpawnPos();
        this.moveTo(sharedSpawnPos.x + 0.5, sharedSpawnPos.y + 1, sharedSpawnPos.z + 0.5, 0.0f, 0.0f);
        this.health = 20;
        this.modelName = "humanoid";
        this.rotOffs = 180.0f;
        this.flameTime = 20;
        this.textureName = "/mob/char.png";
    }
    
    @Override
    protected void definedSynchedData() {
        super.definedSynchedData();
        this.entityData.define(16, 0);
    }
    
    @Override
    public void tick() {
        if (this.isSleeping()) {
            ++this.sleepCounter;
            if (this.sleepCounter > 100) {
                this.sleepCounter = 100;
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
            ++this.sleepCounter;
            if (this.sleepCounter >= 110) {
                this.sleepCounter = 0;
            }
        }
        super.tick();
        if (!this.level.isClientSide && this.containerMenu != null && !this.containerMenu.stillValid(this)) {
            this.closeContainer();
            this.containerMenu = this.inventoryMenu;
        }
        this.xCloakO = this.xCloak;
        this.yCloakO = this.yCloak;
        this.zCloakO = this.zCloak;
        final double n = this.x - this.xCloak;
        final double n2 = this.y - this.yCloak;
        final double n3 = this.z - this.zCloak;
        final double n4 = 10.0;
        if (n > n4) {
            final double x = this.x;
            this.xCloak = x;
            this.xCloakO = x;
        }
        if (n3 > n4) {
            final double z = this.z;
            this.zCloak = z;
            this.zCloakO = z;
        }
        if (n2 > n4) {
            final double y = this.y;
            this.yCloak = y;
            this.yCloakO = y;
        }
        if (n < -n4) {
            final double x2 = this.x;
            this.xCloak = x2;
            this.xCloakO = x2;
        }
        if (n3 < -n4) {
            final double z2 = this.z;
            this.zCloak = z2;
            this.zCloakO = z2;
        }
        if (n2 < -n4) {
            final double y2 = this.y;
            this.yCloak = y2;
            this.yCloakO = y2;
        }
        this.xCloak += n * 0.25;
        this.zCloak += n3 * 0.25;
        this.yCloak += n2 * 0.25;
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
        final double x = this.x;
        final double y = this.y;
        final double z = this.z;
        super.rideTick();
        this.oBob = this.bob;
        this.bob = 0.0f;
        this.checkRidingStatistiscs(this.x - x, this.y - y, this.z - z);
    }
    
    public void resetPos() {
        this.heightOffset = 1.62f;
        this.setSize(0.6f, 1.8f);
        super.resetPos();
        this.health = 20;
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
        if (this.level.difficulty == 0 && this.health < 20 && this.tickCount % 20 * 12 == 0) {
            this.heal(1);
        }
        this.inventory.tick();
        this.oBob = this.bob;
        super.aiStep();
        float sqrt = Mth.sqrt(this.xd * this.xd + this.zd * this.zd);
        float n = (float)Math.atan(-this.yd * 0.2f) * 15.0f;
        if (sqrt > 0.1f) {
            sqrt = 0.1f;
        }
        if (!this.onGround || this.health <= 0) {
            sqrt = 0.0f;
        }
        if (this.onGround || this.health <= 0) {
            n = 0.0f;
        }
        this.bob += (sqrt - this.bob) * 0.4f;
        this.tilt += (n - this.tilt) * 0.8f;
        if (this.health > 0) {
            final List<Entity> entities = this.level.getEntities(this, this.bb.grow(1.0, 0.0, 1.0));
            if (entities != null) {
                for (int i = 0; i < entities.size(); ++i) {
                    final Entity entity = entities.get(i);
                    if (!entity.removed) {
                        this.touch(entity);
                    }
                }
            }
        }
    }
    
    private void touch(final Entity entity) {
        entity.playerTouch(this);
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
            final double n = 0.0;
            this.zd = n;
            this.xd = n;
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
    
    public void drop() {
        this.drop(this.inventory.removeItem(this.inventory.selected, 1), false);
    }
    
    public void drop(final ItemInstance item) {
        this.drop(item, false);
    }
    
    public void drop(final ItemInstance item, final boolean randomly) {
        if (item == null) {
            return;
        }
        final ItemEntity itemEntity = new ItemEntity(this.level, this.x, this.y - 0.3f + this.getHeadHeight(), this.z, item);
        itemEntity.throwTime = 40;
        if (randomly) {
            final float n = this.random.nextFloat() * 0.5f;
            final float n2 = this.random.nextFloat() * Mth.PI * 2.0f;
            itemEntity.xd = -Mth.sin(n2) * n;
            itemEntity.zd = Mth.cos(n2) * n;
            itemEntity.yd = 0.2f;
        }
        else {
            final float n3 = 0.3f;
            itemEntity.xd = -Mth.sin(this.yRot / 180.0f * Mth.PI) * Mth.cos(this.xRot / 180.0f * Mth.PI) * n3;
            itemEntity.zd = Mth.cos(this.yRot / 180.0f * Mth.PI) * Mth.cos(this.xRot / 180.0f * Mth.PI) * n3;
            itemEntity.yd = -Mth.sin(this.xRot / 180.0f * Mth.PI) * n3 + 0.1f;
            final float n4 = 0.02f;
            final float n5 = this.random.nextFloat() * Mth.PI * 2.0f;
            final float n6 = n4 * this.random.nextFloat();
            final ItemEntity itemEntity2 = itemEntity;
            itemEntity2.xd += Math.cos(n5) * n6;
            final ItemEntity itemEntity3 = itemEntity;
            itemEntity3.yd += (this.random.nextFloat() - this.random.nextFloat()) * 0.1f;
            final ItemEntity itemEntity4 = itemEntity;
            itemEntity4.zd += Math.sin(n5) * n6;
        }
        this.reallyDrop(itemEntity);
        this.awardStat(Stats.itemsDropped, 1);
    }
    
    protected void reallyDrop(final ItemEntity itemEntity) {
        this.level.addEntity(itemEntity);
    }
    
    public float getDestroySpeed(final Tile tile) {
        float destroySpeed = this.inventory.getDestroySpeed(tile);
        if (this.isUnderLiquid(Material.water)) {
            destroySpeed /= 5.0f;
        }
        if (!this.onGround) {
            destroySpeed /= 5.0f;
        }
        return destroySpeed;
    }
    
    public boolean canDestroy(final Tile tile) {
        return this.inventory.canDestroy(tile);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.inventory.load(compoundTag.getList("Inventory"));
        this.dimension = compoundTag.getInt("Dimension");
        this.isSleeping = compoundTag.getBoolean("Sleeping");
        this.sleepCounter = compoundTag.getShort("SleepTimer");
        if (this.isSleeping) {
            this.bedPosition = new Pos(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z));
            this.stopSleepInBed(true, true, false);
        }
        if (compoundTag.contains("SpawnX") && compoundTag.contains("SpawnY") && compoundTag.contains("SpawnZ")) {
            this.respawnPosition = new Pos(compoundTag.getInt("SpawnX"), compoundTag.getInt("SpawnY"), compoundTag.getInt("SpawnZ"));
        }
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.put("Inventory", this.inventory.save(new ListTag()));
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
        if (this.health <= 0) {
            return false;
        }
        if (this.isSleeping() && !this.level.isClientSide) {
            this.stopSleepInBed(true, true, false);
        }
        if (source instanceof Monster || source instanceof Arrow) {
            if (this.level.difficulty == 0) {
                dmg = 0;
            }
            if (this.level.difficulty == 1) {
                dmg = dmg / 3 + 1;
            }
            if (this.level.difficulty == 3) {
                dmg = dmg * 3 / 2;
            }
        }
        if (dmg == 0) {
            return false;
        }
        Entity owner = source;
        if (owner instanceof Arrow && ((Arrow)owner).owner != null) {
            owner = ((Arrow)owner).owner;
        }
        if (owner instanceof Mob) {
            this.directAllTameWolvesOnTarget((Mob)owner, false);
        }
        this.awardStat(Stats.damageTaken, dmg);
        return super.hurt(source, dmg);
    }
    
    protected boolean isPlayerVersusPlayer() {
        return false;
    }
    
    protected void directAllTameWolvesOnTarget(final Mob target, final boolean skipSitting) {
        if (target instanceof Creeper || target instanceof Ghast) {
            return;
        }
        if (target instanceof Wolf) {
            final Wolf wolf = (Wolf)target;
            if (wolf.isTame() && this.name.equals(wolf.getOwner())) {
                return;
            }
        }
        if (target instanceof Player && !this.isPlayerVersusPlayer()) {
            return;
        }
        for (final Wolf wolf2 : this.level.getEntitiesOfClass(Wolf.class, AABB.newTemp(this.x, this.y, this.z, this.x + 1.0, this.y + 1.0, this.z + 1.0).grow(16.0, 4.0, 16.0))) {
            if (wolf2.isTame() && wolf2.getAttackTarget() == null && this.name.equals(wolf2.getOwner()) && (!skipSitting || !wolf2.isSitting())) {
                wolf2.setSitting(false);
                wolf2.setAttackTarget(target);
            }
        }
    }
    
    @Override
    protected void actuallyHurt(int dmg) {
        final int n = dmg * (25 - this.inventory.getArmorValue()) + this.dmgSpill;
        this.inventory.hurtArmor(dmg);
        dmg = n / 25;
        this.dmgSpill = n % 25;
        super.actuallyHurt(dmg);
    }
    
    public void openFurnace(final FurnaceTileEntity furnace) {
    }
    
    public void openTrap(final DispenserTileEntity trap) {
    }
    
    public void openTextEdit(final SignTileEntity sign) {
    }
    
    public void interact(final Entity entity) {
        if (entity.interact(this)) {
            return;
        }
        final ItemInstance selectedItem = this.getSelectedItem();
        if (selectedItem != null && entity instanceof Mob) {
            selectedItem.interactEnemy((Mob)entity);
            if (selectedItem.count <= 0) {
                selectedItem.snap(this);
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
        int attackDamage = this.inventory.getAttackDamage(entity);
        if (attackDamage > 0) {
            if (this.yd < 0.0) {
                ++attackDamage;
            }
            entity.hurt(this, attackDamage);
            final ItemInstance selectedItem = this.getSelectedItem();
            if (selectedItem != null && entity instanceof Mob) {
                selectedItem.hurtEnemy((Mob)entity, this);
                if (selectedItem.count <= 0) {
                    selectedItem.snap(this);
                    this.removeSelectedItem();
                }
            }
            if (entity instanceof Mob) {
                if (entity.isAlive()) {
                    this.directAllTameWolvesOnTarget((Mob)entity, true);
                }
                this.awardStat(Stats.damageDealt, attackDamage);
            }
        }
    }
    
    public void respawn() {
    }
    
    public abstract void animateRespawn();
    
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
            final int direction = BedTile.getDirection(this.level.getData(x, y, z));
            float n = 0.5f;
            float n2 = 0.5f;
            switch (direction) {
                case 0: {
                    n2 = 0.9f;
                    break;
                }
                case 2: {
                    n2 = 0.1f;
                    break;
                }
                case 1: {
                    n = 0.1f;
                    break;
                }
                case 3: {
                    n = 0.9f;
                    break;
                }
            }
            this.setBedOffset(direction);
            this.setPos(x + n, y + 0.9375f, z + n2);
        }
        else {
            this.setPos(x + 0.5f, y + 0.9375f, z + 0.5f);
        }
        this.isSleeping = true;
        this.sleepCounter = 0;
        this.bedPosition = new Pos(x, y, z);
        final double xd = 0.0;
        this.yd = xd;
        this.zd = xd;
        this.xd = xd;
        if (!this.level.isClientSide) {
            this.level.updateSleepingPlayerList();
        }
        return BedSleepingResult.OK;
    }
    
    private void setBedOffset(final int bedDirection) {
        this.bedOffsetX = 0.0f;
        this.bedOffsetZ = 0.0f;
        switch (bedDirection) {
            case 0: {
                this.bedOffsetZ = -1.8f;
                break;
            }
            case 2: {
                this.bedOffsetZ = 1.8f;
                break;
            }
            case 1: {
                this.bedOffsetX = 1.8f;
                break;
            }
            case 3: {
                this.bedOffsetX = -1.8f;
                break;
            }
        }
    }
    
    public void stopSleepInBed(final boolean forcefulWakeUp, final boolean updateLevelList, final boolean saveRespawnPoint) {
        this.setSize(0.6f, 1.8f);
        this.setDefaultHeadHeight();
        final Pos bedPosition = this.bedPosition;
        final Pos bedPosition2 = this.bedPosition;
        if (bedPosition != null && this.level.getTile(bedPosition.x, bedPosition.y, bedPosition.z) == Tile.bed.id) {
            BedTile.setOccupied(this.level, bedPosition.x, bedPosition.y, bedPosition.z, false);
            Pos standUpPosition = BedTile.findStandUpPosition(this.level, bedPosition.x, bedPosition.y, bedPosition.z, 0);
            if (standUpPosition == null) {
                standUpPosition = new Pos(bedPosition.x, bedPosition.y + 1, bedPosition.z);
            }
            this.setPos(standUpPosition.x + 0.5f, standUpPosition.y + this.heightOffset + 0.1f, standUpPosition.z + 0.5f);
        }
        this.isSleeping = false;
        if (!this.level.isClientSide && updateLevelList) {
            this.level.updateSleepingPlayerList();
        }
        if (forcefulWakeUp) {
            this.sleepCounter = 0;
        }
        else {
            this.sleepCounter = 100;
        }
        if (saveRespawnPoint) {
            this.setRespawnPosition(this.bedPosition);
        }
    }
    
    private boolean checkBed() {
        return this.level.getTile(this.bedPosition.x, this.bedPosition.y, this.bedPosition.z) == Tile.bed.id;
    }
    
    public static Pos checkBedValidRespawnPosition(final Level level, final Pos pos) {
        final ChunkSource chunkSource = level.getChunkSource();
        chunkSource.create(pos.x - 3 >> 4, pos.z - 3 >> 4);
        chunkSource.create(pos.x + 3 >> 4, pos.z - 3 >> 4);
        chunkSource.create(pos.x - 3 >> 4, pos.z + 3 >> 4);
        chunkSource.create(pos.x + 3 >> 4, pos.z + 3 >> 4);
        if (level.getTile(pos.x, pos.y, pos.z) != Tile.bed.id) {
            return null;
        }
        return BedTile.findStandUpPosition(level, pos.x, pos.y, pos.z, 0);
    }
    
    public float getSleepRotation() {
        if (this.bedPosition != null) {
            switch (BedTile.getDirection(this.level.getData(this.bedPosition.x, this.bedPosition.y, this.bedPosition.z))) {
                case 0: {
                    return 90.0f;
                }
                case 1: {
                    return 0.0f;
                }
                case 2: {
                    return 270.0f;
                }
                case 3: {
                    return 180.0f;
                }
            }
        }
        return 0.0f;
    }
    
    @Override
    public boolean isSleeping() {
        return this.isSleeping;
    }
    
    public boolean isSleepingLongEnough() {
        return this.isSleeping && this.sleepCounter >= 100;
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
        final double x = this.x;
        final double y = this.y;
        final double z = this.z;
        super.travel(xa, ya);
        this.checkMovementStatistiscs(this.x - x, this.y - y, this.z - z);
    }
    
    private void checkMovementStatistiscs(final double dx, final double dy, final double dz) {
        if (this.riding != null) {
            return;
        }
        if (this.isUnderLiquid(Material.water)) {
            final int round = Math.round(Mth.sqrt(dx * dx + dy * dy + dz * dz) * 100.0f);
            if (round > 0) {
                this.awardStat(Stats.diveOneCm, round);
            }
        }
        else if (this.isInWater()) {
            final int round2 = Math.round(Mth.sqrt(dx * dx + dz * dz) * 100.0f);
            if (round2 > 0) {
                this.awardStat(Stats.swimOneCm, round2);
            }
        }
        else if (this.onLadder()) {
            if (dy > 0.0) {
                this.awardStat(Stats.climbOneCm, (int)Math.round(dy * 100.0));
            }
        }
        else if (this.onGround) {
            final int round3 = Math.round(Mth.sqrt(dx * dx + dz * dz) * 100.0f);
            if (round3 > 0) {
                this.awardStat(Stats.walkOneCm, round3);
            }
        }
        else {
            final int round4 = Math.round(Mth.sqrt(dx * dx + dz * dz) * 100.0f);
            if (round4 > 25) {
                this.awardStat(Stats.flyOneCm, round4);
            }
        }
    }
    
    private void checkRidingStatistiscs(final double dx, final double dy, final double dz) {
        if (this.riding != null) {
            final int round = Math.round(Mth.sqrt(dx * dx + dy * dy + dz * dz) * 100.0f);
            if (round > 0) {
                if (this.riding instanceof Minecart) {
                    this.awardStat(Stats.minecartOneCm, round);
                    if (this.minecartAchievementPos == null) {
                        this.minecartAchievementPos = new Pos(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z));
                    }
                    else if (this.minecartAchievementPos.dist(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z)) >= 1000.0) {
                        this.awardStat(Achievements.onARail, 1);
                    }
                }
                else if (this.riding instanceof Boat) {
                    this.awardStat(Stats.boatOneCm, round);
                }
                else if (this.riding instanceof Pig) {
                    this.awardStat(Stats.pigOneCm, round);
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
        int itemInHandIcon = super.getItemInHandIcon(item);
        if (item.id == Item.fishingRod.id && this.fishing != null) {
            itemInHandIcon = item.getIcon() + 16;
        }
        return itemInHandIcon;
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
