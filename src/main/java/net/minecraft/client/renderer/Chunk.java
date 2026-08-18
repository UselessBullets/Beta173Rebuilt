// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer;

import net.minecraft.client.renderer.culling.Culler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.client.renderer.tileentity.TileEntityRenderDispatcher;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.Region;

import java.util.HashSet;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.client.renderer.entity.EntityRenderer;
import util.Mth;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;

import static org.lwjgl.opengl.GL11.*;

public class Chunk
{
    public Level level;
    private int lists = -1;
    private static Tesselator t = Tesselator.instance;
    public static int updates = 0;
    public int x, y, z;
    public int xs, ys, zs;
    public int xRender, yRender, zRender;
    public int xRenderOffs, yRenderOffs, zRenderOffs;
    public boolean visible = false;
    public boolean[] empty = new boolean[2];
    public int xm, ym, zm;
    public float radius;
    public boolean dirty = false;
    public AABB bb;
    public int id;
    public boolean occlusion_visible = true;
    public boolean occlusion_querying;
    public int occlusion_id;
    public boolean skyLit;
    private boolean compiled = false;
    public List<TileEntity> renderableTileEntities = new ArrayList<>();
    private List<TileEntity> globalRenderableTileEntities;
    
    public Chunk(final Level level, final List<TileEntity> globalRenderableTileEntities, final int x, final int y, final int z, final int size, final int lists) {
        this.level = level;
        this.globalRenderableTileEntities = globalRenderableTileEntities;
        this.xs = this.ys = this.zs = size;
        this.radius = Mth.sqrt((float)(this.xs * this.xs + this.ys * this.ys + this.zs * this.zs)) / 2.0f;
        this.lists = lists;
        this.x = -999;
        this.setPos(x, y, z);
    }
    
    public void setPos(final int x, final int y, final int z) {
        if (x == this.x && y == this.y && z == this.z) {
            return;
        }
        this.reset();
        this.x = x;
        this.y = y;
        this.z = z;
        this.xm = x + this.xs / 2;
        this.ym = y + this.ys / 2;
        this.zm = z + this.zs / 2;

        this.xRenderOffs = (x & 0x3FF);
        this.yRenderOffs = y;
        this.zRenderOffs = (z & 0x3FF);
        this.xRender = x - this.xRenderOffs;
        this.yRender = y - this.yRenderOffs;
        this.zRender = z - this.zRenderOffs;

        final float g = 6.0f;
        this.bb = AABB.newPermanent(x - g, y - g, z - g, x + this.xs + g, y + this.ys + g, z + this.zs + g);
        glNewList(this.lists + 2, GL_COMPILE);
        EntityRenderer.renderFlat(
                AABB.newTemp(
                        this.xRenderOffs - g,
                        this.yRenderOffs - g,
                        this.zRenderOffs - g,
                        this.xRenderOffs + this.xs + g,
                        this.yRenderOffs + this.ys + g,
                        this.zRenderOffs + this.zs + g
                )
        );
        glEndList();
        this.setDirty();
    }
    
    private void translateToPos() {
        glTranslatef((float)this.xRenderOffs, (float)this.yRenderOffs, (float)this.zRenderOffs);
    }
    
    public void rebuild() {
        if (!this.dirty) return;
        ++Chunk.updates;

        final int x0 = this.x;
        final int y0 = this.y;
        final int z0 = this.z;
        final int x1 = this.x + this.xs;
        final int y1 = this.y + this.ys;
        final int z1 = this.z + this.zs;

        for (int currentLayer = 0; currentLayer < 2; ++currentLayer) {
            this.empty[currentLayer] = true;
        }
        LevelChunk.touchedSky = false;
        final Set<TileEntity> oldTileEntities = new HashSet<>(this.renderableTileEntities);
        this.renderableTileEntities.clear();
        final int r = 1;
        final Region level = new Region(this.level, x0 - r, y0 - r, z0 - r, x1 + r, y1 + r, z1 + r);
        final TileRenderer tileRenderer = new TileRenderer(level);

        for (int currentLayer = 0; currentLayer < 2; ++currentLayer) {
            boolean renderNextLayer = false;
            boolean rendered = false;
            boolean started = false;

            for (int y = y0; y < y1; ++y) {
                for (int z = z0; z < z1; ++z) {
                    for (int x = x0; x < x1; ++x) {
                        final int tileId = level.getTile(x, y, z);
                        if (tileId > 0) {
                            if (!started) {
                                started = true;
                                glNewList(this.lists + currentLayer, GL_COMPILE);
                                glPushMatrix();
                                this.translateToPos();
                                final float ss = 1.000001f;
                                glTranslatef(-this.zs / 2.0f, -this.ys / 2.0f, -this.zs / 2.0f);
                                glScalef(ss, ss, ss);
                                glTranslatef(this.zs / 2.0f, this.ys / 2.0f, this.zs / 2.0f);
                                Chunk.t.begin();
                                Chunk.t.offset(-this.x, -this.y, -this.z);
                            }

                            if (currentLayer == 0 && Tile.isEntityTile[tileId]) {
                                final TileEntity te = level.getTileEntity(x, y, z);
                                if (TileEntityRenderDispatcher.instance.hasRenderer(te)) {
                                    this.renderableTileEntities.add(te);
                                }
                            }

                            final Tile tile = Tile.tiles[tileId];
                            final int renderLayer = tile.getRenderLayer();
                            if (renderLayer != currentLayer) {
                                renderNextLayer = true;
                            }
                            else if (renderLayer == currentLayer) {
                                rendered |= tileRenderer.tesselateInWorld(tile, x, y, z);
                            }
                        }
                    }
                }
            }

            if (started) {
                Chunk.t.end();
                glPopMatrix();
                glEndList();
                Chunk.t.offset(0.0, 0.0, 0.0);
            }
            else {
                rendered = false;
            }

            if (rendered) {
                this.empty[currentLayer] = false;
            }

            if (!renderNextLayer) {
                break;
            }
        }
        final Set<TileEntity> newTileEntities = new HashSet<>(this.renderableTileEntities);
        newTileEntities.removeAll(oldTileEntities);
        this.globalRenderableTileEntities.addAll(newTileEntities);
        oldTileEntities.removeAll(this.renderableTileEntities);
        this.globalRenderableTileEntities.removeAll(oldTileEntities);
        this.skyLit = LevelChunk.touchedSky;
        this.compiled = true;
    }
    
    public float distanceToSqr(final Entity player) {
        final float xd = (float)(player.x - this.xm);
        final float yd = (float)(player.y - this.ym);
        final float zd = (float)(player.z - this.zm);
        return xd * xd + yd * yd + zd * zd;
    }
    
    public void reset() {
        for (int i = 0; i < 2; ++i) {
            this.empty[i] = true;
        }
        this.visible = false;
        this.compiled = false;
    }
    
    public void delete() {
        this.reset();
        this.level = null;
    }
    
    public int getList(final int layer) {
        if (!this.visible) return -1;
        if (this.empty[layer]) return -1;

        return this.lists + layer;
    }
    
    public void cull(final Culler culler) {
        this.visible = culler.isVisible(this.bb);
    }
    
    public void renderBB() {
        glCallList(this.lists + 2);
    }
    
    public boolean isEmpty() {
        return this.compiled && this.empty[0] && this.empty[1];
    }
    
    public void setDirty() {
        this.dirty = true;
    }

}
