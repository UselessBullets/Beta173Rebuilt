// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.saveddata;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.entity.player.Player;

public class MapItemSavedData_HoldingPlayer
{
    public final Player player;
    public int[] rowsDirtyMin;
    public int[] rowsDirtyMax;
    private int tick;
    private int sendPosTick;
    private byte[] lastSentDecorations;
    final /* synthetic */ MapItemSavedData data;
    
    public MapItemSavedData_HoldingPlayer(final MapItemSavedData data, final Player player) {
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
                final MapItemSavedData_MapDecoration mapItemSavedData_MapDecoration = this.data.decorations.get(i);
                lastSentDecorations[i * 3 + 1] = (byte)(mapItemSavedData_MapDecoration.imgIndex + (mapItemSavedData_MapDecoration.rot & 0xF) * 16);
                lastSentDecorations[i * 3 + 2] = mapItemSavedData_MapDecoration.x;
                lastSentDecorations[i * 3 + 3] = mapItemSavedData_MapDecoration.y;
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
