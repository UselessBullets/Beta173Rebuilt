// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer;

import javax.imageio.ImageIO;
import java.util.Iterator;
import net.minecraft.client.renderer.ptexture.DynamicTexture;
import org.lwjgl.opengl.GL11;
import java.awt.image.ImageObserver;
import java.awt.Image;
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
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

public class Textures
{
    public static boolean MIPMAP;
    private HashMap<String, Integer> idMap;
    private HashMap<String, int[]> pixelsMap;
    private HashMap<Integer, BufferedImage> loadedImages;
    private IntBuffer ib;
    private ByteBuffer pixels;
    private List<DynamicTexture> dynamicTextures;
    private Map<String, HttpTexture> httpTextures;
    private Options options;
    private boolean clamp;
    private boolean blur;
    private TexturePackRepository skins;
    private BufferedImage missingNo;
    
    public Textures(final TexturePackRepository skins, final Options options) {
        this.idMap = new HashMap<>();
        this.pixelsMap = new HashMap<>();
        this.loadedImages = new HashMap<>();
        this.ib = MemoryTracker.createIntBuffer(1);
        this.pixels = MemoryTracker.createByteBuffer(1048576);
        this.dynamicTextures = new ArrayList<>();
        this.httpTextures = new HashMap<>();
        this.clamp = false;
        this.blur = false;
        this.missingNo = new BufferedImage(64, 64, 2);
        this.skins = skins;
        this.options = options;
        final Graphics graphics = this.missingNo.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 64, 64);
        graphics.setColor(Color.BLACK);
        graphics.drawString("missingtex", 1, 10);
        graphics.dispose();
    }
    
    public int[] loadTexturePixels(final String resourceName) {
        final TexturePack selected = this.skins.selected;
        final int[] array = this.pixelsMap.get(resourceName);
        if (array != null) {
            return array;
        }
        try {
            int[] value;
            if (resourceName.startsWith("##")) {
                value = this.loadTexturePixels(this.makeStrip(this.readImage(selected.getResource(resourceName.substring(2)))));
            }
            else if (resourceName.startsWith("%clamp%")) {
                this.clamp = true;
                value = this.loadTexturePixels(this.readImage(selected.getResource(resourceName.substring(7))));
                this.clamp = false;
            }
            else if (resourceName.startsWith("%blur%")) {
                this.blur = true;
                value = this.loadTexturePixels(this.readImage(selected.getResource(resourceName.substring(6))));
                this.blur = false;
            }
            else {
                final InputStream resource = selected.getResource(resourceName);
                if (resource == null) {
                    value = this.loadTexturePixels(this.missingNo);
                }
                else {
                    value = this.loadTexturePixels(this.readImage(resource));
                }
            }
            this.pixelsMap.put(resourceName, value);
            return value;
        }
        catch (final IOException ex) {
            ex.printStackTrace();
            final int[] loadTexturePixels = this.loadTexturePixels(this.missingNo);
            this.pixelsMap.put(resourceName, loadTexturePixels);
            return loadTexturePixels;
        }
    }
    
    private int[] loadTexturePixels(final BufferedImage img) {
        final int width = img.getWidth();
        final int height = img.getHeight();
        final int[] rgbArray = new int[width * height];
        img.getRGB(0, 0, width, height, rgbArray, 0, width);
        return rgbArray;
    }
    
    private int[] loadTexturePixels(final BufferedImage img, final int[] pixels) {
        final int width = img.getWidth();
        img.getRGB(0, 0, width, img.getHeight(), pixels, 0, width);
        return pixels;
    }
    
    public int loadTexture(final String resourceName) {
        final TexturePack selected = this.skins.selected;
        final Integer n = this.idMap.get(resourceName);
        if (n != null) {
            return n;
        }
        try {
            this.ib.clear();
            MemoryTracker.genTextures(this.ib);
            final int value = this.ib.get(0);
            if (resourceName.startsWith("##")) {
                this.loadTexture(this.makeStrip(this.readImage(selected.getResource(resourceName.substring(2)))), value);
            }
            else if (resourceName.startsWith("%clamp%")) {
                this.clamp = true;
                this.loadTexture(this.readImage(selected.getResource(resourceName.substring(7))), value);
                this.clamp = false;
            }
            else if (resourceName.startsWith("%blur%")) {
                this.blur = true;
                this.loadTexture(this.readImage(selected.getResource(resourceName.substring(6))), value);
                this.blur = false;
            }
            else {
                final InputStream resource = selected.getResource(resourceName);
                if (resource == null) {
                    this.loadTexture(this.missingNo, value);
                }
                else {
                    this.loadTexture(this.readImage(resource), value);
                }
            }
            this.idMap.put(resourceName, value);
            return value;
        }
        catch (final IOException ex) {
            ex.printStackTrace();
            MemoryTracker.genTextures(this.ib);
            final int value2 = this.ib.get(0);
            this.loadTexture(this.missingNo, value2);
            this.idMap.put(resourceName, value2);
            return value2;
        }
    }
    
    private BufferedImage makeStrip(final BufferedImage source) {
        final int n = source.getWidth() / 16;
        final BufferedImage bufferedImage = new BufferedImage(16, source.getHeight() * n, 2);
        final Graphics graphics = bufferedImage.getGraphics();
        for (int i = 0; i < n; ++i) {
            graphics.drawImage(source, -i * 16, i * source.getHeight(), null);
        }
        graphics.dispose();
        return bufferedImage;
    }
    
    public int getTexture(final BufferedImage img) {
        this.ib.clear();
        MemoryTracker.genTextures(this.ib);
        final int value = this.ib.get(0);
        this.loadTexture(img, value);
        this.loadedImages.put(value, img);
        return value;
    }
    
    public void loadTexture(final BufferedImage img, final int id) {
        GL11.glBindTexture(3553, id);
        if (Textures.MIPMAP) {
            GL11.glTexParameteri(3553, 10241, 9986);
            GL11.glTexParameteri(3553, 10240, 9728);
        }
        else {
            GL11.glTexParameteri(3553, 10241, 9728);
            GL11.glTexParameteri(3553, 10240, 9728);
        }
        if (this.blur) {
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
        }
        if (this.clamp) {
            GL11.glTexParameteri(3553, 10242, 10496);
            GL11.glTexParameteri(3553, 10243, 10496);
        }
        else {
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
        }
        final int width = img.getWidth();
        final int height = img.getHeight();
        final int[] rgbArray = new int[width * height];
        final byte[] src = new byte[width * height * 4];
        img.getRGB(0, 0, width, height, rgbArray, 0, width);
        for (int i = 0; i < rgbArray.length; ++i) {
            final int n = rgbArray[i] >> 24 & 0xFF;
            int n2 = rgbArray[i] >> 16 & 0xFF;
            int n3 = rgbArray[i] >> 8 & 0xFF;
            int n4 = rgbArray[i] & 0xFF;
            if (this.options != null && this.options.anaglyph3d) {
                final int n5 = (n2 * 30 + n3 * 59 + n4 * 11) / 100;
                final int n6 = (n2 * 30 + n3 * 70) / 100;
                final int n7 = (n2 * 30 + n4 * 70) / 100;
                n2 = n5;
                n3 = n6;
                n4 = n7;
            }
            src[i * 4 + 0] = (byte)n2;
            src[i * 4 + 1] = (byte)n3;
            src[i * 4 + 2] = (byte)n4;
            src[i * 4 + 3] = (byte)n;
        }
        this.pixels.clear();
        this.pixels.put(src);
        this.pixels.position(0).limit(src.length);
        GL11.glTexImage2D(3553, 0, 6408, width, height, 0, 6408, 5121, this.pixels);
        if (Textures.MIPMAP) {
            for (int j = 1; j <= 4; ++j) {
                final int n8 = width >> j - 1;
                final int n9 = width >> j;
                final int n10 = height >> j;
                for (int k = 0; k < n9; ++k) {
                    for (int l = 0; l < n10; ++l) {
                        this.pixels.putInt((k + l * n9) * 4, this.crispBlend(this.crispBlend(this.pixels.getInt((k * 2 + 0 + (l * 2 + 0) * n8) * 4), this.pixels.getInt((k * 2 + 1 + (l * 2 + 0) * n8) * 4)), this.crispBlend(this.pixels.getInt((k * 2 + 1 + (l * 2 + 1) * n8) * 4), this.pixels.getInt((k * 2 + 0 + (l * 2 + 1) * n8) * 4))));
                    }
                }
                GL11.glTexImage2D(3553, j, 6408, n9, n10, 0, 6408, 5121, this.pixels);
            }
        }
    }
    
    public void replaceTextureDirect(final int[] rawPixels, final int w, final int h, final int id) {
        GL11.glBindTexture(3553, id);
        if (Textures.MIPMAP) {
            GL11.glTexParameteri(3553, 10241, 9986);
            GL11.glTexParameteri(3553, 10240, 9728);
        }
        else {
            GL11.glTexParameteri(3553, 10241, 9728);
            GL11.glTexParameteri(3553, 10240, 9728);
        }
        if (this.blur) {
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
        }
        if (this.clamp) {
            GL11.glTexParameteri(3553, 10242, 10496);
            GL11.glTexParameteri(3553, 10243, 10496);
        }
        else {
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
        }
        final byte[] src = new byte[w * h * 4];
        for (int i = 0; i < rawPixels.length; ++i) {
            final int n = rawPixels[i] >> 24 & 0xFF;
            int n2 = rawPixels[i] >> 16 & 0xFF;
            int n3 = rawPixels[i] >> 8 & 0xFF;
            int n4 = rawPixels[i] & 0xFF;
            if (this.options != null && this.options.anaglyph3d) {
                final int n5 = (n2 * 30 + n3 * 59 + n4 * 11) / 100;
                final int n6 = (n2 * 30 + n3 * 70) / 100;
                final int n7 = (n2 * 30 + n4 * 70) / 100;
                n2 = n5;
                n3 = n6;
                n4 = n7;
            }
            src[i * 4 + 0] = (byte)n2;
            src[i * 4 + 1] = (byte)n3;
            src[i * 4 + 2] = (byte)n4;
            src[i * 4 + 3] = (byte)n;
        }
        this.pixels.clear();
        this.pixels.put(src);
        this.pixels.position(0).limit(src.length);
        GL11.glTexSubImage2D(3553, 0, 0, 0, w, h, 6408, 5121, this.pixels);
    }
    
    public void releaseTexture(final int id) {
        this.loadedImages.remove(id);
        this.ib.clear();
        this.ib.put(id);
        this.ib.flip();
        GL11.glDeleteTextures(this.ib);
    }
    
    public int loadHttpTexture(final String url, final String backup) {
        final HttpTexture httpTexture = this.httpTextures.get(url);
        if (httpTexture != null && httpTexture.loadedImage != null && !httpTexture.isLoaded) {
            if (httpTexture.id < 0) {
                httpTexture.id = this.getTexture(httpTexture.loadedImage);
            }
            else {
                this.loadTexture(httpTexture.loadedImage, httpTexture.id);
            }
            httpTexture.isLoaded = true;
        }
        if (httpTexture != null && httpTexture.id >= 0) {
            return httpTexture.id;
        }
        if (backup == null) {
            return -1;
        }
        return this.loadTexture(backup);
    }
    
    public HttpTexture addHttpTexture(final String url, final HttpTextureProcessor processor) {
        final HttpTexture httpTexture = this.httpTextures.get(url);
        if (httpTexture == null) {
            this.httpTextures.put(url, new HttpTexture(url, processor));
        }
        else {
            final HttpTexture httpTexture2 = httpTexture;
            ++httpTexture2.count;
        }
        return httpTexture;
    }
    
    public void removeHttpTexture(final String url) {
        final HttpTexture httpTexture = this.httpTextures.get(url);
        if (httpTexture != null) {
            final HttpTexture httpTexture2 = httpTexture;
            --httpTexture2.count;
            if (httpTexture.count == 0) {
                if (httpTexture.id >= 0) {
                    this.releaseTexture(httpTexture.id);
                }
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
            for (int j = 0; j < dynamicTexture.replicate; ++j) {
                for (int k = 0; k < dynamicTexture.replicate; ++k) {
                    GL11.glTexSubImage2D(3553, 0, dynamicTexture.tex % 16 * 16 + j * 16, dynamicTexture.tex / 16 * 16 + k * 16, 16, 16, 6408, 5121, this.pixels);
                    if (Textures.MIPMAP) {
                        for (int l = 1; l <= 4; ++l) {
                            final int n = 16 >> l - 1;
                            final int n2 = 16 >> l;
                            for (int n3 = 0; n3 < n2; ++n3) {
                                for (int n4 = 0; n4 < n2; ++n4) {
                                    this.pixels.putInt((n3 + n4 * n2) * 4, this.smoothBlend(this.smoothBlend(this.pixels.getInt((n3 * 2 + 0 + (n4 * 2 + 0) * n) * 4), this.pixels.getInt((n3 * 2 + 1 + (n4 * 2 + 0) * n) * 4)), this.smoothBlend(this.pixels.getInt((n3 * 2 + 1 + (n4 * 2 + 1) * n) * 4), this.pixels.getInt((n3 * 2 + 0 + (n4 * 2 + 1) * n) * 4))));
                                }
                            }
                            GL11.glTexSubImage2D(3553, l, dynamicTexture.tex % 16 * n2, dynamicTexture.tex / 16 * n2, n2, n2, 6408, 5121, this.pixels);
                        }
                    }
                }
            }
        }
        for (int n5 = 0; n5 < this.dynamicTextures.size(); ++n5) {
            final DynamicTexture dynamicTexture2 = this.dynamicTextures.get(n5);
            if (dynamicTexture2.copyTo > 0) {
                this.pixels.clear();
                this.pixels.put(dynamicTexture2.pixels);
                this.pixels.position(0).limit(dynamicTexture2.pixels.length);
                GL11.glBindTexture(3553, dynamicTexture2.copyTo);
                GL11.glTexSubImage2D(3553, 0, 0, 0, 16, 16, 6408, 5121, this.pixels);
                if (Textures.MIPMAP) {
                    for (int n6 = 1; n6 <= 4; ++n6) {
                        final int n7 = 16 >> n6 - 1;
                        final int n8 = 16 >> n6;
                        for (int n9 = 0; n9 < n8; ++n9) {
                            for (int n10 = 0; n10 < n8; ++n10) {
                                this.pixels.putInt((n9 + n10 * n8) * 4, this.smoothBlend(this.smoothBlend(this.pixels.getInt((n9 * 2 + 0 + (n10 * 2 + 0) * n7) * 4), this.pixels.getInt((n9 * 2 + 1 + (n10 * 2 + 0) * n7) * 4)), this.smoothBlend(this.pixels.getInt((n9 * 2 + 1 + (n10 * 2 + 1) * n7) * 4), this.pixels.getInt((n9 * 2 + 0 + (n10 * 2 + 1) * n7) * 4))));
                            }
                        }
                        GL11.glTexSubImage2D(3553, n6, 0, 0, n8, n8, 6408, 5121, this.pixels);
                    }
                }
            }
        }
    }
    
    private int smoothBlend(final int c0, final int c1) {
        return (((c0 & 0xFF000000) >> 24 & 0xFF) + ((c1 & 0xFF000000) >> 24 & 0xFF) >> 1 << 24) + ((c0 & 0xFEFEFE) + (c1 & 0xFEFEFE) >> 1);
    }
    
    private int crispBlend(final int c0, final int c1) {
        int n = (c0 & 0xFF000000) >> 24 & 0xFF;
        int n2 = (c1 & 0xFF000000) >> 24 & 0xFF;
        int n3 = 255;
        if (n + n2 == 0) {
            n = 1;
            n2 = 1;
            n3 = 0;
        }
        return n3 << 24 | ((c0 >> 16 & 0xFF) * n + (c1 >> 16 & 0xFF) * n2) / (n + n2) << 16 | ((c0 >> 8 & 0xFF) * n + (c1 >> 8 & 0xFF) * n2) / (n + n2) << 8 | ((c0 & 0xFF) * n + (c1 & 0xFF) * n2) / (n + n2);
    }
    
    public void reloadAll() {
        final TexturePack selected = this.skins.selected;
        for (final int intValue : this.loadedImages.keySet()) {
            this.loadTexture((BufferedImage)this.loadedImages.get(intValue), intValue);
        }
        final Iterator iterator2 = this.httpTextures.values().iterator();
        while (iterator2.hasNext()) {
            ((HttpTexture)iterator2.next()).isLoaded = false;
        }
        for (final String s : this.idMap.keySet()) {
            try {
                BufferedImage img;
                if (s.startsWith("##")) {
                    img = this.makeStrip(this.readImage(selected.getResource(s.substring(2))));
                }
                else if (s.startsWith("%clamp%")) {
                    this.clamp = true;
                    img = this.readImage(selected.getResource(s.substring(7)));
                }
                else if (s.startsWith("%blur%")) {
                    this.blur = true;
                    img = this.readImage(selected.getResource(s.substring(6)));
                }
                else {
                    img = this.readImage(selected.getResource(s));
                }
                this.loadTexture(img, this.idMap.get(s));
                this.blur = false;
                this.clamp = false;
            }
            catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
        for (final String s2 : this.pixelsMap.keySet()) {
            try {
                BufferedImage img2;
                if (s2.startsWith("##")) {
                    img2 = this.makeStrip(this.readImage(selected.getResource(s2.substring(2))));
                }
                else if (s2.startsWith("%clamp%")) {
                    this.clamp = true;
                    img2 = this.readImage(selected.getResource(s2.substring(7)));
                }
                else if (s2.startsWith("%blur%")) {
                    this.blur = true;
                    img2 = this.readImage(selected.getResource(s2.substring(6)));
                }
                else {
                    img2 = this.readImage(selected.getResource(s2));
                }
                this.loadTexturePixels(img2, this.pixelsMap.get(s2));
                this.blur = false;
                this.clamp = false;
            }
            catch (final IOException ex2) {
                ex2.printStackTrace();
            }
        }
    }
    
    private BufferedImage readImage(final InputStream in) throws IOException {
        final BufferedImage read = ImageIO.read(in);
        in.close();
        return read;
    }
    
    public void bind(final int id) {
        if (id < 0) {
            return;
        }
        GL11.glBindTexture(3553, id);
    }
    
    static {
        Textures.MIPMAP = false;
    }
}
