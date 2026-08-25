// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.Facing;
import net.minecraft.world.level.tile.StoneSlabTile;
import net.minecraft.world.level.tile.Tile;

public class StoneSlabTileItem extends TileItem
{
    public StoneSlabTileItem(final int id) {
        super(id);
        this.setMaxDamage(0);
        this.setStackedByData(true);
    }
    
    @Override
    public int getIcon(final int auxValue) {
        return Tile.stoneSlabHalf.getTexture(Facing.NORTH, auxValue);
    }
    
    @Override
    public int getLevelDataForAuxValue(final int auxValue) {
        return auxValue;
    }
    
    @Override
    public String getDescriptionId(final ItemInstance itemInstance) {
        return super.getDescriptionId() + "." + StoneSlabTile.SLAB_NAMES[itemInstance.getAuxValue()];
    }
}
