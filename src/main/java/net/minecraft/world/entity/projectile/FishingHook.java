// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.projectile;

import net.minecraft.SharedConstants;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.item.ItemEntity;
import com.mojang.nbt.CompoundTag;
import java.util.List;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.Item;
import util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

public class FishingHook extends Entity
{
    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;
    private int lastTile = 0;
    private boolean inGround = false;
    public int shakeTime = 0;
    public Player owner;
    private int life;
    private int flightTime = 0;
    private int nibble = 0;
    public Entity hookedIn = null;
    private int lSteps;
    private double lx, ly, lz, lyr, lxr;
    private double lxd, lyd, lzd;
    
    public FishingHook(final Level level) {
        super(level);
        this.setSize(0.25f, 0.25f);
        this.noCulling = true;
    }
    
    public FishingHook(final Level level, final double x, final double y, final double z) {
        this(level);
        this.setPos(x, y, z);
        this.noCulling = true;
    }
    
    public FishingHook(final Level level, final Player mob) {
        super(level);
        this.noCulling = true;

        this.owner = mob;
        this.owner.fishing = this;

        this.setSize(0.25f, 0.25f);
        this.moveTo(mob.x, mob.y + 1.62 - mob.heightOffset, mob.z, mob.yRot, mob.xRot);

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
    
    @Override
    protected void definedSynchedData() {
    }
    
    @Override
    public boolean shouldRenderAtSqrDistance(final double distance) {
        double size = this.bb.getSize() * 4.0;
        size *= 64.0;
        return distance < size * size;
    }
    
    public void shoot(double xd, double yd, double zd, final float pow, final float uncertainty) {
        final float dist = Mth.sqrt(xd * xd + yd * yd + zd * zd);

        xd /= dist;
        yd /= dist;
        zd /= dist;

        xd += this.random.nextGaussian() * 0.0075f * uncertainty;
        yd += this.random.nextGaussian() * 0.0075f * uncertainty;
        zd += this.random.nextGaussian() * 0.0075f * uncertainty;

        xd *= pow;
        yd *= pow;
        zd *= pow;

        this.xd = xd;
        this.yd = yd;
        this.zd = zd;

        final float sd = Mth.sqrt(xd * xd + zd * zd);
        this.yRotO = this.yRot = (float)(Math.atan2(xd, zd) * 180.0 / Math.PI);
        this.xRotO = this.xRot = (float)(Math.atan2(yd, sd) * 180.0 / Math.PI);
        this.life = 0;
    }
    
    @Override
    public void lerpTo(final double x, final double y, final double z, final float yRot, final float xRot, final int steps) {
        this.lx = x;
        this.ly = y;
        this.lz = z;
        this.lyr = yRot;
        this.lxr = xRot;

        this.lSteps = steps;

        this.xd = this.lxd;
        this.yd = this.lyd;
        this.zd = this.lzd;
    }
    
    @Override
    public void lerpMotion(final double xd, final double yd, final double zd) {
        this.lxd = this.xd = xd;
        this.lyd = this.yd = yd;
        this.lzd = this.zd = zd;
    }
    
    @Override
    public void tick() {
        super.tick();

        if (this.lSteps > 0) {
            final double xt = this.x + (this.lx - this.x) / this.lSteps;
            final double yt = this.y + (this.ly - this.y) / this.lSteps;
            final double zt = this.z + (this.lz - this.z) / this.lSteps;

            double yrd = this.lyr - this.yRot;
            while (yrd < -180.0) yrd += 360.0;
            while (yrd >= 180.0) yrd -= 360.0;

            this.yRot += (float)(yrd / this.lSteps);
            this.xRot += (float)((this.lxr - this.xRot) / this.lSteps);

            this.lSteps--;
            this.setPos(xt, yt, zt);
            this.setRot(this.yRot, this.xRot);
            return;
        }

        if (!this.level.isClientSide) {
            final ItemInstance selectedItem = this.owner.getSelectedItem();
            if (this.owner.removed || !this.owner.isAlive() || selectedItem == null || selectedItem.getItem() != Item.fishingRod || this.distanceToSqr(this.owner) > 1024.0) {
                this.remove();
                this.owner.fishing = null;
                return;
            }

            if (this.hookedIn != null) {
                if (this.hookedIn.removed) this.hookedIn = null;
                else {
                    this.x = this.hookedIn.x;
                    this.y = this.hookedIn.bb.y0 + this.hookedIn.bbHeight * 0.8;
                    this.z = this.hookedIn.z;
                    return;
                }
            }
        }

        if (this.shakeTime > 0) this.shakeTime--;

        if (this.inGround) {
            int tile = this.level.getTile(this.xTile, this.yTile, this.zTile);
            if (tile == this.lastTile) {
                this.life++;
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

        if (res != null) {
            if (res.entity != null) {
                if (res.entity.hurt(this.owner, 0)) {
                    this.hookedIn = res.entity;
                }
            }
            else {
                this.inGround = true;
            }
        }

        if (this.inGround) return;

        this.move(this.xd, this.yd, this.zd);

        final float sd = Mth.sqrt(this.xd * this.xd + this.zd * this.zd);
        this.yRot = (float)(Math.atan2(this.xd, this.zd) * 180.0 / Math.PI);
        this.xRot = (float)(Math.atan2(this.yd, sd) * 180.0 / Math.PI);

        while (this.xRot - this.xRotO < -180.0f) this.xRotO -= 360.0f;
        while (this.xRot - this.xRotO >= 180.0f) this.xRotO += 360.0f;
        while (this.yRot - this.yRotO < -180.0f) this.yRotO -= 360.0f;
        while (this.yRot - this.yRotO >= 180.0f) this.yRotO += 360.0f;

        this.xRot = this.xRotO + (this.xRot - this.xRotO) * 0.2f;
        this.yRot = this.yRotO + (this.yRot - this.yRotO) * 0.2f;

        float inertia = 0.92f;

        if (this.onGround || this.horizontalCollision) {
            inertia = 0.5f;
        }

        final int steps = 5;
        double waterPercentage = 0.0;
        for (int i = 0; i < steps; ++i) {
            double y0 = this.bb.y0 + (this.bb.y1 - this.bb.y0) * (i + 0) / steps - 2 / 16.0f + 2 / 16.0f;
            double y1 = this.bb.y0 + (this.bb.y1 - this.bb.y0) * (i + 1) / steps - 2 / 16.0f + 2 / 16.0f;
            AABB bb2 = AABB.newTemp(this.bb.x0, y0, this.bb.z0, this.bb.x1, y1, this.bb.z1);
            if (this.level.containsLiquid(bb2, Material.water)) {
                waterPercentage += 1.0 / steps;
            }
        }

        if (waterPercentage > 0.0) {
            if (this.nibble > 0) {
                this.nibble--;
            }
            else {
                int nibbleOdds = 500;
                if (this.level.isRainingAt(Mth.floor(this.x), Mth.floor(this.y) + 1, Mth.floor(this.z))) nibbleOdds = 300;

                if (this.random.nextInt(nibbleOdds) == 0) {
                    this.nibble = this.random.nextInt(30) + 10;
                    this.yd -= 0.2f;
                    this.level.playSound(this, "random.splash", 0.25f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
                    final float yt = (float)Mth.floor(this.bb.y0);
                    for (int i = 0; i < 1.0f + this.bbWidth * 20.0f; ++i) {
                        float xo = (this.random.nextFloat() * 2.0f - 1.0f) * this.bbWidth;
                        float zo = (this.random.nextFloat() * 2.0f - 1.0f) * this.bbWidth;
                        this.level.addParticle("bubble", this.x + xo, yt + 1.0f, this.z + zo, this.xd, this.yd - this.random.nextFloat() * 0.2f, this.zd);
                    }
                    for (int i = 0; i < 1.0f + this.bbWidth * 20.0f; ++i) {
                        float xo = (this.random.nextFloat() * 2.0f - 1.0f) * this.bbWidth;
                        float zo = (this.random.nextFloat() * 2.0f - 1.0f) * this.bbWidth;
                        this.level.addParticle("splash", this.x + xo, yt + 1.0f, this.z + zo, this.xd, this.yd, this.zd);
                    }
                }
            }
        }

        if (this.nibble > 0) {
            this.yd -= this.random.nextFloat() * this.random.nextFloat() * this.random.nextFloat() * 0.2;
        }

        double bob = waterPercentage * 2.0 - 1.0;
        this.yd += 0.04f * bob;
        if (waterPercentage > 0.0) {
            inertia *= (float)0.9;
            this.yd *= 0.8;
        }

        this.xd *= inertia;
        this.yd *= inertia;
        this.zd *= inertia;

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
    public float getShadowHeightOffs() {
        return 0.0f;
    }
    
    public int retrieve() {
        int dmg = 0;
        if (this.hookedIn != null) {
            final double xa = this.owner.x - this.x;
            final double ya = this.owner.y - this.y;
            final double za = this.owner.z - this.z;

            final double dist = Mth.sqrt(xa * xa + ya * ya + za * za);
            final double speed = 0.1;
            this.hookedIn.xd += xa * speed;
            this.hookedIn.yd += ya * speed + Mth.sqrt(dist) * 0.08;
            this.hookedIn.zd += za * speed;
            dmg = 3;
        }
        else if (this.nibble > 0) {
            final ItemEntity ie = new ItemEntity(this.level, this.x, this.y, this.z, new ItemInstance(Item.fish_raw));
            final double xa = this.owner.x - this.x;
            final double ya = this.owner.y - this.y;
            final double za = this.owner.z - this.z;

            final double dist = Mth.sqrt(xa * xa + ya * ya + za * za);
            final double speed = 0.1;
            ie.xd = xa * speed;
            ie.yd = ya * speed + Mth.sqrt(dist) * 0.08;
            ie.zd = za * speed;
            this.level.addEntity(ie);
            this.owner.awardStat(Stats.fishCaught, 1);
            dmg = 1;
        }
        if (this.inGround) dmg = 2;

        this.remove();
        this.owner.fishing = null;
        return dmg;
    }
}
