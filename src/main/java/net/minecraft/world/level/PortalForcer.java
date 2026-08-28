// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

import net.minecraft.world.level.tile.Tile;
import util.Mth;
import net.minecraft.world.entity.Entity;
import java.util.Random;

public class PortalForcer
{
    private Random random = new Random();

    public void force(final Level level, final Entity e) {
        if (this.findPortal(level, e)) {
            return;
        }

        this.createPortal(level, e);
        this.findPortal(level, e);
    }
    
    public boolean findPortal(final Level level, final Entity e) {
        final int r = 16 * 8;
        double closest = -1.0;
        int xTarget = 0;
        int yTarget = 0;
        int zTarget = 0;

        final int xc = Mth.floor(e.x);
        final int zc = Mth.floor(e.z);

        for (int x = xc - r; x <= xc + r; ++x) {
            final double xd = x + 0.5 - e.x;
            for (int z = zc - r; z <= zc + r; ++z) {
                final double zd = z + 0.5 - e.z;
                for (int y = Level.MAX_HEIGHT - 1; y >= Level.MIN_HEIGHT; --y) {
                    if (level.getTile(x, y, z) == Tile.portalTile.id) {
                        while (level.getTile(x, y - 1, z) == Tile.portalTile.id) {
                            --y;
                        }

                        final double yd = y + 0.5 - e.y;
                        final double dist = xd * xd + yd * yd + zd * zd;
                        if (closest < 0.0 || dist < closest) {
                            closest = dist;
                            xTarget = x;
                            yTarget = y;
                            zTarget = z;
                        }
                    }
                }
            }
        }

        if (closest >= 0.0) {
            final int x = xTarget;
            final int y = yTarget;
            final int z = zTarget;

            double xt = x + 0.5;
            double yt = y + 0.5;
            double zt = z + 0.5;

            if (level.getTile(x - 1, y, z) == Tile.portalTile.id) xt -= 0.5;
            if (level.getTile(x + 1, y, z) == Tile.portalTile.id) xt += 0.5;

            if (level.getTile(x, y, z - 1) == Tile.portalTile.id) zt -= 0.5;
            if (level.getTile(x, y, z + 1) == Tile.portalTile.id) zt += 0.5;

            e.moveTo(xt, yt, zt, e.yRot, 0.0f);
            e.xd = e.yd = e.zd = 0.0;
            return true;
        }

        return false;
    }
    
    public boolean createPortal(final Level level, final Entity e) {
        final int r = 16;
        double closest = -1.0;

        final int xc = Mth.floor(e.x);
        final int yc = Mth.floor(e.y);
        final int zc = Mth.floor(e.z);

        int xTarget = xc;
        int yTarget = yc;
        int zTarget = zc;
        int dirTarget = 0;

        final int dirOffs = this.random.nextInt(4);
        for (int x = xc - r; x <= xc + r; ++x) {
            final double xd = x + 0.5 - e.x;
            for (int z = zc - r; z <= zc + r; ++z) {
                final double zd = z + 0.5 - e.z;
                next_first:
                for (int y = Level.MAX_HEIGHT - 1; y >= Level.MIN_HEIGHT; --y) {
                    if (level.isEmptyTile(x, y, z)) {
                        while (y > 0 && level.isEmptyTile(x, y - 1, z)) {
                            --y;
                        }

                        for (int dir = dirOffs; dir < dirOffs + 4; ++dir) {
                            int xa = dir % 2;
                            int za = 1 - xa;

                            if (dir % 4 >= 2) {
                                xa = -xa;
                                za = -za;
                            }

                            for (int b = 0; b < 3; ++b) {
                                for (int s = 0; s < 4; ++s) {
                                    for (int h = -1; h < 4; ++h) {
                                        final int xt = x + (s - 1) * xa + b * za;
                                        final int yt = y + h;
                                        final int zt = z + (s - 1) * za - b * xa;

                                        if (h < 0 && !level.getMaterial(xt, yt, zt).isSolid()) continue next_first;
                                        if (h >= 0 && !level.isEmptyTile(xt, yt, zt)) continue next_first;
                                    }
                                }
                            }

                            final double yd = y + 0.5 - e.y;
                            final double dist = xd * xd + yd * yd + zd * zd;
                            if (closest < 0.0 || dist < closest) {
                                closest = dist;
                                xTarget = x;
                                yTarget = y;
                                zTarget = z;
                                dirTarget = dir % 4;
                            }
                        }
                    }
                }
            }
        }

        if (closest < 0.0) {
            for (int x = xc - r; x <= xc + r; ++x) {
                final double xd = x + 0.5 - e.x;
                for (int z = zc - r; z <= zc + r; ++z) {
                    final double zd = z + 0.5 - e.z;
                    next_second:
                    for (int y = Level.MAX_HEIGHT - 1; y >= Level.MIN_HEIGHT; --y) {
                        if (level.isEmptyTile(x, y, z)) {
                            while (level.isEmptyTile(x, y - 1, z)) {
                                --y;
                            }

                            for (int dir = dirOffs; dir < dirOffs + 2; ++dir) {
                                final int xa = dir % 2;
                                final int za = 1 - xa;
                                for (int s = 0; s < 4; ++s) {
                                    for (int h = -1; h < 4; ++h) {
                                        final int xt = x + (s - 1) * xa;
                                        final int yt = y + h;
                                        final int zt = z + (s - 1) * za;

                                        if (h < 0 && !level.getMaterial(xt, yt, zt).isSolid()) continue next_second;
                                        if (h >= 0 && !level.isEmptyTile(xt, yt, zt)) continue next_second;
                                    }
                                }

                                final double yd = y + 0.5 - e.y;
                                final double dist = xd * xd + yd * yd + zd * zd;
                                if (closest < 0.0 || dist < closest) {
                                    closest = dist;
                                    xTarget = x;
                                    yTarget = y;
                                    zTarget = z;
                                    dirTarget = dir % 2;
                                }
                            }
                        }
                    }
                }
            }
        }

        int dir = dirTarget;

        int x = xTarget;
        int y = yTarget;
        int z = zTarget;

        int xa = dir % 2;
        int za = 1 - xa;

        if (dir % 4 >= 2) {
            xa = -xa;
            za = -za;
        }

        if (closest < 0.0) {
            if (yTarget < 70) yTarget = 70;
            if (yTarget > Level.MAX_HEIGHT - 10) yTarget = Level.MAX_HEIGHT - 10;
            y = yTarget;

            for (int b = -1; b <= 1; ++b) {
                for (int s = 1; s < 3; ++s) {
                    for (int h = -1; h < 3; ++h) {
                        int xt = x + (s - 1) * xa + b * za;
                        int yt = y + h;
                        int zt = z + (s - 1) * za - b * xa;

                        boolean border = h < 0;

                        level.setTile(xt, yt, zt, border ? Tile.obsidian.id : 0);
                    }
                }
            }
        }

        for (int pass = 0; pass < 4; ++pass) {
            level.noNeighborUpdate = true;
            for (int s = 0; s < 4; ++s) {
                for (int h = -1; h < 4; ++h) {
                    int xt = x + (s - 1) * xa;
                    int yt = y + h;
                    int zt = z + (s - 1) * za;

                    boolean border = s == 0 || s == 3 || h == -1 || h == 3;
                    level.setTile(xt, yt, zt, border ? Tile.obsidian.id : Tile.portalTile.id);
                }
            }
            level.noNeighborUpdate = false;

            for (int s = 0; s < 4; ++s) {
                for (int h = -1; h < 4; ++h) {
                    final int xt = x + (s - 1) * xa;
                    final int yt = y + h;
                    final int zt = z + (s - 1) * za;

                    level.updateNeighborsAt(xt, yt, zt, level.getTile(xt, yt, zt));
                }
            }
        }

        return true;
    }
}
