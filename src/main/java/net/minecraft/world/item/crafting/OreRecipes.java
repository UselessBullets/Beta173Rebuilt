// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item.crafting;

import net.minecraft.world.item.DyePowderItem;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.tile.Tile;

public class OreRecipes
{
    private Object[][] map = new Object[][]
            {
                    { Tile.goldBlock, new ItemInstance(Item.goldIngot, 9) },
                    { Tile.ironBlock, new ItemInstance(Item.ironIngot, 9) },
                    { Tile.emeraldBlock, new ItemInstance(Item.emerald, 9) },
                    { Tile.lapisBlock, new ItemInstance(Item.dye_powder, 9, DyePowderItem.BLUE) }
            };
    
    public void addRecipes(final Recipes recipes) {
        for (int i = 0; i < this.map.length; ++i) {
            final Tile tile = (Tile)this.map[i][0];
            final ItemInstance result = (ItemInstance)this.map[i][1];
            recipes.addShapedRecipe(new ItemInstance(tile),
                    "###",
                    "###",
                    "###",

                    '#', result);
            recipes.addShapedRecipe(result,
                    "#",

                    '#', tile);
        }
    }
}
