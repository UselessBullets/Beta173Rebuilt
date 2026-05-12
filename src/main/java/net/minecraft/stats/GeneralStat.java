// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.stats;

public class GeneralStat extends Stat
{
    public GeneralStat(final int id, final String name, final StatFormatter formatter) {
        super(id, name, formatter);
    }
    
    public GeneralStat(final int id, final String name) {
        super(id, name);
    }
    
    @Override
    public Stat postConstruct() {
        super.postConstruct();
        Stats.generalStats.add(this);
        return this;
    }
}
