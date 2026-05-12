// 
// Decompiled by Procyon v0.6.0
// 

package paulscode.sound;

import java.net.URL;

public class FilenameURL
{
    private SoundSystemLogger logger;
    private String filename;
    private URL url;
    
    public FilenameURL(final URL uRL, final String string) {
        this.filename = null;
        this.url = null;
        this.logger = SoundSystemConfig.getLogger();
        this.filename = string;
        this.url = uRL;
    }
    
    public FilenameURL(final String string) {
        this.filename = null;
        this.url = null;
        this.logger = SoundSystemConfig.getLogger();
        this.filename = string;
        this.url = null;
    }
    
    public String getFilename() {
        return this.filename;
    }
    
    public URL getURL() {
        if (this.url == null) {
            if (this.filename.matches("^[hH][tT][tT][pP]://.*")) {
                try {
                    this.url = new URL(this.filename);
                    return this.url;
                }
                catch (final Exception exception) {
                    this.errorMessage("Unable to access online URL in method 'getURL'");
                    this.printStackTrace(exception);
                    return null;
                }
            }
            this.url = this.getClass().getClassLoader().getResource(SoundSystemConfig.getSoundFilesPackage() + this.filename);
        }
        return this.url;
    }
    
    private void errorMessage(final String string) {
        this.logger.errorMessage("MidiChannel", string, 0);
    }
    
    private void printStackTrace(final Exception exception) {
        this.logger.printStackTrace(exception, 1);
    }
}
