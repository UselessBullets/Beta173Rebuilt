// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;

public class TransparentTile extends Tile
{
    protected boolean allowSame;
    
    protected TransparentTile(final int id, final int tex, final Material material, final boolean allowSame) {
        super(id, tex, material);
        this.allowSame = allowSame;
    }
    
    @Override
    public boolean isSolidRender() {
        return false;
    }
    
    @Override
    public boolean isFaceVisible(final LevelSource level, final int x, final int y, final int z, final int f) {
        final int tile = level.getTile(x, y, z);
        return (this.allowSame || tile != this.id) && super.isFaceVisible(level, x, y, z, f);
    }
}
