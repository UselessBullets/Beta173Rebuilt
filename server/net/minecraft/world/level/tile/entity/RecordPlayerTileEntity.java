// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile.entity;

import com.mojang.nbt.CompoundTag;

public class RecordPlayerTileEntity extends TileEntity
{
    public int record;
    
    @Override
    public void load(final CompoundTag compoundTag) {
        super.load(compoundTag);
        this.record = compoundTag.getInt("Record");
    }
    
    @Override
    public void save(final CompoundTag compoundTag) {
        super.save(compoundTag);
        if (this.record > 0) {
            compoundTag.putInt("Record", this.record);
        }
    }
}
