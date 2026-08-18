// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.tileentity;

import net.minecraft.world.level.tile.entity.TileEntity;
import java.util.Iterator;
import net.minecraft.world.level.tile.entity.PistonPieceEntity;
import net.minecraft.world.level.tile.entity.MobSpawnerTileEntity;
import net.minecraft.world.level.tile.entity.SignTileEntity;
import java.util.HashMap;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.client.renderer.Textures;
import net.minecraft.client.gui.Font;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class TileEntityRenderDispatcher
{
    private Map<Class<? extends TileEntity>, TileEntityRenderer<? extends TileEntity>> renderers = new HashMap<>();
    public static TileEntityRenderDispatcher instance = new TileEntityRenderDispatcher();
    private Font font;
    public static double xOff, yOff, zOff;
    public Textures textures;
    public Level level;
    public Mob player;
    public float playerRotY, playerRotX;
    public double xPlayer, yPlayer, zPlayer;
    
    private TileEntityRenderDispatcher() {
        this.renderers.put(SignTileEntity.class, new SignRenderer());
        this.renderers.put(MobSpawnerTileEntity.class, new MobSpawnerRenderer());
        this.renderers.put(PistonPieceEntity.class, new PistonPieceRenderer());

        for (TileEntityRenderer<? extends TileEntity> tileEntityRenderer : this.renderers.values()) {
            tileEntityRenderer.bindTexture(this);
        }
    }
    
    public <T extends TileEntity> TileEntityRenderer<T> getRenderer(final Class<? extends TileEntity> e) {
        TileEntityRenderer<? extends TileEntity> r = this.renderers.get(e);
        if (r == null && e != TileEntity.class) {
            r = this.getRenderer((Class<? extends TileEntity>) e.getSuperclass());
            this.renderers.put(e, r);
        }
        return (TileEntityRenderer<T>) r;
    }
    
    public boolean hasRenderer(final TileEntity e) {
        return this.getRenderer(e) != null;
    }
    
    public <T extends TileEntity> TileEntityRenderer<T> getRenderer(final TileEntity e) {
        if (e == null) return null;
        return this.getRenderer(e.getClass());
    }
    
    public void prepare(final Level level, final Textures textures, final Font font, final Mob player, final float a) {
        if (this.level != level) {
            this.setLevel(level);
        }
        this.textures = textures;
        this.player = player;
        this.font = font;

        this.playerRotY = player.yRotO + (player.yRot - player.yRotO) * a;
        this.playerRotX = player.xRotO + (player.xRot - player.xRotO) * a;

        this.xPlayer = player.xOld + (player.x - player.xOld) * a;
        this.yPlayer = player.yOld + (player.y - player.yOld) * a;
        this.zPlayer = player.zOld + (player.z - player.zOld) * a;
    }
    
    public void render(final TileEntity e, final float a) {
        if (e.distanceToSqr(this.xPlayer, this.yPlayer, this.zPlayer) < 64 * 64) {
            final float br = this.level.getBrightness(e.x, e.y, e.z);
            glColor3f(br, br, br);
            this.render(e, e.x - TileEntityRenderDispatcher.xOff, e.y - TileEntityRenderDispatcher.yOff, e.z - TileEntityRenderDispatcher.zOff, a);
        }
    }
    
    public void render(final TileEntity e, final double x, final double y, final double z, final float a) {
        final TileEntityRenderer<TileEntity> renderer = this.getRenderer(e);
        if (renderer != null) {
            renderer.render(e, x, y, z, a);
        }
    }
    
    public void setLevel(final Level level) {
        this.level = level;
        for (final TileEntityRenderer<? extends TileEntity> tileEntityRenderer : this.renderers.values()) {
            if (tileEntityRenderer != null) tileEntityRenderer.onNewLevel(level);
        }
    }
    
    public Font getFont() {
        return this.font;
    }

}
