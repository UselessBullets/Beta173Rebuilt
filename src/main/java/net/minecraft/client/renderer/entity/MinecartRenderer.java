// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.client.renderer.TileRenderer;
import util.Mth;
import org.lwjgl.opengl.GL11;
import net.minecraft.world.entity.item.Minecart;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.model.Model;

public class MinecartRenderer extends EntityRenderer<Minecart>
{
    protected Model model;
    
    public MinecartRenderer() {
        this.shadowRadius = 0.5f;
        this.model = new MinecartModel();
    }
    
    public void render(final Minecart entity, double x, double y, double z, float rot, final float partialTick) {
        GL11.glPushMatrix();
        final double x2 = entity.xOld + (entity.x - entity.xOld) * partialTick;
        final double y2 = entity.yOld + (entity.y - entity.yOld) * partialTick;
        final double z2 = entity.zOld + (entity.z - entity.zOld) * partialTick;
        final double offs = 0.30000001192092896;
        final Vec3 pos = entity.getPos(x2, y2, z2);
        float n = entity.xRotO + (entity.xRot - entity.xRotO) * partialTick;
        if (pos != null) {
            Vec3 posOffs = entity.getPosOffs(x2, y2, z2, offs);
            Vec3 posOffs2 = entity.getPosOffs(x2, y2, z2, -offs);
            if (posOffs == null) {
                posOffs = pos;
            }
            if (posOffs2 == null) {
                posOffs2 = pos;
            }
            x += pos.x - x2;
            y += (posOffs.y + posOffs2.y) / 2.0 - y2;
            z += pos.z - z2;
            final Vec3 add = posOffs2.add(-posOffs.x, -posOffs.y, -posOffs.z);
            if (add.length() != 0.0) {
                final Vec3 normalize = add.normalize();
                rot = (float)(Math.atan2(normalize.z, normalize.x) * 180.0 / Math.PI);
                n = (float)(Math.atan(normalize.y) * 73.0);
            }
        }
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(180.0f - rot, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-n, 0.0f, 0.0f, 1.0f);
        final float i = entity.hurtTime - partialTick;
        float n2 = entity.damage - partialTick;
        if (n2 < 0.0f) {
            n2 = 0.0f;
        }
        if (i > 0.0f) {
            GL11.glRotatef(Mth.sin(i) * i * n2 / 10.0f * entity.hurtDir, 1.0f, 0.0f, 0.0f);
        }
        if (entity.type != 0) {
            this.bindTexture("/terrain.png");
            final float n3 = 0.75f;
            GL11.glScalef(n3, n3, n3);
            GL11.glTranslatef(0.0f, 0.3125f, 0.0f);
            GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
            if (entity.type == 1) {
                new TileRenderer().renderTile(Tile.chest, 0, entity.getBrightness(partialTick));
            }
            else if (entity.type == 2) {
                new TileRenderer().renderTile(Tile.furnace, 0, entity.getBrightness(partialTick));
            }
            GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
            GL11.glTranslatef(0.0f, -0.3125f, 0.0f);
            GL11.glScalef(1.0f / n3, 1.0f / n3, 1.0f / n3);
        }
        this.bindTexture("/item/cart.png");
        GL11.glScalef(-1.0f, -1.0f, 1.0f);
        this.model.render(0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        GL11.glPopMatrix();
    }
}
