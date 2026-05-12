// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class LiquidTileDynamic extends LiquidTile
{
    int maxCount;
    boolean[] result;
    int[] dist;
    
    protected LiquidTileDynamic(final int id, final Material material) {
        super(id, material);
        this.maxCount = 0;
        this.result = new boolean[4];
        this.dist = new int[4];
    }
    
    private void setStatic(final Level level, final int x, final int y, final int z) {
        level.setTileAndDataNoUpdate(x, y, z, this.id + 1, level.getData(x, y, z));
        level.setTilesDirty(x, y, z, x, y, z);
        level.sendTileUpdated(x, y, z);
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        int depth = this.getDepth(level, x, y, z);
        int n = 1;
        if (this.material == Material.lava && !level.dimension.ultraWarm) {
            n = 2;
        }
        boolean b = true;
        if (depth > 0) {
            final int current = -100;
            this.maxCount = 0;
            final int highest = this.getHighest(level, x, y, z + 1, this.getHighest(level, x, y, z - 1, this.getHighest(level, x + 1, y, z, this.getHighest(level, x - 1, y, z, current))));
            int n2 = highest + n;
            if (n2 >= 8 || highest < 0) {
                n2 = -1;
            }
            if (this.getDepth(level, x, y + 1, z) >= 0) {
                final int depth2 = this.getDepth(level, x, y + 1, z);
                if (depth2 >= 8) {
                    n2 = depth2;
                }
                else {
                    n2 = depth2 + 8;
                }
            }
            if (this.maxCount >= 2 && this.material == Material.water) {
                if (level.getMaterial(x, y - 1, z).isSolid()) {
                    n2 = 0;
                }
                else if (level.getMaterial(x, y - 1, z) == this.material && level.getData(x, y, z) == 0) {
                    n2 = 0;
                }
            }
            if (this.material == Material.lava && depth < 8 && n2 < 8 && n2 > depth && random.nextInt(4) != 0) {
                n2 = depth;
                b = false;
            }
            if (n2 != depth) {
                depth = n2;
                if (depth < 0) {
                    level.setTile(x, y, z, 0);
                }
                else {
                    level.setData(x, y, z, depth);
                    level.addToTickNextTick(x, y, z, this.id, this.getTickDelay());
                    level.updateNeighborsAt(x, y, z, this.id);
                }
            }
            else if (b) {
                this.setStatic(level, x, y, z);
            }
        }
        else {
            this.setStatic(level, x, y, z);
        }
        if (this.canSpreadTo(level, x, y - 1, z)) {
            if (depth >= 8) {
                level.setTileAndData(x, y - 1, z, this.id, depth);
            }
            else {
                level.setTileAndData(x, y - 1, z, this.id, depth + 8);
            }
        }
        else if (depth >= 0 && (depth == 0 || this.isWaterBlocking(level, x, y - 1, z))) {
            final boolean[] spread = this.getSpread(level, x, y, z);
            int n3 = depth + n;
            if (depth >= 8) {
                n3 = 1;
            }
            if (n3 >= 8) {
                return;
            }
            if (spread[0]) {
                this.trySpreadTo(level, x - 1, y, z, n3);
            }
            if (spread[1]) {
                this.trySpreadTo(level, x + 1, y, z, n3);
            }
            if (spread[2]) {
                this.trySpreadTo(level, x, y, z - 1, n3);
            }
            if (spread[3]) {
                this.trySpreadTo(level, x, y, z + 1, n3);
            }
        }
    }
    
    private void trySpreadTo(final Level level, final int x, final int y, final int z, final int neighbor) {
        if (this.canSpreadTo(level, x, y, z)) {
            final int tile = level.getTile(x, y, z);
            if (tile > 0) {
                if (this.material == Material.lava) {
                    this.fizz(level, x, y, z);
                }
                else {
                    Tile.tiles[tile].spawnResources(level, x, y, z, level.getData(x, y, z));
                }
            }
            level.setTileAndData(x, y, z, this.id, neighbor);
        }
    }
    
    private int getSlopeDistance(final Level level, final int x, final int y, final int z, final int pass, final int from) {
        int n = 1000;
        for (int i = 0; i < 4; ++i) {
            if (i != 0 || from != 1) {
                if (i != 1 || from != 0) {
                    if (i != 2 || from != 3) {
                        if (i != 3 || from != 2) {
                            int x2 = x;
                            int z2 = z;
                            if (i == 0) {
                                --x2;
                            }
                            if (i == 1) {
                                ++x2;
                            }
                            if (i == 2) {
                                --z2;
                            }
                            if (i == 3) {
                                ++z2;
                            }
                            if (!this.isWaterBlocking(level, x2, y, z2)) {
                                if (level.getMaterial(x2, y, z2) != this.material || level.getData(x2, y, z2) != 0) {
                                    if (!this.isWaterBlocking(level, x2, y - 1, z2)) {
                                        return pass;
                                    }
                                    if (pass < 4) {
                                        final int slopeDistance = this.getSlopeDistance(level, x2, y, z2, pass + 1, i);
                                        if (slopeDistance < n) {
                                            n = slopeDistance;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return n;
    }
    
    private boolean[] getSpread(final Level level, final int x, final int y, final int z) {
        for (int i = 0; i < 4; ++i) {
            this.dist[i] = 1000;
            int x2 = x;
            int z2 = z;
            if (i == 0) {
                --x2;
            }
            if (i == 1) {
                ++x2;
            }
            if (i == 2) {
                --z2;
            }
            if (i == 3) {
                ++z2;
            }
            if (!this.isWaterBlocking(level, x2, y, z2)) {
                if (level.getMaterial(x2, y, z2) != this.material || level.getData(x2, y, z2) != 0) {
                    if (!this.isWaterBlocking(level, x2, y - 1, z2)) {
                        this.dist[i] = 0;
                    }
                    else {
                        this.dist[i] = this.getSlopeDistance(level, x2, y, z2, 1, i);
                    }
                }
            }
        }
        int n = this.dist[0];
        for (int j = 1; j < 4; ++j) {
            if (this.dist[j] < n) {
                n = this.dist[j];
            }
        }
        for (int k = 0; k < 4; ++k) {
            this.result[k] = (this.dist[k] == n);
        }
        return this.result;
    }
    
    private boolean isWaterBlocking(final Level level, final int x, final int y, final int z) {
        final int tile = level.getTile(x, y, z);
        return tile == Tile.door_wood.id || tile == Tile.door_iron.id || tile == Tile.sign.id || tile == Tile.ladder.id || tile == Tile.reeds.id || (tile != 0 && Tile.tiles[tile].material.blocksMotion());
    }
    
    protected int getHighest(final Level level, final int x, final int y, final int z, final int current) {
        int depth = this.getDepth(level, x, y, z);
        if (depth < 0) {
            return current;
        }
        if (depth == 0) {
            ++this.maxCount;
        }
        if (depth >= 8) {
            depth = 0;
        }
        return (current < 0 || depth < current) ? depth : current;
    }
    
    private boolean canSpreadTo(final Level level, final int x, final int y, final int z) {
        final Material material = level.getMaterial(x, y, z);
        return material != this.material && material != Material.lava && !this.isWaterBlocking(level, x, y, z);
    }
    
    @Override
    public void onPlace(final Level level, final int x, final int y, final int z) {
        super.onPlace(level, x, y, z);
        if (level.getTile(x, y, z) == this.id) {
            level.addToTickNextTick(x, y, z, this.id, this.getTickDelay());
        }
    }
}
