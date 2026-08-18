// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import org.lwjgl.input.Mouse;
import java.util.List;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.client.Minecraft;

import static org.lwjgl.opengl.GL11.*;

public abstract class ScrolledSelectionList
{
    private static final int NO_DRAG = -1;
    private static final int DRAG_OUTSIDE = -2;
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
    private float yDrag = -2.0f;
    private float yDragScale;
    private float yo;
    private int lastSelection = -1;
    private long lastSelectionTime = 0L;
    private boolean renderSelection = true;
    private boolean renderHeader;
    private int headerHeight;
    
    public ScrolledSelectionList(final Minecraft minecraft, final int width, final int height, final int y0, final int y1, final int itemHeight) {
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
        final int x0 = this.width / 2 - (92 + 16 + 2);
        final int x1 = this.width / 2 + (92 + 16 + 2);

        final int clickSlotPos = y - this.y0 - this.headerHeight + (int)this.yo - 4;
        final int slot = clickSlotPos / this.itemHeight;
        if (x >= x0 && x <= x1 && slot >= 0 && clickSlotPos >= 0 && slot < this.getNumberOfItems()) {
            return slot;
        }
        return -1;
    }
    
    public void init(final List<Button> buttons, final int upButtonId, final int downButtonId) {
        this.upId = upButtonId;
        this.downId = downButtonId;
    }
    
    private void capYPosition() {
        int max = this.getMaxPosition() - (this.y1 - this.y0 - 4);
        if (max < 0) max /= 2;
        if (this.yo < 0.0f) this.yo = 0.0f;
        if (this.yo > max) this.yo = (float) max;
    }
    
    public void buttonClicked(final Button button) {
        if (!button.active) return;

        if (button.id == this.upId) {
            this.yo -= this.itemHeight * 2 / 3;
            this.yDrag = DRAG_OUTSIDE;
            this.capYPosition();
        }
        else if (button.id == this.downId) {
            this.yo += this.itemHeight * 2 / 3;
            this.yDrag = DRAG_OUTSIDE;
            this.capYPosition();
        }
    }
    
    public void render(final int xm, final int ym, final float a) {
        this.renderBackground();

        final int itemCount = this.getNumberOfItems();

        final int xx0 = this.width / 2 + 124;
        final int xx1 = xx0 + 6;

        if (Mouse.isButtonDown(0)) {
            if (this.yDrag == NO_DRAG) {
                boolean doDrag = true;
                if (ym >= this.y0 && ym <= this.y1) {
                    final int x0 = this.width / 2 - (92 + 16 + 2);
                    final int x1 = this.width / 2 + (92 + 16 + 2);

                    final int clickSlotPos = ym - this.y0 - this.headerHeight + (int)this.yo - 4;
                    final int slot = clickSlotPos / this.itemHeight;
                    if (xm >= x0 && xm <= x1 && slot >= 0 && clickSlotPos >= 0 && slot < itemCount) {
                        boolean doubleClick = slot == this.lastSelection && System.currentTimeMillis() - this.lastSelectionTime < 250L;

                        this.selectItem(slot, doubleClick);
                        this.lastSelection = slot;
                        this.lastSelectionTime = System.currentTimeMillis();
                    }
                    else if (xm >= x0 && xm <= x1 && clickSlotPos < 0) {
                        this.clickedHeader(xm - x0, ym - this.y0 + (int)this.yo - 4);
                        doDrag = false;
                    }
                    if (xm >= xx0 && xm <= xx1) {
                        this.yDragScale = -1.0f;

                        int max = this.getMaxPosition() - (this.y1 - this.y0 - 4);
                        if (max < 1) max = 1;
                        int barHeight = (int)((this.y1 - this.y0) * (this.y1 - this.y0) / (float)this.getMaxPosition());
                        if (barHeight < 32) barHeight = 32;
                        if (barHeight > this.y1 - this.y0 - 8) barHeight = this.y1 - this.y0 - 8;

                        this.yDragScale /= (this.y1 - this.y0 - barHeight) / (float)max;
                    }
                    else {
                        this.yDragScale = 1.0f;
                    }
                    if (doDrag) {
                        this.yDrag = (float)ym;
                    }
                    else {
                        this.yDrag = DRAG_OUTSIDE;
                    }
                }
                else {
                    this.yDrag = DRAG_OUTSIDE;
                }
            }
            else if (this.yDrag >= 0.0f) {
                this.yo -= (ym - this.yDrag) * this.yDragScale;
                this.yDrag = (float)ym;
            }
        }
        else {
            this.yDrag = NO_DRAG;
        }
        this.capYPosition();

        glDisable(GL_LIGHTING);
        glDisable(GL_FOG);
        final Tesselator t = Tesselator.instance;

        glBindTexture(GL_TEXTURE_2D, this.minecraft.textures.loadTexture("/gui/background.png"));
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final float s = 32.0f;
        t.begin();
        t.color(0x202020);
        t.vertexUV(this.x0, this.y1, 0.0, this.x0 / s, (this.y1 + (int)this.yo) / s);
        t.vertexUV(this.x1, this.y1, 0.0, this.x1 / s, (this.y1 + (int)this.yo) / s);
        t.vertexUV(this.x1, this.y0, 0.0, this.x1 / s, (this.y0 + (int)this.yo) / s);
        t.vertexUV(this.x0, this.y0, 0.0, this.x0 / s, (this.y0 + (int)this.yo) / s);
        t.end();

        final int rowX = this.width / 2 - 92 - 16;
        final int rowBaseY = this.y0 + 4 - (int)this.yo;

        if (this.renderHeader) {
            this.renderHeader(rowX, rowBaseY, t);
        }

        for (int i = 0; i < itemCount; ++i) {
            final int y = rowBaseY + i * this.itemHeight + this.headerHeight;
            final int h = this.itemHeight - 4;

            if (y > this.y1 || y + h < this.y0) {
                continue;
            }

            if (this.renderSelection && this.isSelectedItem(i)) {
                final int x0 = this.width / 2 - 110;
                final int x1 = this.width / 2 + 110;
                glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                glDisable(GL_TEXTURE_2D);
                t.begin();
                t.color(0x808080);
                t.vertexUV(x0, y + h + 2, 0.0, 0.0, 1.0);
                t.vertexUV(x1, y + h + 2, 0.0, 1.0, 1.0);
                t.vertexUV(x1, y - 2, 0.0, 1.0, 0.0);
                t.vertexUV(x0, y - 2, 0.0, 0.0, 0.0);

                t.color(0x000000);
                t.vertexUV(x0 + 1, y + h + 1, 0.0, 0.0, 1.0);
                t.vertexUV(x1 - 1, y + h + 1, 0.0, 1.0, 1.0);
                t.vertexUV(x1 - 1, y - 1, 0.0, 1.0, 0.0);
                t.vertexUV(x0 + 1, y - 1, 0.0, 0.0, 0.0);

                t.end();
                glEnable(GL_TEXTURE_2D);
            }

            this.renderItem(i, rowX, y, h, t);
        }

        glDisable(GL_DEPTH_TEST);

        final int d = 4;

        this.renderHoleBackground(0, this.y0, 255, 255);
        this.renderHoleBackground(this.y1, this.height, 255, 255);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_ALPHA_TEST);
        glShadeModel(GL_SMOOTH);

        glDisable(GL_TEXTURE_2D);

        t.begin();
        t.color(0x000000, 0);
        t.vertexUV(this.x0, this.y0 + d, 0.0, 0.0, 1.0);
        t.vertexUV(this.x1, this.y0 + d, 0.0, 1.0, 1.0);
        t.color(0x000000, 255);
        t.vertexUV(this.x1, this.y0, 0.0, 1.0, 0.0);
        t.vertexUV(this.x0, this.y0, 0.0, 0.0, 0.0);
        t.end();

        t.begin();
        t.color(0x000000, 255);
        t.vertexUV(this.x0, this.y1, 0.0, 0.0, 1.0);
        t.vertexUV(this.x1, this.y1, 0.0, 1.0, 1.0);
        t.color(0x000000, 0);
        t.vertexUV(this.x1, this.y1 - d, 0.0, 1.0, 0.0);
        t.vertexUV(this.x0, this.y1 - d, 0.0, 0.0, 0.0);
        t.end();

        final int max = this.getMaxPosition() - (this.y1 - this.y0 - 4);
        if (max > 0) {
            int barHeight = (this.y1 - this.y0) * (this.y1 - this.y0) / this.getMaxPosition();
            if (barHeight < 32) barHeight = 32;
            if (barHeight > this.y1 - this.y0 - 8) barHeight = this.y1 - this.y0 - 8;

            int yp = (int)this.yo * (this.y1 - this.y0 - barHeight) / max + this.y0;
            if (yp < this.y0) yp = this.y0;

            t.begin();
            t.color(0x000000, 255);
            t.vertexUV(xx0, this.y1, 0.0, 0.0, 1.0);
            t.vertexUV(xx1, this.y1, 0.0, 1.0, 1.0);
            t.vertexUV(xx1, this.y0, 0.0, 1.0, 0.0);
            t.vertexUV(xx0, this.y0, 0.0, 0.0, 0.0);
            t.end();

            t.begin();
            t.color(0x808080, 255);
            t.vertexUV(xx0, yp + barHeight, 0.0, 0.0, 1.0);
            t.vertexUV(xx1, yp + barHeight, 0.0, 1.0, 1.0);
            t.vertexUV(xx1, yp, 0.0, 1.0, 0.0);
            t.vertexUV(xx0, yp, 0.0, 0.0, 0.0);
            t.end();

            t.begin();
            t.color(0xc0c0c0, 255);
            t.vertexUV(xx0, yp + barHeight - 1, 0.0, 0.0, 1.0);
            t.vertexUV(xx1 - 1, yp + barHeight - 1, 0.0, 1.0, 1.0);
            t.vertexUV(xx1 - 1, yp, 0.0, 1.0, 0.0);
            t.vertexUV(xx0, yp, 0.0, 0.0, 0.0);
            t.end();
        }

        this.renderDecorations(xm, ym);

        glEnable(GL_TEXTURE_2D);

        glShadeModel(GL_FLAT);
        glEnable(GL_ALPHA_TEST);
        glDisable(GL_BLEND);
    }
    
    private void renderHoleBackground(final int y0, final int y1, final int a0, final int a1) {
        final Tesselator t = Tesselator.instance;
        glBindTexture(GL_TEXTURE_2D, this.minecraft.textures.loadTexture("/gui/background.png"));
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final float s = 32.0f;
        t.begin();
        t.color(0x404040, a1);
        t.vertexUV(0.0, y1, 0.0, 0.0, y1 / s);
        t.vertexUV(this.width, y1, 0.0, this.width / s, y1 / s);
        t.color(0x404040, a0);
        t.vertexUV(this.width, y0, 0.0, this.width / s, y0 / s);
        t.vertexUV(0.0, y0, 0.0, 0.0, y0 / s);
        t.end();
    }
}
