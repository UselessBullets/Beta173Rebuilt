// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.level.material.Material;

public class HalfTransparentTile extends Tile
{
    private boolean allowSame;
    
    protected HalfTransparentTile(final int id, final int tex, final Material material, final boolean allowSame) {
        super(id, tex, material);
        this.allowSame = allowSame;
    }
    
    @Override
    public boolean isSolidRender() {
        return false;
    }
}
