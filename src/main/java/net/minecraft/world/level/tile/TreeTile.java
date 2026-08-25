// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import java.util.Random;
import net.minecraft.world.level.material.Material;

public class TreeTile extends Tile
{
    public static final int DARK_TRUNK = 1;
    public static final int BIRCH_TRUNK = 2;
    protected TreeTile(final int id) {
        super(id, Material.wood);
        this.tex = 20;
    }
    
    @Override
    public int getResourceCount(final Random random) {
        return 1;
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        return Tile.treeTrunk.id;
    }
    
    @Override
    public void playerDestroy(final Level level, final Player player, final int x, final int y, final int z, final int data) {
        super.playerDestroy(level, player, x, y, z, data);
    }
    
    @Override
    public void onRemove(final Level level, final int x, final int y, final int z) {
        final int n = 4;
        final int n2 = n + 1;
        if (level.hasChunksAt(x - n2, y - n2, z - n2, x + n2, y + n2, z + n2)) {
            for (int i = -n; i <= n; ++i) {
                for (int j = -n; j <= n; ++j) {
                    for (int k = -n; k <= n; ++k) {
                        if (level.getTile(x + i, y + j, z + k) == Tile.leaves.id) {
                            final int data = level.getData(x + i, y + j, z + k);
                            if ((data & 0x8) == 0x0) {
                                level.setDataNoUpdate(x + i, y + j, z + k, data | 0x8);
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public int getTexture(final int face, final int data) {
        if (face == 1) {
            return 21;
        }
        if (face == 0) {
            return 21;
        }
        if (data == 1) {
            return 116;
        }
        if (data == 2) {
            return 117;
        }
        return 20;
    }
    
    @Override
    protected int getSpawnResourcesAuxValue(final int data) {
        return data;
    }
}
