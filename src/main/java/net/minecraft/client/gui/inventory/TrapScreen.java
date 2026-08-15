// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui.inventory;

import org.lwjgl.opengl.GL11;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.TrapMenu;
import net.minecraft.world.level.tile.entity.DispenserTileEntity;
import net.minecraft.world.entity.player.Inventory;

public class TrapScreen extends AbstractContainerScreen
{
    public TrapScreen(final Inventory inventory, final DispenserTileEntity trap) {
        super(new TrapMenu(inventory, trap));
    }
    
    @Override
    protected void renderLabels() {
        this.font.draw("Dispenser", 60, 6, 0x404040);
        this.font.draw("Inventory", 8, this.imageHeight - 96 + 2, 0x404040);
    }
    
    @Override
    protected void renderBg(final float partialTick) {
        final int loadTexture = this.minecraft.textures.loadTexture("/gui/trap.png");
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.textures.bind(loadTexture);
        this.blit((this.width - this.imageWidth) / 2, (this.height - this.imageHeight) / 2, 0, 0, this.imageWidth, this.imageHeight);
    }
}
