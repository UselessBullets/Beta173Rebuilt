// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

public enum LightLayer
{
    Sky(15), 
    Block(0);
    
    public final int surrounding;
    
    private LightLayer(final int surrounding) {
        this.surrounding = surrounding;
    }
}
