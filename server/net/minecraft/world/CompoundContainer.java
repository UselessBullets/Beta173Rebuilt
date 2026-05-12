// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;

public class CompoundContainer implements Container
{
    private String a;
    private Container b;
    private Container c;
    
    public CompoundContainer(final String string, final Container hp2, final Container hp3) {
        this.a = string;
        this.b = hp2;
        this.c = hp3;
    }
    
    public int getContainerSize() {
        return this.b.getContainerSize() + this.c.getContainerSize();
    }
    
    public String getName() {
        return this.a;
    }
    
    public ItemInstance getItem(final int slot) {
        if (slot >= this.b.getContainerSize()) {
            return this.c.getItem(slot - this.b.getContainerSize());
        }
        return this.b.getItem(slot);
    }
    
    public ItemInstance removeItem(final int slot, final int count) {
        if (slot >= this.b.getContainerSize()) {
            return this.c.removeItem(slot - this.b.getContainerSize(), count);
        }
        return this.b.removeItem(slot, count);
    }
    
    public void setItem(final int slot, final ItemInstance item) {
        if (slot >= this.b.getContainerSize()) {
            this.c.setItem(slot - this.b.getContainerSize(), item);
        }
        else {
            this.b.setItem(slot, item);
        }
    }
    
    public int getMaxStackSize() {
        return this.b.getMaxStackSize();
    }
    
    public void setChanged() {
        this.b.setChanged();
        this.c.setChanged();
    }
    
    public boolean stillValid(final Player player) {
        return this.b.stillValid(player) && this.c.stillValid(player);
    }
}
