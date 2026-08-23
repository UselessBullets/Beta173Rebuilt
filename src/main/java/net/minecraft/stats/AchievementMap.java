// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.stats;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class AchievementMap
{
    public static AchievementMap instance = new AchievementMap();
    private Map<Integer, String> statGuidMap;
    
    private AchievementMap() {
        this.statGuidMap = new HashMap<>();
        try {
            final BufferedReader br = new BufferedReader(new InputStreamReader(AchievementMap.class.getResourceAsStream("/achievement/map.txt")));
            String line;
            while ((line = br.readLine()) != null) {
                final String[] split = line.split(",");
                int statId = Integer.parseInt(split[0]);
                String guid = split[1];
                this.statGuidMap.put(statId, guid);
            }
            br.close();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String getStatGuid(final int statId) {
        return AchievementMap.instance.statGuidMap.get(statId);
    }

}
