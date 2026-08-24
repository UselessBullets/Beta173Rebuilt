// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity;

import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.material.Material;

public enum MobCategory
{
    monster(Enemy.class, 70, Material.air, false),
    creature(Animal.class, 15, Material.air, true),
    waterCreature(WaterAnimal.class, 5, Material.water, true);
    
    private final Class<?> base;
    private final int max;
    private final Material spawnPositionMaterial;
    private final boolean isFriendly;
    
    MobCategory(final Class<?> base, final int max, final Material spawnPositionMaterial, final boolean isFriendly) {
        this.base = base;
        this.max = max;
        this.spawnPositionMaterial = spawnPositionMaterial;
        this.isFriendly = isFriendly;
    }
    
    public Class<?> getBaseClass() {
        return this.base;
    }
    
    public int getMaxInstancesPerChunk() {
        return this.max;
    }
    
    public Material getSpawnPositionMaterial() {
        return this.spawnPositionMaterial;
    }
    
    public boolean isFriendly() {
        return this.isFriendly;
    }
}
