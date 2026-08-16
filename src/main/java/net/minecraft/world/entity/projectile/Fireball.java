// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.projectile;

import com.mojang.nbt.CompoundTag;
import java.util.List;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;

public class Fireball extends Entity
{
    private int xTile;
    private int yTile;
    private int zTile;
    private int lastTile;
    private boolean inGround;
    public int shakeTime;
    public Mob owner;
    private int life;
    private int flightTime;
    public double xPower;
    public double yPower;
    public double zPower;
    
    public Fireball(final Level level) {
        super(level);
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.lastTile = 0;
        this.inGround = false;
        this.shakeTime = 0;
        this.flightTime = 0;
        this.setSize(1.0f, 1.0f);
    }
    
    @Override
    protected void definedSynchedData() {
    }
    
    @Override
    public boolean shouldRenderAtSqrDistance(final double distance) {
        final double n = this.bb.getSize() * 4.0 * 64.0;
        return distance < n * n;
    }
    
    public Fireball(final Level level, final double x, final double y, final double z, final double xa, final double ya, final double za) {
        super(level);
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.lastTile = 0;
        this.inGround = false;
        this.shakeTime = 0;
        this.flightTime = 0;
        this.setSize(1.0f, 1.0f);
        this.moveTo(x, y, z, this.yRot, this.xRot);
        this.setPos(x, y, z);
        final double n = Mth.sqrt(xa * xa + ya * ya + za * za);
        this.xPower = xa / n * 0.1;
        this.yPower = ya / n * 0.1;
        this.zPower = za / n * 0.1;
    }
    
    public Fireball(final Level level, final Mob mob, double xa, double ya, double za) {
        super(level);
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.lastTile = 0;
        this.inGround = false;
        this.shakeTime = 0;
        this.flightTime = 0;
        this.owner = mob;
        this.setSize(1.0f, 1.0f);
        this.moveTo(mob.x, mob.y, mob.z, mob.yRot, mob.xRot);
        this.setPos(this.x, this.y, this.z);
        this.heightOffset = 0.0f;
        final double xd = 0.0;
        this.zd = xd;
        this.yd = xd;
        this.xd = xd;
        xa += this.random.nextGaussian() * 0.4;
        ya += this.random.nextGaussian() * 0.4;
        za += this.random.nextGaussian() * 0.4;
        final double n = Mth.sqrt(xa * xa + ya * ya + za * za);
        this.xPower = xa / n * 0.1;
        this.yPower = ya / n * 0.1;
        this.zPower = za / n * 0.1;
    }
    
    @Override
    public void tick() {
        super.tick();
        this.onFire = 10;
        if (this.shakeTime > 0) {
            --this.shakeTime;
        }
        if (this.inGround) {
            if (this.level.getTile(this.xTile, this.yTile, this.zTile) == this.lastTile) {
                ++this.life;
                if (this.life == 1200) {
                    this.remove();
                }
                return;
            }
            this.inGround = false;
            this.xd *= this.random.nextFloat() * 0.2f;
            this.yd *= this.random.nextFloat() * 0.2f;
            this.zd *= this.random.nextFloat() * 0.2f;
            this.life = 0;
            this.flightTime = 0;
        }
        else {
            ++this.flightTime;
        }
        HitResult clip = this.level.clip(Vec3.newTemp(this.x, this.y, this.z), Vec3.newTemp(this.x + this.xd, this.y + this.yd, this.z + this.zd));
        final Vec3 temp = Vec3.newTemp(this.x, this.y, this.z);
        Vec3 b = Vec3.newTemp(this.x + this.xd, this.y + this.yd, this.z + this.zd);
        if (clip != null) {
            b = Vec3.newTemp(clip.pos.x, clip.pos.y, clip.pos.z);
        }
        Entity entity = null;
        final List<Entity> entities = this.level.getEntities(this, this.bb.expand(this.xd, this.yd, this.zd).grow(1.0, 1.0, 1.0));
        double n = 0.0;
        for (int i = 0; i < entities.size(); ++i) {
            final Entity entity2 = entities.get(i);
            if (entity2.isPickable()) {
                if (entity2 != this.owner || this.flightTime >= 25) {
                    final float n2 = 0.3f;
                    final HitResult clip2 = entity2.bb.grow(n2, n2, n2).clip(temp, b);
                    if (clip2 != null) {
                        final double distanceTo = temp.distanceTo(clip2.pos);
                        if (distanceTo < n || n == 0.0) {
                            entity = entity2;
                            n = distanceTo;
                        }
                    }
                }
            }
        }
        if (entity != null) {
            clip = new HitResult(entity);
        }
        if (clip != null) {
            if (!this.level.isClientSide) {
                if (clip.entity == null || clip.entity.hurt(this.owner, 0)) {}
                this.level.explode(null, this.x, this.y, this.z, 1.0f, true);
            }
            this.remove();
        }
        this.x += this.xd;
        this.y += this.yd;
        this.z += this.zd;
        final float sqrt = Mth.sqrt(this.xd * this.xd + this.zd * this.zd);
        this.yRot = (float)(Math.atan2(this.xd, this.zd) * 180.0 / Math.PI);
        this.xRot = (float)(Math.atan2(this.yd, sqrt) * 180.0 / Math.PI);
        while (this.xRot - this.xRotO < -180.0f) {
            this.xRotO -= 360.0f;
        }
        while (this.xRot - this.xRotO >= 180.0f) {
            this.xRotO += 360.0f;
        }
        while (this.yRot - this.yRotO < -180.0f) {
            this.yRotO -= 360.0f;
        }
        while (this.yRot - this.yRotO >= 180.0f) {
            this.yRotO += 360.0f;
        }
        this.xRot = this.xRotO + (this.xRot - this.xRotO) * 0.2f;
        this.yRot = this.yRotO + (this.yRot - this.yRotO) * 0.2f;
        float n3 = 0.95f;
        if (this.isInWater()) {
            for (int j = 0; j < 4; ++j) {
                final float n4 = 0.25f;
                this.level.addParticle("bubble", this.x - this.xd * n4, this.y - this.yd * n4, this.z - this.zd * n4, this.xd, this.yd, this.zd);
            }
            n3 = 0.8f;
        }
        this.xd += this.xPower;
        this.yd += this.yPower;
        this.zd += this.zPower;
        this.xd *= n3;
        this.yd *= n3;
        this.zd *= n3;
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
