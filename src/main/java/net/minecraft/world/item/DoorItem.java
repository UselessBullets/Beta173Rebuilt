// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import util.Mth;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Material;

public class DoorItem extends Item
{
    private Material material;
    
    public DoorItem(final int id, final Material material) {
        super(id);
        this.material = material;
        this.maxStackSize = 1;
    }
    
    @Override
    public boolean useOn(final ItemInstance itemInstance, final Player player, final Level level, final int x, int y, final int z, final int face) {
        if (face != 1) {
            return false;
        }
        ++y;
        Tile tile;
        if (this.material == Material.wood) {
            tile = Tile.door_wood;
        }
        else {
            tile = Tile.door_iron;
        }
        if (!tile.mayPlace(level, x, y, z)) {
            return false;
        }
        int data = Mth.floor((player.yRot + 180.0f) * 4.0f / 360.0f - 0.5) & 0x3;
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
        final int n3 = (level.isSolidBlockingTile(x - n, y, z - n2) + level.isSolidBlockingTile(x - n, y + 1, z - n2)) ? 1 : 0;
        final int n4 = (level.isSolidBlockingTile(x + n, y, z + n2) + level.isSolidBlockingTile(x + n, y + 1, z + n2)) ? 1 : 0;
        final boolean b = level.getTile(x - n, y, z - n2) == tile.id || level.getTile(x - n, y + 1, z - n2) == tile.id;
        final boolean b2 = level.getTile(x + n, y, z + n2) == tile.id || level.getTile(x + n, y + 1, z + n2) == tile.id;
        boolean b3 = false;
        if (b && !b2) {
            b3 = true;
        }
        else if (n4 > n3) {
            b3 = true;
        }
        if (b3) {
            data = (data - 1 & 0x3);
            data += 4;
        }
        level.noNeighborUpdate = true;
        level.setTileAndData(x, y, z, tile.id, data);
        level.setTileAndData(x, y + 1, z, tile.id, data + 8);
        level.noNeighborUpdate = false;
        level.updateNeighborsAt(x, y, z, tile.id);
        level.updateNeighborsAt(x, y + 1, z, tile.id);
        --itemInstance.count;
        return true;
    }
}
