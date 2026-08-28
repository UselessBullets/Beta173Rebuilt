// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item.crafting;

import java.util.ArrayList;
import net.minecraft.world.inventory.CraftingContainer;
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
        final ArrayList<ItemInstance> tempList = new ArrayList<>(this.ingredients);
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                final ItemInstance item = craftSlots.getItem(x, y);

                if (item != null) {
                    boolean found = false;

                    for (final ItemInstance ingredient : tempList) {
                        if (item.id == ingredient.id && (ingredient.getAuxValue() == Recipes.ANY_AUX_VALUE || item.getAuxValue() == ingredient.getAuxValue())) {
                            found = true;
                            tempList.remove(ingredient);
                            break;
                        }
                    }

                    if (!found) {
                        return false;
                    }
                }
            }
        }

        return tempList.isEmpty();
    }
    
    public ItemInstance assemble(final CraftingContainer craftSlots) {
        return this.result.copy();
    }
    
    public int size() {
        return this.ingredients.size();
    }
}
