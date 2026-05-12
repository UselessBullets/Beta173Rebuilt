// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui.inventory;

import net.minecraft.world.entity.player.Player;
import org.lwjgl.input.Keyboard;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.locale.language.Language;
import net.minecraft.world.inventory.Slot;
import net.minecraft.client.Lighting;
import org.lwjgl.opengl.GL11;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.gui.Screen;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

public abstract class AbstractContainerScreen extends Screen
{
    private static ItemRenderer itemRenderer;
    protected int imageWidth;
    protected int imageHeight;
    public AbstractContainerMenu menu;
    
    public AbstractContainerScreen(final AbstractContainerMenu menu) {
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.menu = menu;
    }
    
    @Override
    public void init() {
        super.init();
        this.minecraft.player.containerMenu = this.menu;
    }
    
    @Override
    public void render(final int xm, final int ym, final float partialTick) {
        this.renderBackground();
        final int n = (this.width - this.imageWidth) / 2;
        final int n2 = (this.height - this.imageHeight) / 2;
        this.renderBg(partialTick);
        GL11.glPushMatrix();
        GL11.glRotatef(120.0f, 1.0f, 0.0f, 0.0f);
        Lighting.turnOn();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef((float)n, (float)n2, 0.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(GL_RESCALE_NORMAL);
        Slot slot = null;
        for (int i = 0; i < this.menu.slots.size(); ++i) {
            final Slot slot2 = this.menu.slots.get(i);
            this.renderSlot(slot2);
            if (this.isHovering(slot2, xm, ym)) {
                slot = slot2;
                GL11.glDisable(GL_LIGHTING);
                GL11.glDisable(GL_DEPTH_TEST);
                final int x = slot2.x;
                final int y = slot2.y;
                this.fillGradient(x, y, x + 16, y + 16, -2130706433, -2130706433);
                GL11.glDisable(GL_LIGHTING);
                GL11.glEnable(GL_DEPTH_TEST);
            }
        }
        final Inventory inventory = this.minecraft.player.inventory;
        if (inventory.getCarried() != null) {
            GL11.glTranslatef(0.0f, 0.0f, 32.0f);
            AbstractContainerScreen.itemRenderer.renderGuiItem(this.font, this.minecraft.textures, inventory.getCarried(), xm - n - 8, ym - n2 - 8);
            AbstractContainerScreen.itemRenderer.renderGuiItemDecorations(this.font, this.minecraft.textures, inventory.getCarried(), xm - n - 8, ym - n2 - 8);
        }
        GL11.glDisable(32826);
        Lighting.turnOff();
        GL11.glDisable(GL_LIGHTING);
        GL11.glDisable(GL_DEPTH_TEST);
        this.renderLabels();
        if (inventory.getCarried() == null && slot != null && slot.hasItem()) {
            final String trim = ("" + Language.getInstance().getElementName(slot.getItem().getDescriptionId())).trim();
            if (trim.length() > 0) {
                final int x2 = xm - n + 12;
                final int y2 = ym - n2 - 12;
                this.fillGradient(x2 - 3, y2 - 3, x2 + this.font.width(trim) + 3, y2 + 8 + 3, -1073741824, -1073741824);
                this.font.drawShadow(trim, x2, y2, -1);
            }
        }
        GL11.glPopMatrix();
        super.render(xm, ym, partialTick);
        GL11.glDisable(GL_LIGHTING);
        GL11.glEnable(GL_DEPTH_TEST);
    }
    
    protected void renderLabels() {
    }
    
    protected abstract void renderBg(final float partialTick);
    
    private void renderSlot(final Slot slot) {
        final int x = slot.x;
        final int y = slot.y;
        final ItemInstance item = slot.getItem();
        if (item == null) {
            final int noItemIcon = slot.getNoItemIcon();
            if (noItemIcon >= 0) {
                GL11.glDisable(GL_LIGHTING);
                this.minecraft.textures.bind(this.minecraft.textures.loadTexture("/gui/items.png"));
                this.blit(x, y, noItemIcon % 16 * 16, noItemIcon / 16 * 16, 16, 16);
                GL11.glDisable(GL_LIGHTING);
                return;
            }
        }
        AbstractContainerScreen.itemRenderer.renderGuiItem(this.font, this.minecraft.textures, item, x, y);
        AbstractContainerScreen.itemRenderer.renderGuiItemDecorations(this.font, this.minecraft.textures, item, x, y);
    }
    
    private Slot findSlot(final int x, final int y) {
        for (int i = 0; i < this.menu.slots.size(); ++i) {
            final Slot slot = this.menu.slots.get(i);
            if (this.isHovering(slot, x, y)) {
                return slot;
            }
        }
        return null;
    }
    
    private boolean isHovering(final Slot slot, int x, int y) {
        final int n = (this.width - this.imageWidth) / 2;
        final int n2 = (this.height - this.imageHeight) / 2;
        x -= n;
        y -= n2;
        return x >= slot.x - 1 && x < slot.x + 16 + 1 && y >= slot.y - 1 && y < slot.y + 16 + 1;
    }
    
    @Override
    protected void mouseClicked(final int x, final int y, final int buttonNum) {
        super.mouseClicked(x, y, buttonNum);
        if (buttonNum == 0 || buttonNum == 1) {
            final Slot slot = this.findSlot(x, y);
            final int n = (this.width - this.imageWidth) / 2;
            final int n2 = (this.height - this.imageHeight) / 2;
            final boolean b = x < n || y < n2 || x >= n + this.imageWidth || y >= n2 + this.imageHeight;
            int index = -1;
            if (slot != null) {
                index = slot.index;
            }
            if (b) {
                index = -999;
            }
            if (index != -1) {
                this.minecraft.gameMode.handleInventoryMouseClick(this.menu.containerId, index, buttonNum, index != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)), this.minecraft.player);
            }
        }
    }
    
    @Override
    protected void mouseReleased(final int x, final int y, final int buttonNum) {
        if (buttonNum == 0) {}
    }
    
    @Override
    protected void keyPressed(final char ch, final int eventKey) {
        if (eventKey == 1 || eventKey == this.minecraft.options.keyBuild.key) {
            this.minecraft.player.closeContainer();
        }
    }
    
    @Override
    public void removed() {
        if (this.minecraft.player == null) {
            return;
        }
        this.minecraft.gameMode.handleCloseInventory(this.menu.containerId, this.minecraft.player);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    @Override
    public void tick() {
        super.tick();
        if (!this.minecraft.player.isAlive() || this.minecraft.player.removed) {
            this.minecraft.player.closeContainer();
        }
    }
    
    static {
        AbstractContainerScreen.itemRenderer = new ItemRenderer();
    }
}
