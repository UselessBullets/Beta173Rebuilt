// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import org.lwjgl.input.Keyboard;
import net.minecraft.locale.language.Language;

public class RenameWorldScreen extends Screen
{
    private Screen lastScreen;
    private EditBox nameEdit;
    private final String levelId;
    
    public RenameWorldScreen(final Screen lastScreen, final String levelId) {
        this.lastScreen = lastScreen;
        this.levelId = levelId;
    }
    
    @Override
    public void tick() {
        this.nameEdit.tick();
    }
    
    @Override
    public void init() {
        final Language instance = Language.getInstance();
        Keyboard.enableRepeatEvents(true);
        this.buttons.clear();
        this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 96 + 12, instance.getElement("selectWorld.renameButton")));
        this.buttons.add(new Button(1, this.width / 2 - 100, this.height / 4 + 120 + 12, instance.getElement("gui.cancel")));
        this.nameEdit = new EditBox(this, this.font, this.width / 2 - 100, 60, 200, 20, this.minecraft.getLevelSource().getDataTagFor(this.levelId).getLevelName());
        this.nameEdit.inFocus = true;
        this.nameEdit.setMaxLength(32);
    }
    
    @Override
    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (!button.active) {
            return;
        }
        if (button.id == 1) {
            this.minecraft.setScreen(this.lastScreen);
        }
        else if (button.id == 0) {
            this.minecraft.getLevelSource().renameLevel(this.levelId, this.nameEdit.getValue().trim());
            this.minecraft.setScreen(this.lastScreen);
        }
    }
    
    @Override
    protected void keyPressed(final char eventCharacter, final int eventKey) {
        this.nameEdit.keyPressed(eventCharacter, eventKey);
        this.buttons.get(0).active = (this.nameEdit.getValue().trim().length() > 0);
        if (eventCharacter == '\r') {
            this.buttonClicked(this.buttons.get(0));
        }
    }
    
    @Override
    protected void mouseClicked(final int x, final int y, final int buttonNum) {
        super.mouseClicked(x, y, buttonNum);
        this.nameEdit.mouseClicked(x, y, buttonNum);
    }
    
    @Override
    public void render(final int xm, final int ym, final float partialTick) {
        final Language instance = Language.getInstance();
        this.renderBackground();
        this.drawCenteredString(this.font, instance.getElement("selectWorld.renameTitle"), this.width / 2, this.height / 4 - 60 + 20, 0xffffff);
        this.drawString(this.font, instance.getElement("selectWorld.enterName"), this.width / 2 - 100, 47, 0xa0a0a0);
        this.nameEdit.render();
        super.render(xm, ym, partialTick);
    }
}
