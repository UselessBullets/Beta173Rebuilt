// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.client.title.TitleScreen;

public class LevelConflictScreen extends Screen
{
    private int frame = 0;
    
    @Override
    public void tick() {
        ++this.frame;
    }
    
    @Override
    public void init() {
        this.buttons.clear();
        this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 120 + 12, "Back to title screen"));
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (!button.active) return;
        if (button.id == 0) this.minecraft.setScreen(new TitleScreen());
    }
    
    @Override
    public void render(final int xm, final int ym, final float partialTick) {
        this.renderBackground();

        this.drawCenteredString(this.font, "Level save conflict", this.width / 2, this.height / 4 - 60 + 20, 0xffffff);
        this.drawString(this.font, "Minecraft detected a conflict in the level save data.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 9 * 0, 0xa0a0a0);

        this.drawString(this.font, "This could be caused by two copies of the game", this.width / 2 - 140, this.height / 4 - 60 + 60 + 9 * 2, 0xa0a0a0);
        this.drawString(this.font, "accessing the same level.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 9 * 3, 0xa0a0a0);

        this.drawString(this.font, "To prevent level corruption, the current game has quit.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 9 * 5, 0xa0a0a0);

        super.render(xm, ym, partialTick);
    }
}
