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
        if (!level.isEmptyTile(x, y, z)) {
            return false;
        }
        if (level.getTile(x, y + 1, z) != Tile.hellRock.id) {
            return false;
        }
        level.setTile(x, y, z, Tile.lightGem.id);
        for (int i = 0; i < 1500; ++i) {
            final int n = x + random.nextInt(8) - random.nextInt(8);
            final int n2 = y - random.nextInt(12);
            final int n3 = z + random.nextInt(8) - random.nextInt(8);
            if (level.getTile(n, n2, n3) == 0) {
                int n4 = 0;
                for (int j = 0; j < 6; ++j) {
                    int n5 = 0;
                    if (j == 0) {
                        n5 = level.getTile(n - 1, n2, n3);
                    }
                    if (j == 1) {
                        n5 = level.getTile(n + 1, n2, n3);
                    }
                    if (j == 2) {
                        n5 = level.getTile(n, n2 - 1, n3);
                    }
                    if (j == 3) {
                        n5 = level.getTile(n, n2 + 1, n3);
                    }
                    if (j == 4) {
                        n5 = level.getTile(n, n2, n3 - 1);
                    }
                    if (j == 5) {
                        n5 = level.getTile(n, n2, n3 + 1);
                    }
                    if (n5 == Tile.lightGem.id) {
                        ++n4;
                    }
                }
                if (n4 == 1) {
                    level.setTile(n, n2, n3, Tile.lightGem.id);
                }
            }
        }
        return true;
    }
}
