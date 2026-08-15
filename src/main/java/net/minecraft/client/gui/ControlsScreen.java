// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.locale.language.Language;
import net.minecraft.client.Options;

public class ControlsScreen extends Screen
{
    private Screen lastScreen;
    protected String tile;
    private Options options;
    private int selectedKey;
    
    public ControlsScreen(final Screen lastScreen, final Options options) {
        this.tile = "Controls";
        this.selectedKey = -1;
        this.lastScreen = lastScreen;
        this.options = options;
    }
    
    private int getLeftScreenPosition() {
        return this.width / 2 - 155;
    }
    
    @Override
    public void init() {
        final Language instance = Language.getInstance();
        final int leftScreenPosition = this.getLeftScreenPosition();
        for (int i = 0; i < this.options.keyMappings.length; ++i) {
            this.buttons.add(new SmallButton(i, leftScreenPosition + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 70, 20, this.options.getKeyMessage(i)));
        }
        this.buttons.add(new Button(200, this.width / 2 - 100, this.height / 6 + 168, instance.getElement("gui.done")));
        this.tile = instance.getElement("controls.title");
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        for (int i = 0; i < this.options.keyMappings.length; ++i) {
            ((Button)this.buttons.get(i)).msg = this.options.getKeyMessage(i);
        }
        if (button.id == 200) {
            this.minecraft.setScreen(this.lastScreen);
        }
        else {
            this.selectedKey = button.id;
            button.msg = "> " + this.options.getKeyMessage(button.id) + " <";
        }
    }
    
    @Override
    protected void keyPressed(final char ch, final int eventKey) {
        if (this.selectedKey >= 0) {
            this.options.setKey(this.selectedKey, eventKey);
            this.buttons.get(this.selectedKey).msg = this.options.getKeyMessage(this.selectedKey);
            this.selectedKey = -1;
        }
        else {
            super.keyPressed(ch, eventKey);
        }
    }
    
    @Override
    public void render(final int xm, final int ym, final float partialTick) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.tile, this.width / 2, 20, 0xffffff);
        final int leftScreenPosition = this.getLeftScreenPosition();
        for (int i = 0; i < this.options.keyMappings.length; ++i) {
            this.drawString(this.font, this.options.getKeyDesciption(i), leftScreenPosition + i % 2 * 160 + 70 + 6, this.height / 6 + 24 * (i >> 1) + 7, 0xffffffff);
        }
        super.render(xm, ym, partialTick);
    }
}
