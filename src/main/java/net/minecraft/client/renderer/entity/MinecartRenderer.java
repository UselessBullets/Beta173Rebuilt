// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.client.renderer.TileRenderer;
import util.Mth;
import net.minecraft.world.entity.item.Minecart;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.model.Model;

import static org.lwjgl.opengl.GL11.*;

public class MinecartRenderer extends EntityRenderer<Minecart>
{
    protected Model model = new MinecartModel();
    
    public MinecartRenderer() {
        this.shadowRadius = 0.5f;
    }
    
    public void render(final Minecart minecart, double x, double y, double z, float rot, final float a) {
        glPushMatrix();

        final double xx = minecart.xOld + (minecart.x - minecart.xOld) * a;
        final double yy = minecart.yOld + (minecart.y - minecart.yOld) * a;
        final double zz = minecart.zOld + (minecart.z - minecart.zOld) * a;

        final double r = 0.3f;

        final Vec3 p = minecart.getPos(xx, yy, zz);

        float xRot = minecart.xRotO + (minecart.xRot - minecart.xRotO) * a;

        if (p != null) {
            Vec3 p0 = minecart.getPosOffs(xx, yy, zz, r);
            Vec3 p1 = minecart.getPosOffs(xx, yy, zz, -r);
            if (p0 == null) p0 = p;
            if (p1 == null) p1 = p;

            x += p.x - xx;
            y += (p0.y + p1.y) / 2.0 - yy;
            z += p.z - zz;

            Vec3 dir = p1.add(-p0.x, -p0.y, -p0.z);
            if (dir.length() == 0.0) {
                // Useless - if is structured like this in source, unsure if something was ever here or not
            } else {
                dir = dir.normalize();
                rot = (float)(Math.atan2(dir.z, dir.x) * 180.0 / Math.PI);
                xRot = (float)(Math.atan(dir.y) * 73.0);
            }
        }
        glTranslatef((float)x, (float)y, (float)z);

        glRotatef(180.0f - rot, 0.0f, 1.0f, 0.0f);
        glRotatef(-xRot, 0.0f, 0.0f, 1.0f);
        final float hurt = minecart.hurtTime - a;
        float dmg = minecart.damage - a;
        if (dmg < 0.0f) dmg = 0.0f;
        if (hurt > 0.0f) {
            glRotatef(Mth.sin(hurt) * hurt * dmg / 10.0f * minecart.hurtDir, 1.0f, 0.0f, 0.0f);
        }

        if (minecart.type != Minecart.RIDEABLE) {
            this.bindTexture("/terrain.png");
            final float ss = 12 / 16.0f;
            glScalef(ss, ss, ss);

            glTranslatef(0.0f, 5.0f / 16.0f, 0.0f);
            glRotatef(90.0f, 0.0f, 1.0f, 0.0f);

            if (minecart.type == Minecart.CHEST) {
                new TileRenderer().renderTile(Tile.chest, 0, minecart.getBrightness(a));
            }
            else if (minecart.type == Minecart.FURNACE) {
                new TileRenderer().renderTile(Tile.furnace, 0, minecart.getBrightness(a));
            }
            glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
            glTranslatef(0.0f, -5.0f / 16.0f, 0.0f);
            glScalef(1.0f / ss, 1.0f / ss, 1.0f / ss);
        }

        this.bindTexture("/item/cart.png");
        glScalef(-1.0f, -1.0f, 1.0f);
        this.model.render(0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 1.0f / 16.0f);
        glPopMatrix();
    }
}
