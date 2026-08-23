// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.item;

import net.minecraft.world.entity.player.Player;
import com.mojang.nbt.CompoundTag;
import java.util.List;
import util.Mth;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;

public class Boat extends Entity
{
    public int damage = 0;
    public int hurtTime = 0;
    public int hurtDir = 1;
    private int lSteps;
    private double lx, ly, lz, lyr, lxr;
    private double lxd, lyd, lzd;
    
    public Boat(final Level level) {
        super(level);
        this.blocksBuilding = true;
        this.setSize(1.5f, 0.6f);
        this.heightOffset = this.bbHeight / 2.0f;
    }
    
    @Override
    protected boolean makeStepSound() {
        return false;
    }
    
    @Override
    protected void definedSynchedData() {
    }
    
    @Override
    public AABB getCollideAgainstBox(final Entity entity) {
        return entity.bb;
    }
    
    @Override
    public AABB getCollideBox() {
        return this.bb;
    }
    
    @Override
    public boolean isPushable() {
        return true;
    }
    
    public Boat(final Level level, final double x, final double y, final double z) {
        this(level);
        this.setPos(x, y + this.heightOffset, z);

        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;

        this.xo = x;
        this.yo = y;
        this.zo = z;
    }
    
    @Override
    public double getRideHeight() {
        return this.bbHeight * 0.0 - 0.3f;
    }
    
    @Override
    public boolean hurt(final Entity source, final int dmg) {
        if (this.level.isClientSide || this.removed) return true;

        this.hurtDir = -this.hurtDir;
        this.hurtTime = 10;

        this.damage += dmg * 10;
        this.markHurt();

        if (this.damage > 20 * 2) {
            if (this.rider != null) this.rider.ride(this);
            for (int i = 0; i < 3; ++i) {
                this.spawnAtLocation(Tile.wood.id, 1, 0.0f);
            }
            for (int j = 0; j < 2; ++j) {
                this.spawnAtLocation(Item.stick.id, 1, 0.0f);
            }
            this.remove();
        }
        return true;
    }
    
    @Override
    public void animateHurt() {
        this.hurtDir = -this.hurtDir;
        this.hurtTime = 10;
        this.damage += this.damage * 10;
    }
    
    @Override
    public boolean isPickable() {
        return !this.removed;
    }
    
    @Override
    public void lerpTo(final double x, final double y, final double z, final float yRot, final float xRot, final int steps) {
        this.lx = x;
        this.ly = y;
        this.lz = z;
        this.lyr = yRot;
        this.lxr = xRot;
        this.lSteps = steps + 4;

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
        if (this.hurtTime > 0) --this.hurtTime;
        if (this.damage > 0) --this.damage;
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        final int steps = 5;
        double waterPercentage = 0.0;
        for (int i = 0; i < steps; ++i) {
            double y0 = this.bb.y0 + (this.bb.y1 - this.bb.y0) * (i + 0) / steps - 2 / 16.0f;
            double y1 = this.bb.y0 + (this.bb.y1 - this.bb.y0) * (i + 1) / steps - 2 / 16.0f;
            AABB bb2 = AABB.newTemp(this.bb.x0, y0, this.bb.z0, this.bb.x1, y1, this.bb.z1);
            if (this.level.containsLiquid(bb2, Material.water)) {
                waterPercentage += 1.0 / steps;
            }
        }

        if (this.level.isClientSide) {
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
            }
            else {
                double xt = this.x + this.xd;
                double yt = this.y + this.yd;
                double zt = this.z + this.zd;
                this.setPos(xt, yt, zt);

                if (this.onGround) {
                    this.xd *= 0.5;
                    this.yd *= 0.5;
                    this.zd *= 0.5;
                }
                this.xd *= 0.99f;
                this.yd *= 0.95f;
                this.zd *= 0.99f;
            }
            return;
        }

        if (waterPercentage < 1.0) {
            double bob = waterPercentage * 2.0 - 1.0;
            this.yd += 0.04f * bob;
        }
        else {
            if (this.yd < 0.0) this.yd /= 2.0;
            this.yd += 0.007f;
        }

        if (this.rider != null) {
            this.xd += this.rider.xd * 0.2;
            this.zd += this.rider.zd * 0.2;
        }

        // Useless - speculative name, clamps both xd and zd to +-0.4 independantly
        final double maxSpeed = 0.4;
        if (this.xd < -maxSpeed) this.xd = -maxSpeed;
        if (this.xd > maxSpeed) this.xd = maxSpeed;
        if (this.zd < -maxSpeed) this.zd = -maxSpeed;
        if (this.zd > maxSpeed) this.zd = maxSpeed;

        if (this.onGround) {
            this.xd *= 0.5;
            this.yd *= 0.5;
            this.zd *= 0.5;
        }
        this.move(this.xd, this.yd, this.zd);

        final double lastSpeed = Math.sqrt(this.xd * this.xd + this.zd * this.zd);
        if (lastSpeed > 0.15) {
            final double xa = Math.cos(this.yRot * Math.PI / 180.0);
            final double za = Math.sin(this.yRot * Math.PI / 180.0);

            for (int i = 0; i < 1.0 + lastSpeed * 60.0; ++i) {
                final double side = this.random.nextFloat() * 2.0f - 1.0f;
                final double side2 = (this.random.nextInt(2) * 2 - 1) * 0.7;

                if (this.random.nextBoolean()) {
                    double xx = this.x - xa * side * 0.8 + za * side2;
                    double zz = this.z - za * side * 0.8 - xa * side2;
                    this.level.addParticle("splash", xx, this.y - 2 / 16.0f, zz, this.xd, this.yd, this.zd);
                }
                else {
                    double xx = this.x + xa + za * side * 0.7;
                    double zz = this.z + za - xa * side * 0.7;
                    this.level.addParticle("splash", xx, this.y - 2 / 16.0f, zz, this.xd, this.yd, this.zd);
                }
            }
        }

        if (this.horizontalCollision && lastSpeed > 0.15) {
            if (!this.level.isClientSide) {
                this.remove();
                for (int i = 0; i < 3; ++i) {
                    this.spawnAtLocation(Tile.wood.id, 1, 0.0f);
                }
                for (int i = 0; i < 2; ++i) {
                    this.spawnAtLocation(Item.stick.id, 1, 0.0f);
                }
            }
        }
        else {
            this.xd *= 0.99f;
            this.yd *= 0.95f;
            this.zd *= 0.99f;
        }

        this.xRot = 0.0f;
        double yRotT = this.yRot;
        final double xDiff = this.xo - this.x;
        final double zDiff = this.zo - this.z;
        if (xDiff * xDiff + zDiff * zDiff > 0.001) {
            yRotT = (float)(Math.atan2(zDiff, xDiff) * 180.0 / Math.PI);
        }
        double rotDiff = yRotT - this.yRot;
        while (rotDiff >= 180.0) rotDiff -= 360.0;
        while (rotDiff < -180.0) rotDiff += 360.0;

        if (rotDiff > 20.0) rotDiff = 20.0;
        if (rotDiff < -20.0) rotDiff = -20.0;

        this.yRot += (float)rotDiff;
        this.setRot(this.yRot, this.xRot);

        final List<Entity> entities = this.level.getEntities(this, this.bb.grow(0.2f, 0.0, 0.2f));
        if (entities != null && entities.size() > 0) {
            for (int i = 0; i < entities.size(); ++i) {
                final Entity e = entities.get(i);
                if (e != this.rider && e.isPushable() && e instanceof Boat) {
                    e.push(this);
                }
            }
        }

        for (int i = 0; i < 4; ++i) {
            final int xx = Mth.floor(this.x + (i % 2 - 0.5) * 0.8);
            final int yy = Mth.floor(this.y);
            final int zz = Mth.floor(this.z + (i / 2 - 0.5) * 0.8);

            if (this.level.getTile(xx, yy, zz) == Tile.topSnow.id) {
                this.level.setTile(xx, yy, zz, 0);
            }
        }

        if (this.rider != null)
            if (this.rider.removed) this.rider = null;
    }
    
    @Override
    public void positionRider() {
        if (this.rider == null) return;

        double xa = Math.cos(this.yRot * Math.PI / 180.0) * 0.4;
        double za = Math.sin(this.yRot * Math.PI / 180.0) * 0.4;
        this.rider.setPos(this.x + xa, this.y + this.getRideHeight() + this.rider.getRidingHeight(), this.z + za);
    }
    
    @Override
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {
    }
    
    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {
    }
    
    @Override
    public float getShadowHeightOffs() {
        return 0.0f;
    }
    
    @Override
    public boolean interact(final Player player) {
        if (this.rider != null && this.rider instanceof Player && this.rider != player) return true;
        if (!this.level.isClientSide) {
            player.ride(this);
        }
        return true;
    }
}
