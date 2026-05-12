// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.dimension;

import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.levelgen.SkyIslandRandomLevelSource;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.Biome;

public class SkyIslandDimension extends Dimension
{
    public void init() {
        this.biomeSource = new FixedBiomeSource(Biome.sky, 0.5, 0.0);
        this.id = 1;
    }
    
    @Override
    public ChunkSource createRandomLevelSource() {
        return new SkyIslandRandomLevelSource(this.level, this.level.getSeed());
    }
    
    @Override
    public float getTimeOfDay(final long time, final float partialTick) {
        return 0.0f;
    }
    
    @Override
    public boolean isValidSpawn(final int x, final int z) {
        final int topTile = this.level.getTopTile(x, z);
        return topTile != 0 && Tile.tiles[topTile].material.blocksMotion();
    }
}
