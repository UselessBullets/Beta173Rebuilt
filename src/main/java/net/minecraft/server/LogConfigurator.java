// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server;

import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Formatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class LogConfigurator
{
    public static Logger logger;
    
    public static void initLogger() {
        final LogConfigurator_Formatter logConfigurator_Formatter = new LogConfigurator_Formatter();
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
}
