// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import util.Mth;
import net.minecraft.world.entity.item.Boat;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.Model;

import static org.lwjgl.opengl.GL11.*;

public class BoatRenderer extends EntityRenderer<Boat>
{
    protected Model model = new BoatModel();
    
    public BoatRenderer() {
        this.shadowRadius = 0.5f;
    }
    
    public void render(final Boat boat, final double x, final double y, final double z, final float rot, final float a) {
        glPushMatrix();

        glTranslatef((float)x, (float)y, (float)z);

        glRotatef(180.0f - rot, 0.0f, 1.0f, 0.0f);
        final float hurt = boat.hurtTime - a;
        float dmg = boat.damage - a;
        if (dmg < 0.0f) dmg = 0.0f;
        if (hurt > 0.0f) {
            glRotatef(Mth.sin(hurt) * hurt * dmg / 10.0f * boat.hurtDir, 1.0f, 0.0f, 0.0f);
        }

        this.bindTexture("/terrain.png");
        final float ss = 12 / 16.0f;
        glScalef(ss, ss, ss);
        glScalef(1.0f / ss, 1.0f / ss, 1.0f / ss);

        this.bindTexture("/item/boat.png");
        glScalef(-1.0f, -1.0f, 1.0f);
        this.model.render(0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 1 / 16.0f);
        glPopMatrix();
    }
}
