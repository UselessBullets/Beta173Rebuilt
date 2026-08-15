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
    private static ItemRenderer itemRenderer = new ItemRenderer();
    protected static final int BUTTON_CANCEL_ID = 0;
    protected static final int BUTTON_STATS_ID = 1;
    protected static final int BUTTON_BLOCKITEMSTATS_ID = 2;
    protected static final int BUTTON_ITEMSTATS_ID = 3;

    private static final float SLOT_TEX_SIZE = 128.0f;
    private static final int SLOT_BG_SIZE = 18;
    private static final int SLOT_STAT_HEIGHT = SLOT_BG_SIZE + 2;
    private static final int SLOT_BG_X = 1;
    private static final int SLOT_BG_Y = 1;
    private static final int SLOT_FG_X = 2;
    private static final int SLOT_FG_Y = 2;
    private static final int SLOT_LEFT_INSERT = 40;
    private static final int ROW_COL_1 = 2 + 113;
    private static final int ROW_COL_2 = 2 + 163;
    private static final int ROW_COL_3 = 2 + 213;
    private static final int SLOT_TEXT_OFFSET = 5;
    private static final int SORT_NONE = 0;
    private static final int SORT_DOWN = -1;
    private static final int SORT_UP = 1;
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

        this.itemStatsList = new ItemStatisticsList();
        this.itemStatsList.init(this.buttons, 1, 1);

        this.blockStatsList = new BlockStatisticsList();
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
        this.blitSlotBg(x + SLOT_BG_X, y + SLOT_BG_Y);

        glEnable(GL_RESCALE_NORMAL);

        glPushMatrix();
        glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        Lighting.turnOn();
        glPopMatrix();

        StatsScreen.itemRenderer.renderGuiItem(this.font, this.minecraft.textures, item, 0, Item.items[item].getIcon(0), x + SLOT_FG_X, y + SLOT_FG_Y);
        Lighting.turnOff();

        glDisable(GL_RESCALE_NORMAL);
    }
    
    private void blitSlotBg(final int x, final int y) {
        this.blitSlotIcon(x, y, 0, 0);
    }
    
    private void blitSlotIcon(final int x, final int y, final int sx, final int sy) {
        final int tex = this.minecraft.textures.loadTexture("/gui/slot.png");
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.textures.bind(tex);

        final float us = 1 / SLOT_TEX_SIZE;
        final float vs = 1 / SLOT_TEX_SIZE;
        final int w = SLOT_BG_SIZE;
        final int h = SLOT_BG_SIZE;
        final Tesselator t = Tesselator.instance;
        t.begin();
        t.vertexUV(x + 0, y + h, this.blitOffset, (sx + 0) * us, (sy + h) * vs);
        t.vertexUV(x + w, y + h, this.blitOffset, (sx + w) * us, (sy + h) * vs);
        t.vertexUV(x + w, y + 0, this.blitOffset, (sx + w) * us, (sy + 0) * vs);
        t.vertexUV(x + 0, y + 0, this.blitOffset, (sx + 0) * us, (sy + 0) * vs);
        t.end();
    }

    class GeneralStatisticsList extends ScrolledSelectionList
    {
        public GeneralStatisticsList(final StatsScreen ss) {
            super(ss.minecraft, ss.width, ss.height, 32, ss.height - 64, 10);
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
            StatsScreen.this.renderBackground();
        }

        @Override
        protected void renderItem(final int i, final int x, final int y, final int h, final Tesselator t) {
            final Stat stat = Stats.generalStats.get(i);
            StatsScreen.this.drawString(StatsScreen.this.font, stat.name, x + 2, y + 1, (i % 2 == 0) ? 0xffffff : 0x909090);
            final String msg = stat.format(StatsScreen.this.stats.getValue(stat));
            StatsScreen.this.drawString(StatsScreen.this.font, msg, x + 2 + 213 - StatsScreen.this.font.width(msg), y + 1, (i % 2 == 0) ? 0xffffff : 0x909090);
        }
    }

    abstract class StatisticsList extends ScrolledSelectionList
    {
        protected int headerPressed;
        protected List<ItemStat> statItemList;
        protected Comparator<ItemStat> itemStatSorter;
        protected int sortColumn;
        protected int sortOrder;

        protected StatisticsList() {
            super(StatsScreen.this.minecraft, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 20);

            this.headerPressed = -1;
            this.sortColumn = -1;
            this.sortOrder = SORT_NONE;

            this.setRenderSelection(false);
            this.setRenderHeader(true, SLOT_STAT_HEIGHT);
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
            StatsScreen.this.renderBackground();
        }

        @Override
        protected void renderHeader(final int x, final int y, final Tesselator t) {
            if (!Mouse.isButtonDown(0)) {
                this.headerPressed = -1;
            }

            if (this.headerPressed == 0) {
                StatsScreen.this.blitSlotIcon(x + ROW_COL_1 - SLOT_BG_SIZE, y + SLOT_BG_Y, SLOT_BG_SIZE * 0, SLOT_BG_SIZE * 0);
            }
            else {
                StatsScreen.this.blitSlotIcon(x + ROW_COL_1 - SLOT_BG_SIZE, y + SLOT_BG_Y, SLOT_BG_SIZE * 0, SLOT_BG_SIZE * 1);
            }
            if (this.headerPressed == 1) {
                StatsScreen.this.blitSlotIcon(x + ROW_COL_2 - SLOT_BG_SIZE, y + SLOT_BG_Y, SLOT_BG_SIZE * 0, SLOT_BG_SIZE * 0);
            }
            else {
                StatsScreen.this.blitSlotIcon(x + ROW_COL_2 - SLOT_BG_SIZE, y + SLOT_BG_Y, SLOT_BG_SIZE * 0, SLOT_BG_SIZE * 1);
            }
            if (this.headerPressed == 2) {
                StatsScreen.this.blitSlotIcon(x + ROW_COL_3 - SLOT_BG_SIZE, y + SLOT_BG_Y, SLOT_BG_SIZE * 0, SLOT_BG_SIZE * 0);
            }
            else {
                StatsScreen.this.blitSlotIcon(x + ROW_COL_3 - SLOT_BG_SIZE, y + SLOT_BG_Y, SLOT_BG_SIZE * 0, SLOT_BG_SIZE * 1);
            }

            if (this.sortColumn != -1) {
                int offset = ROW_COL_1 - SLOT_BG_SIZE * 2;
                int image = SLOT_BG_SIZE;

                if (this.sortColumn == 1) {
                    offset = ROW_COL_2 - SLOT_BG_SIZE * 2;
                }
                else if (this.sortColumn == 2) {
                    offset = ROW_COL_3 - SLOT_BG_SIZE * 2;
                }

                if (this.sortOrder == SORT_UP) {
                    image = SLOT_BG_SIZE * 2;
                }
                StatsScreen.this.blitSlotIcon(x + offset, y + SLOT_BG_Y, image, SLOT_BG_SIZE * 0);
            }
        }

        @Override
        protected void clickedHeader(final int headerMouseX, final int headerMouseY) {
            this.headerPressed = -1;
            if (headerMouseX >= (ROW_COL_1 - SLOT_BG_SIZE * 2) && headerMouseX < ROW_COL_1) {
                this.headerPressed = 0;
            }
            else if (headerMouseX >= (ROW_COL_2 - SLOT_BG_SIZE * 2) && headerMouseX < ROW_COL_2) {
                this.headerPressed = 1;
            }
            else if (headerMouseX >= (ROW_COL_3 - SLOT_BG_SIZE * 2) && headerMouseX < ROW_COL_3) {
                this.headerPressed = 2;
            }

            if (this.headerPressed >= 0) {
                this.sortByColumn(this.headerPressed);
                StatsScreen.this.minecraft.soundEngine.playUI("random.click", 1.0f, 1.0f);
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
                final String msg = stat.format(StatsScreen.this.stats.getValue(stat));
                StatsScreen.this.drawString(StatsScreen.this.font, msg, x - StatsScreen.this.font.width(msg), y + SLOT_TEXT_OFFSET, shaded ? 0xffffff : 0x909090);
            }
            else {
                final String msg = "-";
                StatsScreen.this.drawString(StatsScreen.this.font, msg, x - StatsScreen.this.font.width(msg), y + SLOT_TEXT_OFFSET, shaded ? 0xffffff : 0x909090);
            }
        }

        @Override
        protected void renderDecorations(final int mouseX, final int mouseY) {
            if (mouseY < this.y0 || mouseY > this.y1) {
                return;
            }

            final int slot = this.getItemAtPosition(mouseX, mouseY);
            final int rowX = StatsScreen.this.width / 2 - 92 - 16;
            if (slot >= 0) {
                if (mouseX < rowX + SLOT_LEFT_INSERT || mouseX > rowX + SLOT_LEFT_INSERT + 20) {
                    return;
                }
                this.renderMousehoverTooltip(this.getSlotStat(slot), mouseX, mouseY);
            }
            else {
                String elementId;
                if (mouseX >= rowX + ROW_COL_1 - SLOT_BG_SIZE && mouseX <= rowX + ROW_COL_1) {
                    elementId = this.getHeaderDescriptionId(0);
                }
                else if (mouseX >= rowX + ROW_COL_2 - SLOT_BG_SIZE && mouseX <= rowX + ROW_COL_2) {
                    elementId = this.getHeaderDescriptionId(1);
                }
                else if (mouseX < rowX + ROW_COL_3 - SLOT_BG_SIZE || mouseX > rowX + ROW_COL_3) {
                    elementId = this.getHeaderDescriptionId(2);
                } else {
                    return;
                }

                final String elementName = (Language.getInstance().getElement(elementId)).trim();

                if (elementName.length() > 0) {
                    final int rx = mouseX + 12;
                    final int ry = mouseY - 12;
                    final int width = StatsScreen.this.font.width(elementName);
                    StatsScreen.this.fillGradient(rx - 3, ry - 3, rx + width + 3, ry + 8 + 3, 0xc0000000, 0xc0000000);

                    StatsScreen.this.font.drawShadow(elementName, rx, ry, 0xffffffff);
                }
            }
        }

        protected void renderMousehoverTooltip(final ItemStat stat, final int x, final int y) {
            if (stat == null) {
                return;
            }

            Item item = Item.items[stat.getItemId()];
            final String elementName = (Language.getInstance().getElementName(item.getDescriptionId())).trim();

            if (elementName.length() > 0) {
                final int rx = x + 12;
                final int ry = y - 12;
                final int width = StatsScreen.this.font.width(elementName);
                StatsScreen.this.fillGradient(rx - 3, ry - 3, rx + width + 3, ry + 8 + 3, 0xc0000000, 0xc0000000);

                StatsScreen.this.font.drawShadow(elementName, rx, ry, -1);
            }
        }

        protected void sortByColumn(final int column) {
            if (column != this.sortColumn) {
                this.sortColumn = column;
                this.sortOrder = SORT_DOWN;
            }
            else if (this.sortOrder == SORT_DOWN) {
                this.sortOrder = SORT_UP;
            }
            else {
                this.sortColumn = -1;
                this.sortOrder = SORT_NONE;
            }

            Collections.sort(this.statItemList, this.itemStatSorter);
        }
    }

    class ItemStatisticsList extends StatisticsList
    {
        private static final int COLUMN_DEPLETED = 0;
        private static final int COLUMN_CRAFTED = 1;
        private static final int COLUMN_USED = 2;
        public ItemStatisticsList() {
            this.statItemList = new ArrayList<>();
            for (final ItemStat stat : Stats.itemStats) {
                boolean addToList = false;
                final int id = stat.getItemId();

                if (StatsScreen.this.stats.getValue(stat) > 0) {
                    addToList = true;
                }
                else if (Stats.itemBroke[id] != null && StatsScreen.this.stats.getValue(Stats.itemBroke[id]) > 0) {
                    addToList = true;
                }
                else if (Stats.itemCrafted[id] != null && StatsScreen.this.stats.getValue(Stats.itemCrafted[id]) > 0) {
                    addToList = true;
                }

                if (addToList) {
                    this.statItemList.add(stat);
                }
            }

            this.itemStatSorter = (o1, o2) -> {
                final int id1 = o1.getItemId();
                final int id2 = o2.getItemId();

                Stat stat1 = null;
                Stat stat2 = null;
                if (ItemStatisticsList.this.sortColumn == COLUMN_DEPLETED) {
                    stat1 = Stats.itemBroke[id1];
                    stat2 = Stats.itemBroke[id2];
                }
                else if (ItemStatisticsList.this.sortColumn == COLUMN_CRAFTED) {
                    stat1 = Stats.itemCrafted[id1];
                    stat2 = Stats.itemCrafted[id2];
                }
                else if (ItemStatisticsList.this.sortColumn == COLUMN_USED) {
                    stat1 = Stats.itemUsed[id1];
                    stat2 = Stats.itemUsed[id2];
                }

                if (stat1 != null || stat2 != null) {
                    if (stat1 == null) {
                        return 1;
                    } else if (stat2 == null) {
                        return -1;
                    }
                    final int value1 = StatsScreen.this.stats.getValue(stat1);
                    final int value2 = StatsScreen.this.stats.getValue(stat2);
                    if (value1 != value2) {
                        return (value1 - value2) * this.sortOrder;
                    }
                }

                return id1 - id2;
            };
        }

        @Override
        protected void renderHeader(final int x, final int y, final Tesselator t) {
            super.renderHeader(x, y, t);
            if (this.headerPressed == 0) {
                StatsScreen.this.blitSlotIcon(x + ROW_COL_1 - SLOT_BG_SIZE + 1, y + SLOT_BG_Y + 1, SLOT_BG_SIZE * 4, SLOT_BG_SIZE * 1);
            }
            else {
                StatsScreen.this.blitSlotIcon(x + ROW_COL_1 - SLOT_BG_SIZE, y + SLOT_BG_Y, SLOT_BG_SIZE * 4, SLOT_BG_SIZE * 1);
            }
            if (this.headerPressed == 1) {
                StatsScreen.this.blitSlotIcon(x + ROW_COL_2 - SLOT_BG_SIZE + 1, y + SLOT_BG_Y + 1, SLOT_BG_SIZE * 1, SLOT_BG_SIZE * 1);
            }
            else {
                StatsScreen.this.blitSlotIcon(x + ROW_COL_2 - SLOT_BG_SIZE, y + SLOT_BG_Y, SLOT_BG_SIZE * 1, SLOT_BG_SIZE * 1);
            }
            if (this.headerPressed == 2) {
                StatsScreen.this.blitSlotIcon(x + ROW_COL_3 - SLOT_BG_SIZE + 1, y + SLOT_BG_Y + 1, SLOT_BG_SIZE * 2, SLOT_BG_SIZE * 1);
            }
            else {
                StatsScreen.this.blitSlotIcon(x + ROW_COL_3 - SLOT_BG_SIZE, y + SLOT_BG_Y, SLOT_BG_SIZE * 2, SLOT_BG_SIZE * 1);
            }
        }

        @Override
        protected void renderItem(final int i, final int x, final int y, final int h, final Tesselator t) {
            final ItemStat stat = this.getSlotStat(i);
            final int id = stat.getItemId();

            StatsScreen.this.blitSlot(x + SLOT_LEFT_INSERT, y, id);

            this.renderStat((ItemStat)Stats.itemBroke[id], x + ROW_COL_1, y, i % 2 == 0);
            this.renderStat((ItemStat)Stats.itemCrafted[id], x + ROW_COL_2, y, i % 2 == 0);
            this.renderStat(stat, x + ROW_COL_3, y, i % 2 == 0);
        }

        @Override
        protected String getHeaderDescriptionId(final int column) {
            if (column == COLUMN_CRAFTED) {
                return "stat.crafted";
            }
            if (column == COLUMN_USED) {
                return "stat.used";
            }
            return "stat.depleted";
        }
    }

    class BlockStatisticsList extends StatisticsList
    {
        private static final int COLUMN_CRAFTED = 0;
        private static final int COLUMN_USED = 1;
        private static final int COLUMN_MINED = 2;
        public BlockStatisticsList() {
            this.statItemList = new ArrayList<>();
            for (final ItemStat stat : Stats.blocksStats) {
                boolean addToList = false;
                final int id = stat.getItemId();

                if (StatsScreen.this.stats.getValue(stat) > 0) {
                    addToList = true;
                }
                else if (Stats.itemUsed[id] != null && StatsScreen.this.stats.getValue(Stats.itemUsed[id]) > 0) {
                    addToList = true;
                }
                else if (Stats.itemCrafted[id] != null && StatsScreen.this.stats.getValue(Stats.itemCrafted[id]) > 0) {
                    addToList = true;
                }

                if (addToList) {
                    this.statItemList.add(stat);
                }
            }
            this.itemStatSorter = (o1, o2) -> {
                final int id1 = o1.getItemId();
                final int id2 = o2.getItemId();

                Stat stat1 = null;
                Stat stat2 = null;
                if (this.sortColumn == COLUMN_MINED) {
                    stat1 = Stats.blockMined[id1];
                    stat2 = Stats.blockMined[id2];
                }
                else if (this.sortColumn == COLUMN_CRAFTED) {
                    stat1 = Stats.itemCrafted[id1];
                    stat2 = Stats.itemCrafted[id2];
                }
                else if (this.sortColumn == COLUMN_USED) {
                    stat1 = Stats.itemUsed[id1];
                    stat2 = Stats.itemUsed[id2];
                }

                if (stat1 != null || stat2 != null) {
                    if (stat1 == null) {
                        return 1;
                    }
                    if (stat2 == null) {
                        return -1;
                    }
                    final int value1 = StatsScreen.this.stats.getValue(stat1);
                    final int value2 = StatsScreen.this.stats.getValue(stat2);
                    if (value1 != value2) {
                        return (value1 - value2) * this.sortOrder;
                    }
                }

                return id1 - id2;
            };
        }

        @Override
        protected void renderHeader(final int x, final int y, final Tesselator t) {
            super.renderHeader(x, y, t);
            if (this.headerPressed == 0) {
                StatsScreen.this.blitSlotIcon(x + ROW_COL_1 - SLOT_BG_SIZE + 1, y + SLOT_BG_Y + 1, SLOT_BG_SIZE * 1, SLOT_BG_SIZE * 1);
            }
            else {
                StatsScreen.this.blitSlotIcon(x + ROW_COL_1 - SLOT_BG_SIZE, y + SLOT_BG_Y, SLOT_BG_SIZE * 1, SLOT_BG_SIZE * 1);
            }
            if (this.headerPressed == 1) {
                StatsScreen.this.blitSlotIcon(x + ROW_COL_2 - SLOT_BG_SIZE + 1, y + SLOT_BG_Y + 1, SLOT_BG_SIZE * 2, SLOT_BG_SIZE * 1);
            }
            else {
                StatsScreen.this.blitSlotIcon(x + ROW_COL_2 - SLOT_BG_SIZE, y + SLOT_BG_Y, SLOT_BG_SIZE * 2, SLOT_BG_SIZE * 1);
            }
            if (this.headerPressed == 2) {
                StatsScreen.this.blitSlotIcon(x + ROW_COL_3 - SLOT_BG_SIZE + 1, y + SLOT_BG_Y + 1, SLOT_BG_SIZE * 3, SLOT_BG_SIZE * 1);
            }
            else {
                StatsScreen.this.blitSlotIcon(x + ROW_COL_3 - SLOT_BG_SIZE, y + SLOT_BG_Y, SLOT_BG_SIZE * 3, SLOT_BG_SIZE * 1);
            }
        }

        @Override
        protected void renderItem(final int i, final int x, final int y, final int h, final Tesselator t) {
            final ItemStat mineCount = this.getSlotStat(i);
            final int id = mineCount.getItemId();

            StatsScreen.this.blitSlot(x + 40, y, id);

            this.renderStat((ItemStat)Stats.itemCrafted[id], x + ROW_COL_1, y, i % 2 == 0);
            this.renderStat((ItemStat)Stats.itemUsed[id], x + ROW_COL_2, y, i % 2 == 0);
            this.renderStat(mineCount, x + ROW_COL_3, y, i % 2 == 0);
        }

        @Override
        protected String getHeaderDescriptionId(final int column) {
            if (column == COLUMN_CRAFTED) {
                return "stat.crafted";
            }
            if (column == COLUMN_USED) {
                return "stat.used";
            }
            return "stat.mined";
        }
    }
}
