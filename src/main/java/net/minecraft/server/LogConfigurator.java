// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LogConfigurator
{
    public static Logger logger;
    
    public static void initLogger() {
        final Formatter logConfigurator_Formatter = new Formatter();
        LogConfigurator.logger.setUseParentHandlers(false);
        final ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(logConfigurator_Formatter);
        LogConfigurator.logger.addHandler(handler);
        try {
            final FileHandler handler2 = new FileHandler("server.log", true);
            handler2.setFormatter(logConfigurator_Formatter);
            LogConfigurator.logger.addHandler(handler2);
        }
        catch (final Exception thrown) {
            LogConfigurator.logger.log(Level.WARNING, "Failed to log to server.log", thrown);
        }
    }
    
    static {
        LogConfigurator.logger = Logger.getLogger("Minecraft");
    }

    static final class Formatter extends java.util.logging.Formatter
    {
        private SimpleDateFormat df;

        Formatter() {
            this.df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }

        @Override
        public String format(final LogRecord logRecord) {
            final StringBuilder sb = new StringBuilder();
            sb.append(this.df.format(logRecord.getMillis()));
            final Level level = logRecord.getLevel();
            if (level == Level.FINEST) {
                sb.append(" [FINEST] ");
            }
            else if (level == Level.FINER) {
                sb.append(" [FINER] ");
            }
            else if (level == Level.FINE) {
                sb.append(" [FINE] ");
            }
            else if (level == Level.INFO) {
                sb.append(" [INFO] ");
            }
            else if (level == Level.WARNING) {
                sb.append(" [WARNING] ");
            }
            else if (level == Level.SEVERE) {
                sb.append(" [SEVERE] ");
            }
            else if (level == Level.SEVERE) {
                sb.append(" [" + level.getLocalizedName() + "] ");
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
}
