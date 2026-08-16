// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.monster;

import net.minecraft.world.item.Item;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Fireball;
import util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.FlyingMob;

public class Ghast extends FlyingMob implements Enemy
{
    public int floatDuration;
    public double xTarget;
    public double yTarget;
    public double zTarget;
    private Entity target;
    private int retargetTime;
    public int oCharge;
    public int charge;
    
    public Ghast(final Level level) {
        super(level);
        this.floatDuration = 0;
        this.target = null;
        this.retargetTime = 0;
        this.oCharge = 0;
        this.charge = 0;
        this.textureName = "/mob/ghast.png";
        this.setSize(4.0f, 4.0f);
        this.fireImmune = true;
    }
    
    @Override
    protected void definedSynchedData() {
        super.definedSynchedData();
        this.entityData.define(16, 0);
    }
    
    @Override
    public void tick() {
        super.tick();
        this.textureName = ((this.entityData.getByte(16) == 1) ? "/mob/ghast_fire.png" : "/mob/ghast.png");
    }
    
    @Override
    protected void updateAi() {
        if (!this.level.isClientSide && this.level.difficulty == 0) {
            this.remove();
        }
        this.checkDespawn();
        this.oCharge = this.charge;
        final double n = this.xTarget - this.x;
        final double n2 = this.yTarget - this.y;
        final double n3 = this.zTarget - this.z;
        final double dist = Mth.sqrt(n * n + n2 * n2 + n3 * n3);
        if (dist < 1.0 || dist > 60.0) {
            this.xTarget = this.x + (this.random.nextFloat() * 2.0f - 1.0f) * 16.0f;
            this.yTarget = this.y + (this.random.nextFloat() * 2.0f - 1.0f) * 16.0f;
            this.zTarget = this.z + (this.random.nextFloat() * 2.0f - 1.0f) * 16.0f;
        }
        if (this.floatDuration-- <= 0) {
            this.floatDuration += this.random.nextInt(5) + 2;
            if (this.canReach(this.xTarget, this.yTarget, this.zTarget, dist)) {
                this.xd += n / dist * 0.1;
                this.yd += n2 / dist * 0.1;
                this.zd += n3 / dist * 0.1;
            }
            else {
                this.xTarget = this.x;
                this.yTarget = this.y;
                this.zTarget = this.z;
            }
        }
        if (this.target != null && this.target.removed) {
            this.target = null;
        }
        if (this.target == null || this.retargetTime-- <= 0) {
            this.target = this.level.getNearestPlayer(this, 100.0);
            if (this.target != null) {
                this.retargetTime = 20;
            }
        }
        final double n4 = 64.0;
        if (this.target != null && this.target.distanceToSqr(this) < n4 * n4) {
            final double n5 = this.target.x - this.x;
            final double ya = this.target.bb.y0 + this.target.bbHeight / 2.0f - (this.y + this.bbHeight / 2.0f);
            final double n6 = this.target.z - this.z;
            final float n7 = -(float)Math.atan2(n5, n6) * Mth.RADDEG;
            this.yRot = n7;
            this.yBodyRot = n7;
            if (this.canSee(this.target)) {
                if (this.charge == 10) {
                    this.level.playSound(this, "mob.ghast.charge", this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                }
                ++this.charge;
                if (this.charge == 20) {
                    this.level.playSound(this, "mob.ghast.fireball", this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                    final Fireball e = new Fireball(this.level, this, n5, ya, n6);
                    final double n8 = 4.0;
                    final Vec3 viewVector = this.getViewVector(1.0f);
                    e.x = this.x + viewVector.x * n8;
                    e.y = this.y + this.bbHeight / 2.0f + 0.5;
                    e.z = this.z + viewVector.z * n8;
                    this.level.addEntity(e);
                    this.charge = -40;
                }
            }
            else if (this.charge > 0) {
                --this.charge;
            }
        }
        else {
            final float n9 = -(float)Math.atan2(this.xd, this.zd) * Mth.RADDEG;
            this.yRot = n9;
            this.yBodyRot = n9;
            if (this.charge > 0) {
                --this.charge;
            }
        }
        if (!this.level.isClientSide) {
            final byte byte1 = this.entityData.getByte(16);
            final byte b = (byte)((this.charge > 10) ? 1 : 0);
            if (byte1 != b) {
                this.entityData.set(16, b);
            }
        }
    }
    
    private boolean canReach(final double xt, final double yt, final double zt, final double dist) {
        final double xa = (this.xTarget - this.x) / dist;
        final double ya = (this.yTarget - this.y) / dist;
        final double za = (this.zTarget - this.z) / dist;
        final AABB copy = this.bb.copy();
        for (int n = 1; n < dist; ++n) {
            copy.move(xa, ya, za);
            if (this.level.getCubes(this, copy).size() > 0) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    protected String getAmbientSound() {
        return "mob.ghast.moan";
    }
    
    @Override
    protected String getHurtSound() {
        return "mob.ghast.scream";
    }
    
    @Override
    protected String getDeathSound() {
        return "mob.ghast.death";
    }
    
    @Override
    protected int getDeathLoot() {
        return Item.sulphur.id;
    }
    
    @Override
    protected float getSoundVolume() {
        return 10.0f;
    }
    
    @Override
    public boolean canSpawn() {
        return this.random.nextInt(20) == 0 && super.canSpawn() && this.level.difficulty > 0;
    }
    
    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }
}
