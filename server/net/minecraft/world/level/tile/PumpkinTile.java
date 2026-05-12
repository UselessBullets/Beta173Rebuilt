// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class PumpkinTile extends Tile
{
    private boolean lit;
    
    protected PumpkinTile(final int id, final int tex, final boolean lit) {
        super(id, Material.vegetable);
        this.tex = tex;
        this.setTicking(true);
        this.lit = lit;
    }
    
    @Override
    public int getTexture(final int face, final int data) {
        if (face == 1) {
            return this.tex;
        }
        if (face == 0) {
            return this.tex;
        }
        int n = this.tex + 1 + 16;
        if (this.lit) {
            ++n;
        }
        if (data == 2 && face == 2) {
            return n;
        }
        if (data == 3 && face == 5) {
            return n;
        }
        if (data == 0 && face == 3) {
            return n;
        }
        if (data == 1 && face == 4) {
            return n;
        }
        return this.tex + 16;
    }
    
    @Override
    public int getTexture(final int face) {
        if (face == 1) {
            return this.tex;
        }
        if (face == 0) {
            return this.tex;
        }
        if (face == 3) {
            return this.tex + 1 + 16;
        }
        return this.tex + 16;
    }
    
    @Override
    public void onPlace(final Level level, final int x, final int y, final int z) {
        super.onPlace(level, x, y, z);
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        final int tile = level.getTile(x, y, z);
        return (tile == 0 || Tile.tiles[tile].material.isReplaceable()) && level.isSolidBlockingTile(x, y - 1, z);
    }
    
    @Override
    public void setPlacedBy(final Level level, final int x, final int y, final int z, final Mob by) {
        level.setData(x, y, z, Mth.floor(by.yRot * 4.0f / 360.0f + 2.5) & 0x3);
    }
}
