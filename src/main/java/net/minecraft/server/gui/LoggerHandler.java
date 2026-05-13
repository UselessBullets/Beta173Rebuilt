// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.gui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.swing.JTextArea;
import java.util.logging.Formatter;
import java.util.logging.Handler;

public class LoggerHandler extends Handler
{
    private int[] b; // TODO find proper name
    private int c; // TODO find proper name
    Formatter formatter;
    private JTextArea textArea;
    
    public LoggerHandler(final JTextArea textArea) {
        this.b = new int[1024];
        this.c = 0;
        this.setFormatter(this.formatter = new Formatter() {

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
                    sb.append(out);
                }
                return sb.toString();
            }
        });
        this.textArea = textArea;
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public void flush() {
    }
    
    @Override
    public void publish(final LogRecord logRecord) {
        final int length = this.textArea.getDocument().getLength();
        this.textArea.append(this.formatter.format(logRecord));
        this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
        final int n = this.textArea.getDocument().getLength() - length;
        if (this.b[this.c] != 0) {
            this.textArea.replaceRange("", 0, this.b[this.c]);
        }
        this.b[this.c] = n;
        this.c = (this.c + 1) % 1024;
    }
}
