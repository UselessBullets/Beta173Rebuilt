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
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Textures;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.TileRenderer;
import net.minecraft.client.model.Model;

import static org.lwjgl.opengl.GL11.*;

public abstract class EntityRenderer<T extends Entity>
{
    protected EntityRenderDispatcher entityRenderDispatcher;
    private Model m;
    private TileRenderer tr;
    protected float shadowRadius;
    protected float shadowStrength;
    
    public EntityRenderer() {
        this.m = new HumanoidModel();
        this.tr = new TileRenderer();
        this.shadowRadius = 0.0f;
        this.shadowStrength = 1.0f;
    }
    
    public abstract void render(final T entity, final double x, final double y, final double z, final float rot, final float partialTick);
    
    protected void bindTexture(final String resourceName) {
        final Textures textures = this.entityRenderDispatcher.textures;
        textures.bind(textures.loadTexture(resourceName));
    }
    
    protected boolean bindTexture(final String urlTexture, final String backupTexture) {
        final Textures textures = this.entityRenderDispatcher.textures;
        final int loadHttpTexture = textures.loadHttpTexture(urlTexture, backupTexture);
        if (loadHttpTexture >= 0) {
            textures.bind(loadHttpTexture);
            return true;
        }
        return false;
    }
    
    private void renderFlame(final Entity e, final double x, final double y, final double z, final float partialTick) {
        GL11.glDisable(GL_LIGHTING);
        final int tex = Tile.fire.tex;
        final int n = (tex & 0xF) << 4;
        final int n2 = tex & 0xF0;
        final float n3 = n / 256.0f;
        final float n4 = (n + 15.99f) / 256.0f;
        final float n5 = n2 / 256.0f;
        final float n6 = (n2 + 15.99f) / 256.0f;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        final float n7 = e.bbWidth * 1.4f;
        GL11.glScalef(n7, n7, n7);
        this.bindTexture("/terrain.png");
        final Tesselator instance = Tesselator.instance;
        float n8 = 0.5f;
        final float n9 = 0.0f;
        float n10 = e.bbHeight / n7;
        float n11 = (float)(e.y - e.bb.y0);
        GL11.glRotatef(-this.entityRenderDispatcher.playerRotY, 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef(0.0f, 0.0f, -0.3f + (int)n10 * 0.02f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        float n12 = 0.0f;
        int n13 = 0;
        instance.begin();
        while (n10 > 0.0f) {
            float n14;
            float n15;
            float n16;
            float n17;
            if (n13 % 2 == 0) {
                n14 = n / 256.0f;
                n15 = (n + 15.99f) / 256.0f;
                n16 = n2 / 256.0f;
                n17 = (n2 + 15.99f) / 256.0f;
            }
            else {
                n14 = n / 256.0f;
                n15 = (n + 15.99f) / 256.0f;
                n16 = (n2 + 16) / 256.0f;
                n17 = (n2 + 16 + 15.99f) / 256.0f;
            }
            if (n13 / 2 % 2 == 0) {
                final float n18 = n15;
                n15 = n14;
                n14 = n18;
            }
            instance.vertexUV(n8 - n9, 0.0f - n11, n12, n15, n17);
            instance.vertexUV(-n8 - n9, 0.0f - n11, n12, n14, n17);
            instance.vertexUV(-n8 - n9, 1.4f - n11, n12, n14, n16);
            instance.vertexUV(n8 - n9, 1.4f - n11, n12, n15, n16);
            n10 -= 0.45f;
            n11 -= 0.45f;
            n8 *= 0.9f;
            n12 += 0.03f;
            ++n13;
        }
        instance.end();
        GL11.glPopMatrix();
        GL11.glEnable(GL_LIGHTING);
    }
    
    private void renderShadow(final Entity e, final double x, final double y, final double z, final float pow, final float partialTick) {
        GL11.glEnable(GL_BLEND);
        GL11.glBlendFunc(770, 771);
        final Textures textures = this.entityRenderDispatcher.textures;
        textures.bind(textures.loadTexture("%clamp%/misc/shadow.png"));
        final Level level = this.getLevel();
        GL11.glDepthMask(false);
        final float shadowRadius = this.shadowRadius;
        final double n = e.xOld + (e.x - e.xOld) * partialTick;
        final double v = e.yOld + (e.y - e.yOld) * partialTick + e.getShadowHeightOffs();
        final double n2 = e.zOld + (e.z - e.zOld) * partialTick;
        final int floor = Mth.floor(n - shadowRadius);
        final int floor2 = Mth.floor(n + shadowRadius);
        final int floor3 = Mth.floor(v - shadowRadius);
        final int floor4 = Mth.floor(v);
        final int floor5 = Mth.floor(n2 - shadowRadius);
        final int floor6 = Mth.floor(n2 + shadowRadius);
        final double xo = x - n;
        final double n3 = y - v;
        final double zo = z - n2;
        final Tesselator instance = Tesselator.instance;
        instance.begin();
        for (int i = floor; i <= floor2; ++i) {
            for (int j = floor3; j <= floor4; ++j) {
                for (int k = floor5; k <= floor6; ++k) {
                    final int tile = level.getTile(i, j - 1, k);
                    if (tile > 0 && level.getRawBrightness(i, j, k) > 3) {
                        this.renderTileShadow(Tile.tiles[tile], x, y + e.getShadowHeightOffs(), z, i, j, k, pow, shadowRadius, xo, n3 + e.getShadowHeightOffs(), zo);
                    }
                }
            }
        }
        instance.end();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
    }
    
    private Level getLevel() {
        return this.entityRenderDispatcher.level;
    }
    
    private void renderTileShadow(final Tile tt, final double x, final double y, final double z, final int xt, final int yt, final int zt, final float pow, final float r, final double xo, final double yo, final double zo) {
        final Tesselator instance = Tesselator.instance;
        if (!tt.isCubeShaped()) {
            return;
        }
        double n = (pow - (y - (yt + yo)) / 2.0) * 0.5 * this.getLevel().getBrightness(xt, yt, zt);
        if (n < 0.0) {
            return;
        }
        if (n > 1.0) {
            n = 1.0;
        }
        instance.color(1.0f, 1.0f, 1.0f, (float)n);
        final double n2 = xt + tt.xx0 + xo;
        final double n3 = xt + tt.xx1 + xo;
        final double n4 = yt + tt.yy0 + yo + 0.015625;
        final double n5 = zt + tt.zz0 + zo;
        final double n6 = zt + tt.zz1 + zo;
        final float n7 = (float)((x - n2) / 2.0 / r + 0.5);
        final float n8 = (float)((x - n3) / 2.0 / r + 0.5);
        final float n9 = (float)((z - n5) / 2.0 / r + 0.5);
        final float n10 = (float)((z - n6) / 2.0 / r + 0.5);
        instance.vertexUV(n2, n4, n5, n7, n9);
        instance.vertexUV(n2, n4, n6, n7, n10);
        instance.vertexUV(n3, n4, n6, n8, n10);
        instance.vertexUV(n3, n4, n5, n8, n9);
    }
    
    public static void render(final AABB bb, final double xo, final double yo, final double zo) {
        GL11.glDisable(GL_TEXTURE_2D);
        final Tesselator instance = Tesselator.instance;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        instance.begin();
        instance.offset(xo, yo, zo);
        instance.normal(0.0f, 0.0f, -1.0f);
        instance.vertex(bb.x0, bb.y1, bb.z0);
        instance.vertex(bb.x1, bb.y1, bb.z0);
        instance.vertex(bb.x1, bb.y0, bb.z0);
        instance.vertex(bb.x0, bb.y0, bb.z0);
        instance.normal(0.0f, 0.0f, 1.0f);
        instance.vertex(bb.x0, bb.y0, bb.z1);
        instance.vertex(bb.x1, bb.y0, bb.z1);
        instance.vertex(bb.x1, bb.y1, bb.z1);
        instance.vertex(bb.x0, bb.y1, bb.z1);
        instance.normal(0.0f, -1.0f, 0.0f);
        instance.vertex(bb.x0, bb.y0, bb.z0);
        instance.vertex(bb.x1, bb.y0, bb.z0);
        instance.vertex(bb.x1, bb.y0, bb.z1);
        instance.vertex(bb.x0, bb.y0, bb.z1);
        instance.normal(0.0f, 1.0f, 0.0f);
        instance.vertex(bb.x0, bb.y1, bb.z1);
        instance.vertex(bb.x1, bb.y1, bb.z1);
        instance.vertex(bb.x1, bb.y1, bb.z0);
        instance.vertex(bb.x0, bb.y1, bb.z0);
        instance.normal(-1.0f, 0.0f, 0.0f);
        instance.vertex(bb.x0, bb.y0, bb.z1);
        instance.vertex(bb.x0, bb.y1, bb.z1);
        instance.vertex(bb.x0, bb.y1, bb.z0);
        instance.vertex(bb.x0, bb.y0, bb.z0);
        instance.normal(1.0f, 0.0f, 0.0f);
        instance.vertex(bb.x1, bb.y0, bb.z0);
        instance.vertex(bb.x1, bb.y1, bb.z0);
        instance.vertex(bb.x1, bb.y1, bb.z1);
        instance.vertex(bb.x1, bb.y0, bb.z1);
        instance.offset(0.0, 0.0, 0.0);
        instance.end();
        GL11.glEnable(GL_TEXTURE_2D);
    }
    
    public static void renderFlat(final AABB bb) {
        final Tesselator instance = Tesselator.instance;
        instance.begin();
        instance.vertex(bb.x0, bb.y1, bb.z0);
        instance.vertex(bb.x1, bb.y1, bb.z0);
        instance.vertex(bb.x1, bb.y0, bb.z0);
        instance.vertex(bb.x0, bb.y0, bb.z0);
        instance.vertex(bb.x0, bb.y0, bb.z1);
        instance.vertex(bb.x1, bb.y0, bb.z1);
        instance.vertex(bb.x1, bb.y1, bb.z1);
        instance.vertex(bb.x0, bb.y1, bb.z1);
        instance.vertex(bb.x0, bb.y0, bb.z0);
        instance.vertex(bb.x1, bb.y0, bb.z0);
        instance.vertex(bb.x1, bb.y0, bb.z1);
        instance.vertex(bb.x0, bb.y0, bb.z1);
        instance.vertex(bb.x0, bb.y1, bb.z1);
        instance.vertex(bb.x1, bb.y1, bb.z1);
        instance.vertex(bb.x1, bb.y1, bb.z0);
        instance.vertex(bb.x0, bb.y1, bb.z0);
        instance.vertex(bb.x0, bb.y0, bb.z1);
        instance.vertex(bb.x0, bb.y1, bb.z1);
        instance.vertex(bb.x0, bb.y1, bb.z0);
        instance.vertex(bb.x0, bb.y0, bb.z0);
        instance.vertex(bb.x1, bb.y0, bb.z0);
        instance.vertex(bb.x1, bb.y1, bb.z0);
        instance.vertex(bb.x1, bb.y1, bb.z1);
        instance.vertex(bb.x1, bb.y0, bb.z1);
        instance.end();
    }
    
    public void init(final EntityRenderDispatcher entityRenderDispatcher) {
        this.entityRenderDispatcher = entityRenderDispatcher;
    }
    
    public void postRender(final Entity entity, final double x, final double y, final double z, final float rot, final float partialTick) {
        if (this.entityRenderDispatcher.options.fancyGraphics && this.shadowRadius > 0.0f) {
            final float pow = (float)((1.0 - this.entityRenderDispatcher.distanceToSqr(entity.x, entity.y, entity.z) / 256.0) * this.shadowStrength);
            if (pow > 0.0f) {
                this.renderShadow(entity, x, y, z, pow, partialTick);
            }
        }
        if (entity.isOnFire()) {
            this.renderFlame(entity, x, y, z, partialTick);
        }
    }
    
    public Font getFont() {
        return this.entityRenderDispatcher.getFont();
    }
}
