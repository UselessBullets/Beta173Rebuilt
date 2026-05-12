// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.level.tile.Tile;

public class SaplingTileItem extends TileItem
{
    public SaplingTileItem(final int id) {
        super(id);
        this.setMaxDamage(0);
        this.setStackedByData(true);
    }
    
    @Override
    public int getLevelDataForAuxValue(final int auxValue) {
        return auxValue;
    }
    
    @Override
    public int getIcon(final int auxValue) {
        return Tile.sapling.getTexture(0, auxValue);
    }
}
