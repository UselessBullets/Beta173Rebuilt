// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item.crafting;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import net.minecraft.world.CraftingContainer;
import java.util.List;
import net.minecraft.world.item.ItemInstance;

public class ShapelessRecipe implements Recipe
{
    private final ItemInstance result;
    private final List<ItemInstance> ingredients;
    
    public ShapelessRecipe(final ItemInstance result, final List<ItemInstance> ingredients) {
        this.result = result;
        this.ingredients = ingredients;
    }
    
    public ItemInstance getResultItem() {
        return this.result;
    }
    
    public boolean matches(final CraftingContainer craftSlots) {
        final ArrayList<ItemInstance> list = new ArrayList<>(this.ingredients);
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                final ItemInstance item = craftSlots.getItem(j, i);
                if (item != null) {
                    boolean b = false;
                    for (final ItemInstance itemInstance : list) {
                        if (item.id == itemInstance.id && (itemInstance.getAuxValue() == -1 || item.getAuxValue() == itemInstance.getAuxValue())) {
                            b = true;
                            list.remove(itemInstance);
                            break;
                        }
                    }
                    if (!b) {
                        return false;
                    }
                }
            }
        }
        return list.isEmpty();
    }
    
    public ItemInstance assemble(final CraftingContainer craftSlots) {
        return this.result.copy();
    }
    
    public int size() {
        return this.ingredients.size();
    }
}
