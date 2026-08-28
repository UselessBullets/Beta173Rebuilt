// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.level.LevelSource;
import java.util.ArrayList;
import net.minecraft.world.phys.AABB;
import java.util.Random;
import net.minecraft.Facing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class PistonExtensionTile extends Tile
{
    // i'm reusing this block for the sticky pistons
    public static final int STICKY_BIT = 8;
    private int overrideTopTexture = -1;
    
    public PistonExtensionTile(final int id, final int tex) {
        super(id, tex, Material.piston);
        this.setSoundType(PistonExtensionTile.SOUND_STONE);
        this.setDestroyTime(0.5f);
    }
    
    public void setOverrideTopTexture(final int overrideTopTexture) {
        this.overrideTopTexture = overrideTopTexture;
    }
    
    public void clearOverrideTopTexture() {
        this.overrideTopTexture = -1;
    }
    
    @Override
    public void onRemove(final Level level, int x, int y, int z) {
        super.onRemove(level, x, y, z);
        final int facing = Facing.OPPOSITE_FACING[getFacing(level.getData(x, y, z))];
        x += Facing.STEP_X[facing];
        y += Facing.STEP_Y[facing];
        z += Facing.STEP_Z[facing];

        final int t = level.getTile(x, y, z);

        if (t == Tile.pistonBase.id || t == Tile.pistonStickyBase.id) {
            final int data = level.getData(x, y, z);
            if (PistonBaseTile.isExtended(data)) {
                Tile.tiles[t].spawnResources(level, x, y, z, data);
                level.setTile(x, y, z, 0);
            }
        }
    }
    
    @Override
    public int getTexture(final int face, final int data) {
        final int facing = getFacing(data);
        if (face == facing) {
            if (this.overrideTopTexture >= 0) {
                return this.overrideTopTexture;
            }
            if ((data & STICKY_BIT) != 0x0) {
                return this.tex - 1;
            }
            return this.tex;
        }
        else {
            if (face == Facing.OPPOSITE_FACING[facing]) {
                return PistonBaseTile.PLATFORM_TEX;
            }
            return PistonBaseTile.EDGE_TEX;
        }
    }
    
    @Override
    public int getRenderShape() {
        return Tile.SHAPE_PISTON_EXTENSION;
    }
    
    @Override
    public boolean isSolidRender() {
        return false;
    }
    
    @Override
    public boolean isCubeShaped() {
        return false;
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        return false;
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z, final int face) {
        return false;
    }
    
    @Override
    public int getResourceCount(final Random random) {
        return 0;
    }
    
    @Override
    public void addAABBs(final Level level, final int x, final int y, final int z, final AABB box, final ArrayList boxes) {
        int data = level.getData(x, y, z);

        final float thickness = PistonBaseTile.PLATFORM_THICKNESS / 16.0f;
        final float smallEdge1 = (8.0f - (PistonBaseTile.PLATFORM_THICKNESS / 2.0f)) / 16.0f;
        final float smallEdge2 = (8.0f + (PistonBaseTile.PLATFORM_THICKNESS / 2.0f)) / 16.0f;
        final float largeEdge1 = (8.0f - PistonBaseTile.PLATFORM_THICKNESS) / 16.0f;
        final float largeEdge2 = (8.0f + PistonBaseTile.PLATFORM_THICKNESS) / 16.0f;

        switch (getFacing(data)) {
            case 0: {
                this.setShape(0, 0, 0, 1, thickness, 1);
                super.addAABBs(level, x, y, z, box, boxes);
                this.setShape(smallEdge1, thickness, smallEdge1, smallEdge2, 1, smallEdge2);
                super.addAABBs(level, x, y, z, box, boxes);
                break;
            }
            case 1: {
                this.setShape(0, 1 - thickness, 0, 1, 1, 1);
                super.addAABBs(level, x, y, z, box, boxes);
                this.setShape(smallEdge1, 0, smallEdge1, smallEdge2, 1 - thickness, smallEdge2);
                super.addAABBs(level, x, y, z, box, boxes);
                break;
            }
            case 2: {
                this.setShape(0, 0, 0, 1, 1, thickness);
                super.addAABBs(level, x, y, z, box, boxes);
                this.setShape(largeEdge1, smallEdge1, thickness, largeEdge2, smallEdge2, 1);
                super.addAABBs(level, x, y, z, box, boxes);
                break;
            }
            case 3: {
                this.setShape(0, 0, 1 - thickness, 1, 1, 1);
                super.addAABBs(level, x, y, z, box, boxes);
                this.setShape(largeEdge1, smallEdge1, 0, largeEdge2, smallEdge2, 1 - thickness);
                super.addAABBs(level, x, y, z, box, boxes);
                break;
            }
            case 4: {
                this.setShape(0, 0, 0, thickness, 1, 1);
                super.addAABBs(level, x, y, z, box, boxes);
                this.setShape(smallEdge1, largeEdge1, thickness, smallEdge2, largeEdge2, 1);
                super.addAABBs(level, x, y, z, box, boxes);
                break;
            }
            case 5: {
                this.setShape(1 - thickness, 0, 0, 1, 1, 1);
                super.addAABBs(level, x, y, z, box, boxes);
                this.setShape(0, smallEdge1, largeEdge1, 1 - thickness, smallEdge2, largeEdge2);
                super.addAABBs(level, x, y, z, box, boxes);
                break;
            }
        }
        this.setShape(0, 0, 0, 1, 1, 1);
    }
    
    @Override
    public void updateShape(final LevelSource level, final int x, final int y, final int z) {
        int data = level.getData(x, y, z);

        final float thickness = PistonBaseTile.PLATFORM_THICKNESS / 16.0f;

        switch (getFacing(data)) {
            case Facing.DOWN:
                this.setShape(0.0f, 0.0f, 0.0f, 1.0f, thickness, 1.0f);
                break;
            case Facing.UP:
                this.setShape(0.0f, 1 - thickness, 0.0f, 1.0f, 1.0f, 1.0f);
                break;
            case Facing.NORTH:
                this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, thickness);
                break;
            case Facing.SOUTH:
                this.setShape(0.0f, 0.0f, 1 - thickness, 1.0f, 1.0f, 1.0f);
                break;
            case Facing.WEST:
                this.setShape(0.0f, 0.0f, 0.0f, thickness, 1.0f, 1.0f);
                break;
            case Facing.EAST:
                this.setShape(1 - thickness, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
                break;
        }
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        final int facing = getFacing(level.getData(x, y, z));
        final int tile = level.getTile(x - Facing.STEP_X[facing], y - Facing.STEP_Y[facing], z - Facing.STEP_Z[facing]);
        if (tile != Tile.pistonBase.id && tile != Tile.pistonStickyBase.id) {
            level.setTile(x, y, z, 0);
        }
        else {
            Tile.tiles[tile].neighborChanged(level, x - Facing.STEP_X[facing], y - Facing.STEP_Y[facing], z - Facing.STEP_Z[facing], type);
        }
    }
    
    public static int getFacing(final int data) {
        return data & 0x7;
    }
}
