// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.tile.Tile;
import java.util.Random;
import net.minecraft.world.level.Level;

public class CactusFeature extends Feature
{
    @Override
    public boolean place(final Level level, final Random random, final int x, final int y, final int z) {
        for (int i = 0; i < 10; ++i) {
            final int x2 = x + random.nextInt(8) - random.nextInt(8);
            final int y2 = y + random.nextInt(4) - random.nextInt(4);
            final int z2 = z + random.nextInt(8) - random.nextInt(8);
            if (level.isEmptyTile(x2, y2, z2)) {
                for (int n = 1 + random.nextInt(random.nextInt(3) + 1), j = 0; j < n; ++j) {
                    if (Tile.cactus.canSurvive(level, x2, y2 + j, z2)) {
                        level.setTileNoUpdate(x2, y2 + j, z2, Tile.cactus.id);
                    }
                }
            }
        }
        return true;
    }
}
