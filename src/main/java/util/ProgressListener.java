// 
// Decompiled by Procyon v0.6.0
// 

package util;

public interface ProgressListener
{
    void progressStartNoAbort(final String string);
    
    void progressStage(final String status);
    
    void progressStagePercentage(final int i);
}
