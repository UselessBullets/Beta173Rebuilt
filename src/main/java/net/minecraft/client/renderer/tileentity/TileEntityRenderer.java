// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.gui.Font;
import net.minecraft.world.level.Level;
import net.minecraft.client.renderer.Textures;
import net.minecraft.world.level.tile.entity.TileEntity;

public abstract class TileEntityRenderer<T extends TileEntity>
{
    protected TileEntityRenderDispatcher tileEntityRenderDispatcher;
    
    public abstract void render(final T entity, final double x, final double y, final double z, final float a);
    
    protected void bindTexture(final String resourceName) {
        final Textures textures = this.tileEntityRenderDispatcher.textures;
        textures.bind(textures.loadTexture(resourceName));
    }
    
    public void bindTexture(final TileEntityRenderDispatcher tileEntityRenderDispatcher) {
        this.tileEntityRenderDispatcher = tileEntityRenderDispatcher;
    }
    
    public void onNewLevel(final Level level) {
    }
    
    public Font getFont() {
        return this.tileEntityRenderDispatcher.getFont();
    }
}
