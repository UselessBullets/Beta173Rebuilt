// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.stats;

final class DistanceFormatter implements StatFormatter
{
    public String format(final int value) {
        final double number = value / 100.0;
        final double number2 = number / 1000.0;
        if (number2 > 0.5) {
            return Stat.decimalFormat.format(number2) + " km";
        }
        if (number > 0.5) {
            return Stat.decimalFormat.format(number) + " m";
        }
        return value + " cm";
    }
}
