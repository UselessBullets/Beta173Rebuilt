// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.projectile;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import com.mojang.nbt.CompoundTag;
import java.util.List;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;

public class ThrownEgg extends Entity
{
    private int xTile;
    private int yTile;
    private int zTile;
    private int lastTile;
    private boolean inGround;
    public int shakeTime;
    private Mob owner;
    private int life;
    private int flightTime;
    
    public ThrownEgg(final Level level) {
        super(level);
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.lastTile = 0;
        this.inGround = false;
        this.shakeTime = 0;
        this.flightTime = 0;
        this.setSize(0.25f, 0.25f);
    }
    
    @Override
    protected void definedSynchedData() {
    }
    
    @Override
    public boolean shouldRenderAtSqrDistance(final double distance) {
        final double n = this.bb.getSize() * 4.0 * 64.0;
        return distance < n * n;
    }
    
    public ThrownEgg(final Level level, final Mob mob) {
        super(level);
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.lastTile = 0;
        this.inGround = false;
        this.shakeTime = 0;
        this.flightTime = 0;
        this.owner = mob;
        this.setSize(0.25f, 0.25f);
        this.moveTo(mob.x, mob.y + mob.getHeadHeight(), mob.z, mob.yRot, mob.xRot);
        this.x -= Mth.cos(this.yRot / 180.0f * Mth.PI) * 0.16f;
        this.y -= 0.1f;
        this.z -= Mth.sin(this.yRot / 180.0f * Mth.PI) * 0.16f;
        this.setPos(this.x, this.y, this.z);
        this.heightOffset = 0.0f;
        final float n = 0.4f;
        this.xd = -Mth.sin(this.yRot / 180.0f * Mth.PI) * Mth.cos(this.xRot / 180.0f * Mth.PI) * n;
        this.zd = Mth.cos(this.yRot / 180.0f * Mth.PI) * Mth.cos(this.xRot / 180.0f * Mth.PI) * n;
        this.yd = -Mth.sin(this.xRot / 180.0f * Mth.PI) * n;
        this.shoot(this.xd, this.yd, this.zd, 1.5f, 1.0f);
    }
    
    public ThrownEgg(final Level level, final double x, final double y, final double z) {
        super(level);
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.lastTile = 0;
        this.inGround = false;
        this.shakeTime = 0;
        this.flightTime = 0;
        this.life = 0;
        this.setSize(0.25f, 0.25f);
        this.setPos(x, y, z);
        this.heightOffset = 0.0f;
    }
    
    public void shoot(double x, double y, double z, final float pow, final float uncertainty) {
        final float sqrt = Mth.sqrt(x * x + y * y + z * z);
        x /= sqrt;
        y /= sqrt;
        z /= sqrt;
        x += this.random.nextGaussian() * 0.007499999832361937 * uncertainty;
        y += this.random.nextGaussian() * 0.007499999832361937 * uncertainty;
        z += this.random.nextGaussian() * 0.007499999832361937 * uncertainty;
        x *= pow;
        y *= pow;
        z *= pow;
        this.xd = x;
        this.yd = y;
        this.zd = z;
        final float sqrt2 = Mth.sqrt(x * x + z * z);
        final float n = (float)(Math.atan2(x, z) * 180.0 / Math.PI);
        this.yRot = n;
        this.yRotO = n;
        final float n2 = (float)(Math.atan2(y, sqrt2) * 180.0 / Math.PI);
        this.xRot = n2;
        this.xRotO = n2;
        this.life = 0;
    }
    
    @Override
    public void lerpMotion(final double xd, final double yd, final double zd) {
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        if (this.xRotO == 0.0f && this.yRotO == 0.0f) {
            final float sqrt = Mth.sqrt(xd * xd + zd * zd);
            final float n = (float)(Math.atan2(xd, zd) * 180.0 / Math.PI);
            this.yRot = n;
            this.yRotO = n;
            final float n2 = (float)(Math.atan2(yd, sqrt) * 180.0 / Math.PI);
            this.xRot = n2;
            this.xRotO = n2;
        }
    }
    
    @Override
    public void tick() {
        this.xOld = this.x;
        this.yOld = this.y;
        this.zOld = this.z;
        super.tick();
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
        if (!this.level.isClientSide) {
            Entity entity = null;
            final List<Entity> entities = this.level.getEntities(this, this.bb.expand(this.xd, this.yd, this.zd).grow(1.0, 1.0, 1.0));
            double n = 0.0;
            for (int i = 0; i < entities.size(); ++i) {
                final Entity entity2 = entities.get(i);
                if (entity2.isPickable()) {
                    if (entity2 != this.owner || this.flightTime >= 5) {
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
        }
        if (clip != null) {
            if (clip.entity == null || clip.entity.hurt(this.owner, 0)) {}
            if (!this.level.isClientSide && this.random.nextInt(8) == 0) {
                int n3 = 1;
                if (this.random.nextInt(32) == 0) {
                    n3 = 4;
                }
                for (int j = 0; j < n3; ++j) {
                    final Chicken e = new Chicken(this.level);
                    e.moveTo(this.x, this.y, this.z, this.yRot, 0.0f);
                    this.level.addEntity(e);
                }
            }
            for (int k = 0; k < 8; ++k) {
                this.level.addParticle("snowballpoof", this.x, this.y, this.z, 0.0, 0.0, 0.0);
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
        float n4 = 0.99f;
        final float n5 = 0.03f;
        if (this.isInWater()) {
            for (int l = 0; l < 4; ++l) {
                final float n6 = 0.25f;
                this.level.addParticle("bubble", this.x - this.xd * n6, this.y - this.yd * n6, this.z - this.zd * n6, this.xd, this.yd, this.zd);
            }
            n4 = 0.8f;
        }
        this.xd *= n4;
        this.yd *= n4;
        this.zd *= n4;
        this.yd -= n5;
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
    public void playerTouch(final Player player) {
        if (this.inGround && this.owner == player && this.shakeTime <= 0 && player.inventory.add(new ItemInstance(Item.arrow, 1))) {
            this.level.playSound(this, "random.pop", 0.2f, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7f + 1.0f) * 2.0f);
            player.take(this, 1);
            this.remove();
        }
    }
    
    @Override
    public float getShadowHeightOffs() {
        return 0.0f;
    }
}
