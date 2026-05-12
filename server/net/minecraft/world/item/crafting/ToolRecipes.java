// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item.crafting;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.tile.Tile;

public class ToolRecipes
{
    private String[][] shapes;
    private Object[][] map;
    
    public ToolRecipes() {
        this.shapes = new String[][] { { "XXX", " # ", " # " }, { "X", "#", "#" }, { "XX", "X#", " #" }, { "XX", " #", " #" } };
        this.map = new Object[][] { { Tile.wood, Tile.stoneBrick, Item.ironIngot, Item.emerald, Item.goldIngot }, { Item.pickAxe_wood, Item.pickAxe_stone, Item.pickAxe_iron, Item.pickAxe_emerald, Item.pickAxe_gold }, { Item.shovel_wood, Item.shovel_stone, Item.shovel_item, Item.shovel_emerald, Item.shovel_gold }, { Item.hatchet_wood, Item.hatchet_stone, Item.hatchet_iron, Item.hatchet_emerald, Item.hatchet_gold }, { Item.hoe_wood, Item.hoe_stone, Item.hoe_iron, Item.hoe_emerald, Item.hoe_gold } };
    }
    
    public void addRecipes(final Recipes recipes) {
        for (int i = 0; i < this.map[0].length; ++i) {
            final Object o = this.map[0][i];
            for (int j = 0; j < this.map.length - 1; ++j) {
                recipes.addShapedRecipe(new ItemInstance((Item)this.map[j + 1][i]), this.shapes[j], '#', Item.stick, 'X', o);
            }
        }
        recipes.addShapedRecipe(new ItemInstance(Item.shears), " #", "# ", '#', Item.ironIngot);
    }
}
