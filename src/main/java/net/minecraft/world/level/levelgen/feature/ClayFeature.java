// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen.feature;

import util.Mth;
import net.minecraft.world.level.material.Material;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.Tile;

public class ClayFeature extends Feature
{
    private int tile;
    private int count;
    
    public ClayFeature(final int count) {
        this.tile = Tile.clay.id;
        this.count = count;
    }
    
    @Override
    public boolean place(final Level level, final Random random, final int x, final int y, final int z) {
        if (level.getMaterial(x, y, z) != Material.water) {
            return false;
        }
        final float n = random.nextFloat() * Mth.PI;
        final double n2 = x + 8 + Mth.sin(n) * this.count / 8.0f;
        final double n3 = x + 8 - Mth.sin(n) * this.count / 8.0f;
        final double n4 = z + 8 + Mth.cos(n) * this.count / 8.0f;
        final double n5 = z + 8 - Mth.cos(n) * this.count / 8.0f;
        final double n6 = y + random.nextInt(3) + 2;
        final double n7 = y + random.nextInt(3) + 2;
        for (int i = 0; i <= this.count; ++i) {
            final double n8 = n2 + (n3 - n2) * i / this.count;
            final double n9 = n6 + (n7 - n6) * i / this.count;
            final double n10 = n4 + (n5 - n4) * i / this.count;
            final double n11 = random.nextDouble() * this.count / 16.0;
            final double n12 = (Mth.sin(i * 3.1415927f / this.count) + 1.0f) * n11 + 1.0;
            final double n13 = (Mth.sin(i * 3.1415927f / this.count) + 1.0f) * n11 + 1.0;
            final int floor = Mth.floor(n8 - n12 / 2.0);
            final int floor2 = Mth.floor(n8 + n12 / 2.0);
            final int floor3 = Mth.floor(n9 - n13 / 2.0);
            final int floor4 = Mth.floor(n9 + n13 / 2.0);
            final int floor5 = Mth.floor(n10 - n12 / 2.0);
            final int floor6 = Mth.floor(n10 + n12 / 2.0);
            for (int j = floor; j <= floor2; ++j) {
                for (int k = floor3; k <= floor4; ++k) {
                    for (int l = floor5; l <= floor6; ++l) {
                        final double n14 = (j + 0.5 - n8) / (n12 / 2.0);
                        final double n15 = (k + 0.5 - n9) / (n13 / 2.0);
                        final double n16 = (l + 0.5 - n10) / (n12 / 2.0);
                        if (n14 * n14 + n15 * n15 + n16 * n16 < 1.0 && level.getTile(j, k, l) == Tile.sand.id) {
                            level.setTileNoUpdate(j, k, l, this.tile);
                        }
                    }
                }
            }
        }
        return true;
    }
}
