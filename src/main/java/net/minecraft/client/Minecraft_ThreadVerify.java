// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import java.net.URL;
import java.net.HttpURLConnection;

public class Minecraft_ThreadVerify extends Thread
{
    final /* synthetic */ Minecraft mc;
    
    public Minecraft_ThreadVerify(final Minecraft mc) {
        this.mc = mc;
    }
    
    @Override
    public void run() {
        try {
            final HttpURLConnection httpURLConnection = (HttpURLConnection)new URL("https://login.minecraft.net/session?name=" + this.mc.user.name + "&session=" + this.mc.user.sessionId).openConnection();
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == 400) {
                Minecraft.warezTime = System.currentTimeMillis();
            }
            httpURLConnection.disconnect();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
}
