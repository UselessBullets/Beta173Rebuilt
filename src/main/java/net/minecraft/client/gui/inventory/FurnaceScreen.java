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
        this.font.draw("Furnace", 60, 6, 0x404040);
        this.font.draw("Inventory", 8, this.imageHeight - 96 + 2, 0x404040);
    }
    
    @Override
    protected void renderBg(final float partialTick) {
        final int loadTexture = this.minecraft.textures.loadTexture("/gui/furnace.png");
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.textures.bind(loadTexture);
        final int x = (this.width - this.imageWidth) / 2;
        final int y = (this.height - this.imageHeight) / 2;
        this.blit(x, y, 0, 0, this.imageWidth, this.imageHeight);
        if (this.furnace.isLit()) {
            final int litProgress = this.furnace.getLitProgress(12);
            this.blit(x + 56, y + 36 + 12 - litProgress, 176, 12 - litProgress, 14, litProgress + 2);
        }
        this.blit(x + 79, y + 34, 176, 14, this.furnace.getBurnProgress(24) + 1, 16);
    }
}
