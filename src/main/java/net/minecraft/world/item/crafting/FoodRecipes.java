// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item.crafting;

import net.minecraft.world.item.DyePowderItem;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.Item;

public class FoodRecipes
{
    public void addRecipes(final Recipes recipes) {
        recipes.addShapedRecipe(new ItemInstance(Item.mushroomStew),
                "Y",
                "X",
                "#",

                'X', Tile.mushroom1,
                'Y', Tile.mushroom2,
                '#', Item.bowl);

        recipes.addShapedRecipe(new ItemInstance(Item.mushroomStew),
                "Y",
                "X",
                "#",

                'X', Tile.mushroom2,
                'Y', Tile.mushroom1,
                '#', Item.bowl);

        recipes.addShapedRecipe(new ItemInstance(Item.cookie, 8),
                "#X#",

                'X', new ItemInstance(Item.dye_powder, 1, DyePowderItem.BROWN),
                '#', Item.wheat);
    }
}
