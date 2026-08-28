// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.item.Item;
import java.util.Random;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.LevelSource;

public class TallGrass extends Bush
{
    public static final int DEAD_SHRUB = 0;
    public static final int TALL_GRASS = 1;
    public static final int FERN = 2;
    protected TallGrass(final int id, final int tex) {
        super(id, tex);
        final float n = 0.4f;
        this.setShape(0.5f - n, 0.0f, 0.5f - n, 0.5f + n, 0.8f, 0.5f + n);
    }
    
    @Override
    public int getTexture(final int face, final int data) {
        if (data == TALL_GRASS) return this.tex;
        if (data == FERN) return this.tex + 16 + 1;
        if (data == DEAD_SHRUB) return this.tex + 16;
        return this.tex;
    }
    
    @Override
    public int getColor(final LevelSource level, int x, int y, int z) {
        if (level.getData(x, y, z) == DEAD_SHRUB) return 0xffffff;

        long seed = x * 3129871L + z * 6129781L + y;
        seed = seed * seed * 42317861L + seed * 11L;
        x += (int)(seed >> 14 & 0x1FL);
        y += (int)(seed >> 19 & 0x1FL);
        z += (int)(seed >> 24 & 0x1FL);

        level.getBiomeSource().getBiomeBlock(x, z, 1, 1);
        double temp = level.getBiomeSource().temperatures[0];
        double rain = level.getBiomeSource().downfalls[0];

        return GrassColor.get(temp, rain);
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        if (random.nextInt(8) == 0) {
            return Item.seeds.id;
        }
        return -1;
    }
}
