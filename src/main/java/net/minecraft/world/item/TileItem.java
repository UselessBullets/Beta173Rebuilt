// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.tile.Tile;

public class TileItem extends Item
{
    private int tileId;
    
    public TileItem(final int id) {
        super(id);
        this.tileId = id + 256;
        this.setIcon(Tile.tiles[id + 256].getTexture(2));
    }
    
    @Override
    public boolean useOn(final ItemInstance itemInstance, final Player player, final Level level, int x, int y, int z, int face) {
        if (level.getTile(x, y, z) == Tile.topSnow.id) {
            face = 0;
        }
        else {
            if (face == 0) {
                --y;
            }
            if (face == 1) {
                ++y;
            }
            if (face == 2) {
                --z;
            }
            if (face == 3) {
                ++z;
            }
            if (face == 4) {
                --x;
            }
            if (face == 5) {
                ++x;
            }
        }
        if (itemInstance.count == 0) {
            return false;
        }
        if (y == 127 && Tile.tiles[this.tileId].material.isSolid()) {
            return false;
        }
        if (level.mayPlace(this.tileId, x, y, z, false, face)) {
            final Tile tile = Tile.tiles[this.tileId];
            if (level.setTileAndData(x, y, z, this.tileId, this.getLevelDataForAuxValue(itemInstance.getAuxValue()))) {
                Tile.tiles[this.tileId].setPlacedOnFace(level, x, y, z, face);
                Tile.tiles[this.tileId].setPlacedBy(level, x, y, z, player);
                level.playLocalSound(x + 0.5f, y + 0.5f, z + 0.5f, tile.soundType.getStepSound(), (tile.soundType.getVolume() + 1.0f) / 2.0f, tile.soundType.getPitch() * 0.8f);
                --itemInstance.count;
            }
            return true;
        }
        return false;
    }
    
    @Override
    public String getDescriptionId(final ItemInstance itemInstance) {
        return Tile.tiles[this.tileId].getDescriptionId();
    }
    
    @Override
    public String getDescriptionId() {
        return Tile.tiles[this.tileId].getDescriptionId();
    }
}
