// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.client.gamemode.SurvivalMode;
import util.Mth;
import net.minecraft.world.level.LevelSummary;
import java.util.Collections;
import net.minecraft.locale.language.Language;
import java.text.SimpleDateFormat;
import java.util.List;
import java.text.DateFormat;

public class SelectWorldScreen extends Screen
{
    private final DateFormat DATE_FORMAT;
    protected Screen lastScreen;
    protected String title;
    private boolean done;
    private int selectedWorld;
    private List levelList;
    private SelectWorldScreen_WorldSelectionList worldSelectionList;
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
        (this.worldSelectionList = new SelectWorldScreen_WorldSelectionList(this)).init(this.buttons, 4, 5);
        this.postInit();
    }
    
    private void loadLevelList() {
        Collections.sort((List<Comparable>)(this.levelList = this.minecraft.getLevelSource().getLevelList()));
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
        this.buttons.add(this.selectButton = new Button(1, this.width / 2 - 154, this.height - 52, 150, 20, instance.getElement("selectWorld.select")));
        this.buttons.add(this.deleteButton = new Button(6, this.width / 2 - 154, this.height - 28, 70, 20, instance.getElement("selectWorld.rename")));
        this.buttons.add(this.renameButton = new Button(2, this.width / 2 - 74, this.height - 28, 70, 20, instance.getElement("selectWorld.delete")));
        this.buttons.add(new Button(3, this.width / 2 + 4, this.height - 52, 150, 20, instance.getElement("selectWorld.create")));
        this.buttons.add(new Button(0, this.width / 2 + 4, this.height - 28, 150, 20, instance.getElement("gui.cancel")));
        this.selectButton.active = false;
        this.deleteButton.active = false;
        this.renameButton.active = false;
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (!button.active) {
            return;
        }
        if (button.id == 2) {
            final String worldName = this.getWorldName(this.selectedWorld);
            if (worldName != null) {
                this.isDeleting = true;
                final Language instance = Language.getInstance();
                this.minecraft.setScreen(new ConfirmScreen(this, instance.getElement("selectWorld.deleteQuestion"), "'" + worldName + "' " + instance.getElement("selectWorld.deleteWarning"), instance.getElement("selectWorld.deleteButton"), instance.getElement("gui.cancel"), this.selectedWorld));
            }
        }
        else if (button.id == 1) {
            this.worldSelected(this.selectedWorld);
        }
        else if (button.id == 3) {
            this.minecraft.setScreen(new CreateWorldScreen(this));
        }
        else if (button.id == 6) {
            this.minecraft.setScreen(new RenameWorldScreen(this, this.getWorldId(this.selectedWorld)));
        }
        else if (button.id == 0) {
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
        this.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
        super.render(xm, ym, partialTick);
    }
}
