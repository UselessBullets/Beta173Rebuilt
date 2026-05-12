// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.Mob;

public class SaddleItem extends Item
{
    public SaddleItem(final int id) {
        super(id);
        this.maxStackSize = 1;
    }
    
    @Override
    public void interractEnemy(final ItemInstance itemInstance, final Mob mob) {
        if (mob instanceof Pig) {
            final Pig pig = (Pig)mob;
            if (!pig.hasSaddle()) {
                pig.setSaddle(true);
                --itemInstance.count;
            }
        }
    }
    
    @Override
    public boolean hurtEnemy(final ItemInstance itemInstance, final Mob mob, final Mob attacker) {
        this.interractEnemy(itemInstance, mob);
        return true;
    }
}
