// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class LiquidTileDynamic extends LiquidTile
{
    int maxCount = 0;
    boolean[] result = new boolean[4];
    int[] dist = new int[4];
    
    protected LiquidTileDynamic(final int id, final Material material) {
        super(id, material);
    }
    
    private void setStatic(final Level level, final int x, final int y, final int z) {
        int d = level.getData(x, y, z);
        level.setTileAndDataNoUpdate(x, y, z, this.id + 1, d);
        level.setTilesDirty(x, y, z, x, y, z);
        level.sendTileUpdated(x, y, z);
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        int depth = this.getDepth(level, x, y, z);

        int dropOff = 1;
        if (this.material == Material.lava && !level.dimension.ultraWarm) dropOff = 2;

        boolean becomeStatic = true;
        if (depth > 0) {
            int highest = -100;
            this.maxCount = 0;
            highest = this.getHighest(level, x - 1, y, z, highest);
            highest = this.getHighest(level, x + 1, y, z, highest);
            highest = this.getHighest(level, x, y, z - 1, highest);
            highest = this.getHighest(level, x, y, z + 1, highest);

            int newDepth = highest + dropOff;
            if (newDepth >= 8 || highest < 0) {
                newDepth = -1;
            }

            if (this.getDepth(level, x, y + 1, z) >= 0) {
                final int above = this.getDepth(level, x, y + 1, z);
                if (above >= 8) newDepth = above;
                else newDepth = above + 8;
            }
            if (this.maxCount >= 2 && this.material == Material.water) {
                // Only spread spring if it's on top of an existing spring, or
                // on top of solid ground.
                if (level.getMaterial(x, y - 1, z).isSolid()) {
                    newDepth = 0;
                }
                else if (level.getMaterial(x, y - 1, z) == this.material && level.getData(x, y, z) == 0) {
                    newDepth = 0;
                }
            }
            if (this.material == Material.lava)
                if (depth < 8 && newDepth < 8) {
                    if (newDepth > depth) {
                        if (random.nextInt(4) != 0) {
                            newDepth = depth;
                            becomeStatic = false;
                        }
                    }
                }
            if (newDepth == depth) {
                if (becomeStatic) {
                    this.setStatic(level, x, y, z);
                }
            } else {
                depth = newDepth;
                if (depth < 0) {
                    level.setTile(x, y, z, 0);
                }
                else {
                    level.setData(x, y, z, depth);
                    level.addToTickNextTick(x, y, z, this.id, this.getTickDelay());
                    level.updateNeighborsAt(x, y, z, this.id);
                }
            }
        }
        else {
            this.setStatic(level, x, y, z);
        }

        if (this.canSpreadTo(level, x, y - 1, z)) {
            if (depth >= 8) level.setTileAndData(x, y - 1, z, this.id, depth);
            else level.setTileAndData(x, y - 1, z, this.id, depth + 8);
        }
        else if (depth >= 0 && (depth == 0 || this.isWaterBlocking(level, x, y - 1, z))) {
            final boolean[] spreads = this.getSpread(level, x, y, z);
            int neighbor = depth + dropOff;
            if (depth >= 8) {
                neighbor = 1;
            }
            if (neighbor >= 8) return;
            if (spreads[0]) this.trySpreadTo(level, x - 1, y, z, neighbor);
            if (spreads[1]) this.trySpreadTo(level, x + 1, y, z, neighbor);
            if (spreads[2]) this.trySpreadTo(level, x, y, z - 1, neighbor);
            if (spreads[3]) this.trySpreadTo(level, x, y, z + 1, neighbor);
        }
    }
    
    private void trySpreadTo(final Level level, final int x, final int y, final int z, final int neighbor) {
        if (this.canSpreadTo(level, x, y, z)) {
            final int old = level.getTile(x, y, z);
            if (old > 0) {
                if (this.material == Material.lava) {
                    this.fizz(level, x, y, z);
                }
                else {
                    Tile.tiles[old].spawnResources(level, x, y, z, level.getData(x, y, z));
                }
            }
            level.setTileAndData(x, y, z, this.id, neighbor);
        }
    }
    
    private int getSlopeDistance(final Level level, final int x, final int y, final int z, final int pass, final int from) {
        int lowest = 1000;
        for (int d = 0; d < 4; ++d) {
            if (d == 0 && from == 1) continue;
            if (d == 1 && from == 0) continue;
            if (d == 2 && from == 3) continue;
            if (d == 3 && from == 2) continue;

            int xx = x;
            int yy = y;
            int zz = z;

            if (d == 0) xx--;
            if (d == 1) xx++;
            if (d == 2) zz--;
            if (d == 3) zz++;

            if (this.isWaterBlocking(level, xx, yy, zz)) {
                continue;
            } else if (level.getMaterial(xx, yy, zz) == this.material && level.getData(xx, yy, zz) == 0) {
                continue;
            } else {
                if (this.isWaterBlocking(level, xx, yy - 1, zz)) {
                    if (pass < 4) {
                        final int v = this.getSlopeDistance(level, xx, yy, zz, pass + 1, d);
                        if (v < lowest) lowest = v;
                    }
                } else {
                    return pass;
                }
            }
        }
        return lowest;
    }
    
    private boolean[] getSpread(final Level level, final int x, final int y, final int z) {
        for (int d = 0; d < 4; ++d) {
            this.dist[d] = 1000;
            int xx = x;
            int yy = y;
            int zz = z;

            if (d == 0) xx--;
            if (d == 1) xx++;
            if (d == 2) zz--;
            if (d == 3) zz++;
            if (this.isWaterBlocking(level, xx, yy, zz)) {
                continue;
            } else if (level.getMaterial(xx, yy, zz) == this.material && level.getData(xx, yy, zz) == 0) {
                continue;
            }

            if (!this.isWaterBlocking(level, xx, yy - 1, zz)) {
                this.dist[d] = 0;
            }
            else {
                this.dist[d] = this.getSlopeDistance(level, xx, yy, zz, 1, d);
            }
        }

        int lowest = this.dist[0];
        for (int d = 1; d < 4; ++d) {
            if (this.dist[d] < lowest) lowest = this.dist[d];
        }

        for (int d = 0; d < 4; ++d) {
            this.result[d] = (this.dist[d] == lowest);
        }
        return this.result;
    }
    
    private boolean isWaterBlocking(final Level level, final int x, final int y, final int z) {
        final int t = level.getTile(x, y, z);
        if (t == Tile.door_wood.id || t == Tile.door_iron.id || t == Tile.sign.id || t == Tile.ladder.id || t == Tile.reeds.id) {
            return true;
        }
        if (t == 0) return false;
        Material m = Tile.tiles[t].material;
        if (m.blocksMotion()) return true;
        return false;
    }
    
    protected int getHighest(final Level level, final int x, final int y, final int z, final int current) {
        int d = this.getDepth(level, x, y, z);
        if (d < 0) return current;
        if (d == 0) this.maxCount++;
        if (d >= 8) {
            d = 0;
        }
        return current < 0 || d < current ? d : current;
    }
    
    private boolean canSpreadTo(final Level level, final int x, final int y, final int z) {
        final Material target = level.getMaterial(x, y, z);
        if (target == this.material) return false;
        if (target == Material.lava) return false;
        return !this.isWaterBlocking(level, x, y, z);
    }
    
    @Override
    public void onPlace(final Level level, final int x, final int y, final int z) {
        super.onPlace(level, x, y, z);
        if (level.getTile(x, y, z) == this.id) {
            level.addToTickNextTick(x, y, z, this.id, this.getTickDelay());
        }
    }
}
