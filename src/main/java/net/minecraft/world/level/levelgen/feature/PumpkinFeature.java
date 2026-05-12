// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.tile.Tile;
import java.util.Random;
import net.minecraft.world.level.Level;

public class PumpkinFeature extends Feature
{
    @Override
    public boolean place(final Level level, final Random random, final int x, final int y, final int z) {
        for (int i = 0; i < 64; ++i) {
            final int n = x + random.nextInt(8) - random.nextInt(8);
            final int y2 = y + random.nextInt(4) - random.nextInt(4);
            final int n2 = z + random.nextInt(8) - random.nextInt(8);
            if (level.isEmptyTile(n, y2, n2) && level.getTile(n, y2 - 1, n2) == Tile.grass.id && Tile.pumpkin.mayPlace(level, n, y2, n2)) {
                level.setTileAndDataNoUpdate(n, y2, n2, Tile.pumpkin.id, random.nextInt(4));
            }
        }
        return true;
    }
}
