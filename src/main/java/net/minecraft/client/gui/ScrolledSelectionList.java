// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import org.lwjgl.opengl.GL11;
import org.lwjgl.input.Mouse;
import java.util.List;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.client.Minecraft;

import static org.lwjgl.opengl.GL11.*;

public abstract class ScrolledSelectionList
{
    private final Minecraft minecraft;
    private final int width;
    private final int height;
    protected final int y0;
    protected final int y1;
    private final int x1;
    private final int x0;
    protected final int itemHeight;
    private int upId;
    private int downId;
    private float yDrag;
    private float yDragScale;
    private float yo;
    private int lastSelection;
    private long lastSelectionTime;
    private boolean renderSelection;
    private boolean renderHeader;
    private int headerHeight;
    
    public ScrolledSelectionList(final Minecraft minecraft, final int width, final int height, final int y0, final int y1, final int itemHeight) {
        this.yDrag = -2.0f;
        this.lastSelection = -1;
        this.lastSelectionTime = 0L;
        this.renderSelection = true;
        this.minecraft = minecraft;
        this.width = width;
        this.height = height;
        this.y0 = y0;
        this.y1 = y1;
        this.itemHeight = itemHeight;
        this.x0 = 0;
        this.x1 = width;
    }
    
    public void setRenderSelection(final boolean renderSelection) {
        this.renderSelection = renderSelection;
    }
    
    protected void setRenderHeader(final boolean renderHeader, final int headerHeight) {
        this.renderHeader = renderHeader;
        this.headerHeight = headerHeight;
        if (!renderHeader) {
            this.headerHeight = 0;
        }
    }
    
    protected abstract int getNumberOfItems();
    
    protected abstract void selectItem(final int item, final boolean doubleClick);
    
    protected abstract boolean isSelectedItem(final int item);
    
    protected int getMaxPosition() {
        return this.getNumberOfItems() * this.itemHeight + this.headerHeight;
    }
    
    protected abstract void renderBackground();
    
    protected abstract void renderItem(final int i, final int x, final int y, final int h, final Tesselator t);
    
    protected void renderHeader(final int x, final int y, final Tesselator t) {
    }
    
    protected void clickedHeader(final int headerMouseX, final int headerMouseY) {
    }
    
    protected void renderDecorations(final int mouseX, final int mouseY) {
    }
    
    public int getItemAtPosition(final int x, final int y) {
        final int n = this.width / 2 - 110;
        final int n2 = this.width / 2 + 110;
        final int n3 = y - this.y0 - this.headerHeight + (int)this.yo - 4;
        final int n4 = n3 / this.itemHeight;
        if (x >= n && x <= n2 && n4 >= 0 && n3 >= 0 && n4 < this.getNumberOfItems()) {
            return n4;
        }
        return -1;
    }
    
    public void init(final List buttons, final int upButtonId, final int downButtonId) {
        this.upId = upButtonId;
        this.downId = downButtonId;
    }
    
    private void capYPosition() {
        int n = this.getMaxPosition() - (this.y1 - this.y0 - 4);
        if (n < 0) {
            n /= 2;
        }
        if (this.yo < 0.0f) {
            this.yo = 0.0f;
        }
        if (this.yo > n) {
            this.yo = (float)n;
        }
    }
    
    public void buttonClicked(final Button button) {
        if (!button.active) {
            return;
        }
        if (button.id == this.upId) {
            this.yo -= this.itemHeight * 2 / 3;
            this.yDrag = -2.0f;
            this.capYPosition();
        }
        else if (button.id == this.downId) {
            this.yo += this.itemHeight * 2 / 3;
            this.yDrag = -2.0f;
            this.capYPosition();
        }
    }
    
    public void render(final int xm, final int ym, final float partialTick) {
        this.renderBackground();
        final int numberOfItems = this.getNumberOfItems();
        final int n = this.width / 2 + 124;
        final int n2 = n + 6;
        if (Mouse.isButtonDown(0)) {
            if (this.yDrag == -1.0f) {
                boolean b = true;
                if (ym >= this.y0 && ym <= this.y1) {
                    final int n3 = this.width / 2 - 110;
                    final int n4 = this.width / 2 + 110;
                    final int n5 = ym - this.y0 - this.headerHeight + (int)this.yo - 4;
                    final int n6 = n5 / this.itemHeight;
                    if (xm >= n3 && xm <= n4 && n6 >= 0 && n5 >= 0 && n6 < numberOfItems) {
                        this.selectItem(n6, n6 == this.lastSelection && System.currentTimeMillis() - this.lastSelectionTime < 250L);
                        this.lastSelection = n6;
                        this.lastSelectionTime = System.currentTimeMillis();
                    }
                    else if (xm >= n3 && xm <= n4 && n5 < 0) {
                        this.clickedHeader(xm - n3, ym - this.y0 + (int)this.yo - 4);
                        b = false;
                    }
                    if (xm >= n && xm <= n2) {
                        this.yDragScale = -1.0f;
                        int n7 = this.getMaxPosition() - (this.y1 - this.y0 - 4);
                        if (n7 < 1) {
                            n7 = 1;
                        }
                        int n8 = (int)((this.y1 - this.y0) * (this.y1 - this.y0) / (float)this.getMaxPosition());
                        if (n8 < 32) {
                            n8 = 32;
                        }
                        if (n8 > this.y1 - this.y0 - 8) {
                            n8 = this.y1 - this.y0 - 8;
                        }
                        this.yDragScale /= (this.y1 - this.y0 - n8) / (float)n7;
                    }
                    else {
                        this.yDragScale = 1.0f;
                    }
                    if (b) {
                        this.yDrag = (float)ym;
                    }
                    else {
                        this.yDrag = -2.0f;
                    }
                }
                else {
                    this.yDrag = -2.0f;
                }
            }
            else if (this.yDrag >= 0.0f) {
                this.yo -= (ym - this.yDrag) * this.yDragScale;
                this.yDrag = (float)ym;
            }
        }
        else {
            this.yDrag = -1.0f;
        }
        this.capYPosition();
        GL11.glDisable(GL_LIGHTING);
        GL11.glDisable(2912);
        final Tesselator instance = Tesselator.instance;
        GL11.glBindTexture(3553, this.minecraft.textures.loadTexture("/gui/background.png"));
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final float n9 = 32.0f;
        instance.begin();
        instance.color(2105376);
        instance.vertexUV(this.x0, this.y1, 0.0, this.x0 / n9, (this.y1 + (int)this.yo) / n9);
        instance.vertexUV(this.x1, this.y1, 0.0, this.x1 / n9, (this.y1 + (int)this.yo) / n9);
        instance.vertexUV(this.x1, this.y0, 0.0, this.x1 / n9, (this.y0 + (int)this.yo) / n9);
        instance.vertexUV(this.x0, this.y0, 0.0, this.x0 / n9, (this.y0 + (int)this.yo) / n9);
        instance.end();
        final int n10 = this.width / 2 - 92 - 16;
        final int y = this.y0 + 4 - (int)this.yo;
        if (this.renderHeader) {
            this.renderHeader(n10, y, instance);
        }
        for (int i = 0; i < numberOfItems; ++i) {
            final int y2 = y + i * this.itemHeight + this.headerHeight;
            final int h = this.itemHeight - 4;
            if (y2 <= this.y1) {
                if (y2 + h >= this.y0) {
                    if (this.renderSelection && this.isSelectedItem(i)) {
                        final int n11 = this.width / 2 - 110;
                        final int n12 = this.width / 2 + 110;
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                        GL11.glDisable(GL_TEXTURE_2D);
                        instance.begin();
                        instance.color(8421504);
                        instance.vertexUV(n11, y2 + h + 2, 0.0, 0.0, 1.0);
                        instance.vertexUV(n12, y2 + h + 2, 0.0, 1.0, 1.0);
                        instance.vertexUV(n12, y2 - 2, 0.0, 1.0, 0.0);
                        instance.vertexUV(n11, y2 - 2, 0.0, 0.0, 0.0);
                        instance.color(0);
                        instance.vertexUV(n11 + 1, y2 + h + 1, 0.0, 0.0, 1.0);
                        instance.vertexUV(n12 - 1, y2 + h + 1, 0.0, 1.0, 1.0);
                        instance.vertexUV(n12 - 1, y2 - 1, 0.0, 1.0, 0.0);
                        instance.vertexUV(n11 + 1, y2 - 1, 0.0, 0.0, 0.0);
                        instance.end();
                        GL11.glEnable(GL_TEXTURE_2D);
                    }
                    this.renderItem(i, n10, y2, h, instance);
                }
            }
        }
        GL11.glDisable(GL_DEPTH_TEST);
        final int n13 = 4;
        this.renderHoleBackground(0, this.y0, 255, 255);
        this.renderHoleBackground(this.y1, this.height, 255, 255);
        GL11.glEnable(GL_BLEND);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3008);
        GL11.glShadeModel(7425);
        GL11.glDisable(GL_TEXTURE_2D);
        instance.begin();
        instance.color(0, 0);
        instance.vertexUV(this.x0, this.y0 + n13, 0.0, 0.0, 1.0);
        instance.vertexUV(this.x1, this.y0 + n13, 0.0, 1.0, 1.0);
        instance.color(0, 255);
        instance.vertexUV(this.x1, this.y0, 0.0, 1.0, 0.0);
        instance.vertexUV(this.x0, this.y0, 0.0, 0.0, 0.0);
        instance.end();
        instance.begin();
        instance.color(0, 255);
        instance.vertexUV(this.x0, this.y1, 0.0, 0.0, 1.0);
        instance.vertexUV(this.x1, this.y1, 0.0, 1.0, 1.0);
        instance.color(0, 0);
        instance.vertexUV(this.x1, this.y1 - n13, 0.0, 1.0, 0.0);
        instance.vertexUV(this.x0, this.y1 - n13, 0.0, 0.0, 0.0);
        instance.end();
        final int n14 = this.getMaxPosition() - (this.y1 - this.y0 - 4);
        if (n14 > 0) {
            int n15 = (this.y1 - this.y0) * (this.y1 - this.y0) / this.getMaxPosition();
            if (n15 < 32) {
                n15 = 32;
            }
            if (n15 > this.y1 - this.y0 - 8) {
                n15 = this.y1 - this.y0 - 8;
            }
            int y3 = (int)this.yo * (this.y1 - this.y0 - n15) / n14 + this.y0;
            if (y3 < this.y0) {
                y3 = this.y0;
            }
            instance.begin();
            instance.color(0, 255);
            instance.vertexUV(n, this.y1, 0.0, 0.0, 1.0);
            instance.vertexUV(n2, this.y1, 0.0, 1.0, 1.0);
            instance.vertexUV(n2, this.y0, 0.0, 1.0, 0.0);
            instance.vertexUV(n, this.y0, 0.0, 0.0, 0.0);
            instance.end();
            instance.begin();
            instance.color(8421504, 255);
            instance.vertexUV(n, y3 + n15, 0.0, 0.0, 1.0);
            instance.vertexUV(n2, y3 + n15, 0.0, 1.0, 1.0);
            instance.vertexUV(n2, y3, 0.0, 1.0, 0.0);
            instance.vertexUV(n, y3, 0.0, 0.0, 0.0);
            instance.end();
            instance.begin();
            instance.color(12632256, 255);
            instance.vertexUV(n, y3 + n15 - 1, 0.0, 0.0, 1.0);
            instance.vertexUV(n2 - 1, y3 + n15 - 1, 0.0, 1.0, 1.0);
            instance.vertexUV(n2 - 1, y3, 0.0, 1.0, 0.0);
            instance.vertexUV(n, y3, 0.0, 0.0, 0.0);
            instance.end();
        }
        this.renderDecorations(xm, ym);
        GL11.glEnable(GL_TEXTURE_2D);
        GL11.glShadeModel(7424);
        GL11.glEnable(3008);
        GL11.glDisable(3042);
    }
    
    private void renderHoleBackground(final int y0, final int y1, final int a0, final int a1) {
        final Tesselator instance = Tesselator.instance;
        GL11.glBindTexture(3553, this.minecraft.textures.loadTexture("/gui/background.png"));
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final float n = 32.0f;
        instance.begin();
        instance.color(4210752, a1);
        instance.vertexUV(0.0, y1, 0.0, 0.0, y1 / n);
        instance.vertexUV(this.width, y1, 0.0, this.width / n, y1 / n);
        instance.color(4210752, a0);
        instance.vertexUV(this.width, y0, 0.0, this.width / n, y0 / n);
        instance.vertexUV(0.0, y0, 0.0, 0.0, y0 / n);
        instance.end();
    }
}
