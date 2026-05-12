// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.saveddata;

public class MapItemSavedData_MapDecoration
{
    public byte imgIndex;
    public byte x;
    public byte y;
    public byte rot;
    final /* synthetic */ MapItemSavedData data;
    
    public MapItemSavedData_MapDecoration(final MapItemSavedData data, final byte img, final byte x, final byte y, final byte rot) {
        this.data = data;
        this.imgIndex = img;
        this.x = x;
        this.y = y;
        this.rot = rot;
    }
}
