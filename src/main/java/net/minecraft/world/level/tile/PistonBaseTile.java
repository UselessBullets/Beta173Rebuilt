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
        if (face == Facing.OPPOSITE_FACING[facing]) {
            return BACK_TEX;
        }

        return EDGE_TEX;
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
        int targetData = getNewFacing(level, x, y, z, (Player) by);
        level.setData(x, y, z, targetData);
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
        final boolean extend = this.getNeighborSignal(level, x, y, z, facing);

        if (data == UNDEFINED_FACING) {
            return;
        }

        if (extend && !isExtended(data)) {
            if (canPush(level, x, y, z, facing)) {
                level.setDataNoUpdate(x, y, z, facing | EXTENDED_BIT);
                level.tileEvent(x, y, z, TRIGGER_EXTEND, facing);
            }
        }
        else if (!extend && isExtended(data)) {
            level.setDataNoUpdate(x, y, z, facing);
            level.tileEvent(x, y, z, TRIGGER_CONTRACT, facing);
        }
    }

    /**
     * This method checks neighbor signals for this block and the block above,
     * and directly beneath. However, it avoids checking blocks that would be
     * pushed by this block.
     *
     * @param level
     * @param x
     * @param y
     * @param z
     * @return
     */
    private boolean getNeighborSignal(final Level level, final int x, final int y, final int z, final int facing) {
        // check adjacent neighbors, but not in push direction
        if (facing != Facing.DOWN) if (level.getSignal(x, y - 1, z, Facing.DOWN)) return true;
        if (facing != Facing.UP) if (level.getSignal(x, y + 1, z, Facing.UP)) return true;
        if (facing != Facing.NORTH) if (level.getSignal(x, y, z - 1, Facing.NORTH)) return true;
        if (facing != Facing.SOUTH) if (level.getSignal(x, y, z + 1, Facing.SOUTH)) return true;
        if (facing != Facing.EAST) if (level.getSignal(x + 1, y, z, Facing.EAST)) return true;
        if (facing != Facing.WEST) if (level.getSignal(x - 1, y, z, Facing.WEST)) return true;

        // check signals above
        if (level.getSignal(x, y, z, 0)) return true;
        if (level.getSignal(x, y + 2, z, 1)) return true;
        if (level.getSignal(x, y + 1, z - 1, 2)) return true;
        if (level.getSignal(x, y + 1, z + 1, 3)) return true;
        if (level.getSignal(x - 1, y + 1, z, 4)) return true;
        if (level.getSignal(x + 1, y + 1, z, 5)) return true;

        return false;
    }
    
    @Override
    public void triggerEvent(final Level level, int x, int y, int z, final int param1, final int facing) {
        this.ignoreUpdate = true;

        if (param1 == TRIGGER_EXTEND) {
            if (this.createPush(level, x, y, z, facing)) {
                level.setData(x, y, z, facing | EXTENDED_BIT);
                level.playLocalSound(x + 0.5, y + 0.5, z + 0.5, "tile.piston.out", 0.5f, level.random.nextFloat() * 0.25f + 0.6f);
            }
        }
        else if (param1 == TRIGGER_CONTRACT) {
            final TileEntity prevTileEntity = level.getTileEntity(x + Facing.STEP_X[facing], y + Facing.STEP_Y[facing], z + Facing.STEP_Z[facing]);
            if (prevTileEntity != null && prevTileEntity instanceof PistonPieceEntity) {
                ((PistonPieceEntity)prevTileEntity).finalTick();
            }

            level.setTileAndDataNoUpdate(x, y, z, Tile.pistonMovingPiece.id, facing);
            level.setTileEntity(x, y, z, PistonMovingPiece.newMovingPieceEntity(this.id, facing, facing, false, true));

            // sticky movement
            if (this.isSticky) {
                final int twoX = x + Facing.STEP_X[facing] * 2;
                final int twoY = y + Facing.STEP_Y[facing] * 2;
                final int twoZ = z + Facing.STEP_Z[facing] * 2;
                int block = level.getTile(twoX, twoY, twoZ);
                int blockData = level.getData(twoX, twoY, twoZ);
                boolean pistonPiece = false;

                if (block == Tile.pistonMovingPiece.id) {
                    // the block two steps away is a moving piston block piece,
                    // so replace it with the real data, since it's probably
                    // this piston which is changing too fast
                    final TileEntity tileEntity = level.getTileEntity(twoX, twoY, twoZ);
                    if (tileEntity != null && tileEntity instanceof PistonPieceEntity) {
                        final PistonPieceEntity ppe = (PistonPieceEntity)tileEntity;
                        if (ppe.getFacing() == facing && ppe.isExtending()) {
                            // force the tile to air before pushing
                            ppe.finalTick();
                            block = ppe.getId();
                            blockData = ppe.getData();
                            pistonPiece = true;
                        }
                    }
                }

                if (!pistonPiece && block > 0 && isPushable(block, level, twoX, twoY, twoZ, false)
                        && (Tile.tiles[block].getPistonPushReaction() == Material.PUSH_NORMAL || block == Tile.pistonBase.id || block == Tile.pistonStickyBase.id)) {
                    this.ignoreUpdate = false;
                    level.setTile(twoX, twoY, twoZ, 0);
                    this.ignoreUpdate = true;

                    x += Facing.STEP_X[facing];
                    y += Facing.STEP_Y[facing];
                    z += Facing.STEP_Z[facing];

                    level.setTileAndDataNoUpdate(x, y, z, Tile.pistonMovingPiece.id, blockData);
                    level.setTileEntity(x, y, z, PistonMovingPiece.newMovingPieceEntity(block, blockData, facing, false, false));
                }
                else if (!pistonPiece) {
                    this.ignoreUpdate = false;
                    level.setTile(x + Facing.STEP_X[facing], y + Facing.STEP_Y[facing], z + Facing.STEP_Z[facing], 0);
                    this.ignoreUpdate = true;
                }
            }
            else {
                this.ignoreUpdate = false;
                level.setTile(x + Facing.STEP_X[facing], y + Facing.STEP_Y[facing], z + Facing.STEP_Z[facing], 0);
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
            final float thickness = PLATFORM_THICKNESS / 16.0f;
            switch (getFacing(data)) {
                case Facing.DOWN:
                    this.setShape(0.0f, thickness, 0.0f, 1.0f, 1.0f, 1.0f);
                    break;
                case Facing.UP:
                    this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1 - thickness, 1.0f);
                    break;
                case Facing.NORTH:
                    this.setShape(0.0f, 0.0f, thickness, 1.0f, 1.0f, 1.0f);
                    break;
                case Facing.SOUTH:
                    this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1 - thickness);
                    break;
                case Facing.WEST:
                    this.setShape(thickness, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
                    break;
                case Facing.EAST:
                    this.setShape(0.0f, 0.0f, 0.0f, 1 - thickness, 1.0f, 1.0f);
                    break;
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
        return (data & EXTENDED_BIT) != 0x0;
    }
    
    private static int getNewFacing(final Level level, final int x, final int y, final int z, final Player player) {
        if (Mth.abs((float)player.x - x) < 2.0f && Mth.abs((float)player.z - z) < 2.0f) {
            // If the player is above the block, the slot is on the top
            final double py = player.y + 1.82 - player.heightOffset;
            if (py - y > 2.0) {
                return Facing.UP;
            }
            // If the player is below the block, the slot is on the bottom
            if (y - py > 0.0) {
                return Facing.DOWN;
            }
        }
        // The slot is on the side
        final int i = Mth.floor(player.yRot * 4.0f / 360.0f + 0.5) & 0x3;
        if (i == 0) return Facing.NORTH;
        if (i == 1) return Facing.EAST;
        if (i == 2) return Facing.SOUTH;
        if (i == 3) return Facing.WEST;
        return 0;
    }
    
    private static boolean isPushable(final int block, final Level level, final int cx, final int cy, final int cz, final boolean allowDestroyable) {
        // special case for obsidian
        if (block == Tile.obsidian.id) {
            return false;
        }

        if (block == Tile.pistonBase.id || block == Tile.pistonStickyBase.id) {
            // special case for piston bases
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

        if (level.getTileEntity(cx, cy, cz) == null) {
            // may not push tile entities
            return true;
        }

        return false;
    }
    
    private static boolean canPush(final Level level, final int sx, final int sy, final int sz, final int facing) {
        int cx = sx + Facing.STEP_X[facing];
        int cy = sy + Facing.STEP_Y[facing];
        int cz = sz + Facing.STEP_Z[facing];
        
        for (int i = 0; i < MAX_PUSH_DEPTH + 1; ++i) {
            if (cy <= 0 || cy >= (Level.MAX_HEIGHT - 1)) {
                // out of bounds
                return false;
            }

            final int block = level.getTile(cx, cy, cz);
            if (block == 0) {
                break;
            }

            if (!isPushable(block, level, cx, cy, cz, true)) {
                return false;
            }

            if (Tile.tiles[block].getPistonPushReaction() == Material.PUSH_DESTROY) {
                break;
            }

            if (i == MAX_PUSH_DEPTH) {
                // we've reached the maximum push depth
                // without finding air or a breakable block
                return false;
            }

            cx += Facing.STEP_X[facing];
            cy += Facing.STEP_Y[facing];
            cz += Facing.STEP_Z[facing];
        }

        return true;
    }
    
    private boolean createPush(final Level level, final int sx, final int sy, final int sz, final int facing) {
        int cx = sx + Facing.STEP_X[facing];
        int cy = sy + Facing.STEP_Y[facing];
        int cz = sz + Facing.STEP_Z[facing];

        for (int i = 0; i < (MAX_PUSH_DEPTH + 1); ++i) {
            if (cy <= 0 || cy >= (Level.MAX_HEIGHT - 1)) {
                // out of bounds
                return false;
            }

            final int block = level.getTile(cx, cy, cz);
            if (block == 0) {
                break;
            }

            if (!isPushable(block, level, cx, cy, cz, true)) {
                return false;
            }

            if (Tile.tiles[block].getPistonPushReaction() == Material.PUSH_DESTROY) {
                // this block is destroyed when pushed
                Tile.tiles[block].spawnResources(level, cx, cy, cz, level.getData(cx, cy, cz));
                // setting the tile to air is actually superflous, but
                // helps vs multiplayer problems
                level.setTile(cx, cy, cz, 0);
                break;
            }

            if (i == MAX_PUSH_DEPTH) {
                // we've reached the maximum push depth
                // without finding air or a breakable block
                return false;
            }

            cx += Facing.STEP_X[facing];
            cy += Facing.STEP_Y[facing];
            cz += Facing.STEP_Z[facing];
        }

        while (cx != sx || cy != sy || cz != sz) {
            final int nx = cx - Facing.STEP_X[facing];
            final int ny = cy - Facing.STEP_Y[facing];
            final int nz = cz - Facing.STEP_Z[facing];

            final int block = level.getTile(nx, ny, nz);
            final int data = level.getData(nx, ny, nz);

            if (block == this.id && nx == sx && ny == sy && nz == sz) {
                level.setTileAndDataNoUpdate(cx, cy, cz, Tile.pistonMovingPiece.id, facing | (this.isSticky ? PistonExtensionTile.STICKY_BIT : 0));
                level.setTileEntity(cx, cy, cz, PistonMovingPiece.newMovingPieceEntity(Tile.pistonExtension.id, facing | (this.isSticky ? PistonExtensionTile.STICKY_BIT : 0), facing, true, false));
            }
            else {
                level.setTileAndDataNoUpdate(cx, cy, cz, Tile.pistonMovingPiece.id, data);
                level.setTileEntity(cx, cy, cz, PistonMovingPiece.newMovingPieceEntity(block, data, facing, true, false));
            }

            cx = nx;
            cy = ny;
            cz = nz;
        }

        return true;
    }
}
