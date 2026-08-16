// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.projectile;

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
    private int xTile;
    private int yTile;
    private int zTile;
    private int lastTile;
    private boolean inGround;
    public int shakeTime;
    public Player owner;
    private int life;
    private int flightTime;
    private int nibble;
    public Entity hookedIn;
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;
    
    public FishingHook(final Level level) {
        super(level);
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.lastTile = 0;
        this.inGround = false;
        this.shakeTime = 0;
        this.flightTime = 0;
        this.nibble = 0;
        this.hookedIn = null;
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
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.lastTile = 0;
        this.inGround = false;
        this.shakeTime = 0;
        this.flightTime = 0;
        this.nibble = 0;
        this.hookedIn = null;
        this.noCulling = true;
        this.owner = mob;
        (this.owner.fishing = this).setSize(0.25f, 0.25f);
        this.moveTo(mob.x, mob.y + 1.62 - mob.heightOffset, mob.z, mob.yRot, mob.xRot);
        this.x -= Mth.cos(this.yRot / 180.0f * Mth.PI) * 0.16f;
        this.y -= 0.10000000149011612;
        this.z -= Mth.sin(this.yRot / 180.0f * Mth.PI) * 0.16f;
        this.setPos(this.x, this.y, this.z);
        this.heightOffset = 0.0f;
        final float n = 0.4f;
        this.xd = -Mth.sin(this.yRot / 180.0f * Mth.PI) * Mth.cos(this.xRot / 180.0f * Mth.PI) * n;
        this.zd = Mth.cos(this.yRot / 180.0f * Mth.PI) * Mth.cos(this.xRot / 180.0f * Mth.PI) * n;
        this.yd = -Mth.sin(this.xRot / 180.0f * Mth.PI) * n;
        this.shoot(this.xd, this.yd, this.zd, 1.5f, 1.0f);
    }
    
    @Override
    protected void definedSynchedData() {
    }
    
    @Override
    public boolean shouldRenderAtSqrDistance(final double distance) {
        final double n = this.bb.getSize() * 4.0 * 64.0;
        return distance < n * n;
    }
    
    public void shoot(double xd, double yd, double zd, final float pow, final float uncertainty) {
        final float sqrt = Mth.sqrt(xd * xd + yd * yd + zd * zd);
        xd /= sqrt;
        yd /= sqrt;
        zd /= sqrt;
        xd += this.random.nextGaussian() * 0.007499999832361937 * uncertainty;
        yd += this.random.nextGaussian() * 0.007499999832361937 * uncertainty;
        zd += this.random.nextGaussian() * 0.007499999832361937 * uncertainty;
        xd *= pow;
        yd *= pow;
        zd *= pow;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        final float sqrt2 = Mth.sqrt(xd * xd + zd * zd);
        final float n = (float)(Math.atan2(xd, zd) * 180.0 / Math.PI);
        this.yRot = n;
        this.yRotO = n;
        final float n2 = (float)(Math.atan2(yd, sqrt2) * 180.0 / Math.PI);
        this.xRot = n2;
        this.xRotO = n2;
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
        this.xd = xd;
        this.lxd = xd;
        this.yd = yd;
        this.lyd = yd;
        this.zd = zd;
        this.lzd = zd;
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.lSteps > 0) {
            final double x = this.x + (this.lx - this.x) / this.lSteps;
            final double y = this.y + (this.ly - this.y) / this.lSteps;
            final double z = this.z + (this.lz - this.z) / this.lSteps;
            double n;
            for (n = this.lyr - this.yRot; n < -180.0; n += 360.0) {}
            while (n >= 180.0) {
                n -= 360.0;
            }
            this.yRot += (float)(n / this.lSteps);
            this.xRot += (float)((this.lxr - this.xRot) / this.lSteps);
            --this.lSteps;
            this.setPos(x, y, z);
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
                if (!this.hookedIn.removed) {
                    this.x = this.hookedIn.x;
                    this.y = this.hookedIn.bb.y0 + this.hookedIn.bbHeight * 0.8;
                    this.z = this.hookedIn.z;
                    return;
                }
                this.hookedIn = null;
            }
        }
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
        double n2 = 0.0;
        for (int i = 0; i < entities.size(); ++i) {
            final Entity entity2 = entities.get(i);
            if (entity2.isPickable()) {
                if (entity2 != this.owner || this.flightTime >= 5) {
                    final float n3 = 0.3f;
                    final HitResult clip2 = entity2.bb.grow(n3, n3, n3).clip(temp, b);
                    if (clip2 != null) {
                        final double distanceTo = temp.distanceTo(clip2.pos);
                        if (distanceTo < n2 || n2 == 0.0) {
                            entity = entity2;
                            n2 = distanceTo;
                        }
                    }
                }
            }
        }
        if (entity != null) {
            clip = new HitResult(entity);
        }
        if (clip != null) {
            if (clip.entity != null) {
                if (clip.entity.hurt(this.owner, 0)) {
                    this.hookedIn = clip.entity;
                }
            }
            else {
                this.inGround = true;
            }
        }
        if (this.inGround) {
            return;
        }
        this.move(this.xd, this.yd, this.zd);
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
        float n4 = 0.92f;
        if (this.onGround || this.horizontalCollision) {
            n4 = 0.5f;
        }
        final int n5 = 5;
        double n6 = 0.0;
        for (int j = 0; j < n5; ++j) {
            if (this.level.containsLiquid(AABB.newTemp(this.bb.x0, this.bb.y0 + (this.bb.y1 - this.bb.y0) * (j + 0) / n5 - 0.125 + 0.125, this.bb.z0, this.bb.x1, this.bb.y0 + (this.bb.y1 - this.bb.y0) * (j + 1) / n5 - 0.125 + 0.125, this.bb.z1), Material.water)) {
                n6 += 1.0 / n5;
            }
        }
        if (n6 > 0.0) {
            if (this.nibble > 0) {
                --this.nibble;
            }
            else {
                int bound = 500;
                if (this.level.isRainingAt(Mth.floor(this.x), Mth.floor(this.y) + 1, Mth.floor(this.z))) {
                    bound = 300;
                }
                if (this.random.nextInt(bound) == 0) {
                    this.nibble = this.random.nextInt(30) + 10;
                    this.yd -= 0.20000000298023224;
                    this.level.playSound(this, "random.splash", 0.25f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
                    final float n7 = (float)Mth.floor(this.bb.y0);
                    for (int n8 = 0; n8 < 1.0f + this.bbWidth * 20.0f; ++n8) {
                        this.level.addParticle("bubble", this.x + (this.random.nextFloat() * 2.0f - 1.0f) * this.bbWidth, n7 + 1.0f, this.z + (this.random.nextFloat() * 2.0f - 1.0f) * this.bbWidth, this.xd, this.yd - this.random.nextFloat() * 0.2f, this.zd);
                    }
                    for (int n9 = 0; n9 < 1.0f + this.bbWidth * 20.0f; ++n9) {
                        this.level.addParticle("splash", this.x + (this.random.nextFloat() * 2.0f - 1.0f) * this.bbWidth, n7 + 1.0f, this.z + (this.random.nextFloat() * 2.0f - 1.0f) * this.bbWidth, this.xd, this.yd, this.zd);
                    }
                }
            }
        }
        if (this.nibble > 0) {
            this.yd -= this.random.nextFloat() * this.random.nextFloat() * this.random.nextFloat() * 0.2;
        }
        this.yd += 0.03999999910593033 * (n6 * 2.0 - 1.0);
        if (n6 > 0.0) {
            n4 *= (float)0.9;
            this.yd *= 0.8;
        }
        this.xd *= n4;
        this.yd *= n4;
        this.zd *= n4;
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
        int n = 0;
        if (this.hookedIn != null) {
            final double n2 = this.owner.x - this.x;
            final double n3 = this.owner.y - this.y;
            final double n4 = this.owner.z - this.z;
            final double x = Mth.sqrt(n2 * n2 + n3 * n3 + n4 * n4);
            final double n5 = 0.1;
            final Entity hookedIn = this.hookedIn;
            hookedIn.xd += n2 * n5;
            final Entity hookedIn2 = this.hookedIn;
            hookedIn2.yd += n3 * n5 + Mth.sqrt(x) * 0.08;
            final Entity hookedIn3 = this.hookedIn;
            hookedIn3.zd += n4 * n5;
            n = 3;
        }
        else if (this.nibble > 0) {
            final ItemEntity e = new ItemEntity(this.level, this.x, this.y, this.z, new ItemInstance(Item.fish_raw));
            final double n6 = this.owner.x - this.x;
            final double n7 = this.owner.y - this.y;
            final double n8 = this.owner.z - this.z;
            final double x2 = Mth.sqrt(n6 * n6 + n7 * n7 + n8 * n8);
            final double n9 = 0.1;
            e.xd = n6 * n9;
            e.yd = n7 * n9 + Mth.sqrt(x2) * 0.08;
            e.zd = n8 * n9;
            this.level.addEntity(e);
            this.owner.awardStat(Stats.fishCaught, 1);
            n = 1;
        }
        if (this.inGround) {
            n = 2;
        }
        this.remove();
        this.owner.fishing = null;
        return n;
    }
}
