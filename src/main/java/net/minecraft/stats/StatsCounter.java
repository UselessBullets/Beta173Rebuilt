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

// Useless - Theres very little on the local variable names and method structures of this class, alot of it will be guesses
public class StatsCounter
{
    private Map<Stat, Integer> stats = new HashMap<>();
    private Map<Stat, Integer> unsentStats = new HashMap<>();
    private boolean requiresSave = false;
    private StatsSyncher statsSyncher;
    
    public StatsCounter(final User user, final File workDir) {
        final File statsFile = new File(workDir, "stats");
        if (!statsFile.exists()) statsFile.mkdir();

        for (final File file : workDir.listFiles()) {
            if (file.getName().startsWith("stats_") && file.getName().endsWith(".dat")) {
                final File dest = new File(statsFile, file.getName());
                if (!dest.exists()) {
                    System.out.println("Relocating " + file.getName());
                    file.renameTo(dest);
                }
            }
        }
        this.statsSyncher = new StatsSyncher(user, this, statsFile);
    }
    
    public void award(final Stat stat, final int count) {
        this.add(this.unsentStats, stat, count);
        this.add(this.stats, stat, count);
        this.requiresSave = true;
    }
    
    private void add(final Map<Stat, Integer> statMap, final Stat stat, final int count) {
        final Integer value = statMap.get(stat);
        statMap.put(stat, (value == null ? 0 : value) + count);
    }
    
    public Map<Stat, Integer> getUnsent() {
        return new HashMap<>(this.unsentStats);
    }
    
    public void loadStats(final Map<Stat, Integer> statMap) {
        if (statMap == null) return;

        this.requiresSave = true;
        for (final Stat stat : statMap.keySet()) {
            this.add(this.unsentStats, stat, statMap.get(stat));
            this.add(this.stats, stat, statMap.get(stat));
        }
    }
    
    public void mergeStats(final Map<Stat, Integer> statMap) {
        if (statMap == null) return;

        for (final Stat stat : statMap.keySet()) {
            final Integer change = this.unsentStats.get(stat);
            this.stats.put(stat, statMap.get(stat) + (change == null ? 0 : change));
        }
    }
    
    public void queueStats(final Map<Stat, Integer> statMap) {
        if (statMap == null) return;

        this.requiresSave = true;
        for (final Stat stat : statMap.keySet()) {
            this.add(this.unsentStats, stat, statMap.get(stat));
        }
    }
    
    public static Map<Stat, Integer> loadStatsFromString(final String statsString) {
        final HashMap<Stat, Integer> statMap = new HashMap<>();
        try {
            // Useless - Since this is loading from a file it uses "local" as the source instead of the session id
            final String source = "local";
            final StringBuilder data = new StringBuilder();
            final JsonRootNode parse = new JdomParser().parse(statsString);
            for (JsonNode jsonNode : parse.getArrayNode("stats-change")) {
                final Map.Entry<JsonStringNode, JsonNode> entry = jsonNode.getFields().entrySet().iterator().next();
                final int statId = Integer.parseInt(entry.getKey().getText());
                final int value = Integer.parseInt(entry.getValue().getText());
                final Stat stat = Stats.getStat(statId);
                if (stat == null) {
                    System.out.println(statId + " is not a valid stat");
                } else {
                    data.append(Stats.getStat(statId).guid).append(",");
                    data.append(value).append(",");
                    statMap.put(stat, value);
                }
            }

            final Hasher hasher = new Hasher(source);
            if (!hasher.getHash(data.toString()).equals(parse.getStringValue("checksum"))) {
                System.out.println("CHECKSUM MISMATCH");
                return null;
            }
        }
        catch (final InvalidSyntaxException e) {
            e.printStackTrace();
        }
        return statMap;
    }
    
    public static String saveStatsToString(final String name, final String sessionId, final Map<Stat, Integer> stats) {
        // Useless - Session Id used as the source, presumably this is a way for the server to validate the stats came from a valid game session for synching with a global stats board
        final String source = sessionId;
        final StringBuilder out = new StringBuilder();
        final StringBuilder data = new StringBuilder();

        boolean start = true;
        out.append("{\r\n");
        if (name != null && sessionId != null) {
            out.append("  \"user\":{\r\n");
            out.append("    \"name\":\"").append(name).append("\",\r\n");
            out.append("    \"sessionid\":\"").append(sessionId).append("\"\r\n");
            out.append("  },\r\n");
        }
        out.append("  \"stats-change\":[");
        for (final Stat stat : stats.keySet()) {
            if (!start) {
                out.append("},");
            }
            else {
                start = false;
            }
            out.append("\r\n    {\"").append(stat.id).append("\":").append(stats.get(stat));
            data.append(stat.guid).append(",");
            data.append(stats.get(stat)).append(",");
        }
        if (!start) {
            out.append("}");
        }

        final Hasher hasher = new Hasher(source);
        out.append("\r\n  ],\r\n");
        out.append("  \"checksum\":\"").append(hasher.getHash(data.toString())).append("\"\r\n");
        out.append("}");
        return out.toString();
    }
    
    public boolean hasTaken(final Achievement ach) {
        return this.stats.containsKey(ach);
    }
    
    public boolean canTake(final Achievement ach) {
        return ach.requires == null || this.hasTaken(ach.requires);
    }
    
    public int getValue(final Stat stat) {
        final Integer value = this.stats.get(stat);
        return value == null ? 0 : value;
    }
    
    public void forceSend() {
//        this.statsSyncher.forceSendUnsent(this.getUnsent()); // Useless - Theoretically what would have been here in source, matches formatting of StatsCounter.forceSave
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
