// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

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
        if (face == 1) {
            return this.tex - 16;
        }
        if (face == 0) {
            return Tile.wood.getTexture(0);
        }
        if (face == 2 || face == 4) {
            return this.tex + 1;
        }
        return this.tex;
    }
    
    @Override
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        if (level.isClientSide) {
            return true;
        }
        player.startsCrafting(x, y, z);
        return true;
    }
}
