// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.entity.projectile.FishingHook;
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
    public boolean isHandEquipped() {
        return true;
    }
    
    @Override
    public boolean isMirroredArt() {
        return true;
    }
    
    @Override
    public ItemInstance use(final ItemInstance itemInstance, final Level level, final Player player) {
        if (player.fishing != null) {
            int dmg = player.fishing.retrieve();
            itemInstance.hurt(dmg, player);
            player.swing();
        }
        else {
            level.playSound(player, "random.bow", 0.5f, 0.4f / (FishingRodItem.random.nextFloat() * 0.4f + 0.8f));
            if (!level.isClientSide) {
                FishingHook hook = new FishingHook(level, player);
                level.addEntity(hook);
            }
            player.swing();
        }
        return itemInstance;
    }
}
