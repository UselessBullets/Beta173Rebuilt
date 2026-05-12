// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

public class MobSpawnerData
{
    public Class mobClass;
    public int probabilityWeight;
    
    public MobSpawnerData(final Class mobClass, final int probabilityWeight) {
        this.mobClass = mobClass;
        this.probabilityWeight = probabilityWeight;
    }
}
