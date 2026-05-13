// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

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
        new Thread(() -> {
            URLConnection urlConnection = null;
            try {
                urlConnection = new URL(url).openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(false);
                urlConnection.connect();
                if (((HttpURLConnection)urlConnection).getResponseCode() / 100 == 4) {
                    return;
                }
                if (processor == null) {
                    loadedImage = ImageIO.read(urlConnection.getInputStream());
                }
                else {
                    loadedImage = processor.process(ImageIO.read(urlConnection.getInputStream()));
                }
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
            finally {
                ((HttpURLConnection)urlConnection).disconnect();
            }
        }).start();
    }
}
