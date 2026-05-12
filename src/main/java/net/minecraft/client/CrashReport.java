// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

public class CrashReport
{
    public final String title;
    public final Throwable e;
    
    public CrashReport(final String title, final Throwable e) {
        this.title = title;
        this.e = e;
    }
}
