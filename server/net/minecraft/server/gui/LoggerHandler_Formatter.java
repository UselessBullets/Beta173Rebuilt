// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.gui;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Formatter;

class LoggerHandler_Formatter extends Formatter
{
    final /* synthetic */ LoggerHandler lh;
    
    LoggerHandler_Formatter(final LoggerHandler lh) {
        this.lh = lh;
    }
    
    @Override
    public String format(final LogRecord logRecord) {
        final StringBuilder sb = new StringBuilder();
        final Level level = logRecord.getLevel();
        if (level == Level.FINEST) {
            sb.append("[FINEST] ");
        }
        else if (level == Level.FINER) {
            sb.append("[FINER] ");
        }
        else if (level == Level.FINE) {
            sb.append("[FINE] ");
        }
        else if (level == Level.INFO) {
            sb.append("[INFO] ");
        }
        else if (level == Level.WARNING) {
            sb.append("[WARNING] ");
        }
        else if (level == Level.SEVERE) {
            sb.append("[SEVERE] ");
        }
        else if (level == Level.SEVERE) {
            sb.append("[" + level.getLocalizedName() + "] ");
        }
        sb.append(logRecord.getMessage());
        sb.append('\n');
        final Throwable thrown = logRecord.getThrown();
        if (thrown != null) {
            final StringWriter out = new StringWriter();
            thrown.printStackTrace(new PrintWriter(out));
            sb.append(out.toString());
        }
        return sb.toString();
    }
}
