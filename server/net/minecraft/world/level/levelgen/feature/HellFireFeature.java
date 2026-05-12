// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.tile.Tile;
import java.util.Random;
import net.minecraft.world.level.Level;

public class HellFireFeature extends Feature
{
    @Override
    public boolean place(final Level level, final Random random, final int x, final int y, final int z) {
        for (int i = 0; i < 64; ++i) {
            final int x2 = x + random.nextInt(8) - random.nextInt(8);
            final int n = y + random.nextInt(4) - random.nextInt(4);
            final int z2 = z + random.nextInt(8) - random.nextInt(8);
            if (level.isEmptyTile(x2, n, z2)) {
                if (level.getTile(x2, n - 1, z2) == Tile.hellRock.id) {
                    level.setTile(x2, n, z2, Tile.fire.id);
                }
            }
        }
        return true;
    }
}
