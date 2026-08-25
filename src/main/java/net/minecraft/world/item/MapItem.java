// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.network.packet.ComplexItemDataPacket;
import net.minecraft.network.packet.Packet;
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
    public static final int IMAGE_WIDTH = 128;
    public static final int IMAGE_HEIGHT = 128;
    protected MapItem(final int id) {
        super(id);
        this.setMaxStackSize(1);
    }
    
    public static MapItemSavedData getSavedData(final short idNum, final Level level) {
        String id = "map_" + idNum;
        MapItemSavedData mapItemSavedData = (MapItemSavedData)level.getSavedData(MapItemSavedData.class, id);
        if (mapItemSavedData == null) {
            int aux = level.getFreeAuxValueFor("map");

            id = "map_" + aux;
            mapItemSavedData = new MapItemSavedData(id);

            level.setSavedData(id, mapItemSavedData);
        }
        return mapItemSavedData;
    }
    
    public MapItemSavedData getSavedData(final ItemInstance itemInstance, final Level level) {
        String id = "map_" + itemInstance.getAuxValue();
        MapItemSavedData mapItemSavedData = (MapItemSavedData)level.getSavedData(MapItemSavedData.class, id);

        if (mapItemSavedData == null) {
            itemInstance.setAuxValue(level.getFreeAuxValueFor("map"));

            id = "map_" + itemInstance.getAuxValue();
            mapItemSavedData = new MapItemSavedData(id);

            mapItemSavedData.x = level.getLevelData().getXSpawn();
            mapItemSavedData.z = level.getLevelData().getZSpawn();
            mapItemSavedData.scale = 3;
            mapItemSavedData.dimension = (byte)level.dimension.id;

            mapItemSavedData.setDirty();

            level.setSavedData(id, mapItemSavedData);
        }
        return mapItemSavedData;
    }
    
    public void update(final Level level, final Entity player, final MapItemSavedData data) {
        if (level.dimension.id != data.dimension) {
            // Wrong dimension, abort
            return;
        }

        final int w = MapItem.IMAGE_WIDTH;
        final int h = MapItem.IMAGE_HEIGHT;

        final int scale = 1 << data.scale;

        final int xo = data.x;
        final int zo = data.z;

        final int xp = Mth.floor(player.x - xo) / scale + w / 2;
        final int zp = Mth.floor(player.z - zo) / scale + h / 2;

        int rad = 128 / scale;
        if (level.dimension.hasCeiling) {
            rad /= 2;
        }
        data.step++;

        for (int x = xp - rad + 1; x < xp + rad; ++x) {
            if ((x & 0xF) != (data.step & 0xF)) continue;

            int yd0 = 255;
            int yd1 = 0;

            double ho = 0.0;
            for (int z = zp - rad - 1; z < zp + rad; ++z) {
                if (x < 0 || z < -1 || x >= w || z >= h) continue;

                final int xd = x - xp;
                final int zd = z - zp;

                final boolean ditherBlack = xd * xd + zd * zd > (rad - 2) * (rad - 2);

                final int xx = (xo / scale + x - w / 2) * scale;
                final int zz = (zo / scale + z - h / 2) * scale;

                int r = 0;
                int g = 0;
                int b = 0;

                final int[] count = new int[Tile.TILE_NUM_COUNT];

                final LevelChunk lc = level.getChunkAt(xx, zz);
                final int xso = xx & 0xF;
                final int zso = zz & 0xF;
                int liquidDepth = 0;

                double hh = 0.0;
                if (level.dimension.hasCeiling) {
                    int ss = xx + zz * 231871;
                    ss = ss * ss * 31287121 + ss * 11;

                    if ((ss >> 20 & 0x1) == 0x0) count[Tile.dirt.id] += 10;
                    else count[Tile.rock.id] += 10;
                    hh = 100.0;
                }
                else {
                    for (int xs = 0; xs < scale; ++xs) {
                        for (int zs = 0; zs < scale; ++zs) {
                            int yy = lc.getHeightmap(xs + xso, zs + zso) + 1;
                            int t = 0;

                            if (yy > 1) {
                                boolean ok = false;
                                do {
                                    ok = true;
                                    t = lc.getTile(xs + xso, yy - 1, zs + zso);
                                    if (t == 0) ok = false;
                                    else if (yy > 0 && t > 0 && Tile.tiles[t].material.color == MaterialColor.none) {
                                        ok = false;
                                    }

                                    if (!ok) {
                                        yy--;
                                        t = lc.getTile(xs + xso, yy - 1, zs + zso);
                                    }
                                } while (!ok);

                                if (t != 0 && Tile.tiles[t].material.isLiquid()) {
                                    int y = yy - 1;
                                    int below;
                                    do {
                                        below = lc.getTile(xs + xso, y--, zs + zso);
                                        liquidDepth++;
                                    } while (y > 0 && below != 0 && Tile.tiles[below].material.isLiquid());
                                }
                            }
                            hh += yy / (double)(scale * scale);

                            count[t]++;
                        }
                    }
                }
                liquidDepth /= (scale * scale);
                r /= (scale * scale);
                g /= (scale * scale);
                b /= (scale * scale);

                int best = 0;
                int tBest = 0;
                for (int j = 0; j < 256; ++j) {
                    if (count[j] > best) {
                        tBest = j;
                        best = count[j];
                    }
                }

                double diff = (hh - ho) * 4.0 / (scale + 4) + ((x + z & 0x1) - 0.5) * 0.4;
                int br = 1;
                if (diff > 0.6) br = 2;
                if (diff < -0.6) br = 0;

                int col = 0;
                if (tBest > 0) {
                    final MaterialColor mc = Tile.tiles[tBest].material.color;
                    if (mc == MaterialColor.water) {
                        diff = liquidDepth * 0.1 + (x + z & 0x1) * 0.2;
                        br = 1;
                        if (diff < 0.5) br = 2;
                        if (diff > 0.9) br = 0;
                    }
                    col = mc.id;
                }

                ho = hh;

                if (z < 0) continue;
                if (xd * xd + zd * zd >= rad * rad) continue;
                if (ditherBlack && (((x + z) & 0x1) == 0)) {
                    continue;
                }
                final byte oldColor = data.colors[x + z * w];
                final byte newColor = (byte)(col * 4 + br);
                if (oldColor != newColor) {
                    if (yd0 > z) yd0 = z;
                    if (yd1 < z) yd1 = z;
                    data.colors[x + z * w] = newColor;
                }
            }
            if (yd0 <= yd1) {
                data.setDirty(x, yd0, yd1);
            }
        }
    }
    
    @Override
    public void inventoryTick(final ItemInstance itemInstance, final Level level, final Entity owner, final int slot, final boolean selected) {
        if (level.isClientSide) return;

        final MapItemSavedData data = this.getSavedData(itemInstance, level);
        if (owner instanceof Player) {
            data.tickCarriedBy((Player)owner, itemInstance);
        }

        if (selected) {
            this.update(level, owner, data);
        }
    }
    
    @Override
    public void onCraftedBy(final ItemInstance itemInstance, final Level level, final Player player) {
        itemInstance.setAuxValue(level.getFreeAuxValueFor("map"));

        final String id = "map_" + itemInstance.getAuxValue();
        final MapItemSavedData data = new MapItemSavedData(id);

        level.setSavedData(id, data);
        data.x = Mth.floor(player.x);
        data.z = Mth.floor(player.z);
        data.scale = 3;
        data.dimension = (byte)level.dimension.id;
        data.setDirty();
    }

    @Override
    public Packet getUpdatePacket(final ItemInstance itemInstance, final Level level, final Player player) {
        final byte[] data = this.getSavedData(itemInstance, level).getUpdatePacket(itemInstance, level, player);
        if (data == null) return null;
        return new ComplexItemDataPacket((short)Item.map.id, (short)itemInstance.getAuxValue(), data);
    }
}
