// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.inventory;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.Container;

public class Slot
{
    private final int slot;
    private final Container container;
    public int index;
    public int x, y;
    
    public Slot(final Container container, final int slot, final int x, final int y) {
        this.container = container;
        this.slot = slot;

        this.x = x;
        this.y = y;
    }

    // Useless - In b1.2 and LCE leaks
    public void swap(Slot other) {
        ItemInstance item1 = this.container.getItem(this.slot);
        ItemInstance item2 = other.container.getItem(other.slot);

        if (item1 != null && item1.count > other.getMaxStackSize()) {
            if (item2 != null) return;
            item2 = item1.remove(item1.count - other.getMaxStackSize());
        }

        if (item2 != null && item2.count > this.getMaxStackSize()) {
            if (item1 != null) return;
            item1 = item2.remove(item2.count - this.getMaxStackSize());
        }
        other.container.setItem(other.slot, item1);

        this.container.setItem(this.slot, item2);
        this.setChanged();
    }
    
    public void onTake(final ItemInstance carried) {
        this.setChanged();
    }
    
    public boolean mayPlace(final ItemInstance item) {
        return true;
    }
    
    public ItemInstance getItem() {
        return this.container.getItem(this.slot);
    }
    
    public boolean hasItem() {
        return this.getItem() != null;
    }
    
    public void set(final ItemInstance item) {
        this.container.setItem(this.slot, item);
        this.setChanged();
    }
    
    public void setChanged() {
        this.container.setChanged();
    }
    
    public int getMaxStackSize() {
        return this.container.getMaxStackSize();
    }
    
    public int getNoItemIcon() {
        return -1;
    }
    
    public ItemInstance remove(final int c) {
        return this.container.removeItem(this.slot, c);
    }

    public boolean isAt(final Container container, final int slot) {
        return container == this.container && slot == this.slot;
    }
}
