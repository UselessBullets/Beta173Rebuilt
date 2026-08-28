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
    private int xc1, zc1;
    private LevelChunk[][] chunks;
    private Level level;
    
    public Region(final Level level, final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
        this.level = level;

        this.xc1 = x1 >> 4;
        this.zc1 = z1 >> 4;
        final int xc2 = x2 >> 4;
        final int zc2 = z2 >> 4;

        this.chunks = new LevelChunk[xc2 - this.xc1 + 1][zc2 - this.zc1 + 1];

        for (int xc = this.xc1; xc <= xc2; ++xc) {
            for (int zc = this.zc1; zc <= zc2; ++zc) {
                this.chunks[xc - this.xc1][zc - this.zc1] = level.getChunk(xc, zc);
            }
        }
    }
    
    public int getTile(final int x, final int y, final int z) {
        if (y < Level.MIN_HEIGHT) return 0;
        if (y >= Level.MAX_HEIGHT) return 0;

        final int xc = (x >> 4) - this.xc1;
        final int zc = (z >> 4) - this.zc1;

        if (xc < 0 || xc >= this.chunks.length || zc < 0 || zc >= this.chunks[xc].length) {
            return 0;
        }

        final LevelChunk lc = this.chunks[xc][zc];
        if (lc == null) return 0;
        return lc.getTile(x & 0xF, y, z & 0xF);
    }
    
    public TileEntity getTileEntity(final int x, final int y, final int z) {
        int xc = (x >> 4) - this.xc1;
        int zc = (z >> 4) - this.zc1;

        return this.chunks[xc][zc].getTileEntity(x & 0xF, y, z & 0xF);
    }
    
    public float getBrightness(final int x, final int y, final int z, final int emitt) {
        int n = this.getRawBrightness(x, y, z);
        if (n < emitt) n = emitt;
        return this.level.dimension.brightnessRamp[n];
    }
    
    public float getBrightness(final int x, final int y, final int z) {
        return this.level.dimension.brightnessRamp[this.getRawBrightness(x, y, z)];
    }
    
    public int getRawBrightness(final int x, final int y, final int z) {
        return this.getRawBrightness(x, y, z, true);
    }
    
    public int getRawBrightness(final int x, final int y, final int z, final boolean propagate) {
        if (x < -Level.MAX_LEVEL_SIZE || z < -Level.MAX_LEVEL_SIZE || x >= Level.MAX_LEVEL_SIZE || z > Level.MAX_LEVEL_SIZE) {
            return Level.MAX_BRIGHTNESS;
        }

        if (propagate) {
            final int id = this.getTile(x, y, z);
            if (id == Tile.stoneSlabHalf.id || id == Tile.farmland.id || id == Tile.stairs_wood.id || id == Tile.stairs_stone.id) {
                int br = this.getRawBrightness(x, y + 1, z, false);
                final int br1 = this.getRawBrightness(x + 1, y, z, false);
                final int br2 = this.getRawBrightness(x - 1, y, z, false);
                final int br3 = this.getRawBrightness(x, y, z + 1, false);
                final int br4 = this.getRawBrightness(x, y, z - 1, false);
                if (br1 > br) br = br1;
                if (br2 > br) br = br2;
                if (br3 > br) br = br3;
                if (br4 > br) br = br4;
                return br;
            }
        }

        if (y < Level.MIN_HEIGHT) return 0;
        if (y >= Level.MAX_HEIGHT) {
            int br = Level.MAX_BRIGHTNESS - this.level.skyDarken;
            if (br < 0) br = 0;
            return br;
        }

        int xc = (x >> 4) - this.xc1;
        int zc = (z >> 4) - this.zc1;

        return this.chunks[xc][zc].getRawBrightness(x & 0xF, y, z & 0xF, this.level.skyDarken);
    }
    
    public int getData(final int x, final int y, final int z) {
        if (y < Level.MIN_HEIGHT || y >= Level.MAX_HEIGHT) return 0;

        int xc = (x >> 4) - this.xc1;
        int zc = (z >> 4) - this.zc1;

        return this.chunks[xc][zc].getData(x & 0xF, y, z & 0xF);
    }
    
    public Material getMaterial(final int x, final int y, final int z) {
        final int t = this.getTile(x, y, z);
        if (t == 0) return Material.air;
        return Tile.tiles[t].material;
    }
    
    public BiomeSource getBiomeSource() {
        return this.level.getBiomeSource();
    }
    
    public boolean isSolidRenderTile(final int x, final int y, final int z) {
        final Tile tile = Tile.tiles[this.getTile(x, y, z)];
        if (tile == null) return false;
        return tile.isSolidRender();
    }
    
    public boolean isSolidBlockingTile(final int x, final int y, final int z) {
        final Tile tile = Tile.tiles[this.getTile(x, y, z)];
        if (tile == null) return false;
        return tile.material.blocksMotion() && tile.isCubeShaped();
    }
}
