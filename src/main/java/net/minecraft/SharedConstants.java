// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SharedConstants
{
    public static final String VERSION_STRING = "Beta 1.7.3";
    public static final int maxChatLength = 100;
    public static final String acceptableLetters = readAcceptableChars();
    public static final char[] ILLEGAL_FILE_CHARACTERS = new char[] { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };
    
    private static String readAcceptableChars() {
        StringBuilder result = new StringBuilder();
        try {
            final BufferedReader br = new BufferedReader(new InputStreamReader(SharedConstants.class.getResourceAsStream("/font.txt"), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("#")) {
                    result.append(line);
                }
            }
            br.close();
        } catch (final Exception ignored) {}
        return result.toString();
    }

}
