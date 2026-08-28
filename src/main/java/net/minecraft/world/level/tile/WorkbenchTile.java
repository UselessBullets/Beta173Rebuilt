// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.Facing;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class WorkbenchTile extends Tile
{
    protected WorkbenchTile(final int id) {
        super(id, Material.wood);
        this.tex = 59;
    }
    
    @Override
    public int getTexture(final int face) {
        if (face == Facing.UP) return this.tex - 16;
        if (face == Facing.DOWN) return Tile.wood.getTexture(0);
        if (face == Facing.NORTH || face == Facing.WEST) return this.tex + 1;
        return this.tex;
    }
    
    @Override
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        if (level.isClientSide) {
            return true;
        }
        player.startCrafting(x, y, z);
        return true;
    }
}
