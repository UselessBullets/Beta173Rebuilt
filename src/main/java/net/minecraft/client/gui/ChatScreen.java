// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.SharedConstants;
import org.lwjgl.input.Keyboard;

public class ChatScreen extends Screen
{
    protected String message = "";
    private int frame = 0;
    private static final String allowedChars = SharedConstants.acceptableLetters;
    
    @Override
    public void init() {
        Keyboard.enableRepeatEvents(true);
    }
    
    @Override
    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }
    
    @Override
    public void tick() {
        ++this.frame;
    }
    
    @Override
    protected void keyPressed(final char eventCharacter, final int eventKey) {
        if (eventKey == Keyboard.KEY_ESCAPE) {
            this.minecraft.setScreen(null);
            return;
        }
        if (eventKey == Keyboard.KEY_RETURN) {
            if (this.message.trim().length() > 0) {
                final String trim = this.message.trim();
                if (!this.minecraft.handleClientSideCommand(trim)) {
                    this.minecraft.player.chat(trim);
                }
            }
            this.minecraft.setScreen(null);
            return;
        }
        if (eventKey == Keyboard.KEY_BACK && this.message.length() > 0) this.message = this.message.substring(0, this.message.length() - 1);
        if (ChatScreen.allowedChars.indexOf(eventCharacter) >= 0 && this.message.length() < SharedConstants.maxChatLength) {
            this.message += eventCharacter;
        }
    }
    
    @Override
    public void render(final int xm, final int ym, final float a) {
        this.fill(2, this.height - 14, this.width - 2, this.height - 2, 0x80000000);
        this.drawString(this.font, "> " + this.message + ((this.frame / 6 % 2 == 0) ? "_" : ""), 4, this.height - 12, 0xe0e0e0);

        super.render(xm, ym, a);
    }
    
    @Override
    protected void mouseClicked(final int x, final int y, final int buttonNum) {
        if (buttonNum == 0) {
            if (this.minecraft.gui.selectedName != null) {
                if (this.message.length() > 0 && !this.message.endsWith(" ")) {
                    this.message += " ";
                }
                this.message += this.minecraft.gui.selectedName;
                final int maxLength = SharedConstants.maxChatLength;
                if (this.message.length() > maxLength) {
                    this.message = this.message.substring(0, maxLength);
                }
            }
            else {
                super.mouseClicked(x, y, buttonNum);
            }
        }
    }

}
