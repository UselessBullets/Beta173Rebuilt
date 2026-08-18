// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.world.level.Level;
import util.Mth;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.entity.item.FallingTile;
import net.minecraft.client.renderer.TileRenderer;

import static org.lwjgl.opengl.GL11.*;

public class FallingTileRenderer extends EntityRenderer<FallingTile>
{
    private TileRenderer tileRenderer = new TileRenderer();
    
    public FallingTileRenderer() {
        this.shadowRadius = 0.5f;
    }
    
    public void render(final FallingTile fallingTile, final double x, final double y, final double z, final float rot, final float a) {
        glPushMatrix();
        glTranslatef((float)x, (float)y, (float)z);

        this.bindTexture("/terrain.png");
        final Tile tt = Tile.tiles[fallingTile.tile];

        final Level level = fallingTile.getLevel();
        glDisable(GL_LIGHTING);
        this.tileRenderer.renderBlock(tt, level, Mth.floor(fallingTile.x), Mth.floor(fallingTile.y), Mth.floor(fallingTile.z));
        glEnable(GL_LIGHTING);
        glPopMatrix();
    }
}
