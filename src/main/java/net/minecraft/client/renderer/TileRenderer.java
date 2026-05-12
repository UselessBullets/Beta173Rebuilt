// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer;

import org.lwjgl.opengl.GL11;
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

public class TileRenderer
{
    private LevelSource level;
    private int fixedTexture;
    private boolean xFlipTexture;
    private boolean noCulling;
    public static boolean fancy;
    public boolean setColor;
    private int northFlip;
    private int southFlip;
    private int eastFlip;
    private int westFlip;
    private int upFlip;
    private int downFlip;
    private boolean applyAmbienceOcclusion;
    private float ll000;
    private float llx00;
    private float ll0y0;
    private float ll00z;
    private float llX00;
    private float ll0Y0;
    private float ll00Z;
    private float llxyz;
    private float llxy0;
    private float llxyZ;
    private float ll0yz;
    private float ll0yZ;
    private float llXyz;
    private float llXy0;
    private float llXyZ;
    private float llxYz;
    private float llxY0;
    private float llxYZ;
    private float ll0Yz;
    private float llXYz;
    private float llXY0;
    private float ll0YZ;
    private float llXYZ;
    private float llx0z;
    private float llX0z;
    private float llx0Z;
    private float llX0Z;
    private int blsmooth;
    private float c1r;
    private float c2r;
    private float c3r;
    private float c4r;
    private float c1g;
    private float c2g;
    private float c3g;
    private float c4g;
    private float c1b;
    private float c2b;
    private float c3b;
    private float c4b;
    private boolean llTrans0Yz;
    private boolean llTransXY0;
    private boolean llTransxY0;
    private boolean llTrans0YZ;
    private boolean llTransx0z;
    private boolean llTransX0Z;
    private boolean llTransx0Z;
    private boolean llTransX0z;
    private boolean llTrans0yz;
    private boolean llTransXy0;
    private boolean llTransxy0;
    private boolean llTrans0yZ;
    
    public TileRenderer(final LevelSource level) {
        this.fixedTexture = -1;
        this.xFlipTexture = false;
        this.noCulling = false;
        this.setColor = true;
        this.northFlip = 0;
        this.southFlip = 0;
        this.eastFlip = 0;
        this.westFlip = 0;
        this.upFlip = 0;
        this.downFlip = 0;
        this.blsmooth = 1;
        this.level = level;
    }
    
    public TileRenderer() {
        this.fixedTexture = -1;
        this.xFlipTexture = false;
        this.noCulling = false;
        this.setColor = true;
        this.northFlip = 0;
        this.southFlip = 0;
        this.eastFlip = 0;
        this.westFlip = 0;
        this.upFlip = 0;
        this.downFlip = 0;
        this.blsmooth = 1;
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
        final int renderShape = tt.getRenderShape();
        tt.updateShape(this.level, x, y, z);
        if (renderShape == 0) {
            return this.tesselateBlockInWorld(tt, x, y, z);
        }
        if (renderShape == 4) {
            return this.tesselateWaterInWorld(tt, x, y, z);
        }
        if (renderShape == 13) {
            return this.tesselateCactusInWorld(tt, x, y, z);
        }
        if (renderShape == 1) {
            return this.tesselateCrossInWorld(tt, x, y, z);
        }
        if (renderShape == 6) {
            return this.tesselateRowInWorld(tt, x, y, z);
        }
        if (renderShape == 2) {
            return this.tesselateTorchInWorld(tt, x, y, z);
        }
        if (renderShape == 3) {
            return this.tesselateFireInWorld(tt, x, y, z);
        }
        if (renderShape == 5) {
            return this.tesselateDustInWorld(tt, x, y, z);
        }
        if (renderShape == 8) {
            return this.tesselateLadderInWorld(tt, x, y, z);
        }
        if (renderShape == 7) {
            return this.tesselateDoorInWorld(tt, x, y, z);
        }
        if (renderShape == 9) {
            return this.tesselateRailInWorld((RailTile)tt, x, y, z);
        }
        if (renderShape == 10) {
            return this.tesselateStairsInWorld(tt, x, y, z);
        }
        if (renderShape == 11) {
            return this.tesselateFenceInWorld(tt, x, y, z);
        }
        if (renderShape == 12) {
            return this.tesselateLeverInWorld(tt, x, y, z);
        }
        if (renderShape == 14) {
            return this.tesselateBedInWorld(tt, x, y, z);
        }
        if (renderShape == 15) {
            return this.tesselateDiodeInWorld(tt, x, y, z);
        }
        if (renderShape == 16) {
            return this.tesselatePistonBaseInWorld(tt, x, y, z, false);
        }
        return renderShape == 17 && this.tesselatePistonExtensionInWorld(tt, x, y, z, true);
    }
    
    private boolean tesselateBedInWorld(final Tile tt, final int x, final int y, final int z) {
        final Tesselator instance = Tesselator.instance;
        final int data = this.level.getData(x, y, z);
        final int direction = BedTile.getDirection(data);
        final boolean headPiece = BedTile.isHeadPiece(data);
        final float n = 0.5f;
        final float n2 = 1.0f;
        final float n3 = 0.8f;
        final float n4 = 0.6f;
        final float n5 = n2;
        final float n6 = n2;
        final float n7 = n2;
        final float n8 = n;
        final float n9 = n3;
        final float n10 = n4;
        final float n11 = n;
        final float n12 = n3;
        final float n13 = n4;
        final float n14 = n;
        final float n15 = n3;
        final float n16 = n4;
        final float brightness = tt.getBrightness(this.level, x, y, z);
        instance.color(n8 * brightness, n11 * brightness, n14 * brightness);
        final int texture = tt.getTexture(this.level, x, y, z, 0);
        final int n17 = (texture & 0xF) << 4;
        final int n18 = texture & 0xF0;
        final double n19 = n17 / 256.0f;
        final double n20 = (n17 + 16 - 0.01) / 256.0;
        final double n21 = n18 / 256.0f;
        final double n22 = (n18 + 16 - 0.01) / 256.0;
        final double n23 = x + tt.xx0;
        final double n24 = x + tt.xx1;
        final double n25 = y + tt.yy0 + 0.1875;
        final double n26 = z + tt.zz0;
        final double n27 = z + tt.zz1;
        instance.vertexUV(n23, n25, n27, n19, n22);
        instance.vertexUV(n23, n25, n26, n19, n21);
        instance.vertexUV(n24, n25, n26, n20, n21);
        instance.vertexUV(n24, n25, n27, n20, n22);
        final float brightness2 = tt.getBrightness(this.level, x, y + 1, z);
        instance.color(n5 * brightness2, n6 * brightness2, n7 * brightness2);
        final int texture2 = tt.getTexture(this.level, x, y, z, 1);
        final int n28 = (texture2 & 0xF) << 4;
        final int n29 = texture2 & 0xF0;
        final double n30 = n28 / 256.0f;
        final double n31 = (n28 + 16 - 0.01) / 256.0;
        final double n32 = n29 / 256.0f;
        final double n33 = (n29 + 16 - 0.01) / 256.0;
        double u = n30;
        double u2 = n31;
        double v = n32;
        double v2 = n32;
        double u3 = n30;
        double u4 = n31;
        double v3 = n33;
        double v4 = n33;
        if (direction == 0) {
            u2 = n30;
            v = n33;
            u3 = n31;
            v4 = n32;
        }
        else if (direction == 2) {
            u = n31;
            v2 = n33;
            u4 = n30;
            v3 = n32;
        }
        else if (direction == 3) {
            u = n31;
            v2 = n33;
            u4 = n30;
            v3 = n32;
            u2 = n30;
            v = n33;
            u3 = n31;
            v4 = n32;
        }
        final double n34 = x + tt.xx0;
        final double n35 = x + tt.xx1;
        final double n36 = y + tt.yy1;
        final double n37 = z + tt.zz0;
        final double n38 = z + tt.zz1;
        instance.vertexUV(n35, n36, n38, u3, v3);
        instance.vertexUV(n35, n36, n37, u, v);
        instance.vertexUV(n34, n36, n37, u2, v2);
        instance.vertexUV(n34, n36, n38, u4, v4);
        int n39 = Direction.DIRECTION_FACING[direction];
        if (headPiece) {
            n39 = Direction.DIRECTION_FACING[Direction.DIRECTION_OPPOSITE[direction]];
        }
        int n40 = 4;
        switch (direction) {
            case 0: {
                n40 = 5;
                break;
            }
            case 3: {
                n40 = 2;
                break;
            }
            case 1: {
                n40 = 3;
                break;
            }
        }
        if (n39 != 2 && (this.noCulling || tt.isFaceVisible(this.level, x, y, z - 1, 2))) {
            float brightness3 = tt.getBrightness(this.level, x, y, z - 1);
            if (tt.zz0 > 0.0) {
                brightness3 = brightness;
            }
            instance.color(n9 * brightness3, n12 * brightness3, n15 * brightness3);
            this.xFlipTexture = (n40 == 2);
            this.renderNorth(tt, x, y, z, tt.getTexture(this.level, x, y, z, 2));
        }
        if (n39 != 3 && (this.noCulling || tt.isFaceVisible(this.level, x, y, z + 1, 3))) {
            float brightness4 = tt.getBrightness(this.level, x, y, z + 1);
            if (tt.zz1 < 1.0) {
                brightness4 = brightness;
            }
            instance.color(n9 * brightness4, n12 * brightness4, n15 * brightness4);
            this.xFlipTexture = (n40 == 3);
            this.renderSouth(tt, x, y, z, tt.getTexture(this.level, x, y, z, 3));
        }
        if (n39 != 4 && (this.noCulling || tt.isFaceVisible(this.level, x - 1, y, z, 4))) {
            float brightness5 = tt.getBrightness(this.level, x - 1, y, z);
            if (tt.xx0 > 0.0) {
                brightness5 = brightness;
            }
            instance.color(n10 * brightness5, n13 * brightness5, n16 * brightness5);
            this.xFlipTexture = (n40 == 4);
            this.renderWest(tt, x, y, z, tt.getTexture(this.level, x, y, z, 4));
        }
        if (n39 != 5 && (this.noCulling || tt.isFaceVisible(this.level, x + 1, y, z, 5))) {
            float brightness6 = tt.getBrightness(this.level, x + 1, y, z);
            if (tt.xx1 < 1.0) {
                brightness6 = brightness;
            }
            instance.color(n10 * brightness6, n13 * brightness6, n16 * brightness6);
            this.xFlipTexture = (n40 == 5);
            this.renderEast(tt, x, y, z, tt.getTexture(this.level, x, y, z, 5));
        }
        this.xFlipTexture = false;
        return true;
    }
    
    public boolean tesselateTorchInWorld(final Tile tt, final int x, final int y, final int z) {
        final int data = this.level.getData(x, y, z);
        final Tesselator instance = Tesselator.instance;
        float brightness = tt.getBrightness(this.level, x, y, z);
        if (Tile.lightEmission[tt.id] > 0) {
            brightness = 1.0f;
        }
        instance.color(brightness, brightness, brightness);
        final double n = 0.4000000059604645;
        final double n2 = 0.5 - n;
        final double n3 = 0.20000000298023224;
        if (data == 1) {
            this.tesselateTorch(tt, x - n2, y + n3, z, -n, 0.0);
        }
        else if (data == 2) {
            this.tesselateTorch(tt, x + n2, y + n3, z, n, 0.0);
        }
        else if (data == 3) {
            this.tesselateTorch(tt, x, y + n3, z - n2, 0.0, -n);
        }
        else if (data == 4) {
            this.tesselateTorch(tt, x, y + n3, z + n2, 0.0, n);
        }
        else {
            this.tesselateTorch(tt, x, y, z, 0.0, 0.0);
        }
        return true;
    }
    
    private boolean tesselateDiodeInWorld(final Tile tt, final int x, final int y, final int z) {
        final int data = this.level.getData(x, y, z);
        final int n = data & 0x3;
        final int n2 = (data & 0xC) >> 2;
        this.tesselateBlockInWorld(tt, x, y, z);
        final Tesselator instance = Tesselator.instance;
        float brightness = tt.getBrightness(this.level, x, y, z);
        if (Tile.lightEmission[tt.id] > 0) {
            brightness = (brightness + 1.0f) * 0.5f;
        }
        instance.color(brightness, brightness, brightness);
        final double n3 = -0.1875;
        double n4 = 0.0;
        double n5 = 0.0;
        double n6 = 0.0;
        double n7 = 0.0;
        switch (n) {
            case 0: {
                n7 = -0.3125;
                n5 = DiodeTile.DELAY_RENDER_OFFSETS[n2];
                break;
            }
            case 2: {
                n7 = 0.3125;
                n5 = -DiodeTile.DELAY_RENDER_OFFSETS[n2];
                break;
            }
            case 3: {
                n6 = -0.3125;
                n4 = DiodeTile.DELAY_RENDER_OFFSETS[n2];
                break;
            }
            case 1: {
                n6 = 0.3125;
                n4 = -DiodeTile.DELAY_RENDER_OFFSETS[n2];
                break;
            }
        }
        this.tesselateTorch(tt, x + n4, y + n3, z + n5, 0.0, 0.0);
        this.tesselateTorch(tt, x + n6, y + n3, z + n7, 0.0, 0.0);
        final int texture = tt.getTexture(1);
        final int n8 = (texture & 0xF) << 4;
        final int n9 = texture & 0xF0;
        final double n10 = n8 / 256.0f;
        final double n11 = (n8 + 15.99f) / 256.0f;
        final double n12 = n9 / 256.0f;
        final double n13 = (n9 + 15.99f) / 256.0f;
        final float n14 = 0.125f;
        float n15 = (float)(x + 1);
        float n16 = (float)(x + 1);
        float n17 = (float)(x + 0);
        float n18 = (float)(x + 0);
        float n19 = (float)(z + 0);
        float n20 = (float)(z + 1);
        float n21 = (float)(z + 1);
        float n22 = (float)(z + 0);
        final float n23 = y + n14;
        if (n == 2) {
            n16 = (n15 = (float)(x + 0));
            n18 = (n17 = (float)(x + 1));
            n22 = (n19 = (float)(z + 1));
            n21 = (n20 = (float)(z + 0));
        }
        else if (n == 3) {
            n18 = (n15 = (float)(x + 0));
            n17 = (n16 = (float)(x + 1));
            n20 = (n19 = (float)(z + 0));
            n22 = (n21 = (float)(z + 1));
        }
        else if (n == 1) {
            n18 = (n15 = (float)(x + 1));
            n17 = (n16 = (float)(x + 0));
            n20 = (n19 = (float)(z + 1));
            n22 = (n21 = (float)(z + 0));
        }
        instance.vertexUV(n18, n23, n22, n10, n12);
        instance.vertexUV(n17, n23, n21, n10, n13);
        instance.vertexUV(n16, n23, n20, n11, n13);
        instance.vertexUV(n15, n23, n19, n11, n12);
        return true;
    }
    
    public void tesselatePistonBaseForceExtended(final Tile tile, final int x, final int y, final int z) {
        this.tesselatePistonBaseInWorld(tile, x, y, z, this.noCulling = true);
        this.noCulling = false;
    }
    
    private boolean tesselatePistonBaseInWorld(final Tile tt, final int x, final int y, final int z, final boolean forceExtended) {
        final int data = this.level.getData(x, y, z);
        final boolean b = forceExtended || (data & 0x8) != 0x0;
        final int facing = PistonBaseTile.getFacing(data);
        if (b) {
            switch (facing) {
                case 0: {
                    this.northFlip = 3;
                    this.southFlip = 3;
                    this.eastFlip = 3;
                    this.westFlip = 3;
                    tt.setShape(0.0f, 0.25f, 0.0f, 1.0f, 1.0f, 1.0f);
                    break;
                }
                case 1: {
                    tt.setShape(0.0f, 0.0f, 0.0f, 1.0f, 0.75f, 1.0f);
                    break;
                }
                case 2: {
                    this.eastFlip = 1;
                    this.westFlip = 2;
                    tt.setShape(0.0f, 0.0f, 0.25f, 1.0f, 1.0f, 1.0f);
                    break;
                }
                case 3: {
                    this.eastFlip = 2;
                    this.westFlip = 1;
                    this.upFlip = 3;
                    this.downFlip = 3;
                    tt.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.75f);
                    break;
                }
                case 4: {
                    this.northFlip = 1;
                    this.southFlip = 2;
                    this.upFlip = 2;
                    this.downFlip = 1;
                    tt.setShape(0.25f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
                    break;
                }
                case 5: {
                    this.northFlip = 2;
                    this.southFlip = 1;
                    this.upFlip = 1;
                    this.downFlip = 2;
                    tt.setShape(0.0f, 0.0f, 0.0f, 0.75f, 1.0f, 1.0f);
                    break;
                }
            }
            this.tesselateBlockInWorld(tt, x, y, z);
            this.northFlip = 0;
            this.southFlip = 0;
            this.eastFlip = 0;
            this.westFlip = 0;
            this.upFlip = 0;
            this.downFlip = 0;
            tt.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        }
        else {
            switch (facing) {
                case 0: {
                    this.northFlip = 3;
                    this.southFlip = 3;
                    this.eastFlip = 3;
                    this.westFlip = 3;
                }
                case 2: {
                    this.eastFlip = 1;
                    this.westFlip = 2;
                    break;
                }
                case 3: {
                    this.eastFlip = 2;
                    this.westFlip = 1;
                    this.upFlip = 3;
                    this.downFlip = 3;
                    break;
                }
                case 4: {
                    this.northFlip = 1;
                    this.southFlip = 2;
                    this.upFlip = 2;
                    this.downFlip = 1;
                    break;
                }
                case 5: {
                    this.northFlip = 2;
                    this.southFlip = 1;
                    this.upFlip = 1;
                    this.downFlip = 2;
                    break;
                }
            }
            this.tesselateBlockInWorld(tt, x, y, z);
            this.northFlip = 0;
            this.southFlip = 0;
            this.eastFlip = 0;
            this.westFlip = 0;
            this.upFlip = 0;
            this.downFlip = 0;
        }
        return true;
    }
    
    private void renderPistonArmUpDown(final double x0, final double x1, final double y0, final double y1, final double z0, final double z1, final float br, final double armLengthPixels) {
        int fixedTexture = 108;
        if (this.fixedTexture >= 0) {
            fixedTexture = this.fixedTexture;
        }
        final int n = (fixedTexture & 0xF) << 4;
        final int n2 = fixedTexture & 0xF0;
        final Tesselator instance = Tesselator.instance;
        final double n3 = (n + 0) / 256.0f;
        final double n4 = (n2 + 0) / 256.0f;
        final double n5 = (n + armLengthPixels - 0.01) / 256.0;
        final double n6 = (n2 + 4.0f - 0.01) / 256.0;
        instance.color(br, br, br);
        instance.vertexUV(x0, y1, z0, n5, n4);
        instance.vertexUV(x0, y0, z0, n3, n4);
        instance.vertexUV(x1, y0, z1, n3, n6);
        instance.vertexUV(x1, y1, z1, n5, n6);
    }
    
    private void renderPistonArmNorthSouth(final double x0, final double x1, final double y0, final double y1, final double z0, final double z1, final float br, final double armLengthPixels) {
        int fixedTexture = 108;
        if (this.fixedTexture >= 0) {
            fixedTexture = this.fixedTexture;
        }
        final int n = (fixedTexture & 0xF) << 4;
        final int n2 = fixedTexture & 0xF0;
        final Tesselator instance = Tesselator.instance;
        final double n3 = (n + 0) / 256.0f;
        final double n4 = (n2 + 0) / 256.0f;
        final double n5 = (n + armLengthPixels - 0.01) / 256.0;
        final double n6 = (n2 + 4.0f - 0.01) / 256.0;
        instance.color(br, br, br);
        instance.vertexUV(x0, y0, z1, n5, n4);
        instance.vertexUV(x0, y0, z0, n3, n4);
        instance.vertexUV(x1, y1, z0, n3, n6);
        instance.vertexUV(x1, y1, z1, n5, n6);
    }
    
    private void renderPistonArmEastWest(final double x0, final double x1, final double y0, final double y1, final double z0, final double z1, final float br, final double armLengthPixels) {
        int fixedTexture = 108;
        if (this.fixedTexture >= 0) {
            fixedTexture = this.fixedTexture;
        }
        final int n = (fixedTexture & 0xF) << 4;
        final int n2 = fixedTexture & 0xF0;
        final Tesselator instance = Tesselator.instance;
        final double n3 = (n + 0) / 256.0f;
        final double n4 = (n2 + 0) / 256.0f;
        final double n5 = (n + armLengthPixels - 0.01) / 256.0;
        final double n6 = (n2 + 4.0f - 0.01) / 256.0;
        instance.color(br, br, br);
        instance.vertexUV(x1, y0, z0, n5, n4);
        instance.vertexUV(x0, y0, z0, n3, n4);
        instance.vertexUV(x0, y1, z1, n3, n6);
        instance.vertexUV(x1, y1, z1, n5, n6);
    }
    
    public void tesselatePistonArmNoCulling(final Tile tile, final int x, final int y, final int z, final boolean fullArm) {
        this.noCulling = true;
        this.tesselatePistonExtensionInWorld(tile, x, y, z, fullArm);
        this.noCulling = false;
    }
    
    private boolean tesselatePistonExtensionInWorld(final Tile tt, final int x, final int y, final int z, final boolean fullArm) {
        final int facing = PistonExtensionTile.getFacing(this.level.getData(x, y, z));
        final float brightness = tt.getBrightness(this.level, x, y, z);
        final float n = fullArm ? 1.0f : 0.5f;
        final double n2 = fullArm ? 16.0 : 8.0;
        switch (facing) {
            case 0: {
                this.northFlip = 3;
                this.southFlip = 3;
                this.eastFlip = 3;
                this.westFlip = 3;
                tt.setShape(0.0f, 0.0f, 0.0f, 1.0f, 0.25f, 1.0f);
                this.tesselateBlockInWorld(tt, x, y, z);
                this.renderPistonArmUpDown(x + 0.375f, x + 0.625f, y + 0.25f, y + 0.25f + n, z + 0.625f, z + 0.625f, brightness * 0.8f, n2);
                this.renderPistonArmUpDown(x + 0.625f, x + 0.375f, y + 0.25f, y + 0.25f + n, z + 0.375f, z + 0.375f, brightness * 0.8f, n2);
                this.renderPistonArmUpDown(x + 0.375f, x + 0.375f, y + 0.25f, y + 0.25f + n, z + 0.375f, z + 0.625f, brightness * 0.6f, n2);
                this.renderPistonArmUpDown(x + 0.625f, x + 0.625f, y + 0.25f, y + 0.25f + n, z + 0.625f, z + 0.375f, brightness * 0.6f, n2);
                break;
            }
            case 1: {
                tt.setShape(0.0f, 0.75f, 0.0f, 1.0f, 1.0f, 1.0f);
                this.tesselateBlockInWorld(tt, x, y, z);
                this.renderPistonArmUpDown(x + 0.375f, x + 0.625f, y - 0.25f + 1.0f - n, y - 0.25f + 1.0f, z + 0.625f, z + 0.625f, brightness * 0.8f, n2);
                this.renderPistonArmUpDown(x + 0.625f, x + 0.375f, y - 0.25f + 1.0f - n, y - 0.25f + 1.0f, z + 0.375f, z + 0.375f, brightness * 0.8f, n2);
                this.renderPistonArmUpDown(x + 0.375f, x + 0.375f, y - 0.25f + 1.0f - n, y - 0.25f + 1.0f, z + 0.375f, z + 0.625f, brightness * 0.6f, n2);
                this.renderPistonArmUpDown(x + 0.625f, x + 0.625f, y - 0.25f + 1.0f - n, y - 0.25f + 1.0f, z + 0.625f, z + 0.375f, brightness * 0.6f, n2);
                break;
            }
            case 2: {
                this.eastFlip = 1;
                this.westFlip = 2;
                tt.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.25f);
                this.tesselateBlockInWorld(tt, x, y, z);
                this.renderPistonArmNorthSouth(x + 0.375f, x + 0.375f, y + 0.625f, y + 0.375f, z + 0.25f, z + 0.25f + n, brightness * 0.6f, n2);
                this.renderPistonArmNorthSouth(x + 0.625f, x + 0.625f, y + 0.375f, y + 0.625f, z + 0.25f, z + 0.25f + n, brightness * 0.6f, n2);
                this.renderPistonArmNorthSouth(x + 0.375f, x + 0.625f, y + 0.375f, y + 0.375f, z + 0.25f, z + 0.25f + n, brightness * 0.5f, n2);
                this.renderPistonArmNorthSouth(x + 0.625f, x + 0.375f, y + 0.625f, y + 0.625f, z + 0.25f, z + 0.25f + n, brightness, n2);
                break;
            }
            case 3: {
                this.eastFlip = 2;
                this.westFlip = 1;
                this.upFlip = 3;
                this.downFlip = 3;
                tt.setShape(0.0f, 0.0f, 0.75f, 1.0f, 1.0f, 1.0f);
                this.tesselateBlockInWorld(tt, x, y, z);
                this.renderPistonArmNorthSouth(x + 0.375f, x + 0.375f, y + 0.625f, y + 0.375f, z - 0.25f + 1.0f - n, z - 0.25f + 1.0f, brightness * 0.6f, n2);
                this.renderPistonArmNorthSouth(x + 0.625f, x + 0.625f, y + 0.375f, y + 0.625f, z - 0.25f + 1.0f - n, z - 0.25f + 1.0f, brightness * 0.6f, n2);
                this.renderPistonArmNorthSouth(x + 0.375f, x + 0.625f, y + 0.375f, y + 0.375f, z - 0.25f + 1.0f - n, z - 0.25f + 1.0f, brightness * 0.5f, n2);
                this.renderPistonArmNorthSouth(x + 0.625f, x + 0.375f, y + 0.625f, y + 0.625f, z - 0.25f + 1.0f - n, z - 0.25f + 1.0f, brightness, n2);
                break;
            }
            case 4: {
                this.northFlip = 1;
                this.southFlip = 2;
                this.upFlip = 2;
                this.downFlip = 1;
                tt.setShape(0.0f, 0.0f, 0.0f, 0.25f, 1.0f, 1.0f);
                this.tesselateBlockInWorld(tt, x, y, z);
                this.renderPistonArmEastWest(x + 0.25f, x + 0.25f + n, y + 0.375f, y + 0.375f, z + 0.625f, z + 0.375f, brightness * 0.5f, n2);
                this.renderPistonArmEastWest(x + 0.25f, x + 0.25f + n, y + 0.625f, y + 0.625f, z + 0.375f, z + 0.625f, brightness, n2);
                this.renderPistonArmEastWest(x + 0.25f, x + 0.25f + n, y + 0.375f, y + 0.625f, z + 0.375f, z + 0.375f, brightness * 0.6f, n2);
                this.renderPistonArmEastWest(x + 0.25f, x + 0.25f + n, y + 0.625f, y + 0.375f, z + 0.625f, z + 0.625f, brightness * 0.6f, n2);
                break;
            }
            case 5: {
                this.northFlip = 2;
                this.southFlip = 1;
                this.upFlip = 1;
                this.downFlip = 2;
                tt.setShape(0.75f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
                this.tesselateBlockInWorld(tt, x, y, z);
                this.renderPistonArmEastWest(x - 0.25f + 1.0f - n, x - 0.25f + 1.0f, y + 0.375f, y + 0.375f, z + 0.625f, z + 0.375f, brightness * 0.5f, n2);
                this.renderPistonArmEastWest(x - 0.25f + 1.0f - n, x - 0.25f + 1.0f, y + 0.625f, y + 0.625f, z + 0.375f, z + 0.625f, brightness, n2);
                this.renderPistonArmEastWest(x - 0.25f + 1.0f - n, x - 0.25f + 1.0f, y + 0.375f, y + 0.625f, z + 0.375f, z + 0.375f, brightness * 0.6f, n2);
                this.renderPistonArmEastWest(x - 0.25f + 1.0f - n, x - 0.25f + 1.0f, y + 0.625f, y + 0.375f, z + 0.625f, z + 0.625f, brightness * 0.6f, n2);
                break;
            }
        }
        this.northFlip = 0;
        this.southFlip = 0;
        this.eastFlip = 0;
        this.westFlip = 0;
        this.upFlip = 0;
        this.downFlip = 0;
        tt.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        return true;
    }
    
    public boolean tesselateLeverInWorld(final Tile tt, final int x, final int ry, final int z) {
        final int data = this.level.getData(x, ry, z);
        final int n = data & 0x7;
        final boolean b = (data & 0x8) > 0;
        final Tesselator instance = Tesselator.instance;
        final boolean b2 = this.fixedTexture >= 0;
        if (!b2) {
            this.fixedTexture = Tile.stoneBrick.tex;
        }
        final float n2 = 0.25f;
        final float n3 = 0.1875f;
        final float n4 = 0.1875f;
        if (n == 5) {
            tt.setShape(0.5f - n3, 0.0f, 0.5f - n2, 0.5f + n3, n4, 0.5f + n2);
        }
        else if (n == 6) {
            tt.setShape(0.5f - n2, 0.0f, 0.5f - n3, 0.5f + n2, n4, 0.5f + n3);
        }
        else if (n == 4) {
            tt.setShape(0.5f - n3, 0.5f - n2, 1.0f - n4, 0.5f + n3, 0.5f + n2, 1.0f);
        }
        else if (n == 3) {
            tt.setShape(0.5f - n3, 0.5f - n2, 0.0f, 0.5f + n3, 0.5f + n2, n4);
        }
        else if (n == 2) {
            tt.setShape(1.0f - n4, 0.5f - n2, 0.5f - n3, 1.0f, 0.5f + n2, 0.5f + n3);
        }
        else if (n == 1) {
            tt.setShape(0.0f, 0.5f - n2, 0.5f - n3, n4, 0.5f + n2, 0.5f + n3);
        }
        this.tesselateBlockInWorld(tt, x, ry, z);
        if (!b2) {
            this.fixedTexture = -1;
        }
        float brightness = tt.getBrightness(this.level, x, ry, z);
        if (Tile.lightEmission[tt.id] > 0) {
            brightness = 1.0f;
        }
        instance.color(brightness, brightness, brightness);
        int n5 = tt.getTexture(0);
        if (this.fixedTexture >= 0) {
            n5 = this.fixedTexture;
        }
        final int n6 = (n5 & 0xF) << 4;
        final int n7 = n5 & 0xF0;
        float n8 = n6 / 256.0f;
        float n9 = (n6 + 15.99f) / 256.0f;
        float n10 = n7 / 256.0f;
        float n11 = (n7 + 15.99f) / 256.0f;
        final Vec3[] array = new Vec3[8];
        final float n12 = 0.0625f;
        final float n13 = 0.0625f;
        final float n14 = 0.625f;
        array[0] = Vec3.newTemp(-n12, 0.0, -n13);
        array[1] = Vec3.newTemp(n12, 0.0, -n13);
        array[2] = Vec3.newTemp(n12, 0.0, n13);
        array[3] = Vec3.newTemp(-n12, 0.0, n13);
        array[4] = Vec3.newTemp(-n12, n14, -n13);
        array[5] = Vec3.newTemp(n12, n14, -n13);
        array[6] = Vec3.newTemp(n12, n14, n13);
        array[7] = Vec3.newTemp(-n12, n14, n13);
        for (int i = 0; i < 8; ++i) {
            if (b) {
                final Vec3 vec3 = array[i];
                vec3.z -= 0.0625;
                array[i].xRot(0.69813174f);
            }
            else {
                final Vec3 vec4 = array[i];
                vec4.z += 0.0625;
                array[i].xRot(-0.69813174f);
            }
            if (n == 6) {
                array[i].yRot(1.5707964f);
            }
            if (n < 5) {
                final Vec3 vec5 = array[i];
                vec5.y -= 0.375;
                array[i].xRot(1.5707964f);
                if (n == 4) {
                    array[i].yRot(0.0f);
                }
                if (n == 3) {
                    array[i].yRot(3.1415927f);
                }
                if (n == 2) {
                    array[i].yRot(1.5707964f);
                }
                if (n == 1) {
                    array[i].yRot(-1.5707964f);
                }
                final Vec3 vec6 = array[i];
                vec6.x += x + 0.5;
                final Vec3 vec7 = array[i];
                vec7.y += ry + 0.5f;
                final Vec3 vec8 = array[i];
                vec8.z += z + 0.5;
            }
            else {
                final Vec3 vec9 = array[i];
                vec9.x += x + 0.5;
                final Vec3 vec10 = array[i];
                vec10.y += ry + 0.125f;
                final Vec3 vec11 = array[i];
                vec11.z += z + 0.5;
            }
        }
        Vec3 vec12 = null;
        Vec3 vec13 = null;
        Vec3 vec14 = null;
        Vec3 vec15 = null;
        for (int j = 0; j < 6; ++j) {
            if (j == 0) {
                n8 = (n6 + 7) / 256.0f;
                n9 = (n6 + 9 - 0.01f) / 256.0f;
                n10 = (n7 + 6) / 256.0f;
                n11 = (n7 + 8 - 0.01f) / 256.0f;
            }
            else if (j == 2) {
                n8 = (n6 + 7) / 256.0f;
                n9 = (n6 + 9 - 0.01f) / 256.0f;
                n10 = (n7 + 6) / 256.0f;
                n11 = (n7 + 16 - 0.01f) / 256.0f;
            }
            if (j == 0) {
                vec12 = array[0];
                vec13 = array[1];
                vec14 = array[2];
                vec15 = array[3];
            }
            else if (j == 1) {
                vec12 = array[7];
                vec13 = array[6];
                vec14 = array[5];
                vec15 = array[4];
            }
            else if (j == 2) {
                vec12 = array[1];
                vec13 = array[0];
                vec14 = array[4];
                vec15 = array[5];
            }
            else if (j == 3) {
                vec12 = array[2];
                vec13 = array[1];
                vec14 = array[5];
                vec15 = array[6];
            }
            else if (j == 4) {
                vec12 = array[3];
                vec13 = array[2];
                vec14 = array[6];
                vec15 = array[7];
            }
            else if (j == 5) {
                vec12 = array[0];
                vec13 = array[3];
                vec14 = array[7];
                vec15 = array[4];
            }
            instance.vertexUV(vec12.x, vec12.y, vec12.z, n8, n11);
            instance.vertexUV(vec13.x, vec13.y, vec13.z, n9, n11);
            instance.vertexUV(vec14.x, vec14.y, vec14.z, n9, n10);
            instance.vertexUV(vec15.x, vec15.y, vec15.z, n8, n10);
        }
        return true;
    }
    
    public boolean tesselateFireInWorld(final Tile tt, final int x, int y, final int z) {
        final Tesselator instance = Tesselator.instance;
        int n = tt.getTexture(0);
        if (this.fixedTexture >= 0) {
            n = this.fixedTexture;
        }
        final float brightness = tt.getBrightness(this.level, x, y, z);
        instance.color(brightness, brightness, brightness);
        final int n2 = (n & 0xF) << 4;
        final int n3 = n & 0xF0;
        double n4 = n2 / 256.0f;
        double n5 = (n2 + 15.99f) / 256.0f;
        double n6 = n3 / 256.0f;
        double n7 = (n3 + 15.99f) / 256.0f;
        final float n8 = 1.4f;
        if (this.level.isSolidBlockingTile(x, y - 1, z) || Tile.fire.canBurn(this.level, x, y - 1, z)) {
            final double n9 = x + 0.5 + 0.2;
            final double n10 = x + 0.5 - 0.2;
            final double n11 = z + 0.5 + 0.2;
            final double n12 = z + 0.5 - 0.2;
            final double n13 = x + 0.5 - 0.3;
            final double n14 = x + 0.5 + 0.3;
            final double n15 = z + 0.5 - 0.3;
            final double n16 = z + 0.5 + 0.3;
            instance.vertexUV(n13, y + n8, z + 1, n5, n6);
            instance.vertexUV(n9, y + 0, z + 1, n5, n7);
            instance.vertexUV(n9, y + 0, z + 0, n4, n7);
            instance.vertexUV(n13, y + n8, z + 0, n4, n6);
            instance.vertexUV(n14, y + n8, z + 0, n5, n6);
            instance.vertexUV(n10, y + 0, z + 0, n5, n7);
            instance.vertexUV(n10, y + 0, z + 1, n4, n7);
            instance.vertexUV(n14, y + n8, z + 1, n4, n6);
            final double n17 = n2 / 256.0f;
            final double n18 = (n2 + 15.99f) / 256.0f;
            final double n19 = (n3 + 16) / 256.0f;
            final double n20 = (n3 + 15.99f + 16.0f) / 256.0f;
            instance.vertexUV(x + 1, y + n8, n16, n18, n19);
            instance.vertexUV(x + 1, y + 0, n12, n18, n20);
            instance.vertexUV(x + 0, y + 0, n12, n17, n20);
            instance.vertexUV(x + 0, y + n8, n16, n17, n19);
            instance.vertexUV(x + 0, y + n8, n15, n18, n19);
            instance.vertexUV(x + 0, y + 0, n11, n18, n20);
            instance.vertexUV(x + 1, y + 0, n11, n17, n20);
            instance.vertexUV(x + 1, y + n8, n15, n17, n19);
            final double n21 = x + 0.5 - 0.5;
            final double n22 = x + 0.5 + 0.5;
            final double n23 = z + 0.5 - 0.5;
            final double n24 = z + 0.5 + 0.5;
            final double n25 = x + 0.5 - 0.4;
            final double n26 = x + 0.5 + 0.4;
            final double n27 = z + 0.5 - 0.4;
            final double n28 = z + 0.5 + 0.4;
            instance.vertexUV(n25, y + n8, z + 0, n17, n19);
            instance.vertexUV(n21, y + 0, z + 0, n17, n20);
            instance.vertexUV(n21, y + 0, z + 1, n18, n20);
            instance.vertexUV(n25, y + n8, z + 1, n18, n19);
            instance.vertexUV(n26, y + n8, z + 1, n17, n19);
            instance.vertexUV(n22, y + 0, z + 1, n17, n20);
            instance.vertexUV(n22, y + 0, z + 0, n18, n20);
            instance.vertexUV(n26, y + n8, z + 0, n18, n19);
            final double n29 = n2 / 256.0f;
            final double n30 = (n2 + 15.99f) / 256.0f;
            final double n31 = n3 / 256.0f;
            final double n32 = (n3 + 15.99f) / 256.0f;
            instance.vertexUV(x + 0, y + n8, n28, n29, n31);
            instance.vertexUV(x + 0, y + 0, n24, n29, n32);
            instance.vertexUV(x + 1, y + 0, n24, n30, n32);
            instance.vertexUV(x + 1, y + n8, n28, n30, n31);
            instance.vertexUV(x + 1, y + n8, n27, n29, n31);
            instance.vertexUV(x + 1, y + 0, n23, n29, n32);
            instance.vertexUV(x + 0, y + 0, n23, n30, n32);
            instance.vertexUV(x + 0, y + n8, n27, n30, n31);
        }
        else {
            final float n33 = 0.2f;
            final float n34 = 0.0625f;
            if ((x + y + z & 0x1) == 0x1) {
                n4 = n2 / 256.0f;
                n5 = (n2 + 15.99f) / 256.0f;
                n6 = (n3 + 16) / 256.0f;
                n7 = (n3 + 15.99f + 16.0f) / 256.0f;
            }
            if ((x / 2 + y / 2 + z / 2 & 0x1) == 0x1) {
                final double n35 = n5;
                n5 = n4;
                n4 = n35;
            }
            if (Tile.fire.canBurn(this.level, x - 1, y, z)) {
                instance.vertexUV(x + n33, y + n8 + n34, z + 1, n5, n6);
                instance.vertexUV(x + 0, y + 0 + n34, z + 1, n5, n7);
                instance.vertexUV(x + 0, y + 0 + n34, z + 0, n4, n7);
                instance.vertexUV(x + n33, y + n8 + n34, z + 0, n4, n6);
                instance.vertexUV(x + n33, y + n8 + n34, z + 0, n4, n6);
                instance.vertexUV(x + 0, y + 0 + n34, z + 0, n4, n7);
                instance.vertexUV(x + 0, y + 0 + n34, z + 1, n5, n7);
                instance.vertexUV(x + n33, y + n8 + n34, z + 1, n5, n6);
            }
            if (Tile.fire.canBurn(this.level, x + 1, y, z)) {
                instance.vertexUV(x + 1 - n33, y + n8 + n34, z + 0, n4, n6);
                instance.vertexUV(x + 1 - 0, y + 0 + n34, z + 0, n4, n7);
                instance.vertexUV(x + 1 - 0, y + 0 + n34, z + 1, n5, n7);
                instance.vertexUV(x + 1 - n33, y + n8 + n34, z + 1, n5, n6);
                instance.vertexUV(x + 1 - n33, y + n8 + n34, z + 1, n5, n6);
                instance.vertexUV(x + 1 - 0, y + 0 + n34, z + 1, n5, n7);
                instance.vertexUV(x + 1 - 0, y + 0 + n34, z + 0, n4, n7);
                instance.vertexUV(x + 1 - n33, y + n8 + n34, z + 0, n4, n6);
            }
            if (Tile.fire.canBurn(this.level, x, y, z - 1)) {
                instance.vertexUV(x + 0, y + n8 + n34, z + n33, n5, n6);
                instance.vertexUV(x + 0, y + 0 + n34, z + 0, n5, n7);
                instance.vertexUV(x + 1, y + 0 + n34, z + 0, n4, n7);
                instance.vertexUV(x + 1, y + n8 + n34, z + n33, n4, n6);
                instance.vertexUV(x + 1, y + n8 + n34, z + n33, n4, n6);
                instance.vertexUV(x + 1, y + 0 + n34, z + 0, n4, n7);
                instance.vertexUV(x + 0, y + 0 + n34, z + 0, n5, n7);
                instance.vertexUV(x + 0, y + n8 + n34, z + n33, n5, n6);
            }
            if (Tile.fire.canBurn(this.level, x, y, z + 1)) {
                instance.vertexUV(x + 1, y + n8 + n34, z + 1 - n33, n4, n6);
                instance.vertexUV(x + 1, y + 0 + n34, z + 1 - 0, n4, n7);
                instance.vertexUV(x + 0, y + 0 + n34, z + 1 - 0, n5, n7);
                instance.vertexUV(x + 0, y + n8 + n34, z + 1 - n33, n5, n6);
                instance.vertexUV(x + 0, y + n8 + n34, z + 1 - n33, n5, n6);
                instance.vertexUV(x + 0, y + 0 + n34, z + 1 - 0, n5, n7);
                instance.vertexUV(x + 1, y + 0 + n34, z + 1 - 0, n4, n7);
                instance.vertexUV(x + 1, y + n8 + n34, z + 1 - n33, n4, n6);
            }
            if (Tile.fire.canBurn(this.level, x, y + 1, z)) {
                final double n36 = x + 0.5 + 0.5;
                final double n37 = x + 0.5 - 0.5;
                final double n38 = z + 0.5 + 0.5;
                final double n39 = z + 0.5 - 0.5;
                final double n40 = x + 0.5 - 0.5;
                final double n41 = x + 0.5 + 0.5;
                final double n42 = z + 0.5 - 0.5;
                final double n43 = z + 0.5 + 0.5;
                final double n44 = n2 / 256.0f;
                final double n45 = (n2 + 15.99f) / 256.0f;
                final double n46 = n3 / 256.0f;
                final double n47 = (n3 + 15.99f) / 256.0f;
                ++y;
                final float n48 = -0.2f;
                if ((x + y + z & 0x1) == 0x0) {
                    instance.vertexUV(n40, y + n48, z + 0, n45, n46);
                    instance.vertexUV(n36, y + 0, z + 0, n45, n47);
                    instance.vertexUV(n36, y + 0, z + 1, n44, n47);
                    instance.vertexUV(n40, y + n48, z + 1, n44, n46);
                    final double n49 = n2 / 256.0f;
                    final double n50 = (n2 + 15.99f) / 256.0f;
                    final double n51 = (n3 + 16) / 256.0f;
                    final double n52 = (n3 + 15.99f + 16.0f) / 256.0f;
                    instance.vertexUV(n41, y + n48, z + 1, n50, n51);
                    instance.vertexUV(n37, y + 0, z + 1, n50, n52);
                    instance.vertexUV(n37, y + 0, z + 0, n49, n52);
                    instance.vertexUV(n41, y + n48, z + 0, n49, n51);
                }
                else {
                    instance.vertexUV(x + 0, y + n48, n43, n45, n46);
                    instance.vertexUV(x + 0, y + 0, n39, n45, n47);
                    instance.vertexUV(x + 1, y + 0, n39, n44, n47);
                    instance.vertexUV(x + 1, y + n48, n43, n44, n46);
                    final double n53 = n2 / 256.0f;
                    final double n54 = (n2 + 15.99f) / 256.0f;
                    final double n55 = (n3 + 16) / 256.0f;
                    final double n56 = (n3 + 15.99f + 16.0f) / 256.0f;
                    instance.vertexUV(x + 1, y + n48, n42, n54, n55);
                    instance.vertexUV(x + 1, y + 0, n38, n54, n56);
                    instance.vertexUV(x + 0, y + 0, n38, n53, n56);
                    instance.vertexUV(x + 0, y + n48, n42, n53, n55);
                }
            }
        }
        return true;
    }
    
    public boolean tesselateDustInWorld(final Tile tt, final int x, final int y, final int z) {
        final Tesselator instance = Tesselator.instance;
        final int data = this.level.getData(x, y, z);
        int n = tt.getTexture(1, data);
        if (this.fixedTexture >= 0) {
            n = this.fixedTexture;
        }
        final float brightness = tt.getBrightness(this.level, x, y, z);
        final float n2 = data / 15.0f;
        float n3 = n2 * 0.6f + 0.4f;
        if (data == 0) {
            n3 = 0.3f;
        }
        float n4 = n2 * n2 * 0.7f - 0.5f;
        float n5 = n2 * n2 * 0.6f - 0.7f;
        if (n4 < 0.0f) {
            n4 = 0.0f;
        }
        if (n5 < 0.0f) {
            n5 = 0.0f;
        }
        instance.color(brightness * n3, brightness * n4, brightness * n5);
        final int n6 = (n & 0xF) << 4;
        final int n7 = n & 0xF0;
        double n8 = n6 / 256.0f;
        double n9 = (n6 + 15.99f) / 256.0f;
        double n10 = n7 / 256.0f;
        double n11 = (n7 + 15.99f) / 256.0f;
        boolean b = RedStoneDustTile.shouldReceivePowerFrom(this.level, x - 1, y, z, 1) || (!this.level.isSolidBlockingTile(x - 1, y, z) && RedStoneDustTile.shouldReceivePowerFrom(this.level, x - 1, y - 1, z, -1));
        boolean b2 = RedStoneDustTile.shouldReceivePowerFrom(this.level, x + 1, y, z, 3) || (!this.level.isSolidBlockingTile(x + 1, y, z) && RedStoneDustTile.shouldReceivePowerFrom(this.level, x + 1, y - 1, z, -1));
        boolean b3 = RedStoneDustTile.shouldReceivePowerFrom(this.level, x, y, z - 1, 2) || (!this.level.isSolidBlockingTile(x, y, z - 1) && RedStoneDustTile.shouldReceivePowerFrom(this.level, x, y - 1, z - 1, -1));
        boolean b4 = RedStoneDustTile.shouldReceivePowerFrom(this.level, x, y, z + 1, 0) || (!this.level.isSolidBlockingTile(x, y, z + 1) && RedStoneDustTile.shouldReceivePowerFrom(this.level, x, y - 1, z + 1, -1));
        if (!this.level.isSolidBlockingTile(x, y + 1, z)) {
            if (this.level.isSolidBlockingTile(x - 1, y, z) && RedStoneDustTile.shouldReceivePowerFrom(this.level, x - 1, y + 1, z, -1)) {
                b = true;
            }
            if (this.level.isSolidBlockingTile(x + 1, y, z) && RedStoneDustTile.shouldReceivePowerFrom(this.level, x + 1, y + 1, z, -1)) {
                b2 = true;
            }
            if (this.level.isSolidBlockingTile(x, y, z - 1) && RedStoneDustTile.shouldReceivePowerFrom(this.level, x, y + 1, z - 1, -1)) {
                b3 = true;
            }
            if (this.level.isSolidBlockingTile(x, y, z + 1) && RedStoneDustTile.shouldReceivePowerFrom(this.level, x, y + 1, z + 1, -1)) {
                b4 = true;
            }
        }
        float n12 = (float)(x + 0);
        float n13 = (float)(x + 1);
        float n14 = (float)(z + 0);
        float n15 = (float)(z + 1);
        int n16 = 0;
        if ((b || b2) && !b3 && !b4) {
            n16 = 1;
        }
        if ((b3 || b4) && !b2 && !b) {
            n16 = 2;
        }
        if (n16 != 0) {
            n8 = (n6 + 16) / 256.0f;
            n9 = (n6 + 16 + 15.99f) / 256.0f;
            n10 = n7 / 256.0f;
            n11 = (n7 + 15.99f) / 256.0f;
        }
        if (n16 == 0) {
            if (b2 || b3 || b4 || b) {
                if (!b) {
                    n12 += 0.3125f;
                }
                if (!b) {
                    n8 += 0.01953125;
                }
                if (!b2) {
                    n13 -= 0.3125f;
                }
                if (!b2) {
                    n9 -= 0.01953125;
                }
                if (!b3) {
                    n14 += 0.3125f;
                }
                if (!b3) {
                    n10 += 0.01953125;
                }
                if (!b4) {
                    n15 -= 0.3125f;
                }
                if (!b4) {
                    n11 -= 0.01953125;
                }
            }
            instance.vertexUV(n13, y + 0.015625f, n15, n9, n11);
            instance.vertexUV(n13, y + 0.015625f, n14, n9, n10);
            instance.vertexUV(n12, y + 0.015625f, n14, n8, n10);
            instance.vertexUV(n12, y + 0.015625f, n15, n8, n11);
            instance.color(brightness, brightness, brightness);
            instance.vertexUV(n13, y + 0.015625f, n15, n9, n11 + 0.0625);
            instance.vertexUV(n13, y + 0.015625f, n14, n9, n10 + 0.0625);
            instance.vertexUV(n12, y + 0.015625f, n14, n8, n10 + 0.0625);
            instance.vertexUV(n12, y + 0.015625f, n15, n8, n11 + 0.0625);
        }
        else if (n16 == 1) {
            instance.vertexUV(n13, y + 0.015625f, n15, n9, n11);
            instance.vertexUV(n13, y + 0.015625f, n14, n9, n10);
            instance.vertexUV(n12, y + 0.015625f, n14, n8, n10);
            instance.vertexUV(n12, y + 0.015625f, n15, n8, n11);
            instance.color(brightness, brightness, brightness);
            instance.vertexUV(n13, y + 0.015625f, n15, n9, n11 + 0.0625);
            instance.vertexUV(n13, y + 0.015625f, n14, n9, n10 + 0.0625);
            instance.vertexUV(n12, y + 0.015625f, n14, n8, n10 + 0.0625);
            instance.vertexUV(n12, y + 0.015625f, n15, n8, n11 + 0.0625);
        }
        else if (n16 == 2) {
            instance.vertexUV(n13, y + 0.015625f, n15, n9, n11);
            instance.vertexUV(n13, y + 0.015625f, n14, n8, n11);
            instance.vertexUV(n12, y + 0.015625f, n14, n8, n10);
            instance.vertexUV(n12, y + 0.015625f, n15, n9, n10);
            instance.color(brightness, brightness, brightness);
            instance.vertexUV(n13, y + 0.015625f, n15, n9, n11 + 0.0625);
            instance.vertexUV(n13, y + 0.015625f, n14, n8, n11 + 0.0625);
            instance.vertexUV(n12, y + 0.015625f, n14, n8, n10 + 0.0625);
            instance.vertexUV(n12, y + 0.015625f, n15, n9, n10 + 0.0625);
        }
        if (!this.level.isSolidBlockingTile(x, y + 1, z)) {
            final double n17 = (n6 + 16) / 256.0f;
            final double n18 = (n6 + 16 + 15.99f) / 256.0f;
            final double n19 = n7 / 256.0f;
            final double n20 = (n7 + 15.99f) / 256.0f;
            if (this.level.isSolidBlockingTile(x - 1, y, z) && this.level.getTile(x - 1, y + 1, z) == Tile.redStoneDust.id) {
                instance.color(brightness * n3, brightness * n4, brightness * n5);
                instance.vertexUV(x + 0.015625f, y + 1 + 0.021875f, z + 1, n18, n19);
                instance.vertexUV(x + 0.015625f, y + 0, z + 1, n17, n19);
                instance.vertexUV(x + 0.015625f, y + 0, z + 0, n17, n20);
                instance.vertexUV(x + 0.015625f, y + 1 + 0.021875f, z + 0, n18, n20);
                instance.color(brightness, brightness, brightness);
                instance.vertexUV(x + 0.015625f, y + 1 + 0.021875f, z + 1, n18, n19 + 0.0625);
                instance.vertexUV(x + 0.015625f, y + 0, z + 1, n17, n19 + 0.0625);
                instance.vertexUV(x + 0.015625f, y + 0, z + 0, n17, n20 + 0.0625);
                instance.vertexUV(x + 0.015625f, y + 1 + 0.021875f, z + 0, n18, n20 + 0.0625);
            }
            if (this.level.isSolidBlockingTile(x + 1, y, z) && this.level.getTile(x + 1, y + 1, z) == Tile.redStoneDust.id) {
                instance.color(brightness * n3, brightness * n4, brightness * n5);
                instance.vertexUV(x + 1 - 0.015625f, y + 0, z + 1, n17, n20);
                instance.vertexUV(x + 1 - 0.015625f, y + 1 + 0.021875f, z + 1, n18, n20);
                instance.vertexUV(x + 1 - 0.015625f, y + 1 + 0.021875f, z + 0, n18, n19);
                instance.vertexUV(x + 1 - 0.015625f, y + 0, z + 0, n17, n19);
                instance.color(brightness, brightness, brightness);
                instance.vertexUV(x + 1 - 0.015625f, y + 0, z + 1, n17, n20 + 0.0625);
                instance.vertexUV(x + 1 - 0.015625f, y + 1 + 0.021875f, z + 1, n18, n20 + 0.0625);
                instance.vertexUV(x + 1 - 0.015625f, y + 1 + 0.021875f, z + 0, n18, n19 + 0.0625);
                instance.vertexUV(x + 1 - 0.015625f, y + 0, z + 0, n17, n19 + 0.0625);
            }
            if (this.level.isSolidBlockingTile(x, y, z - 1) && this.level.getTile(x, y + 1, z - 1) == Tile.redStoneDust.id) {
                instance.color(brightness * n3, brightness * n4, brightness * n5);
                instance.vertexUV(x + 1, y + 0, z + 0.015625f, n17, n20);
                instance.vertexUV(x + 1, y + 1 + 0.021875f, z + 0.015625f, n18, n20);
                instance.vertexUV(x + 0, y + 1 + 0.021875f, z + 0.015625f, n18, n19);
                instance.vertexUV(x + 0, y + 0, z + 0.015625f, n17, n19);
                instance.color(brightness, brightness, brightness);
                instance.vertexUV(x + 1, y + 0, z + 0.015625f, n17, n20 + 0.0625);
                instance.vertexUV(x + 1, y + 1 + 0.021875f, z + 0.015625f, n18, n20 + 0.0625);
                instance.vertexUV(x + 0, y + 1 + 0.021875f, z + 0.015625f, n18, n19 + 0.0625);
                instance.vertexUV(x + 0, y + 0, z + 0.015625f, n17, n19 + 0.0625);
            }
            if (this.level.isSolidBlockingTile(x, y, z + 1) && this.level.getTile(x, y + 1, z + 1) == Tile.redStoneDust.id) {
                instance.color(brightness * n3, brightness * n4, brightness * n5);
                instance.vertexUV(x + 1, y + 1 + 0.021875f, z + 1 - 0.015625f, n18, n19);
                instance.vertexUV(x + 1, y + 0, z + 1 - 0.015625f, n17, n19);
                instance.vertexUV(x + 0, y + 0, z + 1 - 0.015625f, n17, n20);
                instance.vertexUV(x + 0, y + 1 + 0.021875f, z + 1 - 0.015625f, n18, n20);
                instance.color(brightness, brightness, brightness);
                instance.vertexUV(x + 1, y + 1 + 0.021875f, z + 1 - 0.015625f, n18, n19 + 0.0625);
                instance.vertexUV(x + 1, y + 0, z + 1 - 0.015625f, n17, n19 + 0.0625);
                instance.vertexUV(x + 0, y + 0, z + 1 - 0.015625f, n17, n20 + 0.0625);
                instance.vertexUV(x + 0, y + 1 + 0.021875f, z + 1 - 0.015625f, n18, n20 + 0.0625);
            }
        }
        return true;
    }
    
    public boolean tesselateRailInWorld(final RailTile tt, final int x, final int y, final int z) {
        final Tesselator instance = Tesselator.instance;
        int data = this.level.getData(x, y, z);
        int n = tt.getTexture(0, data);
        if (this.fixedTexture >= 0) {
            n = this.fixedTexture;
        }
        if (tt.isUsesDataBit()) {
            data &= 0x7;
        }
        final float brightness = tt.getBrightness(this.level, x, y, z);
        instance.color(brightness, brightness, brightness);
        final int n2 = (n & 0xF) << 4;
        final int n3 = n & 0xF0;
        final double n4 = n2 / 256.0f;
        final double n5 = (n2 + 15.99f) / 256.0f;
        final double n6 = n3 / 256.0f;
        final double n7 = (n3 + 15.99f) / 256.0f;
        final float n8 = 0.0625f;
        float n9 = (float)(x + 1);
        float n10 = (float)(x + 1);
        float n11 = (float)(x + 0);
        float n12 = (float)(x + 0);
        float n13 = (float)(z + 0);
        float n14 = (float)(z + 1);
        float n15 = (float)(z + 1);
        float n16 = (float)(z + 0);
        float n17 = y + n8;
        float n18 = y + n8;
        float n19 = y + n8;
        float n20 = y + n8;
        if (data == 1 || data == 2 || data == 3 || data == 7) {
            n12 = (n9 = (float)(x + 1));
            n11 = (n10 = (float)(x + 0));
            n14 = (n13 = (float)(z + 1));
            n16 = (n15 = (float)(z + 0));
        }
        else if (data == 8) {
            n10 = (n9 = (float)(x + 0));
            n12 = (n11 = (float)(x + 1));
            n16 = (n13 = (float)(z + 1));
            n15 = (n14 = (float)(z + 0));
        }
        else if (data == 9) {
            n12 = (n9 = (float)(x + 0));
            n11 = (n10 = (float)(x + 1));
            n14 = (n13 = (float)(z + 0));
            n16 = (n15 = (float)(z + 1));
        }
        if (data == 2 || data == 4) {
            ++n17;
            ++n20;
        }
        else if (data == 3 || data == 5) {
            ++n18;
            ++n19;
        }
        instance.vertexUV(n9, n17, n13, n5, n6);
        instance.vertexUV(n10, n18, n14, n5, n7);
        instance.vertexUV(n11, n19, n15, n4, n7);
        instance.vertexUV(n12, n20, n16, n4, n6);
        instance.vertexUV(n12, n20, n16, n4, n6);
        instance.vertexUV(n11, n19, n15, n4, n7);
        instance.vertexUV(n10, n18, n14, n5, n7);
        instance.vertexUV(n9, n17, n13, n5, n6);
        return true;
    }
    
    public boolean tesselateLadderInWorld(final Tile tt, final int x, final int y, final int z) {
        final Tesselator instance = Tesselator.instance;
        int n = tt.getTexture(0);
        if (this.fixedTexture >= 0) {
            n = this.fixedTexture;
        }
        final float brightness = tt.getBrightness(this.level, x, y, z);
        instance.color(brightness, brightness, brightness);
        final int n2 = (n & 0xF) << 4;
        final int n3 = n & 0xF0;
        final double n4 = n2 / 256.0f;
        final double n5 = (n2 + 15.99f) / 256.0f;
        final double n6 = n3 / 256.0f;
        final double n7 = (n3 + 15.99f) / 256.0f;
        final int data = this.level.getData(x, y, z);
        final float n8 = 0.0f;
        final float n9 = 0.05f;
        if (data == 5) {
            instance.vertexUV(x + n9, y + 1 + n8, z + 1 + n8, n4, n6);
            instance.vertexUV(x + n9, y + 0 - n8, z + 1 + n8, n4, n7);
            instance.vertexUV(x + n9, y + 0 - n8, z + 0 - n8, n5, n7);
            instance.vertexUV(x + n9, y + 1 + n8, z + 0 - n8, n5, n6);
        }
        if (data == 4) {
            instance.vertexUV(x + 1 - n9, y + 0 - n8, z + 1 + n8, n5, n7);
            instance.vertexUV(x + 1 - n9, y + 1 + n8, z + 1 + n8, n5, n6);
            instance.vertexUV(x + 1 - n9, y + 1 + n8, z + 0 - n8, n4, n6);
            instance.vertexUV(x + 1 - n9, y + 0 - n8, z + 0 - n8, n4, n7);
        }
        if (data == 3) {
            instance.vertexUV(x + 1 + n8, y + 0 - n8, z + n9, n5, n7);
            instance.vertexUV(x + 1 + n8, y + 1 + n8, z + n9, n5, n6);
            instance.vertexUV(x + 0 - n8, y + 1 + n8, z + n9, n4, n6);
            instance.vertexUV(x + 0 - n8, y + 0 - n8, z + n9, n4, n7);
        }
        if (data == 2) {
            instance.vertexUV(x + 1 + n8, y + 1 + n8, z + 1 - n9, n4, n6);
            instance.vertexUV(x + 1 + n8, y + 0 - n8, z + 1 - n9, n4, n7);
            instance.vertexUV(x + 0 - n8, y + 0 - n8, z + 1 - n9, n5, n7);
            instance.vertexUV(x + 0 - n8, y + 1 + n8, z + 1 - n9, n5, n6);
        }
        return true;
    }
    
    public boolean tesselateCrossInWorld(final Tile tt, final int x, final int y, final int z) {
        final Tesselator instance = Tesselator.instance;
        final float brightness = tt.getBrightness(this.level, x, y, z);
        final int color = tt.getColor(this.level, x, y, z);
        float n = (color >> 16 & 0xFF) / 255.0f;
        float n2 = (color >> 8 & 0xFF) / 255.0f;
        float n3 = (color & 0xFF) / 255.0f;
        if (GameRenderer.anaglyph3d) {
            final float n4 = (n * 30.0f + n2 * 59.0f + n3 * 11.0f) / 100.0f;
            final float n5 = (n * 30.0f + n2 * 70.0f) / 100.0f;
            final float n6 = (n * 30.0f + n3 * 70.0f) / 100.0f;
            n = n4;
            n2 = n5;
            n3 = n6;
        }
        instance.color(brightness * n, brightness * n2, brightness * n3);
        double x2 = x;
        double y2 = y;
        double z2 = z;
        if (tt == Tile.tallgrass) {
            final long n7 = (long)(x * 3129871) ^ z * 116129781L ^ (long)y;
            final long n8 = n7 * n7 * 42317861L + n7 * 11L;
            x2 += ((n8 >> 16 & 0xFL) / 15.0f - 0.5) * 0.5;
            y2 += ((n8 >> 20 & 0xFL) / 15.0f - 1.0) * 0.2;
            z2 += ((n8 >> 24 & 0xFL) / 15.0f - 0.5) * 0.5;
        }
        this.tesselateCrossTexture(tt, this.level.getData(x, y, z), x2, y2, z2);
        return true;
    }
    
    public boolean tesselateRowInWorld(final Tile tt, final int x, final int y, final int z) {
        final Tesselator instance = Tesselator.instance;
        final float brightness = tt.getBrightness(this.level, x, y, z);
        instance.color(brightness, brightness, brightness);
        this.tesselateRowTexture(tt, this.level.getData(x, y, z), x, y - 0.0625f, z);
        return true;
    }
    
    public void tesselateTorch(final Tile tt, double x, final double y, double z, final double xxa, final double zza) {
        final Tesselator instance = Tesselator.instance;
        int n = tt.getTexture(0);
        if (this.fixedTexture >= 0) {
            n = this.fixedTexture;
        }
        final int n2 = (n & 0xF) << 4;
        final int n3 = n & 0xF0;
        final float n4 = n2 / 256.0f;
        final float n5 = (n2 + 15.99f) / 256.0f;
        final float n6 = n3 / 256.0f;
        final float n7 = (n3 + 15.99f) / 256.0f;
        final double n8 = n4 + 0.02734375;
        final double n9 = n6 + 0.0234375;
        final double n10 = n4 + 0.03515625;
        final double n11 = n6 + 0.03125;
        x += 0.5;
        z += 0.5;
        final double n12 = x - 0.5;
        final double n13 = x + 0.5;
        final double n14 = z - 0.5;
        final double n15 = z + 0.5;
        final double n16 = 0.0625;
        final double n17 = 0.625;
        instance.vertexUV(x + xxa * (1.0 - n17) - n16, y + n17, z + zza * (1.0 - n17) - n16, n8, n9);
        instance.vertexUV(x + xxa * (1.0 - n17) - n16, y + n17, z + zza * (1.0 - n17) + n16, n8, n11);
        instance.vertexUV(x + xxa * (1.0 - n17) + n16, y + n17, z + zza * (1.0 - n17) + n16, n10, n11);
        instance.vertexUV(x + xxa * (1.0 - n17) + n16, y + n17, z + zza * (1.0 - n17) - n16, n10, n9);
        instance.vertexUV(x - n16, y + 1.0, n14, n4, n6);
        instance.vertexUV(x - n16 + xxa, y + 0.0, n14 + zza, n4, n7);
        instance.vertexUV(x - n16 + xxa, y + 0.0, n15 + zza, n5, n7);
        instance.vertexUV(x - n16, y + 1.0, n15, n5, n6);
        instance.vertexUV(x + n16, y + 1.0, n15, n4, n6);
        instance.vertexUV(x + xxa + n16, y + 0.0, n15 + zza, n4, n7);
        instance.vertexUV(x + xxa + n16, y + 0.0, n14 + zza, n5, n7);
        instance.vertexUV(x + n16, y + 1.0, n14, n5, n6);
        instance.vertexUV(n12, y + 1.0, z + n16, n4, n6);
        instance.vertexUV(n12 + xxa, y + 0.0, z + n16 + zza, n4, n7);
        instance.vertexUV(n13 + xxa, y + 0.0, z + n16 + zza, n5, n7);
        instance.vertexUV(n13, y + 1.0, z + n16, n5, n6);
        instance.vertexUV(n13, y + 1.0, z - n16, n4, n6);
        instance.vertexUV(n13 + xxa, y + 0.0, z - n16 + zza, n4, n7);
        instance.vertexUV(n12 + xxa, y + 0.0, z - n16 + zza, n5, n7);
        instance.vertexUV(n12, y + 1.0, z - n16, n5, n6);
    }
    
    public void tesselateCrossTexture(final Tile tt, final int data, final double x, final double y, final double z) {
        final Tesselator instance = Tesselator.instance;
        int n = tt.getTexture(0, data);
        if (this.fixedTexture >= 0) {
            n = this.fixedTexture;
        }
        final int n2 = (n & 0xF) << 4;
        final int n3 = n & 0xF0;
        final double n4 = n2 / 256.0f;
        final double n5 = (n2 + 15.99f) / 256.0f;
        final double n6 = n3 / 256.0f;
        final double n7 = (n3 + 15.99f) / 256.0f;
        final double n8 = x + 0.5 - 0.44999998807907104;
        final double n9 = x + 0.5 + 0.44999998807907104;
        final double n10 = z + 0.5 - 0.44999998807907104;
        final double n11 = z + 0.5 + 0.44999998807907104;
        instance.vertexUV(n8, y + 1.0, n10, n4, n6);
        instance.vertexUV(n8, y + 0.0, n10, n4, n7);
        instance.vertexUV(n9, y + 0.0, n11, n5, n7);
        instance.vertexUV(n9, y + 1.0, n11, n5, n6);
        instance.vertexUV(n9, y + 1.0, n11, n4, n6);
        instance.vertexUV(n9, y + 0.0, n11, n4, n7);
        instance.vertexUV(n8, y + 0.0, n10, n5, n7);
        instance.vertexUV(n8, y + 1.0, n10, n5, n6);
        instance.vertexUV(n8, y + 1.0, n11, n4, n6);
        instance.vertexUV(n8, y + 0.0, n11, n4, n7);
        instance.vertexUV(n9, y + 0.0, n10, n5, n7);
        instance.vertexUV(n9, y + 1.0, n10, n5, n6);
        instance.vertexUV(n9, y + 1.0, n10, n4, n6);
        instance.vertexUV(n9, y + 0.0, n10, n4, n7);
        instance.vertexUV(n8, y + 0.0, n11, n5, n7);
        instance.vertexUV(n8, y + 1.0, n11, n5, n6);
    }
    
    public void tesselateRowTexture(final Tile tt, final int data, final double x, final double y, final double z) {
        final Tesselator instance = Tesselator.instance;
        int n = tt.getTexture(0, data);
        if (this.fixedTexture >= 0) {
            n = this.fixedTexture;
        }
        final int n2 = (n & 0xF) << 4;
        final int n3 = n & 0xF0;
        final double n4 = n2 / 256.0f;
        final double n5 = (n2 + 15.99f) / 256.0f;
        final double n6 = n3 / 256.0f;
        final double n7 = (n3 + 15.99f) / 256.0f;
        final double n8 = x + 0.5 - 0.25;
        final double n9 = x + 0.5 + 0.25;
        final double n10 = z + 0.5 - 0.5;
        final double n11 = z + 0.5 + 0.5;
        instance.vertexUV(n8, y + 1.0, n10, n4, n6);
        instance.vertexUV(n8, y + 0.0, n10, n4, n7);
        instance.vertexUV(n8, y + 0.0, n11, n5, n7);
        instance.vertexUV(n8, y + 1.0, n11, n5, n6);
        instance.vertexUV(n8, y + 1.0, n11, n4, n6);
        instance.vertexUV(n8, y + 0.0, n11, n4, n7);
        instance.vertexUV(n8, y + 0.0, n10, n5, n7);
        instance.vertexUV(n8, y + 1.0, n10, n5, n6);
        instance.vertexUV(n9, y + 1.0, n11, n4, n6);
        instance.vertexUV(n9, y + 0.0, n11, n4, n7);
        instance.vertexUV(n9, y + 0.0, n10, n5, n7);
        instance.vertexUV(n9, y + 1.0, n10, n5, n6);
        instance.vertexUV(n9, y + 1.0, n10, n4, n6);
        instance.vertexUV(n9, y + 0.0, n10, n4, n7);
        instance.vertexUV(n9, y + 0.0, n11, n5, n7);
        instance.vertexUV(n9, y + 1.0, n11, n5, n6);
        final double n12 = x + 0.5 - 0.5;
        final double n13 = x + 0.5 + 0.5;
        final double n14 = z + 0.5 - 0.25;
        final double n15 = z + 0.5 + 0.25;
        instance.vertexUV(n12, y + 1.0, n14, n4, n6);
        instance.vertexUV(n12, y + 0.0, n14, n4, n7);
        instance.vertexUV(n13, y + 0.0, n14, n5, n7);
        instance.vertexUV(n13, y + 1.0, n14, n5, n6);
        instance.vertexUV(n13, y + 1.0, n14, n4, n6);
        instance.vertexUV(n13, y + 0.0, n14, n4, n7);
        instance.vertexUV(n12, y + 0.0, n14, n5, n7);
        instance.vertexUV(n12, y + 1.0, n14, n5, n6);
        instance.vertexUV(n13, y + 1.0, n15, n4, n6);
        instance.vertexUV(n13, y + 0.0, n15, n4, n7);
        instance.vertexUV(n12, y + 0.0, n15, n5, n7);
        instance.vertexUV(n12, y + 1.0, n15, n5, n6);
        instance.vertexUV(n12, y + 1.0, n15, n4, n6);
        instance.vertexUV(n12, y + 0.0, n15, n4, n7);
        instance.vertexUV(n13, y + 0.0, n15, n5, n7);
        instance.vertexUV(n13, y + 1.0, n15, n5, n6);
    }
    
    public boolean tesselateWaterInWorld(final Tile tt, final int x, final int y, final int z) {
        final Tesselator instance = Tesselator.instance;
        final int color = tt.getColor(this.level, x, y, z);
        final float n = (color >> 16 & 0xFF) / 255.0f;
        final float n2 = (color >> 8 & 0xFF) / 255.0f;
        final float n3 = (color & 0xFF) / 255.0f;
        final boolean faceVisible = tt.isFaceVisible(this.level, x, y + 1, z, 1);
        final boolean faceVisible2 = tt.isFaceVisible(this.level, x, y - 1, z, 0);
        final boolean[] array = { tt.isFaceVisible(this.level, x, y, z - 1, 2), tt.isFaceVisible(this.level, x, y, z + 1, 3), tt.isFaceVisible(this.level, x - 1, y, z, 4), tt.isFaceVisible(this.level, x + 1, y, z, 5) };
        if (!faceVisible && !faceVisible2 && !array[0] && !array[1] && !array[2] && !array[3]) {
            return false;
        }
        boolean b = false;
        final float n4 = 0.5f;
        final float n5 = 1.0f;
        final float n6 = 0.8f;
        final float n7 = 0.6f;
        final double yy0 = 0.0;
        final double yy2 = 1.0;
        final Material material = tt.material;
        final int data = this.level.getData(x, y, z);
        final float waterHeight = this.getWaterHeight(x, y, z, material);
        final float waterHeight2 = this.getWaterHeight(x, y, z + 1, material);
        final float waterHeight3 = this.getWaterHeight(x + 1, y, z + 1, material);
        final float waterHeight4 = this.getWaterHeight(x + 1, y, z, material);
        if (this.noCulling || faceVisible) {
            b = true;
            int n8 = tt.getTexture(1, data);
            float n9 = (float)LiquidTile.getSlopeAngle(this.level, x, y, z, material);
            if (n9 > -999.0f) {
                n8 = tt.getTexture(2, data);
            }
            final int n10 = (n8 & 0xF) << 4;
            final int n11 = n8 & 0xF0;
            double n12 = (n10 + 8.0) / 256.0;
            double n13 = (n11 + 8.0) / 256.0;
            if (n9 < -999.0f) {
                n9 = 0.0f;
            }
            else {
                n12 = (n10 + 16) / 256.0f;
                n13 = (n11 + 16) / 256.0f;
            }
            final float n14 = Mth.sin(n9) * 8.0f / 256.0f;
            final float n15 = Mth.cos(n9) * 8.0f / 256.0f;
            final float brightness = tt.getBrightness(this.level, x, y, z);
            instance.color(n5 * brightness * n, n5 * brightness * n2, n5 * brightness * n3);
            instance.vertexUV(x + 0, y + waterHeight, z + 0, n12 - n15 - n14, n13 - n15 + n14);
            instance.vertexUV(x + 0, y + waterHeight2, z + 1, n12 - n15 + n14, n13 + n15 + n14);
            instance.vertexUV(x + 1, y + waterHeight3, z + 1, n12 + n15 + n14, n13 + n15 - n14);
            instance.vertexUV(x + 1, y + waterHeight4, z + 0, n12 + n15 - n14, n13 - n15 - n14);
        }
        if (this.noCulling || faceVisible2) {
            final float brightness2 = tt.getBrightness(this.level, x, y - 1, z);
            instance.color(n4 * brightness2, n4 * brightness2, n4 * brightness2);
            this.renderFaceUp(tt, x, y, z, tt.getTexture(0));
            b = true;
        }
        for (int i = 0; i < 4; ++i) {
            int x2 = x;
            int z2 = z;
            if (i == 0) {
                --z2;
            }
            if (i == 1) {
                ++z2;
            }
            if (i == 2) {
                --x2;
            }
            if (i == 3) {
                ++x2;
            }
            final int texture = tt.getTexture(i + 2, data);
            final int n16 = (texture & 0xF) << 4;
            final int n17 = texture & 0xF0;
            if (this.noCulling || array[i]) {
                float n18;
                float n19;
                float n20;
                float n21;
                float n22;
                float n23;
                if (i == 0) {
                    n18 = waterHeight;
                    n19 = waterHeight4;
                    n20 = (float)x;
                    n21 = (float)(x + 1);
                    n22 = (float)z;
                    n23 = (float)z;
                }
                else if (i == 1) {
                    n18 = waterHeight3;
                    n19 = waterHeight2;
                    n20 = (float)(x + 1);
                    n21 = (float)x;
                    n22 = (float)(z + 1);
                    n23 = (float)(z + 1);
                }
                else if (i == 2) {
                    n18 = waterHeight2;
                    n19 = waterHeight;
                    n20 = (float)x;
                    n21 = (float)x;
                    n22 = (float)(z + 1);
                    n23 = (float)z;
                }
                else {
                    n18 = waterHeight4;
                    n19 = waterHeight3;
                    n20 = (float)(x + 1);
                    n21 = (float)(x + 1);
                    n22 = (float)z;
                    n23 = (float)(z + 1);
                }
                b = true;
                final double n24 = (n16 + 0) / 256.0f;
                final double n25 = (n16 + 16 - 0.01) / 256.0;
                final double v = (n17 + (1.0f - n18) * 16.0f) / 256.0f;
                final double v2 = (n17 + (1.0f - n19) * 16.0f) / 256.0f;
                final double n26 = (n17 + 16 - 0.01) / 256.0;
                final float brightness3 = tt.getBrightness(this.level, x2, y, z2);
                float n27;
                if (i < 2) {
                    n27 = brightness3 * n6;
                }
                else {
                    n27 = brightness3 * n7;
                }
                instance.color(n5 * n27 * n, n5 * n27 * n2, n5 * n27 * n3);
                instance.vertexUV(n20, y + n18, n22, n24, v);
                instance.vertexUV(n21, y + n19, n23, n25, v2);
                instance.vertexUV(n21, y + 0, n23, n25, n26);
                instance.vertexUV(n20, y + 0, n22, n24, n26);
            }
        }
        tt.yy0 = yy0;
        tt.yy1 = yy2;
        return b;
    }
    
    private float getWaterHeight(final int x, final int y, final int z, final Material m) {
        int n = 0;
        float n2 = 0.0f;
        for (int i = 0; i < 4; ++i) {
            final int x2 = x - (i & 0x1);
            final int z2 = z - (i >> 1 & 0x1);
            if (this.level.getMaterial(x2, y + 1, z2) == m) {
                return 1.0f;
            }
            final Material material = this.level.getMaterial(x2, y, z2);
            if (material == m) {
                final int data = this.level.getData(x2, y, z2);
                if (data >= 8 || data == 0) {
                    n2 += LiquidTile.getHeight(data) * 10.0f;
                    n += 10;
                }
                n2 += LiquidTile.getHeight(data);
                ++n;
            }
            else if (!material.isSolid()) {
                ++n2;
                ++n;
            }
        }
        return 1.0f - n2 / n;
    }
    
    public void renderBlock(final Tile tt, final Level level, final int x, final int y, final int z) {
        final float n = 0.5f;
        final float n2 = 1.0f;
        final float n3 = 0.8f;
        final float n4 = 0.6f;
        final Tesselator instance = Tesselator.instance;
        instance.begin();
        final float brightness = tt.getBrightness(level, x, y, z);
        float brightness2 = tt.getBrightness(level, x, y - 1, z);
        if (brightness2 < brightness) {
            brightness2 = brightness;
        }
        instance.color(n * brightness2, n * brightness2, n * brightness2);
        this.renderFaceUp(tt, -0.5, -0.5, -0.5, tt.getTexture(0));
        float brightness3 = tt.getBrightness(level, x, y + 1, z);
        if (brightness3 < brightness) {
            brightness3 = brightness;
        }
        instance.color(n2 * brightness3, n2 * brightness3, n2 * brightness3);
        this.renderFaceDown(tt, -0.5, -0.5, -0.5, tt.getTexture(1));
        float brightness4 = tt.getBrightness(level, x, y, z - 1);
        if (brightness4 < brightness) {
            brightness4 = brightness;
        }
        instance.color(n3 * brightness4, n3 * brightness4, n3 * brightness4);
        this.renderNorth(tt, -0.5, -0.5, -0.5, tt.getTexture(2));
        float brightness5 = tt.getBrightness(level, x, y, z + 1);
        if (brightness5 < brightness) {
            brightness5 = brightness;
        }
        instance.color(n3 * brightness5, n3 * brightness5, n3 * brightness5);
        this.renderSouth(tt, -0.5, -0.5, -0.5, tt.getTexture(3));
        float brightness6 = tt.getBrightness(level, x - 1, y, z);
        if (brightness6 < brightness) {
            brightness6 = brightness;
        }
        instance.color(n4 * brightness6, n4 * brightness6, n4 * brightness6);
        this.renderWest(tt, -0.5, -0.5, -0.5, tt.getTexture(4));
        float brightness7 = tt.getBrightness(level, x + 1, y, z);
        if (brightness7 < brightness) {
            brightness7 = brightness;
        }
        instance.color(n4 * brightness7, n4 * brightness7, n4 * brightness7);
        this.renderEast(tt, -0.5, -0.5, -0.5, tt.getTexture(5));
        instance.end();
    }
    
    public boolean tesselateBlockInWorld(final Tile tt, final int x, final int y, final int z) {
        final int color = tt.getColor(this.level, x, y, z);
        float n = (color >> 16 & 0xFF) / 255.0f;
        float n2 = (color >> 8 & 0xFF) / 255.0f;
        float n3 = (color & 0xFF) / 255.0f;
        if (GameRenderer.anaglyph3d) {
            final float n4 = (n * 30.0f + n2 * 59.0f + n3 * 11.0f) / 100.0f;
            final float n5 = (n * 30.0f + n2 * 70.0f) / 100.0f;
            final float n6 = (n * 30.0f + n3 * 70.0f) / 100.0f;
            n = n4;
            n2 = n5;
            n3 = n6;
        }
        if (Minecraft.useAmbientOcclusion()) {
            return this.tesselateBlockInWorldWithAmbienceOcclusionTexLighting(tt, x, y, z, n, n2, n3);
        }
        return this.tesselateBlockInWorld(tt, x, y, z, n, n2, n3);
    }
    
    public boolean tesselateBlockInWorldWithAmbienceOcclusionTexLighting(final Tile tt, int pX, int pY, int pZ, final float pBaseRed, final float pBaseGreen, final float pBaseBlue) {
        this.applyAmbienceOcclusion = true;
        boolean b = false;
        final float ll000 = this.ll000;
        final float ll2 = this.ll000;
        final float ll3 = this.ll000;
        final float ll4 = this.ll000;
        int n = 1;
        final boolean b2 = true;
        int n2 = 1;
        boolean b3 = true;
        boolean b4 = true;
        boolean b5 = true;
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
        if (tt.tex == 3) {
            n2 = (n = ((b3 = (b4 = (b5 = false))) ? 1 : 0));
        }
        if (this.fixedTexture >= 0) {
            n2 = (n = ((b3 = (b4 = (b5 = false))) ? 1 : 0));
        }
        if (this.noCulling || tt.isFaceVisible(this.level, pX, pY - 1, pZ, 0)) {
            float n3;
            float ll0y0;
            float n4;
            float n5;
            if (this.blsmooth > 0) {
                --pY;
                this.llxy0 = tt.getBrightness(this.level, pX - 1, pY, pZ);
                this.ll0yz = tt.getBrightness(this.level, pX, pY, pZ - 1);
                this.ll0yZ = tt.getBrightness(this.level, pX, pY, pZ + 1);
                this.llXy0 = tt.getBrightness(this.level, pX + 1, pY, pZ);
                if (this.llTrans0yz || this.llTransxy0) {
                    this.llxyz = tt.getBrightness(this.level, pX - 1, pY, pZ - 1);
                }
                else {
                    this.llxyz = this.llxy0;
                }
                if (this.llTrans0yZ || this.llTransxy0) {
                    this.llxyZ = tt.getBrightness(this.level, pX - 1, pY, pZ + 1);
                }
                else {
                    this.llxyZ = this.llxy0;
                }
                if (this.llTrans0yz || this.llTransXy0) {
                    this.llXyz = tt.getBrightness(this.level, pX + 1, pY, pZ - 1);
                }
                else {
                    this.llXyz = this.llXy0;
                }
                if (this.llTrans0yZ || this.llTransXy0) {
                    this.llXyZ = tt.getBrightness(this.level, pX + 1, pY, pZ + 1);
                }
                else {
                    this.llXyZ = this.llXy0;
                }
                ++pY;
                n3 = (this.llxyZ + this.llxy0 + this.ll0yZ + this.ll0y0) / 4.0f;
                ll0y0 = (this.ll0yZ + this.ll0y0 + this.llXyZ + this.llXy0) / 4.0f;
                n4 = (this.ll0y0 + this.ll0yz + this.llXy0 + this.llXyz) / 4.0f;
                n5 = (this.llxy0 + this.llxyz + this.ll0y0 + this.ll0yz) / 4.0f;
            }
            else {
                n5 = (n3 = (n4 = (ll0y0 = this.ll0y0)));
            }
            final float n6 = ((n != 0) ? pBaseRed : 1.0f) * 0.5f;
            this.c4r = n6;
            this.c3r = n6;
            this.c2r = n6;
            this.c1r = n6;
            final float n7 = ((n != 0) ? pBaseGreen : 1.0f) * 0.5f;
            this.c4g = n7;
            this.c3g = n7;
            this.c2g = n7;
            this.c1g = n7;
            final float n8 = ((n != 0) ? pBaseBlue : 1.0f) * 0.5f;
            this.c4b = n8;
            this.c3b = n8;
            this.c2b = n8;
            this.c1b = n8;
            this.c1r *= n3;
            this.c1g *= n3;
            this.c1b *= n3;
            this.c2r *= n5;
            this.c2g *= n5;
            this.c2b *= n5;
            this.c3r *= n4;
            this.c3g *= n4;
            this.c3b *= n4;
            this.c4r *= ll0y0;
            this.c4g *= ll0y0;
            this.c4b *= ll0y0;
            this.renderFaceUp(tt, pX, pY, pZ, tt.getTexture(this.level, pX, pY, pZ, 0));
            b = true;
        }
        if (this.noCulling || tt.isFaceVisible(this.level, pX, pY + 1, pZ, 1)) {
            float ll0Y0;
            float n9;
            float n10;
            float n11;
            if (this.blsmooth > 0) {
                ++pY;
                this.llxY0 = tt.getBrightness(this.level, pX - 1, pY, pZ);
                this.llXY0 = tt.getBrightness(this.level, pX + 1, pY, pZ);
                this.ll0Yz = tt.getBrightness(this.level, pX, pY, pZ - 1);
                this.ll0YZ = tt.getBrightness(this.level, pX, pY, pZ + 1);
                if (this.llTrans0Yz || this.llTransxY0) {
                    this.llxYz = tt.getBrightness(this.level, pX - 1, pY, pZ - 1);
                }
                else {
                    this.llxYz = this.llxY0;
                }
                if (this.llTrans0Yz || this.llTransXY0) {
                    this.llXYz = tt.getBrightness(this.level, pX + 1, pY, pZ - 1);
                }
                else {
                    this.llXYz = this.llXY0;
                }
                if (this.llTrans0YZ || this.llTransxY0) {
                    this.llxYZ = tt.getBrightness(this.level, pX - 1, pY, pZ + 1);
                }
                else {
                    this.llxYZ = this.llxY0;
                }
                if (this.llTrans0YZ || this.llTransXY0) {
                    this.llXYZ = tt.getBrightness(this.level, pX + 1, pY, pZ + 1);
                }
                else {
                    this.llXYZ = this.llXY0;
                }
                --pY;
                ll0Y0 = (this.llxYZ + this.llxY0 + this.ll0YZ + this.ll0Y0) / 4.0f;
                n9 = (this.ll0YZ + this.ll0Y0 + this.llXYZ + this.llXY0) / 4.0f;
                n10 = (this.ll0Y0 + this.ll0Yz + this.llXY0 + this.llXYz) / 4.0f;
                n11 = (this.llxY0 + this.llxYz + this.ll0Y0 + this.ll0Yz) / 4.0f;
            }
            else {
                n10 = (n9 = (n11 = (ll0Y0 = this.ll0Y0)));
            }
            float c1r;
            float c2r;
            float c3r;
            float c4r;
            if (b2) {
                c1r = pBaseRed;
                c2r = pBaseRed;
                c3r = pBaseRed;
                c4r = pBaseRed;
            }
            else {
                c3r = (c4r = (c2r = (c1r = 1.0f)));
            }
            this.c4r = c4r;
            this.c3r = c3r;
            this.c2r = c2r;
            this.c1r = c1r;
            float c1g;
            float c2g;
            float c3g;
            float c4g;
            if (b2) {
                c1g = pBaseGreen;
                c2g = pBaseGreen;
                c3g = pBaseGreen;
                c4g = pBaseGreen;
            }
            else {
                c3g = (c4g = (c2g = (c1g = 1.0f)));
            }
            this.c4g = c4g;
            this.c3g = c3g;
            this.c2g = c2g;
            this.c1g = c1g;
            float c1b;
            float c2b;
            float c3b;
            float c4b;
            if (b2) {
                c1b = pBaseBlue;
                c2b = pBaseBlue;
                c3b = pBaseBlue;
                c4b = pBaseBlue;
            }
            else {
                c3b = (c4b = (c2b = (c1b = 1.0f)));
            }
            this.c4b = c4b;
            this.c3b = c3b;
            this.c2b = c2b;
            this.c1b = c1b;
            this.c1r *= n9;
            this.c1g *= n9;
            this.c1b *= n9;
            this.c2r *= n10;
            this.c2g *= n10;
            this.c2b *= n10;
            this.c3r *= n11;
            this.c3g *= n11;
            this.c3b *= n11;
            this.c4r *= ll0Y0;
            this.c4g *= ll0Y0;
            this.c4b *= ll0Y0;
            this.renderFaceDown(tt, pX, pY, pZ, tt.getTexture(this.level, pX, pY, pZ, 1));
            b = true;
        }
        if (this.noCulling || tt.isFaceVisible(this.level, pX, pY, pZ - 1, 2)) {
            float n12;
            float n13;
            float n14;
            float ll00z;
            if (this.blsmooth > 0) {
                --pZ;
                this.llx0z = tt.getBrightness(this.level, pX - 1, pY, pZ);
                this.ll0yz = tt.getBrightness(this.level, pX, pY - 1, pZ);
                this.ll0Yz = tt.getBrightness(this.level, pX, pY + 1, pZ);
                this.llX0z = tt.getBrightness(this.level, pX + 1, pY, pZ);
                if (this.llTransx0z || this.llTrans0yz) {
                    this.llxyz = tt.getBrightness(this.level, pX - 1, pY - 1, pZ);
                }
                else {
                    this.llxyz = this.llx0z;
                }
                if (this.llTransx0z || this.llTrans0Yz) {
                    this.llxYz = tt.getBrightness(this.level, pX - 1, pY + 1, pZ);
                }
                else {
                    this.llxYz = this.llx0z;
                }
                if (this.llTransX0z || this.llTrans0yz) {
                    this.llXyz = tt.getBrightness(this.level, pX + 1, pY - 1, pZ);
                }
                else {
                    this.llXyz = this.llX0z;
                }
                if (this.llTransX0z || this.llTrans0Yz) {
                    this.llXYz = tt.getBrightness(this.level, pX + 1, pY + 1, pZ);
                }
                else {
                    this.llXYz = this.llX0z;
                }
                ++pZ;
                n12 = (this.llx0z + this.llxYz + this.ll00z + this.ll0Yz) / 4.0f;
                n13 = (this.ll00z + this.ll0Yz + this.llX0z + this.llXYz) / 4.0f;
                n14 = (this.ll0yz + this.ll00z + this.llXyz + this.llX0z) / 4.0f;
                ll00z = (this.llxyz + this.llx0z + this.ll0yz + this.ll00z) / 4.0f;
            }
            else {
                n13 = (n12 = (n14 = (ll00z = this.ll00z)));
            }
            final float n15 = ((n2 != 0) ? pBaseRed : 1.0f) * 0.8f;
            this.c4r = n15;
            this.c3r = n15;
            this.c2r = n15;
            this.c1r = n15;
            final float n16 = ((n2 != 0) ? pBaseGreen : 1.0f) * 0.8f;
            this.c4g = n16;
            this.c3g = n16;
            this.c2g = n16;
            this.c1g = n16;
            final float n17 = ((n2 != 0) ? pBaseBlue : 1.0f) * 0.8f;
            this.c4b = n17;
            this.c3b = n17;
            this.c2b = n17;
            this.c1b = n17;
            this.c1r *= n12;
            this.c1g *= n12;
            this.c1b *= n12;
            this.c2r *= n13;
            this.c2g *= n13;
            this.c2b *= n13;
            this.c3r *= n14;
            this.c3g *= n14;
            this.c3b *= n14;
            this.c4r *= ll00z;
            this.c4g *= ll00z;
            this.c4b *= ll00z;
            final int texture = tt.getTexture(this.level, pX, pY, pZ, 2);
            this.renderNorth(tt, pX, pY, pZ, texture);
            if (TileRenderer.fancy && texture == 3 && this.fixedTexture < 0) {
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
            b = true;
        }
        if (this.noCulling || tt.isFaceVisible(this.level, pX, pY, pZ + 1, 3)) {
            float n18;
            float ll00Z;
            float n19;
            float n20;
            if (this.blsmooth > 0) {
                ++pZ;
                this.llx0Z = tt.getBrightness(this.level, pX - 1, pY, pZ);
                this.llX0Z = tt.getBrightness(this.level, pX + 1, pY, pZ);
                this.ll0yZ = tt.getBrightness(this.level, pX, pY - 1, pZ);
                this.ll0YZ = tt.getBrightness(this.level, pX, pY + 1, pZ);
                if (this.llTransx0Z || this.llTrans0yZ) {
                    this.llxyZ = tt.getBrightness(this.level, pX - 1, pY - 1, pZ);
                }
                else {
                    this.llxyZ = this.llx0Z;
                }
                if (this.llTransx0Z || this.llTrans0YZ) {
                    this.llxYZ = tt.getBrightness(this.level, pX - 1, pY + 1, pZ);
                }
                else {
                    this.llxYZ = this.llx0Z;
                }
                if (this.llTransX0Z || this.llTrans0yZ) {
                    this.llXyZ = tt.getBrightness(this.level, pX + 1, pY - 1, pZ);
                }
                else {
                    this.llXyZ = this.llX0Z;
                }
                if (this.llTransX0Z || this.llTrans0YZ) {
                    this.llXYZ = tt.getBrightness(this.level, pX + 1, pY + 1, pZ);
                }
                else {
                    this.llXYZ = this.llX0Z;
                }
                --pZ;
                n18 = (this.llx0Z + this.llxYZ + this.ll00Z + this.ll0YZ) / 4.0f;
                ll00Z = (this.ll00Z + this.ll0YZ + this.llX0Z + this.llXYZ) / 4.0f;
                n19 = (this.ll0yZ + this.ll00Z + this.llXyZ + this.llX0Z) / 4.0f;
                n20 = (this.llxyZ + this.llx0Z + this.ll0yZ + this.ll00Z) / 4.0f;
            }
            else {
                n20 = (n18 = (n19 = (ll00Z = this.ll00Z)));
            }
            final float n21 = (b3 ? pBaseRed : 1.0f) * 0.8f;
            this.c4r = n21;
            this.c3r = n21;
            this.c2r = n21;
            this.c1r = n21;
            final float n22 = (b3 ? pBaseGreen : 1.0f) * 0.8f;
            this.c4g = n22;
            this.c3g = n22;
            this.c2g = n22;
            this.c1g = n22;
            final float n23 = (b3 ? pBaseBlue : 1.0f) * 0.8f;
            this.c4b = n23;
            this.c3b = n23;
            this.c2b = n23;
            this.c1b = n23;
            this.c1r *= n18;
            this.c1g *= n18;
            this.c1b *= n18;
            this.c2r *= n20;
            this.c2g *= n20;
            this.c2b *= n20;
            this.c3r *= n19;
            this.c3g *= n19;
            this.c3b *= n19;
            this.c4r *= ll00Z;
            this.c4g *= ll00Z;
            this.c4b *= ll00Z;
            final int texture2 = tt.getTexture(this.level, pX, pY, pZ, 3);
            this.renderSouth(tt, pX, pY, pZ, tt.getTexture(this.level, pX, pY, pZ, 3));
            if (TileRenderer.fancy && texture2 == 3 && this.fixedTexture < 0) {
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
            b = true;
        }
        if (this.noCulling || tt.isFaceVisible(this.level, pX - 1, pY, pZ, 4)) {
            float llx00;
            float n24;
            float n25;
            float n26;
            if (this.blsmooth > 0) {
                --pX;
                this.llxy0 = tt.getBrightness(this.level, pX, pY - 1, pZ);
                this.llx0z = tt.getBrightness(this.level, pX, pY, pZ - 1);
                this.llx0Z = tt.getBrightness(this.level, pX, pY, pZ + 1);
                this.llxY0 = tt.getBrightness(this.level, pX, pY + 1, pZ);
                if (this.llTransx0z || this.llTransxy0) {
                    this.llxyz = tt.getBrightness(this.level, pX, pY - 1, pZ - 1);
                }
                else {
                    this.llxyz = this.llx0z;
                }
                if (this.llTransx0Z || this.llTransxy0) {
                    this.llxyZ = tt.getBrightness(this.level, pX, pY - 1, pZ + 1);
                }
                else {
                    this.llxyZ = this.llx0Z;
                }
                if (this.llTransx0z || this.llTransxY0) {
                    this.llxYz = tt.getBrightness(this.level, pX, pY + 1, pZ - 1);
                }
                else {
                    this.llxYz = this.llx0z;
                }
                if (this.llTransx0Z || this.llTransxY0) {
                    this.llxYZ = tt.getBrightness(this.level, pX, pY + 1, pZ + 1);
                }
                else {
                    this.llxYZ = this.llx0Z;
                }
                ++pX;
                llx00 = (this.llxy0 + this.llxyZ + this.llx00 + this.llx0Z) / 4.0f;
                n24 = (this.llx00 + this.llx0Z + this.llxY0 + this.llxYZ) / 4.0f;
                n25 = (this.llx0z + this.llx00 + this.llxYz + this.llxY0) / 4.0f;
                n26 = (this.llxyz + this.llxy0 + this.llx0z + this.llx00) / 4.0f;
            }
            else {
                n25 = (n24 = (n26 = (llx00 = this.llx00)));
            }
            final float n27 = (b4 ? pBaseRed : 1.0f) * 0.6f;
            this.c4r = n27;
            this.c3r = n27;
            this.c2r = n27;
            this.c1r = n27;
            final float n28 = (b4 ? pBaseGreen : 1.0f) * 0.6f;
            this.c4g = n28;
            this.c3g = n28;
            this.c2g = n28;
            this.c1g = n28;
            final float n29 = (b4 ? pBaseBlue : 1.0f) * 0.6f;
            this.c4b = n29;
            this.c3b = n29;
            this.c2b = n29;
            this.c1b = n29;
            this.c1r *= n24;
            this.c1g *= n24;
            this.c1b *= n24;
            this.c2r *= n25;
            this.c2g *= n25;
            this.c2b *= n25;
            this.c3r *= n26;
            this.c3g *= n26;
            this.c3b *= n26;
            this.c4r *= llx00;
            this.c4g *= llx00;
            this.c4b *= llx00;
            final int texture3 = tt.getTexture(this.level, pX, pY, pZ, 4);
            this.renderWest(tt, pX, pY, pZ, texture3);
            if (TileRenderer.fancy && texture3 == 3 && this.fixedTexture < 0) {
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
            b = true;
        }
        if (this.noCulling || tt.isFaceVisible(this.level, pX + 1, pY, pZ, 5)) {
            float n30;
            float llX00;
            float n31;
            float n32;
            if (this.blsmooth > 0) {
                ++pX;
                this.llXy0 = tt.getBrightness(this.level, pX, pY - 1, pZ);
                this.llX0z = tt.getBrightness(this.level, pX, pY, pZ - 1);
                this.llX0Z = tt.getBrightness(this.level, pX, pY, pZ + 1);
                this.llXY0 = tt.getBrightness(this.level, pX, pY + 1, pZ);
                if (this.llTransXy0 || this.llTransX0z) {
                    this.llXyz = tt.getBrightness(this.level, pX, pY - 1, pZ - 1);
                }
                else {
                    this.llXyz = this.llX0z;
                }
                if (this.llTransXy0 || this.llTransX0Z) {
                    this.llXyZ = tt.getBrightness(this.level, pX, pY - 1, pZ + 1);
                }
                else {
                    this.llXyZ = this.llX0Z;
                }
                if (this.llTransXY0 || this.llTransX0z) {
                    this.llXYz = tt.getBrightness(this.level, pX, pY + 1, pZ - 1);
                }
                else {
                    this.llXYz = this.llX0z;
                }
                if (this.llTransXY0 || this.llTransX0Z) {
                    this.llXYZ = tt.getBrightness(this.level, pX, pY + 1, pZ + 1);
                }
                else {
                    this.llXYZ = this.llX0Z;
                }
                --pX;
                n30 = (this.llXy0 + this.llXyZ + this.llX00 + this.llX0Z) / 4.0f;
                llX00 = (this.llX00 + this.llX0Z + this.llXY0 + this.llXYZ) / 4.0f;
                n31 = (this.llX0z + this.llX00 + this.llXYz + this.llXY0) / 4.0f;
                n32 = (this.llXyz + this.llXy0 + this.llX0z + this.llX00) / 4.0f;
            }
            else {
                n32 = (n30 = (n31 = (llX00 = this.llX00)));
            }
            final float n33 = (b5 ? pBaseRed : 1.0f) * 0.6f;
            this.c4r = n33;
            this.c3r = n33;
            this.c2r = n33;
            this.c1r = n33;
            final float n34 = (b5 ? pBaseGreen : 1.0f) * 0.6f;
            this.c4g = n34;
            this.c3g = n34;
            this.c2g = n34;
            this.c1g = n34;
            final float n35 = (b5 ? pBaseBlue : 1.0f) * 0.6f;
            this.c4b = n35;
            this.c3b = n35;
            this.c2b = n35;
            this.c1b = n35;
            this.c1r *= n30;
            this.c1g *= n30;
            this.c1b *= n30;
            this.c2r *= n32;
            this.c2g *= n32;
            this.c2b *= n32;
            this.c3r *= n31;
            this.c3g *= n31;
            this.c3b *= n31;
            this.c4r *= llX00;
            this.c4g *= llX00;
            this.c4b *= llX00;
            final int texture4 = tt.getTexture(this.level, pX, pY, pZ, 5);
            this.renderEast(tt, pX, pY, pZ, texture4);
            if (TileRenderer.fancy && texture4 == 3 && this.fixedTexture < 0) {
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
            b = true;
        }
        this.applyAmbienceOcclusion = false;
        return b;
    }
    
    public boolean tesselateBlockInWorld(final Tile tt, final int x, final int y, final int z, final float r, final float g, final float b) {
        this.applyAmbienceOcclusion = false;
        final Tesselator instance = Tesselator.instance;
        boolean b2 = false;
        final float n = 0.5f;
        final float n2 = 1.0f;
        final float n3 = 0.8f;
        final float n4 = 0.6f;
        final float n5 = n2 * r;
        final float n6 = n2 * g;
        final float n7 = n2 * b;
        float n8 = n;
        float n9 = n3;
        float n10 = n4;
        float n11 = n;
        float n12 = n3;
        float n13 = n4;
        float n14 = n;
        float n15 = n3;
        float n16 = n4;
        if (tt != Tile.grass) {
            n8 *= r;
            n9 *= r;
            n10 *= r;
            n11 *= g;
            n12 *= g;
            n13 *= g;
            n14 *= b;
            n15 *= b;
            n16 *= b;
        }
        final float brightness = tt.getBrightness(this.level, x, y, z);
        if (this.noCulling || tt.isFaceVisible(this.level, x, y - 1, z, 0)) {
            final float brightness2 = tt.getBrightness(this.level, x, y - 1, z);
            instance.color(n8 * brightness2, n11 * brightness2, n14 * brightness2);
            this.renderFaceUp(tt, x, y, z, tt.getTexture(this.level, x, y, z, 0));
            b2 = true;
        }
        if (this.noCulling || tt.isFaceVisible(this.level, x, y + 1, z, 1)) {
            float brightness3 = tt.getBrightness(this.level, x, y + 1, z);
            if (tt.yy1 != 1.0 && !tt.material.isLiquid()) {
                brightness3 = brightness;
            }
            instance.color(n5 * brightness3, n6 * brightness3, n7 * brightness3);
            this.renderFaceDown(tt, x, y, z, tt.getTexture(this.level, x, y, z, 1));
            b2 = true;
        }
        if (this.noCulling || tt.isFaceVisible(this.level, x, y, z - 1, 2)) {
            float brightness4 = tt.getBrightness(this.level, x, y, z - 1);
            if (tt.zz0 > 0.0) {
                brightness4 = brightness;
            }
            instance.color(n9 * brightness4, n12 * brightness4, n15 * brightness4);
            final int texture = tt.getTexture(this.level, x, y, z, 2);
            this.renderNorth(tt, x, y, z, texture);
            if (TileRenderer.fancy && texture == 3 && this.fixedTexture < 0) {
                instance.color(n9 * brightness4 * r, n12 * brightness4 * g, n15 * brightness4 * b);
                this.renderNorth(tt, x, y, z, 38);
            }
            b2 = true;
        }
        if (this.noCulling || tt.isFaceVisible(this.level, x, y, z + 1, 3)) {
            float brightness5 = tt.getBrightness(this.level, x, y, z + 1);
            if (tt.zz1 < 1.0) {
                brightness5 = brightness;
            }
            instance.color(n9 * brightness5, n12 * brightness5, n15 * brightness5);
            final int texture2 = tt.getTexture(this.level, x, y, z, 3);
            this.renderSouth(tt, x, y, z, texture2);
            if (TileRenderer.fancy && texture2 == 3 && this.fixedTexture < 0) {
                instance.color(n9 * brightness5 * r, n12 * brightness5 * g, n15 * brightness5 * b);
                this.renderSouth(tt, x, y, z, 38);
            }
            b2 = true;
        }
        if (this.noCulling || tt.isFaceVisible(this.level, x - 1, y, z, 4)) {
            float brightness6 = tt.getBrightness(this.level, x - 1, y, z);
            if (tt.xx0 > 0.0) {
                brightness6 = brightness;
            }
            instance.color(n10 * brightness6, n13 * brightness6, n16 * brightness6);
            final int texture3 = tt.getTexture(this.level, x, y, z, 4);
            this.renderWest(tt, x, y, z, texture3);
            if (TileRenderer.fancy && texture3 == 3 && this.fixedTexture < 0) {
                instance.color(n10 * brightness6 * r, n13 * brightness6 * g, n16 * brightness6 * b);
                this.renderWest(tt, x, y, z, 38);
            }
            b2 = true;
        }
        if (this.noCulling || tt.isFaceVisible(this.level, x + 1, y, z, 5)) {
            float brightness7 = tt.getBrightness(this.level, x + 1, y, z);
            if (tt.xx1 < 1.0) {
                brightness7 = brightness;
            }
            instance.color(n10 * brightness7, n13 * brightness7, n16 * brightness7);
            final int texture4 = tt.getTexture(this.level, x, y, z, 5);
            this.renderEast(tt, x, y, z, texture4);
            if (TileRenderer.fancy && texture4 == 3 && this.fixedTexture < 0) {
                instance.color(n10 * brightness7 * r, n13 * brightness7 * g, n16 * brightness7 * b);
                this.renderEast(tt, x, y, z, 38);
            }
            b2 = true;
        }
        return b2;
    }
    
    public boolean tesselateCactusInWorld(final Tile tt, final int x, final int y, final int z) {
        final int color = tt.getColor(this.level, x, y, z);
        float r = (color >> 16 & 0xFF) / 255.0f;
        float g = (color >> 8 & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        if (GameRenderer.anaglyph3d) {
            final float n = (r * 30.0f + g * 59.0f + b * 11.0f) / 100.0f;
            final float n2 = (r * 30.0f + g * 70.0f) / 100.0f;
            final float n3 = (r * 30.0f + b * 70.0f) / 100.0f;
            r = n;
            g = n2;
            b = n3;
        }
        return this.tesselateCactusInWorld(tt, x, y, z, r, g, b);
    }
    
    public boolean tesselateCactusInWorld(final Tile tt, final int x, final int y, final int z, final float r, final float g, final float b) {
        final Tesselator instance = Tesselator.instance;
        boolean b2 = false;
        final float n = 0.5f;
        final float n2 = 1.0f;
        final float n3 = 0.8f;
        final float n4 = 0.6f;
        final float n5 = n * r;
        final float n6 = n2 * r;
        final float n7 = n3 * r;
        final float n8 = n4 * r;
        final float n9 = n * g;
        final float n10 = n2 * g;
        final float n11 = n3 * g;
        final float n12 = n4 * g;
        final float n13 = n * b;
        final float n14 = n2 * b;
        final float n15 = n3 * b;
        final float n16 = n4 * b;
        final float n17 = 0.0625f;
        final float brightness = tt.getBrightness(this.level, x, y, z);
        if (this.noCulling || tt.isFaceVisible(this.level, x, y - 1, z, 0)) {
            final float brightness2 = tt.getBrightness(this.level, x, y - 1, z);
            instance.color(n5 * brightness2, n9 * brightness2, n13 * brightness2);
            this.renderFaceUp(tt, x, y, z, tt.getTexture(this.level, x, y, z, 0));
            b2 = true;
        }
        if (this.noCulling || tt.isFaceVisible(this.level, x, y + 1, z, 1)) {
            float brightness3 = tt.getBrightness(this.level, x, y + 1, z);
            if (tt.yy1 != 1.0 && !tt.material.isLiquid()) {
                brightness3 = brightness;
            }
            instance.color(n6 * brightness3, n10 * brightness3, n14 * brightness3);
            this.renderFaceDown(tt, x, y, z, tt.getTexture(this.level, x, y, z, 1));
            b2 = true;
        }
        if (this.noCulling || tt.isFaceVisible(this.level, x, y, z - 1, 2)) {
            float brightness4 = tt.getBrightness(this.level, x, y, z - 1);
            if (tt.zz0 > 0.0) {
                brightness4 = brightness;
            }
            instance.color(n7 * brightness4, n11 * brightness4, n15 * brightness4);
            instance.addOffset(0.0f, 0.0f, n17);
            this.renderNorth(tt, x, y, z, tt.getTexture(this.level, x, y, z, 2));
            instance.addOffset(0.0f, 0.0f, -n17);
            b2 = true;
        }
        if (this.noCulling || tt.isFaceVisible(this.level, x, y, z + 1, 3)) {
            float brightness5 = tt.getBrightness(this.level, x, y, z + 1);
            if (tt.zz1 < 1.0) {
                brightness5 = brightness;
            }
            instance.color(n7 * brightness5, n11 * brightness5, n15 * brightness5);
            instance.addOffset(0.0f, 0.0f, -n17);
            this.renderSouth(tt, x, y, z, tt.getTexture(this.level, x, y, z, 3));
            instance.addOffset(0.0f, 0.0f, n17);
            b2 = true;
        }
        if (this.noCulling || tt.isFaceVisible(this.level, x - 1, y, z, 4)) {
            float brightness6 = tt.getBrightness(this.level, x - 1, y, z);
            if (tt.xx0 > 0.0) {
                brightness6 = brightness;
            }
            instance.color(n8 * brightness6, n12 * brightness6, n16 * brightness6);
            instance.addOffset(n17, 0.0f, 0.0f);
            this.renderWest(tt, x, y, z, tt.getTexture(this.level, x, y, z, 4));
            instance.addOffset(-n17, 0.0f, 0.0f);
            b2 = true;
        }
        if (this.noCulling || tt.isFaceVisible(this.level, x + 1, y, z, 5)) {
            float brightness7 = tt.getBrightness(this.level, x + 1, y, z);
            if (tt.xx1 < 1.0) {
                brightness7 = brightness;
            }
            instance.color(n8 * brightness7, n12 * brightness7, n16 * brightness7);
            instance.addOffset(-n17, 0.0f, 0.0f);
            this.renderEast(tt, x, y, z, tt.getTexture(this.level, x, y, z, 5));
            instance.addOffset(n17, 0.0f, 0.0f);
            b2 = true;
        }
        return b2;
    }
    
    public boolean tesselateFenceInWorld(final Tile tt, final int x, final int y, final int z) {
        final float n = 0.375f;
        final float n2 = 0.625f;
        tt.setShape(n, 0.0f, n, n2, 1.0f, n2);
        this.tesselateBlockInWorld(tt, x, y, z);
        boolean b = true;
        int n3 = 0;
        boolean b2 = false;
        if (this.level.getTile(x - 1, y, z) == tt.id || this.level.getTile(x + 1, y, z) == tt.id) {
            n3 = 1;
        }
        if (this.level.getTile(x, y, z - 1) == tt.id || this.level.getTile(x, y, z + 1) == tt.id) {
            b2 = true;
        }
        final boolean b3 = this.level.getTile(x - 1, y, z) == tt.id;
        final boolean b4 = this.level.getTile(x + 1, y, z) == tt.id;
        final boolean b5 = this.level.getTile(x, y, z - 1) == tt.id;
        final boolean b6 = this.level.getTile(x, y, z + 1) == tt.id;
        if (n3 == 0 && !b2) {
            n3 = 1;
        }
        final float n4 = 0.4375f;
        final float n5 = 0.5625f;
        final float n6 = 0.75f;
        final float n7 = 0.9375f;
        final float n8 = b3 ? 0.0f : n4;
        final float n9 = b4 ? 1.0f : n5;
        final float n10 = b5 ? 0.0f : n4;
        final float n11 = b6 ? 1.0f : n5;
        if (n3 != 0) {
            tt.setShape(n8, n6, n4, n9, n7, n5);
            this.tesselateBlockInWorld(tt, x, y, z);
            b = true;
        }
        if (b2) {
            tt.setShape(n4, n6, n10, n5, n7, n11);
            this.tesselateBlockInWorld(tt, x, y, z);
            b = true;
        }
        final float n12 = 0.375f;
        final float n13 = 0.5625f;
        if (n3 != 0) {
            tt.setShape(n8, n12, n4, n9, n13, n5);
            this.tesselateBlockInWorld(tt, x, y, z);
            b = true;
        }
        if (b2) {
            tt.setShape(n4, n12, n10, n5, n13, n11);
            this.tesselateBlockInWorld(tt, x, y, z);
            b = true;
        }
        tt.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        return b;
    }
    
    public boolean tesselateStairsInWorld(final Tile tt, final int x, final int y, final int z) {
        boolean b = false;
        final int data = this.level.getData(x, y, z);
        if (data == 0) {
            tt.setShape(0.0f, 0.0f, 0.0f, 0.5f, 0.5f, 1.0f);
            this.tesselateBlockInWorld(tt, x, y, z);
            tt.setShape(0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
            this.tesselateBlockInWorld(tt, x, y, z);
            b = true;
        }
        else if (data == 1) {
            tt.setShape(0.0f, 0.0f, 0.0f, 0.5f, 1.0f, 1.0f);
            this.tesselateBlockInWorld(tt, x, y, z);
            tt.setShape(0.5f, 0.0f, 0.0f, 1.0f, 0.5f, 1.0f);
            this.tesselateBlockInWorld(tt, x, y, z);
            b = true;
        }
        else if (data == 2) {
            tt.setShape(0.0f, 0.0f, 0.0f, 1.0f, 0.5f, 0.5f);
            this.tesselateBlockInWorld(tt, x, y, z);
            tt.setShape(0.0f, 0.0f, 0.5f, 1.0f, 1.0f, 1.0f);
            this.tesselateBlockInWorld(tt, x, y, z);
            b = true;
        }
        else if (data == 3) {
            tt.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.5f);
            this.tesselateBlockInWorld(tt, x, y, z);
            tt.setShape(0.0f, 0.0f, 0.5f, 1.0f, 0.5f, 1.0f);
            this.tesselateBlockInWorld(tt, x, y, z);
            b = true;
        }
        tt.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        return b;
    }
    
    public boolean tesselateDoorInWorld(final Tile tt, final int x, final int y, final int z) {
        final Tesselator instance = Tesselator.instance;
        final DoorTile doorTile = (DoorTile)tt;
        final float n = 0.5f;
        final float n2 = 1.0f;
        final float n3 = 0.8f;
        final float n4 = 0.6f;
        final float brightness = tt.getBrightness(this.level, x, y, z);
        float brightness2 = tt.getBrightness(this.level, x, y - 1, z);
        if (doorTile.yy0 > 0.0) {
            brightness2 = brightness;
        }
        if (Tile.lightEmission[tt.id] > 0) {
            brightness2 = 1.0f;
        }
        instance.color(n * brightness2, n * brightness2, n * brightness2);
        this.renderFaceUp(tt, x, y, z, tt.getTexture(this.level, x, y, z, 0));
        float brightness3 = tt.getBrightness(this.level, x, y + 1, z);
        if (doorTile.yy1 < 1.0) {
            brightness3 = brightness;
        }
        if (Tile.lightEmission[tt.id] > 0) {
            brightness3 = 1.0f;
        }
        instance.color(n2 * brightness3, n2 * brightness3, n2 * brightness3);
        this.renderFaceDown(tt, x, y, z, tt.getTexture(this.level, x, y, z, 1));
        float brightness4 = tt.getBrightness(this.level, x, y, z - 1);
        if (doorTile.zz0 > 0.0) {
            brightness4 = brightness;
        }
        if (Tile.lightEmission[tt.id] > 0) {
            brightness4 = 1.0f;
        }
        instance.color(n3 * brightness4, n3 * brightness4, n3 * brightness4);
        int texture = tt.getTexture(this.level, x, y, z, 2);
        if (texture < 0) {
            this.xFlipTexture = true;
            texture = -texture;
        }
        this.renderNorth(tt, x, y, z, texture);
        this.xFlipTexture = false;
        float brightness5 = tt.getBrightness(this.level, x, y, z + 1);
        if (doorTile.zz1 < 1.0) {
            brightness5 = brightness;
        }
        if (Tile.lightEmission[tt.id] > 0) {
            brightness5 = 1.0f;
        }
        instance.color(n3 * brightness5, n3 * brightness5, n3 * brightness5);
        int texture2 = tt.getTexture(this.level, x, y, z, 3);
        if (texture2 < 0) {
            this.xFlipTexture = true;
            texture2 = -texture2;
        }
        this.renderSouth(tt, x, y, z, texture2);
        this.xFlipTexture = false;
        float brightness6 = tt.getBrightness(this.level, x - 1, y, z);
        if (doorTile.xx0 > 0.0) {
            brightness6 = brightness;
        }
        if (Tile.lightEmission[tt.id] > 0) {
            brightness6 = 1.0f;
        }
        instance.color(n4 * brightness6, n4 * brightness6, n4 * brightness6);
        int texture3 = tt.getTexture(this.level, x, y, z, 4);
        if (texture3 < 0) {
            this.xFlipTexture = true;
            texture3 = -texture3;
        }
        this.renderWest(tt, x, y, z, texture3);
        this.xFlipTexture = false;
        float brightness7 = tt.getBrightness(this.level, x + 1, y, z);
        if (doorTile.xx1 < 1.0) {
            brightness7 = brightness;
        }
        if (Tile.lightEmission[tt.id] > 0) {
            brightness7 = 1.0f;
        }
        instance.color(n4 * brightness7, n4 * brightness7, n4 * brightness7);
        int texture4 = tt.getTexture(this.level, x, y, z, 5);
        if (texture4 < 0) {
            this.xFlipTexture = true;
            texture4 = -texture4;
        }
        this.renderEast(tt, x, y, z, texture4);
        final boolean b = true;
        this.xFlipTexture = false;
        return b;
    }
    
    public void renderFaceUp(final Tile tt, final double x, final double y, final double z, int tex) {
        final Tesselator instance = Tesselator.instance;
        if (this.fixedTexture >= 0) {
            tex = this.fixedTexture;
        }
        final int n = (tex & 0xF) << 4;
        final int n2 = tex & 0xF0;
        double n3 = (n + tt.xx0 * 16.0) / 256.0;
        double n4 = (n + tt.xx1 * 16.0 - 0.01) / 256.0;
        double n5 = (n2 + tt.zz0 * 16.0) / 256.0;
        double n6 = (n2 + tt.zz1 * 16.0 - 0.01) / 256.0;
        if (tt.xx0 < 0.0 || tt.xx1 > 1.0) {
            n3 = (n + 0.0f) / 256.0f;
            n4 = (n + 15.99f) / 256.0f;
        }
        if (tt.zz0 < 0.0 || tt.zz1 > 1.0) {
            n5 = (n2 + 0.0f) / 256.0f;
            n6 = (n2 + 15.99f) / 256.0f;
        }
        double n7 = n4;
        double n8 = n3;
        double n9 = n5;
        double n10 = n6;
        if (this.downFlip == 2) {
            n3 = (n + tt.zz0 * 16.0) / 256.0;
            final double n11 = (n2 + 16 - tt.xx1 * 16.0) / 256.0;
            n4 = (n + tt.zz1 * 16.0) / 256.0;
            final double n12 = (n2 + 16 - tt.xx0 * 16.0) / 256.0;
            n9 = n11;
            n10 = n12;
            n7 = n3;
            n8 = n4;
            n5 = n12;
            n6 = n9;
        }
        else if (this.downFlip == 1) {
            final double n13 = (n + 16 - tt.zz1 * 16.0) / 256.0;
            n5 = (n2 + tt.xx0 * 16.0) / 256.0;
            final double n14 = (n + 16 - tt.zz0 * 16.0) / 256.0;
            n6 = (n2 + tt.xx1 * 16.0) / 256.0;
            n7 = n14;
            n8 = n13;
            n3 = n7;
            n4 = n8;
            n9 = n6;
            n10 = n5;
        }
        else if (this.downFlip == 3) {
            n3 = (n + 16 - tt.xx0 * 16.0) / 256.0;
            n4 = (n + 16 - tt.xx1 * 16.0 - 0.01) / 256.0;
            n5 = (n2 + 16 - tt.zz0 * 16.0) / 256.0;
            n6 = (n2 + 16 - tt.zz1 * 16.0 - 0.01) / 256.0;
            n7 = n4;
            n8 = n3;
            n9 = n5;
            n10 = n6;
        }
        final double n15 = x + tt.xx0;
        final double n16 = x + tt.xx1;
        final double n17 = y + tt.yy0;
        final double n18 = z + tt.zz0;
        final double n19 = z + tt.zz1;
        if (this.applyAmbienceOcclusion) {
            instance.color(this.c1r, this.c1g, this.c1b);
            instance.vertexUV(n15, n17, n19, n8, n10);
            instance.color(this.c2r, this.c2g, this.c2b);
            instance.vertexUV(n15, n17, n18, n3, n5);
            instance.color(this.c3r, this.c3g, this.c3b);
            instance.vertexUV(n16, n17, n18, n7, n9);
            instance.color(this.c4r, this.c4g, this.c4b);
            instance.vertexUV(n16, n17, n19, n4, n6);
        }
        else {
            instance.vertexUV(n15, n17, n19, n8, n10);
            instance.vertexUV(n15, n17, n18, n3, n5);
            instance.vertexUV(n16, n17, n18, n7, n9);
            instance.vertexUV(n16, n17, n19, n4, n6);
        }
    }
    
    public void renderFaceDown(final Tile tt, final double x, final double y, final double z, int tex) {
        final Tesselator instance = Tesselator.instance;
        if (this.fixedTexture >= 0) {
            tex = this.fixedTexture;
        }
        final int n = (tex & 0xF) << 4;
        final int n2 = tex & 0xF0;
        double n3 = (n + tt.xx0 * 16.0) / 256.0;
        double n4 = (n + tt.xx1 * 16.0 - 0.01) / 256.0;
        double n5 = (n2 + tt.zz0 * 16.0) / 256.0;
        double n6 = (n2 + tt.zz1 * 16.0 - 0.01) / 256.0;
        if (tt.xx0 < 0.0 || tt.xx1 > 1.0) {
            n3 = (n + 0.0f) / 256.0f;
            n4 = (n + 15.99f) / 256.0f;
        }
        if (tt.zz0 < 0.0 || tt.zz1 > 1.0) {
            n5 = (n2 + 0.0f) / 256.0f;
            n6 = (n2 + 15.99f) / 256.0f;
        }
        double n7 = n4;
        double n8 = n3;
        double n9 = n5;
        double n10 = n6;
        if (this.upFlip == 1) {
            n3 = (n + tt.zz0 * 16.0) / 256.0;
            final double n11 = (n2 + 16 - tt.xx1 * 16.0) / 256.0;
            n4 = (n + tt.zz1 * 16.0) / 256.0;
            final double n12 = (n2 + 16 - tt.xx0 * 16.0) / 256.0;
            n9 = n11;
            n10 = n12;
            n7 = n3;
            n8 = n4;
            n5 = n12;
            n6 = n9;
        }
        else if (this.upFlip == 2) {
            final double n13 = (n + 16 - tt.zz1 * 16.0) / 256.0;
            n5 = (n2 + tt.xx0 * 16.0) / 256.0;
            final double n14 = (n + 16 - tt.zz0 * 16.0) / 256.0;
            n6 = (n2 + tt.xx1 * 16.0) / 256.0;
            n7 = n14;
            n8 = n13;
            n3 = n7;
            n4 = n8;
            n9 = n6;
            n10 = n5;
        }
        else if (this.upFlip == 3) {
            n3 = (n + 16 - tt.xx0 * 16.0) / 256.0;
            n4 = (n + 16 - tt.xx1 * 16.0 - 0.01) / 256.0;
            n5 = (n2 + 16 - tt.zz0 * 16.0) / 256.0;
            n6 = (n2 + 16 - tt.zz1 * 16.0 - 0.01) / 256.0;
            n7 = n4;
            n8 = n3;
            n9 = n5;
            n10 = n6;
        }
        final double n15 = x + tt.xx0;
        final double n16 = x + tt.xx1;
        final double n17 = y + tt.yy1;
        final double n18 = z + tt.zz0;
        final double n19 = z + tt.zz1;
        if (this.applyAmbienceOcclusion) {
            instance.color(this.c1r, this.c1g, this.c1b);
            instance.vertexUV(n16, n17, n19, n4, n6);
            instance.color(this.c2r, this.c2g, this.c2b);
            instance.vertexUV(n16, n17, n18, n7, n9);
            instance.color(this.c3r, this.c3g, this.c3b);
            instance.vertexUV(n15, n17, n18, n3, n5);
            instance.color(this.c4r, this.c4g, this.c4b);
            instance.vertexUV(n15, n17, n19, n8, n10);
        }
        else {
            instance.vertexUV(n16, n17, n19, n4, n6);
            instance.vertexUV(n16, n17, n18, n7, n9);
            instance.vertexUV(n15, n17, n18, n3, n5);
            instance.vertexUV(n15, n17, n19, n8, n10);
        }
    }
    
    public void renderNorth(final Tile tt, final double x, final double y, final double z, int tex) {
        final Tesselator instance = Tesselator.instance;
        if (this.fixedTexture >= 0) {
            tex = this.fixedTexture;
        }
        final int n = (tex & 0xF) << 4;
        final int n2 = tex & 0xF0;
        double n3 = (n + tt.xx0 * 16.0) / 256.0;
        double n4 = (n + tt.xx1 * 16.0 - 0.01) / 256.0;
        double n5 = (n2 + 16 - tt.yy1 * 16.0) / 256.0;
        double n6 = (n2 + 16 - tt.yy0 * 16.0 - 0.01) / 256.0;
        if (this.xFlipTexture) {
            final double n7 = n3;
            n3 = n4;
            n4 = n7;
        }
        if (tt.xx0 < 0.0 || tt.xx1 > 1.0) {
            n3 = (n + 0.0f) / 256.0f;
            n4 = (n + 15.99f) / 256.0f;
        }
        if (tt.yy0 < 0.0 || tt.yy1 > 1.0) {
            n5 = (n2 + 0.0f) / 256.0f;
            n6 = (n2 + 15.99f) / 256.0f;
        }
        double n8 = n4;
        double n9 = n3;
        double n10 = n5;
        double n11 = n6;
        if (this.northFlip == 2) {
            n3 = (n + tt.yy0 * 16.0) / 256.0;
            final double n12 = (n2 + 16 - tt.xx0 * 16.0) / 256.0;
            n4 = (n + tt.yy1 * 16.0) / 256.0;
            final double n13 = (n2 + 16 - tt.xx1 * 16.0) / 256.0;
            n10 = n12;
            n11 = n13;
            n8 = n3;
            n9 = n4;
            n5 = n13;
            n6 = n10;
        }
        else if (this.northFlip == 1) {
            final double n14 = (n + 16 - tt.yy1 * 16.0) / 256.0;
            n5 = (n2 + tt.xx1 * 16.0) / 256.0;
            final double n15 = (n + 16 - tt.yy0 * 16.0) / 256.0;
            n6 = (n2 + tt.xx0 * 16.0) / 256.0;
            n8 = n15;
            n9 = n14;
            n3 = n8;
            n4 = n9;
            n10 = n6;
            n11 = n5;
        }
        else if (this.northFlip == 3) {
            n3 = (n + 16 - tt.xx0 * 16.0) / 256.0;
            n4 = (n + 16 - tt.xx1 * 16.0 - 0.01) / 256.0;
            n5 = (n2 + tt.yy1 * 16.0) / 256.0;
            n6 = (n2 + tt.yy0 * 16.0 - 0.01) / 256.0;
            n8 = n4;
            n9 = n3;
            n10 = n5;
            n11 = n6;
        }
        final double n16 = x + tt.xx0;
        final double n17 = x + tt.xx1;
        final double n18 = y + tt.yy0;
        final double n19 = y + tt.yy1;
        final double n20 = z + tt.zz0;
        if (this.applyAmbienceOcclusion) {
            instance.color(this.c1r, this.c1g, this.c1b);
            instance.vertexUV(n16, n19, n20, n8, n10);
            instance.color(this.c2r, this.c2g, this.c2b);
            instance.vertexUV(n17, n19, n20, n3, n5);
            instance.color(this.c3r, this.c3g, this.c3b);
            instance.vertexUV(n17, n18, n20, n9, n11);
            instance.color(this.c4r, this.c4g, this.c4b);
            instance.vertexUV(n16, n18, n20, n4, n6);
        }
        else {
            instance.vertexUV(n16, n19, n20, n8, n10);
            instance.vertexUV(n17, n19, n20, n3, n5);
            instance.vertexUV(n17, n18, n20, n9, n11);
            instance.vertexUV(n16, n18, n20, n4, n6);
        }
    }
    
    public void renderSouth(final Tile tt, final double x, final double y, final double z, int tex) {
        final Tesselator instance = Tesselator.instance;
        if (this.fixedTexture >= 0) {
            tex = this.fixedTexture;
        }
        final int n = (tex & 0xF) << 4;
        final int n2 = tex & 0xF0;
        double n3 = (n + tt.xx0 * 16.0) / 256.0;
        double n4 = (n + tt.xx1 * 16.0 - 0.01) / 256.0;
        double n5 = (n2 + 16 - tt.yy1 * 16.0) / 256.0;
        double n6 = (n2 + 16 - tt.yy0 * 16.0 - 0.01) / 256.0;
        if (this.xFlipTexture) {
            final double n7 = n3;
            n3 = n4;
            n4 = n7;
        }
        if (tt.xx0 < 0.0 || tt.xx1 > 1.0) {
            n3 = (n + 0.0f) / 256.0f;
            n4 = (n + 15.99f) / 256.0f;
        }
        if (tt.yy0 < 0.0 || tt.yy1 > 1.0) {
            n5 = (n2 + 0.0f) / 256.0f;
            n6 = (n2 + 15.99f) / 256.0f;
        }
        double n8 = n4;
        double n9 = n3;
        double n10 = n5;
        double n11 = n6;
        if (this.southFlip == 1) {
            n3 = (n + tt.yy0 * 16.0) / 256.0;
            final double n12 = (n2 + 16 - tt.xx0 * 16.0) / 256.0;
            n4 = (n + tt.yy1 * 16.0) / 256.0;
            n10 = (n2 + 16 - tt.xx1 * 16.0) / 256.0;
            n11 = n12;
            n8 = n3;
            n9 = n4;
            n5 = n12;
            n6 = n10;
        }
        else if (this.southFlip == 2) {
            final double n13 = (n + 16 - tt.yy1 * 16.0) / 256.0;
            n5 = (n2 + tt.xx0 * 16.0) / 256.0;
            final double n14 = (n + 16 - tt.yy0 * 16.0) / 256.0;
            n6 = (n2 + tt.xx1 * 16.0) / 256.0;
            n8 = n14;
            n9 = n13;
            n3 = n8;
            n4 = n9;
            n10 = n6;
            n11 = n5;
        }
        else if (this.southFlip == 3) {
            n3 = (n + 16 - tt.xx0 * 16.0) / 256.0;
            n4 = (n + 16 - tt.xx1 * 16.0 - 0.01) / 256.0;
            n5 = (n2 + tt.yy1 * 16.0) / 256.0;
            n6 = (n2 + tt.yy0 * 16.0 - 0.01) / 256.0;
            n8 = n4;
            n9 = n3;
            n10 = n5;
            n11 = n6;
        }
        final double n15 = x + tt.xx0;
        final double n16 = x + tt.xx1;
        final double n17 = y + tt.yy0;
        final double n18 = y + tt.yy1;
        final double n19 = z + tt.zz1;
        if (this.applyAmbienceOcclusion) {
            instance.color(this.c1r, this.c1g, this.c1b);
            instance.vertexUV(n15, n18, n19, n3, n5);
            instance.color(this.c2r, this.c2g, this.c2b);
            instance.vertexUV(n15, n17, n19, n9, n11);
            instance.color(this.c3r, this.c3g, this.c3b);
            instance.vertexUV(n16, n17, n19, n4, n6);
            instance.color(this.c4r, this.c4g, this.c4b);
            instance.vertexUV(n16, n18, n19, n8, n10);
        }
        else {
            instance.vertexUV(n15, n18, n19, n3, n5);
            instance.vertexUV(n15, n17, n19, n9, n11);
            instance.vertexUV(n16, n17, n19, n4, n6);
            instance.vertexUV(n16, n18, n19, n8, n10);
        }
    }
    
    public void renderWest(final Tile tt, final double x, final double y, final double z, int tex) {
        final Tesselator instance = Tesselator.instance;
        if (this.fixedTexture >= 0) {
            tex = this.fixedTexture;
        }
        final int n = (tex & 0xF) << 4;
        final int n2 = tex & 0xF0;
        double n3 = (n + tt.zz0 * 16.0) / 256.0;
        double n4 = (n + tt.zz1 * 16.0 - 0.01) / 256.0;
        double n5 = (n2 + 16 - tt.yy1 * 16.0) / 256.0;
        double n6 = (n2 + 16 - tt.yy0 * 16.0 - 0.01) / 256.0;
        if (this.xFlipTexture) {
            final double n7 = n3;
            n3 = n4;
            n4 = n7;
        }
        if (tt.zz0 < 0.0 || tt.zz1 > 1.0) {
            n3 = (n + 0.0f) / 256.0f;
            n4 = (n + 15.99f) / 256.0f;
        }
        if (tt.yy0 < 0.0 || tt.yy1 > 1.0) {
            n5 = (n2 + 0.0f) / 256.0f;
            n6 = (n2 + 15.99f) / 256.0f;
        }
        double n8 = n4;
        double n9 = n3;
        double n10 = n5;
        double n11 = n6;
        if (this.westFlip == 1) {
            n3 = (n + tt.yy0 * 16.0) / 256.0;
            final double n12 = (n2 + 16 - tt.zz1 * 16.0) / 256.0;
            n4 = (n + tt.yy1 * 16.0) / 256.0;
            final double n13 = (n2 + 16 - tt.zz0 * 16.0) / 256.0;
            n10 = n12;
            n11 = n13;
            n8 = n3;
            n9 = n4;
            n5 = n13;
            n6 = n10;
        }
        else if (this.westFlip == 2) {
            final double n14 = (n + 16 - tt.yy1 * 16.0) / 256.0;
            n5 = (n2 + tt.zz0 * 16.0) / 256.0;
            final double n15 = (n + 16 - tt.yy0 * 16.0) / 256.0;
            n6 = (n2 + tt.zz1 * 16.0) / 256.0;
            n8 = n15;
            n9 = n14;
            n3 = n8;
            n4 = n9;
            n10 = n6;
            n11 = n5;
        }
        else if (this.westFlip == 3) {
            n3 = (n + 16 - tt.zz0 * 16.0) / 256.0;
            n4 = (n + 16 - tt.zz1 * 16.0 - 0.01) / 256.0;
            n5 = (n2 + tt.yy1 * 16.0) / 256.0;
            n6 = (n2 + tt.yy0 * 16.0 - 0.01) / 256.0;
            n8 = n4;
            n9 = n3;
            n10 = n5;
            n11 = n6;
        }
        final double n16 = x + tt.xx0;
        final double n17 = y + tt.yy0;
        final double n18 = y + tt.yy1;
        final double n19 = z + tt.zz0;
        final double n20 = z + tt.zz1;
        if (this.applyAmbienceOcclusion) {
            instance.color(this.c1r, this.c1g, this.c1b);
            instance.vertexUV(n16, n18, n20, n8, n10);
            instance.color(this.c2r, this.c2g, this.c2b);
            instance.vertexUV(n16, n18, n19, n3, n5);
            instance.color(this.c3r, this.c3g, this.c3b);
            instance.vertexUV(n16, n17, n19, n9, n11);
            instance.color(this.c4r, this.c4g, this.c4b);
            instance.vertexUV(n16, n17, n20, n4, n6);
        }
        else {
            instance.vertexUV(n16, n18, n20, n8, n10);
            instance.vertexUV(n16, n18, n19, n3, n5);
            instance.vertexUV(n16, n17, n19, n9, n11);
            instance.vertexUV(n16, n17, n20, n4, n6);
        }
    }
    
    public void renderEast(final Tile tt, final double x, final double y, final double z, int tex) {
        final Tesselator instance = Tesselator.instance;
        if (this.fixedTexture >= 0) {
            tex = this.fixedTexture;
        }
        final int n = (tex & 0xF) << 4;
        final int n2 = tex & 0xF0;
        double n3 = (n + tt.zz0 * 16.0) / 256.0;
        double n4 = (n + tt.zz1 * 16.0 - 0.01) / 256.0;
        double n5 = (n2 + 16 - tt.yy1 * 16.0) / 256.0;
        double n6 = (n2 + 16 - tt.yy0 * 16.0 - 0.01) / 256.0;
        if (this.xFlipTexture) {
            final double n7 = n3;
            n3 = n4;
            n4 = n7;
        }
        if (tt.zz0 < 0.0 || tt.zz1 > 1.0) {
            n3 = (n + 0.0f) / 256.0f;
            n4 = (n + 15.99f) / 256.0f;
        }
        if (tt.yy0 < 0.0 || tt.yy1 > 1.0) {
            n5 = (n2 + 0.0f) / 256.0f;
            n6 = (n2 + 15.99f) / 256.0f;
        }
        double n8 = n4;
        double n9 = n3;
        double n10 = n5;
        double n11 = n6;
        if (this.eastFlip == 2) {
            n3 = (n + tt.yy0 * 16.0) / 256.0;
            final double n12 = (n2 + 16 - tt.zz0 * 16.0) / 256.0;
            n4 = (n + tt.yy1 * 16.0) / 256.0;
            final double n13 = (n2 + 16 - tt.zz1 * 16.0) / 256.0;
            n10 = n12;
            n11 = n13;
            n8 = n3;
            n9 = n4;
            n5 = n13;
            n6 = n10;
        }
        else if (this.eastFlip == 1) {
            final double n14 = (n + 16 - tt.yy1 * 16.0) / 256.0;
            n5 = (n2 + tt.zz1 * 16.0) / 256.0;
            final double n15 = (n + 16 - tt.yy0 * 16.0) / 256.0;
            n6 = (n2 + tt.zz0 * 16.0) / 256.0;
            n8 = n15;
            n9 = n14;
            n3 = n8;
            n4 = n9;
            n10 = n6;
            n11 = n5;
        }
        else if (this.eastFlip == 3) {
            n3 = (n + 16 - tt.zz0 * 16.0) / 256.0;
            n4 = (n + 16 - tt.zz1 * 16.0 - 0.01) / 256.0;
            n5 = (n2 + tt.yy1 * 16.0) / 256.0;
            n6 = (n2 + tt.yy0 * 16.0 - 0.01) / 256.0;
            n8 = n4;
            n9 = n3;
            n10 = n5;
            n11 = n6;
        }
        final double n16 = x + tt.xx1;
        final double n17 = y + tt.yy0;
        final double n18 = y + tt.yy1;
        final double n19 = z + tt.zz0;
        final double n20 = z + tt.zz1;
        if (this.applyAmbienceOcclusion) {
            instance.color(this.c1r, this.c1g, this.c1b);
            instance.vertexUV(n16, n17, n20, n9, n11);
            instance.color(this.c2r, this.c2g, this.c2b);
            instance.vertexUV(n16, n17, n19, n4, n6);
            instance.color(this.c3r, this.c3g, this.c3b);
            instance.vertexUV(n16, n18, n19, n8, n10);
            instance.color(this.c4r, this.c4g, this.c4b);
            instance.vertexUV(n16, n18, n20, n3, n5);
        }
        else {
            instance.vertexUV(n16, n17, n20, n9, n11);
            instance.vertexUV(n16, n17, n19, n4, n6);
            instance.vertexUV(n16, n18, n19, n8, n10);
            instance.vertexUV(n16, n18, n20, n3, n5);
        }
    }
    
    public void renderTile(final Tile tile, int data, final float brightness) {
        final Tesselator instance = Tesselator.instance;
        if (this.setColor) {
            final int color = tile.getColor(data);
            GL11.glColor4f((color >> 16 & 0xFF) / 255.0f * brightness, (color >> 8 & 0xFF) / 255.0f * brightness, (color & 0xFF) / 255.0f * brightness, 1.0f);
        }
        final int renderShape = tile.getRenderShape();
        if (renderShape == 0 || renderShape == 16) {
            if (renderShape == 16) {
                data = 1;
            }
            tile.updateDefaultShape();
            GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
            instance.begin();
            instance.normal(0.0f, -1.0f, 0.0f);
            this.renderFaceUp(tile, 0.0, 0.0, 0.0, tile.getTexture(0, data));
            instance.end();
            instance.begin();
            instance.normal(0.0f, 1.0f, 0.0f);
            this.renderFaceDown(tile, 0.0, 0.0, 0.0, tile.getTexture(1, data));
            instance.end();
            instance.begin();
            instance.normal(0.0f, 0.0f, -1.0f);
            this.renderNorth(tile, 0.0, 0.0, 0.0, tile.getTexture(2, data));
            instance.end();
            instance.begin();
            instance.normal(0.0f, 0.0f, 1.0f);
            this.renderSouth(tile, 0.0, 0.0, 0.0, tile.getTexture(3, data));
            instance.end();
            instance.begin();
            instance.normal(-1.0f, 0.0f, 0.0f);
            this.renderWest(tile, 0.0, 0.0, 0.0, tile.getTexture(4, data));
            instance.end();
            instance.begin();
            instance.normal(1.0f, 0.0f, 0.0f);
            this.renderEast(tile, 0.0, 0.0, 0.0, tile.getTexture(5, data));
            instance.end();
            GL11.glTranslatef(0.5f, 0.5f, 0.5f);
        }
        else if (renderShape == 1) {
            instance.begin();
            instance.normal(0.0f, -1.0f, 0.0f);
            this.tesselateCrossTexture(tile, data, -0.5, -0.5, -0.5);
            instance.end();
        }
        else if (renderShape == 13) {
            tile.updateDefaultShape();
            GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
            final float n = 0.0625f;
            instance.begin();
            instance.normal(0.0f, -1.0f, 0.0f);
            this.renderFaceUp(tile, 0.0, 0.0, 0.0, tile.getTexture(0));
            instance.end();
            instance.begin();
            instance.normal(0.0f, 1.0f, 0.0f);
            this.renderFaceDown(tile, 0.0, 0.0, 0.0, tile.getTexture(1));
            instance.end();
            instance.begin();
            instance.normal(0.0f, 0.0f, -1.0f);
            instance.addOffset(0.0f, 0.0f, n);
            this.renderNorth(tile, 0.0, 0.0, 0.0, tile.getTexture(2));
            instance.addOffset(0.0f, 0.0f, -n);
            instance.end();
            instance.begin();
            instance.normal(0.0f, 0.0f, 1.0f);
            instance.addOffset(0.0f, 0.0f, -n);
            this.renderSouth(tile, 0.0, 0.0, 0.0, tile.getTexture(3));
            instance.addOffset(0.0f, 0.0f, n);
            instance.end();
            instance.begin();
            instance.normal(-1.0f, 0.0f, 0.0f);
            instance.addOffset(n, 0.0f, 0.0f);
            this.renderWest(tile, 0.0, 0.0, 0.0, tile.getTexture(4));
            instance.addOffset(-n, 0.0f, 0.0f);
            instance.end();
            instance.begin();
            instance.normal(1.0f, 0.0f, 0.0f);
            instance.addOffset(-n, 0.0f, 0.0f);
            this.renderEast(tile, 0.0, 0.0, 0.0, tile.getTexture(5));
            instance.addOffset(n, 0.0f, 0.0f);
            instance.end();
            GL11.glTranslatef(0.5f, 0.5f, 0.5f);
        }
        else if (renderShape == 6) {
            instance.begin();
            instance.normal(0.0f, -1.0f, 0.0f);
            this.tesselateRowTexture(tile, data, -0.5, -0.5, -0.5);
            instance.end();
        }
        else if (renderShape == 2) {
            instance.begin();
            instance.normal(0.0f, -1.0f, 0.0f);
            this.tesselateTorch(tile, -0.5, -0.5, -0.5, 0.0, 0.0);
            instance.end();
        }
        else if (renderShape == 10) {
            for (int i = 0; i < 2; ++i) {
                if (i == 0) {
                    tile.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.5f);
                }
                if (i == 1) {
                    tile.setShape(0.0f, 0.0f, 0.5f, 1.0f, 0.5f, 1.0f);
                }
                GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
                instance.begin();
                instance.normal(0.0f, -1.0f, 0.0f);
                this.renderFaceUp(tile, 0.0, 0.0, 0.0, tile.getTexture(0));
                instance.end();
                instance.begin();
                instance.normal(0.0f, 1.0f, 0.0f);
                this.renderFaceDown(tile, 0.0, 0.0, 0.0, tile.getTexture(1));
                instance.end();
                instance.begin();
                instance.normal(0.0f, 0.0f, -1.0f);
                this.renderNorth(tile, 0.0, 0.0, 0.0, tile.getTexture(2));
                instance.end();
                instance.begin();
                instance.normal(0.0f, 0.0f, 1.0f);
                this.renderSouth(tile, 0.0, 0.0, 0.0, tile.getTexture(3));
                instance.end();
                instance.begin();
                instance.normal(-1.0f, 0.0f, 0.0f);
                this.renderWest(tile, 0.0, 0.0, 0.0, tile.getTexture(4));
                instance.end();
                instance.begin();
                instance.normal(1.0f, 0.0f, 0.0f);
                this.renderEast(tile, 0.0, 0.0, 0.0, tile.getTexture(5));
                instance.end();
                GL11.glTranslatef(0.5f, 0.5f, 0.5f);
            }
        }
        else if (renderShape == 11) {
            for (int j = 0; j < 4; ++j) {
                final float n2 = 0.125f;
                if (j == 0) {
                    tile.setShape(0.5f - n2, 0.0f, 0.0f, 0.5f + n2, 1.0f, n2 * 2.0f);
                }
                if (j == 1) {
                    tile.setShape(0.5f - n2, 0.0f, 1.0f - n2 * 2.0f, 0.5f + n2, 1.0f, 1.0f);
                }
                final float n3 = 0.0625f;
                if (j == 2) {
                    tile.setShape(0.5f - n3, 1.0f - n3 * 3.0f, -n3 * 2.0f, 0.5f + n3, 1.0f - n3, 1.0f + n3 * 2.0f);
                }
                if (j == 3) {
                    tile.setShape(0.5f - n3, 0.5f - n3 * 3.0f, -n3 * 2.0f, 0.5f + n3, 0.5f - n3, 1.0f + n3 * 2.0f);
                }
                GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
                instance.begin();
                instance.normal(0.0f, -1.0f, 0.0f);
                this.renderFaceUp(tile, 0.0, 0.0, 0.0, tile.getTexture(0));
                instance.end();
                instance.begin();
                instance.normal(0.0f, 1.0f, 0.0f);
                this.renderFaceDown(tile, 0.0, 0.0, 0.0, tile.getTexture(1));
                instance.end();
                instance.begin();
                instance.normal(0.0f, 0.0f, -1.0f);
                this.renderNorth(tile, 0.0, 0.0, 0.0, tile.getTexture(2));
                instance.end();
                instance.begin();
                instance.normal(0.0f, 0.0f, 1.0f);
                this.renderSouth(tile, 0.0, 0.0, 0.0, tile.getTexture(3));
                instance.end();
                instance.begin();
                instance.normal(-1.0f, 0.0f, 0.0f);
                this.renderWest(tile, 0.0, 0.0, 0.0, tile.getTexture(4));
                instance.end();
                instance.begin();
                instance.normal(1.0f, 0.0f, 0.0f);
                this.renderEast(tile, 0.0, 0.0, 0.0, tile.getTexture(5));
                instance.end();
                GL11.glTranslatef(0.5f, 0.5f, 0.5f);
            }
            tile.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        }
    }
    
    public static boolean canRender(final int renderShape) {
        return renderShape == 0 || renderShape == 13 || renderShape == 10 || renderShape == 11 || renderShape == 16;
    }
    
    static {
        TileRenderer.fancy = true;
    }
}
