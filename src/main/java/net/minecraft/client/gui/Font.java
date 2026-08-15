// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.SharedConstants;
import net.minecraft.client.MemoryTracker;
import net.minecraft.client.Options;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.client.renderer.Textures;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Font
{
    private int[] charWidths;
    public int fontTexture;
    private int listPos;
    private IntBuffer ib;

    // Useless - Added these font constants since they were variable in LCE leaks
    private static final int FONT_COLUMNS = 16;
    private static final int FONT_ROWS = 16;
    private static final int FONT_CHAR_WIDTH = 8;
    private static final int FONT_CHAR_HEIGHT = 8;

    public Font(final Options options, final String name, final Textures textures) {
        final int charC = FONT_COLUMNS * FONT_ROWS; // Number of characters in the font

        this.charWidths = new int[charC];
        this.fontTexture = 0;
        this.ib = MemoryTracker.createIntBuffer(1024);

        BufferedImage img;
        try {
            img = ImageIO.read(Textures.class.getResourceAsStream(name));
        }
        catch (final IOException cause) {
            throw new RuntimeException(cause);
        }

        final int w = img.getWidth();
        final int h = img.getHeight();
        final int[] rawPixels = new int[w * h];
        img.getRGB(0, 0, w, h, rawPixels, 0, w);

        for (int i = 0; i < charC; ++i) {
            final int xt = i % FONT_COLUMNS;
            final int yt = i / FONT_COLUMNS;
            int x = 7;
            for (; x >= 0; --x) {
                final int xPixel = xt * 8 + x;
                boolean emptyColumn = true;
                for (int y = 0; y < 8; ++y) {
                    int yPixel = (yt * 8 + y) * w;
                    int alpha = rawPixels[xPixel + yPixel] & 0xFF; // Check the alpha value
                    if (alpha > 0) {
                        emptyColumn = false;
                        break;
                    }
                }
                if (!emptyColumn) {
                    break;
                }
            }
            if (i == ' ') x = 4 - 2;
            this.charWidths[i] = x + 2;
        }

        this.fontTexture = textures.getTexture(img);
        this.listPos = MemoryTracker.genLists(288);
        final Tesselator t = Tesselator.instance;
        for (int i = 0; i < charC; ++i) {
            glNewList(this.listPos + i, GL_COMPILE);
            t.begin();
            final int ix = i % FONT_COLUMNS * FONT_CHAR_WIDTH;
            final int iy = i / FONT_COLUMNS * FONT_CHAR_WIDTH;
            final float s = 7.99f;
            final float uo = 0.0f;
            final float vo = 0.0f;
            final float fontWidth = FONT_COLUMNS * FONT_CHAR_WIDTH;
            final float fontHeight = FONT_ROWS * FONT_CHAR_HEIGHT;

            t.vertexUV(0.0f    , 0.0f + s, 0.0, (ix + 0) / fontWidth + uo, (iy + s) / fontHeight + vo);
            t.vertexUV(0.0f + s, 0.0f + s, 0.0, (ix + s) / fontWidth + uo, (iy + s) / fontHeight + vo);
            t.vertexUV(0.0f + s, 0.0f    , 0.0, (ix + s) / fontWidth + uo, (iy + 0) / fontHeight + vo);
            t.vertexUV(0.0f    , 0.0f    , 0.0, (ix + 0) / fontWidth + uo, (iy + 0) / fontHeight + vo);
            t.end();
            glTranslatef((float)this.charWidths[i], 0.0f, 0.0f);
            glEndList();
        }

        // calculate colors
        for (int colorN = 0; colorN < 32; ++colorN) {
            final int br = (colorN >> 3 & 0x1) * 85;
            int red = (colorN >> 2 & 0x1) * 170 + br;
            int green = (colorN >> 1 & 0x1) * 170 + br;
            int blue = (colorN >> 0 & 0x1) * 170 + br;

            if (colorN == 6) {
                red += 85;
            }

            if (options.anaglyph3d) {
                final int tmpRed = (red * 30 + green * 59 + blue * 11) / 100;
                final int tmpGreen = (red * 30 + green * 70) / 100;
                final int tmpBlue = (red * 30 + blue * 70) / 100;
                red = tmpRed;
                green = tmpGreen;
                blue = tmpBlue;
            }

            if (colorN >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }

            glNewList(this.listPos + charC + colorN, GL_COMPILE);
            glColor3f(red / 255.0f, green / 255.0f, blue / 255.0f);
            glEndList();
        }
    }
    
    public void drawShadow(final String str, final int x, final int y, final int color) {
        this.draw(str, x + 1, y + 1, color, true);
        this.draw(str, x, y, color);
    }
    
    public void draw(final String str, final int x, final int y, final int color) {
        this.draw(str, x, y, color, false);
    }
    
    public void draw(final String str, final int x, final int y, int color, final boolean dropShadow) {
        if (str != null) {
            if (dropShadow) {
                final int oldAlpha = color & 0xFF000000;
                color = (color & 0xFCFCFC) >> 2;
                color += oldAlpha;
            }

            glBindTexture(GL_TEXTURE_2D, this.fontTexture);
            final float r = (color >> 16 & 0xFF) / 255.0f;
            final float g = (color >> 8 & 0xFF) / 255.0f;
            final float b = (color & 0xFF) / 255.0f;
            float a = (color >> 24 & 0xFF) / 255.0f;
            if (a == 0.0f) {
                a = 1.0f;
            }

            glColor4f(r, g, b, a);
            this.ib.clear();
            glPushMatrix();
            glTranslatef((float) x, (float) y, 0.0f);

            for (int i = 0; i < str.length(); ++i) {
                for (; str.length() > i + 1 && str.charAt(i) == '§'; i += 2) {
                    int cc = "0123456789abcdef".indexOf(str.toLowerCase().charAt(i + 1));
                    if (cc < 0 || cc > 15) {
                        cc = 15;
                    }

                    this.ib.put(this.listPos + 256 + cc + (dropShadow ? 16 : 0));
                    if (this.ib.remaining() == 0) {
                        this.ib.flip();
                        glCallLists(this.ib);
                        this.ib.clear();
                    }
                }

                if (i < str.length()) {
                    final int ch = SharedConstants.acceptableLetters.indexOf(str.charAt(i));
                    if (ch >= 0) {
                        this.ib.put(this.listPos + ch + 32);
                    }
                }

                if (this.ib.remaining() == 0) {
                    this.ib.flip();
                    glCallLists(this.ib);
                    this.ib.clear();
                }
            }

            this.ib.flip();
            glCallLists(this.ib);
            glPopMatrix();
        }

    }
    
    public int width(final String str) {
        if (str == null) return 0;
        int len = 0;

        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);

            if (c == '§') {
                // Ignore the character used to define coloured text
                ++i;
            }
            else {
                final int index = SharedConstants.acceptableLetters.indexOf(c);
                if (index >= 0) {
                    len += this.charWidths[index + 32];
                }
            }
        }
        return len;
    }
    
    public void drawWordWrapInternal(final String str, final int x, int y, final int w, final int col) {
        final String[] lines = str.split("\n");
        if (lines.length > 1) {
            for (int i = 0; i < lines.length; ++i) {
                this.drawWordWrapInternal(lines[i], x, y, w, col);
                y += this.wordWrapHeight(lines[i], w);
            }
            return;
        }

        final String[] words = str.split(" ");
        int pos = 0;
        while (pos < words.length) {
            String line = words[pos++] + " ";
            while (pos < words.length && this.width(line + words[pos]) < w) {
                line += words[pos++] + " ";
            }

            while (this.width(line) > w) {
                int l = 0;
                while (this.width(line.substring(0, l + 1)) <= w) {
                    ++l;
                }
                if (line.substring(0, l).trim().length() > 0) {
                    this.draw(line.substring(0, l), x, y, col);
                    y += 8;
                }
                line = line.substring(l);
            }

            if (line.trim().length() > 0) {
                this.draw(line, x, y, col);
                y += 8;
            }
        }
    }
    
    public int wordWrapHeight(final String str, final int w) {
        final String[] lines = str.split("\n");
        if (lines.length > 1) {
            int h = 0;
            for (int i = 0; i < lines.length; ++i) {
                h += this.wordWrapHeight(lines[i], w);
            }
            return h;
        }

        final String[] words = str.split(" ");
        int pos = 0;
        int y = 0;
        while (pos < words.length) {
            String line = words[pos++] + " ";
            while (pos < words.length && this.width(line + words[pos]) < w) {
                line += words[pos++] + " ";
            }

            while (this.width(line) > w) {
                int l = 0;
                while (this.width(line.substring(0, l + 1)) <= w) {
                    ++l;
                }
                if (line.substring(0, l).trim().length() > 0) {
                    y += 8;
                }
                line = line.substring(l);
            }

            if (line.trim().length() > 0) {
                y += 8;
            }
        }

        if (y < 8) {
            y += 8;
        }
        return y;
    }
}
