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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;

import static org.lwjgl.opengl.GL11.*;

public class Chunk
{
    public Level level;
    private int lists;
    private static Tesselator t;
    public static int updates;
    public int x;
    public int y;
    public int z;
    public int xs;
    public int ys;
    public int zs;
    public int xRender;
    public int yRender;
    public int zRender;
    public int xRenderOffs;
    public int yRenderOffs;
    public int zRenderOffs;
    public boolean visible;
    public boolean[] empty;
    public int xm;
    public int ym;
    public int zm;
    public float radius;
    public boolean dirty;
    public AABB bb;
    public int id;
    public boolean occlusion_visible;
    public boolean occlusion_querying;
    public int occlusion_id;
    public boolean skyLit;
    private boolean compiled;
    public List renderableTileEntities;
    private List globalRenderableTileEntities;
    
    public Chunk(final Level level, final List globalRenderableTileEntities, final int x, final int y, final int z, final int size, final int lists) {
        this.lists = -1;
        this.visible = false;
        this.empty = new boolean[2];
        this.occlusion_visible = true;
        this.compiled = false;
        this.renderableTileEntities = new ArrayList();
        this.level = level;
        this.globalRenderableTileEntities = globalRenderableTileEntities;
        this.zs = size;
        this.ys = size;
        this.xs = size;
        this.radius = Mth.sqrt((float)(this.xs * this.xs + this.ys * this.ys + this.zs * this.zs)) / 2.0f;
        this.lists = lists;
        this.x = -999;
        this.setPos(x, y, z);
        this.dirty = false;
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
        final float n = 6.0f;
        this.bb = AABB.newPermanent(x - n, y - n, z - n, x + this.xs + n, y + this.ys + n, z + this.zs + n);
        glNewList(this.lists + 2, GL_COMPILE);
        EntityRenderer.renderFlat(AABB.newTemp(this.xRenderOffs - n, this.yRenderOffs - n, this.zRenderOffs - n, this.xRenderOffs + this.xs + n, this.yRenderOffs + this.ys + n, this.zRenderOffs + this.zs + n));
        glEndList();
        this.setDirty();
    }
    
    private void translateToPos() {
        glTranslatef((float)this.xRenderOffs, (float)this.yRenderOffs, (float)this.zRenderOffs);
    }
    
    public void rebuild() {
        if (!this.dirty) {
            return;
        }
        ++Chunk.updates;
        final int x = this.x;
        final int y = this.y;
        final int z = this.z;
        final int n = this.x + this.xs;
        final int n2 = this.y + this.ys;
        final int n3 = this.z + this.zs;
        for (int i = 0; i < 2; ++i) {
            this.empty[i] = true;
        }
        LevelChunk.touchedSky = false;
        final HashSet set = new HashSet();
        set.addAll(this.renderableTileEntities);
        this.renderableTileEntities.clear();
        final int n4 = 1;
        final Region level = new Region(this.level, x - n4, y - n4, z - n4, n + n4, n2 + n4, n3 + n4);
        final TileRenderer tileRenderer = new TileRenderer(level);
        for (int j = 0; j < 2; ++j) {
            boolean b = false;
            boolean b2 = false;
            int n5 = 0;
            for (int k = y; k < n2; ++k) {
                for (int l = z; l < n3; ++l) {
                    for (int x2 = x; x2 < n; ++x2) {
                        final int tile = level.getTile(x2, k, l);
                        if (tile > 0) {
                            if (n5 == 0) {
                                n5 = 1;
                                glNewList(this.lists + j, GL_COMPILE);
                                glPushMatrix();
                                this.translateToPos();
                                final float n6 = 1.000001f;
                                glTranslatef(-this.zs / 2.0f, -this.ys / 2.0f, -this.zs / 2.0f);
                                glScalef(n6, n6, n6);
                                glTranslatef(this.zs / 2.0f, this.ys / 2.0f, this.zs / 2.0f);
                                Chunk.t.begin();
                                Chunk.t.offset(-this.x, -this.y, -this.z);
                            }
                            if (j == 0 && Tile.isEntityTile[tile]) {
                                final TileEntity tileEntity = level.getTileEntity(x2, k, l);
                                if (TileEntityRenderDispatcher.instance.hasRenderer(tileEntity)) {
                                    this.renderableTileEntities.add(tileEntity);
                                }
                            }
                            final Tile tt = Tile.tiles[tile];
                            final int renderLayer = tt.getRenderLayer();
                            if (renderLayer != j) {
                                b = true;
                            }
                            else if (renderLayer == j) {
                                b2 |= tileRenderer.tesselateInWorld(tt, x2, k, l);
                            }
                        }
                    }
                }
            }
            if (n5 != 0) {
                Chunk.t.end();
                glPopMatrix();
                glEndList();
                Chunk.t.offset(0.0, 0.0, 0.0);
            }
            else {
                b2 = false;
            }
            if (b2) {
                this.empty[j] = false;
            }
            if (!b) {
                break;
            }
        }
        final HashSet set2 = new HashSet();
        set2.addAll(this.renderableTileEntities);
        set2.removeAll(set);
        this.globalRenderableTileEntities.addAll(set2);
        set.removeAll(this.renderableTileEntities);
        this.globalRenderableTileEntities.removeAll(set);
        this.skyLit = LevelChunk.touchedSky;
        this.compiled = true;
    }
    
    public float distanceToSqr(final Entity player) {
        final float n = (float)(player.x - this.xm);
        final float n2 = (float)(player.y - this.ym);
        final float n3 = (float)(player.z - this.zm);
        return n * n + n2 * n2 + n3 * n3;
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
        if (!this.visible) {
            return -1;
        }
        if (!this.empty[layer]) {
            return this.lists + layer;
        }
        return -1;
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
    
    static {
        Chunk.t = Tesselator.instance;
        Chunk.updates = 0;
    }
}
