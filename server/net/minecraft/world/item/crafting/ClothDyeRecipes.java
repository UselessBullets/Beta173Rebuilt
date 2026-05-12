// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item.crafting;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.tile.ClothTile;
import net.minecraft.world.level.tile.Tile;

public class ClothDyeRecipes
{
    public void addRecipes(final Recipes recipes) {
        for (int i = 0; i < 16; ++i) {
            recipes.addShapelessRecipe(new ItemInstance(Tile.cloth, 1, ClothTile.getItemAuxValueForTileData(i)), new ItemInstance(Item.dye_powder, 1, i), new ItemInstance(Item.items[Tile.cloth.id], 1, 0));
        }
        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 2, 11), Tile.flower);
        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 2, 1), Tile.rose);
        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 3, 15), Item.bone);
        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 2, 9), new ItemInstance(Item.dye_powder, 1, 1), new ItemInstance(Item.dye_powder, 1, 15));
        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 2, 14), new ItemInstance(Item.dye_powder, 1, 1), new ItemInstance(Item.dye_powder, 1, 11));
        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 2, 10), new ItemInstance(Item.dye_powder, 1, 2), new ItemInstance(Item.dye_powder, 1, 15));
        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 2, 8), new ItemInstance(Item.dye_powder, 1, 0), new ItemInstance(Item.dye_powder, 1, 15));
        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 2, 7), new ItemInstance(Item.dye_powder, 1, 8), new ItemInstance(Item.dye_powder, 1, 15));
        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 3, 7), new ItemInstance(Item.dye_powder, 1, 0), new ItemInstance(Item.dye_powder, 1, 15), new ItemInstance(Item.dye_powder, 1, 15));
        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 2, 12), new ItemInstance(Item.dye_powder, 1, 4), new ItemInstance(Item.dye_powder, 1, 15));
        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 2, 6), new ItemInstance(Item.dye_powder, 1, 4), new ItemInstance(Item.dye_powder, 1, 2));
        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 2, 5), new ItemInstance(Item.dye_powder, 1, 4), new ItemInstance(Item.dye_powder, 1, 1));
        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 2, 13), new ItemInstance(Item.dye_powder, 1, 5), new ItemInstance(Item.dye_powder, 1, 9));
        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 3, 13), new ItemInstance(Item.dye_powder, 1, 4), new ItemInstance(Item.dye_powder, 1, 1), new ItemInstance(Item.dye_powder, 1, 9));
        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 4, 13), new ItemInstance(Item.dye_powder, 1, 4), new ItemInstance(Item.dye_powder, 1, 1), new ItemInstance(Item.dye_powder, 1, 1), new ItemInstance(Item.dye_powder, 1, 15));
    }
}
