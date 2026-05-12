// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server;

import util.ProgressListener;

public class MinecraftServer_ConversionProgressListener implements ProgressListener
{
    private long lastCheckTime;
    final /* synthetic */ MinecraftServer server;
    
    public MinecraftServer_ConversionProgressListener(final MinecraftServer server) {
        this.server = server;
        this.lastCheckTime = System.currentTimeMillis();
    }
    
    public void progressStartNoAbort(final String string) {
    }
    
    public void progressStagePercentage(final int i) {
        if (System.currentTimeMillis() - this.lastCheckTime >= 1000L) {
            this.lastCheckTime = System.currentTimeMillis();
            MinecraftServer.logger.info("Converting... " + i + "%");
        }
    }
    
    public void progressStage(final String status) {
    }
}
