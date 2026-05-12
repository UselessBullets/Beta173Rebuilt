// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.stats;

import java.util.Map;

class StatsSyncher_SaveUnsentThread extends Thread
{
    final /* synthetic */ Map ss;
    final /* synthetic */ StatsSyncher unsent;
    
    StatsSyncher_SaveUnsentThread(final StatsSyncher ss, final Map unsent) {
        this.unsent = ss;
        this.ss = unsent;
    }
    
    @Override
    public void run() {
        try {
            this.unsent.doSave(this.ss, this.unsent.unsentFile, this.unsent.unsentFileTmp, this.unsent.unsentFileOld);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        finally {
            this.unsent.busy = false;
        }
    }
}
