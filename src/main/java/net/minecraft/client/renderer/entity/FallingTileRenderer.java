// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import util.Mth;
import net.minecraft.world.level.tile.Tile;
import org.lwjgl.opengl.GL11;
import net.minecraft.world.entity.item.FallingTile;
import net.minecraft.client.renderer.TileRenderer;

import static org.lwjgl.opengl.GL11.*;

public class FallingTileRenderer extends EntityRenderer<FallingTile>
{
    private TileRenderer tileRenderer;
    
    public FallingTileRenderer() {
        this.tileRenderer = new TileRenderer();
        this.shadowRadius = 0.5f;
    }
    
    public void render(final FallingTile entity, final double x, final double y, final double z, final float rot, final float partialTick) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        this.bindTexture("/terrain.png");
        final Tile tt = Tile.tiles[entity.tile];
        final Level level = entity.getLevel();
        GL11.glDisable(GL_LIGHTING);
        this.tileRenderer.renderBlock(tt, level, Mth.floor(entity.x), Mth.floor(entity.y), Mth.floor(entity.z));
        GL11.glEnable(GL_LIGHTING);
        GL11.glPopMatrix();
    }
}
