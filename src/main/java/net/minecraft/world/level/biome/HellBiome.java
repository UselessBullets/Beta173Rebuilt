// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.biome;

import net.minecraft.world.entity.monster.PigZombie;
import net.minecraft.world.entity.monster.Ghast;

public class HellBiome extends Biome
{
    public HellBiome() {
        this.enemies.clear();
        this.friendlies.clear();
        this.waterFriendlies.clear();

        this.enemies.add(new MobSpawnerData(Ghast.class, 10));
        this.enemies.add(new MobSpawnerData(PigZombie.class, 10));
    }
}
