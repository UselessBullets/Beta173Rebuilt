// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.Facing;
import util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class PumpkinTile extends Tile
{
    public static final int DIR_SOUTH = 0;
    public static final int DIR_WEST = 1;
    public static final int DIR_NORTH = 2;
    public static final int DIR_EAST = 3;
    private boolean lit;
    
    protected PumpkinTile(final int id, final int tex, final boolean lit) {
        super(id, Material.vegetable);
        this.tex = tex;
        this.setTicking(true);
        this.lit = lit;
    }
    
    @Override
    public int getTexture(final int face, final int data) {
        if (face == Facing.UP) return this.tex;
        if (face == Facing.DOWN) return this.tex;

        int texFace = this.tex + 1 + 16;
        if (this.lit) ++texFace;

        if (data == DIR_NORTH && face == Facing.NORTH) return texFace;
        if (data == DIR_EAST && face == Facing.EAST) return texFace;
        if (data == DIR_SOUTH && face == Facing.SOUTH) return texFace;
        if (data == DIR_WEST && face == Facing.WEST) return texFace;

        return this.tex + 16;
    }
    
    @Override
    public int getTexture(final int face) {
        if (face == Facing.UP) return this.tex;
        if (face == Facing.DOWN) return this.tex;
        if (face == Facing.SOUTH) return this.tex + 1 + 16;
        return this.tex + 16;
    }
    
    @Override
    public void onPlace(final Level level, final int x, final int y, final int z) {
        super.onPlace(level, x, y, z);
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        final int t = level.getTile(x, y, z);
        return (t == 0 || Tile.tiles[t].material.isReplaceable()) && level.isSolidBlockingTile(x, y - 1, z);
    }
    
    @Override
    public void setPlacedBy(final Level level, final int x, final int y, final int z, final Mob by) {
        int dir = Mth.floor(by.yRot * 4.0f / 360.0f + 2.5) & 0x3;
        level.setData(x, y, z, dir);
    }
}
