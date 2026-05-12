// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;

public interface Container
{
    int getContainerSize();
    
    ItemInstance getItem(final int slot);
    
    ItemInstance removeItem(final int slot, final int count);
    
    void setItem(final int slot, final ItemInstance item);
    
    String getName();
    
    int getMaxStackSize();
    
    void setChanged();
    
    boolean stillValid(final Player player);
}
