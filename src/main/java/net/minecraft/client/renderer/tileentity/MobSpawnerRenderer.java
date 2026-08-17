// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.EntityIO;
import net.minecraft.world.entity.Entity;
import org.lwjgl.opengl.GL11;
import net.minecraft.world.level.tile.entity.MobSpawnerTileEntity;
import java.util.HashMap;
import java.util.Map;

public class MobSpawnerRenderer extends TileEntityRenderer<MobSpawnerTileEntity>
{
    private Map<String, Entity> models;
    
    public MobSpawnerRenderer() {
        this.models = new HashMap<>();
    }
    
    public void render(final MobSpawnerTileEntity entity, final double x, final double y, final double z, final float a) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x + 0.5f, (float)y, (float)z + 0.5f);
        Entity entity2 = this.models.get(entity.getEntityId());
        if (entity2 == null) {
            entity2 = EntityIO.newEntity(entity.getEntityId(), null);
            this.models.put(entity.getEntityId(), entity2);
        }
        if (entity2 != null) {
            entity2.setLevel(entity.level);
            final float n = 0.4375f;
            GL11.glTranslatef(0.0f, 0.4f, 0.0f);
            GL11.glRotatef((float)(entity.oSpin + (entity.spin - entity.oSpin) * a) * 10.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(-30.0f, 1.0f, 0.0f, 0.0f);
            GL11.glTranslatef(0.0f, -0.4f, 0.0f);
            GL11.glScalef(n, n, n);
            entity2.moveTo(x, y, z, 0.0f, 0.0f);
            EntityRenderDispatcher.instance.render(entity2, 0.0, 0.0, 0.0, 0.0f, a);
        }
        GL11.glPopMatrix();
    }
}
