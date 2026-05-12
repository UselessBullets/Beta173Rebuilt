// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.BasicTree;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.BirchFeature;
import net.minecraft.world.level.levelgen.feature.SpruceFeature;
import java.util.Random;
import net.minecraft.world.level.Level;

public class Sapling extends Bush
{
    protected Sapling(final int id, final int tex) {
        super(id, tex);
        final float n = 0.4f;
        this.setShape(0.5f - n, 0.0f, 0.5f - n, 0.5f + n, n * 2.0f, 0.5f + n);
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        if (level.isClientSide) {
            return;
        }
        super.tick(level, x, y, z, random);
        if (level.getRawBrightness(x, y + 1, z) >= 9 && random.nextInt(30) == 0) {
            final int data = level.getData(x, y, z);
            if ((data & 0x8) == 0x0) {
                level.setData(x, y, z, data | 0x8);
            }
            else {
                this.growTree(level, x, y, z, random);
            }
        }
    }
    
    @Override
    public int getTexture(final int face, int data) {
        data &= 0x3;
        if (data == 1) {
            return 63;
        }
        if (data == 2) {
            return 79;
        }
        return super.getTexture(face, data);
    }
    
    public void growTree(final Level level, final int x, final int y, final int z, final Random random) {
        final int data = level.getData(x, y, z) & 0x3;
        level.setTileNoUpdate(x, y, z, 0);
        Feature feature;
        if (data == 1) {
            feature = new SpruceFeature();
        }
        else if (data == 2) {
            feature = new BirchFeature();
        }
        else {
            feature = new TreeFeature();
            if (random.nextInt(10) == 0) {
                feature = new BasicTree();
            }
        }
        if (!feature.place(level, random, x, y, z)) {
            level.setTileAndDataNoUpdate(x, y, z, this.id, data);
        }
    }
    
    @Override
    protected int getSpawnResourcesAuxValue(final int data) {
        return data & 0x3;
    }
}
