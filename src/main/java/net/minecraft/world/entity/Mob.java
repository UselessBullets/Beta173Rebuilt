// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity;

import net.minecraft.SharedConstants;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.entity.player.Player;
import java.util.List;
import net.minecraft.world.phys.AABB;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.level.tile.Tile;
import util.Mth;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public abstract class Mob extends Entity
{
    public int invulnerableDuration = 20;
    public float timeOffs;
    public float rotA;
    public float yBodyRot = 0.0f, yBodyRotO = 0.0f;
    protected float oRun, run;
    protected float animStep, animStepO;
    protected boolean hasHair = true;
    protected String textureName = "/mob/char.png";
    protected boolean allowAlpha = true;
    protected float rotOffs = 0.0f;
    protected String modelName = null;
    protected float bobStrength = 1.0f;
    protected int deathScore = 0;
    protected float renderOffset = 0.0f;
    public boolean interpolateOnly = false;
    public float oAttackAnim, attackAnim;
    public int health;
    public int lastHealth;
    private int ambientSoundTime;
    public int hurtTime;
    public int hurtDuration;
    public float hurtDir = 0.0f;
    public int deathTime = 0;
    public int attackTime = 0;
    public float oTilt, tilt;
    protected boolean dead = false;
    public int modelNum = -1;
    public float animSpeed = (float)(Math.random() * 0.90f + 0.1f);
    public float walkAnimSpeedO;
    public float walkAnimSpeed;
    public float walkAnimPos;
    protected int lSteps;
    protected double lx, ly, lz, lyr, lxr;
    float fallTime = 0.0f;
    protected int lastHurt = 0;
    protected int noActionTime = 0;
    protected float xxa, yya, yRotA;
    protected boolean jumping = false;
    protected float defaultLookAngle = 0.0f;
    protected float runSpeed = 0.7f;
    private Entity lookingAt;
    protected int lookTime = 0;
    
    public Mob(final Level level) {
        super(level);
        this.health = 10;

        this.blocksBuilding = true;

        this.rotA = (float)(Math.random() + 1.0) * 0.01f;
        this.setPos(this.x, this.y, this.z);
        this.timeOffs = (float)Math.random() * 12398.0f;
        this.yRot = (float)(Math.random() * Math.PI * 2.0);

        this.footSize = 0.5f;
    }
    
    @Override
    protected void definedSynchedData() {
    }
    
    public boolean canSee(final Entity target) {
        return this.level.clip(Vec3.newTemp(this.x, this.y + this.getHeadHeight(), this.z), Vec3.newTemp(target.x, target.y + target.getHeadHeight(), target.z)) == null;
    }
    
    @Override
    public String getTexture() {
        return this.textureName;
    }
    
    @Override
    public boolean isPickable() {
        return !this.removed;
    }
    
    @Override
    public boolean isPushable() {
        return !this.removed;
    }
    
    @Override
    public float getHeadHeight() {
        return this.bbHeight * 0.85f;
    }
    
    public int getAmbientSoundInterval() {
        return SharedConstants.TICKS_PER_SECOND * 4;
    }
    
    public void playAmbientSound() {
        final String ambient = this.getAmbientSound();
        if (ambient != null) {
            this.level.playSound(this, ambient, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
        }
    }
    
    @Override
    public void baseTick() {
        this.oAttackAnim = this.attackAnim;
        super.baseTick();

        if (this.random.nextInt(1000) < this.ambientSoundTime++) {
            this.ambientSoundTime = -this.getAmbientSoundInterval();
            this.playAmbientSound();
        }

        if (this.isAlive() && this.isInWall()) {
            this.hurt(null, 1);
        }

        if (this.fireImmune || this.level.isClientSide) this.onFire = 0;

        if (this.isAlive() && this.isUnderLiquid(Material.water) && !this.isWaterMob()) {
            this.airSupply--;
            if (this.airSupply == -20) {
                this.airSupply = 0;
                for (int i = 0; i < 8; ++i) {
                    float xo = this.random.nextFloat() - this.random.nextFloat();
                    float yo = this.random.nextFloat() - this.random.nextFloat();
                    float zo = this.random.nextFloat() - this.random.nextFloat();
                    this.level.addParticle("bubble", this.x + xo, this.y + yo, this.z + zo, this.xd, this.yd, this.zd);
                }
                this.hurt(null, 2);
            }

            this.onFire = 0;
        }
        else {
            this.airSupply = this.airCapacity;
        }

        this.oTilt = this.tilt;

        if (this.attackTime > 0) this.attackTime--;
        if (this.hurtTime > 0) this.hurtTime--;
        if (this.invulnerableTime > 0) this.invulnerableTime--;
        if (this.health <= 0) {
            this.deathTime++;
            if (this.deathTime > 20) {
                this.beforeRemove();
                this.remove();
                for (int i = 0; i < 20; ++i) {
                    double xa = this.random.nextGaussian() * 0.02;
                    double ya = this.random.nextGaussian() * 0.02;
                    double za = this.random.nextGaussian() * 0.02;
                    this.level.addParticle("explode", this.x + this.random.nextFloat() * this.bbWidth * 2.0f - this.bbWidth, this.y + this.random.nextFloat() * this.bbHeight, this.z + this.random.nextFloat() * this.bbWidth * 2.0f - this.bbWidth, xa, ya, za);
                }
            }
        }

        this.animStepO = this.animStep;

        this.yBodyRotO = this.yBodyRot;
        this.yRotO = this.yRot;
        this.xRotO = this.xRot;
    }
    
    public void spawnAnim() {
        for (int i = 0; i < 20; ++i) {
            final double xa = this.random.nextGaussian() * 0.02;
            final double ya = this.random.nextGaussian() * 0.02;
            final double za = this.random.nextGaussian() * 0.02;
            final double dd = 10.0;
            this.level.addParticle("explode", this.x + this.random.nextFloat() * this.bbWidth * 2.0f - this.bbWidth - xa * dd, this.y + this.random.nextFloat() * this.bbHeight - ya * dd, this.z + this.random.nextFloat() * this.bbWidth * 2.0f - this.bbWidth - za * dd, xa, ya, za);
        }
    }
    
    @Override
    public void rideTick() {
        super.rideTick();
        this.oRun = this.run;
        this.run = 0.0f;
    }
    
    @Override
    public void lerpTo(final double x, final double y, final double z, final float yRot, final float xRot, final int steps) {
        this.heightOffset = 0.0f;
        this.lx = x;
        this.ly = y;
        this.lz = z;
        this.lyr = yRot;
        this.lxr = xRot;

        this.lSteps = steps;
    }
    
    @Override
    public void tick() {
        super.tick();

        this.aiStep();

        final double xd = this.x - this.xo;
        final double zd = this.z - this.zo;

        final float sideDist = Mth.sqrt(xd * xd + zd * zd);

        float yBodyRotT = this.yBodyRot;

        float walkSpeed = 0.0f;
        this.oRun = this.run;
        float tRun = 0.0f;
        if (sideDist > 0.05f) {
            tRun = 1.0f;
            walkSpeed = sideDist * 3.0f;
            yBodyRotT = (float)Math.atan2(zd, xd) * Mth.RADDEG - 90.0f;
        }
        if (this.attackAnim > 0.0f) {
            yBodyRotT = this.yRot;
        }
        if (!this.onGround) {
            tRun = 0.0f;
        }
        this.run += (tRun - this.run) * 0.3f;

        float yBodyRotD = yBodyRotT - this.yBodyRot;
        while (yBodyRotD < -180.0f) yBodyRotD += 360.0f;
        while (yBodyRotD >= 180.0f) yBodyRotD -= 360.0f;
        this.yBodyRot += yBodyRotD * 0.3f;

        float headDiff = this.yRot - this.yBodyRot;
        while (headDiff < -180.0f) headDiff += 360.0f;
        while (headDiff >= 180.0f) headDiff -= 360.0f;

        final boolean behind = headDiff < -90.0f || headDiff >= 90.0f;
        if (headDiff < -75.0f) headDiff = -75.0f;
        if (headDiff >= 75.0f) headDiff = 75.0f;

        this.yBodyRot = this.yRot - headDiff;
        if (headDiff * headDiff > 2500.0f) {
            this.yBodyRot += headDiff * 0.2f;
        }

        if (behind) {
            walkSpeed *= -1.0f;
        }

        while (this.yRot - this.yRotO < -180.0f) this.yRotO -= 360.0f;
        while (this.yRot - this.yRotO >= 180.0f) this.yRotO += 360.0f;

        while (this.yBodyRot - this.yBodyRotO < -180.0f) this.yBodyRotO -= 360.0f;
        while (this.yBodyRot - this.yBodyRotO >= 180.0f) this.yBodyRotO += 360.0f;

        while (this.xRot - this.xRotO < -180.0f) this.xRotO -= 360.0f;
        while (this.xRot - this.xRotO >= 180.0f) this.xRotO += 360.0f;

        this.animStep += walkSpeed;
    }
    
    @Override
    protected void setSize(final float w, final float h) {
        super.setSize(w, h);
    }
    
    public void heal(final int heal) {
        if (this.health <= 0) return;
        this.health += heal;
        if (this.health > 20) this.health = 20;
        this.invulnerableTime = this.invulnerableDuration / 2;
    }
    
    @Override
    public boolean hurt(final Entity source, final int dmg) {
        if (this.level.isClientSide) return false;
        this.noActionTime = 0;
        if (this.health <= 0) return false;

        this.walkAnimSpeed = 1.5f;

        boolean sound = true;
        if (this.invulnerableTime > this.invulnerableDuration / 2.0f) {
            if (dmg <= this.lastHurt) return false;
            this.actuallyHurt(dmg - this.lastHurt);
            this.lastHurt = dmg;
            sound = false;
        }
        else {
            this.lastHurt = dmg;
            this.lastHealth = this.health;
            this.invulnerableTime = this.invulnerableDuration;
            this.actuallyHurt(dmg);
            this.hurtTime = this.hurtDuration = 10;
        }

        this.hurtDir = 0.0f;

        if (sound) {
            this.level.broadcastEntityEvent(this, EntityEvent.HURT);
            this.markHurt();
            if (source != null) {
                double xd = source.x - this.x;
                double zd = source.z - this.z;
                while (xd * xd + zd * zd < 0.0001) {
                    xd = (Math.random() - Math.random()) * 0.01;
                    zd = (Math.random() - Math.random()) * 0.01;
                }
                this.hurtDir = (float)(Math.atan2(zd, xd) * 180.0 / Math.PI) - this.yRot;
                this.knockback(source, dmg, xd, zd);
            }
            else {
                this.hurtDir = (float)((int)(Math.random() * 2.0) * 180);
            }
        }

        if (this.health <= 0) {
            if (sound) this.level.playSound(this, this.getDeathSound(), this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            this.die(source);
        }
        else {
            if (sound) this.level.playSound(this, this.getHurtSound(), this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
        }

        return true;
    }
    
    @Override
    public void animateHurt() {
        this.hurtTime = this.hurtDuration = 10;
        this.hurtDir = 0.0f;
    }
    
    protected void actuallyHurt(final int dmg) {
        this.health -= dmg;
    }
    
    protected float getSoundVolume() {
        return 1.0f;
    }
    
    protected String getAmbientSound() {
        return null;
    }
    
    protected String getHurtSound() {
        return "random.hurt";
    }
    
    protected String getDeathSound() {
        return "random.hurt";
    }
    
    public void knockback(final Entity source, final int dmg, final double xd, final double zd) {
        final float dd = Mth.sqrt(xd * xd + zd * zd);
        final float pow = 0.4f;

        this.xd /= 2.0;
        this.yd /= 2.0;
        this.zd /= 2.0;

        this.xd -= xd / dd * pow;
        this.yd += 0.4f;
        this.zd -= zd / dd * pow;

        if (this.yd > 0.4f) this.yd = 0.4f;
    }
    
    public void die(final Entity source) {
        if (this.deathScore >= 0 && source != null) source.awardKillScore(this, this.deathScore);

        if (source != null) source.killed(this);

        this.dead = true;
        if (!this.level.isClientSide) {
            this.dropDeathLoot();
        }

        this.level.broadcastEntityEvent(this, EntityEvent.DEATH);
    }
    
    protected void dropDeathLoot() {
        final int loot = this.getDeathLoot();
        if (loot > 0) {
            int count = this.random.nextInt(3);
            for (int i = 0; i < count; ++i) this.spawnAtLocation(loot, 1);
        }
    }
    
    protected int getDeathLoot() {
        return 0;
    }
    
    @Override
    protected void causeFallDamage(final float distance) {
        super.causeFallDamage(distance);
        final int dmg = (int)Math.ceil(distance - 3.0f);
        if (dmg > 0) {
            this.hurt(null, dmg);
            final int t = this.level.getTile(Mth.floor(this.x), Mth.floor(this.y - 0.2f - this.heightOffset), Mth.floor(this.z));
            if (t > 0) {
                final Tile.SoundType soundType = Tile.tiles[t].soundType;
                this.level.playSound(this, soundType.getStepSound(), soundType.getVolume() * 0.5f, soundType.getPitch() * 0.75f);
            }
        }
    }
    
    public void travel(final float xa, final float ya) {
        if (this.isInWater()) {
            final double yo = this.y;
            this.moveRelative(xa, ya, 0.02f);
            this.move(this.xd, this.yd, this.zd);

            this.xd *= 0.8f;
            this.yd *= 0.8f;
            this.zd *= 0.8f;
            this.yd -= 0.02;

            if (this.horizontalCollision && this.isFree(this.xd, this.yd + 0.6f - this.y + yo, this.zd)) {
                this.yd = 0.3f;
            }
        }
        else if (this.isInLava()) {
            final double yo = this.y;
            this.moveRelative(xa, ya, 0.02f);
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.5;
            this.yd *= 0.5;
            this.zd *= 0.5;
            this.yd -= 0.02;

            if (this.horizontalCollision && this.isFree(this.xd, this.yd + 0.6f - this.y + yo, this.zd)) {
                this.yd = 0.3f;
            }
        }
        else {
            float friction = 0.91f;
            if (this.onGround) {
                friction = 0.546f;
                final int t = this.level.getTile(Mth.floor(this.x), Mth.floor(this.bb.y0) - 1, Mth.floor(this.z));
                if (t > 0) {
                    friction = Tile.tiles[t].friction * 0.91f;
                }
            }

            final float friction2 = (0.6f * 0.6f * 0.91f * 0.91f * 0.6f * 0.91f) / (friction * friction * friction);

            this.moveRelative(xa, ya, this.onGround ? (0.1f * friction2) : 0.02f);

            friction = 0.91f;
            if (this.onGround) {
                friction = 0.6f * 0.91f;
                final int t = this.level.getTile(Mth.floor(this.x), Mth.floor(this.bb.y0) - 1, Mth.floor(this.z));
                if (t > 0) {
                    friction = Tile.tiles[t].friction * 0.91f;
                }
            }
            if (this.onLadder()) {
                final float max = 0.15f;
                if (this.xd < -max) this.xd = -max;
                if (this.xd > max) this.xd = max;
                if (this.zd < -max) this.zd = -max;
                if (this.zd > max) this.zd = max;
                this.fallDistance = 0.0f;
                if (this.yd < -0.15) this.yd = -0.15;
                if (this.isSneaking() && this.yd < 0.0) this.yd = 0.0;
            }

            this.move(this.xd, this.yd, this.zd);

            if (this.horizontalCollision && this.onLadder()) {
                this.yd = 0.2;
            }

            this.yd -= 0.08;
            this.yd *= 0.98f;
            this.xd *= friction;
            this.zd *= friction;
        }

        this.walkAnimSpeedO = this.walkAnimSpeed;
        final double xxd = this.x - this.xo;
        final double zzd = this.z - this.zo;
        float wst = Mth.sqrt(xxd * xxd + zzd * zzd) * 4.0f;
        if (wst > 1.0f) wst = 1.0f;
        this.walkAnimSpeed += (wst - this.walkAnimSpeed) * 0.4f;
        this.walkAnimPos += this.walkAnimSpeed;
    }
    
    public boolean onLadder() {
        int xt = Mth.floor(this.x);
        int yt = Mth.floor(this.bb.y0);
        int zt = Mth.floor(this.z);

        return this.level.getTile(xt, yt, zt) == Tile.ladder.id;
    }

    @Override
    public boolean isShootable() // Useless - in b1.2 and LCE leaks
    {
        return true;
    }
    
    public void addAdditionalSaveData(final CompoundTag compoundTag) {
        compoundTag.putShort("Health", (short)this.health);
        compoundTag.putShort("HurtTime", (short)this.hurtTime);
        compoundTag.putShort("DeathTime", (short)this.deathTime);
        compoundTag.putShort("AttackTime", (short)this.attackTime);
    }
    
    public void readAdditionalSaveData(final CompoundTag compoundTag) {
        this.health = compoundTag.getShort("Health");
        if (!compoundTag.contains("Health")) this.health = 10;
        this.hurtTime = compoundTag.getShort("HurtTime");
        this.deathTime = compoundTag.getShort("DeathTime");
        this.attackTime = compoundTag.getShort("AttackTime");
    }
    
    @Override
    public boolean isAlive() {
        return !this.removed && this.health > 0;
    }
    
    public boolean isWaterMob() {
        return false;
    }
    
    public void aiStep() {
        if (this.lSteps > 0) {
            double xt = this.x + (this.lx - this.x) / this.lSteps;
            double yt = this.y + (this.ly - this.y) / this.lSteps;
            double zt = this.z + (this.lz - this.z) / this.lSteps;

            double yrd = this.lyr - this.yRot;
            while (yrd < -180.0) yrd += 360.0;
            while (yrd >= 180.0) yrd -= 360.0;
            double xrd = this.lxr - this.xRot;

            this.yRot += (float)(yrd / this.lSteps);
            this.xRot += (float)(xrd / this.lSteps);

            this.lSteps--;
            this.setPos(xt, yt, zt);
            this.setRot(this.yRot, this.xRot);

            AABB shrinkbb = this.bb.shrink(1 / 32.0, 0.0, 1 / 32.0);
            final List<AABB> collisions = this.level.getCubes(this, shrinkbb);
            if (collisions.size() > 0) {
                double yTop = 0.0;
                for (int i = 0; i < collisions.size(); ++i) {
                    final AABB ab = collisions.get(i);
                    if (ab.y1 > yTop) yTop = ab.y1;
                }

                yt += (yTop - this.bb.y0);
                this.setPos(xt, yt, zt);
            }
        }

        if (this.isImmobile()) {
            this.jumping = false;
            this.xxa = 0.0f;
            this.yya = 0.0f;
            this.yRotA = 0.0f;
        }
        else if (!this.interpolateOnly) {
            this.updateAi();
        }

        final boolean inWater = this.isInWater();
        final boolean inLava = this.isInLava();
        if (this.jumping) {
            if (inWater) {
                this.yd += 0.04f;
            }
            else if (inLava) {
                this.yd += 0.04f;
            }
            else if (this.onGround) {
                this.jumpFromGround();
            }
        }

        this.xxa *= 0.98f;
        this.yya *= 0.98f;
        this.yRotA *= 0.9f;

        this.travel(this.xxa, this.yya);
        final List<Entity> entities = this.level.getEntities(this, this.bb.grow(0.2f, 0.0, 0.2f));
        if (entities != null && entities.size() > 0) {
            for (int i = 0; i < entities.size(); ++i) {
                final Entity e = entities.get(i);
                if (e.isPushable()) e.push(this);
            }
        }
    }
    
    protected boolean isImmobile() {
        return this.health <= 0;
    }
    
    protected void jumpFromGround() {
        this.yd = 0.42f;
    }
    
    protected boolean removeWhenFarAway() {
        return true;
    }
    
    protected void checkDespawn() {
        final Player player = this.level.getNearestPlayer(this, -1.0);
        if (this.removeWhenFarAway() && player != null) {
            final double xd = player.x - this.x;
            final double yd = player.y - this.y;
            final double zd = player.z - this.z;
            final double sd = xd * xd + yd * yd + zd * zd;
            if (sd > 128 * 128) {
                this.remove();
            }

            if (this.noActionTime > SharedConstants.TICKS_PER_SECOND * 30 && this.random.nextInt(800) == 0) {
                if (sd < 32 * 32) {
                    this.noActionTime = 0;
                }
                else {
                    this.remove();
                }
            }
        }
    }
    
    protected void updateAi() {
        this.noActionTime++;

        this.level.getNearestPlayer(this, -1.0);
        this.checkDespawn();

        this.xxa = 0.0f;
        this.yya = 0.0f;

        final float lookDistance = 8.0f;
        if (this.random.nextFloat() < 0.02f) {
            final Player player = this.level.getNearestPlayer(this, lookDistance);
            if (player != null) {
                this.lookingAt = player;
                this.lookTime = 10 + this.random.nextInt(20);
            }
            else {
                this.yRotA = (this.random.nextFloat() - 0.5f) * 20.0f;
            }
        }

        if (this.lookingAt != null) {
            this.lookAt(this.lookingAt, 10.0f, (float)this.getMaxHeadXRot());
            if (this.lookTime-- <= 0 || this.lookingAt.removed || this.lookingAt.distanceToSqr(this) > lookDistance * lookDistance) {
                this.lookingAt = null;
            }
        }
        else {
            if (this.random.nextFloat() < 0.05f) {
                this.yRotA = (this.random.nextFloat() - 0.5f) * 20.0f;
            }
            this.yRot += this.yRotA;
            this.xRot = this.defaultLookAngle;
        }

        final boolean inWater = this.isInWater();
        final boolean inLava = this.isInLava();
        if (inWater || inLava) this.jumping = (this.random.nextFloat() < 0.8f);
    }
    
    protected int getMaxHeadXRot() {
        return 40;
    }
    
    public void lookAt(final Entity e, final float yMax, final float xMax) {
        double xd = e.x - this.x;
        double yd;
        double zd = e.z - this.z;

        if (e instanceof Mob) {
            final Mob mob = (Mob)e;
            yd = this.y + this.getHeadHeight() - (mob.y + mob.getHeadHeight());
        }
        else {
            yd = (e.bb.y0 + e.bb.y1) / 2.0 - (this.y + this.getHeadHeight());
        }

        final double sd = Mth.sqrt(xd * xd + zd * zd);

        float yRotD = (float)(Math.atan2(zd, xd) * 180.0 / Math.PI) - 90.0f;
        float xRotD = (float)(-(Math.atan2(yd, sd) * 180.0 / Math.PI));
        this.xRot = -this.rotLerp(this.xRot, xRotD, xMax);
        this.yRot = this.rotLerp(this.yRot, yRotD, yMax);
    }
    
    public boolean isLookingAtAnEntity() {
        return this.lookingAt != null;
    }
    
    public Entity getLookingAt() {
        return this.lookingAt;
    }
    
    private float rotLerp(final float a, final float b, final float max) {
        float diff = b - a;
        while (diff < -180.0f) diff += 360.0f;
        while (diff >= 180.0f) diff -= 360.0f;

        if (diff > max) diff = max;
        if (diff < -max) diff = -max;
        return a + diff;
    }
    
    public void beforeRemove() {
    }
    
    public boolean canSpawn() {
        return this.level.isUnobstructed(this.bb) && this.level.getCubes(this, this.bb).isEmpty() && !this.level.containsAnyLiquid(this.bb);
    }
    
    @Override
    protected void outOfWorld() {
        this.hurt(null, 4);
    }
    
    public float getAttackAnim(final float a) {
        float diff = this.attackAnim - this.oAttackAnim;
        if (diff < 0.0f) diff += 1;
        return this.oAttackAnim + diff * a;
    }
    
    public Vec3 getPos(final float a) {
        if (a == 1.0f) return Vec3.newTemp(this.x, this.y, this.z);

        double x = this.xo + (this.x - this.xo) * a;
        double y = this.yo + (this.y - this.yo) * a;
        double z = this.zo + (this.z - this.zo) * a;

        return Vec3.newTemp(x, y, z);
    }
    
    @Override
    public Vec3 getLookAngle() {
        return this.getViewVector(1.0f);
    }
    
    public Vec3 getViewVector(final float a) {
        if (a == 1.0f) {
            final float yCos = Mth.cos(-this.yRot * Mth.DEGRAD - Mth.PI);
            final float ySin = Mth.sin(-this.yRot * Mth.DEGRAD - Mth.PI);
            final float xCos = -Mth.cos(-this.xRot * Mth.DEGRAD);
            final float xSin = Mth.sin(-this.xRot * Mth.DEGRAD);

            return Vec3.newTemp(ySin * xCos, xSin, yCos * xCos);
        }
        final float xRot = this.xRotO + (this.xRot - this.xRotO) * a;
        final float yRot = this.yRotO + (this.yRot - this.yRotO) * a;

        final float yCos = Mth.cos(-yRot * Mth.DEGRAD - Mth.PI);
        final float ySin = Mth.sin(-yRot * Mth.DEGRAD - Mth.PI);
        final float xCos = -Mth.cos(-xRot * Mth.DEGRAD);
        final float xSin = Mth.sin(-xRot * Mth.DEGRAD);

        return Vec3.newTemp(ySin * xCos, xSin, yCos * xCos);
    }
    
    public HitResult pick(final double range, final float a) {
        Vec3 from = this.getPos(a);
        Vec3 b = this.getViewVector(a);
        Vec3 to = from.add(b.x * range, b.y * range, b.z * range);
        return this.level.clip(from, to);
    }
    
    public int getMaxSpawnClusterSize() {
        return 4;
    }
    
    public ItemInstance getCarriedItem() {
        return null;
    }
    
    @Override
    public void handleEntityEvent(final byte id) {
        if (id == EntityEvent.HURT) {
            this.walkAnimSpeed = 1.5f;

            this.invulnerableTime = this.invulnerableDuration;
            this.hurtTime = this.hurtDuration = 10;
            this.hurtDir = 0.0f;

            this.level.playSound(this, this.getHurtSound(), this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            this.hurt(null, 0);
        }
        else if (id == EntityEvent.DEATH) {
            this.level.playSound(this, this.getDeathSound(), this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            this.health = 0;
            this.die(null);
        }
        else {
            super.handleEntityEvent(id);
        }
    }
    
    public boolean isSleeping() {
        return false;
    }
    
    public int getItemInHandIcon(final ItemInstance item) {
        return item.getIcon();
    }
}
