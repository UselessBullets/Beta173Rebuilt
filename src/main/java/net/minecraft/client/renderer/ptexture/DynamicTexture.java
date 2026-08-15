// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.ptexture;

import net.minecraft.client.renderer.Textures;

import static org.lwjgl.opengl.GL11.*;

public class DynamicTexture
{
    public byte[] pixels;
    public int tex;
    public boolean anaglyph3d;
    public int copyTo;
    public int replicate;
    public int textureId;
    
    public DynamicTexture(final int tex) {
        this.pixels = new byte[1024];
        this.anaglyph3d = false;
        this.copyTo = 0;
        this.replicate = 1;
        this.textureId = 0;
        this.tex = tex;
    }
    
    public void tick() {
    }
    
    public void bindTexture(final Textures textures) {
        if (this.textureId == 0) {
            glBindTexture(GL_TEXTURE_2D, textures.loadTexture("/terrain.png"));
        }
        else if (this.textureId == 1) {
            glBindTexture(GL_TEXTURE_2D, textures.loadTexture("/gui/items.png"));
        }
    }
}
