// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.Facing;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.tile.Tile;

public class TilePlanterItem extends Item
{
    private int tileId;
    
    public TilePlanterItem(final int id, final Tile tile) {
        super(id);
        this.tileId = tile.id;
    }
    
    @Override
    public boolean useOn(final ItemInstance itemInstance, final Player player, final Level level, int x, int y, int z, int face) {
        if (level.getTile(x, y, z) == Tile.topSnow.id) {
            face = Facing.DOWN; // Useless - LCE had this as Facing::UP, which is not what it is set to in b1.7.3, presumably this is just a b1.7.3 bug where the facing direction for this is just incorrect
        }
        else {
            if (face == Facing.DOWN) y--;
            if (face == Facing.UP) y++;
            if (face == Facing.NORTH) z--;
            if (face == Facing.SOUTH) z++;
            if (face == Facing.WEST) x--;
            if (face == Facing.EAST) x++;
        }

        if (itemInstance.count == 0) return false;

        if (level.mayPlace(this.tileId, x, y, z, false, face)) {
            final Tile tile = Tile.tiles[this.tileId];
            if (level.setTile(x, y, z, this.tileId)) {
                Tile.tiles[this.tileId].setPlacedOnFace(level, x, y, z, face);
                Tile.tiles[this.tileId].setPlacedBy(level, x, y, z, player);

                level.playSound(x + 0.5f, y + 0.5f, z + 0.5f, tile.soundType.getStepSound(), (tile.soundType.getVolume() + 1.0f) / 2.0f, tile.soundType.getPitch() * 0.8f);
                itemInstance.count--;
            }
        }
        return true;
    }
}
