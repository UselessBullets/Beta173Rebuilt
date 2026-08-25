// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.material.Material;
import java.util.Random;
import net.minecraft.world.level.Level;

public class ReedsFeature extends Feature
{
    @Override
    public boolean place(final Level level, final Random random, final int x, final int y, final int z) {
        for (int i = 0; i < 20; ++i) {
            final int x2 = x + random.nextInt(4) - random.nextInt(4);
            final int y2 = y;
            final int z2 = z + random.nextInt(4) - random.nextInt(4);

            if (level.isEmptyTile(x2, y2, z2)) {
                if (level.getMaterial(x2 - 1, y2 - 1, z2) == Material.water ||
                    level.getMaterial(x2 + 1, y2 - 1, z2) == Material.water ||
                    level.getMaterial(x2, y2 - 1, z2 - 1) == Material.water ||
                    level.getMaterial(x2, y2 - 1, z2 + 1) == Material.water)
                {
                    int h = 2 + random.nextInt(random.nextInt(3) + 1);
                    for (int yy = 0; yy < h; ++yy) {
                        if (Tile.reeds.canSurvive(level, x2, y2 + yy, z2)) {
                            level.setTileNoUpdate(x2, y2 + yy, z2, Tile.reeds.id);
                        }
                    }
                }
            }
        }

        return true;
    }
}
