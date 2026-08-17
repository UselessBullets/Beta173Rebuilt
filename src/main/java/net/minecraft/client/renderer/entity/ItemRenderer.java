// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.Textures;
import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.Item;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.level.tile.Tile;
import util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import java.util.Random;
import net.minecraft.client.renderer.TileRenderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

public class ItemRenderer extends EntityRenderer<ItemEntity>
{
    private TileRenderer tileRenderer;
    private Random random;
    public boolean setColor;
    
    public ItemRenderer() {
        this.tileRenderer = new TileRenderer();
        this.random = new Random();
        this.setColor = true;
        this.shadowRadius = 0.15f;
        this.shadowStrength = 0.75f;
    }
    
    public void render(final ItemEntity entity, final double x, final double y, final double z, final float rot, final float a) {
        this.random.setSeed(187L);
        final ItemInstance item = entity.item;

        glPushMatrix();
        final float bob = Mth.sin((entity.age + a) / 10.0f + entity.bobOffs) * 0.1f + 0.1f;
        final float spin = ((entity.age + a) / 20.0f + entity.bobOffs) * Mth.RADDEG;

        int count = 1;
        if (entity.item.count > 1) count = 2;
        if (entity.item.count > 5) count = 3;
        if (entity.item.count > 20) count = 4;

        glTranslatef((float)x, (float)y + bob, (float)z);
        glEnable(GL_RESCALE_NORMAL);

        if (item.id < Tile.TILE_NUM_COUNT && TileRenderer.canRender(Tile.tiles[item.id].getRenderShape())) {
            glRotatef(spin, 0.0f, 1.0f, 0.0f);

            this.bindTexture("/terrain.png");
            float s = 1 / 4.0f;
            if (!Tile.tiles[item.id].isCubeShaped() && item.id != Tile.stoneSlabHalf.id && Tile.tiles[item.id].getRenderShape() != Tile.SHAPE_PISTON_BASE) {
                s = 0.5f;
            }

            glScalef(s, s, s);
            for (int i = 0; i < count; ++i) {
                glPushMatrix();
                if (i > 0) {
                    float xo = (this.random.nextFloat() * 2.0f - 1.0f) * 0.2f / s;
                    float yo = (this.random.nextFloat() * 2.0f - 1.0f) * 0.2f / s;
                    float zo = (this.random.nextFloat() * 2.0f - 1.0f) * 0.2f / s;
                    glTranslatef(xo, yo, zo);
                }
                this.tileRenderer.renderTile(Tile.tiles[item.id], item.getAuxValue(), entity.getBrightness(a));
                glPopMatrix();
            }
        }
        else {
            glScalef(1 / 2.0f, 1 / 2.0f, 1 / 2.0f);
            final int icon = item.getIcon();
            if (item.id < Tile.TILE_NUM_COUNT) {
                this.bindTexture("/terrain.png");
            }
            else {
                this.bindTexture("/gui/items.png");
            }

            final Tesselator t = Tesselator.instance;
            final float u0 = (icon % 16 * 16 + 0) / 256.0f;
            final float u1 = (icon % 16 * 16 + 16) / 256.0f;
            final float v0 = (icon / 16 * 16 + 0) / 256.0f;
            final float v1 = (icon / 16 * 16 + 16) / 256.0f;

            final float r = 1.0f;
            final float xo = 0.5f;
            final float yo = 0.25f;

            if (this.setColor) {
                final int col = Item.items[item.id].getColor(item.getAuxValue());
                final float red = (col >> 16 & 0xFF) / 255.0f;
                final float g = (col >> 8 & 0xFF) / 255.0f;
                final float b = (col & 0xFF) / 255.0f;
                final float br = entity.getBrightness(a);

                glColor4f(red * br, g * br, b * br, 1.0f);
            }

            for (int i = 0; i < count; ++i) {
                glPushMatrix();
                if (i > 0) {
                    float _xo = (this.random.nextFloat() * 2.0f - 1.0f) * 0.3f;
                    float _yo = (this.random.nextFloat() * 2.0f - 1.0f) * 0.3f;
                    float _zo = (this.random.nextFloat() * 2.0f - 1.0f) * 0.3f;
                    glTranslatef(_xo, _yo, _zo);
                }
                glRotatef(180.0f - this.entityRenderDispatcher.playerRotY, 0.0f, 1.0f, 0.0f);
                t.begin();
                t.normal(0.0f, 1.0f, 0.0f);
                t.vertexUV(0 - xo, 0 - yo, 0, u0, v1);
                t.vertexUV(r - xo, 0 - yo, 0, u1, v1);
                t.vertexUV(r - xo, 1 - yo, 0, u1, v0);
                t.vertexUV(0 - xo, 1 - yo, 0, u0, v0);
                t.end();

                glPopMatrix();
            }
        }
        glDisable(GL_RESCALE_NORMAL);
        glPopMatrix();
    }
    
    public void renderGuiItem(final Font font, final Textures textures, final int itemId, final int itemAuxValue, final int itemIcon, final int x, final int y) {
        if (itemId < Tile.TILE_NUM_COUNT && TileRenderer.canRender(Tile.tiles[itemId].getRenderShape())) {
            textures.bind(textures.loadTexture("/terrain.png"));
            final Tile tile = Tile.tiles[itemId];
            glPushMatrix();

            glTranslatef((float)(x - 2), (float)(y + 3), -3.0f);
            glScalef(10.0f, 10.0f, 10.0f);
            glTranslatef(1.0f, 0.5f, 1.0f);
            glScalef(1.0f, 1.0f, -1.0f);
            glRotatef(180.0f + 30.0f, 1.0f, 0.0f, 0.0f);
            glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
            final int col = Item.items[itemId].getColor(itemAuxValue);
            final float r = (col >> 16 & 0xFF) / 255.0f;
            final float g = (col >> 8 & 0xFF) / 255.0f;
            final float b = (col & 0xFF) / 255.0f;
            if (this.setColor) {
                glColor4f(r, g, b, 1.0f);
            }
            glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
            this.tileRenderer.setColor = this.setColor;
            this.tileRenderer.renderTile(tile, itemAuxValue, 1.0f);
            this.tileRenderer.setColor = true;
            glPopMatrix();
        }
        else if (itemIcon >= 0) {
            glDisable(GL_LIGHTING);
            if (itemId < Tile.TILE_NUM_COUNT) {
                textures.bind(textures.loadTexture("/terrain.png"));
            }
            else {
                textures.bind(textures.loadTexture("/gui/items.png"));
            }

            final int col = Item.items[itemId].getColor(itemAuxValue);
            final float r = (col >> 16 & 0xFF) / 255.0f;
            final float g = (col >> 8 & 0xFF) / 255.0f;
            final float b = (col & 0xFF) / 255.0f;
            if (this.setColor) {
                glColor4f(r, g, b, 1.0f);
            }

            this.blit(x, y, itemIcon % 16 * 16, itemIcon / 16 * 16, 16, 16);
            glEnable(GL_LIGHTING);
        }
        glEnable(GL_CULL_FACE);
    }
    
    public void renderGuiItem(final Font font, final Textures textures, final ItemInstance item, final int x, final int y) {
        if (item == null) return;
        this.renderGuiItem(font, textures, item.id, item.getAuxValue(), item.getIcon(), x, y);
    }
    
    public void renderGuiItemDecorations(final Font font, final Textures textures, final ItemInstance item, final int x, final int y) {
        if (item == null) return;

        if (item.count > 1) {
            final String amount = "" + item.count;
            glDisable(GL_LIGHTING);
            glDisable(GL_DEPTH_TEST);
            font.drawShadow(amount, x + 19 - 2 - font.width(amount), y + 6 + 3, 0xffffff);
            glEnable(GL_LIGHTING);
            glEnable(GL_DEPTH_TEST);
        }
        if (item.isDamaged()) {
            final int p = (int)Math.round(13.0 - item.getDamageValue() * 13.0 / item.getMaxDamage());
            final int cc = (int)Math.round(255.0 - item.getDamageValue() * 255.0 / item.getMaxDamage());
            glDisable(GL_LIGHTING);
            glDisable(GL_DEPTH_TEST);
            glDisable(GL_TEXTURE_2D);

            final Tesselator t = Tesselator.instance;
            final int ca = ((0xFF - cc) << 16) | (cc << 8);
            final int cb = (((0xFF - cc) / 4) << 16) | (0xFF / 4) << 8;
            this.fillRect(t, x + 2, y + 13, 13, 2, 0x000000);
            this.fillRect(t, x + 2, y + 13, 12, 1, cb);
            this.fillRect(t, x + 2, y + 13, p, 1, ca);

            glEnable(GL_TEXTURE_2D);
            glEnable(GL_LIGHTING);
            glEnable(GL_DEPTH_TEST);
            glColor4f(1, 1, 1, 1);
        }
    }
    
    private void fillRect(final Tesselator t, final int x, final int y, final int w, final int h, final int c) {
        t.begin();
        t.color(c);
        t.vertex(x + 0, y + 0, 0.0);
        t.vertex(x + 0, y + h, 0.0);
        t.vertex(x + w, y + h, 0.0);
        t.vertex(x + w, y + 0, 0.0);
        t.end();
    }
    
    public void blit(final int x, final int y, final int sx, final int sy, final int w, final int h) {
        final float blitOffset = 0.0f;
        final float us = 1 / 256.0f;
        final float vs = 1 / 256.0f;
        final Tesselator t = Tesselator.instance;
        t.begin();
        t.vertexUV(x + 0, y + h, blitOffset, (sx + 0) * us, (sy + h) * vs);
        t.vertexUV(x + w, y + h, blitOffset, (sx + w) * us, (sy + h) * vs);
        t.vertexUV(x + w, y + 0, blitOffset, (sx + w) * us, (sy + 0) * vs);
        t.vertexUV(x + 0, y + 0, blitOffset, (sx + 0) * us, (sy + 0) * vs);
        t.end();
    }
}
