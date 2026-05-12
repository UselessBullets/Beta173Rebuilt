// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class FishingRodItem extends Item
{
    public FishingRodItem(final int id) {
        super(id);
        this.setMaxDamage(64);
        this.setMaxStackSize(1);
    }
    
    @Override
    public ItemInstance use(final ItemInstance itemInstance, final Level level, final Player player) {
        if (player.fishing != null) {
            itemInstance.hurt(player.fishing.retrieve(), player);
            player.swing();
        }
        else {
            level.playSound(player, "random.bow", 0.5f, 0.4f / (FishingRodItem.random.nextFloat() * 0.4f + 0.8f));
            if (!level.isClientSide) {
                level.addEntity(new FishingHook(level, player));
            }
            player.swing();
        }
        return itemInstance;
    }
}
