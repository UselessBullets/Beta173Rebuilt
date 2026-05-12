// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item.crafting;

import net.minecraft.world.CraftingContainer;
import java.util.HashMap;
import java.util.Comparator;
import java.util.Collections;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.Item;
import java.util.ArrayList;
import java.util.List;

public class Recipes
{
    private static final Recipes instance;
    private List<Recipe> recipes;
    
    public static final Recipes getInstance() {
        return Recipes.instance;
    }
    
    private Recipes() {
        this.recipes = new ArrayList<>();
        new ToolRecipes().addRecipes(this);
        new WeaponRecipes().addRecipes(this);
        new OreRecipes().addRecipes(this);
        new FoodRecipes().addRecipes(this);
        new StructureRecipes().addRecipes(this);
        new ArmorRecipes().addRecipes(this);
        new ClothDyeRecipes().addRecipes(this);
        this.addShapedRecipe(new ItemInstance(Item.paper, 3), "###", '#', Item.reeds);
        this.addShapedRecipe(new ItemInstance(Item.book, 1), "#", "#", "#", '#', Item.paper);
        this.addShapedRecipe(new ItemInstance(Tile.fence, 2), "###", "###", '#', Item.stick);
        this.addShapedRecipe(new ItemInstance(Tile.recordPlayer, 1), "###", "#X#", "###", '#', Tile.wood, 'X', Item.emerald);
        this.addShapedRecipe(new ItemInstance(Tile.musicBlock, 1), "###", "#X#", "###", '#', Tile.wood, 'X', Item.redStone);
        this.addShapedRecipe(new ItemInstance(Tile.bookshelf, 1), "###", "XXX", "###", '#', Tile.wood, 'X', Item.book);
        this.addShapedRecipe(new ItemInstance(Tile.snow, 1), "##", "##", '#', Item.snowBall);
        this.addShapedRecipe(new ItemInstance(Tile.clay, 1), "##", "##", '#', Item.clay);
        this.addShapedRecipe(new ItemInstance(Tile.redBrick, 1), "##", "##", '#', Item.brick);
        this.addShapedRecipe(new ItemInstance(Tile.lightGem, 1), "##", "##", '#', Item.yellowDust);
        this.addShapedRecipe(new ItemInstance(Tile.cloth, 1), "##", "##", '#', Item.string);
        this.addShapedRecipe(new ItemInstance(Tile.tnt, 1), "X#X", "#X#", "X#X", 'X', Item.sulphur, '#', Tile.sand);
        this.addShapedRecipe(new ItemInstance(Tile.stoneSlabHalf, 3, 3), "###", '#', Tile.stoneBrick);
        this.addShapedRecipe(new ItemInstance(Tile.stoneSlabHalf, 3, 0), "###", '#', Tile.rock);
        this.addShapedRecipe(new ItemInstance(Tile.stoneSlabHalf, 3, 1), "###", '#', Tile.sandStone);
        this.addShapedRecipe(new ItemInstance(Tile.stoneSlabHalf, 3, 2), "###", '#', Tile.wood);
        this.addShapedRecipe(new ItemInstance(Tile.ladder, 2), "# #", "###", "# #", '#', Item.stick);
        this.addShapedRecipe(new ItemInstance(Item.door_wood, 1), "##", "##", "##", '#', Tile.wood);
        this.addShapedRecipe(new ItemInstance(Tile.trapdoor, 2), "###", "###", '#', Tile.wood);
        this.addShapedRecipe(new ItemInstance(Item.door_iron, 1), "##", "##", "##", '#', Item.ironIngot);
        this.addShapedRecipe(new ItemInstance(Item.sign, 1), "###", "###", " X ", '#', Tile.wood, 'X', Item.stick);
        this.addShapedRecipe(new ItemInstance(Item.cake, 1), "AAA", "BEB", "CCC", 'A', Item.milk, 'B', Item.sugar, 'C', Item.wheat, 'E', Item.egg);
        this.addShapedRecipe(new ItemInstance(Item.sugar, 1), "#", '#', Item.reeds);
        this.addShapedRecipe(new ItemInstance(Tile.wood, 4), "#", '#', Tile.treeTrunk);
        this.addShapedRecipe(new ItemInstance(Item.stick, 4), "#", "#", '#', Tile.wood);
        this.addShapedRecipe(new ItemInstance(Tile.torch, 4), "X", "#", 'X', Item.coal, '#', Item.stick);
        this.addShapedRecipe(new ItemInstance(Tile.torch, 4), "X", "#", 'X', new ItemInstance(Item.coal, 1, 1), '#', Item.stick);
        this.addShapedRecipe(new ItemInstance(Item.bowl, 4), "# #", " # ", '#', Tile.wood);
        this.addShapedRecipe(new ItemInstance(Tile.rail, 16), "X X", "X#X", "X X", 'X', Item.ironIngot, '#', Item.stick);
        this.addShapedRecipe(new ItemInstance(Tile.goldenRail, 6), "X X", "X#X", "XRX", 'X', Item.goldIngot, 'R', Item.redStone, '#', Item.stick);
        this.addShapedRecipe(new ItemInstance(Tile.detectorRail, 6), "X X", "X#X", "XRX", 'X', Item.ironIngot, 'R', Item.redStone, '#', Tile.pressurePlate_stone);
        this.addShapedRecipe(new ItemInstance(Item.minecart, 1), "# #", "###", '#', Item.ironIngot);
        this.addShapedRecipe(new ItemInstance(Tile.litPumpkin, 1), "A", "B", 'A', Tile.pumpkin, 'B', Tile.torch);
        this.addShapedRecipe(new ItemInstance(Item.minecart_chest, 1), "A", "B", 'A', Tile.chest, 'B', Item.minecart);
        this.addShapedRecipe(new ItemInstance(Item.minecart_furnace, 1), "A", "B", 'A', Tile.furnace, 'B', Item.minecart);
        this.addShapedRecipe(new ItemInstance(Item.boat, 1), "# #", "###", '#', Tile.wood);
        this.addShapedRecipe(new ItemInstance(Item.bucket_empty, 1), "# #", " # ", '#', Item.ironIngot);
        this.addShapedRecipe(new ItemInstance(Item.flintAndSteel, 1), "A ", " B", 'A', Item.ironIngot, 'B', Item.flint);
        this.addShapedRecipe(new ItemInstance(Item.bread, 1), "###", '#', Item.wheat);
        this.addShapedRecipe(new ItemInstance(Tile.stairs_wood, 4), "#  ", "## ", "###", '#', Tile.wood);
        this.addShapedRecipe(new ItemInstance(Item.fishingRod, 1), "  #", " #X", "# X", '#', Item.stick, 'X', Item.string);
        this.addShapedRecipe(new ItemInstance(Tile.stairs_stone, 4), "#  ", "## ", "###", '#', Tile.stoneBrick);
        this.addShapedRecipe(new ItemInstance(Item.painting, 1), "###", "#X#", "###", '#', Item.stick, 'X', Tile.cloth);
        this.addShapedRecipe(new ItemInstance(Item.apple_gold, 1), "###", "#X#", "###", '#', Tile.goldBlock, 'X', Item.apple);
        this.addShapedRecipe(new ItemInstance(Tile.lever, 1), "X", "#", '#', Tile.stoneBrick, 'X', Item.stick);
        this.addShapedRecipe(new ItemInstance(Tile.notGate_on, 1), "X", "#", '#', Item.stick, 'X', Item.redStone);
        this.addShapedRecipe(new ItemInstance(Item.diode, 1), "#X#", "III", '#', Tile.notGate_on, 'X', Item.redStone, 'I', Tile.rock);
        this.addShapedRecipe(new ItemInstance(Item.clock, 1), " # ", "#X#", " # ", '#', Item.goldIngot, 'X', Item.redStone);
        this.addShapedRecipe(new ItemInstance(Item.compass, 1), " # ", "#X#", " # ", '#', Item.ironIngot, 'X', Item.redStone);
        this.addShapedRecipe(new ItemInstance(Item.map, 1), "###", "#X#", "###", '#', Item.paper, 'X', Item.compass);
        this.addShapedRecipe(new ItemInstance(Tile.button, 1), "#", "#", '#', Tile.rock);
        this.addShapedRecipe(new ItemInstance(Tile.pressurePlate_stone, 1), "##", '#', Tile.rock);
        this.addShapedRecipe(new ItemInstance(Tile.pressurePlate_wood, 1), "##", '#', Tile.wood);
        this.addShapedRecipe(new ItemInstance(Tile.dispenser, 1), "###", "#X#", "#R#", '#', Tile.stoneBrick, 'X', Item.bow, 'R', Item.redStone);
        this.addShapedRecipe(new ItemInstance(Tile.pistonBase, 1), "TTT", "#X#", "#R#", '#', Tile.stoneBrick, 'X', Item.ironIngot, 'R', Item.redStone, 'T', Tile.wood);
        this.addShapedRecipe(new ItemInstance(Tile.pistonStickyBase, 1), "S", "P", 'S', Item.slimeBall, 'P', Tile.pistonBase);
        this.addShapedRecipe(new ItemInstance(Item.bed, 1), "###", "XXX", '#', Tile.cloth, 'X', Tile.wood);
        Collections.sort(this.recipes, new RecipeSorter(this));
        System.out.println(this.recipes.size() + " recipes");
    }
    
    void addShapedRecipe(final ItemInstance result, final Object... args) {
        String s = "";
        int i = 0;
        int width = 0;
        int height = 0;
        if (args[i] instanceof String[]) {
            final String[] array = (String[])args[i++];
            for (int j = 0; j < array.length; ++j) {
                final String str = array[j];
                ++height;
                width = str.length();
                s += str;
            }
        }
        else {
            while (args[i] instanceof String) {
                final String str2 = (String)args[i++];
                ++height;
                width = str2.length();
                s += str2;
            }
        }
        final HashMap<Character, Object> hashMap = new HashMap<>();
        while (i < args.length) {
            final Character c = (Character)args[i];
            Object o = null;
            if (args[i + 1] instanceof Item) {
                o = new ItemInstance((Item)args[i + 1]);
            }
            else if (args[i + 1] instanceof Tile) {
                o = new ItemInstance((Tile)args[i + 1], 1, -1);
            }
            else if (args[i + 1] instanceof ItemInstance) {
                o = args[i + 1];
            }
            hashMap.put(c, o);
            i += 2;
        }
        final ItemInstance[] recipeItems = new ItemInstance[width * height];
        for (int k = 0; k < width * height; ++k) {
            final char char1 = s.charAt(k);
            if (hashMap.containsKey(char1)) {
                recipeItems[k] = ((ItemInstance)hashMap.get(char1)).copy();
            }
            else {
                recipeItems[k] = null;
            }
        }
        this.recipes.add(new ShapedRecipe(width, height, recipeItems, result));
    }
    
    void addShapelessRecipe(final ItemInstance result, final Object... args) {
        final ArrayList<ItemInstance> ingredients = new ArrayList<>();
        for (final Object o : args) {
            if (o instanceof ItemInstance) {
                ingredients.add(((ItemInstance)o).copy());
            }
            else if (o instanceof Item) {
                ingredients.add(new ItemInstance((Item)o));
            }
            else {
                if (!(o instanceof Tile)) {
                    throw new RuntimeException("Invalid shapeless recipy!");
                }
                ingredients.add(new ItemInstance((Tile)o));
            }
        }
        this.recipes.add(new ShapelessRecipe(result, ingredients));
    }
    
    public ItemInstance getItemFor(final CraftingContainer craftSlots) {
        for (int i = 0; i < this.recipes.size(); ++i) {
            final Recipe recipe = this.recipes.get(i);
            if (recipe.matches(craftSlots)) {
                return recipe.assemble(craftSlots);
            }
        }
        return null;
    }
    
    public List<Recipe> getRecipes() {
        return this.recipes;
    }
    
    static {
        instance = new Recipes();
    }

    static class RecipeSorter implements Comparator<Recipe>
    {
        final /* synthetic */ Recipes recipes;

        RecipeSorter(final Recipes recipes) {
            this.recipes = recipes;
        }

        public int compare(final Recipe r0, final Recipe r1) {
            if (r0 instanceof ShapelessRecipe && r1 instanceof ShapedRecipe) {
                return 1;
            }
            if (r1 instanceof ShapelessRecipe && r0 instanceof ShapedRecipe) {
                return -1;
            }
            if (r1.size() < r0.size()) {
                return -1;
            }
            if (r1.size() > r0.size()) {
                return 1;
            }
            return 0;
        }
    }
}
