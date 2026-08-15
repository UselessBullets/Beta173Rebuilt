// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.client.multiplayer.ConnectScreen;
import org.lwjgl.input.Keyboard;
import net.minecraft.locale.language.Language;

public class JoinMultiplayerScreen extends Screen
{
    private Screen lastScreen;
    private EditBox ipEdit;
    
    public JoinMultiplayerScreen(final Screen lastScreen) {
        this.lastScreen = lastScreen;
    }
    
    @Override
    public void tick() {
        this.ipEdit.tick();
    }
    
    @Override
    public void init() {
        final Language instance = Language.getInstance();
        Keyboard.enableRepeatEvents(true);
        this.buttons.clear();
        this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 96 + 12, instance.getElement("multiplayer.connect")));
        this.buttons.add(new Button(1, this.width / 2 - 100, this.height / 4 + 120 + 12, instance.getElement("gui.cancel")));
        final String replaceAll = this.minecraft.options.lastMpIp.replaceAll("_", ":");
        this.buttons.get(0).active = (replaceAll.length() > 0);
        this.ipEdit = new EditBox(this, this.font, this.width / 2 - 100, this.height / 4 - 10 + 50 + 18, 200, 20, replaceAll);
        this.ipEdit.inFocus = true;
        this.ipEdit.setMaxLength(128);
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
            final String trim = this.ipEdit.getValue().trim();
            this.minecraft.options.lastMpIp = trim.replaceAll(":", "_");
            this.minecraft.options.save();
            String[] split = trim.split(":");
            if (trim.startsWith("[")) {
                final int index = trim.indexOf("]");
                if (index > 0) {
                    final String substring = trim.substring(1, index);
                    final String trim2 = trim.substring(index + 1).trim();
                    if (trim2.startsWith(":") && trim2.length() > 0) {
                        split = new String[] { substring, trim2.substring(1) };
                    }
                    else {
                        split = new String[] { substring };
                    }
                }
            }
            if (split.length > 2) {
                split = new String[] { trim };
            }
            this.minecraft.setScreen(new ConnectScreen(this.minecraft, split[0], (split.length > 1) ? this.parseInt(split[1], 25565) : 25565));
        }
    }
    
    private int parseInt(final String str, final int def) {
        try {
            return Integer.parseInt(str.trim());
        }
        catch (final Exception ex) {
            return def;
        }
    }
    
    @Override
    protected void keyPressed(final char eventCharacter, final int eventKey) {
        this.ipEdit.keyPressed(eventCharacter, eventKey);
        if (eventCharacter == '\r') {
            this.buttonClicked(this.buttons.get(0));
        }
        this.buttons.get(0).active = (this.ipEdit.getValue().length() > 0);
    }
    
    @Override
    protected void mouseClicked(final int x, final int y, final int buttonNum) {
        super.mouseClicked(x, y, buttonNum);
        this.ipEdit.mouseClicked(x, y, buttonNum);
    }
    
    @Override
    public void render(final int xm, final int ym, final float partialTick) {
        final Language instance = Language.getInstance();
        this.renderBackground();
        this.drawCenteredString(this.font, instance.getElement("multiplayer.title"), this.width / 2, this.height / 4 - 60 + 20, 0xffffff);
        this.drawString(this.font, instance.getElement("multiplayer.info1"), this.width / 2 - 140, this.height / 4 - 60 + 60 + 0, 0xa0a0a0);
        this.drawString(this.font, instance.getElement("multiplayer.info2"), this.width / 2 - 140, this.height / 4 - 60 + 60 + 9, 0xa0a0a0);
        this.drawString(this.font, instance.getElement("multiplayer.ipinfo"), this.width / 2 - 140, this.height / 4 - 60 + 60 + 36, 0xa0a0a0);
        this.ipEdit.render();
        super.render(xm, ym, partialTick);
    }
}
