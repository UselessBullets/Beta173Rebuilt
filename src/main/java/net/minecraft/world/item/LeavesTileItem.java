// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.level.FoliageColor;
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
        return auxValue | 0x8;
    }
    
    @Override
    public int getIcon(final int auxValue) {
        return Tile.leaves.getTexture(0, auxValue);
    }
    
    @Override
    public int getColor(final int auxData) {
        if ((auxData & 0x1) == 0x1) {
            return FoliageColor.getEvergreenColor();
        }
        if ((auxData & 0x2) == 0x2) {
            return FoliageColor.getBirchColor();
        }
        return FoliageColor.getDefaultColor();
    }
}
