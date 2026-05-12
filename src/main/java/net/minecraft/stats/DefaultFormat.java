// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.stats;

final class DefaultFormat implements StatFormatter
{
    public String format(final int value) {
        return Stat.numberFormat.format(value);
    }
}
