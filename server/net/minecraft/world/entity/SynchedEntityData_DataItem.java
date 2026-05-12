// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity;

public class SynchedEntityData_DataItem
{
    private final int type;
    private final int id;
    private Object value;
    private boolean dirty;
    
    public SynchedEntityData_DataItem(final int type, final int id, final Object value) {
        this.id = id;
        this.value = value;
        this.type = type;
        this.dirty = true;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setValue(final Object value) {
        this.value = value;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public int getType() {
        return this.type;
    }
    
    public boolean isDirty() {
        return this.dirty;
    }
    
    public void setDirty(final boolean dirty) {
        this.dirty = dirty;
    }
}
