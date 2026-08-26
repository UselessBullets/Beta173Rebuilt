// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item.crafting;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.tile.Tile;

public class WeaponRecipes
{
    private String[][] shapes = new String[][] {
            { "X",
              "X",
              "#" }
    };
    private Object[][] map = new Object[][] {
            { Tile.wood, Tile.stoneBrick, Item.ironIngot, Item.diamond, Item.goldIngot },
            { Item.sword_wood, Item.sword_stone, Item.sword_iron, Item.sword_diamond, Item.sword_gold }
    };
    
    public void addRecipes(final Recipes recipes) {
        for (int i = 0; i < this.map[0].length; ++i) {
            final Object o = this.map[0][i];
            for (int j = 0; j < this.map.length - 1; ++j) {
                recipes.addShapedRecipe(new ItemInstance((Item)this.map[j + 1][i]), this.shapes[j],
                        '#', Item.stick, 'X', o);
            }
        }
        recipes.addShapedRecipe(new ItemInstance(Item.bow, 1),
                " #X",
                "# X",
                " #X",

                'X', Item.string, '#', Item.stick);

        recipes.addShapedRecipe(new ItemInstance(Item.arrow, 4),
                "X",
                "#",
                "Y",

                'Y', Item.feather, 'X', Item.flint,
                '#', Item.stick);
    }
}
