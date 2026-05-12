// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.tile.Tile;
import util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.MapItemSavedData;
import net.minecraft.world.level.Level;

public class MapItem extends ComplexItem
{
    protected MapItem(final int id) {
        super(id);
        this.setMaxStackSize(1);
    }
    
    public static MapItemSavedData getSavedData(final short idNum, final Level level) {
        new StringBuilder().append("map_").append(idNum).toString();
        MapItemSavedData data = (MapItemSavedData)level.getSavedData(MapItemSavedData.class, "map_" + idNum);
        if (data == null) {
            final String string = "map_" + level.getFreeAuxValueFor("map");
            data = new MapItemSavedData(string);
            level.setSavedData(string, data);
        }
        return data;
    }
    
    public MapItemSavedData getSavedData(final ItemInstance itemInstance, final Level level) {
        new StringBuilder().append("map_").append(itemInstance.getAuxValue()).toString();
        MapItemSavedData data = (MapItemSavedData)level.getSavedData(MapItemSavedData.class, "map_" + itemInstance.getAuxValue());
        if (data == null) {
            itemInstance.setAuxValue(level.getFreeAuxValueFor("map"));
            final String string = "map_" + itemInstance.getAuxValue();
            data = new MapItemSavedData(string);
            data.x = level.getLevelData().getXSpawn();
            data.z = level.getLevelData().getZSpawn();
            data.scale = 3;
            data.dimension = (byte)level.dimension.id;
            data.setDirty();
            level.setSavedData(string, data);
        }
        return data;
    }
    
    public void update(final Level level, final Entity player, final MapItemSavedData data) {
        if (level.dimension.id != data.dimension) {
            return;
        }
        final int n = 128;
        final int n2 = 128;
        final int n3 = 1 << data.scale;
        final int x = data.x;
        final int z = data.z;
        final int n4 = Mth.floor(player.x - x) / n3 + n / 2;
        final int n5 = Mth.floor(player.z - z) / n3 + n2 / 2;
        int n6 = 128 / n3;
        if (level.dimension.hasCeiling) {
            n6 /= 2;
        }
        ++data.step;
        for (int i = n4 - n6 + 1; i < n4 + n6; ++i) {
            if ((i & 0xF) == (data.step & 0xF)) {
                int y0 = 255;
                int y2 = 0;
                double n7 = 0.0;
                for (int j = n5 - n6 - 1; j < n5 + n6; ++j) {
                    if (i >= 0 && j >= -1 && i < n) {
                        if (j < n2) {
                            final int n8 = i - n4;
                            final int n9 = j - n5;
                            final boolean b = n8 * n8 + n9 * n9 > (n6 - 2) * (n6 - 2);
                            final int x2 = (x / n3 + i - n / 2) * n3;
                            final int z2 = (z / n3 + j - n2 / 2) * n3;
                            final int n10 = 0;
                            final int n11 = 0;
                            final int n12 = 0;
                            final int[] array = new int[256];
                            final LevelChunk chunk = level.getChunkAt(x2, z2);
                            final int n13 = x2 & 0xF;
                            final int n14 = z2 & 0xF;
                            int n15 = 0;
                            double n16 = 0.0;
                            if (level.dimension.hasCeiling) {
                                final int n17 = x2 + z2 * 231871;
                                if ((n17 * n17 * 31287121 + n17 * 11 >> 20 & 0x1) == 0x0) {
                                    final int[] array2 = array;
                                    final int id = Tile.dirt.id;
                                    array2[id] += 10;
                                }
                                else {
                                    final int[] array3 = array;
                                    final int id2 = Tile.rock.id;
                                    array3[id2] += 10;
                                }
                                n16 = 100.0;
                            }
                            else {
                                for (int k = 0; k < n3; ++k) {
                                    for (int l = 0; l < n3; ++l) {
                                        int n18 = chunk.getHeightmap(k + n13, l + n14) + 1;
                                        int n19 = 0;
                                        if (n18 > 1) {
                                            boolean b2;
                                            do {
                                                b2 = true;
                                                n19 = chunk.getTile(k + n13, n18 - 1, l + n14);
                                                if (n19 == 0) {
                                                    b2 = false;
                                                }
                                                else if (n18 > 0 && n19 > 0 && Tile.tiles[n19].material.color == MaterialColor.none) {
                                                    b2 = false;
                                                }
                                                if (!b2) {
                                                    --n18;
                                                    n19 = chunk.getTile(k + n13, n18 - 1, l + n14);
                                                }
                                            } while (!b2);
                                            if (n19 != 0 && Tile.tiles[n19].material.isLiquid()) {
                                                int n20 = n18 - 1;
                                                int tile;
                                                do {
                                                    tile = chunk.getTile(k + n13, n20--, l + n14);
                                                    ++n15;
                                                } while (n20 > 0 && tile != 0 && Tile.tiles[tile].material.isLiquid());
                                            }
                                        }
                                        n16 += n18 / (double)(n3 * n3);
                                        final int[] array4 = array;
                                        final int n21 = n19;
                                        ++array4[n21];
                                    }
                                }
                            }
                            final int n22 = n15 / (n3 * n3);
                            final int n23 = n10 / (n3 * n3);
                            final int n24 = n11 / (n3 * n3);
                            final int n25 = n12 / (n3 * n3);
                            int n26 = 0;
                            int n27 = 0;
                            for (int n28 = 0; n28 < 256; ++n28) {
                                if (array[n28] > n26) {
                                    n27 = n28;
                                    n26 = array[n28];
                                }
                            }
                            final double n29 = (n16 - n7) * 4.0 / (n3 + 4) + ((i + j & 0x1) - 0.5) * 0.4;
                            int n30 = 1;
                            if (n29 > 0.6) {
                                n30 = 2;
                            }
                            if (n29 < -0.6) {
                                n30 = 0;
                            }
                            int id3 = 0;
                            if (n27 > 0) {
                                final MaterialColor color = Tile.tiles[n27].material.color;
                                if (color == MaterialColor.water) {
                                    final double n31 = n22 * 0.1 + (i + j & 0x1) * 0.2;
                                    n30 = 1;
                                    if (n31 < 0.5) {
                                        n30 = 2;
                                    }
                                    if (n31 > 0.9) {
                                        n30 = 0;
                                    }
                                }
                                id3 = color.id;
                            }
                            n7 = n16;
                            if (j >= 0) {
                                if (n8 * n8 + n9 * n9 < n6 * n6) {
                                    if (!b || (i + j & 0x1) != 0x0) {
                                        final byte b3 = data.colors[i + j * n];
                                        final byte b4 = (byte)(id3 * 4 + n30);
                                        if (b3 != b4) {
                                            if (y0 > j) {
                                                y0 = j;
                                            }
                                            if (y2 < j) {
                                                y2 = j;
                                            }
                                            data.colors[i + j * n] = b4;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (y0 <= y2) {
                    data.setDirty(i, y0, y2);
                }
            }
        }
    }
    
    @Override
    public void inventoryTick(final ItemInstance itemInstance, final Level level, final Entity owner, final int slot, final boolean selected) {
        if (level.isClientSide) {
            return;
        }
        final MapItemSavedData savedData = this.getSavedData(itemInstance, level);
        if (owner instanceof Player) {
            savedData.tickCarriedBy((Player)owner, itemInstance);
        }
        if (selected) {
            this.update(level, owner, savedData);
        }
    }
    
    @Override
    public void onCraftedBy(final ItemInstance itemInstance, final Level level, final Player player) {
        itemInstance.setAuxValue(level.getFreeAuxValueFor("map"));
        final String string = "map_" + itemInstance.getAuxValue();
        final MapItemSavedData data = new MapItemSavedData(string);
        level.setSavedData(string, data);
        data.x = Mth.floor(player.x);
        data.z = Mth.floor(player.z);
        data.scale = 3;
        data.dimension = (byte)level.dimension.id;
        data.setDirty();
    }
}
