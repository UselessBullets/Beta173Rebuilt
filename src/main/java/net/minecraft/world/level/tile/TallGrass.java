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
    protected TallGrass(final int id, final int tex) {
        super(id, tex);
        final float n = 0.4f;
        this.setShape(0.5f - n, 0.0f, 0.5f - n, 0.5f + n, 0.8f, 0.5f + n);
    }
    
    @Override
    public int getTexture(final int face, final int data) {
        if (data == 1) {
            return this.tex;
        }
        if (data == 2) {
            return this.tex + 16 + 1;
        }
        if (data == 0) {
            return this.tex + 16;
        }
        return this.tex;
    }
    
    @Override
    public int getColor(final LevelSource level, int x, int y, int z) {
        if (level.getData(x, y, z) == 0) {
            return 16777215;
        }
        final long n = x * 3129871 + z * 6129781 + y;
        final long n2 = n * n * 42317861L + n * 11L;
        x += (int)(n2 >> 14 & 0x1FL);
        y += (int)(n2 >> 19 & 0x1FL);
        z += (int)(n2 >> 24 & 0x1FL);
        level.getBiomeSource().getBiomeBlock(x, z, 1, 1);
        return GrassColor.get(level.getBiomeSource().temperatures[0], level.getBiomeSource().downfalls[0]);
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        if (random.nextInt(8) == 0) {
            return Item.seeds.id;
        }
        return -1;
    }
}
