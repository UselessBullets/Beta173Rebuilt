// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer;

import java.awt.image.BufferedImage;

public class HttpTexture
{
    public BufferedImage loadedImage;
    public int count;
    public int id;
    public boolean isLoaded;
    
    public HttpTexture(final String url, final HttpTextureProcessor processor) {
        this.count = 1;
        this.id = -1;
        this.isLoaded = false;
        new HttpTexture_DownloadThread(this, url, processor).start();
    }
}
