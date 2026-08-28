// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer;

import net.minecraft.Facing;
import net.minecraft.world.level.tile.DoorTile;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import util.Mth;
import net.minecraft.world.level.tile.LiquidTile;
import net.minecraft.world.level.tile.RedStoneDustTile;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.tile.PistonExtensionTile;
import net.minecraft.world.level.tile.PistonBaseTile;
import net.minecraft.world.level.tile.DiodeTile;
import net.minecraft.Direction;
import net.minecraft.world.level.tile.BedTile;
import net.minecraft.world.level.tile.RailTile;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.LevelSource;

import static org.lwjgl.opengl.GL11.*;

public class TileRenderer
{
    private LevelSource level;
    private int fixedTexture = -1;
    private boolean xFlipTexture = false;
    private boolean noCulling = false;
    public static boolean fancy = true;
    public boolean setColor = true;
    private static final int FLIP_NONE = 0, FLIP_CW = 1, FLIP_CCW = 2, FLIP_180 = 3;
    private int northFlip = FLIP_NONE;
    private int southFlip = FLIP_NONE;
    private int eastFlip = FLIP_NONE;
    private int westFlip = FLIP_NONE;
    private int upFlip = FLIP_NONE;
    private int downFlip = FLIP_NONE;
    private boolean applyAmbienceOcclusion;
    private float ll000, llx00, ll0y0, ll00z, llX00, ll0Y0, ll00Z;
    private float llxyz, llxy0, llxyZ, ll0yz, ll0yZ, llXyz, llXy0;
    private float llXyZ, llxYz, llxY0, llxYZ, ll0Yz, llXYz, llXY0;
    private float ll0YZ, llXYZ, llx0z, llX0z, llx0Z, llX0Z;
    private int blsmooth = 1;
    private float c1r, c2r, c3r, c4r;
    private float c1g, c2g, c3g, c4g;
    private float c1b, c2b, c3b, c4b;
    private boolean llTrans0Yz, llTransXY0, llTransxY0, llTrans0YZ;
    private boolean llTransx0z, llTransX0Z, llTransx0Z, llTransX0z;
    private boolean llTrans0yz, llTransXy0, llTransxy0, llTrans0yZ;
    
    public TileRenderer(final LevelSource level) {
        this.level = level;
    }
    
    public TileRenderer() {
    }
    
    public void tesselateInWorld(final Tile tile, final int x, final int y, final int z, final int fixedTexture) {
        this.fixedTexture = fixedTexture;
        this.tesselateInWorld(tile, x, y, z);
        this.fixedTexture = -1;
    }
    
    public void tesselateInWorldNoCulling(final Tile tile, final int x, final int y, final int z) {
        this.noCulling = true;
        this.tesselateInWorld(tile, x, y, z);
        this.noCulling = false;
    }
    
    public boolean tesselateInWorld(final Tile tt, final int x, final int y, final int z) {
        final int shape = tt.getRenderShape();
        tt.updateShape(this.level, x, y, z);
        if (shape == Tile.SHAPE_BLOCK) return this.tesselateBlockInWorld(tt, x, y, z);
        if (shape == Tile.SHAPE_WATER) return this.tesselateWaterInWorld(tt, x, y, z);
        if (shape == Tile.SHAPE_CACTUS) return this.tesselateCactusInWorld(tt, x, y, z);
        if (shape == Tile.SHAPE_CROSS_TEXTURE) return this.tesselateCrossInWorld(tt, x, y, z);
        if (shape == Tile.SHAPE_ROWS) return this.tesselateRowInWorld(tt, x, y, z);
        if (shape == Tile.SHAPE_TORCH) return this.tesselateTorchInWorld(tt, x, y, z);
        if (shape == Tile.SHAPE_FIRE) return this.tesselateFireInWorld(tt, x, y, z);
        if (shape == Tile.SHAPE_RED_DUST) return this.tesselateDustInWorld(tt, x, y, z);
        if (shape == Tile.SHAPE_LADDER) return this.tesselateLadderInWorld(tt, x, y, z);
        if (shape == Tile.SHAPE_DOOR) return this.tesselateDoorInWorld(tt, x, y, z);
        if (shape == Tile.SHAPE_RAIL) return this.tesselateRailInWorld((RailTile) tt, x, y, z);
        if (shape == Tile.SHAPE_STAIRS) return this.tesselateStairsInWorld(tt, x, y, z);
        if (shape == Tile.SHAPE_FENCE) return this.tesselateFenceInWorld(tt, x, y, z);
        if (shape == Tile.SHAPE_LEVER) return this.tesselateLeverInWorld(tt, x, y, z);
        if (shape == Tile.SHAPE_BED) return this.tesselateBedInWorld(tt, x, y, z);
        if (shape == Tile.SHAPE_DIODE) return this.tesselateDiodeInWorld(tt, x, y, z);
        if (shape == Tile.SHAPE_PISTON_BASE) return this.tesselatePistonBaseInWorld(tt, x, y, z, false);
        if (shape == Tile.SHAPE_PISTON_EXTENSION) return this.tesselatePistonExtensionInWorld(tt, x, y, z, true);
        return false;
    }
    
    private boolean tesselateBedInWorld(final Tile tt, final int x, final int y, final int z) {
        final Tesselator t = Tesselator.instance;
        final int data = this.level.getData(x, y, z);
        final int direction = BedTile.getDirection(data);
        final boolean isHead = BedTile.isHeadPiece(data);

        final float c10 = 0.5f;
        final float c11 = 1.0f;
        final float c2 = 0.8f;
        final float c3 = 0.6f;

        final float r10 = c10;
        final float g10 = c10;
        final float b10 = c10;

        final float r11 = c11;
        final float g11 = c11;
        final float b11 = c11;

        final float r2 = c2;
        final float g2 = c2;
        final float b2 = c2;

        final float r3 = c3;
        final float g3 = c3;
        final float b3 = c3;

        // Render wooden underside
        final float centerBrightness;
        {
            centerBrightness = tt.getBrightness(this.level, x, y, z);
            t.color(r10 * centerBrightness, g10 * centerBrightness, b10 * centerBrightness);

            final int tex = tt.getTexture(this.level, x, y, z, Facing.DOWN);
            final int texX = (tex & 0xF) << 4;
            final int texY = tex & 0xF0;

            final double u0 = texX / 256.0f;
            final double u1 = (texX + 16 - 0.01) / 256.0;
            final double v0 = texY / 256.0f;
            final double v1 = (texY + 16 - 0.01) / 256.0;

            final double x0 = x + tt.xx0;
            final double x1 = x + tt.xx1;
            final double y0 = y + tt.yy0 + 0.1875;
            final double z0 = z + tt.zz0;
            final double z1 = z + tt.zz1;

            t.vertexUV(x0, y0, z1, u0, v1);
            t.vertexUV(x0, y0, z0, u0, v0);
            t.vertexUV(x1, y0, z0, u1, v0);
            t.vertexUV(x1, y0, z1, u1, v1);
        }

        // render bed top
        final float brightness = tt.getBrightness(this.level, x, y + 1, z);
        t.color(r11 * brightness, g11 * brightness, b11 * brightness);

        final int tex = tt.getTexture(this.level, x, y, z, 1);
        final int texX = (tex & 0xF) << 4;
        final int texY = tex & 0xF0;

        final double u0 = texX / 256.0f;
        final double u1 = (texX + 16 - 0.01) / 256.0;
        final double v0 = texY / 256.0f;
        final double v1 = (texY + 16 - 0.01) / 256.0;

        double topLeftU = u0;
        double topRightU = u1;
        double topLeftV = v0;
        double topRightV = v0;
        double bottomLeftU = u0;
        double bottomRightU = u1;
        double bottomLeftV = v1;
        double bottomRightV = v1;

        if (direction == Direction.SOUTH) {
            // rotate 90 degrees clockwise
            topRightU = u0;
            topLeftV = v1;
            bottomLeftU = u1;
            bottomRightV = v0;
        }
        else if (direction == Direction.NORTH) {
            // rotate 90 degrees counter-clockwise
            topLeftU = u1;
            topRightV = v1;
            bottomRightU = u0;
            bottomLeftV = v0;
        }
        else if (direction == Direction.EAST) {
            // rotate 180 degrees
            topLeftU = u1;
            topRightV = v1;
            bottomRightU = u0;
            bottomLeftV = v0;
            topRightU = u0;
            topLeftV = v1;
            bottomLeftU = u1;
            bottomRightV = v0;
        }

        final double x0 = x + tt.xx0;
        final double x1 = x + tt.xx1;
        final double y1 = y + tt.yy1;
        final double z0 = z + tt.zz0;
        final double z1 = z + tt.zz1;

        t.vertexUV(x1, y1, z1, bottomLeftU, bottomLeftV);
        t.vertexUV(x1, y1, z0, topLeftU, topLeftV);
        t.vertexUV(x0, y1, z0, topRightU, topRightV);
        t.vertexUV(x0, y1, z1, bottomRightU, bottomRightV);

        // determine which edge to skip (the one between foot and head piece)
        int skipEdge = Direction.DIRECTION_FACING[direction];
        if (isHead) {
            skipEdge = Direction.DIRECTION_FACING[Direction.DIRECTION_OPPOSITE[direction]];
        }

        // and which edge to x-flip
        int flipEdge = Facing.WEST;
        switch (direction) {
            case Direction.NORTH:
                break;
            case Direction.SOUTH: {
                flipEdge = Facing.EAST;
                break;
            }
            case Direction.EAST: {
                flipEdge = Facing.NORTH;
                break;
            }
            case Direction.WEST: {
                flipEdge = Facing.SOUTH;
                break;
            }
        }

        if (skipEdge != Facing.NORTH && (this.noCulling || tt.shouldRenderFace(this.level, x, y, z - 1, Facing.NORTH))) {
            float br = tt.getBrightness(this.level, x, y, z - 1);
            if (tt.zz0 > 0.0) br = centerBrightness;
            t.color(r2 * br, g2 * br, b2 * br);

            this.xFlipTexture = flipEdge == Facing.NORTH;
            this.renderNorth(tt, x, y, z, tt.getTexture(this.level, x, y, z, 2));
        }

        if (skipEdge != Facing.SOUTH && (this.noCulling || tt.shouldRenderFace(this.level, x, y, z + 1, Facing.SOUTH))) {
            float br = tt.getBrightness(this.level, x, y, z + 1);
            if (tt.zz1 < 1.0) br = centerBrightness;
            t.color(r2 * br, g2 * br, b2 * br);

            this.xFlipTexture = flipEdge == Facing.SOUTH;
            this.renderSouth(tt, x, y, z, tt.getTexture(this.level, x, y, z, 3));
        }

        if (skipEdge != Facing.WEST && (this.noCulling || tt.shouldRenderFace(this.level, x - 1, y, z, Facing.WEST))) {
            float br = tt.getBrightness(this.level, x - 1, y, z);
            if (tt.xx0 > 0.0) br = centerBrightness;
            t.color(r3 * br, g3 * br, b3 * br);

            this.xFlipTexture = flipEdge == Facing.WEST;
            this.renderWest(tt, x, y, z, tt.getTexture(this.level, x, y, z, 4));
        }

        if (skipEdge != Facing.EAST && (this.noCulling || tt.shouldRenderFace(this.level, x + 1, y, z, Facing.EAST))) {
            float br = tt.getBrightness(this.level, x + 1, y, z);
            if (tt.xx1 < 1.0) br = centerBrightness;
            t.color(r3 * br, g3 * br, b3 * br);

            this.xFlipTexture = flipEdge == Facing.EAST;
            this.renderEast(tt, x, y, z, tt.getTexture(this.level, x, y, z, 5));
        }
        this.xFlipTexture = false;
        return true;
    }
    
    public boolean tesselateTorchInWorld(final Tile tt, final int x, final int y, final int z) {
        final int dir = this.level.getData(x, y, z);

        final Tesselator t = Tesselator.instance;

        float br = tt.getBrightness(this.level, x, y, z);
        if (Tile.lightEmission[tt.id] > 0) br = 1.0f;
        t.color(br, br, br);

        final double r = 0.4f;
        final double r2 = 0.5 - r;
        final double h = 0.2f;

        if (dir == 1) this.tesselateTorch(tt, x - r2, y + h, z, -r, 0.0);
        else if (dir == 2) this.tesselateTorch(tt, x + r2, y + h, z, r, 0.0);
        else if (dir == 3) this.tesselateTorch(tt, x, y + h, z - r2, 0.0, -r);
        else if (dir == 4) this.tesselateTorch(tt, x, y + h, z + r2, 0.0, r);
        else this.tesselateTorch(tt, x, y, z, 0.0, 0.0);

        return true;
    }
    
    private boolean tesselateDiodeInWorld(final Tile tt, final int x, final int y, final int z) {
        final int data = this.level.getData(x, y, z);
        final int dir = data & DiodeTile.DIRECTION_MASK;
        final int delay = (data & DiodeTile.DELAY_MASK) >> DiodeTile.DELAY_SHIFT;

        // render half-block edges
        this.tesselateBlockInWorld(tt, x, y, z);

        final Tesselator t = Tesselator.instance;

        float br = tt.getBrightness(this.level, x, y, z);
        if (Tile.lightEmission[tt.id] > 0) br = (br + 1.0f) * 0.5f;
        t.color(br, br, br);

        {
            double h = -3.0 / 16.0f;
            double transmitterX = 0.0;
            double transmitterZ = 0.0;
            double receiverX = 0.0;
            double receiverZ = 0.0;
            switch (dir) {
                case 0: {
                    receiverZ = -5.0f / 16.0f;
                    transmitterZ = DiodeTile.DELAY_RENDER_OFFSETS[delay];
                    break;
                }
                case 2: {
                    receiverZ = 5.0f / 16.0f;
                    transmitterZ = -DiodeTile.DELAY_RENDER_OFFSETS[delay];
                    break;
                }
                case 3: {
                    receiverX = -5.0f / 16.0f;
                    transmitterX = DiodeTile.DELAY_RENDER_OFFSETS[delay];
                    break;
                }
                case 1: {
                    receiverX = 5.0f / 16.0f;
                    transmitterX = -DiodeTile.DELAY_RENDER_OFFSETS[delay];
                    break;
                }
            }

            // render transmitter
            this.tesselateTorch(tt, x + transmitterX, y + h, z + transmitterZ, 0.0, 0.0);
            // render receiver
            this.tesselateTorch(tt, x + receiverX, y + h, z + receiverZ, 0.0, 0.0);
        }

        final int tex = tt.getTexture(Facing.UP);
        final int texX = (tex & 0xF) << 4;
        final int texY = tex & 0xF0;

        final double u0 = texX / 256.0f;
        final double u1 = (texX + 15.99f) / 256.0f;
        final double v0 = texY / 256.0f;
        final double v1 = (texY + 15.99f) / 256.0f;

        final float r = 2.0f / 16.0f;

        float x0 = (float)(x + 1);
        float x1 = (float)(x + 1);
        float x2 = (float)(x + 0);
        float x3 = (float)(x + 0);

        float z0 = (float)(z + 0);
        float z1 = (float)(z + 1);
        float z2 = (float)(z + 1);
        float z3 = (float)(z + 0);

        float y0 = y + r;

        if (dir == Direction.NORTH) {
            // rotate 180 degrees
            x1 = (x0 = (float)(x + 0));
            x3 = (x2 = (float)(x + 1));
            z3 = (z0 = (float)(z + 1));
            z2 = (z1 = (float)(z + 0));
        }
        else if (dir == Direction.EAST) {
            // rotate 90 degrees counter-clockwise
            x3 = (x0 = (float)(x + 0));
            x2 = (x1 = (float)(x + 1));
            z1 = (z0 = (float)(z + 0));
            z3 = (z2 = (float)(z + 1));
        }
        else if (dir == Direction.WEST) {
            // rotate 90 degrees clockwise
            x3 = (x0 = (float)(x + 1));
            x2 = (x1 = (float)(x + 0));
            z1 = (z0 = (float)(z + 1));
            z3 = (z2 = (float)(z + 0));
        }

        t.vertexUV(x3, y0, z3, u0, v0);
        t.vertexUV(x2, y0, z2, u0, v1);
        t.vertexUV(x1, y0, z1, u1, v1);
        t.vertexUV(x0, y0, z0, u1, v0);
        return true;
    }
    
    public void tesselatePistonBaseForceExtended(final Tile tile, final int x, final int y, final int z) {
        this.noCulling = true;
        this.tesselatePistonBaseInWorld(tile, x, y, z, true);
        this.noCulling = false;
    }
    
    private boolean tesselatePistonBaseInWorld(final Tile tt, final int x, final int y, final int z, final boolean forceExtended) {
        final int data = this.level.getData(x, y, z);
        final boolean extended = forceExtended || (data & PistonBaseTile.EXTENDED_BIT) != 0;
        final int facing = PistonBaseTile.getFacing(data);

        final float thickness = PistonBaseTile.PLATFORM_THICKNESS / 16.0f;

        if (extended) {
            switch (facing) {
                case Facing.DOWN: {
                    this.northFlip = FLIP_180;
                    this.southFlip = FLIP_180;
                    this.eastFlip = FLIP_180;
                    this.westFlip = FLIP_180;
                    tt.setShape(0.0f, thickness, 0.0f, 1.0f, 1.0f, 1.0f);
                    break;
                }
                case Facing.UP: {
                    tt.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1 - thickness, 1.0f);
                    break;
                }
                case Facing.NORTH: {
                    this.eastFlip = FLIP_CW;
                    this.westFlip = FLIP_CCW;
                    tt.setShape(0.0f, 0.0f, thickness, 1.0f, 1.0f, 1.0f);
                    break;
                }
                case Facing.SOUTH: {
                    this.eastFlip = FLIP_CCW;
                    this.westFlip = FLIP_CW;
                    this.upFlip = FLIP_180;
                    this.downFlip = FLIP_180;
                    tt.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1 - thickness);
                    break;
                }
                case Facing.WEST: {
                    this.northFlip = FLIP_CW;
                    this.southFlip = FLIP_CCW;
                    this.upFlip = FLIP_CCW;
                    this.downFlip = FLIP_CW;
                    tt.setShape(thickness, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
                    break;
                }
                case Facing.EAST: {
                    this.northFlip = FLIP_CCW;
                    this.southFlip = FLIP_CW;
                    this.upFlip = FLIP_CW;
                    this.downFlip = FLIP_CCW;
                    tt.setShape(0.0f, 0.0f, 0.0f, 1 - thickness, 1.0f, 1.0f);
                    break;
                }
            }
            this.tesselateBlockInWorld(tt, x, y, z);
            this.northFlip = FLIP_NONE;
            this.southFlip = FLIP_NONE;
            this.eastFlip = FLIP_NONE;
            this.westFlip = FLIP_NONE;
            this.upFlip = FLIP_NONE;
            this.downFlip = FLIP_NONE;
            tt.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        }
        else {
            switch (facing) {
                case Facing.DOWN: {
                    this.northFlip = FLIP_180;
                    this.southFlip = FLIP_180;
                    this.eastFlip = FLIP_180;
                    this.westFlip = FLIP_180;
                }
                case Facing.NORTH: {
                    this.eastFlip = FLIP_CW;
                    this.westFlip = FLIP_CCW;
                    break;
                }
                case Facing.SOUTH: {
                    this.eastFlip = FLIP_CCW;
                    this.westFlip = FLIP_CW;
                    this.upFlip = FLIP_180;
                    this.downFlip = FLIP_180;
                    break;
                }
                case Facing.WEST: {
                    this.northFlip = FLIP_CW;
                    this.southFlip = FLIP_CCW;
                    this.upFlip = FLIP_CCW;
                    this.downFlip = FLIP_CW;
                    break;
                }
                case Facing.EAST: {
                    this.northFlip = FLIP_CCW;
                    this.southFlip = FLIP_CW;
                    this.upFlip = FLIP_CW;
                    this.downFlip = FLIP_CCW;
                    break;
                }
            }
            this.tesselateBlockInWorld(tt, x, y, z);
            this.northFlip = FLIP_NONE;
            this.southFlip = FLIP_NONE;
            this.eastFlip = FLIP_NONE;
            this.westFlip = FLIP_NONE;
            this.upFlip = FLIP_NONE;
            this.downFlip = FLIP_NONE;
        }

        return true;
    }
    
    private void renderPistonArmUpDown(final double x0, final double x1, final double y0, final double y1, final double z0, final double z1, final float br, final double armLengthPixels) {
        int armTex = PistonBaseTile.EDGE_TEX;
        if (this.fixedTexture >= 0) armTex = this.fixedTexture;

        final int texX = (armTex & 0xF) << 4;
        final int texY = armTex & 0xF0;
        final Tesselator t = Tesselator.instance;

        // upwards arm
        final double u00 = (texX + 0) / 256.0f;
        final double v00 = (texY + 0) / 256.0f;
        final double u11 = (texX + armLengthPixels - 0.01) / 256.0;
        final double v11 = (texY + 4.0f - 0.01) / 256.0;

        t.color(br, br, br);

        t.vertexUV(x0, y1, z0, u11, v00);
        t.vertexUV(x0, y0, z0, u00, v00);
        t.vertexUV(x1, y0, z1, u00, v11);
        t.vertexUV(x1, y1, z1, u11, v11);
    }
    
    private void renderPistonArmNorthSouth(final double x0, final double x1, final double y0, final double y1, final double z0, final double z1, final float br, final double armLengthPixels) {
        int armTex = PistonBaseTile.EDGE_TEX;
        if (this.fixedTexture >= 0) armTex = this.fixedTexture;

        final int texX = (armTex & 0xF) << 4;
        final int texY = armTex & 0xF0;
        final Tesselator t = Tesselator.instance;

        // upwards arm
        final double u00 = (texX + 0) / 256.0f;
        final double v00 = (texY + 0) / 256.0f;
        final double u11 = (texX + armLengthPixels - 0.01) / 256.0;
        final double v11 = (texY + 4.0f - 0.01) / 256.0;

        t.color(br, br, br);

        t.vertexUV(x0, y0, z1, u11, v00);
        t.vertexUV(x0, y0, z0, u00, v00);
        t.vertexUV(x1, y1, z0, u00, v11);
        t.vertexUV(x1, y1, z1, u11, v11);
    }
    
    private void renderPistonArmEastWest(final double x0, final double x1, final double y0, final double y1, final double z0, final double z1, final float br, final double armLengthPixels) {
        int armTex = PistonBaseTile.EDGE_TEX;
        if (this.fixedTexture >= 0) armTex = this.fixedTexture;

        final int texX = (armTex & 0xF) << 4;
        final int texY = armTex & 0xF0;
        final Tesselator t = Tesselator.instance;

        // upwards arm
        final double u00 = (texX + 0) / 256.0f;
        final double v00 = (texY + 0) / 256.0f;
        final double u11 = (texX + armLengthPixels - 0.01) / 256.0;
        final double v11 = (texY + 4.0f - 0.01) / 256.0;

        t.color(br, br, br);

        t.vertexUV(x1, y0, z0, u11, v00);
        t.vertexUV(x0, y0, z0, u00, v00);
        t.vertexUV(x0, y1, z1, u00, v11);
        t.vertexUV(x1, y1, z1, u11, v11);
    }
    
    public void tesselatePistonArmNoCulling(final Tile tile, final int x, final int y, final int z, final boolean fullArm) {
        this.noCulling = true;
        this.tesselatePistonExtensionInWorld(tile, x, y, z, fullArm);
        this.noCulling = false;
    }
    
    private boolean tesselatePistonExtensionInWorld(final Tile tt, final int x, final int y, final int z, final boolean fullArm) {
        int data = this.level.getData(x, y, z);
        int facing = PistonExtensionTile.getFacing(data);

        final float thickness = PistonBaseTile.PLATFORM_THICKNESS / 16.0f;
        final float leftEdge = (8.0f - (PistonBaseTile.PLATFORM_THICKNESS / 2.0f)) / 16.0f;
        final float rightEdge = (8.0f + (PistonBaseTile.PLATFORM_THICKNESS / 2.0f)) / 16.0f;
        float br = tt.getBrightness(this.level, x, y, z);
        float armLength = fullArm ? 1.0f : 0.5f;
        double armLengthPixels = fullArm ? 16.0 : 8.0;

        switch (facing) {
            case Facing.DOWN: {
                this.northFlip = FLIP_180;
                this.southFlip = FLIP_180;
                this.eastFlip = FLIP_180;
                this.westFlip = FLIP_180;
                tt.setShape(0.0f, 0.0f, 0.0f, 1.0f, thickness, 1.0f);
                this.tesselateBlockInWorld(tt, x, y, z);

                this.renderPistonArmUpDown(x + leftEdge, x + rightEdge, y + thickness, y + thickness + armLength, z + rightEdge, z + rightEdge, br * 0.8f, armLengthPixels);
                this.renderPistonArmUpDown(x + rightEdge, x + leftEdge, y + thickness, y + thickness + armLength, z + leftEdge, z + leftEdge, br * 0.8f, armLengthPixels);
                this.renderPistonArmUpDown(x + leftEdge, x + leftEdge, y + thickness, y + thickness + armLength, z + leftEdge, z + rightEdge, br * 0.6f, armLengthPixels);
                this.renderPistonArmUpDown(x + rightEdge, x + rightEdge, y + thickness, y + thickness + armLength, z + rightEdge, z + leftEdge, br * 0.6f, armLengthPixels);
                break;
            }
            case Facing.UP: {
                tt.setShape(0.0f, 1.0f - thickness, 0.0f, 1.0f, 1.0f, 1.0f);
                this.tesselateBlockInWorld(tt, x, y, z);

                this.renderPistonArmUpDown(x + leftEdge, x + rightEdge, y - thickness + 1.0f - armLength, y - thickness + 1.0f, z + rightEdge, z + rightEdge, br * 0.8f, armLengthPixels);
                this.renderPistonArmUpDown(x + rightEdge, x + leftEdge, y - thickness + 1.0f - armLength, y - thickness + 1.0f, z + leftEdge, z + leftEdge, br * 0.8f, armLengthPixels);
                this.renderPistonArmUpDown(x + leftEdge, x + leftEdge, y - thickness + 1.0f - armLength, y - thickness + 1.0f, z + leftEdge, z + rightEdge, br * 0.6f, armLengthPixels);
                this.renderPistonArmUpDown(x + rightEdge, x + rightEdge, y - thickness + 1.0f - armLength, y - thickness + 1.0f, z + rightEdge, z + leftEdge, br * 0.6f, armLengthPixels);
                break;
            }
            case Facing.NORTH: {
                this.eastFlip = FLIP_CW;
                this.westFlip = FLIP_CCW;
                tt.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, thickness);
                this.tesselateBlockInWorld(tt, x, y, z);

                this.renderPistonArmNorthSouth(x + leftEdge, x + leftEdge, y + rightEdge, y + leftEdge, z + thickness, z + thickness + armLength, br * 0.6f, armLengthPixels);
                this.renderPistonArmNorthSouth(x + rightEdge, x + rightEdge, y + leftEdge, y + rightEdge, z + thickness, z + thickness + armLength, br * 0.6f, armLengthPixels);
                this.renderPistonArmNorthSouth(x + leftEdge, x + rightEdge, y + leftEdge, y + leftEdge, z + thickness, z + thickness + armLength, br * 0.5f, armLengthPixels);
                this.renderPistonArmNorthSouth(x + rightEdge, x + leftEdge, y + rightEdge, y + rightEdge, z + thickness, z + thickness + armLength, br, armLengthPixels);
                break;
            }
            case Facing.SOUTH: {
                this.eastFlip = FLIP_CCW;
                this.westFlip = FLIP_CW;
                this.upFlip = FLIP_180;
                this.downFlip = FLIP_180;
                tt.setShape(0.0f, 0.0f, 1.0f - thickness, 1.0f, 1.0f, 1.0f);
                this.tesselateBlockInWorld(tt, x, y, z);

                this.renderPistonArmNorthSouth(x + leftEdge, x + leftEdge, y + rightEdge, y + leftEdge, z - thickness + 1.0f - armLength, z - thickness + 1.0f, br * 0.6f, armLengthPixels);
                this.renderPistonArmNorthSouth(x + rightEdge, x + rightEdge, y + leftEdge, y + rightEdge, z - thickness + 1.0f - armLength, z - thickness + 1.0f, br * 0.6f, armLengthPixels);
                this.renderPistonArmNorthSouth(x + leftEdge, x + rightEdge, y + leftEdge, y + leftEdge, z - thickness + 1.0f - armLength, z - thickness + 1.0f, br * 0.5f, armLengthPixels);
                this.renderPistonArmNorthSouth(x + rightEdge, x + leftEdge, y + rightEdge, y + rightEdge, z - thickness + 1.0f - armLength, z - thickness + 1.0f, br, armLengthPixels);
                break;
            }
            case Facing.WEST: {
                this.northFlip = FLIP_CW;
                this.southFlip = FLIP_CCW;
                this.upFlip = FLIP_CCW;
                this.downFlip = FLIP_CW;
                tt.setShape(0.0f, 0.0f, 0.0f, thickness, 1.0f, 1.0f);
                this.tesselateBlockInWorld(tt, x, y, z);

                this.renderPistonArmEastWest(x + thickness, x + thickness + armLength, y + leftEdge, y + leftEdge, z + rightEdge, z + leftEdge, br * 0.5f, armLengthPixels);
                this.renderPistonArmEastWest(x + thickness, x + thickness + armLength, y + rightEdge, y + rightEdge, z + leftEdge, z + rightEdge, br, armLengthPixels);
                this.renderPistonArmEastWest(x + thickness, x + thickness + armLength, y + leftEdge, y + rightEdge, z + leftEdge, z + leftEdge, br * 0.6f, armLengthPixels);
                this.renderPistonArmEastWest(x + thickness, x + thickness + armLength, y + rightEdge, y + leftEdge, z + rightEdge, z + rightEdge, br * 0.6f, armLengthPixels);
                break;
            }
            case Facing.EAST: {
                this.northFlip = FLIP_CCW;
                this.southFlip = FLIP_CW;
                this.upFlip = FLIP_CW;
                this.downFlip = FLIP_CCW;
                tt.setShape(1.0f - thickness, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
                this.tesselateBlockInWorld(tt, x, y, z);

                this.renderPistonArmEastWest(x - thickness + 1.0f - armLength, x - thickness + 1.0f, y + leftEdge, y + leftEdge, z + rightEdge, z + leftEdge, br * 0.5f, armLengthPixels);
                this.renderPistonArmEastWest(x - thickness + 1.0f - armLength, x - thickness + 1.0f, y + rightEdge, y + rightEdge, z + leftEdge, z + rightEdge, br, armLengthPixels);
                this.renderPistonArmEastWest(x - thickness + 1.0f - armLength, x - thickness + 1.0f, y + leftEdge, y + rightEdge, z + leftEdge, z + leftEdge, br * 0.6f, armLengthPixels);
                this.renderPistonArmEastWest(x - thickness + 1.0f - armLength, x - thickness + 1.0f, y + rightEdge, y + leftEdge, z + rightEdge, z + rightEdge, br * 0.6f, armLengthPixels);
                break;
            }
        }
        this.northFlip = FLIP_NONE;
        this.southFlip = FLIP_NONE;
        this.eastFlip = FLIP_NONE;
        this.westFlip = FLIP_NONE;
        this.upFlip = FLIP_NONE;
        this.downFlip = FLIP_NONE;
        tt.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);

        return true;
    }
    
    public boolean tesselateLeverInWorld(final Tile tt, final int x, final int ry, final int z) {
        final int data = this.level.getData(x, ry, z);

        final int dir = data & 0x7;
        final boolean flipped = (data & 0x8) > 0;

        final Tesselator t = Tesselator.instance;

        final boolean hadFixed = this.fixedTexture >= 0;
        if (!hadFixed) this.fixedTexture = Tile.stoneBrick.tex;

        final float w1 = 4.0f / 16.0f;
        final float w2 = 3.0f / 16.0f;
        final float h = 3.0f / 16.0f;

        if (dir == 5) tt.setShape(0.5f - w2, 0.0f, 0.5f - w1, 0.5f + w2, h, 0.5f + w1);
        else if (dir == 6) tt.setShape(0.5f - w1, 0.0f, 0.5f - w2, 0.5f + w1, h, 0.5f + w2);
        else if (dir == 4) tt.setShape(0.5f - w2, 0.5f - w1, 1.0f - h, 0.5f + w2, 0.5f + w1, 1.0f);
        else if (dir == 3) tt.setShape(0.5f - w2, 0.5f - w1, 0.0f, 0.5f + w2, 0.5f + w1, h);
        else if (dir == 2) tt.setShape(1.0f - h, 0.5f - w1, 0.5f - w2, 1.0f, 0.5f + w1, 0.5f + w2);
        else if (dir == 1) tt.setShape(0.0f, 0.5f - w1, 0.5f - w2, h, 0.5f + w1, 0.5f + w2);
        this.tesselateBlockInWorld(tt, x, ry, z);

        if (!hadFixed) this.fixedTexture = -1;

        float br = tt.getBrightness(this.level, x, ry, z);
        if (Tile.lightEmission[tt.id] > 0) br = 1.0f;
        t.color(br, br, br);
        int tex = tt.getTexture(0);

        if (this.fixedTexture >= 0) tex = this.fixedTexture;
        final int texX = (tex & 0xF) << 4;
        final int texY = tex & 0xF0;

        float u0 = texX / 256.0f;
        float v0 = (texX + 15.99f) / 256.0f;
        float u1 = texY / 256.0f;
        float v1 = (texY + 15.99f) / 256.0f;

        final Vec3[] corners = new Vec3[8];
        final float xv = 1.0f / 16.0f;
        final float zv = 1.0f / 16.0f;
        final float yv = 10.0f / 16.0f;
        corners[0] = Vec3.newTemp(-xv, -0, -zv);
        corners[1] = Vec3.newTemp(+xv, -0, -zv);
        corners[2] = Vec3.newTemp(+xv, -0, +zv);
        corners[3] = Vec3.newTemp(-xv, -0, +zv);
        corners[4] = Vec3.newTemp(-xv, +yv, -zv);
        corners[5] = Vec3.newTemp(+xv, +yv, -zv);
        corners[6] = Vec3.newTemp(+xv, +yv, +zv);
        corners[7] = Vec3.newTemp(-xv, +yv, +zv);

        for (int i = 0; i < 8; ++i) {
            if (flipped) {
                corners[i].z -= 1 / 16.0f;
                corners[i].xRot(40 * Mth.DEGRAD);
            }
            else {
                corners[i].z += 1 / 16.0f;
                corners[i].xRot(-40 * Mth.DEGRAD);
            }

            if (dir == 6) {
                corners[i].yRot(90 * Mth.DEGRAD);
            }

            if (dir < 5) {
                corners[i].y -= 6 / 16.0f;
                corners[i].xRot(90 * Mth.DEGRAD);

                if (dir == 4) corners[i].yRot(  0 * Mth.DEGRAD);
                if (dir == 3) corners[i].yRot(180 * Mth.DEGRAD);
                if (dir == 2) corners[i].yRot( 90 * Mth.DEGRAD);
                if (dir == 1) corners[i].yRot(-90 * Mth.DEGRAD);

                corners[i].x += x + 0.5;
                corners[i].y += ry + 0.5f;
                corners[i].z += z + 0.5;
            }
            else {
                corners[i].x += x + 0.5;
                corners[i].y += ry + 2 / 16.0f;
                corners[i].z += z + 0.5;
            }
        }
        Vec3 c0 = null, c1 = null, c2 = null, c3 = null;
        for (int i = 0; i < 6; ++i) {
            if (i == 0) {
                u0 = (texX + 7) / 256.0f;
                v0 = (texX + 9 - 0.01f) / 256.0f;
                u1 = (texY + 6) / 256.0f;
                v1 = (texY + 8 - 0.01f) / 256.0f;
            }
            else if (i == 2) {
                u0 = (texX + 7) / 256.0f;
                v0 = (texX + 9 - 0.01f) / 256.0f;
                u1 = (texY + 6) / 256.0f;
                v1 = (texY + 16 - 0.01f) / 256.0f;
            }
            if (i == 0) {
                c0 = corners[0];
                c1 = corners[1];
                c2 = corners[2];
                c3 = corners[3];
            }
            else if (i == 1) {
                c0 = corners[7];
                c1 = corners[6];
                c2 = corners[5];
                c3 = corners[4];
            }
            else if (i == 2) {
                c0 = corners[1];
                c1 = corners[0];
                c2 = corners[4];
                c3 = corners[5];
            }
            else if (i == 3) {
                c0 = corners[2];
                c1 = corners[1];
                c2 = corners[5];
                c3 = corners[6];
            }
            else if (i == 4) {
                c0 = corners[3];
                c1 = corners[2];
                c2 = corners[6];
                c3 = corners[7];
            }
            else if (i == 5) {
                c0 = corners[0];
                c1 = corners[3];
                c2 = corners[7];
                c3 = corners[4];
            }
            t.vertexUV(c0.x, c0.y, c0.z, u0, v1);
            t.vertexUV(c1.x, c1.y, c1.z, v0, v1);
            t.vertexUV(c2.x, c2.y, c2.z, v0, u1);
            t.vertexUV(c3.x, c3.y, c3.z, u0, u1);
        }
        return true;
    }
    
    public boolean tesselateFireInWorld(final Tile tt, final int x, int y, final int z) {
        final Tesselator t = Tesselator.instance;
        int tex = tt.getTexture(0);
        if (this.fixedTexture >= 0) tex = this.fixedTexture;

        final float br = tt.getBrightness(this.level, x, y, z);
        t.color(br, br, br);

        final int texX = (tex & 0xF) << 4;
        final int texY = tex & 0xF0;
        double u0 = texX / 256.0f;
        double u1 = (texX + 15.99f) / 256.0f;
        double v0 = texY / 256.0f;
        double v1 = (texY + 15.99f) / 256.0f;
        float h = 1.4f;

        if (this.level.isSolidBlockingTile(x, y - 1, z) || Tile.fire.canBurn(this.level, x, y - 1, z)) {
            double x0 = x + 0.5 + 0.2;
            double x1 = x + 0.5 - 0.2;
            double z0 = z + 0.5 + 0.2;
            double z1 = z + 0.5 - 0.2;

            double x0_ = x + 0.5 - 0.3;
            double x1_ = x + 0.5 + 0.3;
            double z0_ = z + 0.5 - 0.3;
            double z1_ = z + 0.5 + 0.3;

            t.vertexUV(x0_, y + h, z + 1, u1, v0);
            t.vertexUV(x0, y + 0, z + 1, u1, v1);
            t.vertexUV(x0, y + 0, z + 0, u0, v1);
            t.vertexUV(x0_, y + h, z + 0, u0, v0);

            t.vertexUV(x1_, y + h, z + 0, u1, v0);
            t.vertexUV(x1, y + 0, z + 0, u1, v1);
            t.vertexUV(x1, y + 0, z + 1, u0, v1);
            t.vertexUV(x1_, y + h, z + 1, u0, v0);

            u0 = texX / 256.0f;
            u1 = (texX + 15.99f) / 256.0f;
            v0 = (texY + 16) / 256.0f;
            v1 = (texY + 15.99f + 16.0f) / 256.0f;

            t.vertexUV(x + 1, y + h, z1_, u1, v0);
            t.vertexUV(x + 1, y + 0, z1, u1, v1);
            t.vertexUV(x + 0, y + 0, z1, u0, v1);
            t.vertexUV(x + 0, y + h, z1_, u0, v0);

            t.vertexUV(x + 0, y + h, z0_, u1, v0);
            t.vertexUV(x + 0, y + 0, z0, u1, v1);
            t.vertexUV(x + 1, y + 0, z0, u0, v1);
            t.vertexUV(x + 1, y + h, z0_, u0, v0);

            x0 = x + 0.5 - 0.5;
            x1 = x + 0.5 + 0.5;
            z0 = z + 0.5 - 0.5;
            z1 = z + 0.5 + 0.5;

            x0_ = x + 0.5 - 0.4;
            x1_ = x + 0.5 + 0.4;
            z0_ = z + 0.5 - 0.4;
            z1_ = z + 0.5 + 0.4;

            t.vertexUV(x0_, y + h, z + 0, u0, v0);
            t.vertexUV(x0, y + 0, z + 0, u0, v1);
            t.vertexUV(x0, y + 0, z + 1, u1, v1);
            t.vertexUV(x0_, y + h, z + 1, u1, v0);

            t.vertexUV(x1_, y + h, z + 1, u0, v0);
            t.vertexUV(x1, y + 0, z + 1, u0, v1);
            t.vertexUV(x1, y + 0, z + 0, u1, v1);
            t.vertexUV(x1_, y + h, z + 0, u1, v0);

            u0 = texX / 256.0f;
            u1 = (texX + 15.99f) / 256.0f;
            v0 = texY / 256.0f;
            v1 = (texY + 15.99f) / 256.0f;

            t.vertexUV(x + 0, y + h, z1_, u0, v0);
            t.vertexUV(x + 0, y + 0, z1, u0, v1);
            t.vertexUV(x + 1, y + 0, z1, u1, v1);
            t.vertexUV(x + 1, y + h, z1_, u1, v0);

            t.vertexUV(x + 1, y + h, z0_, u0, v0);
            t.vertexUV(x + 1, y + 0, z0, u0, v1);
            t.vertexUV(x + 0, y + 0, z0, u1, v1);
            t.vertexUV(x + 0, y + h, z0_, u1, v0);
        }
        else {
            final float r = 0.2f;
            final float yo = 1 / 16.0f;
            if ((x + y + z & 0x1) == 0x1) {
                u0 = texX / 256.0f;
                u1 = (texX + 15.99f) / 256.0f;
                v0 = (texY + 16) / 256.0f;
                v1 = (texY + 15.99f + 16.0f) / 256.0f;
            }
            if ((x / 2 + y / 2 + z / 2 & 0x1) == 0x1) {
                final double tmp = u1;
                u1 = u0;
                u0 = tmp;
            }

            if (Tile.fire.canBurn(this.level, x - 1, y, z)) {
                t.vertexUV(x + r, y + h + yo, z + 1, u1, v0);
                t.vertexUV(x + 0, y + 0 + yo, z + 1, u1, v1);
                t.vertexUV(x + 0, y + 0 + yo, z + 0, u0, v1);
                t.vertexUV(x + r, y + h + yo, z + 0, u0, v0);
                t.vertexUV(x + r, y + h + yo, z + 0, u0, v0);
                t.vertexUV(x + 0, y + 0 + yo, z + 0, u0, v1);
                t.vertexUV(x + 0, y + 0 + yo, z + 1, u1, v1);
                t.vertexUV(x + r, y + h + yo, z + 1, u1, v0);
            }
            if (Tile.fire.canBurn(this.level, x + 1, y, z)) {
                t.vertexUV(x + 1 - r, y + h + yo, z + 0, u0, v0);
                t.vertexUV(x + 1 - 0, y + 0 + yo, z + 0, u0, v1);
                t.vertexUV(x + 1 - 0, y + 0 + yo, z + 1, u1, v1);
                t.vertexUV(x + 1 - r, y + h + yo, z + 1, u1, v0);
                t.vertexUV(x + 1 - r, y + h + yo, z + 1, u1, v0);
                t.vertexUV(x + 1 - 0, y + 0 + yo, z + 1, u1, v1);
                t.vertexUV(x + 1 - 0, y + 0 + yo, z + 0, u0, v1);
                t.vertexUV(x + 1 - r, y + h + yo, z + 0, u0, v0);
            }
            if (Tile.fire.canBurn(this.level, x, y, z - 1)) {
                t.vertexUV(x + 0, y + h + yo, z + r, u1, v0);
                t.vertexUV(x + 0, y + 0 + yo, z + 0, u1, v1);
                t.vertexUV(x + 1, y + 0 + yo, z + 0, u0, v1);
                t.vertexUV(x + 1, y + h + yo, z + r, u0, v0);
                t.vertexUV(x + 1, y + h + yo, z + r, u0, v0);
                t.vertexUV(x + 1, y + 0 + yo, z + 0, u0, v1);
                t.vertexUV(x + 0, y + 0 + yo, z + 0, u1, v1);
                t.vertexUV(x + 0, y + h + yo, z + r, u1, v0);
            }
            if (Tile.fire.canBurn(this.level, x, y, z + 1)) {
                t.vertexUV(x + 1, y + h + yo, z + 1 - r, u0, v0);
                t.vertexUV(x + 1, y + 0 + yo, z + 1 - 0, u0, v1);
                t.vertexUV(x + 0, y + 0 + yo, z + 1 - 0, u1, v1);
                t.vertexUV(x + 0, y + h + yo, z + 1 - r, u1, v0);
                t.vertexUV(x + 0, y + h + yo, z + 1 - r, u1, v0);
                t.vertexUV(x + 0, y + 0 + yo, z + 1 - 0, u1, v1);
                t.vertexUV(x + 1, y + 0 + yo, z + 1 - 0, u0, v1);
                t.vertexUV(x + 1, y + h + yo, z + 1 - r, u0, v0);
            }
            if (Tile.fire.canBurn(this.level, x, y + 1, z)) {
                double x0 = x + 0.5 + 0.5;
                double x1 = x + 0.5 - 0.5;
                double z0 = z + 0.5 + 0.5;
                double z1 = z + 0.5 - 0.5;

                double x0_ = x + 0.5 - 0.5;
                double x1_ = x + 0.5 + 0.5;
                double z0_ = z + 0.5 - 0.5;
                double z1_ = z + 0.5 + 0.5;

                u0 = texX / 256.0f;
                u1 = (texX + 15.99f) / 256.0f;
                v0 = texY / 256.0f;
                v1 = (texY + 15.99f) / 256.0f;

                ++y;
                h = -0.2f;

                if ((x + y + z & 0x1) == 0x0) {
                    t.vertexUV(x0_, y + h, z + 0, u1, v0);
                    t.vertexUV(x0, y + 0, z + 0, u1, v1);
                    t.vertexUV(x0, y + 0, z + 1, u0, v1);
                    t.vertexUV(x0_, y + h, z + 1, u0, v0);

                    u0 = texX / 256.0f;
                    u1 = (texX + 15.99f) / 256.0f;
                    v0 = (texY + 16) / 256.0f;
                    v1 = (texY + 15.99f + 16.0f) / 256.0f;

                    t.vertexUV(x1_, y + h, z + 1, u1, v0);
                    t.vertexUV(x1, y + 0, z + 1, u1, v1);
                    t.vertexUV(x1, y + 0, z + 0, u0, v1);
                    t.vertexUV(x1_, y + h, z + 0, u0, v0);
                }
                else {
                    t.vertexUV(x + 0, y + h, z1_, u1, v0);
                    t.vertexUV(x + 0, y + 0, z1, u1, v1);
                    t.vertexUV(x + 1, y + 0, z1, u0, v1);
                    t.vertexUV(x + 1, y + h, z1_, u0, v0);

                    u0 = texX / 256.0f;
                    u1 = (texX + 15.99f) / 256.0f;
                    v0 = (texY + 16) / 256.0f;
                    v1 = (texY + 15.99f + 16.0f) / 256.0f;

                    t.vertexUV(x + 1, y + h, z0_, u1, v0);
                    t.vertexUV(x + 1, y + 0, z0, u1, v1);
                    t.vertexUV(x + 0, y + 0, z0, u0, v1);
                    t.vertexUV(x + 0, y + h, z0_, u0, v0);
                }
            }
        }
        return true;
    }
    
    public boolean tesselateDustInWorld(final Tile tt, final int x, final int y, final int z) {
        final Tesselator t = Tesselator.instance;

        final int data = this.level.getData(x, y, z);
        int tex = tt.getTexture(1, data);
        if (this.fixedTexture >= 0) tex = this.fixedTexture;

        final float br = tt.getBrightness(this.level, x, y, z);

        final float pow = data / 15.0f;
        float red = pow * 0.6f + 0.4f;
        if (data == 0) red = 0.3f;

        float green = pow * pow * 0.7f - 0.5f;
        float blue = pow * pow * 0.6f - 0.7f;
        if (green < 0.0f) green = 0.0f;
        if (blue < 0.0f) blue = 0.0f;
        t.color(br * red, br * green, br * blue);

        final int texX = (tex & 0xF) << 4;
        final int texY = tex & 0xF0;
        double u0 = texX / 256.0f;
        double u1 = (texX + 15.99f) / 256.0f;
        double v0 = texY / 256.0f;
        double v1 = (texY + 15.99f) / 256.0f;

        float dustOffset = 0.25f / 16.0F;
        float overlayOffset = 0.25f / 16.0F;

        boolean w = RedStoneDustTile.shouldReceivePowerFrom(this.level, x - 1, y, z, Direction.WEST) || (!this.level.isSolidBlockingTile(x - 1, y, z) && RedStoneDustTile.shouldReceivePowerFrom(this.level, x - 1, y - 1, z, Direction.UNDEFINED));
        boolean e = RedStoneDustTile.shouldReceivePowerFrom(this.level, x + 1, y, z, Direction.EAST) || (!this.level.isSolidBlockingTile(x + 1, y, z) && RedStoneDustTile.shouldReceivePowerFrom(this.level, x + 1, y - 1, z, Direction.UNDEFINED));
        boolean n = RedStoneDustTile.shouldReceivePowerFrom(this.level, x, y, z - 1, Direction.NORTH) || (!this.level.isSolidBlockingTile(x, y, z - 1) && RedStoneDustTile.shouldReceivePowerFrom(this.level, x, y - 1, z - 1, Direction.UNDEFINED));
        boolean s = RedStoneDustTile.shouldReceivePowerFrom(this.level, x, y, z + 1, Direction.SOUTH) || (!this.level.isSolidBlockingTile(x, y, z + 1) && RedStoneDustTile.shouldReceivePowerFrom(this.level, x, y - 1, z + 1, Direction.UNDEFINED));
        if (!this.level.isSolidBlockingTile(x, y + 1, z)) {
            if (this.level.isSolidBlockingTile(x - 1, y, z) && RedStoneDustTile.shouldReceivePowerFrom(this.level, x - 1, y + 1, z, Direction.UNDEFINED)) w = true;
            if (this.level.isSolidBlockingTile(x + 1, y, z) && RedStoneDustTile.shouldReceivePowerFrom(this.level, x + 1, y + 1, z, Direction.UNDEFINED)) e = true;
            if (this.level.isSolidBlockingTile(x, y, z - 1) && RedStoneDustTile.shouldReceivePowerFrom(this.level, x, y + 1, z - 1, Direction.UNDEFINED)) n = true;
            if (this.level.isSolidBlockingTile(x, y, z + 1) && RedStoneDustTile.shouldReceivePowerFrom(this.level, x, y + 1, z + 1, Direction.UNDEFINED)) s = true;
        }
        float d = 5.0f / 16.0f;
        float x0 = (float)(x + 0);
        float x1 = (float)(x + 1);
        float z0 = (float)(z + 0);
        float z1 = (float)(z + 1);

        int pic = 0;
        if ((w || e) && !n && !s) pic = 1;
        if ((n || s) && !e && !w) pic = 2;

        if (pic != 0) {
            u0 = (texX + 16) / 256.0f;
            u1 = (texX + 16 + 15.99f) / 256.0f;
            v0 = texY / 256.0f;
            v1 = (texY + 15.99f) / 256.0f;
        }

        if (pic == 0) {
            if (e || n || s || w) {
                if (!w) x0 += d;
                if (!w) u0 += d / 16.0F;
                if (!e) x1 -= d;
                if (!e) u1 -= d / 16.0F;
                if (!n) z0 += d;
                if (!n) v0 += d / 16.0F;
                if (!s) z1 -= d;
                if (!s) v1 -= d / 16.0F;
            }
            t.vertexUV(x1, y + dustOffset, z1, u1, v1);
            t.vertexUV(x1, y + dustOffset, z0, u1, v0);
            t.vertexUV(x0, y + dustOffset, z0, u0, v0);
            t.vertexUV(x0, y + dustOffset, z1, u0, v1);

            t.color(br, br, br);
            t.vertexUV(x1, y + dustOffset, z1, u1, v1 + 1 / 16.0f);
            t.vertexUV(x1, y + dustOffset, z0, u1, v0 + 1 / 16.0f);
            t.vertexUV(x0, y + dustOffset, z0, u0, v0 + 1 / 16.0f);
            t.vertexUV(x0, y + dustOffset, z1, u0, v1 + 1 / 16.0f);
        }
        else if (pic == 1) {
            t.vertexUV(x1, y + dustOffset, z1, u1, v1);
            t.vertexUV(x1, y + dustOffset, z0, u1, v0);
            t.vertexUV(x0, y + dustOffset, z0, u0, v0);
            t.vertexUV(x0, y + dustOffset, z1, u0, v1);
            t.color(br, br, br);

            t.vertexUV(x1, y + overlayOffset, z1, u1, v1 + 1 / 16.0f);
            t.vertexUV(x1, y + overlayOffset, z0, u1, v0 + 1 / 16.0f);
            t.vertexUV(x0, y + overlayOffset, z0, u0, v0 + 1 / 16.0f);
            t.vertexUV(x0, y + overlayOffset, z1, u0, v1 + 1 / 16.0f);
        }
        else if (pic == 2) {
            t.vertexUV(x1, y + dustOffset, z1, u1, v1);
            t.vertexUV(x1, y + dustOffset, z0, u0, v1);
            t.vertexUV(x0, y + dustOffset, z0, u0, v0);
            t.vertexUV(x0, y + dustOffset, z1, u1, v0);
            t.color(br, br, br);

            t.vertexUV(x1, y + overlayOffset, z1, u1, v1 + 1 / 16.0f);
            t.vertexUV(x1, y + overlayOffset, z0, u0, v1 + 1 / 16.0f);
            t.vertexUV(x0, y + overlayOffset, z0, u0, v0 + 1 / 16.0f);
            t.vertexUV(x0, y + overlayOffset, z1, u1, v0 + 1 / 16.0f);
        }

        if (!this.level.isSolidBlockingTile(x, y + 1, z)) {
            u0 = (texX + 16) / 256.0f;
            u1 = (texX + 16 + 15.99f) / 256.0f;
            v0 = texY / 256.0f;
            v1 = (texY + 15.99f) / 256.0f;

            final float yStretch = 0.35f / 16.0f;
            if (this.level.isSolidBlockingTile(x - 1, y, z) && this.level.getTile(x - 1, y + 1, z) == Tile.redStoneDust.id) {
                t.color(br * red, br * green, br * blue);
                t.vertexUV(x + overlayOffset, y + 1 + yStretch, z + 1, u1, v0);
                t.vertexUV(x + overlayOffset, y + 0, z + 1, u0, v0);
                t.vertexUV(x + overlayOffset, y + 0, z + 0, u0, v1);
                t.vertexUV(x + overlayOffset, y + 1 + yStretch, z + 0, u1, v1);
                t.color(br, br, br);
                t.vertexUV(x + overlayOffset, y + 1 + yStretch, z + 1, u1, v0 + 1 / 16.0f);
                t.vertexUV(x + overlayOffset, y + 0, z + 1, u0, v0 + 1 / 16.0f);
                t.vertexUV(x + overlayOffset, y + 0, z + 0, u0, v1 + 1 / 16.0f);
                t.vertexUV(x + overlayOffset, y + 1 + yStretch, z + 0, u1, v1 + 1 / 16.0f);
            }
            if (this.level.isSolidBlockingTile(x + 1, y, z) && this.level.getTile(x + 1, y + 1, z) == Tile.redStoneDust.id) {
                t.color(br * red, br * green, br * blue);
                t.vertexUV(x + 1 - overlayOffset, y + 0, z + 1, u0, v1);
                t.vertexUV(x + 1 - overlayOffset, y + 1 + yStretch, z + 1, u1, v1);
                t.vertexUV(x + 1 - overlayOffset, y + 1 + yStretch, z + 0, u1, v0);
                t.vertexUV(x + 1 - overlayOffset, y + 0, z + 0, u0, v0);

                t.color(br, br, br);
                t.vertexUV(x + 1 - overlayOffset, y + 0, z + 1, u0, v1 + 1 / 16.0f);
                t.vertexUV(x + 1 - overlayOffset, y + 1 + yStretch, z + 1, u1, v1 + 1 / 16.0f);
                t.vertexUV(x + 1 - overlayOffset, y + 1 + yStretch, z + 0, u1, v0 + 1 / 16.0f);
                t.vertexUV(x + 1 - overlayOffset, y + 0, z + 0, u0, v0 + 1 / 16.0f);
            }
            if (this.level.isSolidBlockingTile(x, y, z - 1) && this.level.getTile(x, y + 1, z - 1) == Tile.redStoneDust.id) {
                t.color(br * red, br * green, br * blue);
                t.vertexUV(x + 1, y + 0, z + overlayOffset, u0, v1);
                t.vertexUV(x + 1, y + 1 + yStretch, z + overlayOffset, u1, v1);
                t.vertexUV(x + 0, y + 1 + yStretch, z + overlayOffset, u1, v0);
                t.vertexUV(x + 0, y + 0, z + overlayOffset, u0, v0);

                t.color(br, br, br);
                t.vertexUV(x + 1, y + 0, z + overlayOffset, u0, v1 + 1 / 16.0f);
                t.vertexUV(x + 1, y + 1 + yStretch, z + overlayOffset, u1, v1 + 1 / 16.0f);
                t.vertexUV(x + 0, y + 1 + yStretch, z + overlayOffset, u1, v0 + 1 / 16.0f);
                t.vertexUV(x + 0, y + 0, z + overlayOffset, u0, v0 + 1 / 16.0f);
            }
            if (this.level.isSolidBlockingTile(x, y, z + 1) && this.level.getTile(x, y + 1, z + 1) == Tile.redStoneDust.id) {
                t.color(br * red, br * green, br * blue);
                t.vertexUV(x + 1, y + 1 + yStretch, z + 1 - overlayOffset, u1, v0);
                t.vertexUV(x + 1, y + 0, z + 1 - overlayOffset, u0, v0);
                t.vertexUV(x + 0, y + 0, z + 1 - overlayOffset, u0, v1);
                t.vertexUV(x + 0, y + 1 + yStretch, z + 1 - overlayOffset, u1, v1);

                t.color(br, br, br);
                t.vertexUV(x + 1, y + 1 + yStretch, z + 1 - overlayOffset, u1, v0 + 1 / 16.0f);
                t.vertexUV(x + 1, y + 0, z + 1 - overlayOffset, u0, v0 + 1 / 16.0f);
                t.vertexUV(x + 0, y + 0, z + 1 - overlayOffset, u0, v1 + 1 / 16.0f);
                t.vertexUV(x + 0, y + 1 + yStretch, z + 1 - overlayOffset, u1, v1 + 1 / 16.0f);
            }
        }

        return true;
    }
    
    public boolean tesselateRailInWorld(final RailTile tt, final int x, final int y, final int z) {
        final Tesselator t = Tesselator.instance;
        int data = this.level.getData(x, y, z);

        int tex = tt.getTexture(0, data);
        if (this.fixedTexture >= 0) tex = this.fixedTexture;

        if (tt.isUsesDataBit()) data &= RailTile.RAIL_DIRECTION_MASK;

        final float br = tt.getBrightness(this.level, x, y, z);
        t.color(br, br, br);

        final int texX = (tex & 0xF) << 4;
        final int texY = tex & 0xF0;
        final double u0 = texX / 256.0f;
        final double u1 = (texX + 15.99f) / 256.0f;
        final double v0 = texY / 256.0f;
        final double v1 = (texY + 15.99f) / 256.0f;

        final float r = 1 / 16.0f;

        float x0 = (float)(x + 1);
        float x1 = (float)(x + 1);
        float x2 = (float)(x + 0);
        float x3 = (float)(x + 0);

        float z0 = (float)(z + 0);
        float z1 = (float)(z + 1);
        float z2 = (float)(z + 1);
        float z3 = (float)(z + 0);

        float y0 = y + r;
        float y1 = y + r;
        float y2 = y + r;
        float y3 = y + r;

        if (data == 1 || data == 2 || data == 3 || data == 7) {
            x3 = (x0 = (float)(x + 1));
            x2 = (x1 = (float)(x + 0));
            z1 = (z0 = (float)(z + 1));
            z3 = (z2 = (float)(z + 0));
        }
        else if (data == 8) {
            x1 = (x0 = (float)(x + 0));
            x3 = (x2 = (float)(x + 1));
            z3 = (z0 = (float)(z + 1));
            z2 = (z1 = (float)(z + 0));
        }
        else if (data == 9) {
            x3 = (x0 = (float)(x + 0));
            x2 = (x1 = (float)(x + 1));
            z1 = (z0 = (float)(z + 0));
            z3 = (z2 = (float)(z + 1));
        }

        if (data == 2 || data == 4) {
            ++y0;
            ++y3;
        }
        else if (data == 3 || data == 5) {
            ++y1;
            ++y2;
        }

        t.vertexUV(x0, y0, z0, u1, v0);
        t.vertexUV(x1, y1, z1, u1, v1);
        t.vertexUV(x2, y2, z2, u0, v1);
        t.vertexUV(x3, y3, z3, u0, v0);

        t.vertexUV(x3, y3, z3, u0, v0);
        t.vertexUV(x2, y2, z2, u0, v1);
        t.vertexUV(x1, y1, z1, u1, v1);
        t.vertexUV(x0, y0, z0, u1, v0);

        return true;
    }
    
    public boolean tesselateLadderInWorld(final Tile tt, final int x, final int y, final int z) {
        final Tesselator t = Tesselator.instance;

        int tex = tt.getTexture(0);
        if (this.fixedTexture >= 0) tex = this.fixedTexture;

        final float br = tt.getBrightness(this.level, x, y, z);
        t.color(br, br, br);

        final int texX = (tex & 0xF) << 4;
        final int texY = tex & 0xF0;
        final double u0 = texX / 256.0f;
        final double u1 = (texX + 15.99f) / 256.0f;
        final double v0 = texY / 256.0f;
        final double v1 = (texY + 15.99f) / 256.0f;

        final int face = this.level.getData(x, y, z);

        final float o = 0 / 16.0f;
        final float r = 0.05f;
        if (face == 5) {
            t.vertexUV(x + r, y + 1 + o, z + 1 + o, u0, v0);
            t.vertexUV(x + r, y + 0 - o, z + 1 + o, u0, v1);
            t.vertexUV(x + r, y + 0 - o, z + 0 - o, u1, v1);
            t.vertexUV(x + r, y + 1 + o, z + 0 - o, u1, v0);
        }
        if (face == 4) {
            t.vertexUV(x + 1 - r, y + 0 - o, z + 1 + o, u1, v1);
            t.vertexUV(x + 1 - r, y + 1 + o, z + 1 + o, u1, v0);
            t.vertexUV(x + 1 - r, y + 1 + o, z + 0 - o, u0, v0);
            t.vertexUV(x + 1 - r, y + 0 - o, z + 0 - o, u0, v1);
        }
        if (face == 3) {
            t.vertexUV(x + 1 + o, y + 0 - o, z + r, u1, v1);
            t.vertexUV(x + 1 + o, y + 1 + o, z + r, u1, v0);
            t.vertexUV(x + 0 - o, y + 1 + o, z + r, u0, v0);
            t.vertexUV(x + 0 - o, y + 0 - o, z + r, u0, v1);
        }
        if (face == 2) {
            t.vertexUV(x + 1 + o, y + 1 + o, z + 1 - r, u0, v0);
            t.vertexUV(x + 1 + o, y + 0 - o, z + 1 - r, u0, v1);
            t.vertexUV(x + 0 - o, y + 0 - o, z + 1 - r, u1, v1);
            t.vertexUV(x + 0 - o, y + 1 + o, z + 1 - r, u1, v0);
        }
        return true;
    }
    
    public boolean tesselateCrossInWorld(final Tile tt, final int x, final int y, final int z) {
        final Tesselator t = Tesselator.instance;

        final float br = tt.getBrightness(this.level, x, y, z);
        final int col = tt.getColor(this.level, x, y, z);
        float r = (col >> 16 & 0xFF) / 255.0f;
        float g = (col >> 8 & 0xFF) / 255.0f;
        float b = (col & 0xFF) / 255.0f;

        if (GameRenderer.anaglyph3d) {
            final float rr = (r * 30.0f + g * 59.0f + b * 11.0f) / 100.0f;
            final float gg = (r * 30.0f + g * 70.0f) / 100.0f;
            final float bb = (r * 30.0f + b * 70.0f) / 100.0f;
            r = rr;
            g = gg;
            b = bb;
        }
        t.color(br * r, br * g, br * b);

        double xt = x;
        double yt = y;
        double zt = z;

        if (tt == Tile.tallgrass) {
            long seed = (x * 3129871L) ^ z * 116129781L ^ (long)y;
            seed = seed * seed * 42317861L + seed * 11L;

            xt += ((seed >> 16 & 0xFL) / 15.0f - 0.5) * 0.5;
            yt += ((seed >> 20 & 0xFL) / 15.0f - 1.0) * 0.2;
            zt += ((seed >> 24 & 0xFL) / 15.0f - 0.5) * 0.5;
        }

        this.tesselateCrossTexture(tt, this.level.getData(x, y, z), xt, yt, zt);
        return true;
    }
    
    public boolean tesselateRowInWorld(final Tile tt, final int x, final int y, final int z) {
        final Tesselator t = Tesselator.instance;

        final float br = tt.getBrightness(this.level, x, y, z);
        t.color(br, br, br);

        this.tesselateRowTexture(tt, this.level.getData(x, y, z), x, y - 1.0 / 16.0f, z);
        return true;
    }
    
    public void tesselateTorch(final Tile tt, double x, final double y, double z, final double xxa, final double zza) {
        final Tesselator instance = Tesselator.instance;
        int tex = tt.getTexture(Facing.DOWN);

        if (this.fixedTexture >= 0) tex = this.fixedTexture;

        final int texX = (tex & 0xF) << 4;
        final int texY = tex & 0xF0;

        final float u0 = texX / 256.0f;
        final float u1 = (texX + 15.99f) / 256.0f;
        final float v0 = texY / 256.0f;
        final float v1 = (texY + 15.99f) / 256.0f;

        final double ut0 = u0 + 7 / 256.0f;
        final double vt0 = v0 + 6 / 256.0f;
        final double ut1 = u0 + 9 / 256.0f;
        final double vt1 = v0 + 8 / 256.0f;

        x += 0.5;
        z += 0.5;

        final double x0 = x - 0.5;
        final double x1 = x + 0.5;
        final double z0 = z - 0.5;
        final double z1 = z + 0.5;

        final double r = 1.0 / 16.0f;
        final double h = 10.0 / 16.0f;

        instance.vertexUV(x + xxa * (1.0 - h) - r, y + h, z + zza * (1.0 - h) - r, ut0, vt0);
        instance.vertexUV(x + xxa * (1.0 - h) - r, y + h, z + zza * (1.0 - h) + r, ut0, vt1);
        instance.vertexUV(x + xxa * (1.0 - h) + r, y + h, z + zza * (1.0 - h) + r, ut1, vt1);
        instance.vertexUV(x + xxa * (1.0 - h) + r, y + h, z + zza * (1.0 - h) - r, ut1, vt0);

        instance.vertexUV(x - r, y + 1.0, z0, u0, v0);
        instance.vertexUV(x - r + xxa, y + 0.0, z0 + zza, u0, v1);
        instance.vertexUV(x - r + xxa, y + 0.0, z1 + zza, u1, v1);
        instance.vertexUV(x - r, y + 1.0, z1, u1, v0);

        instance.vertexUV(x + r, y + 1.0, z1, u0, v0);
        instance.vertexUV(x + xxa + r, y + 0.0, z1 + zza, u0, v1);
        instance.vertexUV(x + xxa + r, y + 0.0, z0 + zza, u1, v1);
        instance.vertexUV(x + r, y + 1.0, z0, u1, v0);

        instance.vertexUV(x0, y + 1.0, z + r, u0, v0);
        instance.vertexUV(x0 + xxa, y + 0.0, z + r + zza, u0, v1);
        instance.vertexUV(x1 + xxa, y + 0.0, z + r + zza, u1, v1);
        instance.vertexUV(x1, y + 1.0, z + r, u1, v0);

        instance.vertexUV(x1, y + 1.0, z - r, u0, v0);
        instance.vertexUV(x1 + xxa, y + 0.0, z - r + zza, u0, v1);
        instance.vertexUV(x0 + xxa, y + 0.0, z - r + zza, u1, v1);
        instance.vertexUV(x0, y + 1.0, z - r, u1, v0);
    }
    
    public void tesselateCrossTexture(final Tile tt, final int data, final double x, final double y, final double z) {
        final Tesselator t = Tesselator.instance;

        int tex = tt.getTexture(0, data);
        if (this.fixedTexture >= 0) tex = this.fixedTexture;

        final int texX = (tex & 0xF) << 4;
        final int texY = tex & 0xF0;
        final double u0 = texX / 256.0f;
        final double u1 = (texX + 15.99f) / 256.0f;
        final double v0 = texY / 256.0f;
        final double v1 = (texY + 15.99f) / 256.0f;

        final float width = 0.45f;
        final double x0 = x + 0.5 - width;
        final double x1 = x + 0.5 + width;
        final double z0 = z + 0.5 - width;
        final double z1 = z + 0.5 + width;

        t.vertexUV(x0, y + 1.0, z0, u0, v0);
        t.vertexUV(x0, y + 0.0, z0, u0, v1);
        t.vertexUV(x1, y + 0.0, z1, u1, v1);
        t.vertexUV(x1, y + 1.0, z1, u1, v0);

        t.vertexUV(x1, y + 1.0, z1, u0, v0);
        t.vertexUV(x1, y + 0.0, z1, u0, v1);
        t.vertexUV(x0, y + 0.0, z0, u1, v1);
        t.vertexUV(x0, y + 1.0, z0, u1, v0);

        t.vertexUV(x0, y + 1.0, z1, u0, v0);
        t.vertexUV(x0, y + 0.0, z1, u0, v1);
        t.vertexUV(x1, y + 0.0, z0, u1, v1);
        t.vertexUV(x1, y + 1.0, z0, u1, v0);

        t.vertexUV(x1, y + 1.0, z0, u0, v0);
        t.vertexUV(x1, y + 0.0, z0, u0, v1);
        t.vertexUV(x0, y + 0.0, z1, u1, v1);
        t.vertexUV(x0, y + 1.0, z1, u1, v0);
    }
    
    public void tesselateRowTexture(final Tile tt, final int data, final double x, final double y, final double z) {
        final Tesselator instance = Tesselator.instance;
        int tex = tt.getTexture(0, data);

        if (this.fixedTexture >= 0) tex = this.fixedTexture;

        final int texX = (tex & 0xF) << 4;
        final int texY = tex & 0xF0;
        final double u0 = texX / 256.0f;
        final double u1 = (texX + 15.99f) / 256.0f;
        final double v0 = texY / 256.0f;
        final double v1 = (texY + 15.99f) / 256.0f;

        double x0 = x + 0.5 - 0.25;
        double x1 = x + 0.5 + 0.25;
        double z0 = z + 0.5 - 0.5;
        double z1 = z + 0.5 + 0.5;

        instance.vertexUV(x0, y + 1.0, z0, u0, v0);
        instance.vertexUV(x0, y + 0.0, z0, u0, v1);
        instance.vertexUV(x0, y + 0.0, z1, u1, v1);
        instance.vertexUV(x0, y + 1.0, z1, u1, v0);

        instance.vertexUV(x0, y + 1.0, z1, u0, v0);
        instance.vertexUV(x0, y + 0.0, z1, u0, v1);
        instance.vertexUV(x0, y + 0.0, z0, u1, v1);
        instance.vertexUV(x0, y + 1.0, z0, u1, v0);

        instance.vertexUV(x1, y + 1.0, z1, u0, v0);
        instance.vertexUV(x1, y + 0.0, z1, u0, v1);
        instance.vertexUV(x1, y + 0.0, z0, u1, v1);
        instance.vertexUV(x1, y + 1.0, z0, u1, v0);

        instance.vertexUV(x1, y + 1.0, z0, u0, v0);
        instance.vertexUV(x1, y + 0.0, z0, u0, v1);
        instance.vertexUV(x1, y + 0.0, z1, u1, v1);
        instance.vertexUV(x1, y + 1.0, z1, u1, v0);

        x0 = x + 0.5 - 0.5;
        x1 = x + 0.5 + 0.5;
        z0 = z + 0.5 - 0.25;
        z1 = z + 0.5 + 0.25;

        instance.vertexUV(x0, y + 1.0, z0, u0, v0);
        instance.vertexUV(x0, y + 0.0, z0, u0, v1);
        instance.vertexUV(x1, y + 0.0, z0, u1, v1);
        instance.vertexUV(x1, y + 1.0, z0, u1, v0);

        instance.vertexUV(x1, y + 1.0, z0, u0, v0);
        instance.vertexUV(x1, y + 0.0, z0, u0, v1);
        instance.vertexUV(x0, y + 0.0, z0, u1, v1);
        instance.vertexUV(x0, y + 1.0, z0, u1, v0);

        instance.vertexUV(x1, y + 1.0, z1, u0, v0);
        instance.vertexUV(x1, y + 0.0, z1, u0, v1);
        instance.vertexUV(x0, y + 0.0, z1, u1, v1);
        instance.vertexUV(x0, y + 1.0, z1, u1, v0);

        instance.vertexUV(x0, y + 1.0, z1, u0, v0);
        instance.vertexUV(x0, y + 0.0, z1, u0, v1);
        instance.vertexUV(x1, y + 0.0, z1, u1, v1);
        instance.vertexUV(x1, y + 1.0, z1, u1, v0);
    }
    
    public boolean tesselateWaterInWorld(final Tile tt, final int x, final int y, final int z) {
        // Useless - Source java codebase comment, according to the LCE leak
        // TODO: This all needs to change. Somehow.
        final Tesselator t = Tesselator.instance;

        final int col = tt.getColor(this.level, x, y, z);
        final float r = (col >> 16 & 0xFF) / 255.0f;
        final float g = (col >> 8 & 0xFF) / 255.0f;
        final float b = (col & 0xFF) / 255.0f;
        final boolean up = tt.shouldRenderFace(this.level, x, y + 1, z, 1);
        final boolean down = tt.shouldRenderFace(this.level, x, y - 1, z, 0);
        final boolean[] dirs = {
                tt.shouldRenderFace(this.level, x, y, z - 1, 2),
                tt.shouldRenderFace(this.level, x, y, z + 1, 3),
                tt.shouldRenderFace(this.level, x - 1, y, z, 4),
                tt.shouldRenderFace(this.level, x + 1, y, z, 5)
        };

        if (!up && !down && !dirs[0] && !dirs[1] && !dirs[2] && !dirs[3]) return false;

        boolean changed = false;
        final float c10 = 0.5f;
        final float c11 = 1.0f;
        final float c2 = 0.8f;
        final float c3 = 0.6f;

        final double yo0 = 0.0;
        final double yo1 = 1.0;

        final Material m = tt.material;
        final int data = this.level.getData(x, y, z);

        final float h0 = this.getWaterHeight(x, y, z, m);
        final float h1 = this.getWaterHeight(x, y, z + 1, m);
        final float h2 = this.getWaterHeight(x + 1, y, z + 1, m);
        final float h3 = this.getWaterHeight(x + 1, y, z, m);

        if (this.noCulling || up) {
            changed = true;
            int tex = tt.getTexture(1, data);
            float angle = (float)LiquidTile.getSlopeAngle(this.level, x, y, z, m);
            if (angle > -999.0f) tex = tt.getTexture(2, data);

            final int texX = (tex & 0xF) << 4;
            final int texY = tex & 0xF0;
            double uc = (texX + 8.0) / 256.0;
            double vc = (texY + 8.0) / 256.0;
            if (angle < -999.0f) {
                angle = 0.0f;
            }
            else {
                uc = (texX + 16) / 256.0f;
                vc = (texY + 16) / 256.0f;
            }

            final float s = Mth.sin(angle) * 8.0f / 256.0f;
            final float c = Mth.cos(angle) * 8.0f / 256.0f;
            final float br = tt.getBrightness(this.level, x, y, z);
            t.color(c11 * br * r, c11 * br * g, c11 * br * b);
            t.vertexUV(x + 0, y + h0, z + 0, uc - c - s, vc - c + s);
            t.vertexUV(x + 0, y + h1, z + 1, uc - c + s, vc + c + s);
            t.vertexUV(x + 1, y + h2, z + 1, uc + c + s, vc + c - s);
            t.vertexUV(x + 1, y + h3, z + 0, uc + c - s, vc - c - s);
        }
        if (this.noCulling || down) {
            final float br = tt.getBrightness(this.level, x, y - 1, z);
            t.color(c10 * br, c10 * br, c10 * br);
            this.renderFaceDown(tt, x, y, z, tt.getTexture(0));
            changed = true;
        }

        for (int face = 0; face < 4; ++face) {
            int xt = x;
            int zt = z;

            if (face == 0) --zt;
            if (face == 1) ++zt;
            if (face == 2) --xt;
            if (face == 3) ++xt;

            final int texture = tt.getTexture(face + 2, data);
            final int texX = (texture & 0xF) << 4;
            final int texY = texture & 0xF0;
            if (this.noCulling || dirs[face]) {
                float hh0;
                float hh1;
                float x0, x1, z0, z1;
                if (face == 0) {
                    hh0 = h0;
                    hh1 = h3;
                    x0 = (float)x;
                    x1 = (float)(x + 1);
                    z0 = (float)z;
                    z1 = (float)z;
                }
                else if (face == 1) {
                    hh0 = h2;
                    hh1 = h1;
                    x0 = (float)(x + 1);
                    x1 = (float)x;
                    z0 = (float)(z + 1);
                    z1 = (float)(z + 1);
                }
                else if (face == 2) {
                    hh0 = h1;
                    hh1 = h0;
                    x0 = (float)x;
                    x1 = (float)x;
                    z0 = (float)(z + 1);
                    z1 = (float)z;
                }
                else {
                    hh0 = h3;
                    hh1 = h2;
                    x0 = (float)(x + 1);
                    x1 = (float)(x + 1);
                    z0 = (float)z;
                    z1 = (float)(z + 1);
                }

                changed = true;
                final double u0 = (texX + 0) / 256.0f;
                final double u1 = (texX + 16 - 0.01) / 256.0;
                final double v01 = (texY + (1.0f - hh0) * 16.0f) / 256.0f;
                final double v02 = (texY + (1.0f - hh1) * 16.0f) / 256.0f;
                final double v1 = (texY + 16 - 0.01) / 256.0;

                float br = tt.getBrightness(this.level, xt, y, zt);
                if (face < 2) br *= c2;
                else br *= c3;

                t.color(c11 * br * r, c11 * br * g, c11 * br * b);
                t.vertexUV(x0, y + hh0, z0, u0, v01);
                t.vertexUV(x1, y + hh1, z1, u1, v02);
                t.vertexUV(x1, y + 0, z1, u1, v1);
                t.vertexUV(x0, y + 0, z0, u0, v1);
            }
        }

        tt.yy0 = yo0;
        tt.yy1 = yo1;

        return changed;
    }
    
    private float getWaterHeight(final int x, final int y, final int z, final Material m) {
        int count = 0;
        float h = 0.0f;
        for (int i = 0; i < 4; ++i) {
            final int xx = x - (i & 0x1);
            final int yy = y;
            final int zz = z - (i >> 1 & 0x1);

            if (this.level.getMaterial(xx, yy + 1, zz) == m) return 1.0f;

            final Material tm = this.level.getMaterial(xx, yy, zz);
            if (tm == m) {
                final int d = this.level.getData(xx, yy, zz);
                if (d >= 8 || d == 0) {
                    h += LiquidTile.getHeight(d) * 10.0f;
                    count += 10;
                }
                h += LiquidTile.getHeight(d);
                count++;
            }
            else if (!tm.isSolid()) {
                h += 1;
                count++;
            }
        }
        return 1.0f - h / count;
    }
    
    public void renderBlock(final Tile tt, final Level level, final int x, final int y, final int z) {
        final float c10 = 0.5f;
        final float c11 = 1.0f;
        final float c2 = 0.8f;
        final float c3 = 0.6f;

        final Tesselator t = Tesselator.instance;
        t.begin();
        final float center = tt.getBrightness(level, x, y, z);
        float br = tt.getBrightness(level, x, y - 1, z);
        if (br < center) br = center;
        t.color(c10 * br, c10 * br, c10 * br);
        this.renderFaceDown(tt, -0.5, -0.5, -0.5, tt.getTexture(Facing.DOWN));

        br = tt.getBrightness(level, x, y + 1, z);
        if (br < center) br = center;
        t.color(c11 * br, c11 * br, c11 * br);
        this.renderFaceUp(tt, -0.5, -0.5, -0.5, tt.getTexture(Facing.UP));

        br = tt.getBrightness(level, x, y, z - 1);
        if (br < center) br = center;
        t.color(c2 * br, c2 * br, c2 * br);
        this.renderNorth(tt, -0.5, -0.5, -0.5, tt.getTexture(Facing.NORTH));

        br = tt.getBrightness(level, x, y, z + 1);
        if (br < center) br = center;
        t.color(c2 * br, c2 * br, c2 * br);
        this.renderSouth(tt, -0.5, -0.5, -0.5, tt.getTexture(Facing.SOUTH));

        br = tt.getBrightness(level, x - 1, y, z);
        if (br < center) br = center;
        t.color(c3 * br, c3 * br, c3 * br);
        this.renderWest(tt, -0.5, -0.5, -0.5, tt.getTexture(Facing.WEST));

        br = tt.getBrightness(level, x + 1, y, z);
        if (br < center) br = center;
        t.color(c3 * br, c3 * br, c3 * br);
        this.renderEast(tt, -0.5, -0.5, -0.5, tt.getTexture(Facing.EAST));
        t.end();
    }
    
    public boolean tesselateBlockInWorld(final Tile tt, final int x, final int y, final int z) {
        final int col = tt.getColor(this.level, x, y, z);
        float r = (col >> 16 & 0xFF) / 255.0f;
        float g = (col >> 8 & 0xFF) / 255.0f;
        float b = (col & 0xFF) / 255.0f;

        if (GameRenderer.anaglyph3d) {
            final float cr = (r * 30.0f + g * 59.0f + b * 11.0f) / 100.0f;
            final float cg = (r * 30.0f + g * 70.0f) / 100.0f;
            final float cb = (r * 30.0f + b * 70.0f) / 100.0f;
            r = cr;
            g = cg;
            b = cb;
        }

        if (Minecraft.useAmbientOcclusion()) {
            return this.tesselateBlockInWorldWithAmbienceOcclusionTexLighting(tt, x, y, z, r, g, b);
        } else {
            return this.tesselateBlockInWorld(tt, x, y, z, r, g, b);
        }
    }
    
    public boolean tesselateBlockInWorldWithAmbienceOcclusionTexLighting(final Tile tt, int pX, int pY, int pZ, final float pBaseRed, final float pBaseGreen, final float pBaseBlue) {
        this.applyAmbienceOcclusion = true;
        boolean changed = false;
        float ll1 = this.ll000;
        float ll2 = this.ll000;
        float ll3 = this.ll000;
        float ll4 = this.ll000;
        boolean tint0 = true;
        boolean tint1 = true;
        boolean tint2 = true;
        boolean tint3 = true;
        boolean tint4 = true;
        boolean tint5 = true;

        this.ll000 = tt.getBrightness(this.level, pX, pY, pZ);
        this.llx00 = tt.getBrightness(this.level, pX - 1, pY, pZ);
        this.ll0y0 = tt.getBrightness(this.level, pX, pY - 1, pZ);
        this.ll00z = tt.getBrightness(this.level, pX, pY, pZ - 1);
        this.llX00 = tt.getBrightness(this.level, pX + 1, pY, pZ);
        this.ll0Y0 = tt.getBrightness(this.level, pX, pY + 1, pZ);
        this.ll00Z = tt.getBrightness(this.level, pX, pY, pZ + 1);

        this.llTransXY0 = Tile.transculent[this.level.getTile(pX + 1, pY + 1, pZ)];
        this.llTransXy0 = Tile.transculent[this.level.getTile(pX + 1, pY - 1, pZ)];
        this.llTransX0Z = Tile.transculent[this.level.getTile(pX + 1, pY, pZ + 1)];
        this.llTransX0z = Tile.transculent[this.level.getTile(pX + 1, pY, pZ - 1)];
        this.llTransxY0 = Tile.transculent[this.level.getTile(pX - 1, pY + 1, pZ)];
        this.llTransxy0 = Tile.transculent[this.level.getTile(pX - 1, pY - 1, pZ)];
        this.llTransx0z = Tile.transculent[this.level.getTile(pX - 1, pY, pZ - 1)];
        this.llTransx0Z = Tile.transculent[this.level.getTile(pX - 1, pY, pZ + 1)];
        this.llTrans0YZ = Tile.transculent[this.level.getTile(pX, pY + 1, pZ + 1)];
        this.llTrans0Yz = Tile.transculent[this.level.getTile(pX, pY + 1, pZ - 1)];
        this.llTrans0yZ = Tile.transculent[this.level.getTile(pX, pY - 1, pZ + 1)];
        this.llTrans0yz = Tile.transculent[this.level.getTile(pX, pY - 1, pZ - 1)];

        if (tt.tex == 3) tint0 = tint2 = tint3 = tint4 = tint5 = false;
        if (this.fixedTexture >= 0) tint0 = tint2 = tint3 = tint4 = tint5 = false;

        if (this.noCulling || tt.shouldRenderFace(this.level, pX, pY - 1, pZ, Facing.DOWN)) {
            if (this.blsmooth > 0) {
                --pY;
                this.llxy0 = tt.getBrightness(this.level, pX - 1, pY, pZ);
                this.ll0yz = tt.getBrightness(this.level, pX, pY, pZ - 1);
                this.ll0yZ = tt.getBrightness(this.level, pX, pY, pZ + 1);
                this.llXy0 = tt.getBrightness(this.level, pX + 1, pY, pZ);

                if (this.llTrans0yz || this.llTransxy0) this.llxyz = tt.getBrightness(this.level, pX - 1, pY, pZ - 1);
                else this.llxyz = this.llxy0;

                if (this.llTrans0yZ || this.llTransxy0) this.llxyZ = tt.getBrightness(this.level, pX - 1, pY, pZ + 1);
                else this.llxyZ = this.llxy0;

                if (this.llTrans0yz || this.llTransXy0) this.llXyz = tt.getBrightness(this.level, pX + 1, pY, pZ - 1);
                else this.llXyz = this.llXy0;

                if (this.llTrans0yZ || this.llTransXy0) this.llXyZ = tt.getBrightness(this.level, pX + 1, pY, pZ + 1);
                else this.llXyZ = this.llXy0;
                ++pY;

                ll1 = (this.llxyZ + this.llxy0 + this.ll0yZ + this.ll0y0) / 4.0f;
                ll4 = (this.ll0yZ + this.ll0y0 + this.llXyZ + this.llXy0) / 4.0f;
                ll3 = (this.ll0y0 + this.ll0yz + this.llXy0 + this.llXyz) / 4.0f;
                ll2 = (this.llxy0 + this.llxyz + this.ll0y0 + this.ll0yz) / 4.0f;
            }
            else {
                ll1 = ll2 = ll3 = ll4 = this.ll0y0;
            }

            this.c1r = this.c2r = this.c3r = this.c4r = (tint0 ? pBaseRed : 1.0f) * 0.5f;
            this.c1g = this.c2g = this.c3g = this.c4g = (tint0 ? pBaseGreen : 1.0f) * 0.5f;
            this.c1b = this.c2b = this.c3b = this.c4b = (tint0 ? pBaseBlue : 1.0f) * 0.5f;
            this.c1r *= ll1;
            this.c1g *= ll1;
            this.c1b *= ll1;
            this.c2r *= ll2;
            this.c2g *= ll2;
            this.c2b *= ll2;
            this.c3r *= ll3;
            this.c3g *= ll3;
            this.c3b *= ll3;
            this.c4r *= ll4;
            this.c4g *= ll4;
            this.c4b *= ll4;

            this.renderFaceDown(tt, pX, pY, pZ, tt.getTexture(this.level, pX, pY, pZ, Facing.DOWN));
            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, pX, pY + 1, pZ, Facing.UP)) {
            if (this.blsmooth > 0) {
                ++pY;
                this.llxY0 = tt.getBrightness(this.level, pX - 1, pY, pZ);
                this.llXY0 = tt.getBrightness(this.level, pX + 1, pY, pZ);
                this.ll0Yz = tt.getBrightness(this.level, pX, pY, pZ - 1);
                this.ll0YZ = tt.getBrightness(this.level, pX, pY, pZ + 1);

                if (this.llTrans0Yz || this.llTransxY0) this.llxYz = tt.getBrightness(this.level, pX - 1, pY, pZ - 1);
                else this.llxYz = this.llxY0;

                if (this.llTrans0Yz || this.llTransXY0) this.llXYz = tt.getBrightness(this.level, pX + 1, pY, pZ - 1);
                else this.llXYz = this.llXY0;

                if (this.llTrans0YZ || this.llTransxY0) this.llxYZ = tt.getBrightness(this.level, pX - 1, pY, pZ + 1);
                else this.llxYZ = this.llxY0;

                if (this.llTrans0YZ || this.llTransXY0) this.llXYZ = tt.getBrightness(this.level, pX + 1, pY, pZ + 1);
                else this.llXYZ = this.llXY0;
                --pY;

                ll4 = (this.llxYZ + this.llxY0 + this.ll0YZ + this.ll0Y0) / 4.0f;
                ll1 = (this.ll0YZ + this.ll0Y0 + this.llXYZ + this.llXY0) / 4.0f;
                ll2 = (this.ll0Y0 + this.ll0Yz + this.llXY0 + this.llXYz) / 4.0f;
                ll3 = (this.llxY0 + this.llxYz + this.ll0Y0 + this.ll0Yz) / 4.0f;
            }
            else {
                ll1 = ll2 = ll3 = ll4 = this.ll0Y0;
            }

            this.c1r = this.c2r = this.c3r = this.c4r = ( tint1 ? pBaseRed : 1.0f );
            this.c1g = this.c2g = this.c3g = this.c4g = ( tint1 ? pBaseGreen : 1.0f );
            this.c1b = this.c2b = this.c3b = this.c4b = ( tint1 ? pBaseBlue : 1.0f );
            this.c1r *= ll1;
            this.c1g *= ll1;
            this.c1b *= ll1;
            this.c2r *= ll2;
            this.c2g *= ll2;
            this.c2b *= ll2;
            this.c3r *= ll3;
            this.c3g *= ll3;
            this.c3b *= ll3;
            this.c4r *= ll4;
            this.c4g *= ll4;
            this.c4b *= ll4;
            this.renderFaceUp(tt, pX, pY, pZ, tt.getTexture(this.level, pX, pY, pZ, Facing.UP));
            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, pX, pY, pZ - 1, Facing.NORTH)) {
            if (this.blsmooth > 0) {
                --pZ;
                this.llx0z = tt.getBrightness(this.level, pX - 1, pY, pZ);
                this.ll0yz = tt.getBrightness(this.level, pX, pY - 1, pZ);
                this.ll0Yz = tt.getBrightness(this.level, pX, pY + 1, pZ);
                this.llX0z = tt.getBrightness(this.level, pX + 1, pY, pZ);

                if (this.llTransx0z || this.llTrans0yz) this.llxyz = tt.getBrightness(this.level, pX - 1, pY - 1, pZ);
                else this.llxyz = this.llx0z;

                if (this.llTransx0z || this.llTrans0Yz) this.llxYz = tt.getBrightness(this.level, pX - 1, pY + 1, pZ);
                else this.llxYz = this.llx0z;

                if (this.llTransX0z || this.llTrans0yz) this.llXyz = tt.getBrightness(this.level, pX + 1, pY - 1, pZ);
                else this.llXyz = this.llX0z;

                if (this.llTransX0z || this.llTrans0Yz) this.llXYz = tt.getBrightness(this.level, pX + 1, pY + 1, pZ);
                else this.llXYz = this.llX0z;
                ++pZ;

                ll1 = (this.llx0z + this.llxYz + this.ll00z + this.ll0Yz) / 4.0f;
                ll2 = (this.ll00z + this.ll0Yz + this.llX0z + this.llXYz) / 4.0f;
                ll3 = (this.ll0yz + this.ll00z + this.llXyz + this.llX0z) / 4.0f;
                ll4 = (this.llxyz + this.llx0z + this.ll0yz + this.ll00z) / 4.0f;
            }
            else {
                ll1 = ll2 = ll3 = ll4 = this.ll00z;
            }

            this.c1r = this.c2r = this.c3r = this.c4r = (tint2 ? pBaseRed : 1.0f) * 0.8f;
            this.c1g = this.c2g = this.c3g = this.c4g = (tint2 ? pBaseGreen : 1.0f) * 0.8f;
            this.c1b = this.c2b = this.c3b = this.c4b = (tint2 ? pBaseBlue : 1.0f) * 0.8f;
            this.c1r *= ll1;
            this.c1g *= ll1;
            this.c1b *= ll1;
            this.c2r *= ll2;
            this.c2g *= ll2;
            this.c2b *= ll2;
            this.c3r *= ll3;
            this.c3g *= ll3;
            this.c3b *= ll3;
            this.c4r *= ll4;
            this.c4g *= ll4;
            this.c4b *= ll4;

            final int tex = tt.getTexture(this.level, pX, pY, pZ, Facing.NORTH);
            this.renderNorth(tt, pX, pY, pZ, tex);

            if (TileRenderer.fancy && tex == 3 && this.fixedTexture < 0) {
                this.c1r *= pBaseRed;
                this.c2r *= pBaseRed;
                this.c3r *= pBaseRed;
                this.c4r *= pBaseRed;
                this.c1g *= pBaseGreen;
                this.c2g *= pBaseGreen;
                this.c3g *= pBaseGreen;
                this.c4g *= pBaseGreen;
                this.c1b *= pBaseBlue;
                this.c2b *= pBaseBlue;
                this.c3b *= pBaseBlue;
                this.c4b *= pBaseBlue;
                this.renderNorth(tt, pX, pY, pZ, 38);
            }
            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, pX, pY, pZ + 1, Facing.SOUTH)) {
            if (this.blsmooth > 0) {
                ++pZ;
                this.llx0Z = tt.getBrightness(this.level, pX - 1, pY, pZ);
                this.llX0Z = tt.getBrightness(this.level, pX + 1, pY, pZ);
                this.ll0yZ = tt.getBrightness(this.level, pX, pY - 1, pZ);
                this.ll0YZ = tt.getBrightness(this.level, pX, pY + 1, pZ);

                if (this.llTransx0Z || this.llTrans0yZ) this.llxyZ = tt.getBrightness(this.level, pX - 1, pY - 1, pZ);
                else this.llxyZ = this.llx0Z;

                if (this.llTransx0Z || this.llTrans0YZ) this.llxYZ = tt.getBrightness(this.level, pX - 1, pY + 1, pZ);
                else this.llxYZ = this.llx0Z;

                if (this.llTransX0Z || this.llTrans0yZ) this.llXyZ = tt.getBrightness(this.level, pX + 1, pY - 1, pZ);
                else this.llXyZ = this.llX0Z;

                if (this.llTransX0Z || this.llTrans0YZ) this.llXYZ = tt.getBrightness(this.level, pX + 1, pY + 1, pZ);
                else this.llXYZ = this.llX0Z;
                --pZ;

                ll1 = (this.llx0Z + this.llxYZ + this.ll00Z + this.ll0YZ) / 4.0f;
                ll4 = (this.ll00Z + this.ll0YZ + this.llX0Z + this.llXYZ) / 4.0f;
                ll3 = (this.ll0yZ + this.ll00Z + this.llXyZ + this.llX0Z) / 4.0f;
                ll2 = (this.llxyZ + this.llx0Z + this.ll0yZ + this.ll00Z) / 4.0f;
            }
            else {
                ll1 = ll2 = ll3 = ll4 = this.ll00Z;
            }

            this.c1r = this.c2r = this.c3r = this.c4r = (tint3 ? pBaseRed : 1.0f) * 0.8f;
            this.c1g = this.c2g = this.c3g = this.c4g = (tint3 ? pBaseGreen : 1.0f) * 0.8f;
            this.c1b = this.c2b = this.c3b = this.c4b = (tint3 ? pBaseBlue : 1.0f) * 0.8f;
            this.c1r *= ll1;
            this.c1g *= ll1;
            this.c1b *= ll1;
            this.c2r *= ll2;
            this.c2g *= ll2;
            this.c2b *= ll2;
            this.c3r *= ll3;
            this.c3g *= ll3;
            this.c3b *= ll3;
            this.c4r *= ll4;
            this.c4g *= ll4;
            this.c4b *= ll4;
            final int tex = tt.getTexture(this.level, pX, pY, pZ, Facing.SOUTH);
            this.renderSouth(tt, pX, pY, pZ, tt.getTexture(this.level, pX, pY, pZ, Facing.SOUTH));

            if (TileRenderer.fancy && tex == 3 && this.fixedTexture < 0) {
                this.c1r *= pBaseRed;
                this.c2r *= pBaseRed;
                this.c3r *= pBaseRed;
                this.c4r *= pBaseRed;
                this.c1g *= pBaseGreen;
                this.c2g *= pBaseGreen;
                this.c3g *= pBaseGreen;
                this.c4g *= pBaseGreen;
                this.c1b *= pBaseBlue;
                this.c2b *= pBaseBlue;
                this.c3b *= pBaseBlue;
                this.c4b *= pBaseBlue;
                this.renderSouth(tt, pX, pY, pZ, 38);
            }
            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, pX - 1, pY, pZ, Facing.WEST)) {
            if (this.blsmooth > 0) {
                --pX;
                this.llxy0 = tt.getBrightness(this.level, pX, pY - 1, pZ);
                this.llx0z = tt.getBrightness(this.level, pX, pY, pZ - 1);
                this.llx0Z = tt.getBrightness(this.level, pX, pY, pZ + 1);
                this.llxY0 = tt.getBrightness(this.level, pX, pY + 1, pZ);

                if (this.llTransx0z || this.llTransxy0) this.llxyz = tt.getBrightness(this.level, pX, pY - 1, pZ - 1);
                else this.llxyz = this.llx0z;

                if (this.llTransx0Z || this.llTransxy0) this.llxyZ = tt.getBrightness(this.level, pX, pY - 1, pZ + 1);
                else this.llxyZ = this.llx0Z;

                if (this.llTransx0z || this.llTransxY0) this.llxYz = tt.getBrightness(this.level, pX, pY + 1, pZ - 1);
                else this.llxYz = this.llx0z;

                if (this.llTransx0Z || this.llTransxY0) this.llxYZ = tt.getBrightness(this.level, pX, pY + 1, pZ + 1);
                else this.llxYZ = this.llx0Z;
                ++pX;

                ll4 = (this.llxy0 + this.llxyZ + this.llx00 + this.llx0Z) / 4.0f;
                ll1 = (this.llx00 + this.llx0Z + this.llxY0 + this.llxYZ) / 4.0f;
                ll2 = (this.llx0z + this.llx00 + this.llxYz + this.llxY0) / 4.0f;
                ll3 = (this.llxyz + this.llxy0 + this.llx0z + this.llx00) / 4.0f;
            }
            else {
                ll1 = ll2 = ll3 = ll4 = this.llx00;
            }

            this.c1r = this.c2r = this.c3r =  this.c4r = (tint4 ? pBaseRed : 1.0f) * 0.6f;
            this.c1g = this.c2g = this.c3g = this.c4g = (tint4 ? pBaseGreen : 1.0f) * 0.6f;
            this.c1b = this.c2b = this.c3b = this.c4b = (tint4 ? pBaseBlue : 1.0f) * 0.6f;
            this.c1r *= ll1;
            this.c1g *= ll1;
            this.c1b *= ll1;
            this.c2r *= ll2;
            this.c2g *= ll2;
            this.c2b *= ll2;
            this.c3r *= ll3;
            this.c3g *= ll3;
            this.c3b *= ll3;
            this.c4r *= ll4;
            this.c4g *= ll4;
            this.c4b *= ll4;

            final int tex = tt.getTexture(this.level, pX, pY, pZ, Facing.WEST);
            this.renderWest(tt, pX, pY, pZ, tex);

            if (TileRenderer.fancy && tex == 3 && this.fixedTexture < 0) {
                this.c1r *= pBaseRed;
                this.c2r *= pBaseRed;
                this.c3r *= pBaseRed;
                this.c4r *= pBaseRed;
                this.c1g *= pBaseGreen;
                this.c2g *= pBaseGreen;
                this.c3g *= pBaseGreen;
                this.c4g *= pBaseGreen;
                this.c1b *= pBaseBlue;
                this.c2b *= pBaseBlue;
                this.c3b *= pBaseBlue;
                this.c4b *= pBaseBlue;
                this.renderWest(tt, pX, pY, pZ, 38);
            }
            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, pX + 1, pY, pZ, Facing.EAST)) {
            if (this.blsmooth > 0) {
                ++pX;
                this.llXy0 = tt.getBrightness(this.level, pX, pY - 1, pZ);
                this.llX0z = tt.getBrightness(this.level, pX, pY, pZ - 1);
                this.llX0Z = tt.getBrightness(this.level, pX, pY, pZ + 1);
                this.llXY0 = tt.getBrightness(this.level, pX, pY + 1, pZ);

                if (this.llTransXy0 || this.llTransX0z) this.llXyz = tt.getBrightness(this.level, pX, pY - 1, pZ - 1);
                else this.llXyz = this.llX0z;

                if (this.llTransXy0 || this.llTransX0Z) this.llXyZ = tt.getBrightness(this.level, pX, pY - 1, pZ + 1);
                else this.llXyZ = this.llX0Z;

                if (this.llTransXY0 || this.llTransX0z) this.llXYz = tt.getBrightness(this.level, pX, pY + 1, pZ - 1);
                else this.llXYz = this.llX0z;

                if (this.llTransXY0 || this.llTransX0Z) this.llXYZ = tt.getBrightness(this.level, pX, pY + 1, pZ + 1);
                else this.llXYZ = this.llX0Z;
                --pX;

                ll1 = (this.llXy0 + this.llXyZ + this.llX00 + this.llX0Z) / 4.0f;
                ll4 = (this.llX00 + this.llX0Z + this.llXY0 + this.llXYZ) / 4.0f;
                ll3 = (this.llX0z + this.llX00 + this.llXYz + this.llXY0) / 4.0f;
                ll2 = (this.llXyz + this.llXy0 + this.llX0z + this.llX00) / 4.0f;
            }
            else {
                ll1 = ll2 = ll3 = ll4 = this.llX00;
            }

            this.c1r = this.c2r = this.c3r = this.c4r = (tint5 ? pBaseRed : 1.0f) * 0.6f;
            this.c1g = this.c2g = this.c3g = this.c4g = (tint5 ? pBaseGreen : 1.0f) * 0.6f;
            this.c1b = this.c2b = this.c3b = this.c4b = (tint5 ? pBaseBlue : 1.0f) * 0.6f;
            this.c1r *= ll1;
            this.c1g *= ll1;
            this.c1b *= ll1;
            this.c2r *= ll2;
            this.c2g *= ll2;
            this.c2b *= ll2;
            this.c3r *= ll3;
            this.c3g *= ll3;
            this.c3b *= ll3;
            this.c4r *= ll4;
            this.c4g *= ll4;
            this.c4b *= ll4;

            final int tex = tt.getTexture(this.level, pX, pY, pZ, Facing.EAST);
            this.renderEast(tt, pX, pY, pZ, tex);

            if (TileRenderer.fancy && tex == 3 && this.fixedTexture < 0) {
                this.c1r *= pBaseRed;
                this.c2r *= pBaseRed;
                this.c3r *= pBaseRed;
                this.c4r *= pBaseRed;
                this.c1g *= pBaseGreen;
                this.c2g *= pBaseGreen;
                this.c3g *= pBaseGreen;
                this.c4g *= pBaseGreen;
                this.c1b *= pBaseBlue;
                this.c2b *= pBaseBlue;
                this.c3b *= pBaseBlue;
                this.c4b *= pBaseBlue;
                this.renderEast(tt, pX, pY, pZ, 38);
            }
            changed = true;
        }
        this.applyAmbienceOcclusion = false;

        return changed;
    }
    
    public boolean tesselateBlockInWorld(final Tile tt, final int x, final int y, final int z, final float r, final float g, final float b) {
        this.applyAmbienceOcclusion = false;

        final Tesselator t = Tesselator.instance;

        boolean changed = false;
        final float c10 = 0.5f;
        final float c11 = 1.0f;
        final float c2 = 0.8f;
        final float c3 = 0.6f;

        final float r11 = c11 * r;
        final float g11 = c11 * g;
        final float b11 = c11 * b;

        float r10 = c10;
        float r2 = c2;
        float r3 = c3;

        float g10 = c10;
        float g2 = c2;
        float g3 = c3;

        float b10 = c10;
        float b2 = c2;
        float b3 = c3;

        if (tt != Tile.grass) {
            r10 *= r;
            r2 *= r;
            r3 *= r;

            g10 *= g;
            g2 *= g;
            g3 *= g;

            b10 *= b;
            b2 *= b;
            b3 *= b;
        }

        final float centerBrightness = tt.getBrightness(this.level, x, y, z);

        if (this.noCulling || tt.shouldRenderFace(this.level, x, y - 1, z, Facing.DOWN)) {
            final float br = tt.getBrightness(this.level, x, y - 1, z);
            t.color(r10 * br, g10 * br, b10 * br);

            this.renderFaceDown(tt, x, y, z, tt.getTexture(this.level, x, y, z, Facing.DOWN));
            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x, y + 1, z, Facing.UP)) {
            float br = tt.getBrightness(this.level, x, y + 1, z);
            if (tt.yy1 != 1.0 && !tt.material.isLiquid()) br = centerBrightness;
            t.color(r11 * br, g11 * br, b11 * br);

            this.renderFaceUp(tt, x, y, z, tt.getTexture(this.level, x, y, z, Facing.UP));
            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x, y, z - 1, Facing.NORTH)) {
            float br = tt.getBrightness(this.level, x, y, z - 1);
            if (tt.zz0 > 0.0) br = centerBrightness;
            t.color(r2 * br, g2 * br, b2 * br);
            final int tex = tt.getTexture(this.level, x, y, z, Facing.NORTH);
            this.renderNorth(tt, x, y, z, tex);

            if (TileRenderer.fancy && tex == 3 && this.fixedTexture < 0) {
                t.color(r2 * br * r, g2 * br * g, b2 * br * b);
                this.renderNorth(tt, x, y, z, 38);
            }
            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x, y, z + 1, Facing.SOUTH)) {
            float br = tt.getBrightness(this.level, x, y, z + 1);
            if (tt.zz1 < 1.0) br = centerBrightness;
            t.color(r2 * br, g2 * br, b2 * br);
            final int tex = tt.getTexture(this.level, x, y, z, Facing.SOUTH);
            this.renderSouth(tt, x, y, z, tex);

            if (TileRenderer.fancy && tex == 3 && this.fixedTexture < 0) {
                t.color(r2 * br * r, g2 * br * g, b2 * br * b);
                this.renderSouth(tt, x, y, z, 38);
            }
            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x - 1, y, z, Facing.WEST)) {
            float br = tt.getBrightness(this.level, x - 1, y, z);
            if (tt.xx0 > 0.0) br = centerBrightness;
            t.color(r3 * br, g3 * br, b3 * br);
            final int tex = tt.getTexture(this.level, x, y, z, Facing.WEST);
            this.renderWest(tt, x, y, z, tex);

            if (TileRenderer.fancy && tex == 3 && this.fixedTexture < 0) {
                t.color(r3 * br * r, g3 * br * g, b3 * br * b);
                this.renderWest(tt, x, y, z, 38);
            }
            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x + 1, y, z, Facing.EAST)) {
            float br = tt.getBrightness(this.level, x + 1, y, z);
            if (tt.xx1 < 1.0) br = centerBrightness;
            t.color(r3 * br, g3 * br, b3 * br);
            final int tex = tt.getTexture(this.level, x, y, z, Facing.EAST);
            this.renderEast(tt, x, y, z, tex);

            if (TileRenderer.fancy && tex == 3 && this.fixedTexture < 0) {
                t.color(r3 * br * r, g3 * br * g, b3 * br * b);
                this.renderEast(tt, x, y, z, 38);
            }
            changed = true;
        }
        return changed;
    }
    
    public boolean tesselateCactusInWorld(final Tile tt, final int x, final int y, final int z) {
        final int col = tt.getColor(this.level, x, y, z);
        float r = (col >> 16 & 0xFF) / 255.0f;
        float g = (col >> 8 & 0xFF) / 255.0f;
        float b = (col & 0xFF) / 255.0f;

        if (GameRenderer.anaglyph3d) {
            final float cr = (r * 30.0f + g * 59.0f + b * 11.0f) / 100.0f;
            final float cg = (r * 30.0f + g * 70.0f) / 100.0f;
            final float cb = (r * 30.0f + b * 70.0f) / 100.0f;
            r = cr;
            g = cg;
            b = cb;
        }

        return this.tesselateCactusInWorld(tt, x, y, z, r, g, b);
    }
    
    public boolean tesselateCactusInWorld(final Tile tt, final int x, final int y, final int z, final float r, final float g, final float b) {
        final Tesselator t = Tesselator.instance;

        boolean changed = false;
        final float c10 = 0.5f;
        final float c11 = 1.0f;
        final float c2 = 0.8f;
        final float c3 = 0.6f;

        final float r10 = c10 * r;
        final float r11 = c11 * r;
        final float r2 = c2 * r;
        final float r3 = c3 * r;

        final float g10 = c10 * g;
        final float g11 = c11 * g;
        final float g2 = c2 * g;
        final float g3 = c3 * g;

        final float b10 = c10 * b;
        final float b11 = c11 * b;
        final float b2 = c2 * b;
        final float b3 = c3 * b;

        final float s = 1 / 16.0f;

        final float centerBrightness = tt.getBrightness(this.level, x, y, z);

        if (this.noCulling || tt.shouldRenderFace(this.level, x, y - 1, z, Facing.DOWN)) {
            final float br = tt.getBrightness(this.level, x, y - 1, z);
            t.color(r10 * br, g10 * br, b10 * br);

            this.renderFaceDown(tt, x, y, z, tt.getTexture(this.level, x, y, z, Facing.DOWN));
            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x, y + 1, z, Facing.UP)) {
            float br = tt.getBrightness(this.level, x, y + 1, z);
            if (tt.yy1 != 1.0 && !tt.material.isLiquid()) br = centerBrightness;
            t.color(r11 * br, g11 * br, b11 * br);

            this.renderFaceUp(tt, x, y, z, tt.getTexture(this.level, x, y, z, Facing.UP));
            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x, y, z - 1, Facing.NORTH)) {
            float br = tt.getBrightness(this.level, x, y, z - 1);
            if (tt.zz0 > 0.0) br = centerBrightness;
            t.color(r2 * br, g2 * br, b2 * br);

            t.addOffset(0.0f, 0.0f, s);
            this.renderNorth(tt, x, y, z, tt.getTexture(this.level, x, y, z, Facing.NORTH));
            t.addOffset(0.0f, 0.0f, -s);
            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x, y, z + 1, Facing.SOUTH)) {
            float br = tt.getBrightness(this.level, x, y, z + 1);
            if (tt.zz1 < 1.0) br = centerBrightness;
            t.color(r2 * br, g2 * br, b2 * br);

            t.addOffset(0.0f, 0.0f, -s);
            this.renderSouth(tt, x, y, z, tt.getTexture(this.level, x, y, z, Facing.SOUTH));
            t.addOffset(0.0f, 0.0f, s);
            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x - 1, y, z, Facing.WEST)) {
            float br = tt.getBrightness(this.level, x - 1, y, z);
            if (tt.xx0 > 0.0) br = centerBrightness;
            t.color(r3 * br, g3 * br, b3 * br);

            t.addOffset(s, 0.0f, 0.0f);
            this.renderWest(tt, x, y, z, tt.getTexture(this.level, x, y, z, Facing.WEST));
            t.addOffset(-s, 0.0f, 0.0f);
            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x + 1, y, z, Facing.EAST)) {
            float br = tt.getBrightness(this.level, x + 1, y, z);
            if (tt.xx1 < 1.0) br = centerBrightness;
            t.color(r3 * br, g3 * br, b3 * br);

            t.addOffset(-s, 0.0f, 0.0f);
            this.renderEast(tt, x, y, z, tt.getTexture(this.level, x, y, z, Facing.EAST));
            t.addOffset(s, 0.0f, 0.0f);
            changed = true;
        }

        return changed;
    }
    
    public boolean tesselateFenceInWorld(final Tile tt, final int x, final int y, final int z) {
        float a = 6 / 16.0f;
        float b = 10 / 16.0f;

        tt.setShape(a, 0.0f, a, b, 1.0f, b);
        this.tesselateBlockInWorld(tt, x, y, z);
        boolean changed = true;

        boolean vertical = false;
        boolean horizontal = false;

        if (this.level.getTile(x - 1, y, z) == tt.id || this.level.getTile(x + 1, y, z) == tt.id) vertical = true;
        if (this.level.getTile(x, y, z - 1) == tt.id || this.level.getTile(x, y, z + 1) == tt.id) horizontal = true;

        final boolean l = this.level.getTile(x - 1, y, z) == tt.id;
        final boolean r = this.level.getTile(x + 1, y, z) == tt.id;
        final boolean u = this.level.getTile(x, y, z - 1) == tt.id;
        final boolean d = this.level.getTile(x, y, z + 1) == tt.id;

        if (!vertical && !horizontal) vertical = true;

        a = 7 / 16.0f;
        b = 9 / 16.0f;
        float h0 = 12 / 16.0f;
        float h1 = 15 / 16.0f;

        final float x0 = l ? 0.0f : a;
        final float x1 = r ? 1.0f : b;
        final float z0 = u ? 0.0f : a;
        final float z1 = d ? 1.0f : b;

        if (vertical) {
            tt.setShape(x0, h0, a, x1, h1, b);
            this.tesselateBlockInWorld(tt, x, y, z);
            changed = true;
        }

        if (horizontal) {
            tt.setShape(a, h0, z0, b, h1, z1);
            this.tesselateBlockInWorld(tt, x, y, z);
            changed = true;
        }

        h0 = 6 / 16.0f;
        h1 = 9 / 16.0f;
        if (vertical) {
            tt.setShape(x0, h0, a, x1, h1, b);
            this.tesselateBlockInWorld(tt, x, y, z);
            changed = true;
        }
        if (horizontal) {
            tt.setShape(a, h0, z0, b, h1, z1);
            this.tesselateBlockInWorld(tt, x, y, z);
            changed = true;
        }

        tt.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);

        return changed;
    }
    
    public boolean tesselateStairsInWorld(final Tile tt, final int x, final int y, final int z) {
        boolean changed = false;
        final int data = this.level.getData(x, y, z);
        if (data == 0) {
            tt.setShape(0.0f, 0.0f, 0.0f, 0.5f, 0.5f, 1.0f);
            this.tesselateBlockInWorld(tt, x, y, z);
            tt.setShape(0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
            this.tesselateBlockInWorld(tt, x, y, z);
            changed = true;
        }
        else if (data == 1) {
            tt.setShape(0.0f, 0.0f, 0.0f, 0.5f, 1.0f, 1.0f);
            this.tesselateBlockInWorld(tt, x, y, z);
            tt.setShape(0.5f, 0.0f, 0.0f, 1.0f, 0.5f, 1.0f);
            this.tesselateBlockInWorld(tt, x, y, z);
            changed = true;
        }
        else if (data == 2) {
            tt.setShape(0.0f, 0.0f, 0.0f, 1.0f, 0.5f, 0.5f);
            this.tesselateBlockInWorld(tt, x, y, z);
            tt.setShape(0.0f, 0.0f, 0.5f, 1.0f, 1.0f, 1.0f);
            this.tesselateBlockInWorld(tt, x, y, z);
            changed = true;
        }
        else if (data == 3) {
            tt.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.5f);
            this.tesselateBlockInWorld(tt, x, y, z);
            tt.setShape(0.0f, 0.0f, 0.5f, 1.0f, 0.5f, 1.0f);
            this.tesselateBlockInWorld(tt, x, y, z);
            changed = true;
        }

        tt.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);

        return changed;
    }
    
    public boolean tesselateDoorInWorld(final Tile tt, final int x, final int y, final int z) {
        final Tesselator t = Tesselator.instance;
        final DoorTile doorTile = (DoorTile)tt;
        final float c10 = 0.5f;
        final float c11 = 1.0f;
        final float c2 = 0.8f;
        final float c3 = 0.6f;

        final float centerBrightness = tt.getBrightness(this.level, x, y, z);
        float br = tt.getBrightness(this.level, x, y - 1, z);
        if (doorTile.yy0 > 0.0) br = centerBrightness;
        if (Tile.lightEmission[tt.id] > 0) br = 1.0f;
        t.color(c10 * br, c10 * br, c10 * br);
        this.renderFaceDown(tt, x, y, z, tt.getTexture(this.level, x, y, z, Facing.DOWN));

        br = tt.getBrightness(this.level, x, y + 1, z);
        if (doorTile.yy1 < 1.0) br = centerBrightness;
        if (Tile.lightEmission[tt.id] > 0) br = 1.0f;
        t.color(c11 * br, c11 * br, c11 * br);
        this.renderFaceUp(tt, x, y, z, tt.getTexture(this.level, x, y, z, Facing.UP));

        br = tt.getBrightness(this.level, x, y, z - 1);
        if (doorTile.zz0 > 0.0) br = centerBrightness;
        if (Tile.lightEmission[tt.id] > 0) br = 1.0f;
        t.color(c2 * br, c2 * br, c2 * br);
        int tex = tt.getTexture(this.level, x, y, z, Facing.NORTH);
        if (tex < 0) {
            this.xFlipTexture = true;
            tex = -tex;
        }
        this.renderNorth(tt, x, y, z, tex);

        this.xFlipTexture = false;
        br = tt.getBrightness(this.level, x, y, z + 1);
        if (doorTile.zz1 < 1.0) br = centerBrightness;
        if (Tile.lightEmission[tt.id] > 0) br = 1.0f;
        t.color(c2 * br, c2 * br, c2 * br);
        tex = tt.getTexture(this.level, x, y, z, Facing.SOUTH);
        if (tex < 0) {
            this.xFlipTexture = true;
            tex = -tex;
        }
        this.renderSouth(tt, x, y, z, tex);

        this.xFlipTexture = false;
        br = tt.getBrightness(this.level, x - 1, y, z);
        if (doorTile.xx0 > 0.0) br = centerBrightness;
        if (Tile.lightEmission[tt.id] > 0) br = 1.0f;
        t.color(c3 * br, c3 * br, c3 * br);
        tex = tt.getTexture(this.level, x, y, z, Facing.WEST);
        if (tex < 0) {
            this.xFlipTexture = true;
            tex = -tex;
        }
        this.renderWest(tt, x, y, z, tex);

        this.xFlipTexture = false;
        br = tt.getBrightness(this.level, x + 1, y, z);
        if (doorTile.xx1 < 1.0) br = centerBrightness;
        if (Tile.lightEmission[tt.id] > 0) br = 1.0f;
        t.color(c3 * br, c3 * br, c3 * br);
        tex = tt.getTexture(this.level, x, y, z, Facing.EAST);
        if (tex < 0) {
            this.xFlipTexture = true;
            tex = -tex;
        }
        this.renderEast(tt, x, y, z, tex);

        final boolean changed = true;
        this.xFlipTexture = false;
        return changed;
    }
    
    public void renderFaceDown(final Tile tt, final double x, final double y, final double z, int tex) {
        final Tesselator t = Tesselator.instance;

        if (this.fixedTexture >= 0) tex = this.fixedTexture;
        final int texX = (tex & 0xF) << 4;
        final int texY = tex & 0xF0;
        double u00 = (texX + tt.xx0 * 16.0) / 256.0;
        double u11 = (texX + tt.xx1 * 16.0 - 0.01) / 256.0;
        double v00 = (texY + tt.zz0 * 16.0) / 256.0;
        double v11 = (texY + tt.zz1 * 16.0 - 0.01) / 256.0;

        if (tt.xx0 < 0.0 || tt.xx1 > 1.0) {
            u00 = (texX + 0.0f) / 256.0f;
            u11 = (texX + 15.99f) / 256.0f;
        }
        if (tt.zz0 < 0.0 || tt.zz1 > 1.0) {
            v00 = (texY + 0.0f) / 256.0f;
            v11 = (texY + 15.99f) / 256.0f;
        }

        double u01 = u11, u10 = u00, v01 = v00, v10 = v11;
        if (this.downFlip == FLIP_CCW) {
            u00 = (texX + tt.zz0 * 16.0) / 256.0;
            v00 = (texY + 16 - tt.xx1 * 16.0) / 256.0;
            u11 = (texX + tt.zz1 * 16.0) / 256.0;
            v11 = (texY + 16 - tt.xx0 * 16.0) / 256.0;

            u01 = u11;
            u10 = u00;
            v01 = v00;
            v10 = v11;
            u01 = u00;
            u10 = u11;
            v00 = v11;
            v11 = v01;
        }
        else if (this.downFlip == FLIP_CW) {
            // reshape
            u00 = (texX + 16 - tt.zz1 * 16.0) / 256.0;
            v00 = (texY + tt.xx0 * 16.0) / 256.0;
            u11 = (texX + 16 - tt.zz0 * 16.0) / 256.0;
            v11 = (texY + tt.xx1 * 16.0) / 256.0;

            // rotate
            u01 = u11;
            u10 = u00;
            v01 = v00;
            v10 = v11;
            u00 = u01;
            u11 = u10;
            v01 = v11;
            v10 = v00;
        }
        else if (this.downFlip == FLIP_180) {
            u00 = (texX + 16 - tt.xx0 * 16.0) / 256.0;
            u11 = (texX + 16 - tt.xx1 * 16.0 - 0.01) / 256.0;
            v00 = (texY + 16 - tt.zz0 * 16.0) / 256.0;
            v11 = (texY + 16 - tt.zz1 * 16.0 - 0.01) / 256.0;

            u01 = u11;
            u10 = u00;
            v01 = v00;
            v10 = v11;
        }

        final double x0 = x + tt.xx0;
        final double x1 = x + tt.xx1;
        final double y0 = y + tt.yy0;
        final double z0 = z + tt.zz0;
        final double z1 = z + tt.zz1;

        if (this.applyAmbienceOcclusion) {
            t.color(this.c1r, this.c1g, this.c1b);
            t.vertexUV(x0, y0, z1, u10, v10);
            t.color(this.c2r, this.c2g, this.c2b);
            t.vertexUV(x0, y0, z0, u00, v00);
            t.color(this.c3r, this.c3g, this.c3b);
            t.vertexUV(x1, y0, z0, u01, v01);
            t.color(this.c4r, this.c4g, this.c4b);
            t.vertexUV(x1, y0, z1, u11, v11);
        }
        else {
            t.vertexUV(x0, y0, z1, u10, v10);
            t.vertexUV(x0, y0, z0, u00, v00);
            t.vertexUV(x1, y0, z0, u01, v01);
            t.vertexUV(x1, y0, z1, u11, v11);
        }
    }
    
    public void renderFaceUp(final Tile tt, final double x, final double y, final double z, int tex) {
        final Tesselator t = Tesselator.instance;

        if (this.fixedTexture >= 0) tex = this.fixedTexture;
        final int texX = (tex & 0xF) << 4;
        final int texY = tex & 0xF0;
        double u00 = (texX + tt.xx0 * 16.0) / 256.0;
        double u11 = (texX + tt.xx1 * 16.0 - 0.01) / 256.0;
        double v00 = (texY + tt.zz0 * 16.0) / 256.0;
        double v11 = (texY + tt.zz1 * 16.0 - 0.01) / 256.0;

        if (tt.xx0 < 0.0 || tt.xx1 > 1.0) {
            u00 = (texX + 0.0f) / 256.0f;
            u11 = (texX + 15.99f) / 256.0f;
        }
        if (tt.zz0 < 0.0 || tt.zz1 > 1.0) {
            v00 = (texY + 0.0f) / 256.0f;
            v11 = (texY + 15.99f) / 256.0f;
        }

        double u01 = u11, u10 = u00, v01 = v00, v10 = v11;
        if (this.upFlip == FLIP_CW) {
            u00 = (texX + tt.zz0 * 16.0) / 256.0;
            v00 = (texY + 16 - tt.xx1 * 16.0) / 256.0;
            u11 = (texX + tt.zz1 * 16.0) / 256.0;
            v11 = (texY + 16 - tt.xx0 * 16.0) / 256.0;

            u01 = u11;
            u10 = u00;
            v01 = v00;
            v10 = v11;
            u01 = u00;
            u10 = u11;
            v00 = v11;
            v11 = v01;
        }
        else if (this.upFlip == FLIP_CCW) {
            // reshape
            u00 = (texX + 16 - tt.zz1 * 16.0) / 256.0;
            v00 = (texY + tt.xx0 * 16.0) / 256.0;
            u11 = (texX + 16 - tt.zz0 * 16.0) / 256.0;
            v11 = (texY + tt.xx1 * 16.0) / 256.0;

            // rotate
            u01 = u11;
            u10 = u00;
            v01 = v00;
            v10 = v11;
            u00 = u01;
            u11 = u10;
            v01 = v11;
            v10 = v00;
        }
        else if (this.upFlip == FLIP_180) {
            u00 = (texX + 16 - tt.xx0 * 16.0) / 256.0;
            u11 = (texX + 16 - tt.xx1 * 16.0 - 0.01) / 256.0;
            v00 = (texY + 16 - tt.zz0 * 16.0) / 256.0;
            v11 = (texY + 16 - tt.zz1 * 16.0 - 0.01) / 256.0;

            u01 = u11;
            u10 = u00;
            v01 = v00;
            v10 = v11;
        }

        final double x0 = x + tt.xx0;
        final double x1 = x + tt.xx1;
        final double y1 = y + tt.yy1;
        final double z0 = z + tt.zz0;
        final double z1 = z + tt.zz1;

        if (this.applyAmbienceOcclusion) {
            t.color(this.c1r, this.c1g, this.c1b);
            t.vertexUV(x1, y1, z1, u11, v11);
            t.color(this.c2r, this.c2g, this.c2b);
            t.vertexUV(x1, y1, z0, u01, v01);
            t.color(this.c3r, this.c3g, this.c3b);
            t.vertexUV(x0, y1, z0, u00, v00);
            t.color(this.c4r, this.c4g, this.c4b);
            t.vertexUV(x0, y1, z1, u10, v10);
        }
        else {
            t.vertexUV(x1, y1, z1, u11, v11);
            t.vertexUV(x1, y1, z0, u01, v01);
            t.vertexUV(x0, y1, z0, u00, v00);
            t.vertexUV(x0, y1, z1, u10, v10);
        }
    }
    
    public void renderNorth(final Tile tt, final double x, final double y, final double z, int tex) {
        final Tesselator t = Tesselator.instance;

        if (this.fixedTexture >= 0) tex = this.fixedTexture;
        final int texX = (tex & 0xF) << 4;
        final int texY = tex & 0xF0;
        double u00 = (texX + tt.xx0 * 16.0) / 256.0;
        double u11 = (texX + tt.xx1 * 16.0 - 0.01) / 256.0;
        double v00 = (texY + 16 - tt.yy1 * 16.0) / 256.0;
        double v11 = (texY + 16 - tt.yy0 * 16.0 - 0.01) / 256.0;
        if (this.xFlipTexture) {
            final double tmp = u00;
            u00 = u11;
            u11 = tmp;
        }

        if (tt.xx0 < 0.0 || tt.xx1 > 1.0) {
            u00 = (texX + 0.0f) / 256.0f;
            u11 = (texX + 15.99f) / 256.0f;
        }
        if (tt.yy0 < 0.0 || tt.yy1 > 1.0) {
            v00 = (texY + 0.0f) / 256.0f;
            v11 = (texY + 15.99f) / 256.0f;
        }

        double u01 = u11, u10 = u00, v01 = v00, v10 = v11;
        if (this.northFlip == FLIP_CCW) {
            u00 = (texX + tt.yy0 * 16.0) / 256.0;
            v00 = (texY + 16 - tt.xx0 * 16.0) / 256.0;
            u11 = (texX + tt.yy1 * 16.0) / 256.0;
            v11 = (texY + 16 - tt.xx1 * 16.0) / 256.0;

            u01 = u11;
            u10 = u00;
            v01 = v00;
            v10 = v11;
            u01 = u00;
            u10 = u11;
            v00 = v11;
            v11 = v01;
        }
        else if (this.northFlip == FLIP_CW) {
            // reshape
            u00 = (texX + 16 - tt.yy1 * 16.0) / 256.0;
            v00 = (texY + tt.xx1 * 16.0) / 256.0;
            u11 = (texX + 16 - tt.yy0 * 16.0) / 256.0;
            v11 = (texY + tt.xx0 * 16.0) / 256.0;

            // rotate
            u01 = u11;
            u10 = u00;
            v01 = v00;
            v10 = v11;
            u00 = u01;
            u11 = u10;
            v01 = v11;
            v10 = v00;
        }
        else if (this.northFlip == FLIP_180) {
            u00 = (texX + 16 - tt.xx0 * 16.0) / 256.0;
            u11 = (texX + 16 - tt.xx1 * 16.0 - 0.01) / 256.0;
            v00 = (texY + tt.yy1 * 16.0) / 256.0;
            v11 = (texY + tt.yy0 * 16.0 - 0.01) / 256.0;

            u01 = u11;
            u10 = u00;
            v01 = v00;
            v10 = v11;
        }

        final double x0 = x + tt.xx0;
        final double x1 = x + tt.xx1;
        final double y0 = y + tt.yy0;
        final double y1 = y + tt.yy1;
        final double z0 = z + tt.zz0;

        if (this.applyAmbienceOcclusion) {
            t.color(this.c1r, this.c1g, this.c1b);
            t.vertexUV(x0, y1, z0, u01, v01);
            t.color(this.c2r, this.c2g, this.c2b);
            t.vertexUV(x1, y1, z0, u00, v00);
            t.color(this.c3r, this.c3g, this.c3b);
            t.vertexUV(x1, y0, z0, u10, v10);
            t.color(this.c4r, this.c4g, this.c4b);
            t.vertexUV(x0, y0, z0, u11, v11);
        }
        else {
            t.vertexUV(x0, y1, z0, u01, v01);
            t.vertexUV(x1, y1, z0, u00, v00);
            t.vertexUV(x1, y0, z0, u10, v10);
            t.vertexUV(x0, y0, z0, u11, v11);
        }
    }
    
    public void renderSouth(final Tile tt, final double x, final double y, final double z, int tex) {
        final Tesselator t = Tesselator.instance;

        if (this.fixedTexture >= 0) tex = this.fixedTexture;
        final int texX = (tex & 0xF) << 4;
        final int texY = tex & 0xF0;
        double u00 = (texX + tt.xx0 * 16.0) / 256.0;
        double u11 = (texX + tt.xx1 * 16.0 - 0.01) / 256.0;
        double v00 = (texY + 16 - tt.yy1 * 16.0) / 256.0;
        double v11 = (texY + 16 - tt.yy0 * 16.0 - 0.01) / 256.0;
        if (this.xFlipTexture) {
            final double tmp = u00;
            u00 = u11;
            u11 = tmp;
        }

        if (tt.xx0 < 0.0 || tt.xx1 > 1.0) {
            u00 = (texX + 0.0f) / 256.0f;
            u11 = (texX + 15.99f) / 256.0f;
        }
        if (tt.yy0 < 0.0 || tt.yy1 > 1.0) {
            v00 = (texY + 0.0f) / 256.0f;
            v11 = (texY + 15.99f) / 256.0f;
        }

        double u01 = u11, u10 = u00, v01 = v00, v10 = v11;
        if (this.southFlip == FLIP_CW) {
            u00 = (texX + tt.yy0 * 16.0) / 256.0;
            v11 = (texY + 16 - tt.xx0 * 16.0) / 256.0;
            u11 = (texX + tt.yy1 * 16.0) / 256.0;
            v00 = (texY + 16 - tt.xx1 * 16.0) / 256.0;

            u01 = u11;
            u10 = u00;
            v01 = v00;
            v10 = v11;
            u01 = u00;
            u10 = u11;
            v00 = v11;
            v11 = v01;
        }
        else if (this.southFlip == FLIP_CCW) {
            // reshape
            u00 = (texX + 16 - tt.yy1 * 16.0) / 256.0;
            v00 = (texY + tt.xx0 * 16.0) / 256.0;
            u11 = (texX + 16 - tt.yy0 * 16.0) / 256.0;
            v11 = (texY + tt.xx1 * 16.0) / 256.0;

            // rotate
            u01 = u11;
            u10 = u00;
            v01 = v00;
            v10 = v11;
            u00 = u01;
            u11 = u10;
            v01 = v11;
            v10 = v00;
        }
        else if (this.southFlip == FLIP_180) {
            u00 = (texX + 16 - tt.xx0 * 16.0) / 256.0;
            u11 = (texX + 16 - tt.xx1 * 16.0 - 0.01) / 256.0;
            v00 = (texY + tt.yy1 * 16.0) / 256.0;
            v11 = (texY + tt.yy0 * 16.0 - 0.01) / 256.0;

            u01 = u11;
            u10 = u00;
            v01 = v00;
            v10 = v11;
        }

        final double x0 = x + tt.xx0;
        final double x1 = x + tt.xx1;
        final double y0 = y + tt.yy0;
        final double y1 = y + tt.yy1;
        final double z1 = z + tt.zz1;

        if (this.applyAmbienceOcclusion) {
            t.color(this.c1r, this.c1g, this.c1b);
            t.vertexUV(x0, y1, z1, u00, v00);
            t.color(this.c2r, this.c2g, this.c2b);
            t.vertexUV(x0, y0, z1, u10, v10);
            t.color(this.c3r, this.c3g, this.c3b);
            t.vertexUV(x1, y0, z1, u11, v11);
            t.color(this.c4r, this.c4g, this.c4b);
            t.vertexUV(x1, y1, z1, u01, v01);
        }
        else {
            t.vertexUV(x0, y1, z1, u00, v00);
            t.vertexUV(x0, y0, z1, u10, v10);
            t.vertexUV(x1, y0, z1, u11, v11);
            t.vertexUV(x1, y1, z1, u01, v01);
        }
    }
    
    public void renderWest(final Tile tt, final double x, final double y, final double z, int tex) {
        final Tesselator t = Tesselator.instance;

        if (this.fixedTexture >= 0) tex = this.fixedTexture;
        final int texX = (tex & 0xF) << 4;
        final int texY = tex & 0xF0;
        double u00 = (texX + tt.zz0 * 16.0) / 256.0;
        double u11 = (texX + tt.zz1 * 16.0 - 0.01) / 256.0;
        double v00 = (texY + 16 - tt.yy1 * 16.0) / 256.0;
        double v11 = (texY + 16 - tt.yy0 * 16.0 - 0.01) / 256.0;
        if (this.xFlipTexture) {
            final double tmp = u00;
            u00 = u11;
            u11 = tmp;
        }

        if (tt.zz0 < 0.0 || tt.zz1 > 1.0) {
            u00 = (texX + 0.0f) / 256.0f;
            u11 = (texX + 15.99f) / 256.0f;
        }
        if (tt.yy0 < 0.0 || tt.yy1 > 1.0) {
            v00 = (texY + 0.0f) / 256.0f;
            v11 = (texY + 15.99f) / 256.0f;
        }

        double u01 = u11, u10 = u00, v01 = v00, v10 = v11;
        if (this.westFlip == FLIP_CW) {
            u00 = (texX + tt.yy0 * 16.0) / 256.0;
            v00 = (texY + 16 - tt.zz1 * 16.0) / 256.0;
            u11 = (texX + tt.yy1 * 16.0) / 256.0;
            v11 = (texY + 16 - tt.zz0 * 16.0) / 256.0;

            u01 = u11;
            u10 = u00;
            v01 = v00;
            v10 = v11;
            u01 = u00;
            u10 = u11;
            v00 = v11;
            v11 = v01;
        }
        else if (this.westFlip == 2) {
            // reshape
            u00 = (texX + 16 - tt.yy1 * 16.0) / 256.0;
            v00 = (texY + tt.zz0 * 16.0) / 256.0;
            u11 = (texX + 16 - tt.yy0 * 16.0) / 256.0;
            v11 = (texY + tt.zz1 * 16.0) / 256.0;

            // rotate
            u01 = u11;
            u10 = u00;
            v01 = v00;
            v10 = v11;
            u00 = u01;
            u11 = u10;
            v01 = v11;
            v10 = v00;
        }
        else if (this.westFlip == 3) {
            u00 = (texX + 16 - tt.zz0 * 16.0) / 256.0;
            u11 = (texX + 16 - tt.zz1 * 16.0 - 0.01) / 256.0;
            v00 = (texY + tt.yy1 * 16.0) / 256.0;
            v11 = (texY + tt.yy0 * 16.0 - 0.01) / 256.0;

            u01 = u11;
            u10 = u00;
            v01 = v00;
            v10 = v11;
        }

        final double x0 = x + tt.xx0;
        final double y0 = y + tt.yy0;
        final double y1 = y + tt.yy1;
        final double z0 = z + tt.zz0;
        final double z1 = z + tt.zz1;

        if (this.applyAmbienceOcclusion) {
            t.color(this.c1r, this.c1g, this.c1b);
            t.vertexUV(x0, y1, z1, u01, v01);
            t.color(this.c2r, this.c2g, this.c2b);
            t.vertexUV(x0, y1, z0, u00, v00);
            t.color(this.c3r, this.c3g, this.c3b);
            t.vertexUV(x0, y0, z0, u10, v10);
            t.color(this.c4r, this.c4g, this.c4b);
            t.vertexUV(x0, y0, z1, u11, v11);
        }
        else {
            t.vertexUV(x0, y1, z1, u01, v01);
            t.vertexUV(x0, y1, z0, u00, v00);
            t.vertexUV(x0, y0, z0, u10, v10);
            t.vertexUV(x0, y0, z1, u11, v11);
        }
    }
    
    public void renderEast(final Tile tt, final double x, final double y, final double z, int tex) {
        final Tesselator t = Tesselator.instance;

        if (this.fixedTexture >= 0) tex = this.fixedTexture;
        final int texX = (tex & 0xF) << 4;
        final int texY = tex & 0xF0;
        double u00 = (texX + tt.zz0 * 16.0) / 256.0;
        double u11 = (texX + tt.zz1 * 16.0 - 0.01) / 256.0;
        double v00 = (texY + 16 - tt.yy1 * 16.0) / 256.0;
        double v11 = (texY + 16 - tt.yy0 * 16.0 - 0.01) / 256.0;
        if (this.xFlipTexture) {
            final double tmp = u00;
            u00 = u11;
            u11 = tmp;
        }

        if (tt.zz0 < 0.0 || tt.zz1 > 1.0) {
            u00 = (texX + 0.0f) / 256.0f;
            u11 = (texX + 15.99f) / 256.0f;
        }
        if (tt.yy0 < 0.0 || tt.yy1 > 1.0) {
            v00 = (texY + 0.0f) / 256.0f;
            v11 = (texY + 15.99f) / 256.0f;
        }

        double u01 = u11, u10 = u00, v01 = v00, v10 = v11;
        if (this.eastFlip == FLIP_CCW) {
            u00 = (texX + tt.yy0 * 16.0) / 256.0;
            v00 = (texY + 16 - tt.zz0 * 16.0) / 256.0;
            u11 = (texX + tt.yy1 * 16.0) / 256.0;
            v11 = (texY + 16 - tt.zz1 * 16.0) / 256.0;

            u01 = u11;
            u10 = u00;
            v01 = v00;
            v10 = v11;
            u01 = u00;
            u10 = u11;
            v00 = v11;
            v11 = v01;
        }
        else if (this.eastFlip == FLIP_CW) {
            // reshape
            u00 = (texX + 16 - tt.yy1 * 16.0) / 256.0;
            v00 = (texY + tt.zz1 * 16.0) / 256.0;
            u11 = (texX + 16 - tt.yy0 * 16.0) / 256.0;
            v11 = (texY + tt.zz0 * 16.0) / 256.0;

            // rotate
            u01 = u11;
            u10 = u00;
            v01 = v00;
            v10 = v11;
            u00 = u01;
            u11 = u10;
            v01 = v11;
            v10 = v00;
        }
        else if (this.eastFlip == FLIP_180) {
            u00 = (texX + 16 - tt.zz0 * 16.0) / 256.0;
            u11 = (texX + 16 - tt.zz1 * 16.0 - 0.01) / 256.0;
            v00 = (texY + tt.yy1 * 16.0) / 256.0;
            v11 = (texY + tt.yy0 * 16.0 - 0.01) / 256.0;

            u01 = u11;
            u10 = u00;
            v01 = v00;
            v10 = v11;
        }

        final double x1 = x + tt.xx1;
        final double y0 = y + tt.yy0;
        final double y1 = y + tt.yy1;
        final double z0 = z + tt.zz0;
        final double z1 = z + tt.zz1;

        if (this.applyAmbienceOcclusion) {
            t.color(this.c1r, this.c1g, this.c1b);
            t.vertexUV(x1, y0, z1, u10, v10);
            t.color(this.c2r, this.c2g, this.c2b);
            t.vertexUV(x1, y0, z0, u11, v11);
            t.color(this.c3r, this.c3g, this.c3b);
            t.vertexUV(x1, y1, z0, u01, v01);
            t.color(this.c4r, this.c4g, this.c4b);
            t.vertexUV(x1, y1, z1, u00, v00);
        }
        else {
            t.vertexUV(x1, y0, z1, u10, v10);
            t.vertexUV(x1, y0, z0, u11, v11);
            t.vertexUV(x1, y1, z0, u01, v01);
            t.vertexUV(x1, y1, z1, u00, v00);
        }
    }
    
    public void renderTile(final Tile tile, int data, final float brightness) {
        final Tesselator t = Tesselator.instance;

        if (this.setColor) {
            final int col = tile.getColor(data);
            float red = (col >> 16 & 0xFF) / 255.0f * brightness;
            float g = (col >> 8 & 0xFF) / 255.0f * brightness;
            float b = (col & 0xFF) / 255.0f * brightness;
            glColor4f(red, g, b, 1.0f);
        }

        final int shape = tile.getRenderShape();
        if (shape == Tile.SHAPE_BLOCK || shape == Tile.SHAPE_PISTON_BASE) {
            if (shape == Tile.SHAPE_PISTON_BASE) {
                data = Facing.UP;
            }

            tile.updateDefaultShape();
            glTranslatef(-0.5f, -0.5f, -0.5f);

            t.begin();
            t.normal(0.0f, -1.0f, 0.0f);
            this.renderFaceDown(tile, 0.0, 0.0, 0.0, tile.getTexture(Facing.DOWN, data));
            t.end();

            t.begin();
            t.normal(0.0f, 1.0f, 0.0f);
            this.renderFaceUp(tile, 0.0, 0.0, 0.0, tile.getTexture(Facing.UP, data));
            t.end();

            t.begin();
            t.normal(0.0f, 0.0f, -1.0f);
            this.renderNorth(tile, 0.0, 0.0, 0.0, tile.getTexture(Facing.NORTH, data));
            t.end();

            t.begin();
            t.normal(0.0f, 0.0f, 1.0f);
            this.renderSouth(tile, 0.0, 0.0, 0.0, tile.getTexture(Facing.SOUTH, data));
            t.end();

            t.begin();
            t.normal(-1.0f, 0.0f, 0.0f);
            this.renderWest(tile, 0.0, 0.0, 0.0, tile.getTexture(Facing.WEST, data));
            t.end();

            t.begin();
            t.normal(1.0f, 0.0f, 0.0f);
            this.renderEast(tile, 0.0, 0.0, 0.0, tile.getTexture(Facing.EAST, data));
            t.end();

            glTranslatef(0.5f, 0.5f, 0.5f);
        }
        else if (shape == Tile.SHAPE_CROSS_TEXTURE) {
            t.begin();
            t.normal(0.0f, -1.0f, 0.0f);
            this.tesselateCrossTexture(tile, data, -0.5, -0.5, -0.5);
            t.end();
        }
        else if (shape == Tile.SHAPE_CACTUS) {
            tile.updateDefaultShape();
            glTranslatef(-0.5f, -0.5f, -0.5f);
            final float s = 1 / 16.0f;
            t.begin();
            t.normal(0.0f, -1.0f, 0.0f);
            this.renderFaceDown(tile, 0.0, 0.0, 0.0, tile.getTexture(Facing.DOWN));
            t.end();

            t.begin();
            t.normal(0.0f, 1.0f, 0.0f);
            this.renderFaceUp(tile, 0.0, 0.0, 0.0, tile.getTexture(Facing.UP));
            t.end();

            t.begin();
            t.normal(0.0f, 0.0f, -1.0f);
            t.addOffset(0.0f, 0.0f, s);
            this.renderNorth(tile, 0.0, 0.0, 0.0, tile.getTexture(Facing.NORTH));
            t.addOffset(0.0f, 0.0f, -s);
            t.end();

            t.begin();
            t.normal(0.0f, 0.0f, 1.0f);
            t.addOffset(0.0f, 0.0f, -s);
            this.renderSouth(tile, 0.0, 0.0, 0.0, tile.getTexture(Facing.SOUTH));
            t.addOffset(0.0f, 0.0f, s);
            t.end();

            t.begin();
            t.normal(-1.0f, 0.0f, 0.0f);
            t.addOffset(s, 0.0f, 0.0f);
            this.renderWest(tile, 0.0, 0.0, 0.0, tile.getTexture(Facing.WEST));
            t.addOffset(-s, 0.0f, 0.0f);
            t.end();

            t.begin();
            t.normal(1.0f, 0.0f, 0.0f);
            t.addOffset(-s, 0.0f, 0.0f);
            this.renderEast(tile, 0.0, 0.0, 0.0, tile.getTexture(Facing.EAST));
            t.addOffset(s, 0.0f, 0.0f);
            t.end();

            glTranslatef(0.5f, 0.5f, 0.5f);
        }
        else if (shape == Tile.SHAPE_ROWS) {
            t.begin();
            t.normal(0.0f, -1.0f, 0.0f);
            this.tesselateRowTexture(tile, data, -0.5, -0.5, -0.5);
            t.end();
        }
        else if (shape == Tile.SHAPE_TORCH) {
            t.begin();
            t.normal(0.0f, -1.0f, 0.0f);
            this.tesselateTorch(tile, -0.5, -0.5, -0.5, 0.0, 0.0);
            t.end();
        }
        else if (shape == Tile.SHAPE_STAIRS) {
            for (int i = 0; i < 2; ++i) {
                if (i == 0) tile.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.5f);
                if (i == 1) tile.setShape(0.0f, 0.0f, 0.5f, 1.0f, 0.5f, 1.0f);

                glTranslatef(-0.5f, -0.5f, -0.5f);
                t.begin();
                t.normal(0.0f, -1.0f, 0.0f);
                this.renderFaceDown(tile, 0.0, 0.0, 0.0, tile.getTexture(Facing.DOWN));
                t.end();

                t.begin();
                t.normal(0.0f, 1.0f, 0.0f);
                this.renderFaceUp(tile, 0.0, 0.0, 0.0, tile.getTexture(Facing.UP));
                t.end();

                t.begin();
                t.normal(0.0f, 0.0f, -1.0f);
                this.renderNorth(tile, 0.0, 0.0, 0.0, tile.getTexture(Facing.NORTH));
                t.end();

                t.begin();
                t.normal(0.0f, 0.0f, 1.0f);
                this.renderSouth(tile, 0.0, 0.0, 0.0, tile.getTexture(Facing.SOUTH));
                t.end();

                t.begin();
                t.normal(-1.0f, 0.0f, 0.0f);
                this.renderWest(tile, 0.0, 0.0, 0.0, tile.getTexture(Facing.WEST));
                t.end();

                t.begin();
                t.normal(1.0f, 0.0f, 0.0f);
                this.renderEast(tile, 0.0, 0.0, 0.0, tile.getTexture(Facing.EAST));
                t.end();

                glTranslatef(0.5f, 0.5f, 0.5f);
            }
        }
        else if (shape == Tile.SHAPE_FENCE) {
            for (int i = 0; i < 4; ++i) {
                float w = 2 / 16.0f;
                if (i == 0) tile.setShape(0.5f - w, 0.0f, 0.0f, 0.5f + w, 1.0f, w * 2.0f);
                if (i == 1) tile.setShape(0.5f - w, 0.0f, 1.0f - w * 2.0f, 0.5f + w, 1.0f, 1.0f);
                w = 1 / 16.0f;
                if (i == 2) tile.setShape(0.5f - w, 1.0f - w * 3.0f, -w * 2.0f, 0.5f + w, 1.0f - w, 1.0f + w * 2.0f);
                if (i == 3) tile.setShape(0.5f - w, 0.5f - w * 3.0f, -w * 2.0f, 0.5f + w, 0.5f - w, 1.0f + w * 2.0f);

                glTranslatef(-0.5f, -0.5f, -0.5f);
                t.begin();
                t.normal(0.0f, -1.0f, 0.0f);
                this.renderFaceDown(tile, 0.0, 0.0, 0.0, tile.getTexture(Facing.DOWN));
                t.end();

                t.begin();
                t.normal(0.0f, 1.0f, 0.0f);
                this.renderFaceUp(tile, 0.0, 0.0, 0.0, tile.getTexture(Facing.UP));
                t.end();

                t.begin();
                t.normal(0.0f, 0.0f, -1.0f);
                this.renderNorth(tile, 0.0, 0.0, 0.0, tile.getTexture(Facing.NORTH));
                t.end();

                t.begin();
                t.normal(0.0f, 0.0f, 1.0f);
                this.renderSouth(tile, 0.0, 0.0, 0.0, tile.getTexture(Facing.SOUTH));
                t.end();

                t.begin();
                t.normal(-1.0f, 0.0f, 0.0f);
                this.renderWest(tile, 0.0, 0.0, 0.0, tile.getTexture(Facing.WEST));
                t.end();

                t.begin();
                t.normal(1.0f, 0.0f, 0.0f);
                this.renderEast(tile, 0.0, 0.0, 0.0, tile.getTexture(Facing.EAST));
                t.end();

                glTranslatef(0.5f, 0.5f, 0.5f);
            }
            tile.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        }
    }
    
    public static boolean canRender(final int renderShape) {
        if (renderShape == Tile.SHAPE_BLOCK) return true;
        if (renderShape == Tile.SHAPE_CACTUS) return true;
        if (renderShape == Tile.SHAPE_STAIRS) return true;
        if (renderShape == Tile.SHAPE_FENCE) return true;
        if (renderShape == Tile.SHAPE_PISTON_BASE) return true;
        return false;
    }
}
