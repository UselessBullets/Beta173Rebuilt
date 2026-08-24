// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item.crafting;

import net.minecraft.world.item.DyePowderItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.tile.ClothTile;
import net.minecraft.world.level.tile.Tile;

public class ClothDyeRecipes
{
    public void addRecipes(final Recipes recipes) {
        for (int i = 0; i < 16; ++i) {
            recipes.addShapelessRecipe(new ItemInstance(Tile.cloth, 1, ClothTile.getItemAuxValueForTileData(i)),
                    new ItemInstance(Item.dye_powder, 1, i),
                    new ItemInstance(Item.items[Tile.cloth.id], 1, 15 - DyePowderItem.WHITE));
        }

        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 2, DyePowderItem.YELLOW),
                Tile.flower);

        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 2, DyePowderItem.RED),
                Tile.rose);

        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 3, DyePowderItem.WHITE),
                Item.bone);

        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 2, DyePowderItem.PINK),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.RED),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.WHITE));

        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 2, DyePowderItem.ORANGE),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.RED),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.YELLOW));

        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 2, DyePowderItem.LIME),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.GREEN),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.WHITE));

        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 2, DyePowderItem.GRAY),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.BLACK),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.WHITE));

        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 2, DyePowderItem.SILVER),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.GRAY),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.WHITE));

        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 3, DyePowderItem.SILVER),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.BLACK),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.WHITE),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.WHITE));

        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 2, DyePowderItem.LIGHT_BLUE),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.BLUE),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.WHITE));

        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 2, DyePowderItem.CYAN),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.BLUE),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.GREEN));

        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 2, DyePowderItem.PURPLE),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.BLUE),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.RED));

        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 2, DyePowderItem.MAGENTA),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.PURPLE),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.PINK));

        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 3, DyePowderItem.MAGENTA),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.BLUE),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.RED),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.PINK));

        recipes.addShapelessRecipe(new ItemInstance(Item.dye_powder, 4, DyePowderItem.MAGENTA),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.BLUE),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.RED),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.RED),
                new ItemInstance(Item.dye_powder, 1, DyePowderItem.WHITE));
    }
}
