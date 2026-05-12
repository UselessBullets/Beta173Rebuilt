// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.stats;

import java.util.Locale;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Stat
{
    public final int id;
    public final String name;
    public boolean awardLocallyOnly;
    public String guid;
    private final StatFormatter formatter;
    private static NumberFormat numberFormat;
    public static StatFormatter defaultFormat;
    private static DecimalFormat decimalFormat;
    public static StatFormatter timeFormat;
    public static StatFormatter distanceFormat;
    
    public Stat(final int id, final String name, final StatFormatter formatter) {
        this.awardLocallyOnly = false;
        this.id = id;
        this.name = name;
        this.formatter = formatter;
    }
    
    public Stat(final int id, final String name) {
        this(id, name, Stat.defaultFormat);
    }
    
    public Stat setAwardLocallyOnly() {
        this.awardLocallyOnly = true;
        return this;
    }
    
    public Stat postConstruct() {
        if (Stats.statsById.containsKey(this.id)) {
            throw new RuntimeException("Duplicate stat id: \"" + Stats.statsById.get(this.id).name + "\" and \"" + this.name + "\" at id " + this.id);
        }
        Stats.all.add(this);
        Stats.statsById.put(this.id, this);
        this.guid = AchievementMap.getStatGuid(this.id);
        return this;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    static {
        Stat.numberFormat = NumberFormat.getIntegerInstance(Locale.US);
        Stat.defaultFormat = new DefaultFormat();
        Stat.decimalFormat = new DecimalFormat("########0.00");
        Stat.timeFormat = new TimeFormatter();
        Stat.distanceFormat = new DistanceFormatter();
    }
}
