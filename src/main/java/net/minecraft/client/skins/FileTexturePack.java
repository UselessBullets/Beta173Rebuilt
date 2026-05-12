// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.skins;

import java.util.zip.ZipEntry;
import org.lwjgl.opengl.GL11;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import net.minecraft.client.Minecraft;
import java.io.File;
import java.awt.image.BufferedImage;
import java.util.zip.ZipFile;

public class FileTexturePack extends TexturePack
{
    private ZipFile zf;
    private int texture;
    private BufferedImage icon;
    private File file;
    
    public FileTexturePack(final File file) {
        this.texture = -1;
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
    public void load(final Minecraft minecraft) {
        ZipFile zipFile = null;
        InputStream inputStream = null;
        try {
            zipFile = new ZipFile(this.file);
            try {
                inputStream = zipFile.getInputStream(zipFile.getEntry("pack.txt"));
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                this.desc1 = this.trim(bufferedReader.readLine());
                this.desc2 = this.trim(bufferedReader.readLine());
                bufferedReader.close();
                inputStream.close();
            }
            catch (final Exception ex) {}
            try {
                inputStream = zipFile.getInputStream(zipFile.getEntry("pack.png"));
                this.icon = ImageIO.read(inputStream);
                inputStream.close();
            }
            catch (final Exception ex2) {}
            zipFile.close();
        }
        catch (final Exception ex3) {
            ex3.printStackTrace();
        }
        finally {
            try {
                inputStream.close();
            }
            catch (final Exception ex4) {}
            try {
                zipFile.close();
            }
            catch (final Exception ex5) {}
        }
    }
    
    @Override
    public void unload(final Minecraft minecraft) {
        if (this.icon != null) {
            minecraft.textures.releaseTexture(this.texture);
        }
        this.deselect();
    }
    
    @Override
    public void bindTexture(final Minecraft minecraft) {
        if (this.icon != null && this.texture < 0) {
            this.texture = minecraft.textures.getTexture(this.icon);
        }
        if (this.icon != null) {
            minecraft.textures.bind(this.texture);
        }
        else {
            GL11.glBindTexture(3553, minecraft.textures.loadTexture("/gui/unknown_pack.png"));
        }
    }
    
    @Override
    public void select() {
        try {
            this.zf = new ZipFile(this.file);
        }
        catch (final Exception ex) {}
    }
    
    @Override
    public void deselect() {
        try {
            this.zf.close();
        }
        catch (final Exception ex) {}
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
        catch (final Exception ex) {}
        return TexturePack.class.getResourceAsStream(name);
    }
}
