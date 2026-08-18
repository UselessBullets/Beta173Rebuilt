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
    private SignModel signModel = new SignModel();
    
    public void render(final SignTileEntity sign, final double x, final double y, final double z, final float a) {
        final Tile tile = sign.getTile();

        glPushMatrix();
        final float size = 16 / 24.0f;
        if (tile == Tile.sign) {
            glTranslatef((float)x + 0.5f, (float)y + 0.75f * size, (float)z + 0.5f);
            glRotatef(-(sign.getData() * 360 / 16.0f), 0.0f, 1.0f, 0.0f);
            this.signModel.cube2.visible = true;
        }
        else {
            final int face = sign.getData();
            float rot = 0.0f;

            if (face == 2) rot = 180.0f;
            if (face == 4) rot = 90.0f;
            if (face == 5) rot = -90.0f;

            glTranslatef((float)x + 0.5f, (float)y + 0.75f * size, (float)z + 0.5f);
            glRotatef(-rot, 0.0f, 1.0f, 0.0f);
            glTranslatef(0, -5 / 16.0f, -7 / 16.0f);
            this.signModel.cube2.visible = false;
        }

        this.bindTexture("/item/sign.png");

        glPushMatrix();
        glScalef(size, -size, -size);
        this.signModel.render();
        glPopMatrix();
        final Font font = this.getFont();

        final float s = 1 / 60.0f * size;
        glTranslatef(0.0f, 0.5f * size, 0.07f * size);
        glScalef(s, -s, s);
        glNormal3f(0.0f, 0.0f, -1.0f * s);
        glDepthMask(false);

        final int n4 = 0;
        for (int i = 0; i < sign.messages.length; ++i) {
            String msg = sign.messages[i];
            if (i == sign.selectedLine) {
                msg = "> " + msg + " <";
                font.draw(msg, -font.width(msg) / 2, i * 10 - sign.messages.length * 5, n4);
            }
            else {
                font.draw(msg, -font.width(msg) / 2, i * 10 - sign.messages.length * 5, n4);
            }
        }
        glDepthMask(true);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glPopMatrix();
    }
}
