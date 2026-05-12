// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.locale.language;

import java.io.IOException;
import java.util.Properties;

public class Language
{
    private static Language singleton;
    private Properties languageRepository;
    
    private Language() {
        this.languageRepository = new Properties();
        try {
            this.languageRepository.load(Language.class.getResourceAsStream("/lang/en_US.lang"));
            this.languageRepository.load(Language.class.getResourceAsStream("/lang/stats_US.lang"));
        }
        catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static Language getInstance() {
        return Language.singleton;
    }
    
    public String getElement(final String elementId) {
        return this.languageRepository.getProperty(elementId, elementId);
    }
    
    public String getElement(final String elementId, final Object... objects) {
        return String.format(this.languageRepository.getProperty(elementId, elementId), objects);
    }
    
    public String getElementName(final String elementId) {
        return this.languageRepository.getProperty(elementId + ".name", "");
    }
    
    static {
        Language.singleton = new Language();
    }
}
