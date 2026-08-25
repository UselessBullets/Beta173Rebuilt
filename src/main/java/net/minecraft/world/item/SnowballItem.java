// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SnowballItem extends Item
{
    public SnowballItem(final int id) {
        super(id);
        this.maxStackSize = 16;
    }
    
    @Override
    public ItemInstance use(final ItemInstance itemInstance, final Level level, final Player player) {
        itemInstance.count--;
        level.playSound(player, "random.bow", 0.5f, 0.4f / (SnowballItem.random.nextFloat() * 0.4f + 0.8f));
        if (!level.isClientSide) level.addEntity(new Snowball(level, player));
        return itemInstance;
    }
}
