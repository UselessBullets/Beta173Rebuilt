// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import util.Mth;
import org.lwjgl.opengl.GL11;
import net.minecraft.world.entity.item.Boat;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.Model;

public class BoatRenderer extends EntityRenderer<Boat>
{
    protected Model model;
    
    public BoatRenderer() {
        this.shadowRadius = 0.5f;
        this.model = new BoatModel();
    }
    
    public void render(final Boat entity, final double x, final double y, final double z, final float rot, final float a) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(180.0f - rot, 0.0f, 1.0f, 0.0f);
        final float i = entity.hurtTime - a;
        float n = entity.damage - a;
        if (n < 0.0f) {
            n = 0.0f;
        }
        if (i > 0.0f) {
            GL11.glRotatef(Mth.sin(i) * i * n / 10.0f * entity.hurtDir, 1.0f, 0.0f, 0.0f);
        }
        this.bindTexture("/terrain.png");
        final float n2 = 0.75f;
        GL11.glScalef(n2, n2, n2);
        GL11.glScalef(1.0f / n2, 1.0f / n2, 1.0f / n2);
        this.bindTexture("/item/boat.png");
        GL11.glScalef(-1.0f, -1.0f, 1.0f);
        this.model.render(0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        GL11.glPopMatrix();
    }
}
