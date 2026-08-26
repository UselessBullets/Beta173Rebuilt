// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.client.gamemode.SurvivalMode;
import java.util.Random;
import net.minecraft.world.level.storage.LevelStorageSource;
import util.Mth;
import net.minecraft.SharedConstants;
import org.lwjgl.input.Keyboard;
import net.minecraft.locale.language.Language;

public class CreateWorldScreen extends Screen
{
    private Screen lastScreen;
    private EditBox nameEdit;
    private EditBox seedEdit;
    private String resultFolder;
    private boolean done;
    
    public CreateWorldScreen(final Screen lastScreen) {
        this.lastScreen = lastScreen;
    }
    
    @Override
    public void tick() {
        this.nameEdit.tick();
        this.seedEdit.tick();
    }
    
    @Override
    public void init() {
        final Language language = Language.getInstance();

        Keyboard.enableRepeatEvents(true);
        this.buttons.clear();
        this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 24 * 4 + 12, language.getElement("selectWorld.create")));
        this.buttons.add(new Button(1, this.width / 2 - 100, this.height / 4 + 24 * 5 + 12, language.getElement("gui.cancel")));

        this.nameEdit = new EditBox(this, this.font, this.width / 2 - 100, 60, 200, 20, language.getElement("selectWorld.newWorld"));
        this.nameEdit.inFocus = true;
        this.nameEdit.setMaxLength(32);

        this.seedEdit = new EditBox(this, this.font, this.width / 2 - 100, 116, 200, 20, "");

        this.updateResultFolder();
    }
    
    private void updateResultFolder() {
        this.resultFolder = this.nameEdit.getValue().trim();

        for (int i = 0; i < SharedConstants.ILLEGAL_FILE_CHARACTERS.length; ++i) {
            this.resultFolder = this.resultFolder.replace(SharedConstants.ILLEGAL_FILE_CHARACTERS[i], '_');
        }

        if (Mth.isEmpty(this.resultFolder)) {
            this.resultFolder = "World";
        }
        this.resultFolder = findAvailableFolderName(this.minecraft.getLevelSource(), this.resultFolder);
    }
    
    public static String findAvailableFolderName(final LevelStorageSource levelSource, String folder) {
        while (levelSource.getDataTagFor(folder) != null) {
            folder += "-";
        }
        return folder;
    }
    
    @Override
    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (!button.active) return;
        if (button.id == 1) {
            this.minecraft.setScreen(this.lastScreen);
        }
        else if (button.id == 0) {
            this.minecraft.setScreen(null);
            if (this.done) {
                return;
            }
            this.done = true;
            long seedValue = new Random().nextLong();
            final String seedString = this.seedEdit.getValue();

            if (!Mth.isEmpty(seedString)) {
                try {
                    final long value = Long.parseLong(seedString);
                    if (value != 0L) {
                        seedValue = value;
                    }
                }
                catch (final NumberFormatException ex) {
                    seedValue = seedString.hashCode();
                }
            }

            this.minecraft.gameMode = new SurvivalMode(this.minecraft);
            this.minecraft.selectLevel(this.resultFolder, this.nameEdit.getValue(), seedValue);
            this.minecraft.setScreen(null);
        }
    }
    
    @Override
    protected void keyPressed(final char eventCharacter, final int eventKey) {
        if (this.nameEdit.inFocus) this.nameEdit.keyPressed(eventCharacter, eventKey);
        else this.seedEdit.keyPressed(eventCharacter, eventKey);

        if (eventCharacter == '\r') {
            this.buttonClicked(this.buttons.get(0));
        }
        this.buttons.get(0).active = (this.nameEdit.getValue().length() > 0);

        this.updateResultFolder();
    }
    
    @Override
    protected void mouseClicked(final int x, final int y, final int buttonNum) {
        super.mouseClicked(x, y, buttonNum);

        this.nameEdit.mouseClicked(x, y, buttonNum);
        this.seedEdit.mouseClicked(x, y, buttonNum);
    }
    
    @Override
    public void render(final int xm, final int ym, final float a) {
        final Language language = Language.getInstance();

        this.renderBackground();

        this.drawCenteredString(this.font, language.getElement("selectWorld.create"), this.width / 2, this.height / 4 - 60 + 20, 0xffffff);
        this.drawString(this.font, language.getElement("selectWorld.enterName"), this.width / 2 - 100, 47, 0xa0a0a0);
        this.drawString(this.font, language.getElement("selectWorld.resultFolder") + " " + this.resultFolder, this.width / 2 - 100, 85, 0xa0a0a0);

        this.drawString(this.font, language.getElement("selectWorld.enterSeed"), this.width / 2 - 100, 104, 0xa0a0a0);
        this.drawString(this.font, language.getElement("selectWorld.seedInfo"), this.width / 2 - 100, 140, 0xa0a0a0);

        this.nameEdit.render();
        this.seedEdit.render();

        super.render(xm, ym, a);
    }
    
    @Override
    public void tabPressed() {
        if (this.nameEdit.inFocus) {
            this.nameEdit.focus(false);
            this.seedEdit.focus(true);
        }
        else {
            this.nameEdit.focus(true);
            this.seedEdit.focus(false);
        }
    }
}
