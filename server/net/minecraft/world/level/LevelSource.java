// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.entity.TileEntity;

public interface LevelSource
{
    int getTile(final int x, final int y, final int z);
    
    TileEntity getTileEntity(final int x, final int y, final int z);
    
    int getData(final int x, final int y, final int z);
    
    Material getMaterial(final int x, final int y, final int z);
    
    boolean isSolidBlockingTile(final int x, final int y, final int z);
}
