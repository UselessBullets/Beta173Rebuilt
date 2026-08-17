// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.client.renderer.TileRenderer;

import static org.lwjgl.opengl.GL11.*;

public class TntRenderer extends EntityRenderer<PrimedTnt>
{
    private TileRenderer tileRenderer = new TileRenderer();
    
    public TntRenderer() {
        this.shadowRadius = 0.5f;
    }
    
    public void render(final PrimedTnt tnt, final double x, final double y, final double z, final float rot, final float a) {
        glPushMatrix();
        glTranslatef((float)x, (float)y, (float)z);
        if (tnt.life - a + 1.0f < 10.0f) {
            float g = 1.0f - (tnt.life - a + 1.0f) / 10.0f;
            if (g < 0.0f) g = 0.0f;
            if (g > 1.0f) g = 1.0f;
            g = g * g;
            g = g * g;
            final float s = 1.0f + g * 0.3f;
            glScalef(s, s, s);
        }

        final float br = (1.0f - (tnt.life - a + 1.0f) / 100.0f) * 0.8f;
        this.bindTexture("/terrain.png");
        this.tileRenderer.renderTile(Tile.tnt, 0, tnt.getBrightness(a));
        if (tnt.life / 5 % 2 == 0) {
            glDisable(GL_TEXTURE_2D);
            glDisable(GL_LIGHTING);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_DST_ALPHA);
            glColor4f(1.0f, 1.0f, 1.0f, br);
            this.tileRenderer.renderTile(Tile.tnt, 0, 1.0f);
            glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            glDisable(GL_BLEND);
            glEnable(GL_LIGHTING);
            glEnable(GL_TEXTURE_2D);
        }
        glPopMatrix();
    }
}
