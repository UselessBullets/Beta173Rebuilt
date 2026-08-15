// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui.inventory;

import org.lwjgl.opengl.GL11;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.tile.entity.FurnaceTileEntity;

public class FurnaceScreen extends AbstractContainerScreen
{
    private FurnaceTileEntity furnace;
    
    public FurnaceScreen(final Inventory inventory, final FurnaceTileEntity furnace) {
        super(new FurnaceMenu(inventory, furnace));
        this.furnace = furnace;
    }
    
    @Override
    protected void renderLabels() {
        this.font.draw("Furnace", 16 + 4 + 40, 2 + 2 + 2, 0x404040);
        this.font.draw("Inventory", 8, this.imageHeight - 96 + 2, 0x404040);
    }
    
    @Override
    protected void renderBg(final float partialTick) {
        final int tex = this.minecraft.textures.loadTexture("/gui/furnace.png");
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.textures.bind(tex);
        final int xo = (this.width - this.imageWidth) / 2;
        final int yo = (this.height - this.imageHeight) / 2;
        this.blit(xo, yo, 0, 0, this.imageWidth, this.imageHeight);
        if (this.furnace.isLit()) {
            final int p = this.furnace.getLitProgress(12);
            this.blit(xo + 56, yo + 36 + 12 - p, 176, 12 - p, 14, p + 2);
        }

        final int p = this.furnace.getBurnProgress(24);
        this.blit(xo + 79, yo + 34, 176, 14, p + 1, 16);
    }
}
