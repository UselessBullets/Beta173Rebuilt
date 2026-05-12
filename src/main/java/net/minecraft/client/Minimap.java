// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import java.util.Iterator;
import net.minecraft.world.level.saveddata.MapItemSavedData_MapDecoration;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.saveddata.MapItemSavedData;
import net.minecraft.world.entity.player.Player;
import java.awt.image.BufferedImage;
import net.minecraft.client.renderer.Textures;
import net.minecraft.client.gui.Font;

public class Minimap
{
    private int[] pixels;
    private int mapTexture;
    private Options options;
    private Font font;
    
    public Minimap(final Font font, final Options options, final Textures textures) {
        this.pixels = new int[16384];
        this.options = options;
        this.font = font;
        this.mapTexture = textures.getTexture(new BufferedImage(128, 128, 2));
        for (int i = 0; i < 16384; ++i) {
            this.pixels[i] = 0;
        }
    }
    
    public void render(final Player player, final Textures textures, final MapItemSavedData data) {
        for (int i = 0; i < 16384; ++i) {
            final byte b = data.colors[i];
            if (b / 4 == 0) {
                this.pixels[i] = (i + i / 128 & 0x1) * 8 + 16 << 24;
            }
            else {
                final int col = MaterialColor.colors[b / 4].col;
                final int n = b & 0x3;
                int n2 = 220;
                if (n == 2) {
                    n2 = 255;
                }
                if (n == 0) {
                    n2 = 180;
                }
                int n3 = (col >> 16 & 0xFF) * n2 / 255;
                int n4 = (col >> 8 & 0xFF) * n2 / 255;
                int n5 = (col & 0xFF) * n2 / 255;
                if (this.options.anaglyph3d) {
                    final int n6 = (n3 * 30 + n4 * 59 + n5 * 11) / 100;
                    final int n7 = (n3 * 30 + n4 * 70) / 100;
                    final int n8 = (n3 * 30 + n5 * 70) / 100;
                    n3 = n6;
                    n4 = n7;
                    n5 = n8;
                }
                this.pixels[i] = (0xFF000000 | n3 << 16 | n4 << 8 | n5);
            }
        }
        textures.replaceTextureDirect(this.pixels, 128, 128, this.mapTexture);
        final int x = 0;
        final int y = 0;
        final Tesselator instance = Tesselator.instance;
        final float n9 = 0.0f;
        GL11.glBindTexture(3553, this.mapTexture);
        GL11.glEnable(3042);
        GL11.glDisable(3008);
        instance.begin();
        instance.vertexUV(x + 0 + n9, y + 128 - n9, -0.009999999776482582, 0.0, 1.0);
        instance.vertexUV(x + 128 - n9, y + 128 - n9, -0.009999999776482582, 1.0, 1.0);
        instance.vertexUV(x + 128 - n9, y + 0 + n9, -0.009999999776482582, 1.0, 0.0);
        instance.vertexUV(x + 0 + n9, y + 0 + n9, -0.009999999776482582, 0.0, 0.0);
        instance.end();
        GL11.glEnable(3008);
        GL11.glDisable(3042);
        textures.bind(textures.loadTexture("/misc/mapicons.png"));
        for (final MapItemSavedData_MapDecoration mapItemSavedData_MapDecoration : data.decorations) {
            GL11.glPushMatrix();
            GL11.glTranslatef(x + mapItemSavedData_MapDecoration.x / 2.0f + 64.0f, y + mapItemSavedData_MapDecoration.y / 2.0f + 64.0f, -0.02f);
            GL11.glRotatef(mapItemSavedData_MapDecoration.rot * 360 / 16.0f, 0.0f, 0.0f, 1.0f);
            GL11.glScalef(4.0f, 4.0f, 3.0f);
            GL11.glTranslatef(-0.125f, 0.125f, 0.0f);
            final float n10 = (mapItemSavedData_MapDecoration.imgIndex % 4 + 0) / 4.0f;
            final float n11 = (mapItemSavedData_MapDecoration.imgIndex / 4 + 0) / 4.0f;
            final float n12 = (mapItemSavedData_MapDecoration.imgIndex % 4 + 1) / 4.0f;
            final float n13 = (mapItemSavedData_MapDecoration.imgIndex / 4 + 1) / 4.0f;
            instance.begin();
            instance.vertexUV(-1.0, 1.0, 0.0, n10, n11);
            instance.vertexUV(1.0, 1.0, 0.0, n12, n11);
            instance.vertexUV(1.0, -1.0, 0.0, n12, n13);
            instance.vertexUV(-1.0, -1.0, 0.0, n10, n13);
            instance.end();
            GL11.glPopMatrix();
        }
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, 0.0f, -0.04f);
        GL11.glScalef(1.0f, 1.0f, 1.0f);
        this.font.draw(data.id, x, y, -16777216);
        GL11.glPopMatrix();
    }
}
