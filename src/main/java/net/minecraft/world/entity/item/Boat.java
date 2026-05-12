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
    public int damage;
    public int hurtTime;
    public int hurtDir;
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;
    
    public Boat(final Level level) {
        super(level);
        this.damage = 0;
        this.hurtTime = 0;
        this.hurtDir = 1;
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
        return this.bbHeight * 0.0 - 0.30000001192092896;
    }
    
    @Override
    public boolean hurt(final Entity source, final int dmg) {
        if (this.level.isClientSide || this.removed) {
            return true;
        }
        this.hurtDir = -this.hurtDir;
        this.hurtTime = 10;
        this.damage += dmg * 10;
        this.markHurt();
        if (this.damage > 40) {
            if (this.rider != null) {
                this.rider.ride(this);
            }
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
        if (this.hurtTime > 0) {
            --this.hurtTime;
        }
        if (this.damage > 0) {
            --this.damage;
        }
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        final int n = 5;
        double n2 = 0.0;
        for (int i = 0; i < n; ++i) {
            if (this.level.containsLiquid(AABB.newTemp(this.bb.x0, this.bb.y0 + (this.bb.y1 - this.bb.y0) * (i + 0) / n - 0.125, this.bb.z0, this.bb.x1, this.bb.y0 + (this.bb.y1 - this.bb.y0) * (i + 1) / n - 0.125, this.bb.z1), Material.water)) {
                n2 += 1.0 / n;
            }
        }
        if (this.level.isClientSide) {
            if (this.lSteps > 0) {
                final double x = this.x + (this.lx - this.x) / this.lSteps;
                final double y = this.y + (this.ly - this.y) / this.lSteps;
                final double z = this.z + (this.lz - this.z) / this.lSteps;
                double n3;
                for (n3 = this.lyr - this.yRot; n3 < -180.0; n3 += 360.0) {}
                while (n3 >= 180.0) {
                    n3 -= 360.0;
                }
                this.yRot += (float)(n3 / this.lSteps);
                this.xRot += (float)((this.lxr - this.xRot) / this.lSteps);
                --this.lSteps;
                this.setPos(x, y, z);
                this.setRot(this.yRot, this.xRot);
            }
            else {
                this.setPos(this.x + this.xd, this.y + this.yd, this.z + this.zd);
                if (this.onGround) {
                    this.xd *= 0.5;
                    this.yd *= 0.5;
                    this.zd *= 0.5;
                }
                this.xd *= 0.9900000095367432;
                this.yd *= 0.949999988079071;
                this.zd *= 0.9900000095367432;
            }
            return;
        }
        if (n2 < 1.0) {
            this.yd += 0.03999999910593033 * (n2 * 2.0 - 1.0);
        }
        else {
            if (this.yd < 0.0) {
                this.yd /= 2.0;
            }
            this.yd += 0.007000000216066837;
        }
        if (this.rider != null) {
            this.xd += this.rider.xd * 0.2;
            this.zd += this.rider.zd * 0.2;
        }
        final double n4 = 0.4;
        if (this.xd < -n4) {
            this.xd = -n4;
        }
        if (this.xd > n4) {
            this.xd = n4;
        }
        if (this.zd < -n4) {
            this.zd = -n4;
        }
        if (this.zd > n4) {
            this.zd = n4;
        }
        if (this.onGround) {
            this.xd *= 0.5;
            this.yd *= 0.5;
            this.zd *= 0.5;
        }
        this.move(this.xd, this.yd, this.zd);
        final double sqrt = Math.sqrt(this.xd * this.xd + this.zd * this.zd);
        if (sqrt > 0.15) {
            final double cos = Math.cos(this.yRot * 3.141592653589793 / 180.0);
            final double sin = Math.sin(this.yRot * 3.141592653589793 / 180.0);
            for (int n5 = 0; n5 < 1.0 + sqrt * 60.0; ++n5) {
                final double n6 = this.random.nextFloat() * 2.0f - 1.0f;
                final double n7 = (this.random.nextInt(2) * 2 - 1) * 0.7;
                if (this.random.nextBoolean()) {
                    this.level.addParticle("splash", this.x - cos * n6 * 0.8 + sin * n7, this.y - 0.125, this.z - sin * n6 * 0.8 - cos * n7, this.xd, this.yd, this.zd);
                }
                else {
                    this.level.addParticle("splash", this.x + cos + sin * n6 * 0.7, this.y - 0.125, this.z + sin - cos * n6 * 0.7, this.xd, this.yd, this.zd);
                }
            }
        }
        if (this.horizontalCollision && sqrt > 0.15) {
            if (!this.level.isClientSide) {
                this.remove();
                for (int j = 0; j < 3; ++j) {
                    this.spawnAtLocation(Tile.wood.id, 1, 0.0f);
                }
                for (int k = 0; k < 2; ++k) {
                    this.spawnAtLocation(Item.stick.id, 1, 0.0f);
                }
            }
        }
        else {
            this.xd *= 0.9900000095367432;
            this.yd *= 0.949999988079071;
            this.zd *= 0.9900000095367432;
        }
        this.xRot = 0.0f;
        double n8 = this.yRot;
        final double x2 = this.xo - this.x;
        final double y2 = this.zo - this.z;
        if (x2 * x2 + y2 * y2 > 0.001) {
            n8 = (float)(Math.atan2(y2, x2) * 180.0 / 3.141592653589793);
        }
        double n9;
        for (n9 = n8 - this.yRot; n9 >= 180.0; n9 -= 360.0) {}
        while (n9 < -180.0) {
            n9 += 360.0;
        }
        if (n9 > 20.0) {
            n9 = 20.0;
        }
        if (n9 < -20.0) {
            n9 = -20.0;
        }
        this.setRot(this.yRot += (float)n9, this.xRot);
        final List<Entity> entities = this.level.getEntities(this, this.bb.grow(0.20000000298023224, 0.0, 0.20000000298023224));
        if (entities != null && entities.size() > 0) {
            for (int l = 0; l < entities.size(); ++l) {
                final Entity entity = entities.get(l);
                if (entity != this.rider && entity.isPushable() && entity instanceof Boat) {
                    entity.push(this);
                }
            }
        }
        for (int n10 = 0; n10 < 4; ++n10) {
            final int floor = Mth.floor(this.x + (n10 % 2 - 0.5) * 0.8);
            final int floor2 = Mth.floor(this.y);
            final int floor3 = Mth.floor(this.z + (n10 / 2 - 0.5) * 0.8);
            if (this.level.getTile(floor, floor2, floor3) == Tile.topSnow.id) {
                this.level.setTile(floor, floor2, floor3, 0);
            }
        }
        if (this.rider != null && this.rider.removed) {
            this.rider = null;
        }
    }
    
    @Override
    public void positionRider() {
        if (this.rider == null) {
            return;
        }
        this.rider.setPos(this.x + Math.cos(this.yRot * 3.141592653589793 / 180.0) * 0.4, this.y + this.getRideHeight() + this.rider.getRidingHeight(), this.z + Math.sin(this.yRot * 3.141592653589793 / 180.0) * 0.4);
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
        if (this.rider != null && this.rider instanceof Player && this.rider != player) {
            return true;
        }
        if (!this.level.isClientSide) {
            player.ride(this);
        }
        return true;
    }
}
