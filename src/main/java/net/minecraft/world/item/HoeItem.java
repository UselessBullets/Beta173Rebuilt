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
        final int tile = level.getTile(x, y, z);
        final int tile2 = level.getTile(x, y + 1, z);
        if ((face == 0 || tile2 != 0 || tile != Tile.grass.id) && tile != Tile.dirt.id) {
            return false;
        }
        final Tile farmland = Tile.farmland;
        level.playLocalSound(x + 0.5f, y + 0.5f, z + 0.5f, farmland.soundType.getStepSound(), (farmland.soundType.getVolume() + 1.0f) / 2.0f, farmland.soundType.getPitch() * 0.8f);
        if (level.isClientSide) {
            return true;
        }
        level.setTile(x, y, z, farmland.id);
        itemInstance.hurt(1, player);
        return true;
    }
    
    @Override
    public boolean isHandEquipped() {
        return true;
    }
}
