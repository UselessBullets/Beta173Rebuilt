// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.saveddata;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.entity.player.Player;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class MapItemSavedData extends SavedData
{
    public int x;
    public int z;
    public byte dimension;
    public byte scale;
    public byte[] colors;
    public int step;
    public List<HoldingPlayer> carriedBy;
    private Map<Player, HoldingPlayer> carriedByPlayers;
    public List<MapDecoration> decorations;
    
    public MapItemSavedData(final String id) {
        super(id);
        this.colors = new byte[16384];
        this.carriedBy = new ArrayList<>();
        this.carriedByPlayers = new HashMap<>();
        this.decorations = new ArrayList<>();
    }
    
    @Override
    public void load(final CompoundTag compoundTag) {
        this.dimension = compoundTag.getByte("dimension");
        this.x = compoundTag.getInt("xCenter");
        this.z = compoundTag.getInt("zCenter");
        this.scale = compoundTag.getByte("scale");
        if (this.scale < 0) {
            this.scale = 0;
        }
        if (this.scale > 4) {
            this.scale = 4;
        }
        final short short1 = compoundTag.getShort("width");
        final short short2 = compoundTag.getShort("height");
        if (short1 == 128 && short2 == 128) {
            this.colors = compoundTag.getByteArray("colors");
        }
        else {
            final byte[] byteArray = compoundTag.getByteArray("colors");
            this.colors = new byte[16384];
            final int n = (128 - short1) / 2;
            final int n2 = (128 - short2) / 2;
            for (short n3 = 0; n3 < short2; ++n3) {
                final int n4 = n3 + n2;
                if (n4 >= 0 || n4 < 128) {
                    for (short n5 = 0; n5 < short1; ++n5) {
                        final int n6 = n5 + n;
                        if (n6 >= 0 || n6 < 128) {
                            this.colors[n6 + n4 * 128] = byteArray[n5 + n3 * short1];
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void save(final CompoundTag compoundTag) {
        compoundTag.putByte("dimension", this.dimension);
        compoundTag.putInt("xCenter", this.x);
        compoundTag.putInt("zCenter", this.z);
        compoundTag.putByte("scale", this.scale);
        compoundTag.putShort("width", (short)128);
        compoundTag.putShort("height", (short)128);
        compoundTag.putByteArray("colors", this.colors);
    }
    
    public void tickCarriedBy(final Player player, final ItemInstance item) {
        if (!this.carriedByPlayers.containsKey(player)) {
            final HoldingPlayer mapItemSavedData_HoldingPlayer = new HoldingPlayer(this, player);
            this.carriedByPlayers.put(player, mapItemSavedData_HoldingPlayer);
            this.carriedBy.add(mapItemSavedData_HoldingPlayer);
        }
        this.decorations.clear();
        for (int i = 0; i < this.carriedBy.size(); ++i) {
            final HoldingPlayer mapItemSavedData_HoldingPlayer2 = this.carriedBy.get(i);
            if (mapItemSavedData_HoldingPlayer2.player.removed || !mapItemSavedData_HoldingPlayer2.player.inventory.contains(item)) {
                this.carriedByPlayers.remove(mapItemSavedData_HoldingPlayer2.player);
                this.carriedBy.remove(mapItemSavedData_HoldingPlayer2);
            }
            else {
                final float n = (float)(mapItemSavedData_HoldingPlayer2.player.x - this.x) / (1 << this.scale);
                final float n2 = (float)(mapItemSavedData_HoldingPlayer2.player.z - this.z) / (1 << this.scale);
                final int n3 = 64;
                final int n4 = 64;
                if (n >= -n3 && n2 >= -n4 && n <= n3 && n2 <= n4) {
                    final byte img = 0;
                    final byte x = (byte)(n * 2.0f + 0.5);
                    final byte y = (byte)(n2 * 2.0f + 0.5);
                    byte rot = (byte)(player.yRot * 16.0f / 360.0f + 0.5);
                    if (this.dimension < 0) {
                        final int n5 = this.step / 10;
                        rot = (byte)(n5 * n5 * 34187121 + n5 * 121 >> 15 & 0xF);
                    }
                    if (mapItemSavedData_HoldingPlayer2.player.dimension == this.dimension) {
                        this.decorations.add(new MapDecoration(this, img, x, y, rot));
                    }
                }
            }
        }
    }

    public byte[] getUpdatePacket(final ItemInstance itemInstance, final Level level, final Player player) {
        final HoldingPlayer holdingPlayer = this.carriedByPlayers.get(player);
        if (holdingPlayer == null) {
            return null;
        }
        return holdingPlayer.nextUpdatePacket(itemInstance);
    }
    
    public void setDirty(final int x, final int y0, final int y1) {
        super.setDirty();
        for (int i = 0; i < this.carriedBy.size(); ++i) {
            final HoldingPlayer mapItemSavedData_HoldingPlayer = this.carriedBy.get(i);
            if (mapItemSavedData_HoldingPlayer.rowsDirtyMin[x] < 0 || mapItemSavedData_HoldingPlayer.rowsDirtyMin[x] > y0) {
                mapItemSavedData_HoldingPlayer.rowsDirtyMin[x] = y0;
            }
            if (mapItemSavedData_HoldingPlayer.rowsDirtyMax[x] < 0 || mapItemSavedData_HoldingPlayer.rowsDirtyMax[x] < y1) {
                mapItemSavedData_HoldingPlayer.rowsDirtyMax[x] = y1;
            }
        }
    }
    
    public void handleComplexItemData(final byte[] data) {
        if (data[0] == 0) {
            final int n = data[1] & 0xFF;
            final int n2 = data[2] & 0xFF;
            for (int i = 0; i < data.length - 3; ++i) {
                this.colors[(i + n2) * 128 + n] = data[i + 3];
            }
            this.setDirty();
        }
        else if (data[0] == 1) {
            this.decorations.clear();
            for (int j = 0; j < (data.length - 1) / 3; ++j) {
                this.decorations.add(new MapDecoration(this, (byte)(data[j * 3 + 1] % 16), data[j * 3 + 2], data[j * 3 + 3], (byte)(data[j * 3 + 1] / 16)));
            }
        }
    }

    public static class MapDecoration
    {
        public byte imgIndex;
        public byte x;
        public byte y;
        public byte rot;
        final /* synthetic */ MapItemSavedData data;

        public MapDecoration(final MapItemSavedData data, final byte img, final byte x, final byte y, final byte rot) {
            this.data = data;
            this.imgIndex = img;
            this.x = x;
            this.y = y;
            this.rot = rot;
        }
    }

    public static class HoldingPlayer
    {
        public final Player player;
        public int[] rowsDirtyMin;
        public int[] rowsDirtyMax;
        private int tick;
        private int sendPosTick;
        private byte[] lastSentDecorations;
        final /* synthetic */ MapItemSavedData data;

        public HoldingPlayer(final MapItemSavedData data, final Player player) {
            this.data = data;
            this.rowsDirtyMin = new int[128];
            this.rowsDirtyMax = new int[128];
            this.tick = 0;
            this.sendPosTick = 0;
            this.player = player;
            for (int i = 0; i < this.rowsDirtyMin.length; ++i) {
                this.rowsDirtyMin[i] = 0;
                this.rowsDirtyMax[i] = 127;
            }
        }

        public byte[] nextUpdatePacket(final ItemInstance itemInstance) {
            final int sendPosTick = this.sendPosTick - 1;
            this.sendPosTick = sendPosTick;
            if (sendPosTick < 0) {
                this.sendPosTick = 4;
                final byte[] lastSentDecorations = new byte[this.data.decorations.size() * 3 + 1];
                lastSentDecorations[0] = 1;
                for (int i = 0; i < this.data.decorations.size(); ++i) {
                    final MapDecoration decoration = this.data.decorations.get(i);
                    lastSentDecorations[i * 3 + 1] = (byte)(decoration.imgIndex + (decoration.rot & 0xF) * 16);
                    lastSentDecorations[i * 3 + 2] = decoration.x;
                    lastSentDecorations[i * 3 + 3] = decoration.y;
                }
                boolean b = true;
                if (this.lastSentDecorations == null || this.lastSentDecorations.length != lastSentDecorations.length) {
                    b = false;
                }
                else {
                    for (int j = 0; j < lastSentDecorations.length; ++j) {
                        if (lastSentDecorations[j] != this.lastSentDecorations[j]) {
                            b = false;
                            break;
                        }
                    }
                }
                if (!b) {
                    return this.lastSentDecorations = lastSentDecorations;
                }
            }
            for (int k = 0; k < 10; ++k) {
                final int n = this.tick * 11 % 128;
                ++this.tick;
                if (this.rowsDirtyMin[n] >= 0) {
                    final int n2 = this.rowsDirtyMax[n] - this.rowsDirtyMin[n] + 1;
                    final int n3 = this.rowsDirtyMin[n];
                    final byte[] array = new byte[n2 + 3];
                    array[0] = 0;
                    array[1] = (byte)n;
                    array[2] = (byte)n3;
                    for (int l = 0; l < array.length - 3; ++l) {
                        array[l + 3] = this.data.colors[(l + n3) * 128 + n];
                    }
                    this.rowsDirtyMax[n] = -1;
                    this.rowsDirtyMin[n] = -1;
                    return array;
                }
            }
            return null;
        }
    }
}
