// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;

public class HoeItem extends Item
{
    public HoeItem(final int id, final Tier tier) {
        super(id);
        this.maxStackSize = 1;
        this.setMaxDamage(tier.getUses());
    }
    
    @Override
    public boolean useOn(final ItemInstance itemInstance, final Player player, final Level level, final int x, final int y, final int z, final int face) {
        final int targetType = level.getTile(x, y, z);
        final int above = level.getTile(x, y + 1, z);

        if ((face != 0 && above == 0 && targetType == Tile.grass.id) || targetType == Tile.dirt.id) {
            final Tile tile = Tile.farmland;
            level.playLocalSound(x + 0.5f, y + 0.5f, z + 0.5f, tile.soundType.getStepSound(), (tile.soundType.getVolume() + 1.0f) / 2.0f, tile.soundType.getPitch() * 0.8f);

            if (level.isClientSide) return true;
            level.setTile(x, y, z, tile.id);
            itemInstance.hurt(1, player);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isHandEquipped() {
        return true;
    }
}
