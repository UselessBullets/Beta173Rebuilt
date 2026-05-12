// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import java.util.Random;
import net.minecraft.world.level.material.Material;

public class GlassTile extends HalfTransparentTile
{
    public GlassTile(final int id, final int tex, final Material material, final boolean allowSame) {
        super(id, tex, material, allowSame);
    }
    
    @Override
    public int getResourceCount(final Random random) {
        return 0;
    }
    
    @Override
    public int getRenderLayer() {
        return 0;
    }
}
