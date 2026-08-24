// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.projectile;

import net.minecraft.SharedConstants;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import com.mojang.nbt.CompoundTag;
import java.util.List;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;

public class ThrownEgg extends Entity
{
    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;
    private int lastTile = 0;
    private boolean inGround = false;
    public int shakeTime = 0;
    private Mob owner;
    private int life;
    private int flightTime = 0;
    
    public ThrownEgg(final Level level) {
        super(level);
        this.setSize(0.25f, 0.25f);
    }
    
    @Override
    protected void definedSynchedData() {
    }
    
    @Override
    public boolean shouldRenderAtSqrDistance(final double distance) {
        double size = this.bb.getSize() * 4.0;
        size = size * 64.0;
        return distance < size * size;
    }
    
    public ThrownEgg(final Level level, final Mob mob) {
        super(level);
        this.owner = mob;

        this.setSize(0.25f, 0.25f);

        this.moveTo(mob.x, mob.y + mob.getHeadHeight(), mob.z, mob.yRot, mob.xRot);

        this.x -= Mth.cos(this.yRot / 180.0f * Mth.PI) * 0.16f;
        this.y -= 0.1f;
        this.z -= Mth.sin(this.yRot / 180.0f * Mth.PI) * 0.16f;
        this.setPos(this.x, this.y, this.z);
        this.heightOffset = 0.0f;

        final float speed = 0.4f;
        this.xd = -Mth.sin(this.yRot / 180.0f * Mth.PI) * Mth.cos(this.xRot / 180.0f * Mth.PI) * speed;
        this.zd = Mth.cos(this.yRot / 180.0f * Mth.PI) * Mth.cos(this.xRot / 180.0f * Mth.PI) * speed;
        this.yd = -Mth.sin(this.xRot / 180.0f * Mth.PI) * speed;

        this.shoot(this.xd, this.yd, this.zd, 1.5f, 1.0f);
    }
    
    public ThrownEgg(final Level level, final double x, final double y, final double z) {
        super(level);
        this.life = 0;

        this.setSize(0.25f, 0.25f);

        this.setPos(x, y, z);
        this.heightOffset = 0.0f;
    }
    
    public void shoot(double x, double y, double z, final float pow, final float uncertainty) {
        final float dist = Mth.sqrt(x * x + y * y + z * z);

        x /= dist;
        y /= dist;
        z /= dist;

        x += this.random.nextGaussian() * 0.0075f * uncertainty;
        y += this.random.nextGaussian() * 0.0075f * uncertainty;
        z += this.random.nextGaussian() * 0.0075f * uncertainty;

        x *= pow;
        y *= pow;
        z *= pow;

        this.xd = x;
        this.yd = y;
        this.zd = z;

        final float sd = Mth.sqrt(x * x + z * z);
        this.yRotO = this.yRot = (float)(Math.atan2(x, z) * 180.0 / Math.PI);
        this.xRotO = this.xRot = (float)(Math.atan2(y, sd) * 180.0 / Math.PI);
        this.life = 0;
    }
    
    @Override
    public void lerpMotion(final double xd, final double yd, final double zd) {
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        if (this.xRotO == 0.0f && this.yRotO == 0.0f) {
            final float sqrt = Mth.sqrt(xd * xd + zd * zd);
            this.yRotO = this.yRot = (float)(Math.atan2(xd, zd) * 180.0 / Math.PI);
            this.xRotO = this.xRot = (float)(Math.atan2(yd, sqrt) * 180.0 / Math.PI);
        }
    }
    
    @Override
    public void tick() {
        this.xOld = this.x;
        this.yOld = this.y;
        this.zOld = this.z;
        super.tick();

        if (this.shakeTime > 0) this.shakeTime--;

        if (this.inGround) {
            int tile = this.level.getTile(this.xTile, this.yTile, this.zTile);
            if (tile == this.lastTile) {
                ++this.life;
                if (this.life == SharedConstants.TICKS_PER_SECOND * 60) this.remove();
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
            this.flightTime++;
        }

        Vec3 from = Vec3.newTemp(this.x, this.y, this.z);
        Vec3 to = Vec3.newTemp(this.x + this.xd, this.y + this.yd, this.z + this.zd);
        HitResult res = this.level.clip(from, to);

        from = Vec3.newTemp(this.x, this.y, this.z);
        to = Vec3.newTemp(this.x + this.xd, this.y + this.yd, this.z + this.zd);
        if (res != null) {
            to = Vec3.newTemp(res.pos.x, res.pos.y, res.pos.z);
        }

        if (!this.level.isClientSide) {
            Entity hitEntity = null;
            final List<Entity> objects = this.level.getEntities(this, this.bb.expand(this.xd, this.yd, this.zd).grow(1.0, 1.0, 1.0));
            double nearest = 0.0;
            for (int i = 0; i < objects.size(); ++i) {
                final Entity e = objects.get(i);
                if (!e.isPickable() || e == this.owner && this.flightTime < 5) continue;

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
        }

        if (res != null) {
            if (res.entity != null && !res.entity.hurt(this.owner, 0)) {}
            if (!this.level.isClientSide && this.random.nextInt(8) == 0) {
                int count = 1;
                if (this.random.nextInt(32) == 0) count = 4;
                for (int i = 0; i < count; ++i) {
                    final Chicken chicken = new Chicken(this.level);
                    chicken.moveTo(this.x, this.y, this.z, this.yRot, 0.0f);
                    this.level.addEntity(chicken);
                }
            }

            for (int i = 0; i < 8; ++i) {
                this.level.addParticle("snowballpoof", this.x, this.y, this.z, 0.0, 0.0, 0.0);
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

        float inertia = 0.99f;
        final float gravity = 0.03f;

        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i) {
                final float s = 1 / 4.0f;
                this.level.addParticle("bubble", this.x - this.xd * s, this.y - this.yd * s, this.z - this.zd * s, this.xd, this.yd, this.zd);
            }
            inertia = 0.8f;
        }

        this.xd *= inertia;
        this.yd *= inertia;
        this.zd *= inertia;
        this.yd -= gravity;

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
        if (!this.inGround || this.owner != player || this.shakeTime > 0) return;

        if (player.inventory.add(new ItemInstance(Item.arrow, 1))) {
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
