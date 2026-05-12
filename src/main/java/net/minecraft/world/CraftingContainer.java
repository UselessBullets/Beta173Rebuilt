// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemInstance;

public class CraftingContainer implements Container
{
    private ItemInstance[] items;
    private int width;
    private AbstractContainerMenu menu;
    
    public CraftingContainer(final AbstractContainerMenu menu, final int w, final int h) {
        this.items = new ItemInstance[w * h];
        this.menu = menu;
        this.width = w;
    }
    
    public int getContainerSize() {
        return this.items.length;
    }
    
    public ItemInstance getItem(final int slot) {
        if (slot >= this.getContainerSize()) {
            return null;
        }
        return this.items[slot];
    }
    
    public ItemInstance getItem(final int x, final int y) {
        if (x < 0 || x >= this.width) {
            return null;
        }
        return this.getItem(x + y * this.width);
    }
    
    public String getName() {
        return "Crafting";
    }
    
    public ItemInstance removeItem(final int slot, final int count) {
        if (this.items[slot] == null) {
            return null;
        }
        if (this.items[slot].count <= count) {
            final ItemInstance itemInstance = this.items[slot];
            this.items[slot] = null;
            this.menu.slotsChanged(this);
            return itemInstance;
        }
        final ItemInstance remove = this.items[slot].remove(count);
        if (this.items[slot].count == 0) {
            this.items[slot] = null;
        }
        this.menu.slotsChanged(this);
        return remove;
    }
    
    public void setItem(final int slot, final ItemInstance item) {
        this.items[slot] = item;
        this.menu.slotsChanged(this);
    }
    
    public int getMaxStackSize() {
        return 64;
    }
    
    public void setChanged() {
    }
    
    public boolean stillValid(final Player player) {
        return true;
    }
}
