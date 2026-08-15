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
    private static final String allowedChars = SharedConstants.acceptableLetters;
    
    public TextEditScreen(final SignTileEntity sign) {
        this.line = 0;
        this.title = "Edit sign message:";

        this.sign = sign;
    }
    
    @Override
    public void init() {
        this.buttons.clear();
        Keyboard.enableRepeatEvents(true);
        this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 24 * 5, "Done"));
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
        if (eventKey == Keyboard.KEY_UP) this.line = (this.line - 1 & 0x3);
        if (eventKey == Keyboard.KEY_DOWN || eventKey == Keyboard.KEY_RETURN) this.line = (this.line + 1 & 0x3);

        String temp = this.sign.messages[this.line];
        if (eventKey == 14 && temp.length() > 0) {
            temp = temp.substring(0, temp.length() - 1);
        }
        if (TextEditScreen.allowedChars.indexOf(eventCharacter) >= 0 && temp.length() < 15) {
            temp += eventCharacter;
        }
        this.sign.messages[this.line] = temp;
    }
    
    @Override
    public void render(final int xm, final int ym, final float partialTick) {
        this.renderBackground();

        this.drawCenteredString(this.font, this.title, this.width / 2, 40, 0xffffff);

        GL11.glPushMatrix();
        GL11.glTranslatef((float)(this.width / 2), 0.0f, 50.0f);
        final float ss = 60 / (16 / 25.0f);
        GL11.glScalef(-ss, -ss, -ss);
        GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);

        Tile tile = this.sign.getTile();

        if (tile == Tile.sign) {
            final float rot = (this.sign.getData() * 360) / 16.0f;
            GL11.glRotatef(rot, 0.0f, 1.0f, 0.0f);
            GL11.glTranslatef(0.0f, -17.0f / 16.0f, 0.0f);
        }
        else {
            final int face = this.sign.getData();
            float rot = 0.0f;

            if (face == 2) rot = 180.0f;
            if (face == 4) rot = 90.0f;
            if (face == 5) rot = -90.0f;
            GL11.glRotatef(rot, 0.0f, 1.0f, 0.0f);
            GL11.glTranslatef(0.0f, -17.0f / 16.0f, 0.0f);
        }

        if (this.frame / 6 % 2 == 0) this.sign.selectedLine = this.line;

        TileEntityRenderDispatcher.instance.render(this.sign, -0.5, -0.75, -0.5, 0.0f);
        this.sign.selectedLine = -1;

        GL11.glPopMatrix();

        super.render(xm, ym, partialTick);
    }

}
