// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.client.renderer.Tesselator;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;

import static org.lwjgl.opengl.GL11.*;

public class Screen extends GuiComponent
{
    protected Minecraft minecraft;
    public int width;
    public int height;
    protected List<Button> buttons = new ArrayList<>();
    public boolean passEvents = false;
    protected Font font;
    public GuiParticles particles;
    private Button clickedButton = null;

    public void render(final int xm, final int ym, final float a) {
        for (int i = 0; i < this.buttons.size(); ++i) {
            this.buttons.get(i).render(this.minecraft, xm, ym);
        }
    }
    
    protected void keyPressed(final char eventCharacter, final int eventKey) {
        if (eventKey == Keyboard.KEY_ESCAPE) {
            this.minecraft.setScreen(null);
            this.minecraft.grabMouse();
        }
    }
    
    public static String getClipboard() {
        try {
            final Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String)t.getTransferData(DataFlavor.stringFlavor);
            }
        }
        catch (final Exception ignored) {}
        return null;
    }

    public static void setClipboard(String str) {
        try {
            StringSelection ss = new StringSelection(str);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
        } catch (Exception ignored) {}
    }

    protected void mouseClicked(final int x, final int y, final int buttonNum) {
        if (buttonNum == 0) {
            for (int i = 0; i < this.buttons.size(); ++i) {
                final Button button = this.buttons.get(i);
                if (button.clicked(this.minecraft, x, y)) {
                    this.clickedButton = button;
                    this.minecraft.soundEngine.playUI("random.click", 1.0f, 1.0f);
                    this.buttonClicked(button);
                }
            }
        }
    }
    
    protected void mouseReleased(final int x, final int y, final int buttonNum) {
        if (this.clickedButton != null && buttonNum == 0) {
            this.clickedButton.released(x, y);
            this.clickedButton = null;
        }
    }
    
    protected void buttonClicked(final Button button) {
    }
    
    public void init(final Minecraft minecraft, final int width, final int height) {
        this.particles = new GuiParticles(minecraft);
        this.minecraft = minecraft;
        this.font = minecraft.font;
        this.width = width;
        this.height = height;
        this.buttons.clear();
        this.init();
    }
    
    public void init() {
    }
    
    public void updateEvents() {
        while (Mouse.next()) {
            this.mouseEvent();
        }
        while (Keyboard.next()) {
            this.keyboardEvent();
        }
    }
    
    public void mouseEvent() {
        if (Mouse.getEventButtonState()) {
            int xm = Mouse.getEventX() * this.width / this.minecraft.width;
            int ym = this.height - Mouse.getEventY() * this.height / this.minecraft.height - 1;
            this.mouseClicked(xm, ym, Mouse.getEventButton());
        }
        else {
            int xm = Mouse.getEventX() * this.width / this.minecraft.width;
            int ym = this.height - Mouse.getEventY() * this.height / this.minecraft.height - 1;
            this.mouseReleased(xm, ym, Mouse.getEventButton());
        }
    }
    
    public void keyboardEvent() {
        if (Keyboard.getEventKeyState()) {
            if (Keyboard.getEventKey() == Keyboard.KEY_F11) {
                this.minecraft.toggleFullScreen();
                return;
            }
            this.keyPressed(Keyboard.getEventCharacter(), Keyboard.getEventKey());
        }
    }
    
    public void tick() {
    }
    
    public void removed() {
    }
    
    public void renderBackground() {
        this.renderBackground(0);
    }
    
    public void renderBackground(final int vo) {
        if (this.minecraft.level != null) {
            this.fillGradient(0, 0, this.width, this.height, 0xc0101010, 0xd0101010);
        }
        else {
            this.renderDirtBackground(vo);
        }
    }
    
    public void renderDirtBackground(final int vo) {
        glDisable(GL_LIGHTING);
        glDisable(GL_FOG);
        final Tesselator t = Tesselator.instance;
        glBindTexture(GL_TEXTURE_2D, this.minecraft.textures.loadTexture("/gui/background.png"));
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final float s = 32.0f;
        t.begin();
        t.color(0x404040);
        t.vertexUV(0.0, this.height, 0.0, 0.0, this.height / s + vo);
        t.vertexUV(this.width, this.height, 0.0, this.width / s, this.height / s + vo);
        t.vertexUV(this.width, 0.0, 0.0, this.width / s, 0 + vo);
        t.vertexUV(0.0, 0.0, 0.0, 0.0, 0 + vo);
        t.end();
    }
    
    public boolean isPauseScreen() {
        return true;
    }
    
    public void confirmResult(final boolean result, final int id) {
    }
    
    public void tabPressed() {
    }
}
