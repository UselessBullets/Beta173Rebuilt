// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui.achievement;

import java.util.Collections;
import net.minecraft.world.item.Item;
import net.minecraft.locale.language.Language;
import net.minecraft.stats.Stat;
import net.minecraft.stats.ItemStat;
import org.lwjgl.input.Mouse;
import net.minecraft.client.renderer.Tesselator;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.gui.ScrolledSelectionList;

abstract class StatsScreen_StatisticsList extends ScrolledSelectionList
{
    protected int headerPressed;
    protected List statItemList;
    protected Comparator itemStatSorter;
    protected int sortColumn;
    protected int sortOrder;
    final /* synthetic */ StatsScreen ss;
    
    protected StatsScreen_StatisticsList(final StatsScreen statsScreen) {
        this.ss = statsScreen;
        super(statsScreen.minecraft, statsScreen.width, statsScreen.height, 32, statsScreen.height - 64, 20);
        this.headerPressed = -1;
        this.sortColumn = -1;
        this.sortOrder = 0;
        this.setRenderSelection(false);
        this.setRenderHeader(true, 20);
    }
    
    @Override
    protected void selectItem(final int item, final boolean doubleClick) {
    }
    
    @Override
    protected boolean isSelectedItem(final int item) {
        return false;
    }
    
    @Override
    protected void renderBackground() {
        this.ss.renderBackground();
    }
    
    @Override
    protected void renderHeader(final int x, final int y, final Tesselator t) {
        if (!Mouse.isButtonDown(0)) {
            this.headerPressed = -1;
        }
        if (this.headerPressed == 0) {
            this.ss.blitSlotIcon(x + 115 - 18, y + 1, 0, 0);
        }
        else {
            this.ss.blitSlotIcon(x + 115 - 18, y + 1, 0, 18);
        }
        if (this.headerPressed == 1) {
            this.ss.blitSlotIcon(x + 165 - 18, y + 1, 0, 0);
        }
        else {
            this.ss.blitSlotIcon(x + 165 - 18, y + 1, 0, 18);
        }
        if (this.headerPressed == 2) {
            this.ss.blitSlotIcon(x + 215 - 18, y + 1, 0, 0);
        }
        else {
            this.ss.blitSlotIcon(x + 215 - 18, y + 1, 0, 18);
        }
        if (this.sortColumn != -1) {
            int n = 79;
            int integer4 = 18;
            if (this.sortColumn == 1) {
                n = 129;
            }
            else if (this.sortColumn == 2) {
                n = 179;
            }
            if (this.sortOrder == 1) {
                integer4 = 36;
            }
            this.ss.blitSlotIcon(x + n, y + 1, integer4, 0);
        }
    }
    
    @Override
    protected void clickedHeader(final int headerMouseX, final int headerMouseY) {
        this.headerPressed = -1;
        if (headerMouseX >= 79 && headerMouseX < 115) {
            this.headerPressed = 0;
        }
        else if (headerMouseX >= 129 && headerMouseX < 165) {
            this.headerPressed = 1;
        }
        else if (headerMouseX >= 179 && headerMouseX < 215) {
            this.headerPressed = 2;
        }
        if (this.headerPressed >= 0) {
            this.sortByColumn(this.headerPressed);
            this.ss.minecraft.soundEngine.playUI("random.click", 1.0f, 1.0f);
        }
    }
    
    @Override
    protected final int getNumberOfItems() {
        return this.statItemList.size();
    }
    
    protected final ItemStat getSlotStat(final int slot) {
        return this.statItemList.get(slot);
    }
    
    protected abstract String getHeaderDescriptionId(final int column);
    
    protected void renderStat(final ItemStat stat, final int x, final int y, final boolean shaded) {
        if (stat != null) {
            final String format = stat.format(this.ss.stats.getValue(stat));
            this.ss.drawString(this.ss.font, format, x - this.ss.font.width(format), y + 5, shaded ? 16777215 : 9474192);
        }
        else {
            final String s = "-";
            this.ss.drawString(this.ss.font, s, x - this.ss.font.width(s), y + 5, shaded ? 16777215 : 9474192);
        }
    }
    
    @Override
    protected void renderDecorations(final int mouseX, final int mouseY) {
        if (mouseY < this.y0 || mouseY > this.y1) {
            return;
        }
        final int itemAtPosition = this.getItemAtPosition(mouseX, mouseY);
        final int n = this.ss.width / 2 - 92 - 16;
        if (itemAtPosition >= 0) {
            if (mouseX < n + 40 || mouseX > n + 40 + 20) {
                return;
            }
            this.renderMousehoverTooltip(this.getSlotStat(itemAtPosition), mouseX, mouseY);
        }
        else {
            String elementId;
            if (mouseX >= n + 115 - 18 && mouseX <= n + 115) {
                elementId = this.getHeaderDescriptionId(0);
            }
            else if (mouseX >= n + 165 - 18 && mouseX <= n + 165) {
                elementId = this.getHeaderDescriptionId(1);
            }
            else {
                if (mouseX < n + 215 - 18 || mouseX > n + 215) {
                    return;
                }
                elementId = this.getHeaderDescriptionId(2);
            }
            final String trim = ("" + Language.getInstance().getElement(elementId)).trim();
            if (trim.length() > 0) {
                final int x = mouseX + 12;
                final int y = mouseY - 12;
                this.ss.fillGradient(x - 3, y - 3, x + this.ss.font.width(trim) + 3, y + 8 + 3, -1073741824, -1073741824);
                this.ss.font.drawShadow(trim, x, y, -1);
            }
        }
    }
    
    protected void renderMousehoverTooltip(final ItemStat stat, final int x, final int z) {
        if (stat == null) {
            return;
        }
        final String trim = ("" + Language.getInstance().getElementName(Item.items[stat.getItemId()].getDescriptionId())).trim();
        if (trim.length() > 0) {
            final int x2 = x + 12;
            final int y = z - 12;
            this.ss.fillGradient(x2 - 3, y - 3, x2 + this.ss.font.width(trim) + 3, y + 8 + 3, -1073741824, -1073741824);
            this.ss.font.drawShadow(trim, x2, y, -1);
        }
    }
    
    protected void sortByColumn(final int column) {
        if (column != this.sortColumn) {
            this.sortColumn = column;
            this.sortOrder = -1;
        }
        else if (this.sortOrder == -1) {
            this.sortOrder = 1;
        }
        else {
            this.sortColumn = -1;
            this.sortOrder = 0;
        }
        Collections.sort((List<Object>)this.statItemList, this.itemStatSorter);
    }
}
