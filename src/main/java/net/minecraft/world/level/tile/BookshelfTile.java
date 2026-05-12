// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import java.util.Random;
import net.minecraft.world.level.material.Material;

public class BookshelfTile extends Tile
{
    public BookshelfTile(final int id, final int tex) {
        super(id, tex, Material.wood);
    }
    
    @Override
    public int getTexture(final int face) {
        if (face <= 1) {
            return 4;
        }
        return this.tex;
    }
    
    @Override
    public int getResourceCount(final Random random) {
        return 0;
    }
}
