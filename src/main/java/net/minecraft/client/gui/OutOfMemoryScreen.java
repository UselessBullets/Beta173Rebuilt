// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

public class OutOfMemoryScreen extends Screen
{
    private int frame;
    
    public OutOfMemoryScreen() {
        this.frame = 0;
    }
    
    @Override
    public void tick() {
        ++this.frame;
    }
    
    @Override
    public void init() {
    }
    
    @Override
    protected void buttonClicked(final Button button) {
    }
    
    @Override
    protected void keyPressed(final char eventCharacter, final int eventKey) {
    }
    
    @Override
    public void render(final int xm, final int ym, final float partialTick) {
        this.renderBackground();
        this.drawCenteredString(this.font, "Out of memory!", this.width / 2, this.height / 4 - 60 + 20, 0xffffff);
        this.drawString(this.font, "Minecraft has run out of memory.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 9 * 0, 0xa0a0a0);

        this.drawString(this.font, "This could be caused by a bug in the game or by the", this.width / 2 - 140, this.height / 4 - 60 + 60 + 9 * 2, 0xa0a0a0);
        this.drawString(this.font, "Java Virtual Machine not being allocated enough", this.width / 2 - 140, this.height / 4 - 60 + 60 + 9 * 3, 0xa0a0a0);
        this.drawString(this.font, "memory. If you are playing in a web browser, try", this.width / 2 - 140, this.height / 4 - 60 + 60 + 9 * 4, 0xa0a0a0);
        this.drawString(this.font, "downloading the game and playing it offline.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 9 * 5, 0xa0a0a0);

        this.drawString(this.font, "To prevent level corruption, the current game has quit.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 9 * 7, 0xa0a0a0);

        this.drawString(this.font, "Please restart the game.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 9 * 9, 0xa0a0a0);
        super.render(xm, ym, partialTick);
    }
}
