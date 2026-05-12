// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.stats;

class StatsSyncher_GetStatsFromServerThread extends Thread
{
    final /* synthetic */ StatsSyncher ss;
    
    StatsSyncher_GetStatsFromServerThread(final StatsSyncher ss) {
        this.ss = ss;
    }
    
    @Override
    public void run() {
        try {
            if (this.ss.serverStats != null) {
                this.ss.doSave(this.ss.serverStats, this.ss.lastServerFile, this.ss.lastServerFileTmp, this.ss.lastServerFileOld);
            }
            else if (this.ss.lastServerFile.exists()) {
                this.ss.serverStats = this.ss.loadStatsFromDisk(this.ss.lastServerFile, this.ss.lastServerFileTmp, this.ss.lastServerFileOld);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        finally {
            this.ss.busy = false;
        }
    }
}
