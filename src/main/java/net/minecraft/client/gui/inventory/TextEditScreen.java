// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui.inventory;

import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.tileentity.TileEntityRenderDispatcher;
import net.minecraft.world.level.tile.Tile;
import org.lwjgl.opengl.GL11;
import net.minecraft.network.packet.SignUpdatePacket;
import net.minecraft.client.gui.Button;
import org.lwjgl.input.Keyboard;
import net.minecraft.world.level.tile.entity.SignTileEntity;
import net.minecraft.client.gui.Screen;

public class TextEditScreen extends Screen
{
    protected String title;
    private SignTileEntity sign;
    private int frame;
    private int line;
    private static final String allowedChars;
    
    public TextEditScreen(final SignTileEntity sign) {
        this.title = "Edit sign message:";
        this.line = 0;
        this.sign = sign;
    }
    
    @Override
    public void init() {
        this.buttons.clear();
        Keyboard.enableRepeatEvents(true);
        this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 120, "Done"));
    }
    
    @Override
    public void removed() {
        Keyboard.enableRepeatEvents(false);
        if (this.minecraft.level.isClientSide) {
            this.minecraft.getConnection().send(new SignUpdatePacket(this.sign.x, this.sign.y, this.sign.z, this.sign.messages));
        }
    }
    
    @Override
    public void tick() {
        ++this.frame;
    }
    
    @Override
    protected void buttonClicked(final Button button) {
        if (!button.active) {
            return;
        }
        if (button.id == 0) {
            this.sign.setChanged();
            this.minecraft.setScreen(null);
        }
    }
    
    @Override
    protected void keyPressed(final char eventCharacter, final int eventKey) {
        if (eventKey == 200) {
            this.line = (this.line - 1 & 0x3);
        }
        if (eventKey == 208 || eventKey == 28) {
            this.line = (this.line + 1 & 0x3);
        }
        if (eventKey == 14 && this.sign.messages[this.line].length() > 0) {
            this.sign.messages[this.line] = this.sign.messages[this.line].substring(0, this.sign.messages[this.line].length() - 1);
        }
        if (TextEditScreen.allowedChars.indexOf(eventCharacter) >= 0 && this.sign.messages[this.line].length() < 15) {
            final StringBuilder sb = new StringBuilder();
            final String[] messages = this.sign.messages;
            final int line = this.line;
            messages[line] = sb.append(messages[line]).append(eventCharacter).toString();
        }
    }
    
    @Override
    public void render(final int xm, final int ym, final float partialTick) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title, this.width / 2, 40, 0xffffff);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)(this.width / 2), 0.0f, 50.0f);
        final float n = 93.75f;
        GL11.glScalef(-n, -n, -n);
        GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
        if (this.sign.getTile() == Tile.sign) {
            GL11.glRotatef(this.sign.getData() * 360 / 16.0f, 0.0f, 1.0f, 0.0f);
            GL11.glTranslatef(0.0f, -1.0625f, 0.0f);
        }
        else {
            final int data = this.sign.getData();
            float n2 = 0.0f;
            if (data == 2) {
                n2 = 180.0f;
            }
            if (data == 4) {
                n2 = 90.0f;
            }
            if (data == 5) {
                n2 = -90.0f;
            }
            GL11.glRotatef(n2, 0.0f, 1.0f, 0.0f);
            GL11.glTranslatef(0.0f, -1.0625f, 0.0f);
        }
        if (this.frame / 6 % 2 == 0) {
            this.sign.selectedLine = this.line;
        }
        TileEntityRenderDispatcher.instance.render(this.sign, -0.5, -0.75, -0.5, 0.0f);
        this.sign.selectedLine = -1;
        GL11.glPopMatrix();
        super.render(xm, ym, partialTick);
    }
    
    static {
        allowedChars = SharedConstants.acceptableLetters;
    }
}
