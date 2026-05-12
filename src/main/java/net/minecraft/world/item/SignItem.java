// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.level.tile.entity.SignTileEntity;
import util.Mth;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;

public class SignItem extends Item
{
    public SignItem(final int id) {
        super(id);
        this.maxStackSize = 1;
    }
    
    @Override
    public boolean useOn(final ItemInstance itemInstance, final Player player, final Level level, int x, int y, int z, final int face) {
        if (face == 0) {
            return false;
        }
        if (!level.getMaterial(x, y, z).isSolid()) {
            return false;
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
        if (!Tile.sign.mayPlace(level, x, y, z)) {
            return false;
        }
        if (face == 1) {
            level.setTileAndData(x, y, z, Tile.sign.id, Mth.floor((player.yRot + 180.0f) * 16.0f / 360.0f + 0.5) & 0xF);
        }
        else {
            level.setTileAndData(x, y, z, Tile.wallSign.id, face);
        }
        --itemInstance.count;
        final SignTileEntity sign = (SignTileEntity)level.getTileEntity(x, y, z);
        if (sign != null) {
            player.openTextEdit(sign);
        }
        return true;
    }
}
