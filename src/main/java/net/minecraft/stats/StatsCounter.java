// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.stats;

import argo.jdom.JsonRootNode;
import argo.saj.InvalidSyntaxException;
import argo.jdom.JsonNode;
import argo.jdom.JsonStringNode;
import argo.jdom.JdomParser;
import java.util.Iterator;
import java.util.HashMap;
import java.io.File;
import net.minecraft.client.User;
import java.util.Map;

public class StatsCounter
{
    private Map<Stat, Integer> stats;
    private Map<Stat, Integer> unsentStats;
    private boolean requiresSave;
    private StatsSyncher statsSyncher;
    
    public StatsCounter(final User user, final File workDir) {
        this.stats = new HashMap();
        this.unsentStats = new HashMap();
        this.requiresSave = false;
        final File file = new File(workDir, "stats");
        if (!file.exists()) {
            file.mkdir();
        }
        for (final File file2 : workDir.listFiles()) {
            if (file2.getName().startsWith("stats_") && file2.getName().endsWith(".dat")) {
                final File dest = new File(file, file2.getName());
                if (!dest.exists()) {
                    System.out.println("Relocating " + file2.getName());
                    file2.renameTo(dest);
                }
            }
        }
        this.statsSyncher = new StatsSyncher(user, this, file);
    }
    
    public void award(final Stat stat, final int count) {
        this.add(this.unsentStats, stat, count);
        this.add(this.stats, stat, count);
        this.requiresSave = true;
    }
    
    private void add(final Map<Stat, Integer> statMap, final Stat stat, final int count) {
        final Integer n = statMap.get(stat);
        statMap.put(stat, ((n == null) ? 0 : n) + count);
    }
    
    public Map getUnsent() {
        return new HashMap(this.unsentStats);
    }
    
    public void loadStats(final Map<Stat, Integer> statMap) {
        if (statMap == null) {
            return;
        }
        this.requiresSave = true;
        for (final Stat stat : statMap.keySet()) {
            this.add(this.unsentStats, stat, (int)statMap.get(stat));
            this.add(this.stats, stat, (int)statMap.get(stat));
        }
    }
    
    public void mergeStats(final Map<Stat, Integer> statMap) {
        if (statMap == null) {
            return;
        }
        for (final Stat stat : statMap.keySet()) {
            final Integer n = this.unsentStats.get(stat);
            this.stats.put(stat, statMap.get(stat) + ((n == null) ? 0 : n));
        }
    }
    
    public void queueStats(final Map<Stat, Integer> statMap) {
        if (statMap == null) {
            return;
        }
        this.requiresSave = true;
        for (final Stat stat : statMap.keySet()) {
            this.add(this.unsentStats, stat, (int)statMap.get(stat));
        }
    }
    
    public static Map<Stat, Integer> loadStatsFromString(final String statsString) {
        final HashMap<Stat, Integer> hashMap = new HashMap<>();
        try {
            final String salt = "local";
            final StringBuilder sb = new StringBuilder();
            final JsonRootNode parse = new JdomParser().parse(statsString);
            final Iterator iterator = parse.getArrayNode("stats-change").iterator();
            while (iterator.hasNext()) {
                final Map.Entry<JsonStringNode, JsonNode> entry = ((JsonNode)iterator.next()).getFields().entrySet().iterator().next();
                final int int1 = Integer.parseInt(entry.getKey().getText());
                final int int2 = Integer.parseInt(((JsonNode)entry.getValue()).getText());
                final Stat stat = Stats.getStat(int1);
                if (stat == null) {
                    System.out.println(int1 + " is not a valid stat");
                }
                else {
                    sb.append(Stats.getStat(int1).guid).append(",");
                    sb.append(int2).append(",");
                    hashMap.put(stat, int2);
                }
            }
            if (!new Hasher(salt).getHash(sb.toString()).equals(parse.getStringValue("checksum"))) {
                System.out.println("CHECKSUM MISMATCH");
                return null;
            }
        }
        catch (final InvalidSyntaxException ex) {
            ex.printStackTrace();
        }
        return hashMap;
    }
    
    public static String saveStatsToString(final String name, final String sessionId, final Map<Stat, Integer> stats) {
        final StringBuilder sb = new StringBuilder();
        final StringBuilder sb2 = new StringBuilder();
        int n = 1;
        sb.append("{\r\n");
        if (name != null && sessionId != null) {
            sb.append("  \"user\":{\r\n");
            sb.append("    \"name\":\"").append(name).append("\",\r\n");
            sb.append("    \"sessionid\":\"").append(sessionId).append("\"\r\n");
            sb.append("  },\r\n");
        }
        sb.append("  \"stats-change\":[");
        for (final Stat stat : stats.keySet()) {
            if (n == 0) {
                sb.append("},");
            }
            else {
                n = 0;
            }
            sb.append("\r\n    {\"").append(stat.id).append("\":").append(stats.get(stat));
            sb2.append(stat.guid).append(",");
            sb2.append(stats.get(stat)).append(",");
        }
        if (n == 0) {
            sb.append("}");
        }
        final Hasher hasher = new Hasher(sessionId);
        sb.append("\r\n  ],\r\n");
        sb.append("  \"checksum\":\"").append(hasher.getHash(sb2.toString())).append("\"\r\n");
        sb.append("}");
        return sb.toString();
    }
    
    public boolean hasTaken(final Achievement ach) {
        return this.stats.containsKey(ach);
    }
    
    public boolean canTake(final Achievement ach) {
        return ach.requires == null || this.hasTaken(ach.requires);
    }
    
    public int getValue(final Stat stat) {
        final Integer n = this.stats.get(stat);
        return (n == null) ? 0 : n;
    }
    
    public void forceSend() {
    }
    
    public void forceSave() {
        this.statsSyncher.forceSaveUnsent(this.getUnsent());
    }
    
    public void tick() {
        if (this.requiresSave && this.statsSyncher.maySave()) {
            this.statsSyncher.saveUnsent(this.getUnsent());
        }
        this.statsSyncher.tick();
    }
}
