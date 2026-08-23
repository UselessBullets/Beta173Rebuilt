// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.animal;

import com.mojang.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class Chicken extends Animal
{
    public boolean sheared = false;
    public float flap = 0.0f;
    public float flapSpeed = 0.0f;
    public float oFlapSpeed;
    public float oFlap;
    public float flapping = 1.0f;
    public int eggTime;
    
    public Chicken(final Level level) {
        super(level);
        this.textureName = "/mob/chicken.png";
        this.setSize(0.3f, 0.4f);
        this.health = 4;
        this.eggTime = this.random.nextInt(6000) + 6000;
    }
    
    @Override
    public void aiStep() {
        super.aiStep();

        this.oFlap = this.flap;
        this.oFlapSpeed = this.flapSpeed;

        this.flapSpeed += (float)((this.onGround ? -1 : 4) * 0.3);
        if (this.flapSpeed < 0.0f) this.flapSpeed = 0.0f;
        if (this.flapSpeed > 1.0f) this.flapSpeed = 1.0f;

        if (!this.onGround && this.flapping < 1.0f) this.flapping = 1.0f;
        this.flapping *= (float)0.9;

        if (!this.onGround && this.yd < 0.0) {
            this.yd *= 0.6;
        }

        this.flap += this.flapping * 2.0f;

        if (!this.level.isClientSide && --this.eggTime <= 0) {
            this.level.playSound(this, "mob.chickenplop", 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            this.spawnAtLocation(Item.egg.id, 1);
            this.eggTime = this.random.nextInt(20 * 60 * 5) + 20 * 60 * 5;
        }
    }
    
    @Override
    protected void causeFallDamage(final float distance) {
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
        return "mob.chicken";
    }
    
    @Override
    protected String getHurtSound() {
        return "mob.chickenhurt";
    }
    
    @Override
    protected String getDeathSound() {
        return "mob.chickenhurt";
    }
    
    @Override
    protected int getDeathLoot() {
        return Item.feather.id;
    }
}
