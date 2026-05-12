// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.SharedConstants;
import java.awt.image.BufferedImage;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tesselator;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.minecraft.client.MemoryTracker;
import net.minecraft.client.renderer.Textures;
import net.minecraft.client.Options;
import java.nio.IntBuffer;

public class Font
{
    private int[] charWidths;
    public int fontTexture;
    private int listPos;
    private IntBuffer ib;
    
    public Font(final Options options, final String name, final Textures textures) {
        this.charWidths = new int[256];
        this.fontTexture = 0;
        this.ib = MemoryTracker.createIntBuffer(1024);
        BufferedImage read;
        try {
            read = ImageIO.read(Textures.class.getResourceAsStream(name));
        }
        catch (final IOException cause) {
            throw new RuntimeException(cause);
        }
        final int width = read.getWidth();
        final int height = read.getHeight();
        final int[] rgbArray = new int[width * height];
        read.getRGB(0, 0, width, height, rgbArray, 0, width);
        for (int i = 0; i < 256; ++i) {
            final int n = i % 16;
            final int n2 = i / 16;
            int j;
            for (j = 7; j >= 0; --j) {
                final int n3 = n * 8 + j;
                int n4 = 1;
                for (int n5 = 0; n5 < 8 && n4 != 0; ++n5) {
                    if ((rgbArray[n3 + (n2 * 8 + n5) * width] & 0xFF) > 0) {
                        n4 = 0;
                    }
                }
                if (n4 == 0) {
                    break;
                }
            }
            if (i == 32) {
                j = 2;
            }
            this.charWidths[i] = j + 2;
        }
        this.fontTexture = textures.getTexture(read);
        this.listPos = MemoryTracker.genLists(288);
        final Tesselator instance = Tesselator.instance;
        for (int k = 0; k < 256; ++k) {
            GL11.glNewList(this.listPos + k, 4864);
            instance.begin();
            final int n6 = k % 16 * 8;
            final int n7 = k / 16 * 8;
            final float n8 = 7.99f;
            final float n9 = 0.0f;
            final float n10 = 0.0f;
            instance.vertexUV(0.0, 0.0f + n8, 0.0, n6 / 128.0f + n9, (n7 + n8) / 128.0f + n10);
            instance.vertexUV(0.0f + n8, 0.0f + n8, 0.0, (n6 + n8) / 128.0f + n9, (n7 + n8) / 128.0f + n10);
            instance.vertexUV(0.0f + n8, 0.0, 0.0, (n6 + n8) / 128.0f + n9, n7 / 128.0f + n10);
            instance.vertexUV(0.0, 0.0, 0.0, n6 / 128.0f + n9, n7 / 128.0f + n10);
            instance.end();
            GL11.glTranslatef((float)this.charWidths[k], 0.0f, 0.0f);
            GL11.glEndList();
        }
        for (int l = 0; l < 32; ++l) {
            final int n11 = (l >> 3 & 0x1) * 85;
            int n12 = (l >> 2 & 0x1) * 170 + n11;
            int n13 = (l >> 1 & 0x1) * 170 + n11;
            int n14 = (l >> 0 & 0x1) * 170 + n11;
            if (l == 6) {
                n12 += 85;
            }
            final boolean b = l >= 16;
            if (options.anaglyph3d) {
                final int n15 = (n12 * 30 + n13 * 59 + n14 * 11) / 100;
                final int n16 = (n12 * 30 + n13 * 70) / 100;
                final int n17 = (n12 * 30 + n14 * 70) / 100;
                n12 = n15;
                n13 = n16;
                n14 = n17;
            }
            if (b) {
                n12 /= 4;
                n13 /= 4;
                n14 /= 4;
            }
            GL11.glNewList(this.listPos + 256 + l, 4864);
            GL11.glColor3f(n12 / 255.0f, n13 / 255.0f, n14 / 255.0f);
            GL11.glEndList();
        }
    }
    
    public void drawShadow(final String str, final int x, final int y, final int color) {
        this.draw(str, x + 1, y + 1, color, true);
        this.draw(str, x, y, color);
    }
    
    public void draw(final String str, final int x, final int y, final int color) {
        this.draw(str, x, y, color, false);
    }
    
    public void draw(final String str, final int x, final int y, int color, final boolean darken) {
        if (str == null) {
            return;
        }
        if (darken) {
            final int n = color & 0xFF000000;
            color = (color & 0xFCFCFC) >> 2;
            color += n;
        }
        GL11.glBindTexture(3553, this.fontTexture);
        final float n2 = (color >> 16 & 0xFF) / 255.0f;
        final float n3 = (color >> 8 & 0xFF) / 255.0f;
        final float n4 = (color & 0xFF) / 255.0f;
        float n5 = (color >> 24 & 0xFF) / 255.0f;
        if (n5 == 0.0f) {
            n5 = 1.0f;
        }
        GL11.glColor4f(n2, n3, n4, n5);
        this.ib.clear();
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, 0.0f);
        for (int i = 0; i < str.length(); ++i) {
            while (str.length() > i + 1 && str.charAt(i) == '§') {
                int index = "0123456789abcdef".indexOf(str.toLowerCase().charAt(i + 1));
                if (index < 0 || index > 15) {
                    index = 15;
                }
                this.ib.put(this.listPos + 256 + index + (darken ? 16 : 0));
                if (this.ib.remaining() == 0) {
                    this.ib.flip();
                    GL11.glCallLists(this.ib);
                    this.ib.clear();
                }
                i += 2;
            }
            if (i < str.length()) {
                final int index2 = SharedConstants.acceptableLetters.indexOf(str.charAt(i));
                if (index2 >= 0) {
                    this.ib.put(this.listPos + index2 + 32);
                }
            }
            if (this.ib.remaining() == 0) {
                this.ib.flip();
                GL11.glCallLists(this.ib);
                this.ib.clear();
            }
        }
        this.ib.flip();
        GL11.glCallLists(this.ib);
        GL11.glPopMatrix();
    }
    
    public int width(final String str) {
        if (str == null) {
            return 0;
        }
        int n = 0;
        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) == '§') {
                ++i;
            }
            else {
                final int index = SharedConstants.acceptableLetters.indexOf(str.charAt(i));
                if (index >= 0) {
                    n += this.charWidths[index + 32];
                }
            }
        }
        return n;
    }
    
    public void drawWordWrapInternal(final String str, final int x, int y, final int w, final int col) {
        final String[] split = str.split("\n");
        if (split.length > 1) {
            for (int i = 0; i < split.length; ++i) {
                this.drawWordWrapInternal(split[i], x, y, w, col);
                y += this.wordWrapHeight(split[i], w);
            }
            return;
        }
        final String[] split2 = str.split(" ");
        int j = 0;
        while (j < split2.length) {
            String s;
            for (s = split2[j++] + " "; j < split2.length && this.width(s + split2[j]) < w; s = s + split2[j++] + " ") {}
            while (this.width(s) > w) {
                int beginIndex;
                for (beginIndex = 0; this.width(s.substring(0, beginIndex + 1)) <= w; ++beginIndex) {}
                if (s.substring(0, beginIndex).trim().length() > 0) {
                    this.draw(s.substring(0, beginIndex), x, y, col);
                    y += 8;
                }
                s = s.substring(beginIndex);
            }
            if (s.trim().length() > 0) {
                this.draw(s, x, y, col);
                y += 8;
            }
        }
    }
    
    public int wordWrapHeight(final String str, final int w) {
        final String[] split = str.split("\n");
        if (split.length > 1) {
            int n = 0;
            for (int i = 0; i < split.length; ++i) {
                n += this.wordWrapHeight(split[i], w);
            }
            return n;
        }
        final String[] split2 = str.split(" ");
        int j = 0;
        int n2 = 0;
        while (j < split2.length) {
            String str2;
            for (str2 = split2[j++] + " "; j < split2.length && this.width(str2 + split2[j]) < w; str2 = str2 + split2[j++] + " ") {}
            while (this.width(str2) > w) {
                int n3;
                for (n3 = 0; this.width(str2.substring(0, n3 + 1)) <= w; ++n3) {}
                if (str2.substring(0, n3).trim().length() > 0) {
                    n2 += 8;
                }
                str2 = str2.substring(n3);
            }
            if (str2.trim().length() > 0) {
                n2 += 8;
            }
        }
        if (n2 < 8) {
            n2 += 8;
        }
        return n2;
    }
}
