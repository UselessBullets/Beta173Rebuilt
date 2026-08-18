// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.EntityIO;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.tile.entity.MobSpawnerTileEntity;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class MobSpawnerRenderer extends TileEntityRenderer<MobSpawnerTileEntity>
{
    private Map<String, Entity> models = new HashMap<>();
    
    public void render(final MobSpawnerTileEntity spawner, final double x, final double y, final double z, final float a) {
        glPushMatrix();
        glTranslatef((float)x + 0.5f, (float)y, (float)z + 0.5f);

        Entity e = this.models.get(spawner.getEntityId());
        if (e == null) {
            e = EntityIO.newEntity(spawner.getEntityId(), null);
            this.models.put(spawner.getEntityId(), e);
        }
        if (e != null) {
            e.setLevel(spawner.level);
            final float n = 0.4375f;
            glTranslatef(0.0f, 0.4f, 0.0f);
            glRotatef((float)(spawner.oSpin + (spawner.spin - spawner.oSpin) * a) * 10.0f, 0.0f, 1.0f, 0.0f);
            glRotatef(-30.0f, 1.0f, 0.0f, 0.0f);
            glTranslatef(0.0f, -0.4f, 0.0f);
            glScalef(n, n, n);
            e.moveTo(x, y, z, 0.0f, 0.0f);
            EntityRenderDispatcher.instance.render(e, 0.0, 0.0, 0.0, 0.0f, a);
        }
        glPopMatrix();
    }
}
