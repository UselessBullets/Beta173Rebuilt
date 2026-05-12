// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.animal;

import util.Mth;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Creature;
import net.minecraft.world.entity.PathfinderMob;

public abstract class Animal extends PathfinderMob implements Creature
{
    public Animal(final Level level) {
        super(level);
    }
    
    @Override
    protected float getWalkTargetValue(final int x, final int y, final int z) {
        if (this.level.getTile(x, y - 1, z) == Tile.grass.id) {
            return 10.0f;
        }
        return this.level.getBrightness(x, y, z) - 0.5f;
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
        final int floor = Mth.floor(this.x);
        final int floor2 = Mth.floor(this.bb.y0);
        final int floor3 = Mth.floor(this.z);
        return this.level.getTile(floor, floor2 - 1, floor3) == Tile.grass.id && this.level.getDaytimeRawBrightness(floor, floor2, floor3) > 8 && super.canSpawn();
    }
    
    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }
}
