// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.projectile;

import com.mojang.nbt.CompoundTag;
import java.util.List;

import net.minecraft.SharedConstants;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;

public class Fireball extends Entity
{
    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;
    private int lastTile = 0;
    private boolean inGround = false;
    public int shakeTime = 0;
    public Mob owner;
    private int life;
    private int flightTime = 0;
    public double xPower, yPower, zPower;
    
    public Fireball(final Level level) {
        super(level);
        this.setSize(16 / 16.0f, 16 / 16.0f);
    }
    
    @Override
    protected void definedSynchedData() {
    }
    
    @Override
    public boolean shouldRenderAtSqrDistance(final double distance) {
        double size = this.bb.getSize() * 4.0;
        size *= 64.0;
        return distance < size * size;
    }
    
    public Fireball(final Level level, final double x, final double y, final double z, final double xa, final double ya, final double za) {
        super(level);
        this.setSize(16 / 16.0f, 16 / 16.0f);

        this.moveTo(x, y, z, this.yRot, this.xRot);
        this.setPos(x, y, z);

        final double dd = Mth.sqrt(xa * xa + ya * ya + za * za);
        this.xPower = xa / dd * 0.1;
        this.yPower = ya / dd * 0.1;
        this.zPower = za / dd * 0.1;
    }
    
    public Fireball(final Level level, final Mob mob, double xa, double ya, double za) {
        super(level);
        this.owner = mob;

        this.setSize(16 / 16.0f, 16 / 16.0f);

        this.moveTo(mob.x, mob.y, mob.z, mob.yRot, mob.xRot);
        this.setPos(this.x, this.y, this.z);
        this.heightOffset = 0.0f;

        this.xd = this.yd = this.zd = 0.0;

        xa += this.random.nextGaussian() * 0.4;
        ya += this.random.nextGaussian() * 0.4;
        za += this.random.nextGaussian() * 0.4;

        final double dd = Mth.sqrt(xa * xa + ya * ya + za * za);
        this.xPower = xa / dd * 0.1;
        this.yPower = ya / dd * 0.1;
        this.zPower = za / dd * 0.1;
    }
    
    @Override
    public void tick() {
        super.tick();

        this.onFire = 10;
        if (this.shakeTime > 0) --this.shakeTime;
        if (this.inGround) {
            int tile = this.level.getTile(this.xTile, this.yTile, this.zTile);
            if (tile == this.lastTile) {
                this.life++;
                if (this.life == SharedConstants.TICKS_PER_SECOND * 60) {
                    this.remove();
                }
                return;
            } else {
                this.inGround = false;

                this.xd *= this.random.nextFloat() * 0.2f;
                this.yd *= this.random.nextFloat() * 0.2f;
                this.zd *= this.random.nextFloat() * 0.2f;
                this.life = 0;
                this.flightTime = 0;
            }
        }
        else {
            ++this.flightTime;
        }

        Vec3 from = Vec3.newTemp(this.x, this.y, this.z);
        Vec3 to = Vec3.newTemp(this.x + this.xd, this.y + this.yd, this.z + this.zd);
        HitResult res = this.level.clip(from, to);

        from = Vec3.newTemp(this.x, this.y, this.z);
        to = Vec3.newTemp(this.x + this.xd, this.y + this.yd, this.z + this.zd);
        if (res != null) {
            to = Vec3.newTemp(res.pos.x, res.pos.y, res.pos.z);
        }
        Entity hitEntity = null;
        final List<Entity> objects = this.level.getEntities(this, this.bb.expand(this.xd, this.yd, this.zd).grow(1.0, 1.0, 1.0));
        double nearest = 0.0;
        for (int i = 0; i < objects.size(); ++i) {
            final Entity e = objects.get(i);
            if (!e.isPickable() || e == this.owner && this.flightTime < 25) continue;

            final float rr = 0.3f;
            AABB bb = e.bb.grow(rr, rr, rr);
            final HitResult p = bb.clip(from, to);
            if (p != null) {
                final double dd = from.distanceTo(p.pos);
                if (dd < nearest || nearest == 0.0) {
                    hitEntity = e;
                    nearest = dd;
                }
            }
        }

        if (hitEntity != null) {
            res = new HitResult(hitEntity);
        }
        if (res != null) {
            if (!this.level.isClientSide) {
                if (res.entity != null && !res.entity.hurt(this.owner, 0)) {}
                this.level.explode(null, this.x, this.y, this.z, 1.0f, true);
            }
            this.remove();
        }

        this.x += this.xd;
        this.y += this.yd;
        this.z += this.zd;

        final float sd = Mth.sqrt(this.xd * this.xd + this.zd * this.zd);
        this.yRot = (float)(Math.atan2(this.xd, this.zd) * 180.0 / Math.PI);
        this.xRot = (float)(Math.atan2(this.yd, sd) * 180.0 / Math.PI);

        while (this.xRot - this.xRotO < -180.0f) this.xRotO -= 360.0f;
        while (this.xRot - this.xRotO >= 180.0f) this.xRotO += 360.0f;
        while (this.yRot - this.yRotO < -180.0f) this.yRotO -= 360.0f;
        while (this.yRot - this.yRotO >= 180.0f) this.yRotO += 360.0f;

        this.xRot = this.xRotO + (this.xRot - this.xRotO) * 0.2f;
        this.yRot = this.yRotO + (this.yRot - this.yRotO) * 0.2f;

        float inertia = 0.95f;
        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i) {
                final float s = 1 / 4.0f;
                this.level.addParticle("bubble", this.x - this.xd * s, this.y - this.yd * s, this.z - this.zd * s, this.xd, this.yd, this.zd);
            }
            inertia = 0.8f;
        }

        this.xd += this.xPower;
        this.yd += this.yPower;
        this.zd += this.zPower;
        this.xd *= inertia;
        this.yd *= inertia;
        this.zd *= inertia;

        this.level.addParticle("smoke", this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0);
        this.setPos(this.x, this.y, this.z);
    }
    
    public void addAdditionalSaveData(final CompoundTag compoundTag) {
        compoundTag.putShort("xTile", (short)this.xTile);
        compoundTag.putShort("yTile", (short)this.yTile);
        compoundTag.putShort("zTile", (short)this.zTile);
        compoundTag.putByte("inTile", (byte)this.lastTile);
        compoundTag.putByte("shake", (byte)this.shakeTime);
        compoundTag.putByte("inGround", (byte)(this.inGround ? 1 : 0));
    }
    
    public void readAdditionalSaveData(final CompoundTag compoundTag) {
        this.xTile = compoundTag.getShort("xTile");
        this.yTile = compoundTag.getShort("yTile");
        this.zTile = compoundTag.getShort("zTile");
        this.lastTile = (compoundTag.getByte("inTile") & 0xFF);
        this.shakeTime = (compoundTag.getByte("shake") & 0xFF);
        this.inGround = (compoundTag.getByte("inGround") == 1);
    }
    
    @Override
    public boolean isPickable() {
        return true;
    }
    
    @Override
    public float getPickRadius() {
        return 1.0f;
    }
    
    @Override
    public boolean hurt(final Entity source, final int dmg) {
        this.markHurt();

        if (source != null) {
            final Vec3 lookAngle = source.getLookAngle();
            if (lookAngle != null) {
                this.xd = lookAngle.x;
                this.yd = lookAngle.y;
                this.zd = lookAngle.z;
                this.xPower = this.xd * 0.1;
                this.yPower = this.yd * 0.1;
                this.zPower = this.zd * 0.1;
            }
            return true;
        }
        return false;
    }
    
    @Override
    public float getShadowHeightOffs() {
        return 0.0f;
    }
}
