// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity;

import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.entity.item.ItemEntity;
import com.mojang.nbt.ListTag;
import com.mojang.nbt.FloatTag;
import com.mojang.nbt.DoubleTag;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.tile.LiquidTile;
import net.minecraft.world.level.material.Material;

import java.util.List;
import net.minecraft.world.level.tile.Tile;
import util.Mth;
import java.util.Random;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;

public abstract class Entity
{
    public static final short TOTAL_AIR_SUPPLY = 20 * 15;
    private static int entityCounter;
    public int entityId;
    public double viewScale;
    public boolean blocksBuilding;
    public Entity rider;
    public Entity riding;
    public Level level;
    public double xo;
    public double yo;
    public double zo;
    public double x;
    public double y;
    public double z;
    public double xd;
    public double yd;
    public double zd;
    public float yRot;
    public float xRot;
    public float yRotO;
    public float xRotO;
    public final AABB bb;
    public boolean onGround;
    public boolean horizontalCollision;
    public boolean verticalCollision;
    public boolean collision;
    public boolean hurtMarked;
    public boolean isStuckInWeb;
    public boolean slide;
    public boolean removed;
    public float heightOffset;
    public float bbWidth;
    public float bbHeight;
    public float walkDistO;
    public float walkDist;
    protected float fallDistance;
    private int nextStep;
    public double xOld;
    public double yOld;
    public double zOld;
    public float ySlideOffset;
    public float footSize;
    public boolean noPhysics;
    public float pushthrough;
    protected Random random;
    public int tickCount;
    public int flameTime;
    public int onFire;
    protected int airCapacity;
    protected boolean wasInWater;
    public int invulnerableTime;
    public int airSupply;
    private boolean firstTick;
    public String customTextureUrl;
    public String customTextureUrl2;
    protected boolean fireImmune;
    protected SynchedEntityData entityData;
    public float emission;
    private double xRideRotA;
    private double yRideRotA;
    public boolean inChunk;
    public int xChunk;
    public int yChunk;
    public int zChunk;
    public int xp;
    public int yp;
    public int zp;
    public boolean noCulling;
    
    public Entity(final Level level) {
        this.entityId = Entity.entityCounter++;
        this.viewScale = 1.0;
        this.blocksBuilding = false;
        this.bb = AABB.newPermanent(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        this.onGround = false;
        this.collision = false;
        this.hurtMarked = false;
        this.slide = true;
        this.removed = false;
        this.heightOffset = 0.0f;
        this.bbWidth = 0.6f;
        this.bbHeight = 1.8f;
        this.walkDistO = 0.0f;
        this.walkDist = 0.0f;
        this.fallDistance = 0.0f;
        this.nextStep = 1;
        this.ySlideOffset = 0.0f;
        this.footSize = 0.0f;
        this.noPhysics = false;
        this.pushthrough = 0.0f;
        this.random = new Random();
        this.tickCount = 0;
        this.flameTime = 1;
        this.onFire = 0;
        this.airCapacity = 300;
        this.wasInWater = false;
        this.invulnerableTime = 0;
        this.airSupply = 300;
        this.firstTick = true;
        this.fireImmune = false;
        this.entityData = new SynchedEntityData();
        this.emission = 0.0f;
        this.inChunk = false;
        this.level = level;
        this.setPos(0.0, 0.0, 0.0);
        this.entityData.define(0, (byte)0);
        this.definedSynchedData();
    }
    
    protected abstract void definedSynchedData();
    
    public SynchedEntityData getEntityData() {
        return this.entityData;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof Entity && ((Entity)obj).entityId == this.entityId;
    }
    
    @Override
    public int hashCode() {
        return this.entityId;
    }
    
    protected void resetPos() {
        if (this.level == null) {
            return;
        }
        while (this.y > 0.0) {
            this.setPos(this.x, this.y, this.z);
            if (this.level.getCubes(this, this.bb).size() == 0) {
                break;
            }
            ++this.y;
        }
        final double xd = 0.0;
        this.zd = xd;
        this.yd = xd;
        this.xd = xd;
        this.xRot = 0.0f;
    }
    
    public void remove() {
        this.removed = true;
    }
    
    protected void setSize(final float w, final float h) {
        this.bbWidth = w;
        this.bbHeight = h;
    }
    
    protected void setRot(final float yRot, final float xRot) {
        this.yRot = yRot % 360.0f;
        this.xRot = xRot % 360.0f;
    }
    
    public void setPos(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        final float n = this.bbWidth / 2.0f;
        this.bb.set(x - n, y - this.heightOffset + this.ySlideOffset, z - n, x + n, y - this.heightOffset + this.ySlideOffset + this.bbHeight, z + n);
    }
    
    public void turn(final float xo, final float yo) {
        final float xRot = this.xRot;
        final float yRot = this.yRot;
        this.yRot += (float)(xo * 0.15);
        this.xRot -= (float)(yo * 0.15);
        if (this.xRot < -90.0f) {
            this.xRot = -90.0f;
        }
        if (this.xRot > 90.0f) {
            this.xRot = 90.0f;
        }
        this.xRotO += this.xRot - xRot;
        this.yRotO += this.yRot - yRot;
    }
    
    public void tick() {
        this.baseTick();
    }
    
    public void baseTick() {
        if (this.riding != null && this.riding.removed) {
            this.riding = null;
        }
        ++this.tickCount;
        this.walkDistO = this.walkDist;
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.xRotO = this.xRot;
        this.yRotO = this.yRot;
        if (this.updateInWaterState()) {
            if (!this.wasInWater && !this.firstTick) {
                float volume = Mth.sqrt(this.xd * this.xd * 0.20000000298023224 + this.yd * this.yd + this.zd * this.zd * 0.20000000298023224) * 0.2f;
                if (volume > 1.0f) {
                    volume = 1.0f;
                }
                this.level.playSound(this, "random.splash", volume, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
                final float n = (float)Mth.floor(this.bb.y0);
                for (int n2 = 0; n2 < 1.0f + this.bbWidth * 20.0f; ++n2) {
                    this.level.addParticle("bubble", this.x + (this.random.nextFloat() * 2.0f - 1.0f) * this.bbWidth, n + 1.0f, this.z + (this.random.nextFloat() * 2.0f - 1.0f) * this.bbWidth, this.xd, this.yd - this.random.nextFloat() * 0.2f, this.zd);
                }
                for (int n3 = 0; n3 < 1.0f + this.bbWidth * 20.0f; ++n3) {
                    this.level.addParticle("splash", this.x + (this.random.nextFloat() * 2.0f - 1.0f) * this.bbWidth, n + 1.0f, this.z + (this.random.nextFloat() * 2.0f - 1.0f) * this.bbWidth, this.xd, this.yd, this.zd);
                }
            }
            this.fallDistance = 0.0f;
            this.wasInWater = true;
            this.onFire = 0;
        }
        else {
            this.wasInWater = false;
        }
        if (this.level.isClientSide) {
            this.onFire = 0;
        }
        else if (this.onFire > 0) {
            if (this.fireImmune) {
                this.onFire -= 4;
                if (this.onFire < 0) {
                    this.onFire = 0;
                }
            }
            else {
                if (this.onFire % 20 == 0) {
                    this.hurt(null, 1);
                }
                --this.onFire;
            }
        }
        if (this.isInLava()) {
            this.lavaHurt();
        }
        if (this.y < -64.0) {
            this.outOfWorld();
        }
        if (!this.level.isClientSide) {
            this.setSharedFlag(0, this.onFire > 0);
            this.setSharedFlag(2, this.riding != null);
        }
        this.firstTick = false;
    }
    
    protected void lavaHurt() {
        if (!this.fireImmune) {
            this.hurt(null, 4);
            this.onFire = 600;
        }
    }
    
    protected void outOfWorld() {
        this.remove();
    }
    
    public boolean isFree(final double xa, final double ya, final double z) {
        final AABB cloneMove = this.bb.cloneMove(xa, ya, z);
        return this.level.getCubes(this, cloneMove).size() <= 0 && !this.level.containsAnyLiquid(cloneMove);
    }
    
    public void move(double xa, double ya, double za) {
        if (this.noPhysics) {
            this.bb.move(xa, ya, za);
            this.x = (this.bb.x0 + this.bb.x1) / 2.0;
            this.y = this.bb.y0 + this.heightOffset - this.ySlideOffset;
            this.z = (this.bb.z0 + this.bb.z1) / 2.0;
            return;
        }
        this.ySlideOffset *= 0.4f;
        final double x = this.x;
        final double z = this.z;
        if (this.isStuckInWeb) {
            this.isStuckInWeb = false;
            xa *= 0.25;
            ya *= 0.05000000074505806;
            za *= 0.25;
            this.xd = 0.0;
            this.yd = 0.0;
            this.zd = 0.0;
        }
        double n = xa;
        final double n2 = ya;
        double n3 = za;
        final AABB copy = this.bb.copy();
        final boolean b = this.onGround && this.isSneaking();
        if (b) {
            final double n4 = 0.05;
            while (xa != 0.0 && this.level.getCubes(this, this.bb.cloneMove(xa, -1.0, 0.0)).size() == 0) {
                if (xa < n4 && xa >= -n4) {
                    xa = 0.0;
                }
                else if (xa > 0.0) {
                    xa -= n4;
                }
                else {
                    xa += n4;
                }
                n = xa;
            }
            while (za != 0.0 && this.level.getCubes(this, this.bb.cloneMove(0.0, -1.0, za)).size() == 0) {
                if (za < n4 && za >= -n4) {
                    za = 0.0;
                }
                else if (za > 0.0) {
                    za -= n4;
                }
                else {
                    za += n4;
                }
                n3 = za;
            }
        }
        final List cubes = this.level.getCubes(this, this.bb.expand(xa, ya, za));
        for (int i = 0; i < cubes.size(); ++i) {
            ya = ((AABB)cubes.get(i)).clipYCollide(this.bb, ya);
        }
        this.bb.move(0.0, ya, 0.0);
        if (!this.slide && n2 != ya) {
            ya = (xa = (za = 0.0));
        }
        final boolean b2 = this.onGround || (n2 != ya && n2 < 0.0);
        for (int j = 0; j < cubes.size(); ++j) {
            xa = ((AABB)cubes.get(j)).clipXCollide(this.bb, xa);
        }
        this.bb.move(xa, 0.0, 0.0);
        if (!this.slide && n != xa) {
            ya = (xa = (za = 0.0));
        }
        for (int k = 0; k < cubes.size(); ++k) {
            za = ((AABB)cubes.get(k)).clipZCollide(this.bb, za);
        }
        this.bb.move(0.0, 0.0, za);
        if (!this.slide && n3 != za) {
            ya = (xa = (za = 0.0));
        }
        if (this.footSize > 0.0f && b2 && (b || this.ySlideOffset < 0.05f) && (n != xa || n3 != za)) {
            final double n5 = xa;
            final double n6 = ya;
            final double n7 = za;
            xa = n;
            ya = this.footSize;
            za = n3;
            final AABB copy2 = this.bb.copy();
            this.bb.set(copy);
            final List cubes2 = this.level.getCubes(this, this.bb.expand(xa, ya, za));
            for (int l = 0; l < cubes2.size(); ++l) {
                ya = ((AABB)cubes2.get(l)).clipYCollide(this.bb, ya);
            }
            this.bb.move(0.0, ya, 0.0);
            if (!this.slide && n2 != ya) {
                ya = (xa = (za = 0.0));
            }
            for (int n8 = 0; n8 < cubes2.size(); ++n8) {
                xa = ((AABB)cubes2.get(n8)).clipXCollide(this.bb, xa);
            }
            this.bb.move(xa, 0.0, 0.0);
            if (!this.slide && n != xa) {
                ya = (xa = (za = 0.0));
            }
            for (int n9 = 0; n9 < cubes2.size(); ++n9) {
                za = ((AABB)cubes2.get(n9)).clipZCollide(this.bb, za);
            }
            this.bb.move(0.0, 0.0, za);
            if (!this.slide && n3 != za) {
                ya = (xa = (za = 0.0));
            }
            if (!this.slide && n2 != ya) {
                ya = (xa = (za = 0.0));
            }
            else {
                ya = -this.footSize;
                for (int n10 = 0; n10 < cubes2.size(); ++n10) {
                    ya = ((AABB)cubes2.get(n10)).clipYCollide(this.bb, ya);
                }
                this.bb.move(0.0, ya, 0.0);
            }
            if (n5 * n5 + n7 * n7 >= xa * xa + za * za) {
                xa = n5;
                ya = n6;
                za = n7;
                this.bb.set(copy2);
            }
            else {
                final double n11 = this.bb.y0 - (int)this.bb.y0;
                if (n11 > 0.0) {
                    this.ySlideOffset += (float)(n11 + 0.01);
                }
            }
        }
        this.x = (this.bb.x0 + this.bb.x1) / 2.0;
        this.y = this.bb.y0 + this.heightOffset - this.ySlideOffset;
        this.z = (this.bb.z0 + this.bb.z1) / 2.0;
        this.horizontalCollision = (n != xa || n3 != za);
        this.verticalCollision = (n2 != ya);
        this.onGround = (n2 != ya && n2 < 0.0);
        this.collision = (this.horizontalCollision || this.verticalCollision);
        this.checkFallDamage(ya, this.onGround);
        if (n != xa) {
            this.xd = 0.0;
        }
        if (n2 != ya) {
            this.yd = 0.0;
        }
        if (n3 != za) {
            this.zd = 0.0;
        }
        final double n12 = this.x - x;
        final double n13 = this.z - z;
        if (this.makeStepSound() && !b && this.riding == null) {
            this.walkDist += (float)(Mth.sqrt(n12 * n12 + n13 * n13) * 0.6);
            final int floor = Mth.floor(this.x);
            final int floor2 = Mth.floor(this.y - 0.20000000298023224 - this.heightOffset);
            final int floor3 = Mth.floor(this.z);
            int n14 = this.level.getTile(floor, floor2, floor3);
            if (this.level.getTile(floor, floor2 - 1, floor3) == Tile.fence.id) {
                n14 = this.level.getTile(floor, floor2 - 1, floor3);
            }
            if (this.walkDist > this.nextStep && n14 > 0) {
                ++this.nextStep;
                final Tile.SoundType soundType = Tile.tiles[n14].soundType;
                if (this.level.getTile(floor, floor2 + 1, floor3) == Tile.topSnow.id) {
                    final Tile.SoundType soundType2 = Tile.topSnow.soundType;
                    this.level.playSound(this, soundType2.getStepSound(), soundType2.getVolume() * 0.15f, soundType2.getPitch());
                }
                else if (!Tile.tiles[n14].material.isLiquid()) {
                    this.level.playSound(this, soundType.getStepSound(), soundType.getVolume() * 0.15f, soundType.getPitch());
                }
                Tile.tiles[n14].stepOn(this.level, floor, floor2, floor3, this);
            }
        }
        final int floor4 = Mth.floor(this.bb.x0 + 0.001);
        final int floor5 = Mth.floor(this.bb.y0 + 0.001);
        final int floor6 = Mth.floor(this.bb.z0 + 0.001);
        final int floor7 = Mth.floor(this.bb.x1 - 0.001);
        final int floor8 = Mth.floor(this.bb.y1 - 0.001);
        final int floor9 = Mth.floor(this.bb.z1 - 0.001);
        if (this.level.hasChunksAt(floor4, floor5, floor6, floor7, floor8, floor9)) {
            for (int n15 = floor4; n15 <= floor7; ++n15) {
                for (int n16 = floor5; n16 <= floor8; ++n16) {
                    for (int n17 = floor6; n17 <= floor9; ++n17) {
                        final int tile = this.level.getTile(n15, n16, n17);
                        if (tile > 0) {
                            Tile.tiles[tile].entityInside(this.level, n15, n16, n17, this);
                        }
                    }
                }
            }
        }
        final boolean inWaterOrRain = this.isInWaterOrRain();
        if (this.level.containsFireTile(this.bb.shrink(0.001, 0.001, 0.001))) {
            this.burn(1);
            if (!inWaterOrRain) {
                ++this.onFire;
                if (this.onFire == 0) {
                    this.onFire = 300;
                }
            }
        }
        else if (this.onFire <= 0) {
            this.onFire = -this.flameTime;
        }
        if (inWaterOrRain && this.onFire > 0) {
            this.level.playSound(this, "random.fizz", 0.7f, 1.6f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
            this.onFire = -this.flameTime;
        }
    }
    
    protected boolean makeStepSound() {
        return true;
    }
    
    protected void checkFallDamage(final double ya, final boolean onGround) {
        if (onGround) {
            if (this.fallDistance > 0.0f) {
                this.causeFallDamage(this.fallDistance);
                this.fallDistance = 0.0f;
            }
        }
        else if (ya < 0.0) {
            this.fallDistance -= (float)ya;
        }
    }
    
    public AABB getCollideBox() {
        return null;
    }
    
    protected void burn(final int dmg) {
        if (!this.fireImmune) {
            this.hurt(null, dmg);
        }
    }
    
    protected void causeFallDamage(final float distance) {
        if (this.rider != null) {
            this.rider.causeFallDamage(distance);
        }
    }
    
    public boolean isInWaterOrRain() {
        return this.wasInWater || this.level.isRainingAt(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z));
    }
    
    public boolean isInWater() {
        return this.wasInWater;
    }
    
    public boolean updateInWaterState() {
        return this.level.checkAndHandleWater(this.bb.grow(0.0, -0.4000000059604645, 0.0).shrink(0.001, 0.001, 0.001), Material.water, this);
    }
    
    public boolean isUnderLiquid(final Material material) {
        final double v = this.y + this.getHeadHeight();
        final int floor = Mth.floor(this.x);
        final int floor2 = Mth.floor((float)Mth.floor(v));
        final int floor3 = Mth.floor(this.z);
        final int tile = this.level.getTile(floor, floor2, floor3);
        return tile != 0 && Tile.tiles[tile].material == material && v < floor2 + 1 - (LiquidTile.getHeight(this.level.getData(floor, floor2, floor3)) - 0.11111111f);
    }
    
    public float getHeadHeight() {
        return 0.0f;
    }
    
    public boolean isInLava() {
        return this.level.containsMaterial(this.bb.grow(-0.10000000149011612, -0.4000000059604645, -0.10000000149011612), Material.lava);
    }
    
    public void moveRelative(float xa, float za, final float speed) {
        float sqrt = Mth.sqrt(xa * xa + za * za);
        if (sqrt < 0.01f) {
            return;
        }
        if (sqrt < 1.0f) {
            sqrt = 1.0f;
        }
        final float n = speed / sqrt;
        xa *= n;
        za *= n;
        final float sin = Mth.sin(this.yRot * Mth.PI / 180.0f);
        final float cos = Mth.cos(this.yRot * Mth.PI / 180.0f);
        this.xd += xa * cos - za * sin;
        this.zd += za * cos + xa * sin;
    }
    
    public float getBrightness(final float partialTick) {
        final int floor = Mth.floor(this.x);
        final int floor2 = Mth.floor(this.y - this.heightOffset + (this.bb.y1 - this.bb.y0) * 0.66);
        final int floor3 = Mth.floor(this.z);
        if (this.level.hasChunksAt(Mth.floor(this.bb.x0), Mth.floor(this.bb.y0), Mth.floor(this.bb.z0), Mth.floor(this.bb.x1), Mth.floor(this.bb.y1), Mth.floor(this.bb.z1))) {
            float n = this.level.getBrightness(floor, floor2, floor3);
            if (n < this.emission) {
                n = this.emission;
            }
            return n;
        }
        return this.emission;
    }
    
    public void setLevel(final Level level) {
        this.level = level;
    }
    
    public void absMoveTo(final double x, final double y, final double z, final float yRot, final float xRot) {
        this.x = x;
        this.xo = x;
        this.y = y;
        this.yo = y;
        this.z = z;
        this.zo = z;
        this.yRot = yRot;
        this.yRotO = yRot;
        this.xRot = xRot;
        this.xRotO = xRot;
        this.ySlideOffset = 0.0f;
        final double n = this.yRotO - yRot;
        if (n < -180.0) {
            this.yRotO += 360.0f;
        }
        if (n >= 180.0) {
            this.yRotO -= 360.0f;
        }
        this.setPos(this.x, this.y, this.z);
        this.setRot(yRot, xRot);
    }
    
    public void moveTo(final double x, final double y, final double z, final float yRot, final float xRot) {
        this.x = x;
        this.xo = x;
        this.xOld = x;
        final double yOld = y + this.heightOffset;
        this.y = yOld;
        this.yo = yOld;
        this.yOld = yOld;
        this.z = z;
        this.zo = z;
        this.zOld = z;
        this.yRot = yRot;
        this.xRot = xRot;
        this.setPos(this.x, this.y, this.z);
    }
    
    public float distanceTo(final Entity e) {
        final float n = (float)(this.x - e.x);
        final float n2 = (float)(this.y - e.y);
        final float n3 = (float)(this.z - e.z);
        return Mth.sqrt(n * n + n2 * n2 + n3 * n3);
    }
    
    public double distanceToSqr(final double x2, final double y2, final double z2) {
        final double n = this.x - x2;
        final double n2 = this.y - y2;
        final double n3 = this.z - z2;
        return n * n + n2 * n2 + n3 * n3;
    }
    
    public double distanceTo(final double x2, final double y2, final double z2) {
        final double n = this.x - x2;
        final double n2 = this.y - y2;
        final double n3 = this.z - z2;
        return Mth.sqrt(n * n + n2 * n2 + n3 * n3);
    }
    
    public double distanceToSqr(final Entity e) {
        final double n = this.x - e.x;
        final double n2 = this.y - e.y;
        final double n3 = this.z - e.z;
        return n * n + n2 * n2 + n3 * n3;
    }
    
    public void playerTouch(final Player player) {
    }
    
    public void push(final Entity e) {
        if (e.rider == this || e.riding == this) {
            return;
        }
        final double a = e.x - this.x;
        final double b = e.z - this.z;
        final double asbMax = Mth.asbMax(a, b);
        if (asbMax >= 0.009999999776482582) {
            final double n = Mth.sqrt(asbMax);
            final double n2 = a / n;
            final double n3 = b / n;
            double n4 = 1.0 / n;
            if (n4 > 1.0) {
                n4 = 1.0;
            }
            final double n5 = n2 * n4;
            final double n6 = n3 * n4;
            final double n7 = n5 * 0.05000000074505806;
            final double n8 = n6 * 0.05000000074505806;
            final double xa = n7 * (1.0f - this.pushthrough);
            final double za = n8 * (1.0f - this.pushthrough);
            this.push(-xa, 0.0, -za);
            e.push(xa, 0.0, za);
        }
    }
    
    public void push(final double xa, final double ya, final double za) {
        this.xd += xa;
        this.yd += ya;
        this.zd += za;
    }
    
    protected void markHurt() {
        this.hurtMarked = true;
    }
    
    public boolean hurt(final Entity source, final int dmg) {
        this.markHurt();
        return false;
    }
    
    public boolean isPickable() {
        return false;
    }
    
    public boolean isPushable() {
        return false;
    }
    
    public void awardKillScore(final Entity victim, final int score) {
    }
    
    public boolean shouldRender(final Vec3 c) {
        final double n = this.x - c.x;
        final double n2 = this.y - c.y;
        final double n3 = this.z - c.z;
        return this.shouldRenderAtSqrDistance(n * n + n2 * n2 + n3 * n3);
    }
    
    public boolean shouldRenderAtSqrDistance(final double distance) {
        final double n = this.bb.getSize() * (64.0 * this.viewScale);
        return distance < n * n;
    }
    
    public String getTexture() {
        return null;
    }
    
    public boolean save(final CompoundTag compoundTag) {
        final String encodeId = this.getEncodeId();
        if (this.removed || encodeId == null) {
            return false;
        }
        compoundTag.putString("id", encodeId);
        this.saveWithoutId(compoundTag);
        return true;
    }
    
    public void saveWithoutId(final CompoundTag compoundTag) {
        compoundTag.put("Pos", this.newDoubleList(this.x, this.y + this.ySlideOffset, this.z));
        compoundTag.put("Motion", this.newDoubleList(this.xd, this.yd, this.zd));
        compoundTag.put("Rotation", this.newFloatList(this.yRot, this.xRot));
        compoundTag.putFloat("FallDistance", this.fallDistance);
        compoundTag.putShort("Fire", (short)this.onFire);
        compoundTag.putShort("Air", (short)this.airSupply);
        compoundTag.putBoolean("OnGround", this.onGround);
        this.addAdditionalSaveData(compoundTag);
    }
    
    public void load(final CompoundTag compoundTag) {
        final ListTag list = compoundTag.getList("Pos");
        final ListTag list2 = compoundTag.getList("Motion");
        final ListTag list3 = compoundTag.getList("Rotation");
        this.xd = ((DoubleTag)list2.get(0)).data;
        this.yd = ((DoubleTag)list2.get(1)).data;
        this.zd = ((DoubleTag)list2.get(2)).data;
        if (Math.abs(this.xd) > 10.0) {
            this.xd = 0.0;
        }
        if (Math.abs(this.yd) > 10.0) {
            this.yd = 0.0;
        }
        if (Math.abs(this.zd) > 10.0) {
            this.zd = 0.0;
        }
        final double data = ((DoubleTag)list.get(0)).data;
        this.x = data;
        this.xOld = data;
        this.xo = data;
        final double data2 = ((DoubleTag)list.get(1)).data;
        this.y = data2;
        this.yOld = data2;
        this.yo = data2;
        final double data3 = ((DoubleTag)list.get(2)).data;
        this.z = data3;
        this.zOld = data3;
        this.zo = data3;
        final float data4 = ((FloatTag)list3.get(0)).data;
        this.yRot = data4;
        this.yRotO = data4;
        final float data5 = ((FloatTag)list3.get(1)).data;
        this.xRot = data5;
        this.xRotO = data5;
        this.fallDistance = compoundTag.getFloat("FallDistance");
        this.onFire = compoundTag.getShort("Fire");
        this.airSupply = compoundTag.getShort("Air");
        this.onGround = compoundTag.getBoolean("OnGround");
        this.setPos(this.x, this.y, this.z);
        this.setRot(this.yRot, this.xRot);
        this.readAdditionalSaveData(compoundTag);
    }
    
    protected final String getEncodeId() {
        return EntityIO.getEncodeId(this);
    }
    
    protected abstract void readAdditionalSaveData(final CompoundTag compoundTag);
    
    protected abstract void addAdditionalSaveData(final CompoundTag compoundTag);
    
    protected ListTag newDoubleList(final double... doubles) {
        final ListTag listTag = new ListTag();
        for (int length = doubles.length, i = 0; i < length; ++i) {
            listTag.add(new DoubleTag(doubles[i]));
        }
        return listTag;
    }
    
    protected ListTag newFloatList(final float... floats) {
        final ListTag listTag = new ListTag();
        for (int length = floats.length, i = 0; i < length; ++i) {
            listTag.add(new FloatTag(floats[i]));
        }
        return listTag;
    }
    
    public float getShadowHeightOffs() {
        return this.bbHeight / 2.0f;
    }
    
    public ItemEntity spawnAtLocation(final int resource, final int count) {
        return this.spawnAtLocation(resource, count, 0.0f);
    }
    
    public ItemEntity spawnAtLocation(final int resource, final int count, final float yOffs) {
        return this.spawnAtLocation(new ItemInstance(resource, count, 0), yOffs);
    }
    
    public ItemEntity spawnAtLocation(final ItemInstance itemInstance, final float yOffs) {
        final ItemEntity e = new ItemEntity(this.level, this.x, this.y + yOffs, this.z, itemInstance);
        e.throwTime = 10;
        this.level.addEntity(e);
        return e;
    }
    
    public boolean isAlive() {
        return !this.removed;
    }
    
    public boolean isInWall() {
        for (int i = 0; i < 8; ++i) {
            if (this.level.isSolidBlockingTile(Mth.floor(this.x + ((i >> 0) % 2 - 0.5f) * this.bbWidth * 0.9f), Mth.floor(this.y + this.getHeadHeight() + ((i >> 1) % 2 - 0.5f) * 0.1f), Mth.floor(this.z + ((i >> 2) % 2 - 0.5f) * this.bbWidth * 0.9f))) {
                return true;
            }
        }
        return false;
    }
    
    public boolean interact(final Player player) {
        return false;
    }
    
    public AABB getCollideAgainstBox(final Entity entity) {
        return null;
    }
    
    public void rideTick() {
        if (this.riding.removed) {
            this.riding = null;
            return;
        }
        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;
        this.tick();
        if (this.riding == null) {
            return;
        }
        this.riding.positionRider();
        this.yRideRotA += this.riding.yRot - this.riding.yRotO;
        this.xRideRotA += this.riding.xRot - this.riding.xRotO;
        while (this.yRideRotA >= 180.0) {
            this.yRideRotA -= 360.0;
        }
        while (this.yRideRotA < -180.0) {
            this.yRideRotA += 360.0;
        }
        while (this.xRideRotA >= 180.0) {
            this.xRideRotA -= 360.0;
        }
        while (this.xRideRotA < -180.0) {
            this.xRideRotA += 360.0;
        }
        double n = this.yRideRotA * 0.5;
        double n2 = this.xRideRotA * 0.5;
        final float n3 = 10.0f;
        if (n > n3) {
            n = n3;
        }
        if (n < -n3) {
            n = -n3;
        }
        if (n2 > n3) {
            n2 = n3;
        }
        if (n2 < -n3) {
            n2 = -n3;
        }
        this.yRideRotA -= n;
        this.xRideRotA -= n2;
        this.yRot += (float)n;
        this.xRot += (float)n2;
    }
    
    public void positionRider() {
        this.rider.setPos(this.x, this.y + this.getRideHeight() + this.rider.getRidingHeight(), this.z);
    }
    
    public double getRidingHeight() {
        return this.heightOffset;
    }
    
    public double getRideHeight() {
        return this.bbHeight * 0.75;
    }
    
    public void ride(final Entity e) {
        this.xRideRotA = 0.0;
        this.yRideRotA = 0.0;
        if (e == null) {
            if (this.riding != null) {
                this.moveTo(this.riding.x, this.riding.bb.y0 + this.riding.bbHeight, this.riding.z, this.yRot, this.xRot);
                this.riding.rider = null;
            }
            this.riding = null;
            return;
        }
        if (this.riding == e) {
            this.riding.rider = null;
            this.riding = null;
            this.moveTo(e.x, e.bb.y0 + e.bbHeight, e.z, this.yRot, this.xRot);
            return;
        }
        if (this.riding != null) {
            this.riding.rider = null;
        }
        if (e.rider != null) {
            e.rider.riding = null;
        }
        this.riding = e;
        e.rider = this;
    }
    
    public void lerpTo(final double x, double y, final double z, final float yRot, final float xRot, final int steps) {
        this.setPos(x, y, z);
        this.setRot(yRot, xRot);
        final List<AABB> cubes = this.level.getCubes(this, this.bb.shrink(0.03125, 0.0, 0.03125));
        if (cubes.size() > 0) {
            double y2 = 0.0;
            for (int i = 0; i < cubes.size(); ++i) {
                final AABB aabb = cubes.get(i);
                if (aabb.y1 > y2) {
                    y2 = aabb.y1;
                }
            }
            y += y2 - this.bb.y0;
            this.setPos(x, y, z);
        }
    }
    
    public float getPickRadius() {
        return 0.1f;
    }
    
    public Vec3 getLookAngle() {
        return null;
    }
    
    public void handleInsidePortal() {
    }

    public ItemInstance[] getEquipmentSlots() {
        return null;
    }
    
    public void lerpMotion(final double xd, final double yd, final double zd) {
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
    }
    
    public void handleEntityEvent(final byte id) {
    }
    
    public void animateHurt() {
    }
    
    public void prepareCustomTextures() {
    }
    
    public void setEquippedSlot(final int slot, final int itemId, final int auxValue) {
    }
    
    public boolean isOnFire() {
        return this.onFire > 0 || this.getSharedFlag(0);
    }
    
    public boolean isRiding() {
        return this.riding != null || this.getSharedFlag(2);
    }
    
    public boolean isSneaking() {
        return this.getSharedFlag(1);
    }

    public void setSneaking(final boolean value) {
        this.setSharedFlag(1, value);
    }
    
    protected boolean getSharedFlag(final int flag) {
        return (this.entityData.getByte(0) & 1 << flag) != 0x0;
    }
    
    protected void setSharedFlag(final int flag, final boolean value) {
        final byte byte1 = this.entityData.getByte(0);
        if (value) {
            this.entityData.set(0, (byte)(byte1 | 1 << flag));
        }
        else {
            this.entityData.set(0, (byte)(byte1 & ~(1 << flag)));
        }
    }
    
    public void thunderHit(final LightningBolt lightningBolt) {
        this.burn(5);
        ++this.onFire;
        if (this.onFire == 0) {
            this.onFire = 300;
        }
    }
    
    public void killed(final Mob mob) {
    }
    
    protected boolean checkInTile(final double x, final double y, final double z) {
        final int floor = Mth.floor(x);
        final int floor2 = Mth.floor(y);
        final int floor3 = Mth.floor(z);
        final double n = x - floor;
        final double n2 = y - floor2;
        final double n3 = z - floor3;
        if (this.level.isSolidBlockingTile(floor, floor2, floor3)) {
            final boolean b = !this.level.isSolidBlockingTile(floor - 1, floor2, floor3);
            final boolean b2 = !this.level.isSolidBlockingTile(floor + 1, floor2, floor3);
            final boolean b3 = !this.level.isSolidBlockingTile(floor, floor2 - 1, floor3);
            final boolean b4 = !this.level.isSolidBlockingTile(floor, floor2 + 1, floor3);
            final boolean b5 = !this.level.isSolidBlockingTile(floor, floor2, floor3 - 1);
            final boolean b6 = !this.level.isSolidBlockingTile(floor, floor2, floor3 + 1);
            int n4 = -1;
            double n5 = 9999.0;
            if (b && n < n5) {
                n5 = n;
                n4 = 0;
            }
            if (b2 && 1.0 - n < n5) {
                n5 = 1.0 - n;
                n4 = 1;
            }
            if (b3 && n2 < n5) {
                n5 = n2;
                n4 = 2;
            }
            if (b4 && 1.0 - n2 < n5) {
                n5 = 1.0 - n2;
                n4 = 3;
            }
            if (b5 && n3 < n5) {
                n5 = n3;
                n4 = 4;
            }
            if (b6 && 1.0 - n3 < n5) {
                n4 = 5;
            }
            final float n6 = this.random.nextFloat() * 0.2f + 0.1f;
            if (n4 == 0) {
                this.xd = -n6;
            }
            if (n4 == 1) {
                this.xd = n6;
            }
            if (n4 == 2) {
                this.yd = -n6;
            }
            if (n4 == 3) {
                this.yd = n6;
            }
            if (n4 == 4) {
                this.zd = -n6;
            }
            if (n4 == 5) {
                this.zd = n6;
            }
        }
        return false;
    }
    
    static {
        Entity.entityCounter = 0;
    }
}
