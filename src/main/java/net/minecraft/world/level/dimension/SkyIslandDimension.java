// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.dimension;

import net.minecraft.world.level.tile.Tile;
import util.Mth;
import net.minecraft.world.phys.Vec3;
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
    public float getTimeOfDay(final long time, final float a) {
        return 0.0f;
    }
    
    @Override
    public float[] getSunriseColor(final float td, final float a) {
        return null;
    }
    
    @Override
    public Vec3 getFogColor(final float td, final float a) {
        final int col = 0x8080a0;
        float br = Mth.cos(td * Mth.PI * 2.0f) * 2.0f + 0.5f;
        if (br < 0.0f) br = 0.0f;
        if (br > 1.0f) br = 1.0f;

        float r = (col >> 16 & 0xFF) / 255.0f;
        float g = (col >> 8 & 0xFF) / 255.0f;
        float b = (col & 0xFF) / 255.0f;
        r *= (br * 0.94f + 0.06f);
        g *= (br * 0.94f + 0.06f);
        b *= (br * 0.91f + 0.09f);
        return Vec3.newTemp(r, g, b);
    }
    
    @Override
    public boolean hasGround() {
        return false;
    }
    
    @Override
    public float getCloudHeight() {
        return 8.0f;
    }
    
    @Override
    public boolean isValidSpawn(final int x, final int z) {
        final int topTile = this.level.getTopTile(x, z);
        return topTile != 0 && Tile.tiles[topTile].material.blocksMotion();
    }
}
