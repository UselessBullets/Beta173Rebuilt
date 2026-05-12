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
    
    static {
        Stat.numberFormat = NumberFormat.getIntegerInstance(Locale.US);
        Stat.defaultFormat = new DefaultFormat();
        Stat.decimalFormat = new DecimalFormat("########0.00");
        Stat.timeFormat = new TimeFormatter();
        Stat.distanceFormat = new DistanceFormatter();
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
            final double number = value / 100.0;
            final double number2 = number / 1000.0;
            if (number2 > 0.5) {
                return decimalFormat.format(number2) + " km";
            }
            if (number > 0.5) {
                return decimalFormat.format(number) + " m";
            }
            return value + " cm";
        }
    }

    static final class TimeFormatter implements StatFormatter
    {
        public String format(final int value) {
            final double d = value / 20.0;
            final double number = d / 60.0;
            final double number2 = number / 60.0;
            final double number3 = number2 / 24.0;
            final double number4 = number3 / 365.0;
            if (number4 > 0.5) {
                return decimalFormat.format(number4) + " y";
            }
            if (number3 > 0.5) {
                return decimalFormat.format(number3) + " d";
            }
            if (number2 > 0.5) {
                return decimalFormat.format(number2) + " h";
            }
            if (number > 0.5) {
                return decimalFormat.format(number) + " m";
            }
            return d + " s";
        }
    }
}
