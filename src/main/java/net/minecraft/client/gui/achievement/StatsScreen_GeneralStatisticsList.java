// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui.achievement;

import net.minecraft.stats.Stat;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.stats.Stats;
import net.minecraft.client.gui.ScrolledSelectionList;

class StatsScreen_GeneralStatisticsList extends ScrolledSelectionList
{
    final /* synthetic */ StatsScreen statsScreen;
    
    public StatsScreen_GeneralStatisticsList(final StatsScreen statsScreen) {
        this.statsScreen = statsScreen;
        super(statsScreen.minecraft, statsScreen.width, statsScreen.height, 32, statsScreen.height - 64, 10);
        this.setRenderSelection(false);
    }
    
    @Override
    protected int getNumberOfItems() {
        return Stats.generalStats.size();
    }
    
    @Override
    protected void selectItem(final int item, final boolean doubleClick) {
    }
    
    @Override
    protected boolean isSelectedItem(final int item) {
        return false;
    }
    
    @Override
    protected int getMaxPosition() {
        return this.getNumberOfItems() * 10;
    }
    
    @Override
    protected void renderBackground() {
        this.statsScreen.renderBackground();
    }
    
    @Override
    protected void renderItem(final int i, final int x, final int y, final int h, final Tesselator t) {
        final Stat stat = Stats.generalStats.get(i);
        this.statsScreen.drawString(this.statsScreen.font, stat.name, x + 2, y + 1, (i % 2 == 0) ? 16777215 : 9474192);
        final String format = stat.format(this.statsScreen.stats.getValue(stat));
        this.statsScreen.drawString(this.statsScreen.font, format, x + 2 + 213 - this.statsScreen.font.width(format), y + 1, (i % 2 == 0) ? 16777215 : 9474192);
    }
}
