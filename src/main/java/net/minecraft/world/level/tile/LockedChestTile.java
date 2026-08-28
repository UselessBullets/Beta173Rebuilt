// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.Facing;
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
        if (face == Facing.UP) return this.tex - 1;
        if (face == Facing.DOWN) return this.tex - 1;

        final int n = level.getTile(x, y, z - 1); // face = 2
        final int s = level.getTile(x, y, z + 1); // face = 3
        final int w = level.getTile(x - 1, y, z); // face = 4
        final int e = level.getTile(x + 1, y, z); // face = 5

        int lockDir = 3;
        if (Tile.solid[n] && !Tile.solid[s]) lockDir = 3;
        if (Tile.solid[s] && !Tile.solid[n]) lockDir = 2;
        if (Tile.solid[w] && !Tile.solid[e]) lockDir = 5;
        if (Tile.solid[e] && !Tile.solid[w]) lockDir = 4;
        return (face == lockDir) ? (this.tex + 1) : this.tex;
    }
    
    @Override
    public int getTexture(final int face) {
        if (face == Facing.UP) return this.tex - 1;
        if (face == Facing.DOWN) return this.tex - 1;
        if (face == Facing.SOUTH) return this.tex + 1;
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
