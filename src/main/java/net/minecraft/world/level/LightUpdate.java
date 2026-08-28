// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

import net.minecraft.world.level.tile.Tile;

// Useless - This class has no clear source avaiable. local var names and structure are largely guesswork
public class LightUpdate
{
    public final LightLayer layer;
    public int x0, y0, z0, x1, y1, z1;
    
    public LightUpdate(final LightLayer layer, final int x0, final int y0, final int z0, final int x1, final int y1, final int z1) {
        this.layer = layer;
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
    }
    
    public void update(final Level level) {
        if ((this.x1 - this.x0 + 1) * (this.y1 - this.y0 + 1) * (this.z1 - this.z0 + 1) > 32768) {
            System.out.println("Light too large, skipping!");
            return;
        }

        for (int x = this.x0; x <= this.x1; ++x) {
            for (int z = this.z0; z <= this.z1; ++z) {
                boolean hasChunks;
                hasChunks = (level.hasChunksAt(x, 0, z, 1));
                if (hasChunks && level.getChunk(x >> 4, z >> 4).isEmpty()) {
                    hasChunks = false;
                }

                if (hasChunks) {
                    if (this.y0 < Level.MIN_HEIGHT) this.y0 = Level.MIN_HEIGHT;
                    if (this.y1 >= Level.MAX_HEIGHT) this.y1 = Level.MAX_HEIGHT - 1;

                    for (int y = this.y0; y <= this.y1; ++y) {
                        final int currentBr = level.getBrightness(this.layer, x, y, z);
                        final int tile = level.getTile(x, y, z);

                        int block = Tile.lightBlock[tile];
                        if (block == 0) {
                            block = 1;
                        }

                        int emit = 0;
                        if (this.layer == LightLayer.Sky) {
                            if (level.isSkyLit(x, y, z)) emit = 15;
                        }
                        else if (this.layer == LightLayer.Block) {
                            emit = Tile.lightEmission[tile];
                        }

                        int nextBr;
                        if (block >= 15 && emit == 0) {
                            nextBr = 0;
                        }
                        else {
                            final int br1 = level.getBrightness(this.layer, x - 1, y, z);
                            final int br2 = level.getBrightness(this.layer, x + 1, y, z);
                            final int br3 = level.getBrightness(this.layer, x, y - 1, z);
                            final int br4 = level.getBrightness(this.layer, x, y + 1, z);
                            final int br5 = level.getBrightness(this.layer, x, y, z - 1);
                            final int br6 = level.getBrightness(this.layer, x, y, z + 1);
                            nextBr = br1;
                            if (br2 > nextBr) nextBr = br2;
                            if (br3 > nextBr) nextBr = br3;
                            if (br4 > nextBr) nextBr = br4;
                            if (br5 > nextBr) nextBr = br5;
                            if (br6 > nextBr) nextBr = br6;

                            nextBr = nextBr - block;
                            if (nextBr < 0) nextBr = 0;
                            if (emit > nextBr) nextBr = emit;
                        }

                        if (currentBr != nextBr) {
                            level.setBrightness(this.layer, x, y, z, nextBr);
                            int ajacentBr = nextBr - 1;
                            if (ajacentBr < 0) ajacentBr = 0;
                            level.updateLightIfOtherThan(this.layer, x - 1, y, z, ajacentBr);
                            level.updateLightIfOtherThan(this.layer, x, y - 1, z, ajacentBr);
                            level.updateLightIfOtherThan(this.layer, x, y, z - 1, ajacentBr);
                            if (x + 1 >= this.x1) level.updateLightIfOtherThan(this.layer, x + 1, y, z, ajacentBr);
                            if (y + 1 >= this.y1) level.updateLightIfOtherThan(this.layer, x, y + 1, z, ajacentBr);
                            if (z + 1 >= this.z1) level.updateLightIfOtherThan(this.layer, x, y, z + 1, ajacentBr);
                        }
                    }
                }
            }
        }
    }
    
    public boolean expandToContain(int x0, int y0, int z0, int x2, int y2, int z2) {
        if (x0 >= this.x0 && y0 >= this.y0 && z0 >= this.z0 && x2 <= this.x1 && y2 <= this.y1 && z2 <= this.z1) return true;

        final int r = 1;
        if (x0 >= this.x0 - r
                && y0 >= this.y0 - r
                && z0 >= this.z0 - r
                && x2 <= this.x1 + r
                && y2 <= this.y1 + r
                && z2 <= this.z1 + r) {
            final int xd = this.x1 - this.x0;
            final int yd = this.y1 - this.y0;
            final int zd = this.z1 - this.z0;

            if (x0 > this.x0) x0 = this.x0;
            if (y0 > this.y0) y0 = this.y0;
            if (z0 > this.z0) z0 = this.z0;
            if (x2 < this.x1) x2 = this.x1;
            if (y2 < this.y1) y2 = this.y1;
            if (z2 < this.z1) z2 = this.z1;

            if ((x2 - x0) * (y2 - y0) * (z2 - z0) - xd * yd * zd <= 2) {
                this.x0 = x0;
                this.y0 = y0;
                this.z0 = z0;
                this.x1 = x2;
                this.y1 = y2;
                this.z1 = z2;
                return true;
            }
        }
        return false;
    }
}
