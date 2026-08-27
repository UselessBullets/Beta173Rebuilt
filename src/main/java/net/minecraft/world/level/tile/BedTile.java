// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.Facing;
import net.minecraft.world.item.Item;
import java.util.Random;
import net.minecraft.world.level.LevelSource;
import net.minecraft.Direction;
import net.minecraft.Pos;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class BedTile extends Tile
{
    private static final int PART_FOOT = 0;
    private static final int PART_HEAD = 1;
    public static final int DIRECTION_MASK = 0b0011;
    public static final int HEAD_PIECE_DATA = 0b1000;
    public static final int OCCUPIED_DATA = 0b100;
    public static final int[][] HEAD_DIRECTION_OFFSETS = new int[][] {
            { 0, 1 }, { -1, 0 }, { 0, -1 }, { 1, 0 }
    };
    
    public BedTile(final int id) {
        super(id, 134, Material.cloth);
        this.setShape();
    }
    
    @Override
    public boolean use(final Level level, int x, final int y, int z, final Player player) {
        if (level.isClientSide) return true;

        int data = level.getData(x, y, z);

        if (!isHeadPiece(data)) {
            // fetch head piece instead
            final int direction = getDirection(data);
            x += BedTile.HEAD_DIRECTION_OFFSETS[direction][0];
            z += BedTile.HEAD_DIRECTION_OFFSETS[direction][1];
            if (level.getTile(x, y, z) != this.id) {
                return true;
            }
            data = level.getData(x, y, z);
        }

        if (!level.dimension.mayRespawn()) {
            double xc = x + 0.5;
            double yc = y + 0.5;
            double zc = z + 0.5;
            level.setTile(x, y, z, 0);
            final int direction = getDirection(data);
            x += BedTile.HEAD_DIRECTION_OFFSETS[direction][0];
            z += BedTile.HEAD_DIRECTION_OFFSETS[direction][1];
            if (level.getTile(x, y, z) == this.id) {
                level.setTile(x, y, z, 0);
                xc = (xc + x + 0.5) / 2.0;
                yc = (yc + y + 0.5) / 2.0;
                zc = (zc + z + 0.5) / 2.0;
            }
            level.explode(null, x + 0.5f, y + 0.5f, z + 0.5f, 5.0f, true);
            return true;
        }

        if (isOccupied(data)) {
            Player sleepingPlayer = null;
            for (final Player p : level.players) {
                if (p.isSleeping()) {
                    final Pos pos = p.bedPosition;
                    if (pos.x == x && pos.y == y && pos.z == z) {
                        sleepingPlayer = p;
                    }
                }
            }

            if (sleepingPlayer == null) {
                setOccupied(level, x, y, z, false);
            } else {
                player.displayClientMessage("tile.bed.occupied");

                return true;
            }
        }

        final Player.BedSleepingResult result = player.startSleepInBed(x, y, z);
        if (result == Player.BedSleepingResult.OK) {
            setOccupied(level, x, y, z, true);
            return true;
        }

        if (result == Player.BedSleepingResult.NOT_POSSIBLE_NOW) {
            player.displayClientMessage("tile.bed.noSleep");
        }

        return true;
    }
    
    @Override
    public int getTexture(final int face, final int data) {
        if (face == Facing.DOWN) {
            return Tile.wood.tex;
        }

        int direction = getDirection(data);
        int tileFacing = Direction.RELATIVE_DIRECTION_FACING[direction][face];

        if (isHeadPiece(data)) {
            if (tileFacing == Facing.NORTH) {
                return this.tex + 2 + 16;
            }
            if (tileFacing == Facing.EAST || tileFacing == Facing.WEST) {
                return this.tex + 1 + 16;
            }
            return this.tex + 1;
        }
        else {
            if (tileFacing == Facing.SOUTH) {
                return this.tex - 1 + 16;
            }
            if (tileFacing == Facing.EAST || tileFacing == Facing.WEST) {
                return this.tex + 16;
            }
            return this.tex;
        }
    }
    
    @Override
    public int getRenderShape() {
        return Tile.SHAPE_BED;
    }
    
    @Override
    public boolean isCubeShaped() {
        return false;
    }
    
    @Override
    public boolean isSolidRender() {
        return false;
    }
    
    @Override
    public void updateShape(final LevelSource level, final int x, final int y, final int z) {
        this.setShape();
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        final int data = level.getData(x, y, z);
        final int direction = getDirection(data);

        if (isHeadPiece(data)) {
            if (level.getTile(x - BedTile.HEAD_DIRECTION_OFFSETS[direction][0], y, z - BedTile.HEAD_DIRECTION_OFFSETS[direction][1]) != this.id) {
                level.setTile(x, y, z, 0);
            }
        }
        else if (level.getTile(x + BedTile.HEAD_DIRECTION_OFFSETS[direction][0], y, z + BedTile.HEAD_DIRECTION_OFFSETS[direction][1]) != this.id) {
            level.setTile(x, y, z, 0);
            if (!level.isClientSide) {
                this.spawnResources(level, x, y, z, data);
            }
        }
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        if (isHeadPiece(data)) {
            return 0;
        }
        return Item.bed.id;
    }
    
    private void setShape() {
        this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 9 / 16.0f, 1.0f);
    }
    
    public static int getDirection(final int data) {
        return data & DIRECTION_MASK;
    }
    
    public static boolean isHeadPiece(final int data) {
        return (data & HEAD_PIECE_DATA) != 0x0;
    }
    
    public static boolean isOccupied(final int data) {
        return (data & OCCUPIED_DATA) != 0x0;
    }
    
    public static void setOccupied(final Level level, final int x, final int y, final int z, final boolean occupied) {
        int data = level.getData(x, y, z);
        if (occupied) {
            data = (data | OCCUPIED_DATA);
        }
        else {
            data = (data & ~OCCUPIED_DATA);
        }
        level.setData(x, y, z, data);
    }
    
    public static Pos findStandUpPosition(final Level level, final int x, final int y, final int z, int skipCount) {
        int data = level.getData(x, y, z);
        int direction = getDirection(data);

        // try to find a clear location near the bed
        for (int step = 0; step <= 1; step++) {
            final int startX = x - BedTile.HEAD_DIRECTION_OFFSETS[direction][0] * step - 1;
            final int startZ = z - BedTile.HEAD_DIRECTION_OFFSETS[direction][1] * step - 1;
            final int endX = startX + 2;
            final int endZ = startZ + 2;

            for (int standX = startX; standX <= endX; ++standX) {
                for (int standZ = startZ; standZ <= endZ; ++standZ) {
                    if (level.isSolidBlockingTile(standX, y - 1, standZ) &&
                            level.isEmptyTile(standX, y, standZ) &&
                            level.isEmptyTile(standX, y + 1, standZ)) {
                        if (skipCount > 0) {
                            skipCount--;
                            continue;
                        }
                        return new Pos(standX, y, standZ);
                    }
                }
            }
        }

        return null;
    }
    
    @Override
    public void spawnResources(final Level level, final int x, final int y, final int z, final int data, final float odds) {
        if (!isHeadPiece(data)) {
            super.spawnResources(level, x, y, z, data, odds);
        }
    }
    
    @Override
    public int getPistonPushReaction() {
        return Material.PUSH_DESTROY;
    }

}
