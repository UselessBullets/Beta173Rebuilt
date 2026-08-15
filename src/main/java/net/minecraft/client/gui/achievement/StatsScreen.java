// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui.achievement;

import net.minecraft.client.renderer.Tesselator;
import net.minecraft.stats.ItemStat;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.Item;
import net.minecraft.client.Lighting;
import org.lwjgl.input.Mouse;
import net.minecraft.client.gui.Button;
import net.minecraft.locale.language.Language;
import net.minecraft.locale.language.I18n;
import net.minecraft.client.gui.ScrolledSelectionList;
import net.minecraft.stats.StatsCounter;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.gui.Screen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

public class StatsScreen extends Screen
{
    private static ItemRenderer itemRenderer;
    protected static final int BUTTON_CANCEL_ID = 0;
    protected static final int BUTTON_STATS_ID = 1;
    protected static final int BUTTON_BLOCKITEMSTATS_ID = 2;
    protected static final int BUTTON_ITEMSTATS_ID = 3;
    protected Screen lastScreen;
    protected String title;
    private GeneralStatisticsList statsList;
    private ItemStatisticsList itemStatsList;
    private BlockStatisticsList blockStatsList;
    private final StatsCounter stats;
    private ScrolledSelectionList activeList;
    
    public StatsScreen(final Screen lastScreen, final StatsCounter stats) {
        this.title = "Select world";
        this.activeList = null;
        this.lastScreen = lastScreen;
        this.stats = stats;
    }
    
    @Override
    public void init() {
        this.title = I18n.get("gui.stats");

        this.statsList = new GeneralStatisticsList(this);
        this.statsList.init(this.buttons, 1, 1);

        this.itemStatsList = new ItemStatisticsList(this);
        this.itemStatsList.init(this.buttons, 1, 1);

        this.blockStatsList = new BlockStatisticsList(this);
        this.blockStatsList.init(this.buttons, 1, 1);

        this.activeList = this.statsList;

        this.postInit();
    }
    
    public void postInit() {
        final Language language = Language.getInstance();
        this.buttons.add(new Button(BUTTON_CANCEL_ID, this.width / 2 + 4, this.height - 28, 150, 20, language.getElement("gui.done")));

        final Button blockButton, itemButton;
        this.buttons.add(new Button(BUTTON_STATS_ID, this.width / 2 - 154, this.height - 52, 100, 20, language.getElement("stat.generalButton")));
        this.buttons.add(blockButton = new Button(BUTTON_BLOCKITEMSTATS_ID, this.width / 2 - 46, this.height - 52, 100, 20, language.getElement("stat.blocksButton")));
        this.buttons.add(itemButton = new Button(BUTTON_ITEMSTATS_ID, this.width / 2 + 62, this.height - 52, 100, 20, language.getElement("stat.itemsButton")));

        if (this.blockStatsList.getNumberOfItems() == 0) {
            blockButton.active = false;
        }
        if (this.itemStatsList.getNumberOfItems() == 0) {
            itemButton.active = false;
        }
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (!button.active) return;
        if (button.id == BUTTON_CANCEL_ID) {
            this.minecraft.setScreen(this.lastScreen);
        }
        else if (button.id == BUTTON_STATS_ID) {
            this.activeList = this.statsList;
        }
        else if (button.id == BUTTON_ITEMSTATS_ID) {
            this.activeList = this.itemStatsList;
        }
        else if (button.id == BUTTON_BLOCKITEMSTATS_ID) {
            this.activeList = this.blockStatsList;
        }
        else {
            this.activeList.buttonClicked(button);
        }
    }
    
    @Override
    public void render(final int xm, final int ym, final float partialTick) {
        this.activeList.render(xm, ym, partialTick);
        this.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xffffff);
        super.render(xm, ym, partialTick);
    }
    
    private void blitSlot(final int x, final int y, final int item) {
        this.blitSlotBg(x + 1, y + 1);
        glEnable(GL_RESCALE_NORMAL);
        glPushMatrix();
        glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        Lighting.turnOn();
        glPopMatrix();
        StatsScreen.itemRenderer.renderGuiItem(this.font, this.minecraft.textures, item, 0, Item.items[item].getIcon(0), x + 2, y + 2);
        Lighting.turnOff();
        glDisable(GL_RESCALE_NORMAL);
    }
    
    private void blitSlotBg(final int x, final int y) {
        this.blitSlotIcon(x, y, 0, 0);
    }
    
    private void blitSlotIcon(final int x, final int y, final int sx, final int sy) {
        final int loadTexture = this.minecraft.textures.loadTexture("/gui/slot.png");
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.textures.bind(loadTexture);
        final Tesselator instance = Tesselator.instance;
        instance.begin();
        instance.vertexUV(x, y + 18, this.blitOffset, (sx) * 0.0078125f, (sy + 18) * 0.0078125f);
        instance.vertexUV(x + 18, y + 18, this.blitOffset, (sx + 18) * 0.0078125f, (sy + 18) * 0.0078125f);
        instance.vertexUV(x + 18, y, this.blitOffset, (sx + 18) * 0.0078125f, (sy) * 0.0078125f);
        instance.vertexUV(x, y, this.blitOffset, (sx) * 0.0078125f, (sy) * 0.0078125f);
        instance.end();
    }
    
    static {
        StatsScreen.itemRenderer = new ItemRenderer();
    }

    static class BlockStatisticsList extends StatisticsList
    {
        final /* synthetic */ StatsScreen statsScreen;

        public BlockStatisticsList(final StatsScreen statsScreen) {
            super(statsScreen);
            this.statsScreen = statsScreen;
            this.statItemList = new ArrayList<>();
            for (final ItemStat stat : Stats.itemsCraftedStats) {
                boolean b = false;
                final int itemId = stat.getItemId();
                if (statsScreen.stats.getValue(stat) > 0) {
                    b = true;
                }
                else if (Stats.itemUsed[itemId] != null && statsScreen.stats.getValue(Stats.itemUsed[itemId]) > 0) {
                    b = true;
                }
                else if (Stats.itemCrafted[itemId] != null && statsScreen.stats.getValue(Stats.itemCrafted[itemId]) > 0) {
                    b = true;
                }
                if (b) {
                    this.statItemList.add(stat);
                }
            }
            this.itemStatSorter = (o1, o2) -> {
                final int itemId = o1.getItemId();
                final int itemId2 = o2.getItemId();
                Stat stat = null;
                Stat stat2 = null;
                if (this.sortColumn == 2) {
                    stat = Stats.blockMined[itemId];
                    stat2 = Stats.blockMined[itemId2];
                }
                else if (this.sortColumn == 0) {
                    stat = Stats.itemCrafted[itemId];
                    stat2 = Stats.itemCrafted[itemId2];
                }
                else if (this.sortColumn == 1) {
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
                    final int value = statsScreen.stats.getValue(stat);
                    final int value2 = statsScreen.stats.getValue(stat2);
                    if (value != value2) {
                        return (value - value2) * this.sortOrder;
                    }
                }
                return itemId - itemId2;
            };
        }

        @Override
        protected void renderHeader(final int x, final int y, final Tesselator t) {
            super.renderHeader(x, y, t);
            if (this.headerPressed == 0) {
                this.statsScreen.blitSlotIcon(x + 115 - 18 + 1, y + 1 + 1, 18, 18);
            }
            else {
                this.statsScreen.blitSlotIcon(x + 115 - 18, y + 1, 18, 18);
            }
            if (this.headerPressed == 1) {
                this.statsScreen.blitSlotIcon(x + 165 - 18 + 1, y + 1 + 1, 36, 18);
            }
            else {
                this.statsScreen.blitSlotIcon(x + 165 - 18, y + 1, 36, 18);
            }
            if (this.headerPressed == 2) {
                this.statsScreen.blitSlotIcon(x + 215 - 18 + 1, y + 1 + 1, 54, 18);
            }
            else {
                this.statsScreen.blitSlotIcon(x + 215 - 18, y + 1, 54, 18);
            }
        }

        @Override
        protected void renderItem(final int i, final int x, final int y, final int h, final Tesselator t) {
            final ItemStat slotStat = this.getSlotStat(i);
            final int itemId = slotStat.getItemId();
            this.statsScreen.blitSlot(x + 40, y, itemId);
            this.renderStat((ItemStat)Stats.itemCrafted[itemId], x + 115, y, i % 2 == 0);
            this.renderStat((ItemStat)Stats.itemUsed[itemId], x + 165, y, i % 2 == 0);
            this.renderStat(slotStat, x + 215, y, i % 2 == 0);
        }

        @Override
        protected String getHeaderDescriptionId(final int column) {
            if (column == 0) {
                return "stat.crafted";
            }
            if (column == 1) {
                return "stat.used";
            }
            return "stat.mined";
        }
    }

    static class GeneralStatisticsList extends ScrolledSelectionList
    {
        final /* synthetic */ StatsScreen statsScreen;

        public GeneralStatisticsList(final StatsScreen statsScreen) {
            super(statsScreen.minecraft, statsScreen.width, statsScreen.height, 32, statsScreen.height - 64, 10);
            this.statsScreen = statsScreen;
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
            this.statsScreen.drawString(this.statsScreen.font, stat.name, x + 2, y + 1, (i % 2 == 0) ? 0xffffff : 0x909090);
            final String format = stat.format(this.statsScreen.stats.getValue(stat));
            this.statsScreen.drawString(this.statsScreen.font, format, x + 2 + 213 - this.statsScreen.font.width(format), y + 1, (i % 2 == 0) ? 0xffffff : 0x909090);
        }
    }

    static class ItemStatisticsList extends StatisticsList
    {
        final /* synthetic */ StatsScreen statsScreen;

        public ItemStatisticsList(final StatsScreen statsScreen) {
            super(statsScreen);
            this.statsScreen = statsScreen;
            this.statItemList = new ArrayList<>();
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
            this.itemStatSorter = new Comparator<ItemStat>() {
                public int compare(final ItemStat o1, final ItemStat o2) {
                    final int itemId = o1.getItemId();
                    final int itemId2 = o2.getItemId();
                    Stat stat = null;
                    Stat stat2 = null;
                    if (ItemStatisticsList.this.sortColumn == 0) {
                        stat = Stats.itemBroke[itemId];
                        stat2 = Stats.itemBroke[itemId2];
                    }
                    else if (ItemStatisticsList.this.sortColumn == 1) {
                        stat = Stats.itemCrafted[itemId];
                        stat2 = Stats.itemCrafted[itemId2];
                    }
                    else if (ItemStatisticsList.this.sortColumn == 2) {
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
                        final int value = statsScreen.stats.getValue(stat);
                        final int value2 = statsScreen.stats.getValue(stat2);
                        if (value != value2) {
                            return (value - value2) * ItemStatisticsList.this.sortOrder;
                        }
                    }
                    return itemId - itemId2;
                }
            };
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

    abstract static class StatisticsList extends ScrolledSelectionList
    {
        protected int headerPressed;
        protected List<ItemStat> statItemList;
        protected Comparator<ItemStat> itemStatSorter;
        protected int sortColumn;
        protected int sortOrder;
        final /* synthetic */ StatsScreen ss;

        protected StatisticsList(final StatsScreen statsScreen) {
            super(statsScreen.minecraft, statsScreen.width, statsScreen.height, 32, statsScreen.height - 64, 20);
            this.ss = statsScreen;
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
                this.ss.drawString(this.ss.font, format, x - this.ss.font.width(format), y + 5, shaded ? 0xffffff : 0x909090);
            }
            else {
                final String s = "-";
                this.ss.drawString(this.ss.font, s, x - this.ss.font.width(s), y + 5, shaded ? 0xffffff : 0x909090);
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
                    this.ss.fillGradient(x - 3, y - 3, x + this.ss.font.width(trim) + 3, y + 8 + 3, 0xc0000000, 0xc0000000);
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
                this.ss.fillGradient(x2 - 3, y - 3, x2 + this.ss.font.width(trim) + 3, y + 8 + 3, 0xc0000000, 0xc0000000);
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
            Collections.sort(this.statItemList, this.itemStatSorter);
        }
    }
}
