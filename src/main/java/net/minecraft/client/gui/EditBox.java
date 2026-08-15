// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.SharedConstants;

public class EditBox extends GuiComponent
{
    private final Font font;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private String value;
    private int maxLength;
    private int frame;
    public boolean inFocus;
    public boolean active;
    private Screen screen;
    
    public EditBox(final Screen screen, final Font font, final int x, final int y, final int width, final int height, final String value) {
        this.inFocus = false;
        this.active = true;
        this.screen = screen;
        this.font = font;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.setValue(value);
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void tick() {
        ++this.frame;
    }
    
    public void keyPressed(final char ch, final int eventKey) {
        if (!this.active || !this.inFocus) {
            return;
        }
        if (ch == '\t') {
            this.screen.tabPressed();
        }
        if (ch == '\u0016') {
            String clipboard = Screen.getClipboard();
            if (clipboard == null) {
                clipboard = "";
            }
            int length = 32 - this.value.length();
            if (length > clipboard.length()) {
                length = clipboard.length();
            }
            if (length > 0) {
                this.value += clipboard.substring(0, length);
            }
        }
        if (eventKey == 14 && this.value.length() > 0) {
            this.value = this.value.substring(0, this.value.length() - 1);
        }
        if (SharedConstants.acceptableLetters.indexOf(ch) >= 0 && (this.value.length() < this.maxLength || this.maxLength == 0)) {
            this.value += ch;
        }
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int buttonNum) {
        this.focus(this.active && mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height);
    }
    
    public void focus(final boolean newFocus) {
        if (newFocus && !this.inFocus) {
            this.frame = 0;
        }
        this.inFocus = newFocus;
    }
    
    public void render() {
        this.fill(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, 0xffa0a0a0);
        this.fill(this.x, this.y, this.x + this.width, this.y + this.height, 0xff000000);
        if (this.active) {
            this.drawString(this.font, this.value + ((this.inFocus && this.frame / 6 % 2 == 0) ? "_" : ""), this.x + 4, this.y + (this.height - 8) / 2, 0xe0e0e0);
        }
        else {
            this.drawString(this.font, this.value, this.x + 4, this.y + (this.height - 8) / 2, 0x707070);
        }
    }
    
    public void setMaxLength(final int maxLength) {
        this.maxLength = maxLength;
    }
}
