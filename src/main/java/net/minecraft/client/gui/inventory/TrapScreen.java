// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui.inventory;

import net.minecraft.world.inventory.TrapMenu;
import net.minecraft.world.level.tile.entity.DispenserTileEntity;
import net.minecraft.world.entity.player.Inventory;

import static org.lwjgl.opengl.GL11.*;

public class TrapScreen extends AbstractContainerScreen
{
    public TrapScreen(final Inventory inventory, final DispenserTileEntity trap) {
        super(new TrapMenu(inventory, trap));
    }
    
    @Override
    protected void renderLabels() {
        this.font.draw("Dispenser", 16 + 4 + 40, 2 + 2 + 2, 0x404040);
        this.font.draw("Inventory", 8, this.imageHeight - 96 + 2, 0x404040);
    }
    
    @Override
    protected void renderBg(final float a) {
        final int tex = this.minecraft.textures.loadTexture("/gui/trap.png");
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.textures.bind(tex);
        final int xo = (this.width - this.imageWidth) / 2;
        final int yo = (this.height - this.imageHeight) / 2;
        this.blit(xo, yo, 0, 0, this.imageWidth, this.imageHeight);
    }
}
