// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.level.chunk.LevelChunk;

public class Region implements LevelSource
{
    private int xc1;
    private int zc1;
    private LevelChunk[][] chunks;
    private Level level;
    
    public Region(final Level level, final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
        this.level = level;
        this.xc1 = x1 >> 4;
        this.zc1 = z1 >> 4;
        final int n = x2 >> 4;
        final int n2 = z2 >> 4;
        this.chunks = new LevelChunk[n - this.xc1 + 1][n2 - this.zc1 + 1];
        for (int i = this.xc1; i <= n; ++i) {
            for (int j = this.zc1; j <= n2; ++j) {
                this.chunks[i - this.xc1][j - this.zc1] = level.getChunk(i, j);
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
        final int n = (x >> 4) - this.xc1;
        final int n2 = (z >> 4) - this.zc1;
        if (n < 0 || n >= this.chunks.length || n2 < 0 || n2 >= this.chunks[n].length) {
            return 0;
        }
        final LevelChunk levelChunk = this.chunks[n][n2];
        if (levelChunk == null) {
            return 0;
        }
        return levelChunk.getTile(x & 0xF, y, z & 0xF);
    }
    
    public TileEntity getTileEntity(final int x, final int y, final int z) {
        return this.chunks[(x >> 4) - this.xc1][(z >> 4) - this.zc1].getTileEntity(x & 0xF, y, z & 0xF);
    }
    
    public float getBrightness(final int x, final int y, final int z, final int emitt) {
        int rawBrightness = this.getRawBrightness(x, y, z);
        if (rawBrightness < emitt) {
            rawBrightness = emitt;
        }
        return this.level.dimension.brightnessRamp[rawBrightness];
    }
    
    public float getBrightness(final int x, final int y, final int z) {
        return this.level.dimension.brightnessRamp[this.getRawBrightness(x, y, z)];
    }
    
    public int getRawBrightness(final int x, final int y, final int z) {
        return this.getRawBrightness(x, y, z, true);
    }
    
    public int getRawBrightness(final int x, final int y, final int z, final boolean propagate) {
        if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
            return 15;
        }
        if (propagate) {
            final int tile = this.getTile(x, y, z);
            if (tile == Tile.stoneSlabHalf.id || tile == Tile.farmland.id || tile == Tile.stairs_wood.id || tile == Tile.stairs_stone.id) {
                int rawBrightness = this.getRawBrightness(x, y + 1, z, false);
                final int rawBrightness2 = this.getRawBrightness(x + 1, y, z, false);
                final int rawBrightness3 = this.getRawBrightness(x - 1, y, z, false);
                final int rawBrightness4 = this.getRawBrightness(x, y, z + 1, false);
                final int rawBrightness5 = this.getRawBrightness(x, y, z - 1, false);
                if (rawBrightness2 > rawBrightness) {
                    rawBrightness = rawBrightness2;
                }
                if (rawBrightness3 > rawBrightness) {
                    rawBrightness = rawBrightness3;
                }
                if (rawBrightness4 > rawBrightness) {
                    rawBrightness = rawBrightness4;
                }
                if (rawBrightness5 > rawBrightness) {
                    rawBrightness = rawBrightness5;
                }
                return rawBrightness;
            }
        }
        if (y < 0) {
            return 0;
        }
        if (y >= 128) {
            int n = 15 - this.level.skyDarken;
            if (n < 0) {
                n = 0;
            }
            return n;
        }
        return this.chunks[(x >> 4) - this.xc1][(z >> 4) - this.zc1].getRawBrightness(x & 0xF, y, z & 0xF, this.level.skyDarken);
    }
    
    public int getData(final int x, final int y, final int z) {
        if (y < 0) {
            return 0;
        }
        if (y >= 128) {
            return 0;
        }
        return this.chunks[(x >> 4) - this.xc1][(z >> 4) - this.zc1].getData(x & 0xF, y, z & 0xF);
    }
    
    public Material getMaterial(final int x, final int y, final int z) {
        final int tile = this.getTile(x, y, z);
        if (tile == 0) {
            return Material.air;
        }
        return Tile.tiles[tile].material;
    }
    
    public BiomeSource getBiomeSource() {
        return this.level.getBiomeSource();
    }
    
    public boolean isSolidTile(final int x, final int y, final int z) {
        final Tile tile = Tile.tiles[this.getTile(x, y, z)];
        return tile != null && tile.isSolidRender();
    }
    
    public boolean isSolidBlockingTile(final int x, final int y, final int z) {
        final Tile tile = Tile.tiles[this.getTile(x, y, z)];
        return tile != null && tile.material.blocksMotion() && tile.isCubeShaped();
    }
}
