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
import org.lwjgl.opengl.GL11;
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
        GL11.glPushMatrix();
        final float n = Mth.sin((entity.age + a) / 10.0f + entity.bobOffs) * 0.1f + 0.1f;
        final float n2 = ((entity.age + a) / 20.0f + entity.bobOffs) * Mth.RADDEG;
        int n3 = 1;
        if (entity.item.count > 1) {
            n3 = 2;
        }
        if (entity.item.count > 5) {
            n3 = 3;
        }
        if (entity.item.count > 20) {
            n3 = 4;
        }
        GL11.glTranslatef((float)x, (float)y + n, (float)z);
        GL11.glEnable(GL_RESCALE_NORMAL);
        if (item.id < 256 && TileRenderer.canRender(Tile.tiles[item.id].getRenderShape())) {
            GL11.glRotatef(n2, 0.0f, 1.0f, 0.0f);
            this.bindTexture("/terrain.png");
            float n4 = 0.25f;
            if (!Tile.tiles[item.id].isCubeShaped() && item.id != Tile.stoneSlabHalf.id && Tile.tiles[item.id].getRenderShape() != 16) {
                n4 = 0.5f;
            }
            GL11.glScalef(n4, n4, n4);
            for (int i = 0; i < n3; ++i) {
                GL11.glPushMatrix();
                if (i > 0) {
                    GL11.glTranslatef((this.random.nextFloat() * 2.0f - 1.0f) * 0.2f / n4, (this.random.nextFloat() * 2.0f - 1.0f) * 0.2f / n4, (this.random.nextFloat() * 2.0f - 1.0f) * 0.2f / n4);
                }
                this.tileRenderer.renderTile(Tile.tiles[item.id], item.getAuxValue(), entity.getBrightness(a));
                GL11.glPopMatrix();
            }
        }
        else {
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            final int icon = item.getIcon();
            if (item.id < 256) {
                this.bindTexture("/terrain.png");
            }
            else {
                this.bindTexture("/gui/items.png");
            }
            final Tesselator instance = Tesselator.instance;
            final float n5 = (icon % 16 * 16 + 0) / 256.0f;
            final float n6 = (icon % 16 * 16 + 16) / 256.0f;
            final float n7 = (icon / 16 * 16 + 0) / 256.0f;
            final float n8 = (icon / 16 * 16 + 16) / 256.0f;
            final float n9 = 1.0f;
            final float n10 = 0.5f;
            final float n11 = 0.25f;
            if (this.setColor) {
                final int color = Item.items[item.id].getColor(item.getAuxValue());
                final float n12 = (color >> 16 & 0xFF) / 255.0f;
                final float n13 = (color >> 8 & 0xFF) / 255.0f;
                final float n14 = (color & 0xFF) / 255.0f;
                final float brightness = entity.getBrightness(a);
                GL11.glColor4f(n12 * brightness, n13 * brightness, n14 * brightness, 1.0f);
            }
            for (int j = 0; j < n3; ++j) {
                GL11.glPushMatrix();
                if (j > 0) {
                    GL11.glTranslatef((this.random.nextFloat() * 2.0f - 1.0f) * 0.3f, (this.random.nextFloat() * 2.0f - 1.0f) * 0.3f, (this.random.nextFloat() * 2.0f - 1.0f) * 0.3f);
                }
                GL11.glRotatef(180.0f - this.entityRenderDispatcher.playerRotY, 0.0f, 1.0f, 0.0f);
                instance.begin();
                instance.normal(0.0f, 1.0f, 0.0f);
                instance.vertexUV(0.0f - n10, 0.0f - n11, 0.0, n5, n8);
                instance.vertexUV(n9 - n10, 0.0f - n11, 0.0, n6, n8);
                instance.vertexUV(n9 - n10, 1.0f - n11, 0.0, n6, n7);
                instance.vertexUV(0.0f - n10, 1.0f - n11, 0.0, n5, n7);
                instance.end();
                GL11.glPopMatrix();
            }
        }
        GL11.glDisable(GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }
    
    public void renderGuiItem(final Font font, final Textures textures, final int id, final int auxData, final int icon, final int x, final int y) {
        if (id < 256 && TileRenderer.canRender(Tile.tiles[id].getRenderShape())) {
            textures.bind(textures.loadTexture("/terrain.png"));
            final Tile tile = Tile.tiles[id];
            GL11.glPushMatrix();
            GL11.glTranslatef((float)(x - 2), (float)(y + 3), -3.0f);
            GL11.glScalef(10.0f, 10.0f, 10.0f);
            GL11.glTranslatef(1.0f, 0.5f, 1.0f);
            GL11.glScalef(1.0f, 1.0f, -1.0f);
            GL11.glRotatef(210.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
            final int color = Item.items[id].getColor(auxData);
            final float n = (color >> 16 & 0xFF) / 255.0f;
            final float n2 = (color >> 8 & 0xFF) / 255.0f;
            final float n3 = (color & 0xFF) / 255.0f;
            if (this.setColor) {
                GL11.glColor4f(n, n2, n3, 1.0f);
            }
            GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
            this.tileRenderer.setColor = this.setColor;
            this.tileRenderer.renderTile(tile, auxData, 1.0f);
            this.tileRenderer.setColor = true;
            GL11.glPopMatrix();
        }
        else if (icon >= 0) {
            GL11.glDisable(GL_LIGHTING);
            if (id < 256) {
                textures.bind(textures.loadTexture("/terrain.png"));
            }
            else {
                textures.bind(textures.loadTexture("/gui/items.png"));
            }
            final int color2 = Item.items[id].getColor(auxData);
            final float n4 = (color2 >> 16 & 0xFF) / 255.0f;
            final float n5 = (color2 >> 8 & 0xFF) / 255.0f;
            final float n6 = (color2 & 0xFF) / 255.0f;
            if (this.setColor) {
                GL11.glColor4f(n4, n5, n6, 1.0f);
            }
            this.blit(x, y, icon % 16 * 16, icon / 16 * 16, 16, 16);
            GL11.glEnable(GL_LIGHTING);
        }
        GL11.glEnable(GL_CULL_FACE);
    }
    
    public void renderGuiItem(final Font font, final Textures textures, final ItemInstance item, final int x, final int y) {
        if (item == null) {
            return;
        }
        this.renderGuiItem(font, textures, item.id, item.getAuxValue(), item.getIcon(), x, y);
    }
    
    public void renderGuiItemDecorations(final Font font, final Textures textures, final ItemInstance item, final int x, final int y) {
        if (item == null) {
            return;
        }
        if (item.count > 1) {
            final String string = "" + item.count;
            GL11.glDisable(GL_LIGHTING);
            GL11.glDisable(GL_DEPTH_TEST);
            font.drawShadow(string, x + 19 - 2 - font.width(string), y + 6 + 3, 0xffffff);
            GL11.glEnable(GL_LIGHTING);
            GL11.glEnable(GL_DEPTH_TEST);
        }
        if (item.isDamaged()) {
            final int w = (int)Math.round(13.0 - item.getDamageValue() * 13.0 / item.getMaxDamage());
            final int n = (int)Math.round(255.0 - item.getDamageValue() * 255.0 / item.getMaxDamage());
            GL11.glDisable(GL_LIGHTING);
            GL11.glDisable(GL_DEPTH_TEST);
            GL11.glDisable(GL_TEXTURE_2D);
            final Tesselator instance = Tesselator.instance;
            final int c = 255 - n << 16 | n << 8;
            final int c2 = (255 - n) / 4 << 16 | 0x3F00;
            this.fillRect(instance, x + 2, y + 13, 13, 2, 0x0);
            this.fillRect(instance, x + 2, y + 13, 12, 1, c2);
            this.fillRect(instance, x + 2, y + 13, w, 1, c);
            GL11.glEnable(GL_TEXTURE_2D);
            GL11.glEnable(GL_LIGHTING);
            GL11.glEnable(GL_DEPTH_TEST);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
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
        final float n = 0.0f;
        final float n2 = 0.00390625f;
        final float n3 = 0.00390625f;
        final Tesselator instance = Tesselator.instance;
        instance.begin();
        instance.vertexUV(x + 0, y + h, n, (sx + 0) * n2, (sy + h) * n3);
        instance.vertexUV(x + w, y + h, n, (sx + w) * n2, (sy + h) * n3);
        instance.vertexUV(x + w, y + 0, n, (sx + w) * n2, (sy + 0) * n3);
        instance.vertexUV(x + 0, y + 0, n, (sx + 0) * n2, (sy + 0) * n3);
        instance.end();
    }
}
