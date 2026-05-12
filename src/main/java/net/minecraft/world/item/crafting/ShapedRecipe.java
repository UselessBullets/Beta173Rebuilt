// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item.crafting;

import net.minecraft.world.CraftingContainer;
import net.minecraft.world.item.ItemInstance;

public class ShapedRecipe implements Recipe
{
    private int width;
    private int height;
    private ItemInstance[] recipeItems;
    private ItemInstance result;
    public final int resultId;
    
    public ShapedRecipe(final int width, final int height, final ItemInstance[] recipeItems, final ItemInstance result) {
        this.resultId = result.id;
        this.width = width;
        this.height = height;
        this.recipeItems = recipeItems;
        this.result = result;
    }
    
    public ItemInstance getResultItem() {
        return this.result;
    }
    
    public boolean matches(final CraftingContainer craftSlots) {
        for (int i = 0; i <= 3 - this.width; ++i) {
            for (int j = 0; j <= 3 - this.height; ++j) {
                if (this.matches(craftSlots, i, j, true)) {
                    return true;
                }
                if (this.matches(craftSlots, i, j, false)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean matches(final CraftingContainer craftSlots, final int xOffs, final int yOffs, final boolean xFlip) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                final int n = i - xOffs;
                final int n2 = j - yOffs;
                ItemInstance itemInstance = null;
                if (n >= 0 && n2 >= 0 && n < this.width && n2 < this.height) {
                    if (xFlip) {
                        itemInstance = this.recipeItems[this.width - n - 1 + n2 * this.width];
                    }
                    else {
                        itemInstance = this.recipeItems[n + n2 * this.width];
                    }
                }
                final ItemInstance item = craftSlots.getItem(i, j);
                if (item != null || itemInstance != null) {
                    if ((item == null && itemInstance != null) || (item != null && itemInstance == null)) {
                        return false;
                    }
                    if (itemInstance.id != item.id) {
                        return false;
                    }
                    if (itemInstance.getAuxValue() != -1 && itemInstance.getAuxValue() != item.getAuxValue()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public ItemInstance assemble(final CraftingContainer craftSlots) {
        return new ItemInstance(this.result.id, this.result.count, this.result.getAuxValue());
    }
    
    public int size() {
        return this.width * this.height;
    }
}
