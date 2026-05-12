// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world;

import net.minecraft.world.entity.player.Player;
import java.util.List;
import net.minecraft.world.item.ItemInstance;

public class SimpleContainer implements Container
{
    private String name;
    private int size;
    private ItemInstance[] items;
    private List listeners;
    
    public SimpleContainer(final String name, final int size) {
        this.name = name;
        this.size = size;
        this.items = new ItemInstance[size];
    }
    
    public ItemInstance getItem(final int slot) {
        return this.items[slot];
    }
    
    public ItemInstance removeItem(final int slot, final int count) {
        if (this.items[slot] == null) {
            return null;
        }
        if (this.items[slot].count <= count) {
            final ItemInstance itemInstance = this.items[slot];
            this.items[slot] = null;
            this.setChanged();
            return itemInstance;
        }
        final ItemInstance remove = this.items[slot].remove(count);
        if (this.items[slot].count == 0) {
            this.items[slot] = null;
        }
        this.setChanged();
        return remove;
    }
    
    public void setItem(final int slot, final ItemInstance item) {
        this.items[slot] = item;
        if (item != null && item.count > this.getMaxStackSize()) {
            item.count = this.getMaxStackSize();
        }
        this.setChanged();
    }
    
    public int getContainerSize() {
        return this.size;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getMaxStackSize() {
        return 64;
    }
    
    public void setChanged() {
        if (this.listeners != null) {
            for (int i = 0; i < this.listeners.size(); ++i) {
                ((ContainerListener)this.listeners.get(i)).containerChanged(this);
            }
        }
    }
    
    public boolean stillValid(final Player player) {
        return true;
    }
}
