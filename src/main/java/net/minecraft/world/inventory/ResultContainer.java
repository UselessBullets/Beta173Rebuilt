// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.Container;

public class ResultContainer implements Container
{
    private ItemInstance[] items;
    
    public ResultContainer() {
        this.items = new ItemInstance[1];
    }
    
    public int getContainerSize() {
        return 1;
    }
    
    public ItemInstance getItem(final int slot) {
        return this.items[slot];
    }
    
    public String getName() {
        return "Result";
    }
    
    public ItemInstance removeItem(final int slot, final int count) {
        if (this.items[slot] != null) {
            final ItemInstance itemInstance = this.items[slot];
            this.items[slot] = null;
            return itemInstance;
        }
        return null;
    }
    
    public void setItem(final int slot, final ItemInstance item) {
        this.items[slot] = item;
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
