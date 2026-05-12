// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class BowItem extends Item
{
    public BowItem(final int id) {
        super(id);
        this.maxStackSize = 1;
    }
    
    @Override
    public ItemInstance use(final ItemInstance itemInstance, final Level level, final Player player) {
        if (player.inventory.removeResource(Item.arrow.id)) {
            level.playSound(player, "random.bow", 1.0f, 1.0f / (BowItem.random.nextFloat() * 0.4f + 0.8f));
            if (!level.isClientSide) {
                level.addEntity(new Arrow(level, player));
            }
        }
        return itemInstance;
    }
}
