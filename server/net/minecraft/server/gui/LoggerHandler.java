// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.gui;

import java.util.logging.LogRecord;
import javax.swing.JTextArea;
import java.util.logging.Formatter;
import java.util.logging.Handler;

public class LoggerHandler extends Handler
{
    private int[] b;
    private int c;
    Formatter formatter;
    private JTextArea textArea;
    
    public LoggerHandler(final JTextArea textArea) {
        this.b = new int[1024];
        this.c = 0;
        this.setFormatter(this.formatter = new LoggerHandler_Formatter(this));
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
