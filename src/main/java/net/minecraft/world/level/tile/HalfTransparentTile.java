// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.level.LevelSource;
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
    
    @Override
    public boolean shouldRenderFace(final LevelSource level, final int x, final int y, final int z, final int f) {
        final int id = level.getTile(x, y, z);
        if (!this.allowSame && id == this.id) return false;
        return super.shouldRenderFace(level, x, y, z, f);
    }
}
