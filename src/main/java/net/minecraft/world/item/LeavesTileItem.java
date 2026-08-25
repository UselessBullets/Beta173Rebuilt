// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.Facing;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.tile.LeafTile;
import net.minecraft.world.level.tile.Tile;

public class LeavesTileItem extends TileItem
{
    public LeavesTileItem(final int id) {
        super(id);
        this.setMaxDamage(0);
        this.setStackedByData(true);
    }
    
    @Override
    public int getLevelDataForAuxValue(final int auxValue) {
        return auxValue | LeafTile.UPDATE_LEAF_BIT;
    }
    
    @Override
    public int getIcon(final int auxValue) {
        return Tile.leaves.getTexture(Facing.DOWN, auxValue);
    }
    
    @Override
    public int getColor(final int auxData) {
        if ((auxData & LeafTile.LEAF_TYPE_MASK) == LeafTile.EVERGREEN_LEAF) {
            return FoliageColor.getEvergreenColor();
        }
        if ((auxData & LeafTile.LEAF_TYPE_MASK) == LeafTile.BIRCH_LEAF) {
            return FoliageColor.getBirchColor();
        }
        return FoliageColor.getDefaultColor();
    }
}
