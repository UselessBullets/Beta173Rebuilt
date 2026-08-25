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
            final int x2 = x + random.nextInt(8) - random.nextInt(8);
            final int y2 = y + random.nextInt(4) - random.nextInt(4);
            final int z2 = z + random.nextInt(8) - random.nextInt(8);
            if (level.isEmptyTile(x2, y2, z2) && level.getTile(x2, y2 - 1, z2) == Tile.grass.id) {
                if (Tile.pumpkin.mayPlace(level, x2, y2, z2)) {
                    level.setTileAndDataNoUpdate(x2, y2, z2, Tile.pumpkin.id, random.nextInt(4));
                }
            }
        }

        return true;
    }
}
