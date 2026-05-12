// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MinecraftServer_HandleCommandsThread extends Thread
{
    final /* synthetic */ MinecraftServer server;
    
    public MinecraftServer_HandleCommandsThread(final MinecraftServer server) {
        this.server = server;
    }
    
    @Override
    public void run() {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String line;
            while (!this.server.stopped && MinecraftServer.getRunning(this.server) && (line = bufferedReader.readLine()) != null) {
                this.server.handleConsoleInput(line, this.server);
            }
        }
        catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
}
