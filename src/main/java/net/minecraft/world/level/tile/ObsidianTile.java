// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import java.util.Random;

public class ObsidianTile extends StoneTile
{
    public ObsidianTile(final int id, final int tex) {
        super(id, tex);
    }
    
    @Override
    public int getResourceCount(final Random random) {
        return 1;
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        return Tile.obsidian.id;
    }
}
