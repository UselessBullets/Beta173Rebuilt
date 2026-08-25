// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.tile.Tile;
import java.util.Random;
import net.minecraft.world.level.Level;

public class TallGrassFeature extends Feature
{
    private int tile;
    private int type;
    
    public TallGrassFeature(final int tile, final int type) {
        this.tile = tile;
        this.type = type;
    }
    
    @Override
    public boolean place(final Level level, final Random random, final int x, int y, final int z) {
        int t = 0;
        while (((t = level.getTile(x, y, z)) == 0 || t == Tile.leaves.id) && y > 0) {
            --y;
        }

        for (int i = 0; i < 128; ++i) {
            final int x2 = x + random.nextInt(8) - random.nextInt(8);
            final int y2 = y + random.nextInt(4) - random.nextInt(4);
            final int z2 = z + random.nextInt(8) - random.nextInt(8);
            if (level.isEmptyTile(x2, y2, z2)) {
                if (Tile.tiles[this.tile].canSurvive(level, x2, y2, z2)) {
                    level.setTileAndDataNoUpdate(x2, y2, z2, this.tile, this.type);
                }
            }
        }
        return true;
    }
}
