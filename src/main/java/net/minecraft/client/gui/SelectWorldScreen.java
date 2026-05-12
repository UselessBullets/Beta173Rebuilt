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

    private final DateFormat DATE_FORMAT;
    protected Screen lastScreen;
    protected String title;
    private boolean done;
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
        this.DATE_FORMAT = new SimpleDateFormat();
        this.title = "Select world";
        this.done = false;
        this.lastScreen = lastScreen;
    }
    
    @Override
    public void init() {
        final Language instance = Language.getInstance();
        this.title = instance.getElement("selectWorld.title");
        this.worldLang = instance.getElement("selectWorld.world");
        this.conversionLang = instance.getElement("selectWorld.conversion");
        this.loadLevelList();
        (this.worldSelectionList = new WorldSelectionList(this)).init(this.buttons, 4, 5);
        this.postInit();
    }
    
    private void loadLevelList() {
        Collections.sort((this.levelList = this.minecraft.getLevelSource().getLevelList()));
        this.selectedWorld = -1;
    }
    
    protected String getWorldId(final int id) {
        return this.levelList.get(id).getLevelId();
    }
    
    protected String getWorldName(final int id) {
        String str = this.levelList.get(id).getLevelName();
        if (str == null || Mth.isNullOrEmpty(str)) {
            str = Language.getInstance().getElement("selectWorld.world") + " " + (id + 1);
        }
        return str;
    }
    
    public void postInit() {
        final Language instance = Language.getInstance();
        this.buttons.add(this.selectButton = new Button(BUTTON_SELECT_ID, this.width / 2 - 154, this.height - 52, 150, 20, instance.getElement("selectWorld.select")));
        this.buttons.add(this.deleteButton = new Button(BUTTON_RENAME_ID, this.width / 2 - 154, this.height - 28, 70, 20, instance.getElement("selectWorld.rename")));
        this.buttons.add(this.renameButton = new Button(BUTTON_DELETE_ID, this.width / 2 - 74, this.height - 28, 70, 20, instance.getElement("selectWorld.delete")));
        this.buttons.add(new Button(BUTTON_CREATE_ID, this.width / 2 + 4, this.height - 52, 150, 20, instance.getElement("selectWorld.create")));
        this.buttons.add(new Button(BUTTON_CANCEL_ID, this.width / 2 + 4, this.height - 28, 150, 20, instance.getElement("gui.cancel")));
        this.selectButton.active = false;
        this.deleteButton.active = false;
        this.renameButton.active = false;
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (!button.active) {
            return;
        }
        if (button.id == BUTTON_DELETE_ID) {
            final String worldName = this.getWorldName(this.selectedWorld);
            if (worldName != null) {
                this.isDeleting = true;
                final Language instance = Language.getInstance();
                this.minecraft.setScreen(new ConfirmScreen(this, instance.getElement("selectWorld.deleteQuestion"), "'" + worldName + "' " + instance.getElement("selectWorld.deleteWarning"), instance.getElement("selectWorld.deleteButton"), instance.getElement("gui.cancel"), this.selectedWorld));
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
        if (this.done) {
            return;
        }
        this.done = true;
        this.minecraft.gameMode = new SurvivalMode(this.minecraft);
        String levelId = this.getWorldId(id);
        if (levelId == null) {
            levelId = "World" + id;
        }
        this.minecraft.selectLevel(levelId, this.getWorldName(id), 0L);
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

    static class WorldSelectionList extends ScrolledSelectionList
    {
        final /* synthetic */ SelectWorldScreen sws;

        public WorldSelectionList(final SelectWorldScreen sws) {
            super(sws.minecraft, sws.width, sws.height, 32, sws.height - 64, 36);
            this.sws = sws;
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
            this.sws.drawString(this.sws.font, s, x + 2, y + 1, 0xffffff);
            this.sws.drawString(this.sws.font, string, x + 2, y + 12, 0x808080);
            this.sws.drawString(this.sws.font, string2, x + 2, y + 12 + 10, 0x808080);
        }
    }
}
