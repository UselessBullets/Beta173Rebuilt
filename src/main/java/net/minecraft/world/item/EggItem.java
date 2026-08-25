// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class EggItem extends Item
{
    public EggItem(final int id) {
        super(id);
        this.maxStackSize = 16;
    }
    
    @Override
    public ItemInstance use(final ItemInstance itemInstance, final Level level, final Player player) {
        itemInstance.count--;
        level.playSound(player, "random.bow", 0.5f, 0.4f / (EggItem.random.nextFloat() * 0.4f + 0.8f));
        if (!level.isClientSide) level.addEntity(new ThrownEgg(level, player));
        return itemInstance;
    }
}
