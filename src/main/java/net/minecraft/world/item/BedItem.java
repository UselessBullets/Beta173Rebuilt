// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import util.Mth;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.BedTile;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;

public class BedItem extends Item
{
    public BedItem(final int id) {
        super(id);
    }
    
    @Override
    public boolean useOn(final ItemInstance itemInstance, final Player player, final Level level, final int x, int y, final int z, final int face) {
        if (face != 1) {
            return false;
        }
        ++y;
        final BedTile bedTile = (BedTile)Tile.bed;
        final int data = Mth.floor(player.yRot * 4.0f / 360.0f + 0.5) & 0x3;
        int n = 0;
        int n2 = 0;
        if (data == 0) {
            n2 = 1;
        }
        if (data == 1) {
            n = -1;
        }
        if (data == 2) {
            n2 = -1;
        }
        if (data == 3) {
            n = 1;
        }
        if (level.isEmptyTile(x, y, z) && level.isEmptyTile(x + n, y, z + n2) && level.isSolidBlockingTile(x, y - 1, z) && level.isSolidBlockingTile(x + n, y - 1, z + n2)) {
            level.setTileAndData(x, y, z, bedTile.id, data);
            level.setTileAndData(x + n, y, z + n2, bedTile.id, data + 8);
            --itemInstance.count;
            return true;
        }
        return false;
    }
}
