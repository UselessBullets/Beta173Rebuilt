// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.level.tile.ClothTile;
import net.minecraft.world.level.tile.Tile;

public class ClothTileItem extends TileItem
{
    public ClothTileItem(final int id) {
        super(id);
        this.setMaxDamage(0);
        this.setStackedByData(true);
    }
    
    @Override
    public int getIcon(final int auxValue) {
        return Tile.cloth.getTexture(2, ClothTile.getTileDataForItemAuxValue(auxValue));
    }
    
    @Override
    public int getLevelDataForAuxValue(final int auxValue) {
        return auxValue;
    }
    
    @Override
    public String getDescriptionId(final ItemInstance itemInstance) {
        return super.getDescriptionId() + "." + DyePowderItem.COLOR_DESCS[ClothTile.getTileDataForItemAuxValue(itemInstance.getAuxValue())];
    }
}
