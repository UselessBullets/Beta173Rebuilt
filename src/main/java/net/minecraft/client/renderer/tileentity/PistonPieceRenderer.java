// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.tileentity;

import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.PistonBaseTile;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Lighting;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.entity.PistonPieceEntity;
import net.minecraft.client.renderer.TileRenderer;

import static org.lwjgl.opengl.GL11.*;

public class PistonPieceRenderer extends TileEntityRenderer<PistonPieceEntity>
{
    private TileRenderer tileRenderer;
    
    public void render(final PistonPieceEntity entity, final double x, final double y, final double z, final float partialTick) {
        final Tile tile = Tile.tiles[entity.getId()];
        if (tile != null && entity.getProgress(partialTick) < 1.0f) {
            final Tesselator instance = Tesselator.instance;
            this.bindTexture("/terrain.png");
            Lighting.turnOff();
            GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL_BLEND);
            GL11.glDisable(GL_CULL_FACE);
            if (Minecraft.useAmbientOcclusion()) {
                GL11.glShadeModel(7425);
            }
            else {
                GL11.glShadeModel(GL_FLAT);
            }
            instance.begin();
            instance.offset((float)x - entity.x + entity.getXOff(partialTick), (float)y - entity.y + entity.getYOff(partialTick), (float)z - entity.z + entity.getZOff(partialTick));
            instance.color(1, 1, 1);
            if (tile == Tile.pistonExtension && entity.getProgress(partialTick) < 0.5f) {
                this.tileRenderer.tesselatePistonArmNoCulling(tile, entity.x, entity.y, entity.z, false);
            }
            else if (entity.isSourcePiston() && !entity.isExtending()) {
                Tile.pistonExtension.setOverrideTopTexture(((PistonBaseTile)tile).getPlatformTexture());
                this.tileRenderer.tesselatePistonArmNoCulling(Tile.pistonExtension, entity.x, entity.y, entity.z, entity.getProgress(partialTick) < 0.5f);
                Tile.pistonExtension.clearOverrideTopTexture();
                instance.offset((float)x - entity.x, (float)y - entity.y, (float)z - entity.z);
                this.tileRenderer.tesselatePistonBaseForceExtended(tile, entity.x, entity.y, entity.z);
            }
            else {
                this.tileRenderer.tesselateInWorldNoCulling(tile, entity.x, entity.y, entity.z);
            }
            instance.offset(0.0, 0.0, 0.0);
            instance.end();
            Lighting.turnOn();
        }
    }
    
    @Override
    public void onNewLevel(final Level level) {
        this.tileRenderer = new TileRenderer(level);
    }
}
