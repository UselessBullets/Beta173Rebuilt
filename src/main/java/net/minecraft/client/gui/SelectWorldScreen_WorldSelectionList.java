// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import java.util.Date;
import util.Mth;
import net.minecraft.world.level.LevelSummary;
import net.minecraft.client.renderer.Tesselator;

class SelectWorldScreen_WorldSelectionList extends ScrolledSelectionList
{
    final /* synthetic */ SelectWorldScreen sws;
    
    public SelectWorldScreen_WorldSelectionList(final SelectWorldScreen sws) {
        this.sws = sws;
        super(sws.minecraft, sws.width, sws.height, 32, sws.height - 64, 36);
    }
    
    @Override
    protected int getNumberOfItems() {
        return this.sws.levelList.size();
    }
    
    @Override
    protected void selectItem(final int item, final boolean doubleClick) {
        this.sws.selectedWorld = item;
        final boolean active = this.sws.selectedWorld >= 0 && this.sws.selectedWorld < this.getNumberOfItems();
        this.sws.selectButton.active = active;
        this.sws.deleteButton.active = active;
        this.sws.renameButton.active = active;
        if (doubleClick && active) {
            this.sws.worldSelected(item);
        }
    }
    
    @Override
    protected boolean isSelectedItem(final int item) {
        return item == this.sws.selectedWorld;
    }
    
    @Override
    protected int getMaxPosition() {
        return this.sws.levelList.size() * 36;
    }
    
    @Override
    protected void renderBackground() {
        this.sws.renderBackground();
    }
    
    @Override
    protected void renderItem(final int i, final int x, final int y, final int h, final Tesselator t) {
        final LevelSummary levelSummary = this.sws.levelList.get(i);
        String s = levelSummary.getLevelName();
        if (s == null || Mth.isNullOrEmpty(s)) {
            s = this.sws.worldLang + " " + (i + 1);
        }
        final String string = levelSummary.getLevelId() + " (" + this.sws.DATE_FORMAT.format(new Date(levelSummary.getLastPlayed())) + ", " + levelSummary.getSizeOnDisk() / 1024L * 100L / 1024L / 100.0f + " MB)";
        String string2 = "";
        if (levelSummary.isRequiresConversion()) {
            string2 = this.sws.conversionLang + " " + string2;
        }
        this.sws.drawString(this.sws.font, s, x + 2, y + 1, 16777215);
        this.sws.drawString(this.sws.font, string, x + 2, y + 12, 8421504);
        this.sws.drawString(this.sws.font, string2, x + 2, y + 12 + 10, 8421504);
    }
}
