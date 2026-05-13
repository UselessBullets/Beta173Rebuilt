// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

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
    public static final int[][] HEAD_DIRECTION_OFFSETS;
    
    public BedTile(final int id) {
        super(id, 134, Material.cloth);
        this.setShape();
    }
    
    @Override
    public boolean use(final Level level, int x, final int y, int z, final Player player) {
        if (level.isClientSide) {
            return true;
        }
        int n = level.getData(x, y, z);
        if (!isHeadPiece(n)) {
            final int direction = getDirection(n);
            x += BedTile.HEAD_DIRECTION_OFFSETS[direction][0];
            z += BedTile.HEAD_DIRECTION_OFFSETS[direction][1];
            if (level.getTile(x, y, z) != this.id) {
                return true;
            }
            n = level.getData(x, y, z);
        }
        if (!level.dimension.mayRespawn()) {
            final double n2 = x + 0.5;
            final double n3 = y + 0.5;
            final double n4 = z + 0.5;
            level.setTile(x, y, z, 0);
            final int direction2 = getDirection(n);
            x += BedTile.HEAD_DIRECTION_OFFSETS[direction2][0];
            z += BedTile.HEAD_DIRECTION_OFFSETS[direction2][1];
            if (level.getTile(x, y, z) == this.id) {
                level.setTile(x, y, z, 0);
                final double n5 = (n2 + x + 0.5) / 2.0;
                final double n6 = (n3 + y + 0.5) / 2.0;
                final double n7 = (n4 + z + 0.5) / 2.0;
            }
            level.explode(null, x + 0.5f, y + 0.5f, z + 0.5f, 5.0f, true);
            return true;
        }
        if (isOccupied(n)) {
            Player player2 = null;
            for (final Player player3 : level.players) {
                if (player3.isSleeping()) {
                    final Pos bedPosition = player3.bedPosition;
                    if (bedPosition.x != x || bedPosition.y != y || bedPosition.z != z) {
                        continue;
                    }
                    player2 = player3;
                }
            }
            if (player2 != null) {
                player.displayClientMessage("tile.bed.occupied");
                return true;
            }
            setOccupied(level, x, y, z, false);
        }
        final Player.BedSleepingResult startSleepInBed = player.startSleepInBed(x, y, z);
        if (startSleepInBed == Player.BedSleepingResult.OK) {
            setOccupied(level, x, y, z, true);
            return true;
        }
        if (startSleepInBed == Player.BedSleepingResult.NOT_POSSIBLE_NOW) {
            player.displayClientMessage("tile.bed.noSleep");
        }
        return true;
    }
    
    @Override
    public int getTexture(final int face, final int data) {
        if (face == 0) {
            return Tile.wood.tex;
        }
        final int n = Direction.RELATIVE_DIRECTION_FACING[getDirection(data)][face];
        if (isHeadPiece(data)) {
            if (n == 2) {
                return this.tex + 2 + 16;
            }
            if (n == 5 || n == 4) {
                return this.tex + 1 + 16;
            }
            return this.tex + 1;
        }
        else {
            if (n == 3) {
                return this.tex - 1 + 16;
            }
            if (n == 5 || n == 4) {
                return this.tex + 16;
            }
            return this.tex;
        }
    }
    
    @Override
    public int getRenderShape() {
        return 14;
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
        this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 0.5625f, 1.0f);
    }
    
    public static int getDirection(final int data) {
        return data & 0x3;
    }
    
    public static boolean isHeadPiece(final int data) {
        return (data & 0x8) != 0x0;
    }
    
    public static boolean isOccupied(final int data) {
        return (data & 0x4) != 0x0;
    }
    
    public static void setOccupied(final Level level, final int x, final int y, final int z, final boolean occupied) {
        final int data = level.getData(x, y, z);
        int data2;
        if (occupied) {
            data2 = (data | 0x4);
        }
        else {
            data2 = (data & 0xFFFFFFFB);
        }
        level.setData(x, y, z, data2);
    }
    
    public static Pos findStandUpPosition(final Level level, final int x, final int y, final int z, int skipCount) {
        final int direction = getDirection(level.getData(x, y, z));
        for (int i = 0; i <= 1; ++i) {
            final int n = x - BedTile.HEAD_DIRECTION_OFFSETS[direction][0] * i - 1;
            final int n2 = z - BedTile.HEAD_DIRECTION_OFFSETS[direction][1] * i - 1;
            final int n3 = n + 2;
            final int n4 = n2 + 2;
            for (int j = n; j <= n3; ++j) {
                for (int k = n2; k <= n4; ++k) {
                    if (level.isSolidBlockingTile(j, y - 1, k) && level.isEmptyTile(j, y, k) && level.isEmptyTile(j, y + 1, k)) {
                        if (skipCount <= 0) {
                            return new Pos(j, y, k);
                        }
                        --skipCount;
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
        return 1;
    }
    
    static {
        HEAD_DIRECTION_OFFSETS = new int[][] { { 0, 1 }, { -1, 0 }, { 0, -1 }, { 1, 0 } };
    }
}
