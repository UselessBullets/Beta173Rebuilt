// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.locale.language;

public class I18n
{
    private static Language lang = Language.getInstance();
    
    public static String get(final String id) {
        return I18n.lang.getElement(id);
    }
    
    public static String get(final String id, final Object... args) {
        return I18n.lang.getElement(id, args);
    }

}
