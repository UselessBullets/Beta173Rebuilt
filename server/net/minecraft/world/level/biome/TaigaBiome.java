// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.biome;

import net.minecraft.world.level.levelgen.feature.SpruceFeature;
import net.minecraft.world.level.levelgen.feature.PineFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import java.util.Random;
import net.minecraft.world.level.MobSpawnerData;
import net.minecraft.world.entity.animal.Wolf;

public class TaigaBiome extends Biome
{
    public TaigaBiome() {
        this.friendlies.add(new MobSpawnerData(Wolf.class, 2));
    }
    
    @Override
    public Feature getTreeFeature(final Random random) {
        if (random.nextInt(3) == 0) {
            return new PineFeature();
        }
        return new SpruceFeature();
    }
}
