// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.skins;

import net.minecraft.client.gui.Font;
import org.lwjgl.Sys;
import net.minecraft.client.gui.Button;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.SmallButton;
import net.minecraft.locale.language.Language;
import net.minecraft.client.gui.Screen;

public class TexturePackSelectScreen extends Screen
{
    protected Screen lastScreen;
    private int updateIn;
    private String instructions;
    private TexturePackSelectScreen_PackList packList;
    
    public TexturePackSelectScreen(final Screen lastScreen) {
        this.updateIn = -1;
        this.instructions = "";
        this.lastScreen = lastScreen;
    }
    
    @Override
    public void init() {
        final Language instance = Language.getInstance();
        this.buttons.add(new SmallButton(5, this.width / 2 - 154, this.height - 48, instance.getElement("texturePack.openFolder")));
        this.buttons.add(new SmallButton(6, this.width / 2 + 4, this.height - 48, instance.getElement("gui.done")));
        this.minecraft.skins.updateList();
        this.instructions = new File(Minecraft.getWorkingDirectory(), "texturepacks").getAbsolutePath();
        (this.packList = new TexturePackSelectScreen_PackList(this)).init(this.buttons, 7, 8);
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (!button.active) {
            return;
        }
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
    public void render(final int xm, final int ym, final float partialTick) {
        this.packList.render(xm, ym, partialTick);
        if (this.updateIn <= 0) {
            this.minecraft.skins.updateList();
            this.updateIn += 20;
        }
        final Language instance = Language.getInstance();
        this.drawCenteredString(this.font, instance.getElement("texturePack.title"), this.width / 2, 16, 16777215);
        this.drawCenteredString(this.font, instance.getElement("texturePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
        super.render(xm, ym, partialTick);
    }
    
    @Override
    public void tick() {
        super.tick();
        --this.updateIn;
    }
}
