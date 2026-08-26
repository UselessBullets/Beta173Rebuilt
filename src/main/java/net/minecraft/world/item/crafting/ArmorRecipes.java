// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item.crafting;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.item.Item;

public class ArmorRecipes
{
    private String[][] shapes = new String[][]
            {
                { "XXX",
                  "X X" },

                { "X X",
                  "XXX",
                  "XXX" },

                { "XXX",
                  "X X",
                  "X X" },

                { "X X",
                  "X X" }
            };
    private Object[][] map = new Object[][]
            {
                    { Item.leather, Tile.fire, Item.ironIngot, Item.diamond, Item.goldIngot },
                    { Item.helmet_cloth, Item.helmet_chain, Item.helmet_iron, Item.helmet_diamond, Item.helmet_gold },
                    { Item.chestplate_cloth, Item.chestplate_chain, Item.chestplate_iron, Item.chestplate_diamond, Item.chestplate_gold },
                    { Item.leggings_cloth, Item.leggings_chain, Item.leggings_iron, Item.leggings_diamond, Item.leggings_gold },
                    { Item.boots_cloth, Item.boots_chain, Item.boots_iron, Item.boots_diamond, Item.boots_gold }
            };
    
    public void addRecipes(final Recipes recipes) {
        for (int m = 0; m < this.map[0].length; ++m) {
            final Object objMaterial = this.map[0][m];
            for (int t = 0; t < this.map.length - 1; ++t) {
                recipes.addShapedRecipe(new ItemInstance((Item)this.map[t + 1][m]), this.shapes[t], 'X', objMaterial);
            }
        }
    }
}
