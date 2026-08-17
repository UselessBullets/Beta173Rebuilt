// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.client.gamemode.SurvivalMode;
import util.Mth;
import net.minecraft.world.level.LevelSummary;
import java.util.Collections;
import net.minecraft.locale.language.Language;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.text.DateFormat;

public class SelectWorldScreen extends Screen
{
    protected static final int BUTTON_CANCEL_ID = 0;
    protected static final int BUTTON_SELECT_ID = 1;
    protected static final int BUTTON_DELETE_ID = 2;
    protected static final int BUTTON_CREATE_ID = 3;
    protected static final int BUTTON_UP_ID = 4;
    protected static final int BUTTON_DOWN_ID = 5;
    protected static final int BUTTON_RENAME_ID = 6;

    private final DateFormat DATE_FORMAT = new SimpleDateFormat();
    protected Screen lastScreen;
    protected String title = "Select world";
    private boolean done = false;
    private int selectedWorld;
    private List<LevelSummary> levelList;
    private WorldSelectionList worldSelectionList;
    private String worldLang;
    private String conversionLang;
    private boolean isDeleting;
    private Button deleteButton;
    private Button selectButton;
    private Button renameButton;
    
    public SelectWorldScreen(final Screen lastScreen) {
        this.lastScreen = lastScreen;
    }
    
    @Override
    public void init() {
        final Language language = Language.getInstance();
        this.title = language.getElement("selectWorld.title");

        this.worldLang = language.getElement("selectWorld.world");
        this.conversionLang = language.getElement("selectWorld.conversion");
        this.loadLevelList();

        this.worldSelectionList = new WorldSelectionList();
        this.worldSelectionList.init(this.buttons, 4, 5);

        this.postInit();
    }
    
    private void loadLevelList() {
        LevelStorageSource levelSource = this.minecraft.getLevelSource();
        this.levelList = levelSource.getLevelList();
        Collections.sort(this.levelList);
        this.selectedWorld = -1;
    }
    
    protected String getWorldId(final int id) {
        return this.levelList.get(id).getLevelId();
    }
    
    protected String getWorldName(final int id) {
        String levelName = this.levelList.get(id).getLevelName();
        if (Mth.isNullOrEmpty(levelName)) {
            levelName = Language.getInstance().getElement("selectWorld.world") + " " + (id + 1);
        }
        return levelName;
    }
    
    public void postInit() {
        final Language language = Language.getInstance();

        this.buttons.add(this.selectButton = new Button(BUTTON_SELECT_ID, this.width / 2 - 154, this.height - 52, 150, 20, language.getElement("selectWorld.select")));
        this.buttons.add(this.deleteButton = new Button(BUTTON_RENAME_ID, this.width / 2 - 154, this.height - 28, 70, 20, language.getElement("selectWorld.rename")));
        this.buttons.add(this.renameButton = new Button(BUTTON_DELETE_ID, this.width / 2 - 74, this.height - 28, 70, 20, language.getElement("selectWorld.delete")));
        this.buttons.add(new Button(BUTTON_CREATE_ID, this.width / 2 + 4, this.height - 52, 150, 20, language.getElement("selectWorld.create")));
        this.buttons.add(new Button(BUTTON_CANCEL_ID, this.width / 2 + 4, this.height - 28, 150, 20, language.getElement("gui.cancel")));

        this.selectButton.active = false;
        this.deleteButton.active = false;
        this.renameButton.active = false;
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (!button.active) return;
        if (button.id == BUTTON_DELETE_ID) {
            final String worldName = this.getWorldName(this.selectedWorld);
            if (worldName != null) {
                this.isDeleting = true;

                final Language instance = Language.getInstance();
                final String title = instance.getElement("selectWorld.deleteQuestion");
                final String warning = "'" + worldName + "' " + instance.getElement("selectWorld.deleteWarning");
                final String yes = instance.getElement("selectWorld.deleteButton");
                final String no = instance.getElement("gui.cancel");

                ConfirmScreen confirmScreen = new ConfirmScreen(this, title, warning, yes, no, this.selectedWorld);
                this.minecraft.setScreen(confirmScreen);
            }
        }
        else if (button.id == BUTTON_SELECT_ID) {
            this.worldSelected(this.selectedWorld);
        }
        else if (button.id == BUTTON_CREATE_ID) {
            this.minecraft.setScreen(new CreateWorldScreen(this));
        }
        else if (button.id == BUTTON_RENAME_ID) {
            this.minecraft.setScreen(new RenameWorldScreen(this, this.getWorldId(this.selectedWorld)));
        }
        else if (button.id == BUTTON_CANCEL_ID) {
            this.minecraft.setScreen(this.lastScreen);
        }
        else {
            this.worldSelectionList.buttonClicked(button);
        }
    }
    
    public void worldSelected(final int id) {
        this.minecraft.setScreen(null);
        if (this.done) return;
        this.done = true;
        this.minecraft.gameMode = new SurvivalMode(this.minecraft);

        String worldFolderName = this.getWorldId(id);
        if (worldFolderName == null) {
            worldFolderName = "World" + id;
        }

        this.minecraft.selectLevel(worldFolderName, this.getWorldName(id), 0L);
        this.minecraft.setScreen(null);
    }
    
    @Override
    public void confirmResult(final boolean result, final int id) {
        if (this.isDeleting) {
            this.isDeleting = false;
            if (result) {
                final LevelStorageSource levelSource = this.minecraft.getLevelSource();
                levelSource.clearAll();
                levelSource.deleteLevel(this.getWorldId(id));

                this.loadLevelList();
            }
            this.minecraft.setScreen(this);
        }
    }
    
    @Override
    public void render(final int xm, final int ym, final float partialTick) {
        this.worldSelectionList.render(xm, ym, partialTick);

        this.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xffffff);

        super.render(xm, ym, partialTick);
    }

    class WorldSelectionList extends ScrolledSelectionList
    {
        public WorldSelectionList() {
            super(SelectWorldScreen.this.minecraft, SelectWorldScreen.this.width, SelectWorldScreen.this.height, 32, SelectWorldScreen.this.height - 64, 36);
        }

        @Override
        protected int getNumberOfItems() {
            return SelectWorldScreen.this.levelList.size();
        }

        @Override
        protected void selectItem(final int item, final boolean doubleClick) {
            SelectWorldScreen.this.selectedWorld = item;
            final boolean active = SelectWorldScreen.this.selectedWorld >= 0 && SelectWorldScreen.this.selectedWorld < this.getNumberOfItems();
            SelectWorldScreen.this.selectButton.active = active;
            SelectWorldScreen.this.deleteButton.active = active;
            SelectWorldScreen.this.renameButton.active = active;

            if (doubleClick && active) {
                SelectWorldScreen.this.worldSelected(item);
            }
        }

        @Override
        protected boolean isSelectedItem(final int item) {
            return item == SelectWorldScreen.this.selectedWorld;
        }

        @Override
        protected int getMaxPosition() {
            return SelectWorldScreen.this.levelList.size() * 36;
        }

        @Override
        protected void renderBackground() {
            SelectWorldScreen.this.renderBackground();
        }

        @Override
        protected void renderItem(final int i, final int x, final int y, final int h, final Tesselator t) {
            final LevelSummary levelSummary = SelectWorldScreen.this.levelList.get(i);

            String name = levelSummary.getLevelName();
            if (Mth.isNullOrEmpty(name)) {
                name = SelectWorldScreen.this.worldLang + " " + (i + 1);
            }

            String id = levelSummary.getLevelId();
            String dateString = SelectWorldScreen.this.DATE_FORMAT.format(new Date(levelSummary.getLastPlayed()));
            id = id + " (" + dateString + ", " + levelSummary.getSizeOnDisk() / 1024L * 100L / 1024L / 100.0f + " MB)";

            String info = "";
            if (levelSummary.isRequiresConversion()) {
                info = SelectWorldScreen.this.conversionLang + " " + info;
            }

            SelectWorldScreen.this.drawString(SelectWorldScreen.this.font, name, x + 2, y + 1, 0xffffff);
            SelectWorldScreen.this.drawString(SelectWorldScreen.this.font, id, x + 2, y + 12, 0x808080);
            SelectWorldScreen.this.drawString(SelectWorldScreen.this.font, info, x + 2, y + 12 + 10, 0x808080);
        }
    }
}
