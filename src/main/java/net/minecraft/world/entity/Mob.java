// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity;

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
    public int invulnerableDuration;
    public float timeOffs;
    public float rotA;
    public float yBodyRot;
    public float yBodyRotO;
    protected float oRun;
    protected float run;
    protected float animStep;
    protected float animStepO;
    protected boolean hasHair;
    protected String textureName;
    protected boolean allowAlpha;
    protected float rotOffs;
    protected String modelName;
    protected float bobStrength;
    protected int deathScore;
    protected float renderOffset;
    public boolean interpolateOnly;
    public float oAttackAnim;
    public float attackAnim;
    public int health;
    public int lastHealth;
    private int ambientSoundTime;
    public int hurtTime;
    public int hurtDuration;
    public float hurtDir;
    public int deathTime;
    public int attackTime;
    public float oTilt;
    public float tilt;
    protected boolean dead;
    public int modelNum;
    public float animSpeed;
    public float walkAnimSpeedO;
    public float walkAnimSpeed;
    public float walkAnimPos;
    protected int lSteps;
    protected double lx;
    protected double ly;
    protected double lz;
    protected double lyr;
    protected double lxr;
    float fallTime;
    protected int lastHurt;
    protected int noActionTime;
    protected float xxa;
    protected float yya;
    protected float yRotA;
    protected boolean jumping;
    protected float defaultLookAngle;
    protected float runSpeed;
    private Entity lookingAt;
    protected int lookTime;
    
    public Mob(final Level level) {
        super(level);
        this.invulnerableDuration = 20;
        this.yBodyRot = 0.0f;
        this.yBodyRotO = 0.0f;
        this.hasHair = true;
        this.textureName = "/mob/char.png";
        this.allowAlpha = true;
        this.rotOffs = 0.0f;
        this.modelName = null;
        this.bobStrength = 1.0f;
        this.deathScore = 0;
        this.renderOffset = 0.0f;
        this.interpolateOnly = false;
        this.hurtDir = 0.0f;
        this.deathTime = 0;
        this.attackTime = 0;
        this.dead = false;
        this.modelNum = -1;
        this.animSpeed = (float)(Math.random() * 0.8999999761581421 + 0.10000000149011612);
        this.fallTime = 0.0f;
        this.lastHurt = 0;
        this.noActionTime = 0;
        this.jumping = false;
        this.defaultLookAngle = 0.0f;
        this.runSpeed = 0.7f;
        this.lookTime = 0;
        this.health = 10;
        this.blocksBuilding = true;
        this.rotA = (float)(Math.random() + 1.0) * 0.01f;
        this.setPos(this.x, this.y, this.z);
        this.timeOffs = (float)Math.random() * 12398.0f;
        this.yRot = (float)(Math.random() * 3.1415927410125732 * 2.0);
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
        return 80;
    }
    
    public void playAmbientSound() {
        final String ambientSound = this.getAmbientSound();
        if (ambientSound != null) {
            this.level.playSound(this, ambientSound, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
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
        if (this.fireImmune || this.level.isClientSide) {
            this.onFire = 0;
        }
        if (this.isAlive() && this.isUnderLiquid(Material.water) && !this.isWaterMob()) {
            --this.airSupply;
            if (this.airSupply == -20) {
                this.airSupply = 0;
                for (int i = 0; i < 8; ++i) {
                    this.level.addParticle("bubble", this.x + (this.random.nextFloat() - this.random.nextFloat()), this.y + (this.random.nextFloat() - this.random.nextFloat()), this.z + (this.random.nextFloat() - this.random.nextFloat()), this.xd, this.yd, this.zd);
                }
                this.hurt(null, 2);
            }
            this.onFire = 0;
        }
        else {
            this.airSupply = this.airCapacity;
        }
        this.oTilt = this.tilt;
        if (this.attackTime > 0) {
            --this.attackTime;
        }
        if (this.hurtTime > 0) {
            --this.hurtTime;
        }
        if (this.invulnerableTime > 0) {
            --this.invulnerableTime;
        }
        if (this.health <= 0) {
            ++this.deathTime;
            if (this.deathTime > 20) {
                this.beforeRemove();
                this.remove();
                for (int j = 0; j < 20; ++j) {
                    this.level.addParticle("explode", this.x + this.random.nextFloat() * this.bbWidth * 2.0f - this.bbWidth, this.y + this.random.nextFloat() * this.bbHeight, this.z + this.random.nextFloat() * this.bbWidth * 2.0f - this.bbWidth, this.random.nextGaussian() * 0.02, this.random.nextGaussian() * 0.02, this.random.nextGaussian() * 0.02);
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
            final double xd = this.random.nextGaussian() * 0.02;
            final double yd = this.random.nextGaussian() * 0.02;
            final double zd = this.random.nextGaussian() * 0.02;
            final double n = 10.0;
            this.level.addParticle("explode", this.x + this.random.nextFloat() * this.bbWidth * 2.0f - this.bbWidth - xd * n, this.y + this.random.nextFloat() * this.bbHeight - yd * n, this.z + this.random.nextFloat() * this.bbWidth * 2.0f - this.bbWidth - zd * n, xd, yd, zd);
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
        final double x = this.x - this.xo;
        final double y = this.z - this.zo;
        final float sqrt = Mth.sqrt(x * x + y * y);
        float n = this.yBodyRot;
        float n2 = 0.0f;
        this.oRun = this.run;
        float n3 = 0.0f;
        if (sqrt > 0.05f) {
            n3 = 1.0f;
            n2 = sqrt * 3.0f;
            n = (float)Math.atan2(y, x) * 180.0f / Mth.PI - 90.0f;
        }
        if (this.attackAnim > 0.0f) {
            n = this.yRot;
        }
        if (!this.onGround) {
            n3 = 0.0f;
        }
        this.run += (n3 - this.run) * 0.3f;
        float n4;
        for (n4 = n - this.yBodyRot; n4 < -180.0f; n4 += 360.0f) {}
        while (n4 >= 180.0f) {
            n4 -= 360.0f;
        }
        this.yBodyRot += n4 * 0.3f;
        float n5;
        for (n5 = this.yRot - this.yBodyRot; n5 < -180.0f; n5 += 360.0f) {}
        while (n5 >= 180.0f) {
            n5 -= 360.0f;
        }
        final boolean b = n5 < -90.0f || n5 >= 90.0f;
        if (n5 < -75.0f) {
            n5 = -75.0f;
        }
        if (n5 >= 75.0f) {
            n5 = 75.0f;
        }
        this.yBodyRot = this.yRot - n5;
        if (n5 * n5 > 2500.0f) {
            this.yBodyRot += n5 * 0.2f;
        }
        if (b) {
            n2 *= -1.0f;
        }
        while (this.yRot - this.yRotO < -180.0f) {
            this.yRotO -= 360.0f;
        }
        while (this.yRot - this.yRotO >= 180.0f) {
            this.yRotO += 360.0f;
        }
        while (this.yBodyRot - this.yBodyRotO < -180.0f) {
            this.yBodyRotO -= 360.0f;
        }
        while (this.yBodyRot - this.yBodyRotO >= 180.0f) {
            this.yBodyRotO += 360.0f;
        }
        while (this.xRot - this.xRotO < -180.0f) {
            this.xRotO -= 360.0f;
        }
        while (this.xRot - this.xRotO >= 180.0f) {
            this.xRotO += 360.0f;
        }
        this.animStep += n2;
    }
    
    @Override
    protected void setSize(final float w, final float h) {
        super.setSize(w, h);
    }
    
    public void heal(final int heal) {
        if (this.health <= 0) {
            return;
        }
        this.health += heal;
        if (this.health > 20) {
            this.health = 20;
        }
        this.invulnerableTime = this.invulnerableDuration / 2;
    }
    
    @Override
    public boolean hurt(final Entity source, final int dmg) {
        if (this.level.isClientSide) {
            return false;
        }
        this.noActionTime = 0;
        if (this.health <= 0) {
            return false;
        }
        this.walkAnimSpeed = 1.5f;
        boolean b = true;
        if (this.invulnerableTime > this.invulnerableDuration / 2.0f) {
            if (dmg <= this.lastHurt) {
                return false;
            }
            this.actuallyHurt(dmg - this.lastHurt);
            this.lastHurt = dmg;
            b = false;
        }
        else {
            this.lastHurt = dmg;
            this.lastHealth = this.health;
            this.invulnerableTime = this.invulnerableDuration;
            this.actuallyHurt(dmg);
            final int n = 10;
            this.hurtDuration = n;
            this.hurtTime = n;
        }
        this.hurtDir = 0.0f;
        if (b) {
            this.level.broadcastEntityEvent(this, (byte)2);
            this.markHurt();
            if (source != null) {
                double n2;
                double n3;
                for (n2 = source.x - this.x, n3 = source.z - this.z; n2 * n2 + n3 * n3 < 1.0E-4; n2 = (Math.random() - Math.random()) * 0.01, n3 = (Math.random() - Math.random()) * 0.01) {}
                this.hurtDir = (float)(Math.atan2(n3, n2) * 180.0 / 3.1415927410125732) - this.yRot;
                this.knockback(source, dmg, n2, n3);
            }
            else {
                this.hurtDir = (float)((int)(Math.random() * 2.0) * 180);
            }
        }
        if (this.health <= 0) {
            if (b) {
                this.level.playSound(this, this.getDeathSound(), this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            }
            this.die(source);
        }
        else if (b) {
            this.level.playSound(this, this.getHurtSound(), this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
        }
        return true;
    }
    
    @Override
    public void animateHurt() {
        final int n = 10;
        this.hurtDuration = n;
        this.hurtTime = n;
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
        final float sqrt = Mth.sqrt(xd * xd + zd * zd);
        final float n = 0.4f;
        this.xd /= 2.0;
        this.yd /= 2.0;
        this.zd /= 2.0;
        this.xd -= xd / sqrt * n;
        this.yd += 0.4000000059604645;
        this.zd -= zd / sqrt * n;
        if (this.yd > 0.4000000059604645) {
            this.yd = 0.4000000059604645;
        }
    }
    
    public void die(final Entity source) {
        if (this.deathScore >= 0 && source != null) {
            source.awardKillScore(this, this.deathScore);
        }
        if (source != null) {
            source.killed(this);
        }
        this.dead = true;
        if (!this.level.isClientSide) {
            this.dropDeathLoot();
        }
        this.level.broadcastEntityEvent(this, (byte)3);
    }
    
    protected void dropDeathLoot() {
        final int deathLoot = this.getDeathLoot();
        if (deathLoot > 0) {
            for (int nextInt = this.random.nextInt(3), i = 0; i < nextInt; ++i) {
                this.spawnAtLocation(deathLoot, 1);
            }
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
            final int tile = this.level.getTile(Mth.floor(this.x), Mth.floor(this.y - 0.20000000298023224 - this.heightOffset), Mth.floor(this.z));
            if (tile > 0) {
                final Tile.SoundType soundType = Tile.tiles[tile].soundType;
                this.level.playSound(this, soundType.getStepSound(), soundType.getVolume() * 0.5f, soundType.getPitch() * 0.75f);
            }
        }
    }
    
    public void travel(final float xa, final float ya) {
        if (this.isInWater()) {
            final double y = this.y;
            this.moveRelative(xa, ya, 0.02f);
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.800000011920929;
            this.yd *= 0.800000011920929;
            this.zd *= 0.800000011920929;
            this.yd -= 0.02;
            if (this.horizontalCollision && this.isFree(this.xd, this.yd + 0.6000000238418579 - this.y + y, this.zd)) {
                this.yd = 0.30000001192092896;
            }
        }
        else if (this.isInLava()) {
            final double y2 = this.y;
            this.moveRelative(xa, ya, 0.02f);
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.5;
            this.yd *= 0.5;
            this.zd *= 0.5;
            this.yd -= 0.02;
            if (this.horizontalCollision && this.isFree(this.xd, this.yd + 0.6000000238418579 - this.y + y2, this.zd)) {
                this.yd = 0.30000001192092896;
            }
        }
        else {
            float n = 0.91f;
            if (this.onGround) {
                n = 0.54600006f;
                final int tile = this.level.getTile(Mth.floor(this.x), Mth.floor(this.bb.y0) - 1, Mth.floor(this.z));
                if (tile > 0) {
                    n = Tile.tiles[tile].friction * 0.91f;
                }
            }
            final float n2 = 0.16277136f / (n * n * n);
            this.moveRelative(xa, ya, this.onGround ? (0.1f * n2) : 0.02f);
            float n3 = 0.91f;
            if (this.onGround) {
                n3 = 0.54600006f;
                final int tile2 = this.level.getTile(Mth.floor(this.x), Mth.floor(this.bb.y0) - 1, Mth.floor(this.z));
                if (tile2 > 0) {
                    n3 = Tile.tiles[tile2].friction * 0.91f;
                }
            }
            if (this.onLadder()) {
                final float n4 = 0.15f;
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
                this.fallDistance = 0.0f;
                if (this.yd < -0.15) {
                    this.yd = -0.15;
                }
                if (this.isSneaking() && this.yd < 0.0) {
                    this.yd = 0.0;
                }
            }
            this.move(this.xd, this.yd, this.zd);
            if (this.horizontalCollision && this.onLadder()) {
                this.yd = 0.2;
            }
            this.yd -= 0.08;
            this.yd *= 0.9800000190734863;
            this.xd *= n3;
            this.zd *= n3;
        }
        this.walkAnimSpeedO = this.walkAnimSpeed;
        final double n5 = this.x - this.xo;
        final double n6 = this.z - this.zo;
        float n7 = Mth.sqrt(n5 * n5 + n6 * n6) * 4.0f;
        if (n7 > 1.0f) {
            n7 = 1.0f;
        }
        this.walkAnimSpeed += (n7 - this.walkAnimSpeed) * 0.4f;
        this.walkAnimPos += this.walkAnimSpeed;
    }
    
    public boolean onLadder() {
        return this.level.getTile(Mth.floor(this.x), Mth.floor(this.bb.y0), Mth.floor(this.z)) == Tile.ladder.id;
    }
    
    public void addAdditionalSaveData(final CompoundTag compoundTag) {
        compoundTag.putShort("Health", (short)this.health);
        compoundTag.putShort("HurtTime", (short)this.hurtTime);
        compoundTag.putShort("DeathTime", (short)this.deathTime);
        compoundTag.putShort("AttackTime", (short)this.attackTime);
    }
    
    public void readAdditionalSaveData(final CompoundTag compoundTag) {
        this.health = compoundTag.getShort("Health");
        if (!compoundTag.contains("Health")) {
            this.health = 10;
        }
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
            final double n = this.x + (this.lx - this.x) / this.lSteps;
            final double y = this.y + (this.ly - this.y) / this.lSteps;
            final double n2 = this.z + (this.lz - this.z) / this.lSteps;
            double n3;
            for (n3 = this.lyr - this.yRot; n3 < -180.0; n3 += 360.0) {}
            while (n3 >= 180.0) {
                n3 -= 360.0;
            }
            this.yRot += (float)(n3 / this.lSteps);
            this.xRot += (float)((this.lxr - this.xRot) / this.lSteps);
            --this.lSteps;
            this.setPos(n, y, n2);
            this.setRot(this.yRot, this.xRot);
            final List<AABB> cubes = this.level.getCubes(this, this.bb.shrink(0.03125, 0.0, 0.03125));
            if (cubes.size() > 0) {
                double y2 = 0.0;
                for (int i = 0; i < cubes.size(); ++i) {
                    final AABB aabb = cubes.get(i);
                    if (aabb.y1 > y2) {
                        y2 = aabb.y1;
                    }
                }
                this.setPos(n, y + (y2 - this.bb.y0), n2);
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
                this.yd += 0.03999999910593033;
            }
            else if (inLava) {
                this.yd += 0.03999999910593033;
            }
            else if (this.onGround) {
                this.jumpFromGround();
            }
        }
        this.xxa *= 0.98f;
        this.yya *= 0.98f;
        this.yRotA *= 0.9f;
        this.travel(this.xxa, this.yya);
        final List<Entity> entities = this.level.getEntities(this, this.bb.grow(0.20000000298023224, 0.0, 0.20000000298023224));
        if (entities != null && entities.size() > 0) {
            for (int j = 0; j < entities.size(); ++j) {
                final Entity entity = entities.get(j);
                if (entity.isPushable()) {
                    entity.push(this);
                }
            }
        }
    }
    
    protected boolean isImmobile() {
        return this.health <= 0;
    }
    
    protected void jumpFromGround() {
        this.yd = 0.41999998688697815;
    }
    
    protected boolean removeWhenFarAway() {
        return true;
    }
    
    protected void checkDespawn() {
        final Player nearestPlayer = this.level.getNearestPlayer(this, -1.0);
        if (this.removeWhenFarAway() && nearestPlayer != null) {
            final double n = nearestPlayer.x - this.x;
            final double n2 = nearestPlayer.y - this.y;
            final double n3 = nearestPlayer.z - this.z;
            final double n4 = n * n + n2 * n2 + n3 * n3;
            if (n4 > 16384.0) {
                this.remove();
            }
            if (this.noActionTime > 600 && this.random.nextInt(800) == 0) {
                if (n4 < 1024.0) {
                    this.noActionTime = 0;
                }
                else {
                    this.remove();
                }
            }
        }
    }
    
    protected void updateAi() {
        ++this.noActionTime;
        this.level.getNearestPlayer(this, -1.0);
        this.checkDespawn();
        this.xxa = 0.0f;
        this.yya = 0.0f;
        final float n = 8.0f;
        if (this.random.nextFloat() < 0.02f) {
            final Player nearestPlayer = this.level.getNearestPlayer(this, n);
            if (nearestPlayer != null) {
                this.lookingAt = nearestPlayer;
                this.lookTime = 10 + this.random.nextInt(20);
            }
            else {
                this.yRotA = (this.random.nextFloat() - 0.5f) * 20.0f;
            }
        }
        if (this.lookingAt != null) {
            this.lookAt(this.lookingAt, 10.0f, (float)this.getMaxHeadXRot());
            if (this.lookTime-- <= 0 || this.lookingAt.removed || this.lookingAt.distanceToSqr(this) > n * n) {
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
        if (inWater || inLava) {
            this.jumping = (this.random.nextFloat() < 0.8f);
        }
    }
    
    protected int getMaxHeadXRot() {
        return 40;
    }
    
    public void lookAt(final Entity e, final float yMax, final float xMax) {
        final double x = e.x - this.x;
        final double y = e.z - this.z;
        double y2;
        if (e instanceof Mob) {
            final Mob mob = (Mob)e;
            y2 = this.y + this.getHeadHeight() - (mob.y + mob.getHeadHeight());
        }
        else {
            y2 = (e.bb.y0 + e.bb.y1) / 2.0 - (this.y + this.getHeadHeight());
        }
        final double x2 = Mth.sqrt(x * x + y * y);
        final float b = (float)(Math.atan2(y, x) * 180.0 / 3.1415927410125732) - 90.0f;
        this.xRot = -this.rotLerp(this.xRot, (float)(-(Math.atan2(y2, x2) * 180.0 / 3.1415927410125732)), xMax);
        this.yRot = this.rotLerp(this.yRot, b, yMax);
    }
    
    public boolean isLookingAtAnEntity() {
        return this.lookingAt != null;
    }
    
    public Entity getLookingAt() {
        return this.lookingAt;
    }
    
    private float rotLerp(final float a, final float b, final float max) {
        float n;
        for (n = b - a; n < -180.0f; n += 360.0f) {}
        while (n >= 180.0f) {
            n -= 360.0f;
        }
        if (n > max) {
            n = max;
        }
        if (n < -max) {
            n = -max;
        }
        return a + n;
    }
    
    public void beforeRemove() {
    }
    
    public boolean canSpawn() {
        return this.level.isUnobstructed(this.bb) && this.level.getCubes(this, this.bb).size() == 0 && !this.level.containsAnyLiquid(this.bb);
    }
    
    @Override
    protected void outOfWorld() {
        this.hurt(null, 4);
    }
    
    public float getAttackAnim(final float partialTick) {
        float n = this.attackAnim - this.oAttackAnim;
        if (n < 0.0f) {
            ++n;
        }
        return this.oAttackAnim + n * partialTick;
    }
    
    public Vec3 getPos(final float partialTick) {
        if (partialTick == 1.0f) {
            return Vec3.newTemp(this.x, this.y, this.z);
        }
        return Vec3.newTemp(this.xo + (this.x - this.xo) * partialTick, this.yo + (this.y - this.yo) * partialTick, this.zo + (this.z - this.zo) * partialTick);
    }
    
    @Override
    public Vec3 getLookAngle() {
        return this.getViewVector(1.0f);
    }
    
    public Vec3 getViewVector(final float partialTick) {
        if (partialTick == 1.0f) {
            final float cos = Mth.cos(-this.yRot * Mth.DEGRAD - Mth.PI);
            final float sin = Mth.sin(-this.yRot * Mth.DEGRAD - Mth.PI);
            final float n = -Mth.cos(-this.xRot * Mth.DEGRAD);
            return Vec3.newTemp(sin * n, Mth.sin(-this.xRot * Mth.DEGRAD), cos * n);
        }
        final float n2 = this.xRotO + (this.xRot - this.xRotO) * partialTick;
        final float n3 = this.yRotO + (this.yRot - this.yRotO) * partialTick;
        final float cos2 = Mth.cos(-n3 * Mth.DEGRAD - Mth.PI);
        final float sin2 = Mth.sin(-n3 * Mth.DEGRAD - Mth.PI);
        final float n4 = -Mth.cos(-n2 * Mth.DEGRAD);
        return Vec3.newTemp(sin2 * n4, Mth.sin(-n2 * Mth.DEGRAD), cos2 * n4);
    }
    
    public HitResult pick(final double range, final float partialTick) {
        final Vec3 pos = this.getPos(partialTick);
        final Vec3 viewVector = this.getViewVector(partialTick);
        return this.level.clip(pos, pos.add(viewVector.x * range, viewVector.y * range, viewVector.z * range));
    }
    
    public int getMaxSpawnClusterSize() {
        return 4;
    }
    
    public ItemInstance getCarriedItem() {
        return null;
    }
    
    @Override
    public void handleEntityEvent(final byte id) {
        if (id == 2) {
            this.walkAnimSpeed = 1.5f;
            this.invulnerableTime = this.invulnerableDuration;
            final int n = 10;
            this.hurtDuration = n;
            this.hurtTime = n;
            this.hurtDir = 0.0f;
            this.level.playSound(this, this.getHurtSound(), this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            this.hurt(null, 0);
        }
        else if (id == 3) {
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
