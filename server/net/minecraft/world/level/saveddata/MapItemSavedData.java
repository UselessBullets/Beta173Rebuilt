// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.saveddata;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.entity.player.Player;
import com.mojang.nbt.CompoundTag;
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
    public List carriedBy;
    private Map carriedByPlayers;
    public List decorations;
    
    public MapItemSavedData(final String id) {
        super(id);
        this.colors = new byte[16384];
        this.carriedBy = new ArrayList();
        this.carriedByPlayers = new HashMap();
        this.decorations = new ArrayList();
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
            final MapItemSavedData_HoldingPlayer mapItemSavedData_HoldingPlayer = new MapItemSavedData_HoldingPlayer(this, player);
            this.carriedByPlayers.put(player, mapItemSavedData_HoldingPlayer);
            this.carriedBy.add(mapItemSavedData_HoldingPlayer);
        }
        this.decorations.clear();
        for (int i = 0; i < this.carriedBy.size(); ++i) {
            final MapItemSavedData_HoldingPlayer mapItemSavedData_HoldingPlayer2 = this.carriedBy.get(i);
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
                        this.decorations.add(new MapItemSavedData_MapDecoration(this, img, x, y, rot));
                    }
                }
            }
        }
    }
    
    public byte[] getUpdatePacket(final ItemInstance itemInstance, final Level level, final Player player) {
        final MapItemSavedData_HoldingPlayer mapItemSavedData_HoldingPlayer = this.carriedByPlayers.get(player);
        if (mapItemSavedData_HoldingPlayer == null) {
            return null;
        }
        return mapItemSavedData_HoldingPlayer.nextUpdatePacket(itemInstance);
    }
    
    public void setDirty(final int x, final int y0, final int y1) {
        super.setDirty();
        for (int i = 0; i < this.carriedBy.size(); ++i) {
            final MapItemSavedData_HoldingPlayer mapItemSavedData_HoldingPlayer = this.carriedBy.get(i);
            if (mapItemSavedData_HoldingPlayer.rowsDirtyMin[x] < 0 || mapItemSavedData_HoldingPlayer.rowsDirtyMin[x] > y0) {
                mapItemSavedData_HoldingPlayer.rowsDirtyMin[x] = y0;
            }
            if (mapItemSavedData_HoldingPlayer.rowsDirtyMax[x] < 0 || mapItemSavedData_HoldingPlayer.rowsDirtyMax[x] < y1) {
                mapItemSavedData_HoldingPlayer.rowsDirtyMax[x] = y1;
            }
        }
    }
}
