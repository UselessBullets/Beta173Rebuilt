// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.ptexture;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Textures;

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
            GL11.glBindTexture(3553, textures.loadTexture("/terrain.png"));
        }
        else if (this.textureId == 1) {
            GL11.glBindTexture(3553, textures.loadTexture("/gui/items.png"));
        }
    }
}
