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
            final float n = 0.5f;
            final float n2 = 0.125f;
            this.setShape(0.5f - n, 0.0f, 0.5f - n2, 0.5f + n, 1.0f, 0.5f + n2);
        }
        else {
            final float n3 = 0.125f;
            final float n4 = 0.5f;
            this.setShape(0.5f - n3, 0.0f, 0.5f - n4, 0.5f + n3, 1.0f, 0.5f + n4);
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
        int n = 0;
        int n2 = 0;
        if (level.getTile(x - 1, y, z) == Tile.obsidian.id || level.getTile(x + 1, y, z) == Tile.obsidian.id) {
            n = 1;
        }
        if (level.getTile(x, y, z - 1) == Tile.obsidian.id || level.getTile(x, y, z + 1) == Tile.obsidian.id) {
            n2 = 1;
        }
        if (n == n2) {
            return false;
        }
        if (level.getTile(x - n, y, z - n2) == 0) {
            x -= n;
            z -= n2;
        }
        for (int i = -1; i <= 2; ++i) {
            for (int j = -1; j <= 3; ++j) {
                final boolean b = i == -1 || i == 2 || j == -1 || j == 3;
                if (i == -1 || i == 2) {
                    if (j == -1) {
                        continue;
                    }
                    if (j == 3) {
                        continue;
                    }
                }
                final int tile = level.getTile(x + n * i, y + j, z + n2 * i);
                if (b) {
                    if (tile != Tile.obsidian.id) {
                        return false;
                    }
                }
                else if (tile != 0 && tile != Tile.fire.id) {
                    return false;
                }
            }
        }
        level.noNeighborUpdate = true;
        for (int k = 0; k < 2; ++k) {
            for (int l = 0; l < 3; ++l) {
                level.setTile(x + n * k, y + l, z + n2 * k, Tile.portalTile.id);
            }
        }
        level.noNeighborUpdate = false;
        return true;
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        int n = 0;
        int n2 = 1;
        if (level.getTile(x - 1, y, z) == this.id || level.getTile(x + 1, y, z) == this.id) {
            n = 1;
            n2 = 0;
        }
        int n3;
        for (n3 = y; level.getTile(x, n3 - 1, z) == this.id; --n3) {}
        if (level.getTile(x, n3 - 1, z) != Tile.obsidian.id) {
            level.setTile(x, y, z, 0);
            return;
        }
        int n4;
        for (n4 = 1; n4 < 4 && level.getTile(x, n3 + n4, z) == this.id; ++n4) {}
        if (n4 != 3 || level.getTile(x, n3 + n4, z) != Tile.obsidian.id) {
            level.setTile(x, y, z, 0);
            return;
        }
        final boolean b = level.getTile(x - 1, y, z) == this.id || level.getTile(x + 1, y, z) == this.id;
        final boolean b2 = level.getTile(x, y, z - 1) == this.id || level.getTile(x, y, z + 1) == this.id;
        if (b && b2) {
            level.setTile(x, y, z, 0);
            return;
        }
        if ((level.getTile(x + n, y, z + n2) != Tile.obsidian.id || level.getTile(x - n, y, z - n2) != this.id) && (level.getTile(x - n, y, z - n2) != Tile.obsidian.id || level.getTile(x + n, y, z + n2) != this.id)) {
            level.setTile(x, y, z, 0);
        }
    }
    
    @Override
    public boolean isFaceVisible(final LevelSource level, final int x, final int y, final int z, final int f) {
        if (level.getTile(x, y, z) == this.id) {
            return false;
        }
        final boolean b = level.getTile(x - 1, y, z) == this.id && level.getTile(x - 2, y, z) != this.id;
        final boolean b2 = level.getTile(x + 1, y, z) == this.id && level.getTile(x + 2, y, z) != this.id;
        final boolean b3 = level.getTile(x, y, z - 1) == this.id && level.getTile(x, y, z - 2) != this.id;
        final boolean b4 = level.getTile(x, y, z + 1) == this.id && level.getTile(x, y, z + 2) != this.id;
        final boolean b5 = b || b2;
        final boolean b6 = b3 || b4;
        return (b5 && f == 4) || (b5 && f == 5) || (b6 && f == 2) || (b6 && f == 3);
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
        if (entity.riding == null && entity.rider == null) {
            entity.handleInsidePortal();
        }
    }
    
    @Override
    public void animateTick(final Level level, final int x, final int y, final int z, final Random random) {
        if (random.nextInt(100) == 0) {
            level.playLocalSound(x + 0.5, y + 0.5, z + 0.5, "portal.portal", 1.0f, random.nextFloat() * 0.4f + 0.8f);
        }
        for (int i = 0; i < 4; ++i) {
            double x2 = x + random.nextFloat();
            final double y2 = y + random.nextFloat();
            double z2 = z + random.nextFloat();
            final int n = random.nextInt(2) * 2 - 1;
            double xd = (random.nextFloat() - 0.5) * 0.5;
            final double yd = (random.nextFloat() - 0.5) * 0.5;
            double zd = (random.nextFloat() - 0.5) * 0.5;
            if (level.getTile(x - 1, y, z) == this.id || level.getTile(x + 1, y, z) == this.id) {
                z2 = z + 0.5 + 0.25 * n;
                zd = random.nextFloat() * 2.0f * n;
            }
            else {
                x2 = x + 0.5 + 0.25 * n;
                xd = random.nextFloat() * 2.0f * n;
            }
            level.addParticle("portal", x2, y2, z2, xd, yd, zd);
        }
    }
}
