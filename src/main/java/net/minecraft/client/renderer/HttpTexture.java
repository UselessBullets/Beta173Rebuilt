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
    public int count = 1;
    public int id = -1;
    public boolean isLoaded = false;
    
    public HttpTexture(final String _url, final HttpTextureProcessor processor) {
        new Thread(() -> {
            HttpURLConnection huc = null;
            try {
                URL url = new URL(_url);
                huc = (HttpURLConnection) url.openConnection();
                huc.setDoInput(true);
                huc.setDoOutput(false);
                huc.connect();
                if (huc.getResponseCode() / 100 == 4) return;

                if (processor == null) {
                    this.loadedImage = ImageIO.read(huc.getInputStream());
                }
                else {
                    this.loadedImage = processor.process(ImageIO.read(huc.getInputStream()));
                }

                return;
            }
            catch (final Exception e) {
                e.printStackTrace();
                return;
            }
            finally {
                huc.disconnect();
            }
        }).start();
    }
}
