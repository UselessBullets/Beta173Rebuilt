// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.level.tile.PistonBaseTile;

public class PistonTileItem extends TileItem
{
    public PistonTileItem(final int id) {
        super(id);
    }
    
    @Override
    public int getLevelDataForAuxValue(final int auxValue) {
        return PistonBaseTile.UNDEFINED_FACING;
    }
}
