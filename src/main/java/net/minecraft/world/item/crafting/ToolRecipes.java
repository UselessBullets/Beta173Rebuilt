// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item.crafting;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.tile.Tile;

public class ToolRecipes
{
    private String[][] shapes = new String[][] {
            { "XXX",
              " # ",
              " # " },

            { "X",
              "#",
              "#" },

            { "XX",
              "X#",
              " #" },

            { "XX",
              " #",
              " #" } };
    private Object[][] map = new Object[][] {
            { Tile.wood, Tile.stoneBrick, Item.ironIngot, Item.emerald, Item.goldIngot },
            { Item.pickAxe_wood, Item.pickAxe_stone, Item.pickAxe_iron, Item.pickAxe_emerald, Item.pickAxe_gold },
            { Item.shovel_wood, Item.shovel_stone, Item.shovel_iron, Item.shovel_emerald, Item.shovel_gold },
            { Item.hatchet_wood, Item.hatchet_stone, Item.hatchet_iron, Item.hatchet_emerald, Item.hatchet_gold },
            { Item.hoe_wood, Item.hoe_stone, Item.hoe_iron, Item.hoe_emerald, Item.hoe_gold }
    };
    
    public void addRecipes(final Recipes recipes) {
        for (int m = 0; m < this.map[0].length; ++m) {
            final Object objMaterial = this.map[0][m];

            for (int t = 0; t < this.map.length - 1; ++t) {
                recipes.addShapedRecipe(new ItemInstance((Item)this.map[t + 1][m]), this.shapes[t],
                        '#', Item.stick, 'X', objMaterial);
            }
        }

        recipes.addShapedRecipe(new ItemInstance(Item.shears),
                " #",
                "# ",

                '#', Item.ironIngot);
    }
}
