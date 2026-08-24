// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity;

import net.minecraft.SharedConstants;
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

import java.util.Arrays;
import java.util.List;
import net.minecraft.world.level.tile.Tile;
import util.Mth;
import java.util.Random;
import java.util.stream.IntStream;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;

public abstract class Entity
{
    public static final short TOTAL_AIR_SUPPLY = 20 * 15;
    // shared flags that are sent to clients (max 8)
    private static final int DATA_SHARED_FLAGS_ID = 0;
    private static final int FLAG_ONFIRE = 0;
    private static final int FLAG_SNEAKING = 1;
    private static final int FLAG_RIDING = 2;
    private static int entityCounter = 0;
    public int entityId = Entity.entityCounter++;
    public double viewScale = 1.0;
    public boolean blocksBuilding = false;
    public Entity rider;
    public Entity riding;
    public Level level;
    public double xo, yo, zo;
    public double x, y, z;
    public double xd, yd, zd;
    public float yRot, xRot;
    public float yRotO, xRotO;
    public final AABB bb = AABB.newPermanent(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    public boolean onGround = false;
    public boolean horizontalCollision, verticalCollision;
    public boolean collision = false;
    public boolean hurtMarked = false;
    public boolean isStuckInWeb;
    public boolean slide = true;
    public boolean removed = false;
    public float heightOffset = 0.0f;
    public float bbWidth = 0.6f;
    public float bbHeight = 1.8f;
    public float walkDistO = 0.0f;
    public float walkDist = 0.0f;
    protected float fallDistance = 0.0f;
    private int nextStep = 1;
    public double xOld, yOld, zOld;
    public float ySlideOffset = 0.0f;
    public float footSize = 0.0f;
    public boolean noPhysics = false;
    public float pushthrough = 0.0f;
    protected Random random = new Random();
    public int tickCount = 0;
    public int flameTime = 1;
    public int onFire = 0;
    protected int airCapacity = 300;
    protected boolean wasInWater = false;
    public int invulnerableTime = 0;
    public int airSupply = 300;
    private boolean firstTick = true;
    public String customTextureUrl;
    public String customTextureUrl2;
    protected boolean fireImmune = false;
    // values that need to be sent to clients in SMP
    protected SynchedEntityData entityData = new SynchedEntityData();
    public float emission = 0.0f;
    private double xRideRotA, yRideRotA;
    public boolean inChunk = false;
    public int xChunk, yChunk, zChunk;
    public int xp, yp, zp;
    public boolean noCulling;
    
    public Entity(final Level level) {
        this.level = level;
        this.setPos(0.0, 0.0, 0.0);
        this.entityData.define(DATA_SHARED_FLAGS_ID, (byte)0);
        this.definedSynchedData();
    }
    
    protected abstract void definedSynchedData();
    
    public SynchedEntityData getEntityData() {
        return this.entityData;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Entity) return ((Entity) obj).entityId == this.entityId;
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.entityId;
    }
    
    protected void resetPos() {
        if (this.level == null) return;

        while (this.y > 0.0) {
            this.setPos(this.x, this.y, this.z);
            if (this.level.getCubes(this, this.bb).isEmpty()) break;
            this.y += 1;
        }
        this.xd = this.yd = this.zd = 0.0;
        this.xRot = 0.0f;
    }
    
    public void remove() {
        this.removed = true;
    }
    
    protected void setSize(final float w, final float h) {
        this.bbWidth = w;
        this.bbHeight = h;
    }

    // Useless - Existed in b1.2 and LCE leaks
    protected void setPos(EntityPos pos) {
        if (pos.move) this.setPos(pos.x, pos.y, pos.z);
        else this.setPos(this.x, this.y, this.z);

        if (pos.rot) this.setRot(pos.yRot, pos.xRot);
        else this.setRot(this.yRot, this.xRot);
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
        final float xRotOld = this.xRot;
        final float yRotOld = this.yRot;

        this.yRot += (float)(xo * 0.15);
        this.xRot -= (float)(yo * 0.15);
        if (this.xRot < -90.0f) this.xRot = -90.0f;
        if (this.xRot > 90.0f) this.xRot = 90.0f;

        this.xRotO += this.xRot - xRotOld;
        this.yRotO += this.yRot - yRotOld;
    }

    // Useless - Existed in b1.2 and LCE leaks
    public void interpolateTurn(float xo, float yo) {
        this.yRot = (float)(this.yRot + xo * 0.15);
        this.xRot = (float)(this.xRot - yo * 0.15);
        if (this.xRot < -90.0F) this.xRot = -90.0F;
        if (this.xRot > 90.0F) this.xRot = 90.0F;
    }

    public void tick() {
        this.baseTick();
    }
    
    public void baseTick() {
        if (this.riding != null && this.riding.removed) this.riding = null;

        this.tickCount++;
        this.walkDistO = this.walkDist;
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.xRotO = this.xRot;
        this.yRotO = this.yRot;

        if (this.updateInWaterState()) {
            if (!this.wasInWater && !this.firstTick) {
                float speed = Mth.sqrt(this.xd * this.xd * 0.2f + this.yd * this.yd + this.zd * this.zd * 0.2f) * 0.2f;
                if (speed > 1.0f) speed = 1.0f;

                this.level.playSound(this, "random.splash", speed, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);

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
        else {
            if (this.onFire > 0) {
                if (this.fireImmune) {
                    this.onFire -= 4;
                    if (this.onFire < 0) this.onFire = 0;
                }
                else {
                    if (this.onFire % 20 == 0) {
                        this.hurt(null, 1);
                    }
                    this.onFire--;
                }
            }
        }

        if (this.isInLava()) {
            this.lavaHurt();
        }

        if (this.y < -64.0) {
            this.outOfWorld();
        }

        if (!this.level.isClientSide) {
            this.setSharedFlag(FLAG_ONFIRE, this.onFire > 0);
            this.setSharedFlag(FLAG_RIDING, this.riding != null);
        }

        this.firstTick = false;
    }
    
    protected void lavaHurt() {
        if (this.fireImmune) return;

        this.hurt(null, 4);
        this.onFire = SharedConstants.TICKS_PER_SECOND * 30;
    }
    
    protected void outOfWorld() {
        this.remove();
    }
    
    public boolean isFree(final double xa, final double ya, final double z) {
        final AABB box = this.bb.cloneMove(xa, ya, z);
        List<AABB> aabbs = this.level.getCubes(this, box);
        if (!aabbs.isEmpty()) return false;
        if (this.level.containsAnyLiquid(box)) return false;
        return true;
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

        final double xo = this.x;
        final double zo = this.z;

        if (this.isStuckInWeb) {
            this.isStuckInWeb = false;

            xa *= 0.25;
            ya *= 0.05f;
            za *= 0.25;
            this.xd = 0.0;
            this.yd = 0.0;
            this.zd = 0.0;
        }

        double xaOrg = xa;
        double yaOrg = ya;
        double zaOrg = za;

        final AABB bbOrg = this.bb.copy();

        final boolean isPlayerSneaking = this.onGround && this.isSneaking();

        if (isPlayerSneaking) {
            final double d = 0.05;
            while (xa != 0.0 && this.level.getCubes(this, this.bb.cloneMove(xa, -1.0, 0.0)).size() == 0) {
                if (xa < d && xa >= -d) xa = 0.0;
                else if (xa > 0.0) xa -= d;
                else xa += d;
                xaOrg = xa;
            }
            while (za != 0.0 && this.level.getCubes(this, this.bb.cloneMove(0.0, -1.0, za)).size() == 0) {
                if (za < d && za >= -d) za = 0.0;
                else if (za > 0.0) za -= d;
                else za += d;
                zaOrg = za;
            }
        }

        List<AABB> aabbs = this.level.getCubes(this, this.bb.expand(xa, ya, za));
        for (int i = 0; i < aabbs.size(); ++i) ya = aabbs.get(i).clipYCollide(this.bb, ya);
        this.bb.move(0.0, ya, 0.0);
        if (!this.slide && yaOrg != ya) {
            xa = ya = za = 0.0;
        }

        final boolean og = this.onGround || (yaOrg != ya && yaOrg < 0.0);

        for (int i = 0; i < aabbs.size(); ++i) xa = aabbs.get(i).clipXCollide(this.bb, xa);
        this.bb.move(xa, 0.0, 0.0);
        if (!this.slide && xaOrg != xa) {
            xa = ya = za = 0.0;
        }

        for (int i = 0; i < aabbs.size(); ++i) za = aabbs.get(i).clipZCollide(this.bb, za);
        this.bb.move(0.0, 0.0, za);
        if (!this.slide && zaOrg != za) {
            xa = ya = za = 0.0;
        }

        if (this.footSize > 0.0f && og && (isPlayerSneaking || this.ySlideOffset < 0.05f) && (xaOrg != xa || zaOrg != za)) {
            final double xaN = xa;
            final double yaN = ya;
            final double zaN = za;

            xa = xaOrg;
            ya = this.footSize;
            za = zaOrg;

            final AABB normal = this.bb.copy();
            this.bb.set(bbOrg);
            aabbs = this.level.getCubes(this, this.bb.expand(xa, ya, za));

            for (int i = 0; i < aabbs.size(); ++i) ya = aabbs.get(i).clipYCollide(this.bb, ya);
            this.bb.move(0.0, ya, 0.0);
            if (!this.slide && yaOrg != ya) {
                xa = ya = za = 0.0;
            }

            for (int i = 0; i < aabbs.size(); ++i) xa = aabbs.get(i).clipXCollide(this.bb, xa);
            this.bb.move(xa, 0.0, 0.0);
            if (!this.slide && xaOrg != xa) {
                xa = ya = za = 0.0;
            }

            for (int i = 0; i < aabbs.size(); ++i) za = aabbs.get(i).clipZCollide(this.bb, za);
            this.bb.move(0.0, 0.0, za);
            if (!this.slide && zaOrg != za) {
                xa = ya = za = 0.0;
            }

            if (!this.slide && yaOrg != ya) {
                xa = ya = za = 0.0;
            }
            else {
                ya = -this.footSize;
                for (int i = 0; i < aabbs.size(); ++i) ya = aabbs.get(i).clipYCollide(this.bb, ya);
                this.bb.move(0.0, ya, 0.0);
            }

            if (xaN * xaN + zaN * zaN >= xa * xa + za * za) {
                xa = xaN;
                ya = yaN;
                za = zaN;
                this.bb.set(normal);
            }
            else {
                final double ss = this.bb.y0 - (int)this.bb.y0;
                if (ss > 0.0) {
                    this.ySlideOffset += (float)(ss + 0.01);
                }
            }
        }

        this.x = (this.bb.x0 + this.bb.x1) / 2.0;
        this.y = this.bb.y0 + this.heightOffset - this.ySlideOffset;
        this.z = (this.bb.z0 + this.bb.z1) / 2.0;

        this.horizontalCollision = (xaOrg != xa || zaOrg != za);
        this.verticalCollision = (yaOrg != ya);
        this.onGround = (yaOrg != ya && yaOrg < 0.0);
        this.collision = (this.horizontalCollision || this.verticalCollision);
        this.checkFallDamage(ya, this.onGround);

        if (xaOrg != xa) this.xd = 0.0;
        if (yaOrg != ya) this.yd = 0.0;
        if (zaOrg != za) this.zd = 0.0;

        final double xm = this.x - xo;
        final double zm = this.z - zo;

        if (this.makeStepSound() && !isPlayerSneaking && this.riding == null) {
            this.walkDist += (float)(Mth.sqrt(xm * xm + zm * zm) * 0.6);
            final int xt = Mth.floor(this.x);
            final int yt = Mth.floor(this.y - 0.2f - this.heightOffset);
            final int zt = Mth.floor(this.z);
            int t = this.level.getTile(xt, yt, zt);
            if (this.level.getTile(xt, yt - 1, zt) == Tile.fence.id) {
                t = this.level.getTile(xt, yt - 1, zt);
            }

            if (this.walkDist > this.nextStep && t > 0) {
                this.nextStep++;

                Tile.SoundType soundType = Tile.tiles[t].soundType;
                if (this.level.getTile(xt, yt + 1, zt) == Tile.topSnow.id) {
                    soundType = Tile.topSnow.soundType;
                    this.level.playSound(this, soundType.getStepSound(), soundType.getVolume() * 0.15f, soundType.getPitch());
                }
                else if (!Tile.tiles[t].material.isLiquid()) {
                    this.level.playSound(this, soundType.getStepSound(), soundType.getVolume() * 0.15f, soundType.getPitch());
                }

                Tile.tiles[t].stepOn(this.level, xt, yt, zt, this);
            }
        }

        final int x0 = Mth.floor(this.bb.x0 + 0.001);
        final int y0 = Mth.floor(this.bb.y0 + 0.001);
        final int z0 = Mth.floor(this.bb.z0 + 0.001);
        final int x1 = Mth.floor(this.bb.x1 - 0.001);
        final int y1 = Mth.floor(this.bb.y1 - 0.001);
        final int z1 = Mth.floor(this.bb.z1 - 0.001);

        if (this.level.hasChunksAt(x0, y0, z0, x1, y1, z1)) {
            for (int x = x0; x <= x1; ++x) {
                for (int y = y0; y <= y1; ++y) {
                    for (int z = z0; z <= z1; ++z) {
                        final int t = this.level.getTile(x, y, z);
                        if (t > 0) {
                            Tile.tiles[t].entityInside(this.level, x, y, z, this);
                        }
                    }
                }
            }
        }

        final boolean water = this.isInWaterOrRain();
        if (this.level.containsFireTile(this.bb.shrink(0.001, 0.001, 0.001))) {
            this.burn(1);
            if (!water) {
                this.onFire++;
                if (this.onFire == 0) this.onFire = SharedConstants.TICKS_PER_SECOND * 15;
            }
        }
        else {
            if (this.onFire <= 0) {
                this.onFire = -this.flameTime;
            }
        }

        if (water && this.onFire > 0) {
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
        else {
            if (ya < 0.0) this.fallDistance -= (float) ya;
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
        if (this.rider != null) this.rider.causeFallDamage(distance);
    }
    
    public boolean isInWaterOrRain() {
        return this.wasInWater || this.level.isRainingAt(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z));
    }
    
    public boolean isInWater() {
        return this.wasInWater;
    }
    
    public boolean updateInWaterState() {
        return this.level.checkAndHandleWater(this.bb.grow(0.0, -0.4f, 0.0).shrink(0.001, 0.001, 0.001), Material.water, this);
    }
    
    public boolean isUnderLiquid(final Material material) {
        final double yp = this.y + this.getHeadHeight();
        final int xt = Mth.floor(this.x);
        final int yt = Mth.floor((float)Mth.floor(yp));
        final int zt = Mth.floor(this.z);
        final int t = this.level.getTile(xt, yt, zt);
        if (t != 0 && Tile.tiles[t].material == material) {
            float hh = LiquidTile.getHeight(this.level.getData(xt, yt, zt)) - 1 / 9.0f;
            float h = yt + 1 - hh;
            return yp < h;
        }
        return false;
    }
    
    public float getHeadHeight() {
        return 0.0f;
    }
    
    public boolean isInLava() {
        return this.level.containsMaterial(this.bb.grow(-0.1f, -0.4f, -0.1f), Material.lava);
    }
    
    public void moveRelative(float xa, float za, final float speed) {
        float dist = Mth.sqrt(xa * xa + za * za);
        if (dist < 0.01f) return;
        if (dist < 1.0f) dist = 1.0f;

        dist = speed / dist;
        xa *= dist;
        za *= dist;

        final float sinVar = Mth.sin(this.yRot * Mth.DEGRAD);
        final float cosVar = Mth.cos(this.yRot * Mth.DEGRAD);

        this.xd += xa * cosVar - za * sinVar;
        this.zd += za * cosVar + xa * sinVar;
    }
    
    public float getBrightness(final float a) {
        final int xTile = Mth.floor(this.x);
        double hh = this.heightOffset + (this.bb.y1 - this.bb.y0) * 0.66;
        final int yTile = Mth.floor(this.y - hh);
        final int zTile = Mth.floor(this.z);
        if (this.level.hasChunksAt(Mth.floor(this.bb.x0), Mth.floor(this.bb.y0), Mth.floor(this.bb.z0), Mth.floor(this.bb.x1), Mth.floor(this.bb.y1), Mth.floor(this.bb.z1))) {
            float br = this.level.getBrightness(xTile, yTile, zTile);
            if (br < this.emission) br = this.emission;
            return br;
        }
        return this.emission;
    }
    
    public void setLevel(final Level level) {
        this.level = level;
    }
    
    public void absMoveTo(final double x, final double y, final double z, final float yRot, final float xRot) {
        this.xo = this.x = x;
        this.yo = this.y = y;
        this.zo = this.z = z;
        this.yRotO = this.yRot = yRot;
        this.xRotO = this.xRot = xRot;
        this.ySlideOffset = 0.0f;

        final double yRotDiff = this.yRotO - yRot;
        if (yRotDiff < -180.0) this.yRotO += 360.0f;
        if (yRotDiff >= 180.0) this.yRotO -= 360.0f;
        this.setPos(this.x, this.y, this.z);
        this.setRot(yRot, xRot);
    }
    
    public void moveTo(final double x, final double y, final double z, final float yRot, final float xRot) {
        this.xOld = this.xo = this.x = x;
        this.yOld = this.yo = this.y = y + this.heightOffset;
        this.zOld = this.zo = this.z = z;
        this.yRot = yRot;
        this.xRot = xRot;
        this.setPos(this.x, this.y, this.z);
    }
    
    public float distanceTo(final Entity e) {
        final float xd = (float)(this.x - e.x);
        final float yd = (float)(this.y - e.y);
        final float zd = (float)(this.z - e.z);
        return Mth.sqrt(xd * xd + yd * yd + zd * zd);
    }
    
    public double distanceToSqr(final double x2, final double y2, final double z2) {
        final double xd = this.x - x2;
        final double yd = this.y - y2;
        final double zd = this.z - z2;
        return xd * xd + yd * yd + zd * zd;
    }
    
    public double distanceTo(final double x2, final double y2, final double z2) {
        final double xd = this.x - x2;
        final double yd = this.y - y2;
        final double zd = this.z - z2;
        return Mth.sqrt(xd * xd + yd * yd + zd * zd);
    }
    
    public double distanceToSqr(final Entity e) {
        final double xd = this.x - e.x;
        final double yd = this.y - e.y;
        final double zd = this.z - e.z;
        return xd * xd + yd * yd + zd * zd;
    }
    
    public void playerTouch(final Player player) {
    }
    
    public void push(final Entity e) {
        if (e.rider == this || e.riding == this) return;

        double xa = e.x - this.x;
        double za = e.z - this.z;

        double dd = Mth.asbMax(xa, za);

        if (dd >= 0.01f) {
            dd = Mth.sqrt(dd);
            xa /= dd;
            za /= dd;

            double pow = 1.0 / dd;
            if (pow > 1.0) pow = 1.0;
            xa *= pow;
            za *= pow;

            xa *= 0.05f;
            za *= 0.05f;

            xa *= (1.0f - this.pushthrough);
            za *= (1.0f - this.pushthrough);

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

    // Useless - in b1.2 and LCE leaks
    public boolean isShootable() {
        return false;
    }
    
    public void awardKillScore(final Entity victim, final int score) {
    }
    
    public boolean shouldRender(final Vec3 c) {
        final double xd = this.x - c.x;
        final double yd = this.y - c.y;
        final double zd = this.z - c.z;
        double distance = xd * xd + yd * yd + zd * zd;
        return this.shouldRenderAtSqrDistance(distance);
    }
    
    public boolean shouldRenderAtSqrDistance(final double distance) {
        double size = this.bb.getSize();
        size *= (64.0 * this.viewScale);
        return distance < size * size;
    }
    
    public String getTexture() {
        return null;
    }

    public boolean isCreativeModeAllowed() { // Useless - In b1.2 and LCE leaks
        return false;
    }

    public boolean save(final CompoundTag entityTag) {
        final String id = this.getEncodeId();
        if (this.removed || id == null) {
            return false;
        }
        entityTag.putString("id", id);
        this.saveWithoutId(entityTag);
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
        final ListTag<DoubleTag> pos = (ListTag<DoubleTag>) compoundTag.getList("Pos");
        final ListTag<DoubleTag> motion = (ListTag<DoubleTag>) compoundTag.getList("Motion");
        final ListTag<FloatTag> rotation = (ListTag<FloatTag>) compoundTag.getList("Rotation");

        this.xd = motion.get(0).data;
        this.yd = motion.get(1).data;
        this.zd = motion.get(2).data;

        if (Math.abs(this.xd) > 10.0) this.xd = 0.0;
        if (Math.abs(this.yd) > 10.0) this.yd = 0.0;
        if (Math.abs(this.zd) > 10.0) this.zd = 0.0;

        this.xo = this.xOld = this.x = pos.get(0).data;
        this.yo = this.yOld = this.y = pos.get(1).data;
        this.zo = this.zOld = this.z = pos.get(2).data;

        this.yRotO = this.yRot = rotation.get(0).data;
        this.xRotO = this.xRot = rotation.get(1).data;

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
    
    protected ListTag<DoubleTag> newDoubleList(final double... doubles) {
        final ListTag<DoubleTag> res = new ListTag<>();
        for (double val : doubles) {
            res.add(new DoubleTag(val));
        }
        return res;
    }
    
    protected ListTag<FloatTag> newFloatList(final float... floats) {
        final ListTag<FloatTag> res = new ListTag<>();
        for (float val : floats) {
            res.add(new FloatTag(val));
        }
        return res;
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
            float xo = ((i >> 0) % 2 - 0.5f) * this.bbWidth * 0.9f;
            float yo = ((i >> 1) % 2 - 0.5f) * 0.1f;
            float zo = ((i >> 2) % 2 - 0.5f) * this.bbWidth * 0.9f;
            int xt = Mth.floor(this.x + xo);
            int yt = Mth.floor(this.y + this.getHeadHeight() + yo);
            int zt = Mth.floor(this.z + zo);
            if (this.level.isSolidBlockingTile(xt, yt, zt)) {
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
        this.xd = this.yd = this.zd = 0.0;
        this.tick();

        if (this.riding == null) return;

        // Sets riders old&new position to it's mount's old&new position (plus the ride y-seperatation).
        this.riding.positionRider();

        this.yRideRotA += this.riding.yRot - this.riding.yRotO;
        this.xRideRotA += this.riding.xRot - this.riding.xRotO;

        // Wrap rotation angles.
        while (this.yRideRotA >= 180.0) this.yRideRotA -= 360.0;
        while (this.yRideRotA < -180.0) this.yRideRotA += 360.0;
        while (this.xRideRotA >= 180.0) this.xRideRotA -= 360.0;
        while (this.xRideRotA < -180.0) this.xRideRotA += 360.0;

        double yra = this.yRideRotA * 0.5;
        double xra = this.xRideRotA * 0.5;

        // Cap rotation speed.
        final float max = 10.0f;
        if (yra > max) yra = max;
        if (yra < -max) yra = -max;
        if (xra > max) xra = max;
        if (xra < -max) xra = -max;

        this.yRideRotA -= yra;
        this.xRideRotA -= xra;

        this.yRot += (float)yra;
        this.xRot += (float)xra;
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

        final List<AABB> collisions = this.level.getCubes(this, this.bb.shrink(0.03125, 0.0, 0.03125));
        if (!collisions.isEmpty()) {
            double yTop = 0.0;
            for (int i = 0; i < collisions.size(); ++i) {
                final AABB ab = collisions.get(i);
                if (ab.y1 > yTop) yTop = ab.y1;
            }

            y += yTop - this.bb.y0;
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
        return this.onFire > 0 || this.getSharedFlag(FLAG_ONFIRE);
    }
    
    public boolean isRiding() {
        return this.riding != null || this.getSharedFlag(FLAG_RIDING);
    }
    
    public boolean isSneaking() {
        return this.getSharedFlag(FLAG_SNEAKING);
    }

    public void setSneaking(final boolean value) {
        this.setSharedFlag(FLAG_SNEAKING, value);
    }
    
    protected boolean getSharedFlag(final int flag) {
        return (this.entityData.getByte(DATA_SHARED_FLAGS_ID) & 1 << flag) != 0x0;
    }
    
    protected void setSharedFlag(final int flag, final boolean value) {
        final byte current = this.entityData.getByte(DATA_SHARED_FLAGS_ID);
        if (value) {
            this.entityData.set(DATA_SHARED_FLAGS_ID, (byte)(current | 1 << flag));
        }
        else {
            this.entityData.set(DATA_SHARED_FLAGS_ID, (byte)(current & ~(1 << flag)));
        }
    }
    
    public void thunderHit(final LightningBolt lightningBolt) {
        this.burn(5);
        this.onFire++;
        if (this.onFire == 0) {
            this.onFire = SharedConstants.TICKS_PER_SECOND * 15;
        }
    }
    
    public void killed(final Mob mob) {
    }
    
    protected boolean checkInTile(final double x, final double y, final double z) {
        final int xTile = Mth.floor(x);
        final int yTile = Mth.floor(y);
        final int zTile = Mth.floor(z);

        final double xd = x - xTile;
        final double yd = y - yTile;
        final double zd = z - zTile;

        if (this.level.isSolidBlockingTile(xTile, yTile, zTile)) {
            final boolean west = !this.level.isSolidBlockingTile(xTile - 1, yTile, zTile);
            final boolean east = !this.level.isSolidBlockingTile(xTile + 1, yTile, zTile);
            final boolean up = !this.level.isSolidBlockingTile(xTile, yTile - 1, zTile);
            final boolean down = !this.level.isSolidBlockingTile(xTile, yTile + 1, zTile);
            final boolean north = !this.level.isSolidBlockingTile(xTile, yTile, zTile - 1);
            final boolean south = !this.level.isSolidBlockingTile(xTile, yTile, zTile + 1);

            int dir = -1;
            double closest = 9999.0;
            if (west && xd < closest) {
                closest = xd;
                dir = 0;
            }
            if (east && 1.0 - xd < closest) {
                closest = 1.0 - xd;
                dir = 1;
            }
            if (up && yd < closest) {
                closest = yd;
                dir = 2;
            }
            if (down && 1.0 - yd < closest) {
                closest = 1.0 - yd;
                dir = 3;
            }
            if (north && zd < closest) {
                closest = zd;
                dir = 4;
            }
            if (south && 1.0 - zd < closest) {
                closest = 1 - zd;
                dir = 5;
            }

            final float speed = this.random.nextFloat() * 0.2f + 0.1f;
            if (dir == 0) this.xd = -speed;
            if (dir == 1) this.xd = +speed;

            if (dir == 2) this.yd = -speed;
            if (dir == 3) this.yd = +speed;

            if (dir == 4) this.zd = -speed;
            if (dir == 5) this.zd = +speed;
        }
        return false;
    }

}
