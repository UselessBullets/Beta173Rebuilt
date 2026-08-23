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
    public static final int DATA_IS_CHARGING = 16;
    public int floatDuration = 0;
    public double xTarget, yTarget, zTarget;
    private Entity target = null;
    private int retargetTime = 0;
    public int oCharge = 0;
    public int charge = 0;
    
    public Ghast(final Level level) {
        super(level);
        this.textureName = "/mob/ghast.png";
        this.setSize(4.0f, 4.0f);
        this.fireImmune = true;
    }
    
    @Override
    protected void definedSynchedData() {
        super.definedSynchedData();
        this.entityData.define(DATA_IS_CHARGING, (byte)0);
    }
    
    @Override
    public void tick() {
        super.tick();
        byte current = this.entityData.getByte(DATA_IS_CHARGING);
        this.textureName = current == 1 ? "/mob/ghast_fire.png" : "/mob/ghast.png";
    }
    
    @Override
    protected void updateAi() {
        if (!this.level.isClientSide && this.level.difficulty == 0) this.remove();
        this.checkDespawn();

        this.oCharge = this.charge;
        final double xd = this.xTarget - this.x;
        final double yd = this.yTarget - this.y;
        final double zd = this.zTarget - this.z;

        final double dd = Mth.sqrt(xd * xd + yd * yd + zd * zd);

        if (dd < 1.0 || dd > 60.0) {
            this.xTarget = this.x + (this.random.nextFloat() * 2.0f - 1.0f) * 16.0f;
            this.yTarget = this.y + (this.random.nextFloat() * 2.0f - 1.0f) * 16.0f;
            this.zTarget = this.z + (this.random.nextFloat() * 2.0f - 1.0f) * 16.0f;
        }

        if (this.floatDuration-- <= 0) {
            this.floatDuration += this.random.nextInt(5) + 2;

            if (this.canReach(this.xTarget, this.yTarget, this.zTarget, dd)) {
                this.xd += xd / dd * 0.1;
                this.yd += yd / dd * 0.1;
                this.zd += zd / dd * 0.1;
            }
            else {
                this.xTarget = this.x;
                this.yTarget = this.y;
                this.zTarget = this.z;
            }
        }

        if (this.target != null && this.target.removed) this.target = null;
        if (this.target == null || this.retargetTime-- <= 0) {
            this.target = this.level.getNearestPlayer(this, 100.0);
            if (this.target != null) {
                this.retargetTime = 20;
            }
        }

        final double maxDist = 64.0;
        if (this.target != null && this.target.distanceToSqr(this) < maxDist * maxDist) {
            final double xdd = this.target.x - this.x;
            final double ydd = this.target.bb.y0 + this.target.bbHeight / 2.0f - (this.y + this.bbHeight / 2.0f);
            final double zdd = this.target.z - this.z;
            this.yBodyRot = this.yRot = -(float)Math.atan2(xdd, zdd) * Mth.RADDEG;

            if (this.canSee(this.target)) {
                if (this.charge == 10) {
                    this.level.playSound(this, "mob.ghast.charge", this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                }
                this.charge++;
                if (this.charge == 20) {
                    this.level.playSound(this, "mob.ghast.fireball", this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                    final Fireball ie = new Fireball(this.level, this, xdd, ydd, zdd);
                    final double d = 4.0;
                    final Vec3 viewVector = this.getViewVector(1.0f);
                    ie.x = this.x + viewVector.x * d;
                    ie.y = this.y + this.bbHeight / 2.0f + 0.5;
                    ie.z = this.z + viewVector.z * d;
                    this.level.addEntity(ie);
                    this.charge = -40;
                }
            }
            else {
                if (this.charge > 0) --this.charge;
            }
        }
        else {
            this.yBodyRot = this.yRot = -(float)Math.atan2(this.xd, this.zd) * Mth.RADDEG;
            if (this.charge > 0) --this.charge;
        }

        if (!this.level.isClientSide) {
            final byte old = this.entityData.getByte(DATA_IS_CHARGING);
            final byte current = (byte)((this.charge > 10) ? 1 : 0);
            if (old != current) {
                this.entityData.set(DATA_IS_CHARGING, current);
            }
        }
    }
    
    private boolean canReach(final double xt, final double yt, final double zt, final double dist) {
        final double xd = (this.xTarget - this.x) / dist;
        final double yd = (this.yTarget - this.y) / dist;
        final double zz = (this.zTarget - this.z) / dist;

        final AABB bb = this.bb.copy();
        for (int d = 1; d < dist; ++d) {
            bb.move(xd, yd, zz);
            if (this.level.getCubes(this, bb).size() > 0) return false;
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
