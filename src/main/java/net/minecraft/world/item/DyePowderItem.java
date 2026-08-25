// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.level.tile.ClothTile;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.tile.CropTile;
import net.minecraft.world.level.tile.Sapling;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;

public class DyePowderItem extends Item
{
    public static final String[] COLOR_DESCS = new String[] {
            "black",
            "red",
            "green",
            "brown",
            "blue",
            "purple",
            "cyan",
            "silver",
            "gray",
            "pink",
            "lime",
            "yellow",
            "lightBlue",
            "magenta",
            "orange",
            "white"
    };
    public static final int[] COLOR_RGB = new int[] {
            0x1e1b1b,
            0xb3312c,
            0x3b511a,
            0x51301a,
            0x253192,
            0x7b2fbe,
            0x287697,
            0x287697,
            0x434343,
            0xd88198,
            0x41cd34,
            0xdecf2a,
            0x6689d3,
            0xc354cd,
            0xeb8844,
            0xf0f0f0
    };

    public static final int BLACK = 0;
    public static final int RED = 1;
    public static final int GREEN = 2;
    public static final int BROWN = 3;
    public static final int BLUE = 4;
    public static final int PURPLE = 5;
    public static final int CYAN = 6;
    public static final int SILVER = 7;
    public static final int GRAY = 8;
    public static final int PINK = 9;
    public static final int LIME = 10;
    public static final int YELLOW = 11;
    public static final int LIGHT_BLUE = 12;
    public static final int MAGENTA = 13;
    public static final int ORANGE = 14;
    public static final int WHITE = 15;
    
    public DyePowderItem(final int id) {
        super(id);
        this.setStackedByData(true);
        this.setMaxDamage(0);
    }
    
    @Override
    public int getIcon(final int auxValue) {
        return this.icon + auxValue % 8 * 16 + auxValue / 8;
    }
    
    @Override
    public String getDescriptionId(final ItemInstance itemInstance) {
        return super.getDescriptionId() + "." + DyePowderItem.COLOR_DESCS[itemInstance.getAuxValue()];
    }
    
    @Override
    public boolean useOn(final ItemInstance itemInstance, final Player player, final Level level, final int x, final int y, final int z, final int face) {
        if (itemInstance.getAuxValue() == WHITE) {
            // bone meal is a fertilizer, so instantly grow trees and stuff
            final int tile = level.getTile(x, y, z);
            if (tile == Tile.sapling.id) {
                if (!level.isClientSide) {
                    ((Sapling)Tile.sapling).growTree(level, x, y, z, level.random);
                    itemInstance.count--;
                }
                return true;
            }
            if (tile == Tile.crops.id) {
                if (!level.isClientSide) {
                    ((CropTile)Tile.crops).growCropsToMax(level, x, y, z);
                    itemInstance.count--;
                }
                return true;
            }
            if (tile == Tile.grass.id) {
                if (!level.isClientSide) {
                    itemInstance.count--;
                    mainLoop:
                    for (int j = 0; j < 128; j++) {
                        int xx = x;
                        int yy = y + 1;
                        int zz = z;
                        for (int i = 0; i < j / 16; i++) {
                            xx += DyePowderItem.random.nextInt(3) - 1;
                            yy += (DyePowderItem.random.nextInt(3) - 1) * DyePowderItem.random.nextInt(3) / 2;
                            zz += DyePowderItem.random.nextInt(3) - 1;
                            if (level.getTile(xx, yy - 1, zz) != Tile.grass.id || level.isSolidBlockingTile(xx, yy, zz)) {
                                continue mainLoop;
                            }
                        }

                        if (level.getTile(xx, yy, zz) == 0) {
                            if (DyePowderItem.random.nextInt(10) != 0) {
                                level.setTileAndData(xx, yy, zz, Tile.tallgrass.id, 1);
                            }
                            else if (DyePowderItem.random.nextInt(3) != 0) {
                                level.setTile(xx, yy, zz, Tile.flower.id);
                            } else {
                                level.setTile(xx, yy, zz, Tile.rose.id);
                            }
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void interactEnemy(final ItemInstance itemInstance, final Mob mob) {
        if (mob instanceof Sheep) {
            final Sheep sheep = (Sheep)mob;
            // convert to tile-based color value (0 is white instead of black)
            final int newColor = ClothTile.getTileDataForItemAuxValue(itemInstance.getAuxValue());
            if (!sheep.isSheared() && sheep.getColor() != newColor) {
                sheep.setColor(newColor);
                itemInstance.count--;
            }
        }
    }


}
