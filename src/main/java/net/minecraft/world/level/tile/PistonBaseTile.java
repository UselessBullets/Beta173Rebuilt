// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import util.Mth;
import java.util.ArrayList;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.level.tile.entity.PistonPieceEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.Facing;
import net.minecraft.world.level.material.Material;

public class PistonBaseTile extends Tile
{
    public static final int EXTENDED_BIT = 8;
    public static final int UNDEFINED_FACING = 7;

    public static final float PLATFORM_THICKNESS = 4.0f;
    public static final int MAX_PUSH_DEPTH = 12;
    public static final int TRIGGER_EXTEND = 0;
    public static final int TRIGGER_CONTRACT = 1;

    public static final int EDGE_TEX = 108;
    public static final int PLATFORM_TEX = 107;
    public static final int PLATFORM_STICKY_TEX = 106;
    public static final int BACK_TEX = 109;
    public static final int INSIDE_TEX = 110;
    private boolean isSticky;
    private boolean ignoreUpdate;
    
    public PistonBaseTile(final int id, final int tex, final boolean isSticky) {
        super(id, tex, Material.piston);
        this.isSticky = isSticky;
        this.setSoundType(PistonBaseTile.SOUND_STONE);
        this.setDestroyTime(0.5f);
    }
    
    public int getPlatformTexture() {
        if (this.isSticky) return PLATFORM_STICKY_TEX;
        return PLATFORM_TEX;
    }
    
    @Override
    public int getTexture(final int face, final int data) {
        final int facing = getFacing(data);
        if (facing > 5) {
            return this.tex; // Useless - this is the platform tex
        }
        if (face == facing) {
            if (isExtended(data) || this.xx0 > 0.0 || this.yy0 > 0.0 || this.zz0 > 0.0 || this.xx1 < 1.0 || this.yy1 < 1.0 || this.zz1 < 1.0) {
                return INSIDE_TEX;
            }
            return this.tex; // Useless - this is the platform tex
        }
        else {
            if (face == Facing.OPPOSITE_FACING[facing]) {
                return BACK_TEX;
            }
            return EDGE_TEX;
        }
    }
    
    @Override
    public int getRenderShape() {
        return Tile.SHAPE_PISTON_BASE;
    }
    
    @Override
    public boolean isSolidRender() {
        return false;
    }
    
    @Override
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        return false;
    }
    
    @Override
    public void setPlacedBy(final Level level, final int x, final int y, final int z, final Mob by) {
        level.setData(x, y, z, getNewFacing(level, x, y, z, (Player)by));
        if (!level.isClientSide) {
            this.checkIfExtend(level, x, y, z);
        }
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        if (!level.isClientSide && !this.ignoreUpdate) {
            this.checkIfExtend(level, x, y, z);
        }
    }
    
    @Override
    public void onPlace(final Level level, final int x, final int y, final int z) {
        if (!level.isClientSide && level.getTileEntity(x, y, z) == null) {
            this.checkIfExtend(level, x, y, z);
        }
    }
    
    private void checkIfExtend(final Level level, final int x, final int y, final int z) {
        final int data = level.getData(x, y, z);
        final int facing = getFacing(data);
        final boolean neighborSignal = this.getNeighborSignal(level, x, y, z, facing);
        if (data == 7) {
            return;
        }
        if (neighborSignal && !isExtended(data)) {
            if (canPush(level, x, y, z, facing)) {
                level.setDataNoUpdate(x, y, z, facing | 0x8);
                level.tileEvent(x, y, z, 0, facing);
            }
        }
        else if (!neighborSignal && isExtended(data)) {
            level.setDataNoUpdate(x, y, z, facing);
            level.tileEvent(x, y, z, 1, facing);
        }
    }
    
    private boolean getNeighborSignal(final Level level, final int x, final int y, final int z, final int facing) {
        return (facing != 0 && level.getSignal(x, y - 1, z, 0)) || (facing != 1 && level.getSignal(x, y + 1, z, 1)) || (facing != 2 && level.getSignal(x, y, z - 1, 2)) || (facing != 3 && level.getSignal(x, y, z + 1, 3)) || (facing != 5 && level.getSignal(x + 1, y, z, 5)) || (facing != 4 && level.getSignal(x - 1, y, z, 4)) || level.getSignal(x, y, z, 0) || level.getSignal(x, y + 2, z, 1) || level.getSignal(x, y + 1, z - 1, 2) || level.getSignal(x, y + 1, z + 1, 3) || level.getSignal(x - 1, y + 1, z, 4) || level.getSignal(x + 1, y + 1, z, 5);
    }
    
    @Override
    public void triggerEvent(final Level level, int x, int y, int z, final int b0, final int b1) {
        this.ignoreUpdate = true;
        if (b0 == 0) {
            if (this.createPush(level, x, y, z, b1)) {
                level.setData(x, y, z, b1 | 0x8);
                level.playLocalSound(x + 0.5, y + 0.5, z + 0.5, "tile.piston.out", 0.5f, level.random.nextFloat() * 0.25f + 0.6f);
            }
        }
        else if (b0 == 1) {
            final TileEntity tileEntity = level.getTileEntity(x + Facing.STEP_X[b1], y + Facing.STEP_Y[b1], z + Facing.STEP_Z[b1]);
            if (tileEntity != null && tileEntity instanceof PistonPieceEntity) {
                ((PistonPieceEntity)tileEntity).finalTick();
            }
            level.setTileAndDataNoUpdate(x, y, z, Tile.pistonMovingPiece.id, b1);
            level.setTileEntity(x, y, z, PistonMovingPiece.newMovingPieceEntity(this.id, b1, b1, false, true));
            if (this.isSticky) {
                final int x2 = x + Facing.STEP_X[b1] * 2;
                final int y2 = y + Facing.STEP_Y[b1] * 2;
                final int z2 = z + Facing.STEP_Z[b1] * 2;
                int n = level.getTile(x2, y2, z2);
                int n2 = level.getData(x2, y2, z2);
                boolean b2 = false;
                if (n == Tile.pistonMovingPiece.id) {
                    final TileEntity tileEntity2 = level.getTileEntity(x2, y2, z2);
                    if (tileEntity2 != null && tileEntity2 instanceof PistonPieceEntity) {
                        final PistonPieceEntity pistonPieceEntity = (PistonPieceEntity)tileEntity2;
                        if (pistonPieceEntity.getFacing() == b1 && pistonPieceEntity.isExtending()) {
                            pistonPieceEntity.finalTick();
                            n = pistonPieceEntity.getId();
                            n2 = pistonPieceEntity.getData();
                            b2 = true;
                        }
                    }
                }
                if (!b2 && n > 0 && isPushable(n, level, x2, y2, z2, false) && (Tile.tiles[n].getPistonPushReaction() == 0 || n == Tile.pistonBase.id || n == Tile.pistonStickyBase.id)) {
                    this.ignoreUpdate = false;
                    level.setTile(x2, y2, z2, 0);
                    this.ignoreUpdate = true;
                    x += Facing.STEP_X[b1];
                    y += Facing.STEP_Y[b1];
                    z += Facing.STEP_Z[b1];
                    level.setTileAndDataNoUpdate(x, y, z, Tile.pistonMovingPiece.id, n2);
                    level.setTileEntity(x, y, z, PistonMovingPiece.newMovingPieceEntity(n, n2, b1, false, false));
                }
                else if (!b2) {
                    this.ignoreUpdate = false;
                    level.setTile(x + Facing.STEP_X[b1], y + Facing.STEP_Y[b1], z + Facing.STEP_Z[b1], 0);
                    this.ignoreUpdate = true;
                }
            }
            else {
                this.ignoreUpdate = false;
                level.setTile(x + Facing.STEP_X[b1], y + Facing.STEP_Y[b1], z + Facing.STEP_Z[b1], 0);
                this.ignoreUpdate = true;
            }
            level.playLocalSound(x + 0.5, y + 0.5, z + 0.5, "tile.piston.in", 0.5f, level.random.nextFloat() * 0.15f + 0.6f);
        }
        this.ignoreUpdate = false;
    }
    
    @Override
    public void updateShape(final LevelSource level, final int x, final int y, final int z) {
        final int data = level.getData(x, y, z);
        if (isExtended(data)) {
            switch (getFacing(data)) {
                case 0: {
                    this.setShape(0.0f, 0.25f, 0.0f, 1.0f, 1.0f, 1.0f);
                    break;
                }
                case 1: {
                    this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 0.75f, 1.0f);
                    break;
                }
                case 2: {
                    this.setShape(0.0f, 0.0f, 0.25f, 1.0f, 1.0f, 1.0f);
                    break;
                }
                case 3: {
                    this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.75f);
                    break;
                }
                case 4: {
                    this.setShape(0.25f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
                    break;
                }
                case 5: {
                    this.setShape(0.0f, 0.0f, 0.0f, 0.75f, 1.0f, 1.0f);
                    break;
                }
            }
        }
        else {
            this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        }
    }
    
    @Override
    public void updateDefaultShape() {
        this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    }
    
    @Override
    public void addAABBs(final Level level, final int x, final int y, final int z, final AABB box, final ArrayList boxes) {
        this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        super.addAABBs(level, x, y, z, box, boxes);
    }
    
    @Override
    public boolean isCubeShaped() {
        return false;
    }
    
    public static int getFacing(final int data) {
        return data & 0x7;
    }
    
    public static boolean isExtended(final int data) {
        return (data & 0x8) != 0x0;
    }
    
    private static int getNewFacing(final Level level, final int x, final int y, final int z, final Player player) {
        if (Mth.abs((float)player.x - x) < 2.0f && Mth.abs((float)player.z - z) < 2.0f) {
            final double n = player.y + 1.82 - player.heightOffset;
            if (n - y > 2.0) {
                return 1;
            }
            if (y - n > 0.0) {
                return 0;
            }
        }
        final int n2 = Mth.floor(player.yRot * 4.0f / 360.0f + 0.5) & 0x3;
        if (n2 == 0) {
            return 2;
        }
        if (n2 == 1) {
            return 5;
        }
        if (n2 == 2) {
            return 3;
        }
        if (n2 == 3) {
            return 4;
        }
        return 0;
    }
    
    private static boolean isPushable(final int block, final Level level, final int cx, final int cy, final int cz, final boolean allowDestroyable) {
        if (block == Tile.obsidian.id) {
            return false;
        }
        if (block == Tile.pistonBase.id || block == Tile.pistonStickyBase.id) {
            if (isExtended(level.getData(cx, cy, cz))) {
                return false;
            }
        }
        else {
            if (Tile.tiles[block].getDestroySpeed() == -1.0f) {
                return false;
            }
            if (Tile.tiles[block].getPistonPushReaction() == 2) {
                return false;
            }
            if (!allowDestroyable && Tile.tiles[block].getPistonPushReaction() == 1) {
                return false;
            }
        }
        return level.getTileEntity(cx, cy, cz) == null;
    }
    
    private static boolean canPush(final Level level, final int sx, final int sy, final int sz, final int facing) {
        int n = sx + Facing.STEP_X[facing];
        int n2 = sy + Facing.STEP_Y[facing];
        int n3 = sz + Facing.STEP_Z[facing];
        for (int i = 0; i < 13; ++i) {
            if (n2 <= 0 || n2 >= 127) {
                return false;
            }
            final int tile = level.getTile(n, n2, n3);
            if (tile == 0) {
                break;
            }
            if (!isPushable(tile, level, n, n2, n3, true)) {
                return false;
            }
            if (Tile.tiles[tile].getPistonPushReaction() == 1) {
                break;
            }
            if (i == 12) {
                return false;
            }
            n += Facing.STEP_X[facing];
            n2 += Facing.STEP_Y[facing];
            n3 += Facing.STEP_Z[facing];
        }
        return true;
    }
    
    private boolean createPush(final Level level, final int sx, final int sy, final int sz, final int facing) {
        int x = sx + Facing.STEP_X[facing];
        int y = sy + Facing.STEP_Y[facing];
        int z = sz + Facing.STEP_Z[facing];
        for (int i = 0; i < 13; ++i) {
            if (y <= 0 || y >= 127) {
                return false;
            }
            final int tile = level.getTile(x, y, z);
            if (tile == 0) {
                break;
            }
            if (!isPushable(tile, level, x, y, z, true)) {
                return false;
            }
            if (Tile.tiles[tile].getPistonPushReaction() == 1) {
                Tile.tiles[tile].spawnResources(level, x, y, z, level.getData(x, y, z));
                level.setTile(x, y, z, 0);
                break;
            }
            if (i == 12) {
                return false;
            }
            x += Facing.STEP_X[facing];
            y += Facing.STEP_Y[facing];
            z += Facing.STEP_Z[facing];
        }
        while (x != sx || y != sy || z != sz) {
            final int n = x - Facing.STEP_X[facing];
            final int n2 = y - Facing.STEP_Y[facing];
            final int n3 = z - Facing.STEP_Z[facing];
            final int tile2 = level.getTile(n, n2, n3);
            final int data = level.getData(n, n2, n3);
            if (tile2 == this.id && n == sx && n2 == sy && n3 == sz) {
                level.setTileAndDataNoUpdate(x, y, z, Tile.pistonMovingPiece.id, facing | (this.isSticky ? 8 : 0));
                level.setTileEntity(x, y, z, PistonMovingPiece.newMovingPieceEntity(Tile.pistonExtension.id, facing | (this.isSticky ? 8 : 0), facing, true, false));
            }
            else {
                level.setTileAndDataNoUpdate(x, y, z, Tile.pistonMovingPiece.id, data);
                level.setTileEntity(x, y, z, PistonMovingPiece.newMovingPieceEntity(tile2, data, facing, true, false));
            }
            x = n;
            y = n2;
            z = n3;
        }
        return true;
    }
}
