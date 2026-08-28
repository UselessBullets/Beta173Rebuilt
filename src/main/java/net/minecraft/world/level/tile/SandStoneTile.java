// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.Facing;
import net.minecraft.world.level.material.Material;

public class SandStoneTile extends Tile
{
    public SandStoneTile(final int id) {
        super(id, 192, Material.stone);
    }
    
    @Override
    public int getTexture(final int face) {
        if (face == Facing.UP) return this.tex - 16;
        if (face == Facing.DOWN) return this.tex + 16;
        return this.tex;
    }
}
