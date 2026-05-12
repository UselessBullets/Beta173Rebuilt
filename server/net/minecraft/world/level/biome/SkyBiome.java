// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.biome;

import net.minecraft.world.level.MobSpawnerData;
import net.minecraft.world.entity.animal.Chicken;

public class SkyBiome extends Biome
{
    public SkyBiome() {
        this.enemies.clear();
        this.friendlies.clear();
        this.waterFriendlies.clear();
        this.friendlies.add(new MobSpawnerData(Chicken.class, 10));
    }
}
