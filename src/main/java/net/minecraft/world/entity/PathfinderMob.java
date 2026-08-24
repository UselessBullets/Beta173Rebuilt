// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity;

import net.minecraft.world.phys.Vec3;
import util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;

public class PathfinderMob extends Mob
{
    private static final int MAX_TURN = 30;
    private Path path;
    protected Entity attackTarget;
    protected boolean holdGround = false;
    
    public PathfinderMob(final Level level) {
        super(level);
    }
    
    protected boolean shouldHoldGround() {
        return false;
    }
    
    @Override
    protected void updateAi() {
        this.holdGround = this.shouldHoldGround();
        final float maxDist = 16.0f;

        if (this.attackTarget == null) {
            this.attackTarget = this.findAttackTarget();
            if (this.attackTarget != null) {
                this.path = this.level.findPath(this, this.attackTarget, maxDist);
            }
        }
        else {
            if (this.attackTarget.isAlive()) {
                final float d = this.attackTarget.distanceTo(this);
                if (this.canSee(this.attackTarget)) {
                    this.checkHurtTarget(this.attackTarget, d);
                }
                else {
                    this.cantSeeTarget(this.attackTarget, d);
                }
            } else {
                this.attackTarget = null;
            }
        }

        if (!this.holdGround && this.attackTarget != null && (this.path == null || this.random.nextInt(20) == 0)) {
            this.path = this.level.findPath(this, this.attackTarget, maxDist);
        }
        else if (!this.holdGround && ((this.path == null && this.random.nextInt(80) == 0) || this.random.nextInt(80) == 0)) {
            this.findRandomStrollLocation();
        }

        final int yFloor = Mth.floor(this.bb.y0 + 0.5);

        final boolean inWater = this.isInWater();
        final boolean inLava = this.isInLava();
        this.xRot = 0.0f;
        if (this.path == null || this.random.nextInt(100) == 0) {
            super.updateAi();
            this.path = null;
            return;
        }

        Vec3 target = this.path.current(this);
        final double r = this.bbWidth * 2.0f;
        while (target != null && target.distanceToSqr(this.x, target.y, this.z) < r * r) {
            this.path.next();
            if (this.path.isDone()) {
                target = null;
                this.path = null;
            }
            else target = this.path.current(this);
        }

        this.jumping = false;
        if (target != null) {
            final double xd = target.x - this.x;
            final double zd = target.z - this.z;
            final double yd = target.y - yFloor;
            float yRotD = (float)(Math.atan2(zd, xd) * 180.0 / Math.PI) - 90.0f;
            float rotDiff = yRotD - this.yRot;
            this.yya = this.runSpeed;
            while (rotDiff < -180.0f) rotDiff += 360.0f;
            while (rotDiff >= 180.0f) rotDiff -= 360.0f;

            if (rotDiff > MAX_TURN) rotDiff = MAX_TURN;
            if (rotDiff < -MAX_TURN) rotDiff = -MAX_TURN;
            this.yRot += rotDiff;

            if (this.holdGround) {
                if (this.attackTarget != null) {
                    final double xd2 = this.attackTarget.x - this.x;
                    final double zd2 = this.attackTarget.z - this.z;

                    final float oldyRot = this.yRot;
                    this.yRot = (float) (Math.atan2(zd2, xd2) * 180.0 / Math.PI) - 90.0f;

                    rotDiff = (oldyRot - this.yRot + 90.0f) * Mth.DEGRAD;
                    this.xxa = -Mth.sin(rotDiff) * this.yya * 1.0f;
                    this.yya = Mth.cos(rotDiff) * this.yya * 1.0f;
                }
            }
            if (yd > 0.0) {
                this.jumping = true;
            }
        }

        if (this.attackTarget != null) {
            this.lookAt(this.attackTarget, 30.0f, 30.0f);
        }

        if (this.horizontalCollision && !this.isPathFinding()) this.jumping = true;
        if (this.random.nextFloat() < 0.8f && (inWater || inLava)) this.jumping = true;
    }
    
    protected void findRandomStrollLocation() {
        boolean hasBest = false;
        int xBest = -1;
        int yBest = -1;
        int zBest = -1;
        float best = -99999.0f;
        for (int i = 0; i < 10; ++i) {
            final int xt = Mth.floor(this.x + this.random.nextInt(13) - 6.0);
            final int yt = Mth.floor(this.y + this.random.nextInt(7) - 3.0);
            final int zt = Mth.floor(this.z + this.random.nextInt(13) - 6.0);
            final float walkTargetValue = this.getWalkTargetValue(xt, yt, zt);
            if (walkTargetValue > best) {
                best = walkTargetValue;
                xBest = xt;
                yBest = yt;
                zBest = zt;
                hasBest = true;
            }
        }
        if (hasBest) {
            this.path = this.level.findPath(this, xBest, yBest, zBest, 10.0f);
        }
    }
    
    protected void checkHurtTarget(final Entity target, final float distance) {
    }
    
    protected void cantSeeTarget(final Entity target, final float distance) {
    }
    
    protected float getWalkTargetValue(final int x, final int y, final int z) {
        return 0.0f;
    }
    
    protected Entity findAttackTarget() {
        return null;
    }
    
    @Override
    public boolean canSpawn() {
        final int xt = Mth.floor(this.x);
        final int yt = Mth.floor(this.bb.y0);
        final int zt = Mth.floor(this.z);
        return super.canSpawn() && this.getWalkTargetValue(xt, yt, zt) >= 0.0f;
    }
    
    public boolean isPathFinding() {
        return this.path != null;
    }
    
    public void setPath(final Path path) {
        this.path = path;
    }
    
    public Entity getAttackTarget() {
        return this.attackTarget;
    }
    
    public void setAttackTarget(final Entity attacker) {
        this.attackTarget = attacker;
    }
}
