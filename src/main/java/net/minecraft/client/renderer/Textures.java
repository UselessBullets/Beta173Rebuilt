// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer;

import javax.imageio.ImageIO;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;
import net.minecraft.client.renderer.ptexture.DynamicTexture;

import java.io.InputStream;
import net.minecraft.client.skins.TexturePack;
import java.io.IOException;
import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;
import net.minecraft.client.MemoryTracker;
import java.awt.image.BufferedImage;
import net.minecraft.client.skins.TexturePackRepository;
import net.minecraft.client.Options;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;

public class Textures
{
    public static boolean MIPMAP = false;
    private HashMap<String, Integer> idMap = new HashMap<>();
    private HashMap<String, int[]> pixelsMap = new HashMap<>();
    private HashMap<Integer, BufferedImage> loadedImages = new HashMap<>();
    private IntBuffer ib = MemoryTracker.createIntBuffer(1);
    private ByteBuffer pixels = MemoryTracker.createByteBuffer(1024 * 1024);
    private List<DynamicTexture> dynamicTextures = new ArrayList<>();
    private Map<String, HttpTexture> httpTextures = new HashMap<>();
    private Options options;
    private boolean clamp = false;
    private boolean blur = false;
    private TexturePackRepository skins;
    private BufferedImage missingNo;
    
    public Textures(final TexturePackRepository skins, final Options options) {
        this.missingNo = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        this.skins = skins;
        this.options = options;

        final Graphics g = this.missingNo.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 64, 64);
        g.setColor(Color.BLACK);
        g.drawString("missingtex", 1, 10);
        g.dispose();
    }
    
    public int[] loadTexturePixels(final String resourceName) {
        final TexturePack skin = this.skins.selected;

        {
            final int[] id = this.pixelsMap.get(resourceName);
            if (id != null) return id;
        }

        try {
            int[] res;
            if (resourceName.startsWith("##")) {
                res = this.loadTexturePixels(this.makeStrip(this.readImage(skin.getResource(resourceName.substring(2)))));
            }
            else if (resourceName.startsWith("%clamp%")) {
                this.clamp = true;
                res = this.loadTexturePixels(this.readImage(skin.getResource(resourceName.substring(7))));
                this.clamp = false;
            }
            else if (resourceName.startsWith("%blur%")) {
                this.blur = true;
                res = this.loadTexturePixels(this.readImage(skin.getResource(resourceName.substring(6))));
                this.blur = false;
            }
            else {
                final InputStream in = skin.getResource(resourceName);
                if (in == null) {
                    res = this.loadTexturePixels(this.missingNo);
                }
                else {
                    res = this.loadTexturePixels(this.readImage(in));
                }
            }
            this.pixelsMap.put(resourceName, res);
            return res;
        }
        catch (final IOException e) {
            e.printStackTrace();
            final int[] res = this.loadTexturePixels(this.missingNo);
            this.pixelsMap.put(resourceName, res);
            return res;
        }
    }
    
    private int[] loadTexturePixels(final BufferedImage img) {
        final int w = img.getWidth();
        final int h = img.getHeight();
        final int[] pixels = new int[w * h];
        img.getRGB(0, 0, w, h, pixels, 0, w);
        return pixels;
    }
    
    private int[] loadTexturePixels(final BufferedImage img, final int[] pixels) {
        int w = img.getWidth();
        int h = img.getHeight();
        img.getRGB(0, 0, w, h, pixels, 0, w);
        return pixels;
    }
    
    public int loadTexture(final String resourceName) {
        final TexturePack skin = this.skins.selected;
        {
            final Integer id = this.idMap.get(resourceName);
            if (id != null) return id;
        }
        try {
            this.ib.clear();
            MemoryTracker.genTextures(this.ib);
            final int id = this.ib.get(0);

            if (resourceName.startsWith("##")) {
                this.loadTexture(this.makeStrip(this.readImage(skin.getResource(resourceName.substring(2)))), id);
            }
            else if (resourceName.startsWith("%clamp%")) {
                this.clamp = true;
                this.loadTexture(this.readImage(skin.getResource(resourceName.substring(7))), id);
                this.clamp = false;
            }
            else if (resourceName.startsWith("%blur%")) {
                this.blur = true;
                this.loadTexture(this.readImage(skin.getResource(resourceName.substring(6))), id);
                this.blur = false;
            }
            else {
                final InputStream in = skin.getResource(resourceName);
                if (in == null) {
                    this.loadTexture(this.missingNo, id);
                }
                else {
                    this.loadTexture(this.readImage(in), id);
                }
            }

            this.idMap.put(resourceName, id);
            return id;
        }
        catch (final IOException e) {
            e.printStackTrace();
            MemoryTracker.genTextures(this.ib);
            final int id = this.ib.get(0);
            this.loadTexture(this.missingNo, id);
            this.idMap.put(resourceName, id);
            return id;
        }
    }
    
    private BufferedImage makeStrip(final BufferedImage source) {
        final int cols = source.getWidth() / 16;
        final BufferedImage out = new BufferedImage(16, source.getHeight() * cols, BufferedImage.TYPE_INT_ARGB);
        final Graphics g = out.getGraphics();

        for (int i = 0; i < cols; ++i) {
            g.drawImage(source, -i * 16, i * source.getHeight(), null);
        }

        g.dispose();
        return out;
    }
    
    public int getTexture(final BufferedImage img) {
        this.ib.clear();
        MemoryTracker.genTextures(this.ib);
        final int id = this.ib.get(0);
        this.loadTexture(img, id);
        this.loadedImages.put(id, img);
        return id;
    }
    
    public void loadTexture(final BufferedImage img, final int id) {
        glBindTexture(GL_TEXTURE_2D, id);
        if (Textures.MIPMAP) {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        }
        else {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        }
        if (this.blur) {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        }
        if (this.clamp) {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
        }
        else {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        }

        final int w = img.getWidth();
        final int h = img.getHeight();

        final int[] rawPixels = new int[w * h];
        final byte[] newPixels = new byte[w * h * 4];
        img.getRGB(0, 0, w, h, rawPixels, 0, w);

        for (int i = 0; i < rawPixels.length; ++i) {
            int a = rawPixels[i] >> 24 & 0xFF;
            int r = rawPixels[i] >> 16 & 0xFF;
            int g = rawPixels[i] >> 8 & 0xFF;
            int b = rawPixels[i] & 0xFF;

            if (this.options != null && this.options.anaglyph3d) {
                final int rr = (r * 30 + g * 59 + b * 11) / 100;
                final int gg = (r * 30 + g * 70) / 100;
                final int bb = (r * 30 + b * 70) / 100;
                r = rr;
                g = gg;
                b = bb;
            }

            newPixels[i * 4 + 0] = (byte)r;
            newPixels[i * 4 + 1] = (byte)g;
            newPixels[i * 4 + 2] = (byte)b;
            newPixels[i * 4 + 3] = (byte)a;
        }
        this.pixels.clear();
        this.pixels.put(newPixels);
        this.pixels.position(0).limit(newPixels.length);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, this.pixels);

        if (Textures.MIPMAP) {
            for (int level = 1; level <= 4; ++level) {
                final int ow = w >> level - 1;

                final int ww = w >> level;
                final int hh = h >> level;

                for (int x = 0; x < ww; ++x) {
                    for (int y = 0; y < hh; ++y) {
                        int c0 = this.pixels.getInt((x * 2 + 0 + (y * 2 + 0) * ow) * 4);
                        int c1 = this.pixels.getInt((x * 2 + 1 + (y * 2 + 0) * ow) * 4);
                        int c2 = this.pixels.getInt((x * 2 + 1 + (y * 2 + 1) * ow) * 4);
                        int c3 = this.pixels.getInt((x * 2 + 0 + (y * 2 + 1) * ow) * 4);

                        int col = this.crispBlend(this.crispBlend(c0, c1), this.crispBlend(c2, c3));
                        this.pixels.putInt((x + y * ww) * 4, col);
                    }
                }
                glTexImage2D(GL_TEXTURE_2D, level, GL_RGBA, ww, hh, 0, GL_RGBA, GL_UNSIGNED_BYTE, this.pixels);
            }
        }
    }
    
    public void replaceTextureDirect(final int[] rawPixels, final int w, final int h, final int id) {
        glBindTexture(GL_TEXTURE_2D, id);
        if (Textures.MIPMAP) {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        }
        else {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        }
        if (this.blur) {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        }
        if (this.clamp) {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
        }
        else {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        }
        final byte[] newPixels = new byte[w * h * 4];
        for (int i = 0; i < rawPixels.length; ++i) {
            int a = rawPixels[i] >> 24 & 0xFF;
            int r = rawPixels[i] >> 16 & 0xFF;
            int g = rawPixels[i] >> 8 & 0xFF;
            int b = rawPixels[i] & 0xFF;

            if (this.options != null && this.options.anaglyph3d) {
                final int rr = (r * 30 + g * 59 + b * 11) / 100;
                final int gg = (r * 30 + g * 70) / 100;
                final int bb = (r * 30 + b * 70) / 100;
                r = rr;
                g = gg;
                b = bb;
            }

            newPixels[i * 4 + 0] = (byte)r;
            newPixels[i * 4 + 1] = (byte)g;
            newPixels[i * 4 + 2] = (byte)b;
            newPixels[i * 4 + 3] = (byte)a;
        }
        this.pixels.clear();
        this.pixels.put(newPixels);
        this.pixels.position(0).limit(newPixels.length);

        glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, w, h, GL_RGBA, GL_UNSIGNED_BYTE, this.pixels);
    }
    
    public void releaseTexture(final int id) {
        this.loadedImages.remove(id);
        this.ib.clear();
        this.ib.put(id);
        this.ib.flip();
        glDeleteTextures(this.ib);
    }
    
    public int loadHttpTexture(final String url, final String backup) {
        final HttpTexture texture = this.httpTextures.get(url);
        if (texture != null) {
            if (texture.loadedImage != null && !texture.isLoaded) {
                if (texture.id < 0) {
                    texture.id = this.getTexture(texture.loadedImage);
                } else {
                    this.loadTexture(texture.loadedImage, texture.id);
                }
                texture.isLoaded = true;
            }
        }

        if (texture == null || texture.id < 0) {
            if (backup == null) return -1;
            return this.loadTexture(backup);
        }
        return texture.id;
    }
    
    public HttpTexture addHttpTexture(final String url, final HttpTextureProcessor processor) {
        final HttpTexture texture = this.httpTextures.get(url);
        if (texture == null) {
            this.httpTextures.put(url, new HttpTexture(url, processor));
        }
        else {
            ++texture.count;
        }
        return texture;
    }
    
    public void removeHttpTexture(final String url) {
        final HttpTexture texture = this.httpTextures.get(url);
        if (texture != null) {
            --texture.count;
            if (texture.count == 0) {
                if (texture.id >= 0) this.releaseTexture(texture.id);
                this.httpTextures.remove(url);
            }
        }
    }
    
    public void addDynamicTexture(final DynamicTexture dynamicTexture) {
        this.dynamicTextures.add(dynamicTexture);
        dynamicTexture.tick();
    }
    
    public void tick() {
        for (int i = 0; i < this.dynamicTextures.size(); ++i) {
            final DynamicTexture dynamicTexture = this.dynamicTextures.get(i);
            dynamicTexture.anaglyph3d = this.options.anaglyph3d;
            dynamicTexture.tick();
            this.pixels.clear();
            this.pixels.put(dynamicTexture.pixels);
            this.pixels.position(0).limit(dynamicTexture.pixels.length);
            dynamicTexture.bindTexture(this);

            for (int xx = 0; xx < dynamicTexture.replicate; ++xx) {
                for (int yy = 0; yy < dynamicTexture.replicate; ++yy) {
                    glTexSubImage2D(GL_TEXTURE_2D, 0, dynamicTexture.tex % 16 * 16 + xx * 16, dynamicTexture.tex / 16 * 16 + yy * 16, 16, 16, GL_RGBA, GL_UNSIGNED_BYTE, this.pixels);
                    if (Textures.MIPMAP) {
                        for (int level = 1; level <= 4; ++level) {
                            final int os = 16 >> level - 1;
                            final int s = 16 >> level;

                            for (int x = 0; x < s; ++x) {
                                for (int y = 0; y < s; ++y) {
                                    int c0 = this.pixels.getInt((x * 2 + 0 + (y * 2 + 0) * os) * 4);
                                    int c1 = this.pixels.getInt((x * 2 + 1 + (y * 2 + 0) * os) * 4);
                                    int c2 = this.pixels.getInt((x * 2 + 1 + (y * 2 + 1) * os) * 4);
                                    int c3 = this.pixels.getInt((x * 2 + 0 + (y * 2 + 1) * os) * 4);

                                    int col = this.smoothBlend(this.smoothBlend(c0, c1), this.smoothBlend(c2, c3));
                                    this.pixels.putInt((x + y * s) * 4, col);
                                }
                            }

                            glTexSubImage2D(GL_TEXTURE_2D, level, dynamicTexture.tex % 16 * s, dynamicTexture.tex / 16 * s, s, s, GL_RGBA, GL_UNSIGNED_BYTE, this.pixels);
                        }
                    }
                }
            }
        }

        for (int i = 0; i < this.dynamicTextures.size(); ++i) {
            final DynamicTexture dynamicTexture = this.dynamicTextures.get(i);
            if (dynamicTexture.copyTo > 0) {
                this.pixels.clear();
                this.pixels.put(dynamicTexture.pixels);
                this.pixels.position(0).limit(dynamicTexture.pixels.length);
                glBindTexture(GL_TEXTURE_2D, dynamicTexture.copyTo);
                glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 16, 16, GL_RGBA, GL_UNSIGNED_BYTE, this.pixels);
                if (Textures.MIPMAP) {
                    for (int level = 1; level <= 4; ++level) {
                        final int os = 16 >> level - 1;
                        final int s = 16 >> level;

                        for (int x = 0; x < s; ++x) {
                            for (int y = 0; y < s; ++y) {
                                int c0 = this.pixels.getInt((x * 2 + 0 + (y * 2 + 0) * os) * 4);
                                int c1 = this.pixels.getInt((x * 2 + 1 + (y * 2 + 0) * os) * 4);
                                int c2 = this.pixels.getInt((x * 2 + 1 + (y * 2 + 1) * os) * 4);
                                int c3 = this.pixels.getInt((x * 2 + 0 + (y * 2 + 1) * os) * 4);

                                int col = this.smoothBlend(this.smoothBlend(c0, c1), this.smoothBlend(c2, c3));
                                this.pixels.putInt((x + y * s) * 4, col);
                            }
                        }
                        glTexSubImage2D(GL_TEXTURE_2D, level, 0, 0, s, s, GL_RGBA, GL_UNSIGNED_BYTE, this.pixels);
                    }
                }
            }
        }
    }
    
    private int smoothBlend(final int c0, final int c1) {
        int a0 = ((c0 & 0xFF000000) >> 24 & 0xFF);
        int a1 = ((c1 & 0xFF000000) >> 24 & 0xFF);
        return (a0 + a1 >> 1 << 24) + ((c0 & 0xFEFEFE) + (c1 & 0xFEFEFE) >> 1);
    }
    
    private int crispBlend(final int c0, final int c1) {
        int a0 = (c0 & 0xFF000000) >> 24 & 0xFF;
        int a1 = (c1 & 0xFF000000) >> 24 & 0xFF;
        int a = 255;
        if (a0 + a1 == 0) {
            a0 = 1;
            a1 = 1;
            a = 0;
        }

        int r0 = (c0 >> 16 & 0xFF) * a0;
        int g0 = (c0 >> 8 & 0xFF) * a0;
        int b0 = (c0 & 0xFF) * a0;

        int r1 = (c1 >> 16 & 0xFF) * a1;
        int g1 = (c1 >> 8 & 0xFF) * a1;
        int b1 = (c1 & 0xFF) * a1;

        int r = (r0 + r1) / (a0 + a1);
        int g = (g0 + g1) / (a0 + a1);
        int b = (b0 + b1) / (a0 + a1);

        return a << 24 | r << 16 | g << 8 | b;
    }
    
    public void reloadAll() {
        final TexturePack skin = this.skins.selected;

        for (final int id : this.loadedImages.keySet()) {
            this.loadTexture(this.loadedImages.get(id), id);
        }

        for (HttpTexture httpTexture : this.httpTextures.values()) {
            httpTexture.isLoaded = false;
        }

        for (final String name : this.idMap.keySet()) {
            try {
                BufferedImage image;
                if (name.startsWith("##")) {
                    image = this.makeStrip(this.readImage(skin.getResource(name.substring(2))));
                } else if (name.startsWith("%clamp%")) {
                    this.clamp = true;
                    image = this.readImage(skin.getResource(name.substring(7)));
                } else if (name.startsWith("%blur%")) {
                    this.blur = true;
                    image = this.readImage(skin.getResource(name.substring(6)));
                } else {
                    image = this.readImage(skin.getResource(name));
                }
                this.loadTexture(image, this.idMap.get(name));
                this.blur = false;
                this.clamp = false;
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
        }
        for (final String name : this.pixelsMap.keySet()) {
            try {
                BufferedImage image;
                if (name.startsWith("##")) {
                    image = this.makeStrip(this.readImage(skin.getResource(name.substring(2))));
                } else if (name.startsWith("%clamp%")) {
                    this.clamp = true;
                    image = this.readImage(skin.getResource(name.substring(7)));
                } else if (name.startsWith("%blur%")) {
                    this.blur = true;
                    image = this.readImage(skin.getResource(name.substring(6)));
                } else {
                    image = this.readImage(skin.getResource(name));
                }
                this.loadTexturePixels(image, this.pixelsMap.get(name));
                this.blur = false;
                this.clamp = false;
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private BufferedImage readImage(final InputStream in) throws IOException {
        final BufferedImage img = ImageIO.read(in);
        in.close();
        return img;
    }
    
    public void bind(final int id) {
        if (id >= 0) glBindTexture(GL_TEXTURE_2D, id);
    }
}
