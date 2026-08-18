// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.gui.Font;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.entity.SignTileEntity;
import net.minecraft.client.model.SignModel;

import static org.lwjgl.opengl.GL11.*;

public class SignRenderer extends TileEntityRenderer<SignTileEntity>
{
    private SignModel signModel;
    
    public SignRenderer() {
        this.signModel = new SignModel();
    }
    
    public void render(final SignTileEntity entity, final double x, final double y, final double z, final float a) {
        final Tile tile = entity.getTile();
        glPushMatrix();
        final float n = 0.6666667f;
        if (tile == Tile.sign) {
            glTranslatef((float)x + 0.5f, (float)y + 0.75f * n, (float)z + 0.5f);
            glRotatef(-(entity.getData() * 360 / 16.0f), 0.0f, 1.0f, 0.0f);
            this.signModel.cube2.visible = true;
        }
        else {
            final int data = entity.getData();
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
            glTranslatef((float)x + 0.5f, (float)y + 0.75f * n, (float)z + 0.5f);
            glRotatef(-n2, 0.0f, 1.0f, 0.0f);
            glTranslatef(0.0f, -0.3125f, -0.4375f);
            this.signModel.cube2.visible = false;
        }
        this.bindTexture("/item/sign.png");
        glPushMatrix();
        glScalef(n, -n, -n);
        this.signModel.render();
        glPopMatrix();
        final Font font = this.getFont();
        final float n3 = 0.016666668f * n;
        glTranslatef(0.0f, 0.5f * n, 0.07f * n);
        glScalef(n3, -n3, n3);
        glNormal3f(0.0f, 0.0f, -1.0f * n3);
        glDepthMask(false);
        final int n4 = 0;
        for (int i = 0; i < entity.messages.length; ++i) {
            final String str = entity.messages[i];
            if (i == entity.selectedLine) {
                final String string = "> " + str + " <";
                font.draw(string, -font.width(string) / 2, i * 10 - entity.messages.length * 5, n4);
            }
            else {
                font.draw(str, -font.width(str) / 2, i * 10 - entity.messages.length * 5, n4);
            }
        }
        glDepthMask(true);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glPopMatrix();
    }
}
