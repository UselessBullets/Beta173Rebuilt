// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.skins;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.client.gui.ScrolledSelectionList;

class TexturePackSelectScreen_PackList extends ScrolledSelectionList
{
    final /* synthetic */ TexturePackSelectScreen a;
    
    public TexturePackSelectScreen_PackList(final TexturePackSelectScreen ft) {
        this.a = ft;
        super(ft.minecraft, ft.width, ft.height, 32, ft.height - 55 + 4, 36);
    }
    
    @Override
    protected int getNumberOfItems() {
        return this.a.minecraft.skins.getAll().size();
    }
    
    @Override
    protected void selectItem(final int item, final boolean doubleClick) {
        this.a.minecraft.skins.selectSkin(this.a.minecraft.skins.getAll().get(item));
        this.a.minecraft.textures.reloadAll();
    }
    
    @Override
    protected boolean isSelectedItem(final int item) {
        return this.a.minecraft.skins.selected == this.a.minecraft.skins.getAll().get(item);
    }
    
    @Override
    protected int getMaxPosition() {
        return this.getNumberOfItems() * 36;
    }
    
    @Override
    protected void renderBackground() {
        this.a.renderBackground();
    }
    
    @Override
    protected void renderItem(final int i, final int x, final int y, final int h, final Tesselator t) {
        final TexturePack texturePack = this.a.minecraft.skins.getAll().get(i);
        texturePack.bindTexture(this.a.minecraft);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        t.begin();
        t.color(16777215);
        t.vertexUV(x, y + h, 0.0, 0.0, 1.0);
        t.vertexUV(x + 32, y + h, 0.0, 1.0, 1.0);
        t.vertexUV(x + 32, y, 0.0, 1.0, 0.0);
        t.vertexUV(x, y, 0.0, 0.0, 0.0);
        t.end();
        this.a.drawString(this.a.font, texturePack.name, x + 32 + 2, y + 1, 16777215);
        this.a.drawString(this.a.font, texturePack.desc1, x + 32 + 2, y + 12, 8421504);
        this.a.drawString(this.a.font, texturePack.desc2, x + 32 + 2, y + 12 + 10, 8421504);
    }
}
