// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item.crafting;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.inventory.CraftingContainer;

public interface Recipe
{
    boolean matches(final CraftingContainer craftSlots);
    
    ItemInstance assemble(final CraftingContainer craftSlots);
    
    int size();
    
    ItemInstance getResultItem();
}
