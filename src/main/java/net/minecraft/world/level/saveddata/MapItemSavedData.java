// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.saveddata;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.entity.player.Player;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class MapItemSavedData extends SavedData
{
    public static final int MAP_SIZE = 64;
    public static final int MAX_SCALE = 4;

    public int x, z;
    public byte dimension;
    public byte scale;
    public byte[] colors = new byte[MapItem.IMAGE_WIDTH * MapItem.IMAGE_HEIGHT];
    public int step;
    public List<HoldingPlayer> carriedBy = new ArrayList<>();
    private Map<Player, HoldingPlayer> carriedByPlayers = new HashMap<>();
    public List<MapDecoration> decorations = new ArrayList<>();
    
    public MapItemSavedData(final String id) {
        super(id);
    }
    
    @Override
    public void load(final CompoundTag tag) {
        this.dimension = tag.getByte("dimension");
        this.x = tag.getInt("xCenter");
        this.z = tag.getInt("zCenter");
        this.scale = tag.getByte("scale");
        if (this.scale < 0) this.scale = 0;
        if (this.scale > MAX_SCALE) this.scale = MAX_SCALE;

        final short width = tag.getShort("width");
        final short height = tag.getShort("height");
        if (width == MapItem.IMAGE_WIDTH && height == MapItem.IMAGE_HEIGHT) {
            this.colors = tag.getByteArray("colors");
        }
        else {
            final byte[] newColors = tag.getByteArray("colors");
            this.colors = new byte[MapItem.IMAGE_WIDTH * MapItem.IMAGE_HEIGHT];
            final int xo = (MapItem.IMAGE_WIDTH - width) / 2;
            final int yo = (MapItem.IMAGE_HEIGHT - height) / 2;
            for (short y = 0; y < height; ++y) {
                final int yt = y + yo;

                if (yt < 0 && yt >= MapItem.IMAGE_HEIGHT) continue;
                for (short x = 0; x < width; ++x) {
                    final int xt = x + xo;

                    if (xt < 0 && xt >= MapItem.IMAGE_WIDTH) continue;
                    this.colors[xt + yt * MapItem.IMAGE_WIDTH] = newColors[x + y * width];
                }
            }
        }
    }
    
    @Override
    public void save(final CompoundTag tag) {
        tag.putByte("dimension", this.dimension);
        tag.putInt("xCenter", this.x);
        tag.putInt("zCenter", this.z);
        tag.putByte("scale", this.scale);
        tag.putShort("width", (short)MapItem.IMAGE_WIDTH);
        tag.putShort("height", (short)MapItem.IMAGE_HEIGHT);
        tag.putByteArray("colors", this.colors);
    }
    
    public void tickCarriedBy(final Player player, final ItemInstance item) {
        if (!this.carriedByPlayers.containsKey(player)) {
            final HoldingPlayer hp = new HoldingPlayer(player);
            this.carriedByPlayers.put(player, hp);
            this.carriedBy.add(hp);
        }

        this.decorations.clear();
        for (int i = 0; i < this.carriedBy.size(); ++i) {
            final HoldingPlayer hp = this.carriedBy.get(i);
            if (hp.player.removed || !hp.player.inventory.contains(item)) {
                this.carriedByPlayers.remove(hp.player);
                this.carriedBy.remove(hp);
            }
            else {
                final float xd = (float)(hp.player.x - this.x) / (1 << this.scale);
                final float yd = (float)(hp.player.z - this.z) / (1 << this.scale);
                final int ww = MAP_SIZE;
                final int hh = MAP_SIZE;
                if (xd >= -ww && yd >= -hh && xd <= ww && yd <= hh) {
                    final byte img = 0;
                    final byte x = (byte)(xd * 2.0f + 0.5);
                    final byte y = (byte)(yd * 2.0f + 0.5);
                    byte rot = (byte)(player.yRot * 16.0f / 360.0f + 0.5);
                    if (this.dimension < 0) {
                        final int s = this.step / 10;
                        rot = (byte)(s * s * 34187121 + s * 121 >> 15 & 0xF);
                    }
                    if (hp.player.dimension == this.dimension) {
                        this.decorations.add(new MapDecoration(img, x, y, rot));
                    }
                }
            }
        }
    }

    public byte[] getUpdatePacket(final ItemInstance itemInstance, final Level level, final Player player) {
        final HoldingPlayer hp = this.carriedByPlayers.get(player);
        if (hp == null) return null;

        return hp.nextUpdatePacket(itemInstance);
    }
    
    public void setDirty(final int x, final int y0, final int y1) {
        super.setDirty();
        for (int i = 0; i < this.carriedBy.size(); ++i) {
            final HoldingPlayer hp = this.carriedBy.get(i);
            if (hp.rowsDirtyMin[x] < 0 || hp.rowsDirtyMin[x] > y0) hp.rowsDirtyMin[x] = y0;
            if (hp.rowsDirtyMax[x] < 0 || hp.rowsDirtyMax[x] < y1) hp.rowsDirtyMax[x] = y1;
        }
    }
    
    public void handleComplexItemData(final byte[] data) {
        if (data[0] == 0) {
            final int xx = data[1] & 0xFF;
            final int yy = data[2] & 0xFF;
            for (int i = 0; i < data.length - 3; ++i) {
                this.colors[(i + yy) * MapItem.IMAGE_WIDTH + xx] = data[i + 3];
            }
            this.setDirty();
        }
        else if (data[0] == 1) {
            this.decorations.clear();
            for (int i = 0; i < (data.length - 1) / 3; ++i) {
                byte img = (byte) (data[i * 3 + 1] % 16);
                byte x = data[i * 3 + 2];
                byte y = data[i * 3 + 3];
                byte rot = (byte) (data[i * 3 + 1] / 16);
                this.decorations.add(new MapDecoration(img, x, y, rot));
            }
        }
    }

    public class MapDecoration
    {
        public byte img, x, y, rot;

        public MapDecoration(final byte img, final byte x, final byte y, final byte rot) {
            this.img = img;
            this.x = x;
            this.y = y;
            this.rot = rot;
        }
    }

    public class HoldingPlayer
    {
        public final Player player;
        public int[] rowsDirtyMin = new int[MapItem.IMAGE_WIDTH];
        public int[] rowsDirtyMax = new int[MapItem.IMAGE_WIDTH];
        private int tick = 0;
        private int sendPosTick = 0;
        private byte[] lastSentDecorations;

        public HoldingPlayer(final Player player) {
            this.player = player;
            for (int i = 0; i < this.rowsDirtyMin.length; ++i) {
                this.rowsDirtyMin[i] = 0;
                this.rowsDirtyMax[i] = MapItem.IMAGE_HEIGHT - 1;
            }
        }

        public byte[] nextUpdatePacket(final ItemInstance itemInstance) {
            final int sendPosTick = this.sendPosTick - 1;
            this.sendPosTick = sendPosTick;
            if (sendPosTick < 0) {
                this.sendPosTick = 4;
                int playerDecorationSize = MapItemSavedData.this.decorations.size();
                final byte[] data = new byte[playerDecorationSize * 3 + 1];
                data[0] = 1;
                for (int i = 0; i < MapItemSavedData.this.decorations.size(); ++i) {
                    final MapDecoration md = MapItemSavedData.this.decorations.get(i);
                    data[i * 3 + 1] = (byte) (md.img | ((md.rot & 0xF) << 4));
                    data[i * 3 + 2] = md.x;
                    data[i * 3 + 3] = md.y;
                }

                boolean thesame = true;
                if (this.lastSentDecorations == null || this.lastSentDecorations.length != data.length) {
                    thesame = false;
                }
                else {
                    for (int i = 0; i < data.length; ++i) {
                        if (data[i] != this.lastSentDecorations[i]) {
                            thesame = false;
                            break;
                        }
                    }
                }
                if (!thesame) {
                    return this.lastSentDecorations = data;
                }
            }

            for (int d = 0; d < 10; ++d) {
                final int column = this.tick * 11 % MapItem.IMAGE_WIDTH;
                this.tick++;

                if (this.rowsDirtyMin[column] >= 0) {
                    final int len = this.rowsDirtyMax[column] - this.rowsDirtyMin[column] + 1;
                    final int min = this.rowsDirtyMin[column];

                    final byte[] data = new byte[len + 3];
                    data[0] = 0;
                    data[1] = (byte)column;
                    data[2] = (byte)min;
                    for (int y = 0; y < data.length - 3; ++y) {
                        data[y + 3] = MapItemSavedData.this.colors[(y + min) * MapItem.IMAGE_HEIGHT + column];
                    }
                    this.rowsDirtyMax[column] = -1;
                    this.rowsDirtyMin[column] = -1;
                    return data;
                }
            }
            return null;
        }
    }
}
