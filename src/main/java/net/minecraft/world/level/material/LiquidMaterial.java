// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.material;

public class LiquidMaterial extends Material
{
    public LiquidMaterial(final MaterialColor color) {
        super(color);
        this.replaceable();
        this.destroyOnPush();
    }
    
    @Override
    public boolean isLiquid() {
        return true;
    }
    
    @Override
    public boolean blocksMotion() {
        return false;
    }
    
    @Override
    public boolean isSolid() {
        return false;
    }
}
