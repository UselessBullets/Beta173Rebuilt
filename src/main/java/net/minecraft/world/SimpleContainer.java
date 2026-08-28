// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world;

import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemInstance;

public class SimpleContainer implements Container
{
    private String name;
    private int size;
    private ItemInstance[] items;
    private List<ContainerListener> listeners;
    
    public SimpleContainer(final String name, final int size) {
        this.name = name;
        this.size = size;

        this.items = new ItemInstance[size];
    }

    // Useless - Existed in b1.2 and LCE leaks
    public void addListener(ContainerListener listener) {
        if (this.listeners == null) this.listeners = new ArrayList<>();
        this.listeners.add(listener);
    }

    // Useless - Existed in b1.2 and LCE leaks
    public void removeListener(ContainerListener listener) {
        this.listeners.remove(listener);
    }
    
    public ItemInstance getItem(final int slot) {
        return this.items[slot];
    }
    
    public ItemInstance removeItem(final int slot, final int count) {
        if (this.items[slot] != null) {
            if (this.items[slot].count <= count) {
                final ItemInstance item = this.items[slot];
                this.items[slot] = null;
                this.setChanged();
                return item;
            } else {
                final ItemInstance i = this.items[slot].remove(count);
                if (this.items[slot].count == 0) this.items[slot] = null;
                this.setChanged();
                return i;
            }
        }
        return null;
    }
    
    public void setItem(final int slot, final ItemInstance item) {
        this.items[slot] = item;
        if (item != null && item.count > this.getMaxStackSize()) item.count = this.getMaxStackSize();
        this.setChanged();
    }
    
    public int getContainerSize() {
        return this.size;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getMaxStackSize() {
        return Container.LARGE_MAX_STACK_SIZE;
    }
    
    public void setChanged() {
        if (this.listeners != null) {
            for (int i = 0; i < this.listeners.size(); ++i) {
                this.listeners.get(i).containerChanged(this);
            }
        }
    }
    
    public boolean stillValid(final Player player) {
        return true;
    }
}
