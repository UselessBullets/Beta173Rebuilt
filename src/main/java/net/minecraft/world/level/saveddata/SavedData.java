// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.saveddata;

import com.mojang.nbt.CompoundTag;

public abstract class SavedData
{
    public final String id;
    private boolean dirty;
    
    public SavedData(final String id) {
        this.id = id;
    }
    
    public abstract void load(final CompoundTag compoundTag);
    
    public abstract void save(final CompoundTag compoundTag);
    
    public void setDirty() {
        this.setDirty(true);
    }
    
    public void setDirty(final boolean dirty) {
        this.dirty = dirty;
    }
    
    public boolean isDirty() {
        return this.dirty;
    }
}
