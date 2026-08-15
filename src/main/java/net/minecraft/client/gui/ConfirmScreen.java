// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

public class ConfirmScreen extends Screen
{
    private Screen parent;
    private String title1;
    private String title2;
    private String yetButton;
    private String noButton;
    private int id;

    public ConfirmScreen(final Screen parent, final String title1, final String title2, final String yesButton, final String noButton, final int id) {
        this.parent = parent;
        this.title1 = title1;
        this.title2 = title2;
        this.yetButton = yesButton;
        this.noButton = noButton;
        this.id = id;
    }
    
    @Override
    public void init() {
        this.buttons.add(new SmallButton(0, this.width / 2 - 155 + 0 % 2 * 160, this.height / 6 + 24 * 4, this.yetButton));
        this.buttons.add(new SmallButton(1, this.width / 2 - 155 + 1 % 2 * 160, this.height / 6 + 24 * 4, this.noButton));
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        this.parent.confirmResult(button.id == 0, this.id);
    }
    
    @Override
    public void render(final int xm, final int ym, final float partialTick) {
        this.renderBackground();

        this.drawCenteredString(this.font, this.title1, this.width / 2, 70, 0xffffff);
        this.drawCenteredString(this.font, this.title2, this.width / 2, 90, 0xffffff);

        super.render(xm, ym, partialTick);
    }
}
