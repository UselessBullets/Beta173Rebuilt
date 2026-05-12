// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.stats;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class AchievementMap
{
    public static AchievementMap instance;
    private Map statGuidMap;
    
    private AchievementMap() {
        this.statGuidMap = new HashMap();
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(AchievementMap.class.getResourceAsStream("/achievement/map.txt")));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                final String[] split = line.split(",");
                this.statGuidMap.put(Integer.parseInt(split[0]), split[1]);
            }
            bufferedReader.close();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static String getStatGuid(final int statId) {
        return AchievementMap.instance.statGuidMap.get(statId);
    }
    
    static {
        AchievementMap.instance = new AchievementMap();
    }
}
