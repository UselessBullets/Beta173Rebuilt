// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.multiplayer;

import net.minecraft.client.title.TitleScreen;
import net.minecraft.client.gui.Button;
import net.minecraft.locale.language.Language;
import net.minecraft.client.gui.Screen;

public class DisconnectedScreen extends Screen
{
    private String title;
    private String reason;
    
    public DisconnectedScreen(final String title, final String reason, final Object... reasonObjects) {
        final Language language = Language.getInstance();

        this.title = language.getElement(title);
        if (reasonObjects != null) {
            this.reason = language.getElement(reason, reasonObjects);
        }
        else {
            this.reason = language.getElement(reason);
        }
    }
    
    @Override
    public void tick() {
    }
    
    @Override
    protected void keyPressed(final char eventCharacter, final int eventKey) {
    }
    
    @Override
    public void init() {
        final Language language = Language.getInstance();

        this.buttons.clear();
        this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 24 * 5 + 12, language.getElement("gui.toMenu")));
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (button.id == 0) {
            this.minecraft.setScreen(new TitleScreen());
        }
    }
    
    @Override
    public void render(final int xm, final int ym, final float a) {
        this.renderBackground();

        this.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 50, 0xffffff);
        this.drawCenteredString(this.font, this.reason, this.width / 2, this.height / 2 - 10, 0xffffff);

        super.render(xm, ym, a);
    }
}
