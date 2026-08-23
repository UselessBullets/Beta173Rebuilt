// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.animal;

import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.pathfinder.Path;
import util.Mth;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.FoodItem;
import net.minecraft.world.item.Item;
import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.Entity;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class Wolf extends Animal
{
    public static final int TeleportDistance = 12; // Useless - from LCE FollowOwnerGoal, is in code shared with b1.7.3 wolf code so presumably existed

    private static final int DATA_FLAGS_ID = 16;
    private static final int DATA_OWNER_ID = 17; // Useless - Was "DATA_OWNERUUID_ID" in LCE, b1.7.3 doesn't use UUIDs so its been renamed to "DATA_OWNER_ID"
    // synch health in a separate field to show tame wolves' health
    private static final int DATA_HEALTH_ID = 18;
    private static final int START_HEALTH = 8;
    private static final int MAX_HEALTH = 20;
    private static final int TAME_HEALTH = 20;

    private boolean isInterested = false;
    private float interestedAngle, interestedAngleO;
    private boolean isWet, isShaking;
    private float shakeAnim, shakeAnimO;
    
    public Wolf(final Level level) {
        super(level);
        this.textureName = "/mob/wolf.png";
        this.setSize(0.8f, 0.8f);
        this.runSpeed = 1.1f;
        this.health = START_HEALTH;
    }
    
    @Override
    protected void definedSynchedData() {
        super.definedSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(DATA_OWNER_ID, "");
        this.entityData.define(DATA_HEALTH_ID, this.health);
    }
    
    @Override
    protected boolean makeStepSound() {
        return false;
    }
    
    @Override
    public String getTexture() {
        if (this.isTame()) return "/mob/wolf_tame.png";
        if (this.isAngry()) return "/mob/wolf_angry.png";
        return super.getTexture();
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);

        compoundTag.putBoolean("Angry", this.isAngry());
        compoundTag.putBoolean("Sitting", this.isSitting());
        compoundTag.putString("Owner", this.getOwner() == null ? "" : this.getOwner());
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setAngry(compoundTag.getBoolean("Angry"));
        this.setSitting(compoundTag.getBoolean("Sitting"));

        final String owner = compoundTag.getString("Owner");
        if (owner.length() > 0) {
            this.setOwner(owner);
            this.setTame(true);
        }
    }
    
    @Override
    protected boolean removeWhenFarAway() {
        return !this.isTame();
    }
    
    @Override
    protected String getAmbientSound() {
        if (this.isAngry()) return "mob.wolf.growl";
        if (this.random.nextInt(3) != 0) return "mob.wolf.bark";
        if (this.isTame() && this.entityData.getInteger(DATA_HEALTH_ID) < 10) return "mob.wolf.whine";
        return "mob.wolf.panting";
    }
    
    @Override
    protected String getHurtSound() {
        return "mob.wolf.hurt";
    }
    
    @Override
    protected String getDeathSound() {
        return "mob.wolf.death";
    }
    
    @Override
    protected float getSoundVolume() {
        return 0.4f;
    }
    
    @Override
    protected int getDeathLoot() {
        return -1;
    }
    
    @Override
    protected void updateAi() {
        super.updateAi();

        if (!this.holdGround && !this.isPathFinding() && this.isTame() && this.riding == null) {
            final Player playerByName = this.level.getPlayerByName(this.getOwner());
            if (playerByName != null) {
                final float distanceTo = playerByName.distanceTo(this);
                if (distanceTo > 5.0f) {
                    this.followOwner(playerByName, distanceTo);
                }
            }
            else if (!this.isInWater()) {
                this.setSitting(true);
            }
        }
        else if (this.attackTarget == null && !this.isPathFinding() && !this.isTame() && this.level.random.nextInt(100) == 0) {
            final List<Sheep> entitiesOfClass = this.level.getEntitiesOfClass(Sheep.class, AABB.newTemp(this.x, this.y, this.z, this.x + 1.0, this.y + 1.0, this.z + 1.0).grow(16.0, 4.0, 16.0));
            if (!entitiesOfClass.isEmpty()) {
                this.setAttackTarget(entitiesOfClass.get(this.level.random.nextInt(entitiesOfClass.size())));
            }
        }
        if (this.isInWater()) {
            this.setSitting(false);
        }
        if (!this.level.isClientSide) {
            this.entityData.set(DATA_HEALTH_ID, this.health);
        }
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
        this.isInterested = false;
        if (this.isLookingAtAnEntity() && !this.isPathFinding() && !this.isAngry()) {
            final Entity looking = this.getLookingAt();
            if (looking instanceof Player) {
                final ItemInstance item = ((Player)looking).inventory.getSelected();
                if (item != null) {
                    if (!this.isTame() && item.id == Item.bone.id) {
                        this.isInterested = true;
                    }
                    else if (this.isTame() && Item.items[item.id] instanceof FoodItem) {
                        this.isInterested = ((FoodItem)Item.items[item.id]).isMeat();
                    }
                }
            }
        }

        if (!this.interpolateOnly && this.isWet && !this.isShaking && !this.isPathFinding() && this.onGround) {
            this.isShaking = true;
            this.shakeAnim = 0.0f;
            this.shakeAnimO = 0.0f;
            this.level.broadcastEntityEvent(this, EntityEvent.SHAKE_WETNESS);
        }
    }
    
    @Override
    public void tick() {
        super.tick();

        this.interestedAngleO = this.interestedAngle;
        if (this.isInterested) {
            this.interestedAngle += (1.0f - this.interestedAngle) * 0.4f;
        }
        else {
            this.interestedAngle += (0.0f - this.interestedAngle) * 0.4f;
        }
        if (this.isInterested) {
            this.lookTime = 10;
        }

        if (this.isInWaterOrRain()) {
            this.isWet = true;
            this.isShaking = false;
            this.shakeAnim = 0.0f;
            this.shakeAnimO = 0.0f;
        }
        else if ((this.isWet || this.isShaking) && this.isShaking) {
            if (this.shakeAnim == 0.0f) {
                this.level.playSound(this, "mob.wolf.shake", this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            }

            this.shakeAnimO = this.shakeAnim;
            this.shakeAnim += 0.05f;

            if (this.shakeAnimO >= 2.0f) {
                this.isWet = false;
                this.isShaking = false;
                this.shakeAnimO = 0.0f;
                this.shakeAnim = 0.0f;
            }

            if (this.shakeAnim > 0.4f) {
                final float yt = (float)this.bb.y0;
                int shakeCount = (int)(Mth.sin((this.shakeAnim - 0.4f) * Mth.PI) * 7.0f);
                for (int i = 0; i < shakeCount; ++i) {
                    float xo = (this.random.nextFloat() * 2.0f - 1.0f) * this.bbWidth * 0.5f;
                    float zo = (this.random.nextFloat() * 2.0f - 1.0f) * this.bbWidth * 0.5f;
                    this.level.addParticle("splash", this.x + xo, yt + 0.8f, this.z + zo, this.xd, this.yd, this.zd);
                }
            }
        }
    }
    
    public boolean isWet() {
        return this.isWet;
    }
    
    public float getWetShade(final float a) {
        return 0.75f + (this.shakeAnimO + (this.shakeAnim - this.shakeAnimO) * a) / 2.0f * 0.25f;
    }
    
    public float getBodyRollAngle(final float a, final float offset) {
        float progress = (this.shakeAnimO + (this.shakeAnim - this.shakeAnimO) * a + offset) / 1.8f;
        if (progress < 0.0f) progress = 0.0f;
        else if (progress > 1.0f) progress = 1.0f;

        return Mth.sin(progress * Mth.PI) * Mth.sin(progress * Mth.PI * 11.0f) * 0.15f * Mth.PI;
    }
    
    public float getHeadRollAngle(final float a) {
        return (this.interestedAngleO + (this.interestedAngle - this.interestedAngleO) * a) * 0.15f * Mth.PI;
    }
    
    @Override
    public float getHeadHeight() {
        return this.bbHeight * 0.8f;
    }
    
    @Override
    protected int getMaxHeadXRot() {
        if (this.isSitting()) {
            return 20;
        }
        return super.getMaxHeadXRot();
    }
    
    private void followOwner(final Entity owner, final float distance) {
        final Path path = this.level.findPath(this, owner, 16.0f);
        if (path == null && distance > TeleportDistance) {
            // find a good spawn position nearby the owner
            final int sx = Mth.floor(owner.x) - 2;
            final int sz = Mth.floor(owner.z) - 2;
            final int y = Mth.floor(owner.bb.y0);
            for (int x = 0; x <= 4; ++x) {
                for (int z = 0; z <= 4; ++z) {
                    if (x >= 1 && z >= 1 && x <= 3 && z <= 3) continue;

                    if (this.level.isSolidBlockingTile(sx + x, y - 1, sz + z) && !this.level.isSolidBlockingTile(sx + x, y, sz + z) && !this.level.isSolidBlockingTile(sx + x, y + 1, sz + z)) {
                        this.moveTo(sx + x + 0.5f, y, sz + z + 0.5f, this.yRot, this.xRot);
                        return;
                    }
                }
            }
        }
        else {
            this.setPath(path);
        }
    }
    
    @Override
    protected boolean shouldHoldGround() {
        return this.isSitting() || this.isShaking;
    }
    
    @Override
    public boolean hurt(Entity sourceEntity, int dmg) {
        this.setSitting(false);
        if (sourceEntity != null && !(sourceEntity instanceof Player || sourceEntity instanceof Arrow)) {
            // take half damage from non-players and arrows
            dmg = (dmg + 1) / 2;
        }

        if (super.hurt(sourceEntity, dmg)) {
            if (!this.isTame() && !this.isAngry()) {
                if (sourceEntity instanceof Player) {
                    this.setAngry(true);
                    this.attackTarget = sourceEntity;
                }

                if (sourceEntity instanceof Arrow && ((Arrow)sourceEntity).owner != null) {
                    sourceEntity = ((Arrow)sourceEntity).owner;
                }

                if (sourceEntity instanceof Mob) {
                    for (final Wolf wolf : this.level.getEntitiesOfClass(Wolf.class, AABB.newTemp(this.x, this.y, this.z, this.x + 1.0, this.y + 1.0, this.z + 1.0).grow(16.0, 4.0, 16.0))) {
                        if (!wolf.isTame() && wolf.attackTarget == null) {
                            wolf.attackTarget = sourceEntity;
                            if (!(sourceEntity instanceof Player)) continue;
                            wolf.setAngry(true);
                        }
                    }
                }
            }
            else if (sourceEntity != this && sourceEntity != null) {
                if (this.isTame() && sourceEntity instanceof Player && ((Player)sourceEntity).name.equalsIgnoreCase(this.getOwner())) {
                    return true;
                }
                this.attackTarget = sourceEntity;
            }
            return true;
        }
        return false;
    }
    
    @Override
    protected Entity findAttackTarget() {
        if (this.isAngry()) return this.level.getNearestPlayer(this, 16.0);
        return null;
    }
    
    @Override
    protected void checkHurtTarget(final Entity target, final float distance) {
        if (distance > 2.0f && distance < 6.0f && this.random.nextInt(10) == 0) {
            if (this.onGround) {
                final double xdd = target.x - this.x;
                final double zdd = target.z - this.z;
                final float dd = Mth.sqrt(xdd * xdd + zdd * zdd);
                this.xd = xdd / dd * 0.5 * 0.8f + this.xd * 0.2f;
                this.zd = zdd / dd * 0.5 * 0.8f + this.zd * 0.2f;
                this.yd = 0.4f;
            }
        }
        else if (distance < 1.5 && target.bb.y1 > this.bb.y0 && target.bb.y0 < this.bb.y1) {
            this.attackTime = 20;
            int damage = this.isTame() ? 4 : 2;
            target.hurt(this, damage);
        }
    }
    
    @Override
    public boolean interact(final Player player) {
        final ItemInstance item = player.inventory.getSelected();

        if (this.isTame()) {
            if (item != null) {
                if (Item.items[item.id] instanceof FoodItem) {
                    FoodItem food = ((FoodItem) Item.items[item.id]);

                    if (food.isMeat()) {
                        if (this.entityData.getInteger(DATA_HEALTH_ID) < MAX_HEALTH) {
                            item.count--;
                            if (item.count <= 0) {
                                player.inventory.setItem(player.inventory.selected, null);
                            }
                            this.heal(((FoodItem) Item.porkChop_raw).getNutrition());
                            return true;
                        }
                    }
                }
            }
            if (player.name.equalsIgnoreCase(this.getOwner())) {
                if (!this.level.isClientSide) {
                    this.setSitting(!this.isSitting());
                    this.jumping = false;
                    this.setPath(null);
                }
                return true;
            }
        } else {
            if (item != null && item.id == Item.bone.id && !this.isAngry()) {
                item.count--;
                if (item.count <= 0) {
                    player.inventory.setItem(player.inventory.selected, null);
                }

                if (!this.level.isClientSide) {
                    if (this.random.nextInt(3) == 0) {
                        this.setTame(true);
                        this.setPath(null);
                        this.setSitting(true);
                        this.health = TAME_HEALTH;
                        this.setOwner(player.name);
                        this.spawnTamingParticles(true);
                        this.level.broadcastEntityEvent(this, EntityEvent.TAMING_SUCCEEDED);
                    }
                    else {
                        this.spawnTamingParticles(false);
                        this.level.broadcastEntityEvent(this, EntityEvent.TAMING_FAILED);
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    void spawnTamingParticles(final boolean success) {
        String id = !success ? "smoke" : "heart";

        for (int i = 0; i < 7; ++i) {
            double xa = this.random.nextGaussian() * 0.02;
            double ya = this.random.nextGaussian() * 0.02;
            double za = this.random.nextGaussian() * 0.02;
            this.level.addParticle(id, this.x + this.random.nextFloat() * this.bbWidth * 2.0f - this.bbWidth, this.y + 0.5 + this.random.nextFloat() * this.bbHeight, this.z + this.random.nextFloat() * this.bbWidth * 2.0f - this.bbWidth, xa, ya, za);
        }
    }
    
    @Override
    public void handleEntityEvent(final byte id) {
        if (id == EntityEvent.TAMING_SUCCEEDED) {
            this.spawnTamingParticles(true);
        }
        else if (id == EntityEvent.TAMING_FAILED) {
            this.spawnTamingParticles(false);
        }
        else if (id == EntityEvent.SHAKE_WETNESS) {
            this.isShaking = true;
            this.shakeAnim = 0.0f;
            this.shakeAnimO = 0.0f;
        }
        else {
            super.handleEntityEvent(id);
        }
    }
    
    public float getTailAngle() {
        if (this.isAngry()) return 0.49f * Mth.PI;
        if (this.isTame()) return (0.55f - (20 - this.entityData.getInteger(DATA_HEALTH_ID)) * 0.02f) * Mth.PI;
        return 0.2f * Mth.PI;
    }
    
    @Override
    public int getMaxSpawnClusterSize() {
        return 8;
    }
    
    public String getOwner() {
        return this.entityData.getString(DATA_OWNER_ID);
    }
    
    public void setOwner(final String name) {
        this.entityData.set(DATA_OWNER_ID, name);
    }
    
    public boolean isSitting() {
        return (this.entityData.getByte(DATA_FLAGS_ID) & 0x1) != 0x0;
    }
    
    public void setSitting(final boolean value) {
        final byte current = this.entityData.getByte(16);
        if (value) {
            this.entityData.set(DATA_FLAGS_ID, (byte)(current | 0x1));
        }
        else {
            this.entityData.set(DATA_FLAGS_ID, (byte)(current & ~0x1));
        }
    }
    
    public boolean isAngry() {
        return (this.entityData.getByte(DATA_FLAGS_ID) & 0x2) != 0x0;
    }
    
    public void setAngry(final boolean value) {
        final byte current = this.entityData.getByte(DATA_FLAGS_ID);
        if (value) {
            this.entityData.set(DATA_FLAGS_ID, (byte)(current | 0x2));
        }
        else {
            this.entityData.set(DATA_FLAGS_ID, (byte)(current & ~0x2));
        }
    }
    
    public boolean isTame() {
        return (this.entityData.getByte(DATA_FLAGS_ID) & 0x4) != 0x0;
    }
    
    public void setTame(final boolean value) {
        final byte current = this.entityData.getByte(DATA_FLAGS_ID);
        if (value) {
            this.entityData.set(DATA_FLAGS_ID, (byte)(current | 0x4));
        }
        else {
            this.entityData.set(DATA_FLAGS_ID, (byte)(current & ~0x4));
        }
    }
}
