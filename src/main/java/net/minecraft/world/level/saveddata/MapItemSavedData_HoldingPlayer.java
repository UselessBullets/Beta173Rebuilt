// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.saveddata;

import net.minecraft.world.entity.player.Player;

public class MapItemSavedData_HoldingPlayer
{
    public final Player player;
    public int[] rowsDirtyMin;
    public int[] rowsDirtyMax;
    private int tick;
    private int sendPosTick;
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
}
