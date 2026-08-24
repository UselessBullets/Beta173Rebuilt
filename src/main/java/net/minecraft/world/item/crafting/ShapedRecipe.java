// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item.crafting;

import net.minecraft.world.CraftingContainer;
import net.minecraft.world.item.ItemInstance;

public class ShapedRecipe implements Recipe
{
    private int width, height;
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
        for (int xOffs = 0; xOffs <= 3 - this.width; ++xOffs) {
            for (int yOffs = 0; yOffs <= 3 - this.height; ++yOffs) {
                if (this.matches(craftSlots, xOffs, yOffs, true)) return true;
                if (this.matches(craftSlots, xOffs, yOffs, false)) return true;
            }
        }
        return false;
    }
    
    private boolean matches(final CraftingContainer craftSlots, final int xOffs, final int yOffs, final boolean xFlip) {
        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                final int xs = x - xOffs;
                final int ys = y - yOffs;
                ItemInstance expected = null;
                if (xs >= 0 && ys >= 0 && xs < this.width && ys < this.height) {
                    if (xFlip) expected = this.recipeItems[this.width - xs - 1 + ys * this.width];
                    else expected = this.recipeItems[xs + ys * this.width];
                }
                final ItemInstance item = craftSlots.getItem(x, y);
                if (item != null || expected != null) {
                    if ((item == null && expected != null) || (item != null && expected == null)) {
                        return false;
                    }
                    if (expected.id != item.id) {
                        return false;
                    }
                    if (expected.getAuxValue() != Recipes.ANY_AUX_VALUE && expected.getAuxValue() != item.getAuxValue()) {
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
