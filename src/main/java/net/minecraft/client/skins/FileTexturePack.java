// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.skins;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import net.minecraft.client.Minecraft;
import java.io.File;
import java.awt.image.BufferedImage;
import java.util.zip.ZipFile;

import static org.lwjgl.opengl.GL11.*;

public class FileTexturePack extends TexturePack
{
    private ZipFile zf;
    private int texture = -1;
    private BufferedImage icon;
    private File file;
    
    public FileTexturePack(final File file) {
        this.name = file.getName();
        this.file = file;
    }
    
    private String trim(String line) {
        if (line != null && line.length() > 34) {
            line = line.substring(0, 34);
        }

        return line;
    }
    
    @Override
    public void load(final Minecraft minecraft) throws IOException {
        ZipFile zf = null;
        InputStream in = null;

        try {
            zf = new ZipFile(this.file);

            try {
                in = zf.getInputStream(zf.getEntry("pack.txt"));
                final BufferedReader br = new BufferedReader(new InputStreamReader(in));
                this.desc1 = this.trim(br.readLine());
                this.desc2 = this.trim(br.readLine());
                br.close();
                in.close();
            }
            catch (final Exception ignored) {}

            try {
                in = zf.getInputStream(zf.getEntry("pack.png"));
                this.icon = ImageIO.read(in);
                in.close();
            }
            catch (final Exception ignored) {}

            zf.close();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                in.close();
            }
            catch (final Exception e) {}

            try {
                zf.close();
            }
            catch (final Exception e) {}
        }
    }
    
    @Override
    public void unload(final Minecraft minecraft) {
        if (this.icon != null) minecraft.textures.releaseTexture(this.texture);
        this.deselect();
    }
    
    @Override
    public void bindTexture(final Minecraft minecraft) {
        if (this.icon != null && this.texture < 0) {
            this.texture = minecraft.textures.getTexture(this.icon);
        }

        if (this.icon != null) {
            minecraft.textures.bind(this.texture);
        } else {
            glBindTexture(GL_TEXTURE_2D, minecraft.textures.loadTexture("/gui/unknown_pack.png"));
        }
    }
    
    @Override
    public void select() {
        try {
            this.zf = new ZipFile(this.file);
        }
        catch (final Exception ignored) {}
    }
    
    @Override
    public void deselect() {
        try {
            this.zf.close();
        }
        catch (final Exception ignored) {}
        this.zf = null;
    }
    
    @Override
    public InputStream getResource(final String name) {
        try {
            final ZipEntry entry = this.zf.getEntry(name.substring(1));
            if (entry != null) {
                return this.zf.getInputStream(entry);
            }
        }
        catch (final Exception ignored) {}
        return TexturePack.class.getResourceAsStream(name);
    }
}
