// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server;

public class ConsoleInput
{
    public final String msg;
    public final ConsoleInputSource source;
    
    public ConsoleInput(final String msg, final ConsoleInputSource source) {
        this.msg = msg;
        this.source = source;
    }
}
