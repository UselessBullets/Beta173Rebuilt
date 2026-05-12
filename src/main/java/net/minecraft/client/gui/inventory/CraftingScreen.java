// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui.inventory;

import org.lwjgl.opengl.GL11;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Inventory;

public class CraftingScreen extends AbstractContainerScreen
{
    public CraftingScreen(final Inventory inventory, final Level level, final int x, final int y, final int z) {
        super(new CraftingMenu(inventory, level, x, y, z));
    }
    
    @Override
    public void removed() {
        super.removed();
        this.menu.removed(this.minecraft.player);
    }
    
    @Override
    protected void renderLabels() {
        this.font.draw("Crafting", 28, 6, 4210752);
        this.font.draw("Inventory", 8, this.imageHeight - 96 + 2, 4210752);
    }
    
    @Override
    protected void renderBg(final float partialTick) {
        final int loadTexture = this.minecraft.textures.loadTexture("/gui/crafting.png");
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.textures.bind(loadTexture);
        this.blit((this.width - this.imageWidth) / 2, (this.height - this.imageHeight) / 2, 0, 0, this.imageWidth, this.imageHeight);
    }
}
