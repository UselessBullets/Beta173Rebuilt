// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.tileentity;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.PistonBaseTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Lighting;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.entity.PistonPieceEntity;
import net.minecraft.client.renderer.TileRenderer;

import static org.lwjgl.opengl.GL11.*;

public class PistonPieceRenderer extends TileEntityRenderer<PistonPieceEntity>
{
    private TileRenderer tileRenderer;
    
    public void render(final PistonPieceEntity entity, final double x, final double y, final double z, final float a) {
        final Tile tile = Tile.tiles[entity.getId()];
        if (tile != null && entity.getProgress(a) < 1.0f) {
            final Tesselator t = Tesselator.instance;
            this.bindTexture("/terrain.png");

            Lighting.turnOff();

            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_BLEND);
            glDisable(GL_CULL_FACE);
            if (Minecraft.useAmbientOcclusion()) {
                glShadeModel(GL_SMOOTH);
            }
            else {
                glShadeModel(GL_FLAT);
            }

            t.begin();
            t.offset((float)x - entity.x + entity.getXOff(a), (float)y - entity.y + entity.getYOff(a), (float)z - entity.z + entity.getZOff(a));
            t.color(1, 1, 1);
            if (tile == Tile.pistonExtension && entity.getProgress(a) < 0.5f) {
                // extending arms may appear through the base block
                this.tileRenderer.tesselatePistonArmNoCulling(tile, entity.x, entity.y, entity.z, false);
            }
            else if (entity.isSourcePiston() && !entity.isExtending()) {
                // special case for withdrawing the arm back into the base
                Tile.pistonExtension.setOverrideTopTexture(((PistonBaseTile)tile).getPlatformTexture());
                this.tileRenderer.tesselatePistonArmNoCulling(Tile.pistonExtension, entity.x, entity.y, entity.z, entity.getProgress(a) < 0.5f);
                Tile.pistonExtension.clearOverrideTopTexture();

                t.offset((float)x - entity.x, (float)y - entity.y, (float)z - entity.z);
                this.tileRenderer.tesselatePistonBaseForceExtended(tile, entity.x, entity.y, entity.z);
            }
            else {
                this.tileRenderer.tesselateInWorldNoCulling(tile, entity.x, entity.y, entity.z);
            }
            t.offset(0.0, 0.0, 0.0);
            t.end();

            Lighting.turnOn();
        }
    }
    
    @Override
    public void onNewLevel(final Level level) {
        this.tileRenderer = new TileRenderer(level);
    }
}
