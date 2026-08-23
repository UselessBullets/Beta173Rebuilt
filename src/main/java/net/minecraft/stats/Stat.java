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
    public boolean awardLocallyOnly = false;
    public String guid;
    private final StatFormatter formatter;
    private static NumberFormat numberFormat = NumberFormat.getIntegerInstance(Locale.US);
    public static StatFormatter defaultFormat = new DefaultFormat();
    private static DecimalFormat decimalFormat = new DecimalFormat("########0.00");
    public static StatFormatter timeFormat = new TimeFormatter();
    public static StatFormatter distanceFormat = new DistanceFormatter();
    
    public Stat(final int id, final String name, final StatFormatter formatter) {
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
    
    public boolean isAchievement() {
        return false;
    }
    
    public String format(final int value) {
        return this.formatter.format(value);
    }
    
    @Override
    public String toString() {
        return this.name;
    }

    static final class TimeFormatter implements StatFormatter
    {
        public String format(final int value) {
            final double seconds = value / 20.0;
            final double minutes = seconds / 60.0;
            final double hours = minutes / 60.0;
            final double days = hours / 24.0;
            final double years = days / 365.0;

            if (years > 0.5) return decimalFormat.format(years) + " y";
            if (days > 0.5) return decimalFormat.format(days) + " d";
            if (hours > 0.5) return decimalFormat.format(hours) + " h";
            if (minutes > 0.5) return decimalFormat.format(minutes) + " m";
            return seconds + " s";
        }
    }
    static final class DefaultFormat implements StatFormatter
    {
        public String format(final int value) {
            return numberFormat.format(value);
        }

    }
    static final class DistanceFormatter implements StatFormatter
    {
        public String format(final int value) {
            final double meters = value / 100.0;
            final double kilometers = meters / 1000.0;

            if (kilometers > 0.5) return decimalFormat.format(kilometers) + " km";
            if (meters > 0.5) return decimalFormat.format(meters) + " m";
            return value + " cm";
        }

    }
}
