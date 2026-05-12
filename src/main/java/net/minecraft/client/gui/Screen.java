// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.client.renderer.Tesselator;
import org.lwjgl.opengl.GL11;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
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
    protected List<Button> buttons;
    public boolean passEvents;
    protected Font font;
    public GuiParticles particles;
    private Button clickedButton;
    
    public Screen() {
        this.buttons = new ArrayList();
        this.passEvents = false;
        this.clickedButton = null;
    }
    
    public void render(final int xm, final int ym, final float partialTick) {
        for (int i = 0; i < this.buttons.size(); ++i) {
            ((Button)this.buttons.get(i)).render(this.minecraft, xm, ym);
        }
    }
    
    protected void keyPressed(final char ch, final int eventKey) {
        if (eventKey == 1) {
            this.minecraft.setScreen(null);
            this.minecraft.grabMouse();
        }
    }
    
    public static String getClipboard() {
        try {
            final Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String)contents.getTransferData(DataFlavor.stringFlavor);
            }
        }
        catch (final Exception ex) {}
        return null;
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
            this.mouseClicked(Mouse.getEventX() * this.width / this.minecraft.width, this.height - Mouse.getEventY() * this.height / this.minecraft.height - 1, Mouse.getEventButton());
        }
        else {
            this.mouseReleased(Mouse.getEventX() * this.width / this.minecraft.width, this.height - Mouse.getEventY() * this.height / this.minecraft.height - 1, Mouse.getEventButton());
        }
    }
    
    public void keyboardEvent() {
        if (Keyboard.getEventKeyState()) {
            if (Keyboard.getEventKey() == 87) {
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
            this.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        }
        else {
            this.renderDirtBackground(vo);
        }
    }
    
    public void renderDirtBackground(final int vo) {
        GL11.glDisable(GL_LIGHTING);
        GL11.glDisable(2912);
        final Tesselator instance = Tesselator.instance;
        GL11.glBindTexture(3553, this.minecraft.textures.loadTexture("/gui/background.png"));
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final float n = 32.0f;
        instance.begin();
        instance.color(4210752);
        instance.vertexUV(0.0, this.height, 0.0, 0.0, this.height / n + vo);
        instance.vertexUV(this.width, this.height, 0.0, this.width / n, this.height / n + vo);
        instance.vertexUV(this.width, 0.0, 0.0, this.width / n, 0 + vo);
        instance.vertexUV(0.0, 0.0, 0.0, 0.0, 0 + vo);
        instance.end();
    }
    
    public boolean isPauseScreen() {
        return true;
    }
    
    public void confirmResult(final boolean result, final int id) {
    }
    
    public void tabPressed() {
    }
}
