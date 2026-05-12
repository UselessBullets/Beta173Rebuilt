// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item.crafting;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.tile.Tile;

public class StructureRecipes
{
    public void addRecipes(final Recipes recipes) {
        recipes.addShapedRecipe(new ItemInstance(Tile.chest), "###", "# #", "###", '#', Tile.wood);
        recipes.addShapedRecipe(new ItemInstance(Tile.furnace), "###", "# #", "###", '#', Tile.stoneBrick);
        recipes.addShapedRecipe(new ItemInstance(Tile.workBench), "##", "##", '#', Tile.wood);
        recipes.addShapedRecipe(new ItemInstance(Tile.sandStone), "##", "##", '#', Tile.sand);
    }
}
