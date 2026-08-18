// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.client.gui.Font;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import util.Mth;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.client.renderer.Textures;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.TileRenderer;
import net.minecraft.client.model.Model;

import static org.lwjgl.opengl.GL11.*;

public abstract class EntityRenderer<T extends Entity>
{
    protected EntityRenderDispatcher entityRenderDispatcher;
    private Model model = new HumanoidModel();
    private TileRenderer tileRenderer = new TileRenderer();
    protected float shadowRadius = 0.0f;
    protected float shadowStrength = 1.0f;
    
    public abstract void render(final T entity, final double x, final double y, final double z, final float rot, final float a);
    
    protected void bindTexture(final String resourceName) {
        final Textures t = this.entityRenderDispatcher.textures;
        t.bind(t.loadTexture(resourceName));
    }
    
    protected boolean bindTexture(final String urlTexture, final String backupTexture) {
        final Textures t = this.entityRenderDispatcher.textures;
        final int id = t.loadHttpTexture(urlTexture, backupTexture);

        if (id >= 0) {
            t.bind(id);
            return true;
        }
        return false;
    }
    
    private void renderFlame(final Entity e, final double x, final double y, final double z, final float a) {
        glDisable(GL_LIGHTING);

        final int tex = Tile.fire.tex;
        final int xt = (tex & 0xF) << 4;
        final int yt = tex & 0xF0;
        float u0 = xt / 256.0f;
        float u1 = (xt + 15.99f) / 256.0f;
        float v0 = yt / 256.0f;
        float v1 = (yt + 15.99f) / 256.0f;

        glPushMatrix();
        glTranslatef((float)x, (float)y, (float)z);

        final float s = e.bbWidth * 1.4f;
        glScalef(s, s, s);
        this.bindTexture("/terrain.png");
        final Tesselator t = Tesselator.instance;

        float r = 0.5f;
        final float xo = 0.0f;

        float h = e.bbHeight / s;
        float yo = (float)(e.y - e.bb.y0);

        glRotatef(-this.entityRenderDispatcher.playerRotY, 0.0f, 1.0f, 0.0f);

        glTranslatef(0.0f, 0.0f, -0.3f + (int)h * 0.02f);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        float zo = 0.0f;
        int ss = 0;
        t.begin();
        while (h > 0.0f) {
            if (ss % 2 == 0) {
                u0 = xt / 256.0f;
                u1 = (xt + 15.99f) / 256.0f;
                v0 = yt / 256.0f;
                v1 = (yt + 15.99f) / 256.0f;
            }
            else {
                u0 = xt / 256.0f;
                u1 = (xt + 15.99f) / 256.0f;
                v0 = (yt + 16) / 256.0f;
                v1 = (yt + 16 + 15.99f) / 256.0f;
            }

            if (ss / 2 % 2 == 0) {
                final float tmp = u1;
                u1 = u0;
                u0 = tmp;
            }
            t.vertexUV(r - xo, 0.0f - yo, zo, u1, v1);
            t.vertexUV(-r - xo, 0.0f - yo, zo, u0, v1);
            t.vertexUV(-r - xo, 1.4f - yo, zo, u0, v0);
            t.vertexUV(r - xo, 1.4f - yo, zo, u1, v0);
            h -= 0.45f;
            yo -= 0.45f;
            r *= 0.9f;
            zo += 0.03f;
            ++ss;
        }
        t.end();
        glPopMatrix();
        glEnable(GL_LIGHTING);
    }
    
    private void renderShadow(final Entity e, final double x, final double y, final double z, final float pow, final float a) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        final Textures textures = this.entityRenderDispatcher.textures;
        textures.bind(textures.loadTexture("%clamp%/misc/shadow.png"));
        final Level level = this.getLevel();
        glDepthMask(false);

        final float r = this.shadowRadius;
        final double ex = e.xOld + (e.x - e.xOld) * a;
        final double ey = e.yOld + (e.y - e.yOld) * a + e.getShadowHeightOffs();
        final double ez = e.zOld + (e.z - e.zOld) * a;

        final int x0 = Mth.floor(ex - r);
        final int x1 = Mth.floor(ex + r);
        final int y0 = Mth.floor(ey - r);
        final int y1 = Mth.floor(ey);
        final int z0 = Mth.floor(ez - r);
        final int z1 = Mth.floor(ez + r);

        final double xo = x - ex;
        final double n3 = y - ey;
        final double zo = z - ez;
        final Tesselator tt = Tesselator.instance;
        tt.begin();

        for (int xt = x0; xt <= x1; ++xt) {
            for (int yt = y0; yt <= y1; ++yt) {
                for (int zt = z0; zt <= z1; ++zt) {
                    final int t = level.getTile(xt, yt - 1, zt);
                    if (t > 0 && level.getRawBrightness(xt, yt, zt) > 3) {
                        this.renderTileShadow(Tile.tiles[t], x, y + e.getShadowHeightOffs(), z, xt, yt, zt, pow, r, xo, n3 + e.getShadowHeightOffs(), zo);
                    }
                }
            }
        }

        tt.end();
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glDisable(GL_BLEND);
        glDepthMask(true);
    }
    
    private Level getLevel() {
        return this.entityRenderDispatcher.level;
    }
    
    private void renderTileShadow(final Tile tt, final double x, final double y, final double z, final int xt, final int yt, final int zt, final float pow, final float r, final double xo, final double yo, final double zo) {
        final Tesselator t = Tesselator.instance;
        if (!tt.isCubeShaped()) return;

        double a = (pow - (y - (yt + yo)) / 2.0) * 0.5 * this.getLevel().getBrightness(xt, yt, zt);
        if (a < 0.0) return;
        if (a > 1.0) a = 1.0;

        t.color(1.0f, 1.0f, 1.0f, (float)a);

        final double x0 = xt + tt.xx0 + xo;
        final double x1 = xt + tt.xx1 + xo;
        final double y0 = yt + tt.yy0 + yo + 0.015625;
        final double z0 = zt + tt.zz0 + zo;
        final double z1 = zt + tt.zz1 + zo;

        final float u0 = (float)((x - x0) / 2.0 / r + 0.5);
        final float u1 = (float)((x - x1) / 2.0 / r + 0.5);
        final float v0 = (float)((z - z0) / 2.0 / r + 0.5);
        final float v1 = (float)((z - z1) / 2.0 / r + 0.5);

        t.vertexUV(x0, y0, z0, u0, v0);
        t.vertexUV(x0, y0, z1, u0, v1);
        t.vertexUV(x1, y0, z1, u1, v1);
        t.vertexUV(x1, y0, z0, u1, v0);
    }
    
    public static void render(final AABB bb, final double xo, final double yo, final double zo) {
        glDisable(GL_TEXTURE_2D);
        final Tesselator t = Tesselator.instance;
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        t.begin();
        t.offset(xo, yo, zo);
        t.normal(0.0f, 0.0f, -1.0f);
        t.vertex(bb.x0, bb.y1, bb.z0);
        t.vertex(bb.x1, bb.y1, bb.z0);
        t.vertex(bb.x1, bb.y0, bb.z0);
        t.vertex(bb.x0, bb.y0, bb.z0);

        t.normal(0.0f, 0.0f, 1.0f);
        t.vertex(bb.x0, bb.y0, bb.z1);
        t.vertex(bb.x1, bb.y0, bb.z1);
        t.vertex(bb.x1, bb.y1, bb.z1);
        t.vertex(bb.x0, bb.y1, bb.z1);

        t.normal(0.0f, -1.0f, 0.0f);
        t.vertex(bb.x0, bb.y0, bb.z0);
        t.vertex(bb.x1, bb.y0, bb.z0);
        t.vertex(bb.x1, bb.y0, bb.z1);
        t.vertex(bb.x0, bb.y0, bb.z1);

        t.normal(0.0f, 1.0f, 0.0f);
        t.vertex(bb.x0, bb.y1, bb.z1);
        t.vertex(bb.x1, bb.y1, bb.z1);
        t.vertex(bb.x1, bb.y1, bb.z0);
        t.vertex(bb.x0, bb.y1, bb.z0);

        t.normal(-1.0f, 0.0f, 0.0f);
        t.vertex(bb.x0, bb.y0, bb.z1);
        t.vertex(bb.x0, bb.y1, bb.z1);
        t.vertex(bb.x0, bb.y1, bb.z0);
        t.vertex(bb.x0, bb.y0, bb.z0);

        t.normal(1.0f, 0.0f, 0.0f);
        t.vertex(bb.x1, bb.y0, bb.z0);
        t.vertex(bb.x1, bb.y1, bb.z0);
        t.vertex(bb.x1, bb.y1, bb.z1);
        t.vertex(bb.x1, bb.y0, bb.z1);
        t.offset(0.0, 0.0, 0.0);
        t.end();
        glEnable(GL_TEXTURE_2D);
    }
    
    public static void renderFlat(final AABB bb) {
        final Tesselator t = Tesselator.instance;
        t.begin();
        t.vertex(bb.x0, bb.y1, bb.z0);
        t.vertex(bb.x1, bb.y1, bb.z0);
        t.vertex(bb.x1, bb.y0, bb.z0);
        t.vertex(bb.x0, bb.y0, bb.z0);
        t.vertex(bb.x0, bb.y0, bb.z1);
        t.vertex(bb.x1, bb.y0, bb.z1);
        t.vertex(bb.x1, bb.y1, bb.z1);
        t.vertex(bb.x0, bb.y1, bb.z1);
        t.vertex(bb.x0, bb.y0, bb.z0);
        t.vertex(bb.x1, bb.y0, bb.z0);
        t.vertex(bb.x1, bb.y0, bb.z1);
        t.vertex(bb.x0, bb.y0, bb.z1);
        t.vertex(bb.x0, bb.y1, bb.z1);
        t.vertex(bb.x1, bb.y1, bb.z1);
        t.vertex(bb.x1, bb.y1, bb.z0);
        t.vertex(bb.x0, bb.y1, bb.z0);
        t.vertex(bb.x0, bb.y0, bb.z1);
        t.vertex(bb.x0, bb.y1, bb.z1);
        t.vertex(bb.x0, bb.y1, bb.z0);
        t.vertex(bb.x0, bb.y0, bb.z0);
        t.vertex(bb.x1, bb.y0, bb.z0);
        t.vertex(bb.x1, bb.y1, bb.z0);
        t.vertex(bb.x1, bb.y1, bb.z1);
        t.vertex(bb.x1, bb.y0, bb.z1);
        t.end();
    }
    
    public void init(final EntityRenderDispatcher entityRenderDispatcher) {
        this.entityRenderDispatcher = entityRenderDispatcher;
    }
    
    public void postRender(final Entity entity, final double x, final double y, final double z, final float rot, final float a) {
        if (this.entityRenderDispatcher.options.fancyGraphics && this.shadowRadius > 0.0f) {
            final double dist = this.entityRenderDispatcher.distanceToSqr(entity.x, entity.y, entity.z);
            final float pow = (float)((1.0 - dist / (16.0f * 16.0f)) * this.shadowStrength);
            if (pow > 0.0f) {
                this.renderShadow(entity, x, y, z, pow, a);
            }
        }
        if (entity.isOnFire()) this.renderFlame(entity, x, y, z, a);
    }
    
    public Font getFont() {
        return this.entityRenderDispatcher.getFont();
    }
}
