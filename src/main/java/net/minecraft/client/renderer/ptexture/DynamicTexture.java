// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.ptexture;

import net.minecraft.client.renderer.Textures;

import static org.lwjgl.opengl.GL11.*;

public class DynamicTexture
{
    public static final int TEXTURE_TERRAIN = 0;
    public static final int TEXTURE_ITEMS = 1;
    public byte[] pixels = new byte[16 * 16 * 4];
    public int tex;
    public boolean anaglyph3d = false;
    public int copyTo = 0;
    public int replicate = 1;
    public int textureId = 0;
    
    public DynamicTexture(final int tex) {
        this.tex = tex;
    }
    
    public void tick() {
    }
    
    public void bindTexture(final Textures textures) {
        if (this.textureId == TEXTURE_TERRAIN) {
            glBindTexture(GL_TEXTURE_2D, textures.loadTexture("/terrain.png"));
        }
        else if (this.textureId == TEXTURE_ITEMS) {
            glBindTexture(GL_TEXTURE_2D, textures.loadTexture("/gui/items.png"));
        }
    }
}
