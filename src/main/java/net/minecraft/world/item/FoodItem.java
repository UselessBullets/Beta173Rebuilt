// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class FoodItem extends Item
{
    private int nutrition;
    private boolean isMeat;
    
    public FoodItem(final int id, final int nutrition, final boolean isMeat) {
        super(id);
        this.nutrition = nutrition;
        this.isMeat = isMeat;
        this.maxStackSize = 1;
    }
    
    @Override
    public ItemInstance use(final ItemInstance itemInstance, final Level level, final Player player) {
        itemInstance.count--;
        player.heal(this.nutrition);
        return itemInstance;
    }
    
    public int getNutrition() {
        return this.nutrition;
    }
    
    public boolean isMeat() {
        return this.isMeat;
    }
}
