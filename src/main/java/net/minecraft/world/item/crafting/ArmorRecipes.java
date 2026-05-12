// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item.crafting;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.item.Item;

public class ArmorRecipes
{
    private String[][] shapes;
    private Object[][] map;
    
    public ArmorRecipes() {
        this.shapes = new String[][] { { "XXX", "X X" }, { "X X", "XXX", "XXX" }, { "XXX", "X X", "X X" }, { "X X", "X X" } };
        this.map = new Object[][] { { Item.leather, Tile.fire, Item.ironIngot, Item.emerald, Item.goldIngot }, { Item.helmet_cloth, Item.helmet_chain, Item.helmet_iron, Item.helmet_emerald, Item.helmet_gold }, { Item.chestplate_cloth, Item.chestplate_chain, Item.chestplate_iron, Item.chestplate_emerald, Item.chestplate_gold }, { Item.leggings_cloth, Item.leggings_chain, Item.leggings_iron, Item.leggings_emerald, Item.leggings_gold }, { Item.boots_cloth, Item.boots_chain, Item.boots_iron, Item.boots_emerald, Item.boots_gold } };
    }
    
    public void addRecipes(final Recipes recipes) {
        for (int i = 0; i < this.map[0].length; ++i) {
            final Object o = this.map[0][i];
            for (int j = 0; j < this.map.length - 1; ++j) {
                recipes.addShapedRecipe(new ItemInstance((Item)this.map[j + 1][i]), this.shapes[j], 'X', o);
            }
        }
    }
}
