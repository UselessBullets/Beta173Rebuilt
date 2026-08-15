// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

public class ErrorScreen extends Screen
{
    private String title;
    private String message;
    
    @Override
    public void init() {
    }
    
    @Override
    public void render(final int xm, final int ym, final float partialTick) {
        this.fillGradient(0, 0, this.width, this.height, 0xff402020, 0xff501010);
        this.drawCenteredString(this.font, this.title, this.width / 2, 90, 0xffffff);
        this.drawCenteredString(this.font, this.message, this.width / 2, 110, 0xffffff);
        super.render(xm, ym, partialTick);
    }
    
    @Override
    protected void keyPressed(final char eventCharacter, final int eventKey) {
    }
}
