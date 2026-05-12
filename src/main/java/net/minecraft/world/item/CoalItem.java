// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

public class CoalItem extends Item
{
    public CoalItem(final int id) {
        super(id);
        this.setStackedByData(true);
        this.setMaxDamage(0);
    }
    
    @Override
    public String getDescriptionId(final ItemInstance itemInstance) {
        if (itemInstance.getAuxValue() == 1) {
            return "item.charcoal";
        }
        return "item.coal";
    }
}
