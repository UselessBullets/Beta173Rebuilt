// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

final class Tile_SoundType_Sand extends Tile_SoundType
{
    Tile_SoundType_Sand(final String name, final float volume, final float pitch) {
        super(name, volume, pitch);
    }
    
    @Override
    public String getBreakSound() {
        return "step.gravel";
    }
}
