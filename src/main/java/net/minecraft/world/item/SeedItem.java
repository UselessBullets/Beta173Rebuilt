// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.Facing;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;

public class SeedItem extends Item
{
    private int resultId;
    
    public SeedItem(final int id, final int resultId) {
        super(id);
        this.resultId = resultId;
    }
    
    @Override
    public boolean useOn(final ItemInstance itemInstance, final Player player, final Level level, final int x, final int y, final int z, final int face) {
        if (face != Facing.UP) return false;

        if (level.getTile(x, y, z) == Tile.farmland.id && level.isEmptyTile(x, y + 1, z)) {
            level.setTile(x, y + 1, z, this.resultId);
            itemInstance.count--;
            return true;
        }
        return false;
    }
}
