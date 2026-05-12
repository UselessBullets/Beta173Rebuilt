// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

public class Tile_SoundType
{
    public final String name;
    public final float volume;
    public final float pitch;
    
    public Tile_SoundType(final String name, final float volume, final float pitch) {
        this.name = name;
        this.volume = volume;
        this.pitch = pitch;
    }
    
    public float getVolume() {
        return this.volume;
    }
    
    public float getPitch() {
        return this.pitch;
    }
    
    public String getBreakSound() {
        return "step." + this.name;
    }
    
    public String getStepSound() {
        return "step." + this.name;
    }
}
