// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.stats;

final class TimeFormatter implements StatFormatter
{
    public String format(final int value) {
        final double d = value / 20.0;
        final double number = d / 60.0;
        final double number2 = number / 60.0;
        final double number3 = number2 / 24.0;
        final double number4 = number3 / 365.0;
        if (number4 > 0.5) {
            return Stat.decimalFormat.format(number4) + " y";
        }
        if (number3 > 0.5) {
            return Stat.decimalFormat.format(number3) + " d";
        }
        if (number2 > 0.5) {
            return Stat.decimalFormat.format(number2) + " h";
        }
        if (number > 0.5) {
            return Stat.decimalFormat.format(number) + " m";
        }
        return d + " s";
    }
}
