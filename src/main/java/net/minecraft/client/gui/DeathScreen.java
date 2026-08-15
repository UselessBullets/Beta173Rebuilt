// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.title.TitleScreen;

public class DeathScreen extends Screen
{
    @Override
    public void init() {
        this.buttons.clear();
        this.buttons.add(new Button(1, this.width / 2 - 100, this.height / 4 + 72, "Respawn"));
        this.buttons.add(new Button(2, this.width / 2 - 100, this.height / 4 + 96, "Title menu"));

        if (this.minecraft.user == null) {
            this.buttons.get(1).active = false;
        }
    }
    
    @Override
    protected void keyPressed(final char eventCharacter, final int eventKey) {
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (button.id == 0) {
            //            minecraft.setScreen(new OptionsScreen(this, minecraft.options));
        }
        if (button.id == 1) {
            this.minecraft.player.respawn();
            this.minecraft.setScreen(null);
            //            minecraft.setScreen(new NewLevelScreen(this));
        }
        if (button.id == 2) {
            this.minecraft.setLevel(null);
            this.minecraft.setScreen(new TitleScreen());
        }
    }
    
    @Override
    public void render(final int xm, final int ym, final float partialTick) {
        this.fillGradient(0, 0, this.width, this.height, 0x60500000, 0xa0803030);

        GL11.glPushMatrix();
        GL11.glScalef(2.0f, 2.0f, 2.0f);
        this.drawCenteredString(this.font, "Game over!", this.width / 2 / 2, 30, 0xffffff);
        GL11.glPopMatrix();
        this.drawCenteredString(this.font, "Score: &e" + this.minecraft.player.getScore(), this.width / 2, 100, 0xffffff);

        super.render(xm, ym, partialTick);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
