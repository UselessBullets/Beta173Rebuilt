// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item.crafting;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.tile.Tile;
import java.util.HashMap;
import java.util.Map;

public class FurnaceRecipes
{
    private static final FurnaceRecipes instance;
    private Map<Integer, ItemInstance> recipes;
    
    public static final FurnaceRecipes getInstance() {
        return FurnaceRecipes.instance;
    }
    
    private FurnaceRecipes() {
        this.recipes = new HashMap<>();
        this.addFurnaceRecipe(Tile.ironOre.id, new ItemInstance(Item.ironIngot));
        this.addFurnaceRecipe(Tile.goldOre.id, new ItemInstance(Item.goldIngot));
        this.addFurnaceRecipe(Tile.emeraldOre.id, new ItemInstance(Item.emerald));
        this.addFurnaceRecipe(Tile.sand.id, new ItemInstance(Tile.glass));
        this.addFurnaceRecipe(Item.porkChop_raw.id, new ItemInstance(Item.porkChop_cooked));
        this.addFurnaceRecipe(Item.fish_raw.id, new ItemInstance(Item.fish_cooked));
        this.addFurnaceRecipe(Tile.stoneBrick.id, new ItemInstance(Tile.rock));
        this.addFurnaceRecipe(Item.clay.id, new ItemInstance(Item.brick));
        this.addFurnaceRecipe(Tile.cactus.id, new ItemInstance(Item.dye_powder, 1, 2));
        this.addFurnaceRecipe(Tile.treeTrunk.id, new ItemInstance(Item.coal, 1, 1));
    }
    
    public void addFurnaceRecipe(final int itemId, final ItemInstance result) {
        this.recipes.put(itemId, result);
    }
    
    public ItemInstance getResult(final int itemId) {
        return this.recipes.get(itemId);
    }
    
    public Map<Integer, ItemInstance> getRecipies() {
        return this.recipes;
    }
    
    static {
        instance = new FurnaceRecipes();
    }
}
