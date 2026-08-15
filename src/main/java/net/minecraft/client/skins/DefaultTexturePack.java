// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.skins;

import net.minecraft.client.Minecraft;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import static org.lwjgl.opengl.GL11.*;

public class DefaultTexturePack extends TexturePack
{
    private int texture;
    private BufferedImage icon;
    
    public DefaultTexturePack() {
        this.texture = -1;
        this.name = "Default";
        this.desc1 = "The default look of Minecraft";
        try {
            this.icon = ImageIO.read(DefaultTexturePack.class.getResource("/pack.png"));
        }
        catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void unload(final Minecraft minecraft) {
        if (this.icon != null) {
            minecraft.textures.releaseTexture(this.texture);
        }
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
            glBindTexture(GL_TEXTURE_2D, minecraft.textures.loadTexture("/gui/unknown_pack.png"));
        }
    }
}
