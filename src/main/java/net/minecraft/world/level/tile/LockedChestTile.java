// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;

public class LockedChestTile extends Tile
{
    protected LockedChestTile(final int id) {
        super(id, Material.wood);
        this.tex = 26;
    }
    
    @Override
    public int getTexture(final LevelSource level, final int x, final int y, final int z, final int face) {
        if (face == 1) {
            return this.tex - 1;
        }
        if (face == 0) {
            return this.tex - 1;
        }
        final int tile = level.getTile(x, y, z - 1);
        final int tile2 = level.getTile(x, y, z + 1);
        final int tile3 = level.getTile(x - 1, y, z);
        final int tile4 = level.getTile(x + 1, y, z);
        int n = 3;
        if (Tile.solid[tile] && !Tile.solid[tile2]) {
            n = 3;
        }
        if (Tile.solid[tile2] && !Tile.solid[tile]) {
            n = 2;
        }
        if (Tile.solid[tile3] && !Tile.solid[tile4]) {
            n = 5;
        }
        if (Tile.solid[tile4] && !Tile.solid[tile3]) {
            n = 4;
        }
        return (face == n) ? (this.tex + 1) : this.tex;
    }
    
    @Override
    public int getTexture(final int face) {
        if (face == 1) {
            return this.tex - 1;
        }
        if (face == 0) {
            return this.tex - 1;
        }
        if (face == 3) {
            return this.tex + 1;
        }
        return this.tex;
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        return true;
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        level.setTile(x, y, z, 0);
    }
}
