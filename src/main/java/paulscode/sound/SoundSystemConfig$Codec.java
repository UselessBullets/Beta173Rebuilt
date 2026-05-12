// 
// Decompiled by Procyon v0.6.0
// 

package paulscode.sound;

import java.util.Locale;

class SoundSystemConfig$Codec
{
    public String extensionRegX;
    public Class iCodecClass;
    
    public SoundSystemConfig$Codec(final String string, final Class class2) {
        this.extensionRegX = "";
        if (string != null && string.length() > 0) {
            this.extensionRegX = ".*";
            for (int i = 0; i < string.length(); ++i) {
                final String substring = string.substring(i, i + 1);
                this.extensionRegX = this.extensionRegX + "[" + substring.toLowerCase(Locale.ENGLISH) + substring.toUpperCase(Locale.ENGLISH) + "]";
            }
            this.extensionRegX += "$";
        }
        this.iCodecClass = class2;
    }
    
    public ICodec getInstance() {
        if (this.iCodecClass == null) {
            return null;
        }
        ICodec instance;
        try {
            instance = this.iCodecClass.newInstance();
        }
        catch (final InstantiationException ex) {
            this.instantiationErrorMessage();
            return null;
        }
        catch (final IllegalAccessException ex2) {
            this.instantiationErrorMessage();
            return null;
        }
        catch (final ExceptionInInitializerError exceptionInInitializerError) {
            this.instantiationErrorMessage();
            return null;
        }
        catch (final SecurityException ex3) {
            this.instantiationErrorMessage();
            return null;
        }
        if (instance == null) {
            this.instantiationErrorMessage();
            return null;
        }
        return instance;
    }
    
    private void instantiationErrorMessage() {
        errorMessage("Unrecognized ICodec implementation in method 'getInstance'.  Ensure that the implementing class has one public, parameterless constructor.");
    }
}
