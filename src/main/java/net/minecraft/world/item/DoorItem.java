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

        int xra = 0;
        int zra = 0;
        if (data == 0) zra = 1;
        if (data == 1) xra = -1;
        if (data == 2) zra = -1;
        if (data == 3) xra = 1;

        final int solidLeft = (level.isSolidBlockingTile(x - xra, y, z - zra) ? 1 : 0) + (level.isSolidBlockingTile(x - xra, y + 1, z - zra) ? 1 : 0);
        final int solidRight = (level.isSolidBlockingTile(x + xra, y, z + zra) ? 1 : 0) + (level.isSolidBlockingTile(x + xra, y + 1, z + zra) ? 1 : 0);

        final boolean b = level.getTile(x - xra, y, z - zra) == tile.id || level.getTile(x - xra, y + 1, z - zra) == tile.id;
        final boolean b2 = level.getTile(x + xra, y, z + zra) == tile.id || level.getTile(x + xra, y + 1, z + zra) == tile.id;
        boolean b3 = false;
        if (b && !b2) {
            b3 = true;
        }
        else if (solidRight > solidLeft) {
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
