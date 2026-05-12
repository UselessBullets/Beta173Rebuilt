// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.dimension;

import net.minecraft.world.phys.Vec3;
import util.Mth;
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
    
    public float[] getSunriseColor(final float td, final float partialTick) {
        final float n = 0.4f;
        final float n2 = Mth.cos(td * 3.1415927f * 2.0f) - 0.0f;
        final float n3 = -0.0f;
        if (n2 >= n3 - n && n2 <= n3 + n) {
            final float n4 = (n2 - n3) / n * 0.5f + 0.5f;
            final float n5 = 1.0f - (1.0f - Mth.sin(n4 * 3.1415927f)) * 0.99f;
            final float n6 = n5 * n5;
            this.sunriseCol[0] = n4 * 0.3f + 0.7f;
            this.sunriseCol[1] = n4 * n4 * 0.7f + 0.2f;
            this.sunriseCol[2] = n4 * n4 * 0.0f + 0.2f;
            this.sunriseCol[3] = n6;
            return this.sunriseCol;
        }
        return null;
    }
    
    public Vec3 getFogColor(final float td, final float partialTick) {
        float n = Mth.cos(td * 3.1415927f * 2.0f) * 2.0f + 0.5f;
        if (n < 0.0f) {
            n = 0.0f;
        }
        if (n > 1.0f) {
            n = 1.0f;
        }
        return Vec3.newTemp(0.7529412f * (n * 0.94f + 0.06f), 0.84705883f * (n * 0.94f + 0.06f), 1.0f * (n * 0.91f + 0.09f));
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
    
    public float getCloudHeight() {
        return 108.0f;
    }
    
    public boolean hasGround() {
        return true;
    }
}
