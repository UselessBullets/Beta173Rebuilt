// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.entity.Entity;
import java.util.Random;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class PortalTile extends HalfTransparentTile
{
    public PortalTile(final int id, final int tex) {
        super(id, tex, Material.portal, false);
    }
    
    @Override
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        return null;
    }
    
    @Override
    public void updateShape(final LevelSource level, final int x, final int y, final int z) {
        if (level.getTile(x - 1, y, z) == this.id || level.getTile(x + 1, y, z) == this.id) {
            final float xr = 8 / 16.0f;
            final float yr = 2 / 16.0f;
            this.setShape(0.5f - xr, 0.0f, 0.5f - yr, 0.5f + xr, 1.0f, 0.5f + yr);
        }
        else {
            final float xr = 2 / 16.0f;
            final float yr = 8 / 16.0f;
            this.setShape(0.5f - xr, 0.0f, 0.5f - yr, 0.5f + xr, 1.0f, 0.5f + yr);
        }
    }
    
    @Override
    public boolean isSolidRender() {
        return false;
    }
    
    @Override
    public boolean isCubeShaped() {
        return false;
    }
    
    public boolean trySpawnPortal(final Level level, int x, final int y, int z) {
        int xd = 0;
        int zd = 0;
        if (level.getTile(x - 1, y, z) == Tile.obsidian.id || level.getTile(x + 1, y, z) == Tile.obsidian.id) xd = 1;
        if (level.getTile(x, y, z - 1) == Tile.obsidian.id || level.getTile(x, y, z + 1) == Tile.obsidian.id) zd = 1;

        if (xd == zd) return false;

        if (level.getTile(x - xd, y, z - zd) == 0) {
            x -= xd;
            z -= zd;
        }

        for (int xx = -1; xx <= 2; ++xx) {
            for (int yy = -1; yy <= 3; ++yy) {
                final boolean edge = xx == -1 || xx == 2 || yy == -1 || yy == 3;
                if ((xx == -1 || xx == 2) && (yy == -1 || yy == 3)) continue;

                final int t = level.getTile(x + xd * xx, y + yy, z + zd * xx);

                if (edge) {
                    if (t != Tile.obsidian.id) return false;
                }
                else {
                    if (t != 0 && t != Tile.fire.id) return false;
                }
            }
        }

        level.noNeighborUpdate = true;
        for (int xx = 0; xx < 2; ++xx) {
            for (int yy = 0; yy < 3; ++yy) {
                level.setTile(x + xd * xx, y + yy, z + zd * xx, Tile.portalTile.id);
            }
        }
        level.noNeighborUpdate = false;

        return true;
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        int xd = 0;
        int zd = 1;
        if (level.getTile(x - 1, y, z) == this.id || level.getTile(x + 1, y, z) == this.id) {
            xd = 1;
            zd = 0;
        }

        int yBottom = y;
        while (level.getTile(x, yBottom - 1, z) == this.id) yBottom--;

        if (level.getTile(x, yBottom - 1, z) != Tile.obsidian.id) {
            level.setTile(x, y, z, 0);
            return;
        }

        int height = 1;
        while (height < 4 && level.getTile(x, yBottom + height, z) == this.id) height++;

        if (height != 3 || level.getTile(x, yBottom + height, z) != Tile.obsidian.id) {
            level.setTile(x, y, z, 0);
            return;
        }

        final boolean we = level.getTile(x - 1, y, z) == this.id || level.getTile(x + 1, y, z) == this.id;
        final boolean ns = level.getTile(x, y, z - 1) == this.id || level.getTile(x, y, z + 1) == this.id;
        if (we && ns) {
            level.setTile(x, y, z, 0);
            return;
        }

        if (!(
           (level.getTile(x + xd, y, z + zd) == Tile.obsidian.id && level.getTile(x - xd, y, z - zd) == this.id) ||
           (level.getTile(x - xd, y, z - zd) == Tile.obsidian.id && level.getTile(x + xd, y, z + zd) == this.id)))
        {
            level.setTile(x, y, z, 0);
            return;
        }
    }
    
    @Override
    public boolean shouldRenderFace(final LevelSource level, final int x, final int y, final int z, final int face) {
        if (level.getTile(x, y, z) == this.id) return false;

        final boolean w = level.getTile(x - 1, y, z) == this.id && level.getTile(x - 2, y, z) != this.id;
        final boolean e = level.getTile(x + 1, y, z) == this.id && level.getTile(x + 2, y, z) != this.id;

        final boolean n = level.getTile(x, y, z - 1) == this.id && level.getTile(x, y, z - 2) != this.id;
        final boolean s = level.getTile(x, y, z + 1) == this.id && level.getTile(x, y, z + 2) != this.id;

        final boolean we = w || e;
        final boolean ns = n || s;

        if (we && face == 4) return true;
        if (we && face == 5) return true;
        if (ns && face == 2) return true;
        if (ns && face == 3) return true;

        return false;
    }
    
    @Override
    public int getResourceCount(final Random random) {
        return 0;
    }
    
    @Override
    public int getRenderLayer() {
        return 1;
    }
    
    @Override
    public void entityInside(final Level level, final int x, final int y, final int z, final Entity entity) {
        if (entity.riding == null && entity.rider == null) entity.handleInsidePortal();
    }
    
    @Override
    public void animateTick(final Level level, final int xt, final int yt, final int zt, final Random random) {
        if (random.nextInt(100) == 0) {
            level.playLocalSound(xt + 0.5, yt + 0.5, zt + 0.5, "portal.portal", 1.0f, random.nextFloat() * 0.4f + 0.8f);
        }
        for (int i = 0; i < 4; ++i) {
            double x = xt + random.nextFloat();
            double y = yt + random.nextFloat();
            double z = zt + random.nextFloat();
            double xa = 0;
            double ya = 0;
            double za = 0;
            final int flip = random.nextInt(2) * 2 - 1;
            xa = (random.nextFloat() - 0.5) * 0.5;
            ya = (random.nextFloat() - 0.5) * 0.5;
            za = (random.nextFloat() - 0.5) * 0.5;
            if (level.getTile(xt - 1, yt, zt) == this.id || level.getTile(xt + 1, yt, zt) == this.id) {
                z = zt + 0.5 + 0.25 * flip;
                za = random.nextFloat() * 2.0f * flip;
            }
            else {
                x = xt + 0.5 + 0.25 * flip;
                xa = random.nextFloat() * 2.0f * flip;
            }

            level.addParticle("portal", x, y, z, xa, ya, za);
        }
    }
}
