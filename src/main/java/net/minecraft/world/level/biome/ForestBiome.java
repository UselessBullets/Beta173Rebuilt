// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.biome;

import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.BasicTree;
import net.minecraft.world.level.levelgen.feature.BirchFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import java.util.Random;

import net.minecraft.world.entity.animal.Wolf;

public class ForestBiome extends Biome
{
    public ForestBiome() {
        this.friendlies.add(new MobSpawnerData(Wolf.class, 2));
    }
    
    @Override
    public Feature getTreeFeature(final Random random) {
        if (random.nextInt(5) == 0) {
            return new BirchFeature();
        }
        if (random.nextInt(3) == 0) {
            return new BasicTree();
        }
        return new TreeFeature();
    }
}
