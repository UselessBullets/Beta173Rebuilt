// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.animal;

import util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.Item;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class Squid extends WaterAnimal
{
    public float xBodyRot;
    public float xBodyRotO;
    public float zBodyRot;
    public float zBodyRotO;
    public float tentacleMovement;
    public float oldTentacleMovement;
    public float tentacleAngle;
    public float oldTentacleAngle;
    private float speed;
    private float tentacleSpeed;
    private float rotateSpeed;
    private float tx;
    private float ty;
    private float tz;
    
    public Squid(final Level level) {
        super(level);
        this.xBodyRot = 0.0f;
        this.xBodyRotO = 0.0f;
        this.zBodyRot = 0.0f;
        this.zBodyRotO = 0.0f;
        this.tentacleMovement = 0.0f;
        this.oldTentacleMovement = 0.0f;
        this.tentacleAngle = 0.0f;
        this.oldTentacleAngle = 0.0f;
        this.speed = 0.0f;
        this.tentacleSpeed = 0.0f;
        this.rotateSpeed = 0.0f;
        this.tx = 0.0f;
        this.ty = 0.0f;
        this.tz = 0.0f;
        this.textureName = "/mob/squid.png";
        this.setSize(0.95f, 0.95f);
        this.tentacleSpeed = 1.0f / (this.random.nextFloat() + 1.0f) * 0.2f;
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
    }
    
    @Override
    protected String getAmbientSound() {
        return null;
    }
    
    @Override
    protected String getHurtSound() {
        return null;
    }
    
    @Override
    protected String getDeathSound() {
        return null;
    }
    
    @Override
    protected float getSoundVolume() {
        return 0.4f;
    }
    
    @Override
    protected int getDeathLoot() {
        return 0;
    }
    
    @Override
    protected void dropDeathLoot() {
        for (int n = this.random.nextInt(3) + 1, i = 0; i < n; ++i) {
            this.spawnAtLocation(new ItemInstance(Item.dye_powder, 1, 0), 0.0f);
        }
    }
    
    @Override
    public boolean interact(final Player player) {
        return false;
    }
    
    @Override
    public boolean isInWater() {
        return this.level.checkAndHandleWater(this.bb.grow(0.0, -0.6000000238418579, 0.0), Material.water, this);
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
        this.xBodyRotO = this.xBodyRot;
        this.zBodyRotO = this.zBodyRot;
        this.oldTentacleMovement = this.tentacleMovement;
        this.oldTentacleAngle = this.tentacleAngle;
        this.tentacleMovement += this.tentacleSpeed;
        if (this.tentacleMovement > 6.2831855f) {
            this.tentacleMovement -= 6.2831855f;
            if (this.random.nextInt(10) == 0) {
                this.tentacleSpeed = 1.0f / (this.random.nextFloat() + 1.0f) * 0.2f;
            }
        }
        if (this.isInWater()) {
            if (this.tentacleMovement < Mth.PI) {
                final float n = this.tentacleMovement / Mth.PI;
                this.tentacleAngle = Mth.sin(n * n * Mth.PI) * Mth.PI * 0.25f;
                if (n > 0.75) {
                    this.speed = 1.0f;
                    this.rotateSpeed = 1.0f;
                }
                else {
                    this.rotateSpeed *= 0.8f;
                }
            }
            else {
                this.tentacleAngle = 0.0f;
                this.speed *= 0.9f;
                this.rotateSpeed *= 0.99f;
            }
            if (!this.interpolateOnly) {
                this.xd = this.tx * this.speed;
                this.yd = this.ty * this.speed;
                this.zd = this.tz * this.speed;
            }
            final float sqrt = Mth.sqrt(this.xd * this.xd + this.zd * this.zd);
            this.yBodyRot += (-(float)Math.atan2(this.xd, this.zd) * Mth.RADDEG - this.yBodyRot) * 0.1f;
            this.yRot = this.yBodyRot;
            this.zBodyRot += Mth.PI * this.rotateSpeed * 1.5f;
            this.xBodyRot += (-(float)Math.atan2(sqrt, this.yd) * Mth.RADDEG - this.xBodyRot) * 0.1f;
        }
        else {
            this.tentacleAngle = Mth.abs(Mth.sin(this.tentacleMovement)) * Mth.PI * 0.25f;
            if (!this.interpolateOnly) {
                this.xd = 0.0;
                this.yd -= 0.08;
                this.yd *= 0.9800000190734863;
                this.zd = 0.0;
            }
            this.xBodyRot += (float)((-90.0f - this.xBodyRot) * 0.02);
        }
    }
    
    @Override
    public void travel(final float xa, final float ya) {
        this.move(this.xd, this.yd, this.zd);
    }
    
    @Override
    protected void updateAi() {
        if (this.random.nextInt(50) == 0 || !this.wasInWater || (this.tx == 0.0f && this.ty == 0.0f && this.tz == 0.0f)) {
            final float n = this.random.nextFloat() * Mth.PI * 2.0f;
            this.tx = Mth.cos(n) * 0.2f;
            this.ty = -0.1f + this.random.nextFloat() * 0.2f;
            this.tz = Mth.sin(n) * 0.2f;
        }
        this.checkDespawn();
    }
}
