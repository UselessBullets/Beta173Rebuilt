// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

public class Settings
{
    public static Logger logger;
    private Properties properties;
    private File file;
    
    public Settings(final File file) {
        this.properties = new Properties();
        this.file = file;
        if (file.exists()) {
            try {
                this.properties.load(new FileInputStream(file));
            }
            catch (final Exception thrown) {
                Settings.logger.log(Level.WARNING, "Failed to load " + file, thrown);
                this.generateNewProperties();
            }
        }
        else {
            Settings.logger.log(Level.WARNING, file + " does not exist");
            this.generateNewProperties();
        }
    }
    
    public void generateNewProperties() {
        Settings.logger.log(Level.INFO, "Generating new properties file");
        this.saveProperties();
    }
    
    public void saveProperties() {
        try {
            this.properties.store(new FileOutputStream(this.file), "Minecraft server properties");
        }
        catch (final Exception thrown) {
            Settings.logger.log(Level.WARNING, "Failed to save " + this.file, thrown);
            this.generateNewProperties();
        }
    }
    
    public String getString(final String key, final String defaultValue) {
        if (!this.properties.containsKey(key)) {
            this.properties.setProperty(key, defaultValue);
            this.saveProperties();
        }
        return this.properties.getProperty(key, defaultValue);
    }
    
    public int getInt(final String key, final int defaultVal) {
        try {
            return Integer.parseInt(this.getString(key, "" + defaultVal));
        }
        catch (final Exception ex) {
            this.properties.setProperty(key, "" + defaultVal);
            return defaultVal;
        }
    }
    
    public boolean getBoolean(final String key, final boolean defaultVal) {
        try {
            return Boolean.parseBoolean(this.getString(key, "" + defaultVal));
        }
        catch (final Exception ex) {
            this.properties.setProperty(key, "" + defaultVal);
            return defaultVal;
        }
    }
    
    public void setBooleanAndSave(final String key, final boolean value) {
        this.properties.setProperty(key, "" + value);
        this.saveProperties();
    }
    
    static {
        Settings.logger = Logger.getLogger("Minecraft");
    }
}
