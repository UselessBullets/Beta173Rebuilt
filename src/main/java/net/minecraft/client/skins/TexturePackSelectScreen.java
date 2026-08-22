// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.skins;

import net.minecraft.client.gui.ScrolledSelectionList;
import net.minecraft.client.renderer.Tesselator;
import org.lwjgl.Sys;
import net.minecraft.client.gui.Button;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.SmallButton;
import net.minecraft.locale.language.Language;
import net.minecraft.client.gui.Screen;

import static org.lwjgl.opengl.GL11.*;

public class TexturePackSelectScreen extends Screen
{
    protected Screen lastScreen;
    private int updateIn = -1;
    private String instructions = "";
    private ScrolledSelectionList packList;
    
    public TexturePackSelectScreen(final Screen lastScreen) {
        this.lastScreen = lastScreen;
    }
    
    @Override
    public void init() {
        final Language instance = Language.getInstance();
        this.buttons.add(new SmallButton(5, this.width / 2 - 154, this.height - 48, instance.getElement("texturePack.openFolder")));
        this.buttons.add(new SmallButton(6, this.width / 2 + 4, this.height - 48, instance.getElement("gui.done")));
        this.minecraft.skins.updateList();
        this.instructions = new File(Minecraft.getWorkingDirectory(), "texturepacks").getAbsolutePath();
        (this.packList = new ScrolledSelectionList(this.minecraft, this.width, this.height, 32, this.height - 55 + 4, 36) {

            @Override
            protected int getNumberOfItems() {
                return TexturePackSelectScreen.this.minecraft.skins.getAll().size();
            }

            @Override
            protected void selectItem(final int item, final boolean doubleClick) {
                TexturePackSelectScreen.this.minecraft.skins.selectSkin(TexturePackSelectScreen.this.minecraft.skins.getAll().get(item));
                TexturePackSelectScreen.this.minecraft.textures.reloadAll();
            }

            @Override
            protected boolean isSelectedItem(final int item) {
                return TexturePackSelectScreen.this.minecraft.skins.selected == TexturePackSelectScreen.this.minecraft.skins.getAll().get(item);
            }

            @Override
            protected int getMaxPosition() {
                return this.getNumberOfItems() * 36;
            }

            @Override
            protected void renderBackground() {
                TexturePackSelectScreen.this.renderBackground();
            }

            @Override
            protected void renderItem(final int i, final int x, final int y, final int h, final Tesselator t) {
                final TexturePack texturePack = TexturePackSelectScreen.this.minecraft.skins.getAll().get(i);
                texturePack.bindTexture(TexturePackSelectScreen.this.minecraft);
                glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

                t.begin();
                t.color(0xffffff);
                t.vertexUV(x, y + h, 0.0, 0.0, 1.0);
                t.vertexUV(x + 32, y + h, 0.0, 1.0, 1.0);
                t.vertexUV(x + 32, y, 0.0, 1.0, 0.0);
                t.vertexUV(x, y, 0.0, 0.0, 0.0);
                t.end();
                
                drawString(TexturePackSelectScreen.this.font, texturePack.name, x + 32 + 2, y + 1, 0xffffff);
                drawString(TexturePackSelectScreen.this.font, texturePack.desc1, x + 32 + 2, y + 12, 0x808080);
                drawString(TexturePackSelectScreen.this.font, texturePack.desc2, x + 32 + 2, y + 12 + 10, 0x808080);
            }
        }).init(this.buttons, 7, 8);
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (!button.active) return;

        if (button.id == 5) {
            Sys.openURL("file://" + this.instructions);
        }
        else if (button.id == 6) {
            this.minecraft.textures.reloadAll();
            this.minecraft.setScreen(this.lastScreen);
        }
        else {
            this.packList.buttonClicked(button);
        }
    }
    
    @Override
    protected void mouseClicked(final int x, final int y, final int buttonNum) {
        super.mouseClicked(x, y, buttonNum);
    }
    
    @Override
    protected void mouseReleased(final int x, final int y, final int buttonNum) {
        super.mouseReleased(x, y, buttonNum);
    }
    
    @Override
    public void render(final int xm, final int ym, final float a) {
        this.packList.render(xm, ym, a);
        if (this.updateIn <= 0) {
            this.minecraft.skins.updateList();
            this.updateIn += 20;
        }
        final Language instance = Language.getInstance();
        this.drawCenteredString(this.font, instance.getElement("texturePack.title"), this.width / 2, 16, 0xffffff);
        this.drawCenteredString(this.font, instance.getElement("texturePack.folderInfo"), this.width / 2 - 77, this.height - 26, 0x808080);
        super.render(xm, ym, a);
    }
    
    @Override
    public void tick() {
        super.tick();
        --this.updateIn;
    }

}
