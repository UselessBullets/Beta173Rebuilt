// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.animal;

import com.mojang.nbt.CompoundTag;
import net.minecraft.world.entity.Creature;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.PathfinderMob;

public class WaterAnimal extends PathfinderMob implements Creature
{
    public WaterAnimal(final Level level) {
        super(level);
    }
    
    @Override
    public boolean isWaterMob() {
        return true;
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
    public boolean canSpawn() {
        return this.level.isUnobstructed(this.bb);
    }
    
    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }
}
