// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.Facing;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;

public class FlintAndSteelItem extends Item
{
    public FlintAndSteelItem(final int id) {
        super(id);
        this.maxStackSize = 1;
        this.setMaxDamage(64);
    }
    
    @Override
    public boolean useOn(final ItemInstance itemInstance, final Player player, final Level level, int x, int y, int z, final int face) {
        if (face == Facing.DOWN) --y;
        if (face == Facing.UP) ++y;
        if (face == Facing.NORTH) --z;
        if (face == Facing.SOUTH) ++z;
        if (face == Facing.WEST) --x;
        if (face == Facing.EAST) ++x;

        if (level.getTile(x, y, z) == 0) {
            level.playSound(x + 0.5, y + 0.5, z + 0.5, "fire.ignite", 1.0f, FlintAndSteelItem.random.nextFloat() * 0.4f + 0.8f);
            level.setTile(x, y, z, Tile.fire.id);
        }
        itemInstance.hurt(1, player);
        return true;
    }
}
