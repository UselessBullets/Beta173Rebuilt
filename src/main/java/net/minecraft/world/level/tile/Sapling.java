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
    public static final int TYPE_DEFAULT = LeafTile.NORMAL_LEAF;
    public static final int TYPE_EVERGREEN = LeafTile.EVERGREEN_LEAF;
    public static final int TYPE_BIRCH = LeafTile.BIRCH_LEAF;
    private static final int TYPE_MASK = 0x3;
    private static final int AGE_BIT = 0x8;
    protected Sapling(final int id, final int tex) {
        super(id, tex);
        final float ss = 0.4f;
        this.setShape(0.5f - ss, 0.0f, 0.5f - ss, 0.5f + ss, ss * 2.0f, 0.5f + ss);
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        if (level.isClientSide) return;

        super.tick(level, x, y, z, random);

        if (level.getRawBrightness(x, y + 1, z) >= (Level.MAX_BRIGHTNESS - 6)) {
            if (random.nextInt(30) == 0) {
                final int data = level.getData(x, y, z);
                if ((data & AGE_BIT) == 0x0) {
                    level.setData(x, y, z, data | AGE_BIT);
                } else {
                    this.growTree(level, x, y, z, random);
                }
            }
        }
    }
    
    @Override
    public int getTexture(final int face, int data) {
        data &= TYPE_MASK;
        if (data == TYPE_EVERGREEN) return 63;
        if (data == TYPE_BIRCH) return 79;
        return super.getTexture(face, data);
    }
    
    public void growTree(final Level level, final int x, final int y, final int z, final Random random) {
        final int data = level.getData(x, y, z) & TYPE_MASK;

        level.setTileNoUpdate(x, y, z, 0);

        Feature f = null;
        if (data == TYPE_EVERGREEN) {
            f = new SpruceFeature();
        }
        else if (data == TYPE_BIRCH) {
            f = new BirchFeature();
        }
        else {
            f = new TreeFeature();
            if (random.nextInt(10) == 0) {
                f = new BasicTree();
            }
        }

        if (!f.place(level, random, x, y, z)) {
            level.setTileAndDataNoUpdate(x, y, z, this.id, data);
        }
    }
    
    @Override
    protected int getSpawnResourcesAuxValue(final int data) {
        return data & TYPE_MASK;
    }
}
