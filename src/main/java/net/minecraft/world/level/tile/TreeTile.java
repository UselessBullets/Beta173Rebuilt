// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.Facing;
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
        final int r = LeafTile.REQUIRED_WOOD_RANGE;
        final int r2 = r + 1;

        if (level.hasChunksAt(x - r2, y - r2, z - r2, x + r2, y + r2, z + r2)) {
            for (int xo = -r; xo <= r; ++xo) {
                for (int yo = -r; yo <= r; ++yo) {
                    for (int zo = -r; zo <= r; ++zo) {
                        int t = level.getTile(x + xo, y + yo, z + zo);
                        if (t == Tile.leaves.id) {
                            final int currentData = level.getData(x + xo, y + yo, z + zo);
                            if ((currentData & LeafTile.UPDATE_LEAF_BIT) == 0x0) {
                                level.setDataNoUpdate(x + xo, y + yo, z + zo, currentData | LeafTile.UPDATE_LEAF_BIT);
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public int getTexture(final int face, final int data) {
        if (face == Facing.UP || face == Facing.DOWN) return 21;
        if (data == DARK_TRUNK) return 116;
        if (data == BIRCH_TRUNK) return 117;
        return 20;
    }
    
    @Override
    protected int getSpawnResourcesAuxValue(final int data) {
        return data;
    }
}
