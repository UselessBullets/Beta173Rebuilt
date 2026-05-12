// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.monster;

import net.minecraft.world.level.LightLayer;
import util.Mth;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.PathfinderMob;

public class Monster extends PathfinderMob implements Enemy
{
    protected int attackDamage;
    
    public Monster(final Level level) {
        super(level);
        this.attackDamage = 2;
        this.health = 20;
    }
    
    @Override
    public void aiStep() {
        if (this.getBrightness(1.0f) > 0.5f) {
            this.noActionTime += 2;
        }
        super.aiStep();
    }
    
    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide && this.level.difficulty == 0) {
            this.remove();
        }
    }
    
    @Override
    protected Entity findAttackTarget() {
        final Player nearestPlayer = this.level.getNearestPlayer(this, 16.0);
        if (nearestPlayer != null && this.canSee(nearestPlayer)) {
            return nearestPlayer;
        }
        return null;
    }
    
    @Override
    public boolean hurt(final Entity source, final int dmg) {
        if (!super.hurt(source, dmg)) {
            return false;
        }
        if (this.rider == source || this.riding == source) {
            return true;
        }
        if (source != this) {
            this.attackTarget = source;
        }
        return true;
    }
    
    @Override
    protected void checkHurtTarget(final Entity target, final float distance) {
        if (this.attackTime <= 0 && distance < 2.0f && target.bb.y1 > this.bb.y0 && target.bb.y0 < this.bb.y1) {
            this.attackTime = 20;
            target.hurt(this, this.attackDamage);
        }
    }
    
    @Override
    protected float getWalkTargetValue(final int x, final int y, final int z) {
        return 0.5f - this.level.getBrightness(x, y, z);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
    }
    
    @Override
    public boolean canSpawn() {
        final int floor = Mth.floor(this.x);
        final int floor2 = Mth.floor(this.bb.y0);
        final int floor3 = Mth.floor(this.z);
        if (this.level.getBrightness(LightLayer.Sky, floor, floor2, floor3) > this.random.nextInt(32)) {
            return false;
        }
        int n = this.level.getRawBrightness(floor, floor2, floor3);
        if (this.level.isThundering()) {
            final int skyDarken = this.level.skyDarken;
            this.level.skyDarken = 10;
            n = this.level.getRawBrightness(floor, floor2, floor3);
            this.level.skyDarken = skyDarken;
        }
        return n <= this.random.nextInt(8) && super.canSpawn();
    }
}
