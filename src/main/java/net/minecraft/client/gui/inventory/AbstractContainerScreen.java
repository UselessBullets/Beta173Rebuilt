// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui.inventory;

import org.lwjgl.input.Keyboard;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.locale.language.Language;
import net.minecraft.world.inventory.Slot;
import net.minecraft.client.Lighting;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.gui.Screen;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

public abstract class AbstractContainerScreen extends Screen
{
    private static ItemRenderer itemRenderer = new ItemRenderer();
    protected int imageWidth = 176;
    protected int imageHeight = 166;
    public AbstractContainerMenu menu;
    
    public AbstractContainerScreen(final AbstractContainerMenu menu) {
        this.menu = menu;
    }
    
    @Override
    public void init() {
        super.init();
        this.minecraft.player.containerMenu = this.menu;
    }
    
    @Override
    public void render(final int xm, final int ym, final float a) {
        this.renderBackground();
        final int xo = (this.width - this.imageWidth) / 2;
        final int yo = (this.height - this.imageHeight) / 2;

        this.renderBg(a);

        glPushMatrix();
        glRotatef(120.0f, 1.0f, 0.0f, 0.0f);
        Lighting.turnOn();
        glPopMatrix();

        glPushMatrix();
        glTranslatef((float)xo, (float)yo, 0.0f);

        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glEnable(GL_RESCALE_NORMAL);

        Slot hoveredSlot = null;
        for (int i = 0; i < this.menu.slots.size(); ++i) {
            final Slot slot = this.menu.slots.get(i);

            this.renderSlot(slot);

            if (this.isHovering(slot, xm, ym)) {
                hoveredSlot = slot;

                glDisable(GL_LIGHTING);
                glDisable(GL_DEPTH_TEST);

                final int x = slot.x;
                final int y = slot.y;
                this.fillGradient(x, y, x + 16, y + 16, 0x80ffffff, 0x80ffffff);
                glEnable(GL_LIGHTING);
                glEnable(GL_DEPTH_TEST);
            }
        }

        final Inventory inventory = this.minecraft.player.inventory;
        if (inventory.getCarried() != null) {
            glTranslatef(0.0f, 0.0f, 32.0f);
            AbstractContainerScreen.itemRenderer.renderGuiItem(this.font, this.minecraft.textures, inventory.getCarried(), xm - xo - 8, ym - yo - 8);
            AbstractContainerScreen.itemRenderer.renderGuiItemDecorations(this.font, this.minecraft.textures, inventory.getCarried(), xm - xo - 8, ym - yo - 8);
        }
        glDisable(GL_RESCALE_NORMAL);
        Lighting.turnOff();

        glDisable(GL_LIGHTING);
        glDisable(GL_DEPTH_TEST);

        this.renderLabels();

        if (inventory.getCarried() == null && hoveredSlot != null && hoveredSlot.hasItem()) {
            final String elementName = (Language.getInstance().getElementName(hoveredSlot.getItem().getDescriptionId())).trim();
            if (elementName.length() > 0) {
                final int x = xm - xo + 12;
                final int y = ym - yo - 12;
                final int width = this.font.width(elementName);
                this.fillGradient(x - 3, y - 3, x + width + 3, y + 8 + 3, 0xc0000000, 0xc0000000);

                this.font.drawShadow(elementName, x, y, 0xffffffff);
            }
        }

        glPopMatrix();

        super.render(xm, ym, a);
        glEnable(GL_LIGHTING);
        glEnable(GL_DEPTH_TEST);
    }
    
    protected void renderLabels() {
    }
    
    protected abstract void renderBg(final float a);
    
    private void renderSlot(final Slot slot) {
        final int x = slot.x;
        final int y = slot.y;
        final ItemInstance item = slot.getItem();

        if (item == null) {
            final int icon = slot.getNoItemIcon();
            if (icon >= 0) {
                glDisable(GL_LIGHTING);
                this.minecraft.textures.bind(this.minecraft.textures.loadTexture("/gui/items.png"));
                this.blit(x, y, icon % 16 * 16, icon / 16 * 16, 16, 16);
                glEnable(GL_LIGHTING);
                return;
            }
        }

        AbstractContainerScreen.itemRenderer.renderGuiItem(this.font, this.minecraft.textures, item, x, y);
        AbstractContainerScreen.itemRenderer.renderGuiItemDecorations(this.font, this.minecraft.textures, item, x, y);
    }
    
    private Slot findSlot(final int x, final int y) {
        for (int i = 0; i < this.menu.slots.size(); ++i) {
            final Slot slot = this.menu.slots.get(i);
            if (this.isHovering(slot, x, y)) return slot;
        }
        return null;
    }
    
    private boolean isHovering(final Slot slot, int xm, int ym) {
        final int xo = (this.width - this.imageWidth) / 2;
        final int yo = (this.height - this.imageHeight) / 2;
        xm -= xo;
        ym -= yo;

        return xm >= slot.x - 1 && xm < slot.x + 16 + 1 && ym >= slot.y - 1 && ym < slot.y + 16 + 1;
    }
    
    @Override
    protected void mouseClicked(final int x, final int y, final int buttonNum) {
        super.mouseClicked(x, y, buttonNum);
        if (buttonNum == 0 || buttonNum == 1) {
            final Slot slot = this.findSlot(x, y);

            final int xo = (this.width - this.imageWidth) / 2;
            final int yo = (this.height - this.imageHeight) / 2;
            final boolean clickedOutside = x < xo || y < yo || x >= xo + this.imageWidth || y >= yo + this.imageHeight;

            int slotId = -1;
            if (slot != null) slotId = slot.index;

            if (clickedOutside) {
                slotId = AbstractContainerMenu.CLICKED_OUTSIDE;
            }

            if (slotId != -1) {
                boolean quickKey = slotId != AbstractContainerMenu.CLICKED_OUTSIDE && (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
                this.minecraft.gameMode.handleInventoryMouseClick(this.menu.containerId, slotId, buttonNum, quickKey, this.minecraft.player);
            }
        }
    }
    
    @Override
    protected void mouseReleased(final int x, final int y, final int buttonNum) {
        if (buttonNum == 0) {}
    }
    
    @Override
    protected void keyPressed(final char eventCharacter, final int eventKey) {
        if (eventKey == Keyboard.KEY_ESCAPE || eventKey == this.minecraft.options.keyBuild.key) {
            this.minecraft.player.closeContainer();
        }
    }
    
    @Override
    public void removed() {
        if (this.minecraft.player == null) return;
        this.minecraft.gameMode.handleCloseInventory(this.menu.containerId, this.minecraft.player);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    @Override
    public void tick() {
        super.tick();
        if (!this.minecraft.player.isAlive() || this.minecraft.player.removed) this.minecraft.player.closeContainer();
    }

}
