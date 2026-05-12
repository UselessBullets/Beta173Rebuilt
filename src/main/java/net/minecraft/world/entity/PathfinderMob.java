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
    private Path path;
    protected Entity attackTarget;
    protected boolean holdGround;
    
    public PathfinderMob(final Level level) {
        super(level);
        this.holdGround = false;
    }
    
    protected boolean shouldHoldGround() {
        return false;
    }
    
    @Override
    protected void updateAi() {
        this.holdGround = this.shouldHoldGround();
        final float n = 16.0f;
        if (this.attackTarget == null) {
            this.attackTarget = this.findAttackTarget();
            if (this.attackTarget != null) {
                this.path = this.level.findPath(this, this.attackTarget, n);
            }
        }
        else if (!this.attackTarget.isAlive()) {
            this.attackTarget = null;
        }
        else {
            final float distanceTo = this.attackTarget.distanceTo(this);
            if (this.canSee(this.attackTarget)) {
                this.checkHurtTarget(this.attackTarget, distanceTo);
            }
            else {
                this.cantSeeTarget(this.attackTarget, distanceTo);
            }
        }
        if (!this.holdGround && this.attackTarget != null && (this.path == null || this.random.nextInt(20) == 0)) {
            this.path = this.level.findPath(this, this.attackTarget, n);
        }
        else if (!this.holdGround && ((this.path == null && this.random.nextInt(80) == 0) || this.random.nextInt(80) == 0)) {
            this.findRandomStrollLocation();
        }
        final int floor = Mth.floor(this.bb.y0 + 0.5);
        final boolean inWater = this.isInWater();
        final boolean inLava = this.isInLava();
        this.xRot = 0.0f;
        if (this.path == null || this.random.nextInt(100) == 0) {
            super.updateAi();
            this.path = null;
            return;
        }
        Vec3 vec3 = this.path.current(this);
        final double n2 = this.bbWidth * 2.0f;
        while (vec3 != null && vec3.distanceToSqr(this.x, vec3.y, this.z) < n2 * n2) {
            this.path.next();
            if (this.path.isDone()) {
                vec3 = null;
                this.path = null;
            }
            else {
                vec3 = this.path.current(this);
            }
        }
        this.jumping = false;
        if (vec3 != null) {
            final double x = vec3.x - this.x;
            final double y = vec3.z - this.z;
            final double n3 = vec3.y - floor;
            float n4 = (float)(Math.atan2(y, x) * 180.0 / 3.1415927410125732) - 90.0f - this.yRot;
            this.yya = this.runSpeed;
            while (n4 < -180.0f) {
                n4 += 360.0f;
            }
            while (n4 >= 180.0f) {
                n4 -= 360.0f;
            }
            if (n4 > 30.0f) {
                n4 = 30.0f;
            }
            if (n4 < -30.0f) {
                n4 = -30.0f;
            }
            this.yRot += n4;
            if (this.holdGround && this.attackTarget != null) {
                final double x2 = this.attackTarget.x - this.x;
                final double y2 = this.attackTarget.z - this.z;
                final float yRot = this.yRot;
                this.yRot = (float)(Math.atan2(y2, x2) * 180.0 / 3.1415927410125732) - 90.0f;
                final float n5 = (yRot - this.yRot + 90.0f) * 3.1415927f / 180.0f;
                this.xxa = -Mth.sin(n5) * this.yya * 1.0f;
                this.yya = Mth.cos(n5) * this.yya * 1.0f;
            }
            if (n3 > 0.0) {
                this.jumping = true;
            }
        }
        if (this.attackTarget != null) {
            this.lookAt(this.attackTarget, 30.0f, 30.0f);
        }
        if (this.horizontalCollision && !this.isPathFinding()) {
            this.jumping = true;
        }
        if (this.random.nextFloat() < 0.8f && (inWater || inLava)) {
            this.jumping = true;
        }
    }
    
    protected void findRandomStrollLocation() {
        boolean b = false;
        int xBest = -1;
        int yBest = -1;
        int zBest = -1;
        float n = -99999.0f;
        for (int i = 0; i < 10; ++i) {
            final int floor = Mth.floor(this.x + this.random.nextInt(13) - 6.0);
            final int floor2 = Mth.floor(this.y + this.random.nextInt(7) - 3.0);
            final int floor3 = Mth.floor(this.z + this.random.nextInt(13) - 6.0);
            final float walkTargetValue = this.getWalkTargetValue(floor, floor2, floor3);
            if (walkTargetValue > n) {
                n = walkTargetValue;
                xBest = floor;
                yBest = floor2;
                zBest = floor3;
                b = true;
            }
        }
        if (b) {
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
        final int floor = Mth.floor(this.x);
        final int floor2 = Mth.floor(this.bb.y0);
        final int floor3 = Mth.floor(this.z);
        return super.canSpawn() && this.getWalkTargetValue(floor, floor2, floor3) >= 0.0f;
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
