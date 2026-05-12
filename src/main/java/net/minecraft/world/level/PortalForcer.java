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
    private Random random;
    
    public PortalForcer() {
        this.random = new Random();
    }
    
    public void force(final Level level, final Entity e) {
        if (this.findPortal(level, e)) {
            return;
        }
        this.createPortal(level, e);
        this.findPortal(level, e);
    }
    
    public boolean findPortal(final Level level, final Entity e) {
        final int n = 128;
        double n2 = -1.0;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        final int floor = Mth.floor(e.x);
        final int floor2 = Mth.floor(e.z);
        for (int i = floor - n; i <= floor + n; ++i) {
            final double n6 = i + 0.5 - e.x;
            for (int j = floor2 - n; j <= floor2 + n; ++j) {
                final double n7 = j + 0.5 - e.z;
                for (int k = 127; k >= 0; --k) {
                    if (level.getTile(i, k, j) == Tile.portalTile.id) {
                        while (level.getTile(i, k - 1, j) == Tile.portalTile.id) {
                            --k;
                        }
                        final double n8 = k + 0.5 - e.y;
                        final double n9 = n6 * n6 + n8 * n8 + n7 * n7;
                        if (n2 < 0.0 || n9 < n2) {
                            n2 = n9;
                            n3 = i;
                            n4 = k;
                            n5 = j;
                        }
                    }
                }
            }
        }
        if (n2 >= 0.0) {
            final int n10 = n3;
            final int n11 = n4;
            final int n12 = n5;
            double x = n10 + 0.5;
            final double y = n11 + 0.5;
            double z = n12 + 0.5;
            if (level.getTile(n10 - 1, n11, n12) == Tile.portalTile.id) {
                x -= 0.5;
            }
            if (level.getTile(n10 + 1, n11, n12) == Tile.portalTile.id) {
                x += 0.5;
            }
            if (level.getTile(n10, n11, n12 - 1) == Tile.portalTile.id) {
                z -= 0.5;
            }
            if (level.getTile(n10, n11, n12 + 1) == Tile.portalTile.id) {
                z += 0.5;
            }
            e.moveTo(x, y, z, e.yRot, 0.0f);
            final double xd = 0.0;
            e.zd = xd;
            e.yd = xd;
            e.xd = xd;
            return true;
        }
        return false;
    }
    
    public boolean createPortal(final Level level, final Entity e) {
        final int n = 16;
        double n2 = -1.0;
        final int floor = Mth.floor(e.x);
        final int floor2 = Mth.floor(e.y);
        final int floor3 = Mth.floor(e.z);
        int n3 = floor;
        int n4 = floor2;
        int n5 = floor3;
        int n6 = 0;
        final int nextInt = this.random.nextInt(4);
        for (int i = floor - n; i <= floor + n; ++i) {
            final double n7 = i + 0.5 - e.x;
            for (int j = floor3 - n; j <= floor3 + n; ++j) {
                final double n8 = j + 0.5 - e.z;
            Label_0418:
                for (int k = 127; k >= 0; --k) {
                    if (level.isEmptyTile(i, k, j)) {
                        while (k > 0 && level.isEmptyTile(i, k - 1, j)) {
                            --k;
                        }
                        for (int l = nextInt; l < nextInt + 4; ++l) {
                            int n9 = l % 2;
                            int n10 = 1 - n9;
                            if (l % 4 >= 2) {
                                n9 = -n9;
                                n10 = -n10;
                            }
                            for (int n11 = 0; n11 < 3; ++n11) {
                                for (int n12 = 0; n12 < 4; ++n12) {
                                    for (int n13 = -1; n13 < 4; ++n13) {
                                        final int n14 = i + (n12 - 1) * n9 + n11 * n10;
                                        final int n15 = k + n13;
                                        final int n16 = j + (n12 - 1) * n10 - n11 * n9;
                                        if (n13 < 0 && !level.getMaterial(n14, n15, n16).isSolid()) {
                                            continue Label_0418;
                                        }
                                        if (n13 >= 0 && !level.isEmptyTile(n14, n15, n16)) {
                                            continue Label_0418;
                                        }
                                    }
                                }
                            }
                            final double n17 = k + 0.5 - e.y;
                            final double n18 = n7 * n7 + n17 * n17 + n8 * n8;
                            if (n2 < 0.0 || n18 < n2) {
                                n2 = n18;
                                n3 = i;
                                n4 = k;
                                n5 = j;
                                n6 = l % 4;
                            }
                        }
                    }
                }
            }
        }
        if (n2 < 0.0) {
            for (int n19 = floor - n; n19 <= floor + n; ++n19) {
                final double n20 = n19 + 0.5 - e.x;
                for (int n21 = floor3 - n; n21 <= floor3 + n; ++n21) {
                    final double n22 = n21 + 0.5 - e.z;
                Label_0751:
                    for (int y = 127; y >= 0; --y) {
                        if (level.isEmptyTile(n19, y, n21)) {
                            while (level.isEmptyTile(n19, y - 1, n21)) {
                                --y;
                            }
                            for (int n23 = nextInt; n23 < nextInt + 2; ++n23) {
                                final int n24 = n23 % 2;
                                final int n25 = 1 - n24;
                                for (int n26 = 0; n26 < 4; ++n26) {
                                    for (int n27 = -1; n27 < 4; ++n27) {
                                        final int n28 = n19 + (n26 - 1) * n24;
                                        final int n29 = y + n27;
                                        final int n30 = n21 + (n26 - 1) * n25;
                                        if (n27 < 0 && !level.getMaterial(n28, n29, n30).isSolid()) {
                                            continue Label_0751;
                                        }
                                        if (n27 >= 0 && !level.isEmptyTile(n28, n29, n30)) {
                                            continue Label_0751;
                                        }
                                    }
                                }
                                final double n31 = y + 0.5 - e.y;
                                final double n32 = n20 * n20 + n31 * n31 + n22 * n22;
                                if (n2 < 0.0 || n32 < n2) {
                                    n2 = n32;
                                    n3 = n19;
                                    n4 = y;
                                    n5 = n21;
                                    n6 = n23 % 2;
                                }
                            }
                        }
                    }
                }
            }
        }
        final int n33 = n6;
        final int n34 = n3;
        int n35 = n4;
        final int n36 = n5;
        int n37 = n33 % 2;
        int n38 = 1 - n37;
        if (n33 % 4 >= 2) {
            n37 = -n37;
            n38 = -n38;
        }
        if (n2 < 0.0) {
            if (n4 < 70) {
                n4 = 70;
            }
            if (n4 > 118) {
                n4 = 118;
            }
            n35 = n4;
            for (int n39 = -1; n39 <= 1; ++n39) {
                for (int n40 = 1; n40 < 3; ++n40) {
                    for (int n41 = -1; n41 < 3; ++n41) {
                        level.setTile(n34 + (n40 - 1) * n37 + n39 * n38, n35 + n41, n36 + (n40 - 1) * n38 - n39 * n37, (n41 < 0) ? Tile.obsidian.id : 0);
                    }
                }
            }
        }
        for (int n42 = 0; n42 < 4; ++n42) {
            level.noNeighborUpdate = true;
            for (int n43 = 0; n43 < 4; ++n43) {
                for (int n44 = -1; n44 < 4; ++n44) {
                    level.setTile(n34 + (n43 - 1) * n37, n35 + n44, n36 + (n43 - 1) * n38, (n43 == 0 || n43 == 3 || n44 == -1 || n44 == 3) ? Tile.obsidian.id : Tile.portalTile.id);
                }
            }
            level.noNeighborUpdate = false;
            for (int n45 = 0; n45 < 4; ++n45) {
                for (int n46 = -1; n46 < 4; ++n46) {
                    final int n47 = n34 + (n45 - 1) * n37;
                    final int n48 = n35 + n46;
                    final int n49 = n36 + (n45 - 1) * n38;
                    level.updateNeighborsAt(n47, n48, n49, level.getTile(n47, n48, n49));
                }
            }
        }
        return true;
    }
}
