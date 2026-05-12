// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.material;

public class PortalMaterial extends Material
{
    public PortalMaterial(final MaterialColor color) {
        super(color);
    }
    
    @Override
    public boolean isSolid() {
        return false;
    }
    
    @Override
    public boolean blocksLight() {
        return false;
    }
    
    @Override
    public boolean blocksMotion() {
        return false;
    }
}
