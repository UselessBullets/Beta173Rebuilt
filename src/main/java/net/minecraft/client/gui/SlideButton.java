// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.client.Options.Option;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;

public class SlideButton extends Button
{
    public float value;
    public boolean sliding;
    private Option option;
    
    public SlideButton(final int id, final int x, final int y, final Option option, final String msg, final float value) {
        super(id, x, y, 150, 20, msg);
        this.value = 1.0f;
        this.sliding = false;
        this.option = null;
        this.option = option;
        this.value = value;
    }
    
    @Override
    protected int getYImage(final boolean hovered) {
        return 0;
    }
    
    @Override
    protected void renderBg(final Minecraft minecraft, final int xm, final int ym) {
        if (!this.visible) {
            return;
        }
        if (this.sliding) {
            this.value = (xm - (this.x + 4)) / (float)(this.w - 8);
            if (this.value < 0.0f) {
                this.value = 0.0f;
            }
            if (this.value > 1.0f) {
                this.value = 1.0f;
            }
            minecraft.options.set(this.option, this.value);
            this.msg = minecraft.options.getMessage(this.option);
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.blit(this.x + (int)(this.value * (this.w - 8)), this.y, 0, 66, 4, 20);
        this.blit(this.x + (int)(this.value * (this.w - 8)) + 4, this.y, 196, 66, 4, 20);
    }
    
    @Override
    public boolean clicked(final Minecraft minecraft, final int mx, final int my) {
        if (super.clicked(minecraft, mx, my)) {
            this.value = (mx - (this.x + 4)) / (float)(this.w - 8);
            if (this.value < 0.0f) {
                this.value = 0.0f;
            }
            if (this.value > 1.0f) {
                this.value = 1.0f;
            }
            minecraft.options.set(this.option, this.value);
            this.msg = minecraft.options.getMessage(this.option);
            return this.sliding = true;
        }
        return false;
    }
    
    @Override
    public void released(final int mx, final int my) {
        this.sliding = false;
    }
}
