// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.tile.Tile;
import org.lwjgl.opengl.GL11;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.client.renderer.TileRenderer;

import static org.lwjgl.opengl.GL11.*;

public class TntRenderer extends EntityRenderer<PrimedTnt>
{
    private TileRenderer tileRenderer;
    
    public TntRenderer() {
        this.tileRenderer = new TileRenderer();
        this.shadowRadius = 0.5f;
    }
    
    public void render(final PrimedTnt entity, final double x, final double y, final double z, final float rot, final float partialTick) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        if (entity.life - partialTick + 1.0f < 10.0f) {
            float n = 1.0f - (entity.life - partialTick + 1.0f) / 10.0f;
            if (n < 0.0f) {
                n = 0.0f;
            }
            if (n > 1.0f) {
                n = 1.0f;
            }
            final float n2 = n * n;
            final float n3 = 1.0f + n2 * n2 * 0.3f;
            GL11.glScalef(n3, n3, n3);
        }
        final float n4 = (1.0f - (entity.life - partialTick + 1.0f) / 100.0f) * 0.8f;
        this.bindTexture("/terrain.png");
        this.tileRenderer.renderTile(Tile.tnt, 0, entity.getBrightness(partialTick));
        if (entity.life / 5 % 2 == 0) {
            GL11.glDisable(GL_TEXTURE_2D);
            GL11.glDisable(GL_LIGHTING);
            GL11.glEnable(GL_BLEND);
            GL11.glBlendFunc(770, 772);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, n4);
            this.tileRenderer.renderTile(Tile.tnt, 0, 1.0f);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glDisable(3042);
            GL11.glEnable(GL_LIGHTING);
            GL11.glEnable(GL_TEXTURE_2D);
        }
        GL11.glPopMatrix();
    }
}
