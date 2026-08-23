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
    protected int attackDamage = 2;
    
    public Monster(final Level level) {
        super(level);
        this.health = 20;
    }
    
    @Override
    public void aiStep() {
        float br = this.getBrightness(1.0f);
        if (br > 0.5f) {
            this.noActionTime += 2;
        }

        super.aiStep();
    }
    
    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide && this.level.difficulty == 0) this.remove();
    }
    
    @Override
    protected Entity findAttackTarget() {
        final Player player = this.level.getNearestPlayer(this, 16.0);
        if (player != null && this.canSee(player)) return player;
        return null;
    }
    
    @Override
    public boolean hurt(final Entity source, final int dmg) {
        if (super.hurt(source, dmg)) {
            if (this.rider == source || this.riding == source) return true;

            if (source != this) {
                this.attackTarget = source;
            }
            return true;
        }
        return false;
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
        final int xt = Mth.floor(this.x);
        final int yt = Mth.floor(this.bb.y0);
        final int zt = Mth.floor(this.z);
        if (this.level.getBrightness(LightLayer.Sky, xt, yt, zt) > this.random.nextInt(32)) return false;

        int br = this.level.getRawBrightness(xt, yt, zt);

        if (this.level.isThundering()) {
            final int tmp = this.level.skyDarken;
            this.level.skyDarken = 10;
            br = this.level.getRawBrightness(xt, yt, zt);
            this.level.skyDarken = tmp;
        }

        return br <= this.random.nextInt(8) && super.canSpawn();
    }
}
