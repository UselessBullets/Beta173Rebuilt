// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

public class ClothTileItem extends TileItem
{
    public ClothTileItem(final int id) {
        super(id);
        this.setMaxDamage(0);
        this.setStackedByData(true);
    }
    
    @Override
    public int getLevelDataForAuxValue(final int auxValue) {
        return auxValue;
    }
}
