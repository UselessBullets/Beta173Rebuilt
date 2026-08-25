// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.tile.Tile;
import java.util.Random;
import net.minecraft.world.level.Level;

public class LightGemFeature extends Feature
{
    @Override
    public boolean place(final Level level, final Random random, final int x, final int y, final int z) {
        if (!level.isEmptyTile(x, y, z)) return false;
        if (level.getTile(x, y + 1, z) != Tile.hellRock.id) return false;
        level.setTile(x, y, z, Tile.lightGem.id);

        for (int i = 0; i < 1500; ++i) {
            final int x2 = x + random.nextInt(8) - random.nextInt(8);
            final int y2 = y - random.nextInt(12);
            final int z2 = z + random.nextInt(8) - random.nextInt(8);
            if (level.getTile(x2, y2, z2) != 0) continue;

            int count = 0;
            for (int t = 0; t < 6; ++t) {
                int tile = 0;
                if (t == 0) tile = level.getTile(x2 - 1, y2, z2);
                if (t == 1) tile = level.getTile(x2 + 1, y2, z2);
                if (t == 2) tile = level.getTile(x2, y2 - 1, z2);
                if (t == 3) tile = level.getTile(x2, y2 + 1, z2);
                if (t == 4) tile = level.getTile(x2, y2, z2 - 1);
                if (t == 5) tile = level.getTile(x2, y2, z2 + 1);

                if (tile == Tile.lightGem.id) ++count;
            }

            if (count == 1) level.setTile(x2, y2, z2, Tile.lightGem.id);
        }

        return true;
    }
}
