// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui.achievement;

import net.minecraft.client.renderer.Tesselator;
import java.util.Iterator;
import net.minecraft.stats.Stat;
import net.minecraft.stats.ItemStat;
import net.minecraft.stats.Stats;
import java.util.ArrayList;

class StatsScreen_ItemStatisticsList extends StatsScreen_StatisticsList
{
    final /* synthetic */ StatsScreen statsScreen;
    
    public StatsScreen_ItemStatisticsList(final StatsScreen statsScreen) {
        this.statsScreen = statsScreen;
        super(statsScreen);
        this.statItemList = new ArrayList();
        for (final ItemStat stat : Stats.blocksMinedStats) {
            boolean b = false;
            final int itemId = stat.getItemId();
            if (statsScreen.stats.getValue(stat) > 0) {
                b = true;
            }
            else if (Stats.itemBroke[itemId] != null && statsScreen.stats.getValue(Stats.itemBroke[itemId]) > 0) {
                b = true;
            }
            else if (Stats.itemCrafted[itemId] != null && statsScreen.stats.getValue(Stats.itemCrafted[itemId]) > 0) {
                b = true;
            }
            if (b) {
                this.statItemList.add(stat);
            }
        }
        this.itemStatSorter = new StatsScreen_ItemStatisticsList_StatSorter(this, statsScreen);
    }
    
    @Override
    protected void renderHeader(final int x, final int y, final Tesselator t) {
        super.renderHeader(x, y, t);
        if (this.headerPressed == 0) {
            this.statsScreen.blitSlotIcon(x + 115 - 18 + 1, y + 1 + 1, 72, 18);
        }
        else {
            this.statsScreen.blitSlotIcon(x + 115 - 18, y + 1, 72, 18);
        }
        if (this.headerPressed == 1) {
            this.statsScreen.blitSlotIcon(x + 165 - 18 + 1, y + 1 + 1, 18, 18);
        }
        else {
            this.statsScreen.blitSlotIcon(x + 165 - 18, y + 1, 18, 18);
        }
        if (this.headerPressed == 2) {
            this.statsScreen.blitSlotIcon(x + 215 - 18 + 1, y + 1 + 1, 36, 18);
        }
        else {
            this.statsScreen.blitSlotIcon(x + 215 - 18, y + 1, 36, 18);
        }
    }
    
    @Override
    protected void renderItem(final int i, final int x, final int y, final int h, final Tesselator t) {
        final ItemStat slotStat = this.getSlotStat(i);
        final int itemId = slotStat.getItemId();
        this.statsScreen.blitSlot(x + 40, y, itemId);
        this.renderStat((ItemStat)Stats.itemBroke[itemId], x + 115, y, i % 2 == 0);
        this.renderStat((ItemStat)Stats.itemCrafted[itemId], x + 165, y, i % 2 == 0);
        this.renderStat(slotStat, x + 215, y, i % 2 == 0);
    }
    
    @Override
    protected String getHeaderDescriptionId(final int column) {
        if (column == 1) {
            return "stat.crafted";
        }
        if (column == 2) {
            return "stat.used";
        }
        return "stat.depleted";
    }
}
