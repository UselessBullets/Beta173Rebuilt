// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.dimension;

import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.levelgen.RandomLevelSource;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.Level;

public abstract class Dimension
{
    public Level level;
    public BiomeSource biomeSource;
    public boolean foggy;
    public boolean ultraWarm;
    public boolean hasCeiling;
    public float[] brightnessRamp;
    public int id;
    private float[] sunriseCol;
    
    public Dimension() {
        this.foggy = false;
        this.ultraWarm = false;
        this.hasCeiling = false;
        this.brightnessRamp = new float[16];
        this.id = 0;
        this.sunriseCol = new float[4];
    }
    
    public final void init(final Level level) {
        this.level = level;
        this.init();
        this.updateLightRamp();
    }
    
    protected void updateLightRamp() {
        final float n = 0.05f;
        for (int i = 0; i <= 15; ++i) {
            final float n2 = 1.0f - i / 15.0f;
            this.brightnessRamp[i] = (1.0f - n2) / (n2 * 3.0f + 1.0f) * (1.0f - n) + n;
        }
    }
    
    protected void init() {
        this.biomeSource = new BiomeSource(this.level);
    }
    
    public ChunkSource createRandomLevelSource() {
        return new RandomLevelSource(this.level, this.level.getSeed());
    }
    
    public boolean isValidSpawn(final int x, final int z) {
        return this.level.getTopTile(x, z) == Tile.sand.id;
    }
    
    public float getTimeOfDay(final long time, final float partialTick) {
        float n = ((int)(time % 24000L) + partialTick) / 24000.0f - 0.25f;
        if (n < 0.0f) {
            ++n;
        }
        if (n > 1.0f) {
            --n;
        }
        final float n2 = n;
        return n2 + (1.0f - (float)((Math.cos(n * 3.141592653589793) + 1.0) / 2.0) - n2) / 3.0f;
    }
    
    public boolean mayRespawn() {
        return true;
    }
    
    public static Dimension getNew(final int id) {
        if (id == -1) {
            return new HellDimension();
        }
        if (id == 0) {
            return new NormalDimension();
        }
        if (id == 1) {
            return new SkyIslandDimension();
        }
        return null;
    }
}
