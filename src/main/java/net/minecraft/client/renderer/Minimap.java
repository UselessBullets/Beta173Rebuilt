// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer;

import net.minecraft.client.Options;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.saveddata.MapItemSavedData;
import net.minecraft.world.entity.player.Player;
import java.awt.image.BufferedImage;

import net.minecraft.client.gui.Font;

import static org.lwjgl.opengl.GL11.*;

public class Minimap
{
    private static final int w = MapItem.IMAGE_WIDTH;
    private static final int h = MapItem.IMAGE_HEIGHT;
    private int[] pixels = new int[w * h];
    private int mapTexture;
    private Options options;
    private Font font;
    
    public Minimap(final Font font, final Options options, final Textures textures) {
        this.options = options;
        this.font = font;
        this.mapTexture = textures.getTexture(new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB));
        for (int i = 0; i < w * h; ++i) {
            this.pixels[i] = 0x00000000;
        }
    }
    
    public void render(final Player player, final Textures textures, final MapItemSavedData data) {
        for (int i = 0; i < w * h; ++i) {
            final byte c = data.colors[i];
            if (c / 4 == 0) {
                this.pixels[i] = (i + i / w & 0x1) * 8 + 16 << 24;
            }
            else {
                final int color = MaterialColor.colors[c / 4].col;
                final int brightness = c & 0x3;

                int br = 220;
                if (brightness == 2) { br = 255; }
                if (brightness == 0) { br = 180; }

                int r = (color >> 16 & 0xFF) * br / 255;
                int g = (color >> 8 & 0xFF) * br / 255;
                int b = (color & 0xFF) * br / 255;

                if (this.options.anaglyph3d) {
                    r = (r * 30 + g * 59 + b * 11) / 100;
                    g = (r * 30 + g * 70) / 100;
                    b = (r * 30 + b * 70) / 100;
                }
                this.pixels[i] = (0xFF000000 | r << 16 | g << 8 | b);
            }
        }

        textures.replaceTextureDirect(this.pixels, w, h, this.mapTexture);

        final int x = 0;
        final int y = 0;
        final Tesselator t = Tesselator.instance;

        final float vo = 0.0f;

        glBindTexture(GL_TEXTURE_2D, this.mapTexture);
        glEnable(GL_BLEND);
        glDisable(GL_ALPHA_TEST);
        t.begin();
        final float offset = -0.01f;
        t.vertexUV(x + 0 + vo, y + h - vo, offset, 0.0, 1.0);
        t.vertexUV(x + w - vo, y + h - vo, offset, 1.0, 1.0);
        t.vertexUV(x + w - vo, y + 0 + vo, offset, 1.0, 0.0);
        t.vertexUV(x + 0 + vo, y + 0 + vo, offset, 0.0, 0.0);
        t.end();
        glEnable(GL_ALPHA_TEST);
        glDisable(GL_BLEND);

        textures.bind(textures.loadTexture("/misc/mapicons.png"));

        for (final MapItemSavedData.MapDecoration dec : data.decorations) {
            glPushMatrix();
            glTranslatef(x + dec.x / 2.0f + 64.0f, y + dec.y / 2.0f + 64.0f, -0.02f);
            glRotatef(dec.rot * 360 / 16.0f, 0.0f, 0.0f, 1.0f);
            glScalef(4.0f, 4.0f, 3.0f);
            glTranslatef(-0.125f, 0.125f, 0.0f);

            final float u0 = (dec.imgIndex % 4 + 0) / 4.0f;
            final float v0 = (dec.imgIndex / 4 + 0) / 4.0f;
            final float u1 = (dec.imgIndex % 4 + 1) / 4.0f;
            final float v1 = (dec.imgIndex / 4 + 1) / 4.0f;

            t.begin();
            t.vertexUV(-1.0, 1.0, 0.0, u0, v0);
            t.vertexUV(1.0, 1.0, 0.0, u1, v0);
            t.vertexUV(1.0, -1.0, 0.0, u1, v1);
            t.vertexUV(-1.0, -1.0, 0.0, u0, v1);
            t.end();
            glPopMatrix();
        }

        glPushMatrix();
        glTranslatef(0.0f, 0.0f, -0.04f);
        glScalef(1.0f, 1.0f, 1.0f);
        this.font.draw(data.id, x, y, 0xff000000);
        glPopMatrix();
    }
}
