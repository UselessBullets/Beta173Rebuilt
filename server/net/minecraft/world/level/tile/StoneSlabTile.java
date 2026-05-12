// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class StoneSlabTile extends Tile
{
    public static final String[] SLAB_NAMES;
    private boolean fullSize;
    
    public StoneSlabTile(final int id, final boolean fullSize) {
        super(id, 6, Material.stone);
        if (!(this.fullSize = fullSize)) {
            this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 0.5f, 1.0f);
        }
        this.setLightBlock(255);
    }
    
    @Override
    public int getTexture(final int face, final int data) {
        if (data == 0) {
            if (face <= 1) {
                return 6;
            }
            return 5;
        }
        else if (data == 1) {
            if (face == 0) {
                return 208;
            }
            if (face == 1) {
                return 176;
            }
            return 192;
        }
        else {
            if (data == 2) {
                return 4;
            }
            if (data == 3) {
                return 16;
            }
            return 6;
        }
    }
    
    @Override
    public int getTexture(final int face) {
        return this.getTexture(face, 0);
    }
    
    @Override
    public boolean isSolidRender() {
        return this.fullSize;
    }
    
    @Override
    public void onPlace(final Level level, final int x, final int y, final int z) {
        if (this != Tile.stoneSlabHalf) {
            super.onPlace(level, x, y, z);
        }
        final int tile = level.getTile(x, y - 1, z);
        final int data = level.getData(x, y, z);
        if (data != level.getData(x, y - 1, z)) {
            return;
        }
        if (tile == StoneSlabTile.stoneSlabHalf.id) {
            level.setTile(x, y, z, 0);
            level.setTileAndData(x, y - 1, z, Tile.stoneSlab.id, data);
        }
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        return Tile.stoneSlabHalf.id;
    }
    
    @Override
    public int getResourceCount(final Random random) {
        if (this.fullSize) {
            return 2;
        }
        return 1;
    }
    
    @Override
    protected int getSpawnResourcesAuxValue(final int data) {
        return data;
    }
    
    @Override
    public boolean isCubeShaped() {
        return this.fullSize;
    }
    
    static {
        SLAB_NAMES = new String[] { "stone", "sand", "wood", "cobble" };
    }
}
