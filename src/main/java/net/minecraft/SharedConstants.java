// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SharedConstants
{
    public static final String acceptableLetters;
    public static final char[] ILLEGAL_FILE_CHARACTERS;
    
    private static String readAcceptableChars() {
        String string = "";
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(SharedConstants.class.getResourceAsStream("/font.txt"), "UTF-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.startsWith("#")) {
                    string += line;
                }
            }
            bufferedReader.close();
        }
        catch (final Exception ex) {}
        return string;
    }
    
    static {
        acceptableLetters = readAcceptableChars();
        ILLEGAL_FILE_CHARACTERS = new char[] { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };
    }
}
