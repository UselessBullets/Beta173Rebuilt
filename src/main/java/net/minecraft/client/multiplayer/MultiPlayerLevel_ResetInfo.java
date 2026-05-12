// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.multiplayer;

class MultiPlayerLevel_ResetInfo
{
    int x;
    int y;
    int z;
    int ticks;
    int tile;
    int data;
    final /* synthetic */ MultiPlayerLevel mpLevel;
    
    public MultiPlayerLevel_ResetInfo(final MultiPlayerLevel mpLevel, final int x, final int y, final int z, final int tile, final int data) {
        this.mpLevel = mpLevel;
        this.x = x;
        this.y = y;
        this.z = z;
        this.ticks = 80;
        this.tile = tile;
        this.data = data;
    }
}
