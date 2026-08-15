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
        final Language language = Language.getInstance();

        Keyboard.enableRepeatEvents(true);
        this.buttons.clear();
        this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 24 * 4 + 12, language.getElement("multiplayer.connect")));
        this.buttons.add(new Button(1, this.width / 2 - 100, this.height / 4 + 24  * 5 + 12, language.getElement("gui.cancel")));
        final String ip = this.minecraft.options.lastMpIp.replaceAll("_", ":");
        this.buttons.get(0).active = (ip.length() > 0);

        this.ipEdit = new EditBox(this, this.font, this.width / 2 - 100, this.height / 4 - 10 + 50 + 18, 200, 20, ip);
        this.ipEdit.inFocus = true;
        this.ipEdit.setMaxLength(128);
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
            final String ip = this.ipEdit.getValue().trim();

            this.minecraft.options.lastMpIp = ip.replaceAll(":", "_");
            this.minecraft.options.save();

            String[] parts = ip.split(":");
            if (ip.startsWith("[")) {
                final int pos = ip.indexOf("]");
                if (pos > 0) {
                    final String path = ip.substring(1, pos);
                    final String port = ip.substring(pos + 1).trim();
                    if (port.startsWith(":") && port.length() > 0) {
                        parts = new String[] { path, port.substring(1) };
                    }
                    else {
                        parts = new String[] { path };
                    }
                }
            }
            if (parts.length > 2) {
                parts = new String[] { ip };
            }

            this.minecraft.setScreen(new ConnectScreen(this.minecraft, parts[0], (parts.length > 1) ? this.parseInt(parts[1], 25565) : 25565));
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
        final Language language = Language.getInstance();

        this.renderBackground();

        this.drawCenteredString(this.font, language.getElement("multiplayer.title"), this.width / 2, this.height / 4 - 60 + 20, 0xffffff);
        this.drawString(this.font, language.getElement("multiplayer.info1"), this.width / 2 - 140, this.height / 4 - 60 + 60 + 9 * 0, 0xa0a0a0);
        this.drawString(this.font, language.getElement("multiplayer.info2"), this.width / 2 - 140, this.height / 4 - 60 + 60 + 9 * 1, 0xa0a0a0);
        this.drawString(this.font, language.getElement("multiplayer.ipinfo"), this.width / 2 - 140, this.height / 4 - 60 + 60 + 9 * 4, 0xa0a0a0);

        this.ipEdit.render();

        super.render(xm, ym, partialTick);
    }
}
