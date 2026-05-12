// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.animal;

import java.util.Iterator;
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
    private boolean isInterested;
    private float interestedAngle;
    private float interestedAngleO;
    private boolean isWet;
    private boolean isShaking;
    private float shakeAnim;
    private float shakeAnimO;
    
    public Wolf(final Level level) {
        super(level);
        this.isInterested = false;
        this.textureName = "/mob/wolf.png";
        this.setSize(0.8f, 0.8f);
        this.runSpeed = 1.1f;
        this.health = 8;
    }
    
    @Override
    protected void definedSynchedData() {
        super.definedSynchedData();
        this.entityData.define(16, 0);
        this.entityData.define(17, "");
        this.entityData.define(18, new Integer(this.health));
    }
    
    @Override
    protected boolean makeStepSound() {
        return false;
    }
    
    @Override
    public String getTexture() {
        if (this.isTame()) {
            return "/mob/wolf_tame.png";
        }
        if (this.isAngry()) {
            return "/mob/wolf_angry.png";
        }
        return super.getTexture();
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("Angry", this.isAngry());
        compoundTag.putBoolean("Sitting", this.isSitting());
        if (this.getOwner() == null) {
            compoundTag.putString("Owner", "");
        }
        else {
            compoundTag.putString("Owner", this.getOwner());
        }
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setAngry(compoundTag.getBoolean("Angry"));
        this.setSitting(compoundTag.getBoolean("Sitting"));
        final String string = compoundTag.getString("Owner");
        if (string.length() > 0) {
            this.setOwner(string);
            this.setTame(true);
        }
    }
    
    @Override
    protected boolean removeWhenFarAway() {
        return !this.isTame();
    }
    
    @Override
    protected String getAmbientSound() {
        if (this.isAngry()) {
            return "mob.wolf.growl";
        }
        if (this.random.nextInt(3) != 0) {
            return "mob.wolf.bark";
        }
        if (this.isTame() && this.entityData.getInteger(18) < 10) {
            return "mob.wolf.whine";
        }
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
            final List entitiesOfClass = this.level.getEntitiesOfClass(Sheep.class, AABB.newTemp(this.x, this.y, this.z, this.x + 1.0, this.y + 1.0, this.z + 1.0).grow(16.0, 4.0, 16.0));
            if (!entitiesOfClass.isEmpty()) {
                this.setAttackTarget((Entity)entitiesOfClass.get(this.level.random.nextInt(entitiesOfClass.size())));
            }
        }
        if (this.isInWater()) {
            this.setSitting(false);
        }
        if (!this.level.isClientSide) {
            this.entityData.set(18, this.health);
        }
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
        this.isInterested = false;
        if (this.isLookingAtAnEntity() && !this.isPathFinding() && !this.isAngry()) {
            final Entity looking = this.getLookingAt();
            if (looking instanceof Player) {
                final ItemInstance selected = ((Player)looking).inventory.getSelected();
                if (selected != null) {
                    if (!this.isTame() && selected.id == Item.bone.id) {
                        this.isInterested = true;
                    }
                    else if (this.isTame() && Item.items[selected.id] instanceof FoodItem) {
                        this.isInterested = ((FoodItem)Item.items[selected.id]).isMeat();
                    }
                }
            }
        }
        if (!this.interpolateOnly && this.isWet && !this.isShaking && !this.isPathFinding() && this.onGround) {
            this.isShaking = true;
            this.shakeAnim = 0.0f;
            this.shakeAnimO = 0.0f;
            this.level.broadcastEntityEvent(this, (byte)8);
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
                final float n = (float)this.bb.y0;
                for (int n2 = (int)(Mth.sin((this.shakeAnim - 0.4f) * 3.1415927f) * 7.0f), i = 0; i < n2; ++i) {
                    this.level.addParticle("splash", this.x + (this.random.nextFloat() * 2.0f - 1.0f) * this.bbWidth * 0.5f, n + 0.8f, this.z + (this.random.nextFloat() * 2.0f - 1.0f) * this.bbWidth * 0.5f, this.xd, this.yd, this.zd);
                }
            }
        }
    }
    
    public boolean isWet() {
        return this.isWet;
    }
    
    public float getWetShade(final float partialTick) {
        return 0.75f + (this.shakeAnimO + (this.shakeAnim - this.shakeAnimO) * partialTick) / 2.0f * 0.25f;
    }
    
    public float getBodyRollAngle(final float partialTick, final float offset) {
        float n = (this.shakeAnimO + (this.shakeAnim - this.shakeAnimO) * partialTick + offset) / 1.8f;
        if (n < 0.0f) {
            n = 0.0f;
        }
        else if (n > 1.0f) {
            n = 1.0f;
        }
        return Mth.sin(n * 3.1415927f) * Mth.sin(n * 3.1415927f * 11.0f) * 0.15f * 3.1415927f;
    }
    
    public float getHeadRollAngle(final float partialTick) {
        return (this.interestedAngleO + (this.interestedAngle - this.interestedAngleO) * partialTick) * 0.15f * 3.1415927f;
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
        if (path == null && distance > 12.0f) {
            final int n = Mth.floor(owner.x) - 2;
            final int n2 = Mth.floor(owner.z) - 2;
            final int floor = Mth.floor(owner.bb.y0);
            for (int i = 0; i <= 4; ++i) {
                for (int j = 0; j <= 4; ++j) {
                    if (i < 1 || j < 1 || i > 3 || j > 3) {
                        if (this.level.isSolidBlockingTile(n + i, floor - 1, n2 + j) && !this.level.isSolidBlockingTile(n + i, floor, n2 + j) && !this.level.isSolidBlockingTile(n + i, floor + 1, n2 + j)) {
                            this.moveTo(n + i + 0.5f, floor, n2 + j + 0.5f, this.yRot, this.xRot);
                            return;
                        }
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
    public boolean hurt(Entity var_1_5D, int dmg) {
        this.setSitting(false);
        if (var_1_5D != null && !(var_1_5D instanceof Player) && !(var_1_5D instanceof Arrow)) {
            dmg = (dmg + 1) / 2;
        }
        if (super.hurt(var_1_5D, dmg)) {
            if (!this.isTame() && !this.isAngry()) {
                if (var_1_5D instanceof Player) {
                    this.setAngry(true);
                    this.attackTarget = var_1_5D;
                }
                if (var_1_5D instanceof Arrow && ((Arrow)var_1_5D).owner != null) {
                    var_1_5D = ((Arrow)var_1_5D).owner;
                }
                if (var_1_5D instanceof Mob) {
                    for (final Wolf wolf : this.level.getEntitiesOfClass(Wolf.class, AABB.newTemp(this.x, this.y, this.z, this.x + 1.0, this.y + 1.0, this.z + 1.0).grow(16.0, 4.0, 16.0))) {
                        if (!wolf.isTame() && wolf.attackTarget == null) {
                            wolf.attackTarget = var_1_5D;
                            if (!(var_1_5D instanceof Player)) {
                                continue;
                            }
                            wolf.setAngry(true);
                        }
                    }
                }
            }
            else if (var_1_5D != this && var_1_5D != null) {
                if (this.isTame() && var_1_5D instanceof Player && ((Player)var_1_5D).name.equalsIgnoreCase(this.getOwner())) {
                    return true;
                }
                this.attackTarget = var_1_5D;
            }
            return true;
        }
        return false;
    }
    
    @Override
    protected Entity findAttackTarget() {
        if (this.isAngry()) {
            return this.level.getNearestPlayer(this, 16.0);
        }
        return null;
    }
    
    @Override
    protected void checkHurtTarget(final Entity target, final float distance) {
        if (distance > 2.0f && distance < 6.0f && this.random.nextInt(10) == 0) {
            if (this.onGround) {
                final double n = target.x - this.x;
                final double n2 = target.z - this.z;
                final float sqrt = Mth.sqrt(n * n + n2 * n2);
                this.xd = n / sqrt * 0.5 * 0.800000011920929 + this.xd * 0.20000000298023224;
                this.zd = n2 / sqrt * 0.5 * 0.800000011920929 + this.zd * 0.20000000298023224;
                this.yd = 0.4000000059604645;
            }
        }
        else if (distance < 1.5 && target.bb.y1 > this.bb.y0 && target.bb.y0 < this.bb.y1) {
            this.attackTime = 20;
            int dmg = 2;
            if (this.isTame()) {
                dmg = 4;
            }
            target.hurt(this, dmg);
        }
    }
    
    @Override
    public boolean interact(final Player player) {
        final ItemInstance selected = player.inventory.getSelected();
        if (!this.isTame()) {
            if (selected != null && selected.id == Item.bone.id && !this.isAngry()) {
                final ItemInstance itemInstance = selected;
                --itemInstance.count;
                if (selected.count <= 0) {
                    player.inventory.setItem(player.inventory.selected, null);
                }
                if (!this.level.isClientSide) {
                    if (this.random.nextInt(3) == 0) {
                        this.setTame(true);
                        this.setPath(null);
                        this.setSitting(true);
                        this.health = 20;
                        this.setOwner(player.name);
                        this.spawnTamingParticles(true);
                        this.level.broadcastEntityEvent(this, (byte)7);
                    }
                    else {
                        this.spawnTamingParticles(false);
                        this.level.broadcastEntityEvent(this, (byte)6);
                    }
                }
                return true;
            }
        }
        else {
            if (selected != null && Item.items[selected.id] instanceof FoodItem && ((FoodItem)Item.items[selected.id]).isMeat() && this.entityData.getInteger(18) < 20) {
                final ItemInstance itemInstance2 = selected;
                --itemInstance2.count;
                if (selected.count <= 0) {
                    player.inventory.setItem(player.inventory.selected, null);
                }
                this.heal(((FoodItem)Item.porkChop_raw).getNutrition());
                return true;
            }
            if (player.name.equalsIgnoreCase(this.getOwner())) {
                if (!this.level.isClientSide) {
                    this.setSitting(!this.isSitting());
                    this.jumping = false;
                    this.setPath(null);
                }
                return true;
            }
        }
        return false;
    }
    
    void spawnTamingParticles(final boolean success) {
        String id = "heart";
        if (!success) {
            id = "smoke";
        }
        for (int i = 0; i < 7; ++i) {
            this.level.addParticle(id, this.x + this.random.nextFloat() * this.bbWidth * 2.0f - this.bbWidth, this.y + 0.5 + this.random.nextFloat() * this.bbHeight, this.z + this.random.nextFloat() * this.bbWidth * 2.0f - this.bbWidth, this.random.nextGaussian() * 0.02, this.random.nextGaussian() * 0.02, this.random.nextGaussian() * 0.02);
        }
    }
    
    @Override
    public void handleEntityEvent(final byte id) {
        if (id == 7) {
            this.spawnTamingParticles(true);
        }
        else if (id == 6) {
            this.spawnTamingParticles(false);
        }
        else if (id == 8) {
            this.isShaking = true;
            this.shakeAnim = 0.0f;
            this.shakeAnimO = 0.0f;
        }
        else {
            super.handleEntityEvent(id);
        }
    }
    
    public float getTailAngle() {
        if (this.isAngry()) {
            return 1.5393804f;
        }
        if (this.isTame()) {
            return (0.55f - (20 - this.entityData.getInteger(18)) * 0.02f) * 3.1415927f;
        }
        return 0.62831855f;
    }
    
    @Override
    public int getMaxSpawnClusterSize() {
        return 8;
    }
    
    public String getOwner() {
        return this.entityData.getString(17);
    }
    
    public void setOwner(final String name) {
        this.entityData.set(17, name);
    }
    
    public boolean isSitting() {
        return (this.entityData.getByte(16) & 0x1) != 0x0;
    }
    
    public void setSitting(final boolean value) {
        final byte byte1 = this.entityData.getByte(16);
        if (value) {
            this.entityData.set(16, (byte)(byte1 | 0x1));
        }
        else {
            this.entityData.set(16, (byte)(byte1 & 0xFFFFFFFE));
        }
    }
    
    public boolean isAngry() {
        return (this.entityData.getByte(16) & 0x2) != 0x0;
    }
    
    public void setAngry(final boolean value) {
        final byte byte1 = this.entityData.getByte(16);
        if (value) {
            this.entityData.set(16, (byte)(byte1 | 0x2));
        }
        else {
            this.entityData.set(16, (byte)(byte1 & 0xFFFFFFFD));
        }
    }
    
    public boolean isTame() {
        return (this.entityData.getByte(16) & 0x4) != 0x0;
    }
    
    public void setTame(final boolean value) {
        final byte byte1 = this.entityData.getByte(16);
        if (value) {
            this.entityData.set(16, (byte)(byte1 | 0x4));
        }
        else {
            this.entityData.set(16, (byte)(byte1 & 0xFFFFFFFB));
        }
    }
}
