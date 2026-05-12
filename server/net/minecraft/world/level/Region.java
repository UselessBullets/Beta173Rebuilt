// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.level.chunk.LevelChunk;

public class Region implements LevelSource
{
    private int a;
    private int b;
    private LevelChunk[][] c;
    private Level d;
    
    public Region(final Level dj, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7) {
        this.d = dj;
        this.a = integer2 >> 4;
        this.b = integer4 >> 4;
        final int n = integer5 >> 4;
        final int n2 = integer7 >> 4;
        this.c = new LevelChunk[n - this.a + 1][n2 - this.b + 1];
        for (int i = this.a; i <= n; ++i) {
            for (int j = this.b; j <= n2; ++j) {
                this.c[i - this.a][j - this.b] = dj.getChunk(i, j);
            }
        }
    }
    
    public int getTile(final int x, final int y, final int z) {
        if (y < 0) {
            return 0;
        }
        if (y >= 128) {
            return 0;
        }
        final int n = (x >> 4) - this.a;
        final int n2 = (z >> 4) - this.b;
        if (n < 0 || n >= this.c.length || n2 < 0 || n2 >= this.c[n].length) {
            return 0;
        }
        final LevelChunk levelChunk = this.c[n][n2];
        if (levelChunk == null) {
            return 0;
        }
        return levelChunk.getTile(x & 0xF, y, z & 0xF);
    }
    
    public TileEntity getTileEntity(final int x, final int y, final int z) {
        return this.c[(x >> 4) - this.a][(z >> 4) - this.b].getTileEntity(x & 0xF, y, z & 0xF);
    }
    
    public int getData(final int x, final int y, final int z) {
        if (y < 0) {
            return 0;
        }
        if (y >= 128) {
            return 0;
        }
        return this.c[(x >> 4) - this.a][(z >> 4) - this.b].getData(x & 0xF, y, z & 0xF);
    }
    
    public Material getMaterial(final int x, final int y, final int z) {
        final int tile = this.getTile(x, y, z);
        if (tile == 0) {
            return Material.air;
        }
        return Tile.tiles[tile].material;
    }
    
    public boolean isSolidBlockingTile(final int x, final int y, final int z) {
        final Tile tile = Tile.tiles[this.getTile(x, y, z)];
        return tile != null && tile.material.blocksMotion() && tile.isCubeShaped();
    }
}
