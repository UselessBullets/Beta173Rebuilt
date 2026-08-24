// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class BowlFoodItem extends FoodItem
{
    public BowlFoodItem(final int id, final int nutrition) {
        super(id, nutrition, false);
    }
    
    @Override
    public ItemInstance use(final ItemInstance itemInstance, final Level level, final Player player) {
        super.use(itemInstance, level, player);

        return new ItemInstance(Item.bowl);
    }
}
