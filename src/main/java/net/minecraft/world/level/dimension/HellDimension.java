// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.dimension;

import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.levelgen.HellRandomLevelSource;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.Biome;

public class HellDimension extends Dimension
{
    public void init() {
        this.biomeSource = new FixedBiomeSource(Biome.hell, 1.0, 0.0);
        this.foggy = true;
        this.ultraWarm = true;
        this.hasCeiling = true;
        this.id = -1;
    }
    
    @Override
    public Vec3 getFogColor(final float td, final float partialTick) {
        return Vec3.newTemp(0.20000000298023224, 0.029999999329447746, 0.029999999329447746);
    }
    
    @Override
    protected void updateLightRamp() {
        final float n = 0.1f;
        for (int i = 0; i <= 15; ++i) {
            final float n2 = 1.0f - i / 15.0f;
            this.brightnessRamp[i] = (1.0f - n2) / (n2 * 3.0f + 1.0f) * (1.0f - n) + n;
        }
    }
    
    @Override
    public ChunkSource createRandomLevelSource() {
        return new HellRandomLevelSource(this.level, this.level.getSeed());
    }
    
    @Override
    public boolean isValidSpawn(final int x, final int z) {
        final int topTile = this.level.getTopTile(x, z);
        return topTile != Tile.unbreakable.id && topTile != 0 && Tile.solid[topTile];
    }
    
    @Override
    public float getTimeOfDay(final long time, final float partialTick) {
        return 0.5f;
    }
    
    @Override
    public boolean mayRespawn() {
        return false;
    }
}
