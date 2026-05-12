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
    public static final String[] COLOR_DESCS;
    public static final int[] COLOR_RGB;
    
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
        if (itemInstance.getAuxValue() == 15) {
            final int tile = level.getTile(x, y, z);
            if (tile == Tile.sapling.id) {
                if (!level.isClientSide) {
                    ((Sapling)Tile.sapling).growTree(level, x, y, z, level.random);
                    --itemInstance.count;
                }
                return true;
            }
            if (tile == Tile.crops.id) {
                if (!level.isClientSide) {
                    ((CropTile)Tile.crops).growCropsToMax(level, x, y, z);
                    --itemInstance.count;
                }
                return true;
            }
            if (tile == Tile.grass.id) {
                if (!level.isClientSide) {
                    --itemInstance.count;
                    int i = 0;
                Label_0370_Outer:
                    while (i < 128) {
                        int n = x;
                        int y2 = y + 1;
                        int n2 = z;
                        int j = 0;
                        while (true) {
                            while (j < i / 16) {
                                n += DyePowderItem.random.nextInt(3) - 1;
                                y2 += (DyePowderItem.random.nextInt(3) - 1) * DyePowderItem.random.nextInt(3) / 2;
                                n2 += DyePowderItem.random.nextInt(3) - 1;
                                if (level.getTile(n, y2 - 1, n2) == Tile.grass.id) {
                                    if (!level.isSolidBlockingTile(n, y2, n2)) {
                                        ++j;
                                        continue Label_0370_Outer;
                                    }
                                }
                                ++i;
                                continue Label_0370_Outer;
                            }
                            if (level.getTile(n, y2, n2) != 0) {
                                continue;
                            }
                            if (DyePowderItem.random.nextInt(10) != 0) {
                                level.setTileAndData(n, y2, n2, Tile.tallgrass.id, 1);
                                continue;
                            }
                            if (DyePowderItem.random.nextInt(3) != 0) {
                                level.setTile(n, y2, n2, Tile.flower.id);
                                continue;
                            }
                            level.setTile(n, y2, n2, Tile.rose.id);
                            continue;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void interractEnemy(final ItemInstance itemInstance, final Mob mob) {
        if (mob instanceof Sheep) {
            final Sheep sheep = (Sheep)mob;
            final int tileDataForItemAuxValue = ClothTile.getTileDataForItemAuxValue(itemInstance.getAuxValue());
            if (!sheep.isSheared() && sheep.getColor() != tileDataForItemAuxValue) {
                sheep.setColor(tileDataForItemAuxValue);
                --itemInstance.count;
            }
        }
    }
    
    static {
        COLOR_DESCS = new String[] { "black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "lightBlue", "magenta", "orange", "white" };
        COLOR_RGB = new int[] { 1973019, 11743532, 3887386, 5320730, 2437522, 8073150, 2651799, 2651799, 4408131, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 15790320 };
    }
}
