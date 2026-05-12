// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item.crafting;

import java.util.Comparator;

class Recipes_RecipeSorter implements Comparator
{
    final /* synthetic */ Recipes recipes;
    
    Recipes_RecipeSorter(final Recipes recipes) {
        this.recipes = recipes;
    }
    
    public int compare(final Recipe r0, final Recipe r1) {
        if (r0 instanceof ShapelessRecipe && r1 instanceof ShapedRecipe) {
            return 1;
        }
        if (r1 instanceof ShapelessRecipe && r0 instanceof ShapedRecipe) {
            return -1;
        }
        if (r1.size() < r0.size()) {
            return -1;
        }
        if (r1.size() > r0.size()) {
            return 1;
        }
        return 0;
    }
}
