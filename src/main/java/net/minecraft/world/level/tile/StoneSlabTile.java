// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.Facing;
import net.minecraft.world.level.LevelSource;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class StoneSlabTile extends Tile
{
    public static final int STONE_SLAB = 0;
    public static final int SAND_SLAB = 1;
    public static final int WOOD_SLAB = 2;
    public static final int COBBLESTONE_SLAB = 3;
    public static final String[] SLAB_NAMES = new String[] { "stone", "sand", "wood", "cobble" };
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
        if (data == STONE_SLAB) {
            if (face <= Facing.UP) return 6;
            return 5;
        }
        else if (data == SAND_SLAB) {
            if (face == Facing.DOWN) return 208;
            if (face == Facing.UP) return 176;
            return 192;
        }
        else if (data == WOOD_SLAB) return 4;
        else if (data == COBBLESTONE_SLAB) return 16;
        return 6;
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
        if (this != Tile.stoneSlabHalf) super.onPlace(level, x, y, z);

        final int below = level.getTile(x, y - 1, z);
        final int data = level.getData(x, y, z);
        final int dataBelow = level.getData(x, y - 1, z);
        if (data != dataBelow) return;

        if (below == StoneSlabTile.stoneSlabHalf.id) {
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
    
    @Override
    public boolean shouldRenderFace(final LevelSource level, final int x, final int y, final int z, final int face) {
        if (this != Tile.stoneSlabHalf) super.shouldRenderFace(level, x, y, z, face);

        return face == Facing.UP || super.shouldRenderFace(level, x, y, z, face) && !(face != Facing.DOWN && level.getTile(x, y, z) == this.id);
    }

}
