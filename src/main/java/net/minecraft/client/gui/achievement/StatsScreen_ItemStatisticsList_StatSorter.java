// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui.achievement;

import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.stats.ItemStat;
import java.util.Comparator;

class StatsScreen_ItemStatisticsList_StatSorter implements Comparator
{
    final /* synthetic */ StatsScreen ss;
    final /* synthetic */ StatsScreen_ItemStatisticsList isl;
    
    StatsScreen_ItemStatisticsList_StatSorter(final StatsScreen_ItemStatisticsList isl, final StatsScreen ss) {
        this.isl = isl;
        this.ss = ss;
    }
    
    public int compare(final ItemStat o1, final ItemStat o2) {
        final int itemId = o1.getItemId();
        final int itemId2 = o2.getItemId();
        Stat stat = null;
        Stat stat2 = null;
        if (this.isl.sortColumn == 0) {
            stat = Stats.itemBroke[itemId];
            stat2 = Stats.itemBroke[itemId2];
        }
        else if (this.isl.sortColumn == 1) {
            stat = Stats.itemCrafted[itemId];
            stat2 = Stats.itemCrafted[itemId2];
        }
        else if (this.isl.sortColumn == 2) {
            stat = Stats.itemUsed[itemId];
            stat2 = Stats.itemUsed[itemId2];
        }
        if (stat != null || stat2 != null) {
            if (stat == null) {
                return 1;
            }
            if (stat2 == null) {
                return -1;
            }
            final int value = this.isl.statsScreen.stats.getValue(stat);
            final int value2 = this.isl.statsScreen.stats.getValue(stat2);
            if (value != value2) {
                return (value - value2) * this.isl.sortOrder;
            }
        }
        return itemId - itemId2;
    }
}
