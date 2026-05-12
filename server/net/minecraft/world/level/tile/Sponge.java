// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class Sponge extends Tile
{
    protected Sponge(final int id) {
        super(id, Material.sponge);
        this.tex = 48;
    }
    
    @Override
    public void onPlace(final Level level, final int x, final int y, final int z) {
        for (int n = 2, i = x - n; i <= x + n; ++i) {
            for (int j = y - n; j <= y + n; ++j) {
                for (int k = z - n; k <= z + n; ++k) {
                    if (level.getMaterial(i, j, k) == Material.water) {}
                }
            }
        }
    }
    
    @Override
    public void onRemove(final Level level, final int x, final int y, final int z) {
        for (int n = 2, i = x - n; i <= x + n; ++i) {
            for (int j = y - n; j <= y + n; ++j) {
                for (int k = z - n; k <= z + n; ++k) {
                    level.updateNeighborsAt(i, j, k, level.getTile(i, j, k));
                }
            }
        }
    }
}
