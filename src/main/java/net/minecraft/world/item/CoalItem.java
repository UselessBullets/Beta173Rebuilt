// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

public class CoalItem extends Item
{
    public static final int STONE_COAL = 0;
    public static final int CHAR_COAL = 1;
    public CoalItem(final int id) {
        super(id);
        this.setStackedByData(true);
        this.setMaxDamage(0);
    }
    
    @Override
    public String getDescriptionId(final ItemInstance itemInstance) {
        if (itemInstance.getAuxValue() == CHAR_COAL) {
            return "item.charcoal";
        }
        return "item.coal";
    }
}
