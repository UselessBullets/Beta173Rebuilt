// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui.inventory;

import org.lwjgl.opengl.GL11;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerMenu;
import net.minecraft.world.Container;

public class ContainerScreen extends AbstractContainerScreen
{
    private Container inventory;
    private Container container;
    private int containerRows;
    
    public ContainerScreen(final Container inventory, final Container container) {
        super(new ContainerMenu(inventory, container));
        this.containerRows = 0;
        this.inventory = inventory;
        this.container = container;
        this.passEvents = false;
        final int n = 222 - 108;
        this.containerRows = container.getContainerSize() / 9;
        this.imageHeight = n + this.containerRows * 18;
    }
    
    @Override
    protected void renderLabels() {
        this.font.draw(this.container.getName(), 8, 6, 4210752);
        this.font.draw(this.inventory.getName(), 8, this.imageHeight - 96 + 2, 4210752);
    }
    
    @Override
    protected void renderBg(final float partialTick) {
        final int loadTexture = this.minecraft.textures.loadTexture("/gui/container.png");
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.textures.bind(loadTexture);
        final int n = (this.width - this.imageWidth) / 2;
        final int y = (this.height - this.imageHeight) / 2;
        this.blit(n, y, 0, 0, this.imageWidth, this.containerRows * 18 + 17);
        this.blit(n, y + this.containerRows * 18 + 17, 0, 126, this.imageWidth, 96);
    }
}
