// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class Sponge extends Tile
{
    public static final int RANGE = 2;
    protected Sponge(final int id) {
        super(id, Material.sponge);
        this.tex = 48;
    }
    
    @Override
    public void onPlace(final Level level, final int x, final int y, final int z) {
        int r = RANGE;
        for (int xx = x - r; xx <= x + r; ++xx) {
            for (int yy = y - r; yy <= y + r; ++yy) {
                for (int zz = z - r; zz <= z + r; ++zz) {
                    if (level.getMaterial(xx, yy, zz) == Material.water) {
                        // Useless - This class is emptied out in LCE but presumably there is commented out code to delete water here, like what was in classic MC
                    }
                }
            }
        }
    }
    
    @Override
    public void onRemove(final Level level, final int x, final int y, final int z) {
        int r = RANGE;
        for (int xx = x - r; xx <= x + r; ++xx) {
            for (int yy = y - r; yy <= y + r; ++yy) {
                for (int zz = z - r; zz <= z + r; ++zz) {
                    level.updateNeighborsAt(xx, yy, zz, level.getTile(xx, yy, zz));
                }
            }
        }
    }
}
