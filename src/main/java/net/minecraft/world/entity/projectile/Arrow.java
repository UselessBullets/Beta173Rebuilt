// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.projectile;

import net.minecraft.SharedConstants;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.Item;
import com.mojang.nbt.CompoundTag;

import java.util.List;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.tile.Tile;
import util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;

public class Arrow extends Entity {
    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;
    private int lastTile = 0;
    private int lastData = 0;
    private boolean inGround = false;
    public boolean pickup = false;
    public int shakeTime = 0;
    public Mob owner;
    private int life;
    private int flightTime = 0;

    public Arrow(final Level level) {
        super(level);
        this.setSize(0.5f, 0.5f);
    }

    public Arrow(final Level level, final double x, final double y, final double z) {
        super(level);
        this.setSize(0.5f, 0.5f);
        this.setPos(x, y, z);
        this.heightOffset = 0.0f;
    }

    public Arrow(final Level level, final Mob mob) {
        super(level);
        this.owner = mob;
        this.pickup = (mob instanceof Player);

        this.setSize(0.5f, 0.5f);
        this.moveTo(mob.x, mob.y + mob.getHeadHeight(), mob.z, mob.yRot, mob.xRot);
        this.x -= Mth.cos(this.yRot / 180.0f * Mth.PI) * 0.16f;
        this.y -= 0.1f;
        this.z -= Mth.sin(this.yRot / 180.0f * Mth.PI) * 0.16f;
        this.setPos(this.x, this.y, this.z);
        this.heightOffset = 0.0f;

        this.xd = -Mth.sin(this.yRot / 180.0f * Mth.PI) * Mth.cos(this.xRot / 180.0f * Mth.PI);
        this.zd = Mth.cos(this.yRot / 180.0f * Mth.PI) * Mth.cos(this.xRot / 180.0f * Mth.PI);
        this.yd = -Mth.sin(this.xRot / 180.0f * Mth.PI);
        this.shoot(this.xd, this.yd, this.zd, 1.5f, 1.0f);
    }

    @Override
    protected void definedSynchedData() {
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

        this.yRotO = this.yRot = (float) (Math.atan2(xd, zd) * 180.0 / Math.PI);
        this.xRotO = this.xRot = (float) (Math.atan2(yd, sd) * 180.0 / Math.PI);
        this.life = 0;
    }

    @Override
    public void lerpMotion(final double xd, final double yd, final double zd) {
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        if (this.xRotO == 0.0f && this.yRotO == 0.0f) {
            final float sd = Mth.sqrt(xd * xd + zd * zd);
            this.yRotO = this.yRot = (float) (Math.atan2(xd, zd) * 180.0 / Math.PI);
            this.xRotO = this.xRot = (float) (Math.atan2(yd, sd) * 180.0 / Math.PI);
            this.xRotO = this.xRot;
            this.yRotO = this.yRot;
            this.moveTo(this.x, this.y, this.z, this.yRot, this.xRot);
            this.life = 0;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.xRotO == 0.0f && this.yRotO == 0.0f) {
            final float sd = Mth.sqrt(this.xd * this.xd + this.zd * this.zd);
            this.yRotO = this.yRot = (float) (Math.atan2(this.xd, this.zd) * 180.0 / Math.PI);
            this.xRotO = this.xRot = (float) (Math.atan2(this.yd, sd) * 180.0 / Math.PI);
        }

        {
            final int t = this.level.getTile(this.xTile, this.yTile, this.zTile);
            if (t > 0) {
                Tile.tiles[t].updateShape(this.level, this.xTile, this.yTile, this.zTile);
                final AABB aabb = Tile.tiles[t].getAABB(this.level, this.xTile, this.yTile, this.zTile);
                if (aabb != null && aabb.contains(Vec3.newTemp(this.x, this.y, this.z))) {
                    this.inGround = true;
                }
            }
        }

        if (this.shakeTime > 0) --this.shakeTime;

        if (this.inGround) {
            final int tile = this.level.getTile(this.xTile, this.yTile, this.zTile);
            final int data = this.level.getData(this.xTile, this.yTile, this.zTile);
            if (tile != this.lastTile || data != this.lastData) {
                this.inGround = false;

                this.xd *= this.random.nextFloat() * 0.2f;
                this.yd *= this.random.nextFloat() * 0.2f;
                this.zd *= this.random.nextFloat() * 0.2f;
                this.life = 0;
                this.flightTime = 0;
                return;
            } else {
                this.life++;
                if (this.life == SharedConstants.TICKS_PER_SECOND * 60) this.remove();
                return;
            }
        } else {
            ++this.flightTime;
        }

        Vec3 from = Vec3.newTemp(this.x, this.y, this.z);
        Vec3 to = Vec3.newTemp(this.x + this.xd, this.y + this.yd, this.z + this.zd);
        HitResult res = this.level.clip(from, to, false, true);

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
            HitResult p = bb.clip(from, to);
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
                if (res.entity.hurt(this.owner, 4)) {
                    this.level.playSound(this, "random.drr", 1.0f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
                    this.remove();
                } else {
                    this.xd *= -0.1f;
                    this.yd *= -0.1f;
                    this.zd *= -0.1f;
                    this.yRot += 180.0f;
                    this.yRotO += 180.0f;
                    this.flightTime = 0;
                }
            } else {
                this.xTile = res.x;
                this.yTile = res.y;
                this.zTile = res.z;
                this.lastTile = this.level.getTile(this.xTile, this.yTile, this.zTile);
                this.lastData = this.level.getData(this.xTile, this.yTile, this.zTile);
                this.xd = (float) (res.pos.x - this.x);
                this.yd = (float) (res.pos.y - this.y);
                this.zd = (float) (res.pos.z - this.z);

                final float dd = Mth.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
                this.x -= this.xd / dd * 0.05f;
                this.y -= this.yd / dd * 0.05f;
                this.z -= this.zd / dd * 0.05f;

                this.level.playSound(this, "random.drr", 1.0f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
                this.inGround = true;
                this.shakeTime = 7;
            }
        }

        this.x += this.xd;
        this.y += this.yd;
        this.z += this.zd;

        final float sd = Mth.sqrt(this.xd * this.xd + this.zd * this.zd);
        this.yRot = (float) (Math.atan2(this.xd, this.zd) * 180.0 / Math.PI);
        this.xRot = (float) (Math.atan2(this.yd, sd) * 180.0 / Math.PI);

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
        compoundTag.putShort("xTile", (short) this.xTile);
        compoundTag.putShort("yTile", (short) this.yTile);
        compoundTag.putShort("zTile", (short) this.zTile);
        compoundTag.putByte("inTile", (byte) this.lastTile);
        compoundTag.putByte("inData", (byte) this.lastData);
        compoundTag.putByte("shake", (byte) this.shakeTime);
        compoundTag.putByte("inGround", (byte) (this.inGround ? 1 : 0));
        compoundTag.putBoolean("player", this.pickup);
    }

    public void readAdditionalSaveData(final CompoundTag compoundTag) {
        this.xTile = compoundTag.getShort("xTile");
        this.yTile = compoundTag.getShort("yTile");
        this.zTile = compoundTag.getShort("zTile");
        this.lastTile = (compoundTag.getByte("inTile") & 0xFF);
        this.lastData = (compoundTag.getByte("inData") & 0xFF);
        this.shakeTime = (compoundTag.getByte("shake") & 0xFF);
        this.inGround = (compoundTag.getByte("inGround") == 1);
        this.pickup = compoundTag.getBoolean("player");
    }

    @Override
    public void playerTouch(final Player player) {
        if (this.level.isClientSide || !this.inGround || !this.pickup || this.shakeTime > 0) return;

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
