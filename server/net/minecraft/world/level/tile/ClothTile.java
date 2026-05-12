// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.level.material.Material;

public class ClothTile extends Tile
{
    public ClothTile() {
        super(35, 64, Material.cloth);
    }
    
    @Override
    public int getTexture(final int face, int data) {
        if (data == 0) {
            return this.tex;
        }
        data = ~(data & 0xF);
        return 113 + ((data & 0x8) >> 3) + (data & 0x7) * 16;
    }
    
    @Override
    protected int getSpawnResourcesAuxValue(final int data) {
        return data;
    }
    
    public static int getTileDataForItemAuxValue(final int auxValue) {
        return ~auxValue & 0xF;
    }
    
    public static int getItemAuxValueForTileData(final int data) {
        return ~data & 0xF;
    }
}
