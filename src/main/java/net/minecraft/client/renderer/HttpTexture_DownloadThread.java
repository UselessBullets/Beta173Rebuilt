// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer;

import java.net.HttpURLConnection;
import javax.imageio.ImageIO;
import java.net.URL;
import java.net.URLConnection;

class HttpTexture_DownloadThread extends Thread
{
    final /* synthetic */ String url;
    final /* synthetic */ HttpTextureProcessor processor;
    final /* synthetic */ HttpTexture texture;
    
    HttpTexture_DownloadThread(final HttpTexture texture, final String url, final HttpTextureProcessor processor) {
        this.texture = texture;
        this.url = url;
        this.processor = processor;
    }
    
    @Override
    public void run() {
        URLConnection urlConnection = null;
        try {
            urlConnection = new URL(this.url).openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(false);
            urlConnection.connect();
            if (((HttpURLConnection)urlConnection).getResponseCode() / 100 == 4) {
                return;
            }
            if (this.processor == null) {
                this.texture.loadedImage = ImageIO.read(urlConnection.getInputStream());
            }
            else {
                this.texture.loadedImage = this.processor.process(ImageIO.read(urlConnection.getInputStream()));
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        finally {
            ((HttpURLConnection)urlConnection).disconnect();
        }
    }
}
