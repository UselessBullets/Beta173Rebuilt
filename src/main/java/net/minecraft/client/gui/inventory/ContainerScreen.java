// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui.inventory;

import org.lwjgl.opengl.GL11;
import net.minecraft.world.inventory.ContainerMenu;
import net.minecraft.world.Container;

public class ContainerScreen extends AbstractContainerScreen
{
    private Container inventory;
    private Container container;
    private int containerRows;
    
    public ContainerScreen(final Container inventory, final Container container) {
        super(new ContainerMenu(inventory, container));

        this.inventory = inventory;
        this.container = container;
        this.passEvents = false;

        final int defaultHeight = 222;
        final int noRowHeight = defaultHeight - 6 * 18;
        this.containerRows = container.getContainerSize() / 9;

        this.imageHeight = noRowHeight + this.containerRows * 18;
    }
    
    @Override
    protected void renderLabels() {
        this.font.draw(this.container.getName(), 8, 2 + 2 + 2, 0x404040);
        this.font.draw(this.inventory.getName(), 8, this.imageHeight - 96 + 2, 0x404040);
    }
    
    @Override
    protected void renderBg(final float a) {
        final int tex = this.minecraft.textures.loadTexture("/gui/container.png");
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.textures.bind(tex);
        final int xo = (this.width - this.imageWidth) / 2;
        final int yo = (this.height - this.imageHeight) / 2;
        this.blit(xo, yo, 0, 0, this.imageWidth, this.containerRows * 18 + 17);
        this.blit(xo, yo + this.containerRows * 18 + 17, 0, 222 - 96, this.imageWidth, 96);
    }
}
