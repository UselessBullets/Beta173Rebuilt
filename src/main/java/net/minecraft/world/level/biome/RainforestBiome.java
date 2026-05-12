// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.biome;

import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.BasicTree;
import net.minecraft.world.level.levelgen.feature.Feature;
import java.util.Random;

public class RainforestBiome extends Biome
{
    @Override
    public Feature getTreeFeature(final Random random) {
        if (random.nextInt(3) == 0) {
            return new BasicTree();
        }
        return new TreeFeature();
    }
}
