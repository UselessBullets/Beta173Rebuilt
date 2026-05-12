// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.material;

public class GasMaterial extends Material
{
    public GasMaterial(final MaterialColor color) {
        super(color);
        this.replaceable();
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
