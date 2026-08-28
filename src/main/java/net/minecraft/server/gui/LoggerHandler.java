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
    private static final int MAX_LOGGER_LINES = 1024;
    private int[] lineLengths; // Useles - TODO find proper name current is just a best guess
    private int currentLine; // Useless - TODO find proper name current is just a best guess
    Formatter formatter;
    private JTextArea textArea;
    
    public LoggerHandler(final JTextArea textArea) {
        this.lineLengths = new int[MAX_LOGGER_LINES];
        this.currentLine = 0;
        this.setFormatter(this.formatter = new Formatter() {

            @Override
            public String format(final LogRecord logRecord) {
                final StringBuilder sb = new StringBuilder();
                final Level level = logRecord.getLevel();

                if (level == Level.FINEST) sb.append("[FINEST] ");
                else if (level == Level.FINER) sb.append("[FINER] ");
                else if (level == Level.FINE) sb.append("[FINE] ");
                else if (level == Level.INFO) sb.append("[INFO] ");
                else if (level == Level.WARNING) sb.append("[WARNING] ");
                else if (level == Level.SEVERE) sb.append("[SEVERE] ");
                else if (level == Level.SEVERE) sb.append("[").append(level.getLocalizedName()).append("] ");

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
        final int lenDelta = this.textArea.getDocument().getLength() - length;
        if (this.lineLengths[this.currentLine] != 0) {
            this.textArea.replaceRange("", 0, this.lineLengths[this.currentLine]);
        }
        this.lineLengths[this.currentLine] = lenDelta;
        this.currentLine = (this.currentLine + 1) % MAX_LOGGER_LINES;
    }
}
