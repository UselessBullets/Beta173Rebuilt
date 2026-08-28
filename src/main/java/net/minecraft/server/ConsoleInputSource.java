// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server;

public interface ConsoleInputSource
{
    void info(final String string);

    // Useless - In LCE
    void warn(final String string);
    
    String getConsoleName();
}
