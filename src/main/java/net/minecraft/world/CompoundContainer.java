// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;

public class CompoundContainer implements Container
{
    private String name;
    private Container c1;
    private Container c2;
    
    public CompoundContainer(final String name, final Container c1, final Container c2) {
        this.name = name;
        this.c1 = c1;
        this.c2 = c2;
    }
    
    public int getContainerSize() {
        return this.c1.getContainerSize() + this.c2.getContainerSize();
    }
    
    public String getName() {
        return this.name;
    }
    
    public ItemInstance getItem(final int slot) {
        if (slot >= this.c1.getContainerSize()) {
            return this.c2.getItem(slot - this.c1.getContainerSize());
        }
        return this.c1.getItem(slot);
    }
    
    public ItemInstance removeItem(final int slot, final int count) {
        if (slot >= this.c1.getContainerSize()) {
            return this.c2.removeItem(slot - this.c1.getContainerSize(), count);
        }
        return this.c1.removeItem(slot, count);
    }
    
    public void setItem(final int slot, final ItemInstance item) {
        if (slot >= this.c1.getContainerSize()) {
            this.c2.setItem(slot - this.c1.getContainerSize(), item);
        }
        else {
            this.c1.setItem(slot, item);
        }
    }
    
    public int getMaxStackSize() {
        return this.c1.getMaxStackSize();
    }
    
    public void setChanged() {
        this.c1.setChanged();
        this.c2.setChanged();
    }
    
    public boolean stillValid(final Player player) {
        return this.c1.stillValid(player) && this.c2.stillValid(player);
    }
}
