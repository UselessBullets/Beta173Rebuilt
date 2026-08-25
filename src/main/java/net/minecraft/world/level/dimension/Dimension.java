// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.dimension;

import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.chunk.storage.OldChunkStorage;
import net.minecraft.world.phys.Vec3;
import util.Mth;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.levelgen.RandomLevelSource;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.Level;

import java.io.File;

public abstract class Dimension
{
    public Level level;
    public BiomeSource biomeSource;
    public boolean foggy = false;
    public boolean ultraWarm = false;
    public boolean hasCeiling = false;
    public float[] brightnessRamp = new float[Level.MAX_BRIGHTNESS + 1];
    public int id = 0;
    private float[] sunriseCol = new float[4];
    
    public final void init(final Level level) {
        this.level = level;
        this.init();
        this.updateLightRamp();
    }
    
    protected void updateLightRamp() {
        final float ambientLight = 0.05f;
        for (int i = 0; i <= Level.MAX_BRIGHTNESS; ++i) {
            final float v = 1.0f - i / (float) Level.MAX_BRIGHTNESS;
            this.brightnessRamp[i] = (1.0f - v) / (v * 3.0f + 1.0f) * (1.0f - ambientLight) + ambientLight;
        }
    }
    
    protected void init() {
        this.biomeSource = new BiomeSource(this.level);
    }
    
    public ChunkSource createRandomLevelSource() {
        return new RandomLevelSource(this.level, this.level.getSeed());
    }

    // Useless - In b1.2 and LCE leaks
    public ChunkStorage createStorage(File dir) {
        return new OldChunkStorage(dir, true);
    }
    
    public boolean isValidSpawn(final int x, final int z) {
        int topTile = this.level.getTopTile(x, z);
        return topTile == Tile.sand.id;
    }
    
    public float getTimeOfDay(final long time, final float a) {
        int dayStep = (int) (time % Level.TICKS_PER_DAY);
        float td = (dayStep + a) / Level.TICKS_PER_DAY - 0.25f;
        if (td < 0.0f) td += 1;
        if (td > 1.0f) td -= 1;
        final float tDo = td;
        td = 1.0f - (float)((Math.cos(td * Math.PI) + 1.0) / 2.0);
        td = tDo + (td - tDo) / 3.0f;
        return td;
    }
    
    public float[] getSunriseColor(final float td, final float a) {
        final float span = 0.4f;
        final float tt = Mth.cos(td * Mth.PI * 2.0f) - 0.0f;
        final float mid = -0.0f;
        if (tt >= mid - span && tt <= mid + span) {
            float aa = (tt - mid) / span * 0.5f + 0.5f;
            float mix = 1.0f - (1.0f - Mth.sin(aa * Mth.PI)) * 0.99f;
            mix = mix * mix;
            this.sunriseCol[0] = aa * 0.3f + 0.7f;
            this.sunriseCol[1] = aa * aa * 0.7f + 0.2f;
            this.sunriseCol[2] = aa * aa * 0.0f + 0.2f;
            this.sunriseCol[3] = mix;
            return this.sunriseCol;
        }
        return null;
    }
    
    public Vec3 getFogColor(final float td, final float a) {
        float br = Mth.cos(td * Mth.PI * 2.0f) * 2.0f + 0.5f;
        if (br < 0.0f) br = 0.0f;
        if (br > 1.0f) br = 1.0f;

        float r = 192 / 255.0f;
        float g = 216 / 255.0f;
        float b = 255 / 255.0f;
        r *= (br * 0.94f + 0.06f);
        g *= (br * 0.94f + 0.06f);
        b *= (br * 0.91f + 0.09f);

        return Vec3.newTemp(r, g, b);
    }
    
    public boolean mayRespawn() {
        return true;
    }
    
    public static Dimension getNew(final int id) {
        if (id == -1) return new HellDimension();
        if (id == 0) return new NormalDimension();
        if (id == 1) return new SkyIslandDimension();

        return null;
    }
    
    public float getCloudHeight() {
        return 108.0f;
    }
    
    public boolean hasGround() {
        return true;
    }
}
