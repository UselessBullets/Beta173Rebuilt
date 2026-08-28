// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen;

import util.ProgressListener;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.levelgen.feature.SpringFeature;
import net.minecraft.world.level.levelgen.feature.CactusFeature;
import net.minecraft.world.level.levelgen.feature.PumpkinFeature;
import net.minecraft.world.level.levelgen.feature.ReedsFeature;
import net.minecraft.world.level.levelgen.feature.FlowerFeature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.ClayFeature;
import net.minecraft.world.level.levelgen.feature.MonsterRoomFeature;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.tile.SandTile;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import java.util.Random;
import net.minecraft.world.level.chunk.ChunkSource;

public class SkyIslandRandomLevelSource implements ChunkSource
{
    private static final double SNOW_CUTOFF = 0.5;
    private static final double SNOW_SCALE = 0.3;
    private static final boolean FLOATING_ISLANDS = false;
    public static final int CHUNK_HEIGHT = 4;
    public static final int CHUNK_WIDTH = 8;
    private Random random;
    private PerlinNoise lperlinNoise1;
    private PerlinNoise lperlinNoise2;
    private PerlinNoise perlinNoise1;
    private PerlinNoise perlinNoise2;
    private PerlinNoise perlinNoise3;
    public PerlinNoise scaleNoise;
    public PerlinNoise depthNoise;
    private PerlinNoise floatingIslandScale; // Useless - Exists in b1.2 and LCE leaks version of RandomLevelSource, and this class is a modified version of that
    private PerlinNoise floatingIslandNoise; // Useless - Exists in b1.2 and LCE leaks version of RandomLevelSource, and this class is a modified version of that
    public PerlinNoise forestNoise;
    private Level level;
    private double[] buffer;
    private double[] sandBuffer = new double[16 * 16];
    private double[] gravelBuffer = new double[16 * 16];
    private double[] depthBuffer = new double[16 * 16];
    private LargeFeature caveFeature = new LargeCaveFeature();
    private Biome[] biomes;
    double[] pnr;
    double[] ar;
    double[] br;
    double[] sr;
    double[] dr;
    double[] fi; // Useless - Exists in b1.2 leaks version of RandomLevelSource, and this class is a modified version of that
    double[] fis; // Useless - Exists in b1.2 leaks version of RandomLevelSource, and this class is a modified version of that
    int[][] waterDepths = new int[32][32];
    private double[] temperatures;
    
    public SkyIslandRandomLevelSource(final Level level, final long seed) {
        this.level = level;
        this.random = new Random(seed);
        this.lperlinNoise1 = new PerlinNoise(this.random, 16);
        this.lperlinNoise2 = new PerlinNoise(this.random, 16);
        this.perlinNoise1 = new PerlinNoise(this.random, 8);
        this.perlinNoise2 = new PerlinNoise(this.random, 4);
        this.perlinNoise3 = new PerlinNoise(this.random, 4);
        this.scaleNoise = new PerlinNoise(this.random, 10);
        this.depthNoise = new PerlinNoise(this.random, 16);

        // Useless - b1.2 and LCE floating island code
        if (FLOATING_ISLANDS)
        {
            this.floatingIslandScale = new PerlinNoise(this.random, 10);
            this.floatingIslandNoise = new PerlinNoise(this.random, 16);
        }
        else
        {
            this.floatingIslandScale = null;
            this.floatingIslandNoise = null;
        }

        this.forestNoise = new PerlinNoise(this.random, 8);
    }
    
    public void prepareHeights(final int xOffs, final int zOffs, final byte[] blocks, final Biome[] biomes, final double[] temperatures) {
        final int xChunks = 16 / CHUNK_WIDTH;
        final int yChunks = Level.MAX_HEIGHT / CHUNK_HEIGHT;

        final int xSize = xChunks + 1;
        final int ySize = Level.MAX_HEIGHT / CHUNK_HEIGHT + 1;
        final int zSize = xChunks + 1;

        this.buffer = this.getHeights(this.buffer, xOffs * xChunks, 0, zOffs * xChunks, xSize, ySize, zSize);
        for (int xc = 0; xc < xChunks; ++xc) {
            for (int zc = 0; zc < xChunks; ++zc) {
                for (int yc = 0; yc < yChunks; ++yc) {
                    final double yStep = 1 / (double) CHUNK_HEIGHT;
                    double s0 = this.buffer[((xc + 0) * zSize + (zc + 0)) * ySize + (yc + 0)];
                    double s1 = this.buffer[((xc + 0) * zSize + (zc + 1)) * ySize + (yc + 0)];
                    double s2 = this.buffer[((xc + 1) * zSize + (zc + 0)) * ySize + (yc + 0)];
                    double s3 = this.buffer[((xc + 1) * zSize + (zc + 1)) * ySize + (yc + 0)];

                    final double s0a = (this.buffer[((xc + 0) * zSize + (zc + 0)) * ySize + (yc + 1)] - s0) * yStep;
                    final double s1a = (this.buffer[((xc + 0) * zSize + (zc + 1)) * ySize + (yc + 1)] - s1) * yStep;
                    final double s2a = (this.buffer[((xc + 1) * zSize + (zc + 0)) * ySize + (yc + 1)] - s2) * yStep;
                    final double s3a = (this.buffer[((xc + 1) * zSize + (zc + 1)) * ySize + (yc + 1)] - s3) * yStep;

                    for (int y = 0; y < CHUNK_HEIGHT; ++y) {
                        final double xStep = 1 / (double) CHUNK_WIDTH;

                        double _s0 = s0;
                        double _s1 = s1;
                        final double _s0a = (s2 - s0) * xStep;
                        final double _s1a = (s3 - s1) * xStep;

                        for (int x = 0; x < CHUNK_WIDTH; ++x) {
                            int offs = x + xc * CHUNK_WIDTH << 11 | 0 + zc * CHUNK_WIDTH << 7 | yc * CHUNK_HEIGHT + y;
                            final int step = Level.MAX_HEIGHT;
                            final double zStep = 1 / (double) CHUNK_WIDTH;

                            double val = _s0;
                            final double vala = (_s1 - _s0) * zStep;
                            for (int z = 0; z < CHUNK_WIDTH; ++z) {
                                int tileId = 0;
                                if (val > 0.0) {
                                    tileId = Tile.rock.id;
                                }

                                blocks[offs] = (byte)tileId;
                                offs += step;
                                val += vala;
                            }
                            _s0 += _s0a;
                            _s1 += _s1a;
                        }

                        s0 += s0a;
                        s1 += s1a;
                        s2 += s2a;
                        s3 += s3a;
                    }
                }
            }
        }
    }
    
    public void buildSurfaces(final int xOffs, final int zOffs, final byte[] blocks, final Biome[] biomes) {
        final int waterHeight = 0;
        final double s = 1 / 32.0;

        this.sandBuffer = this.perlinNoise2.getRegion(this.sandBuffer, xOffs * 16, zOffs * 16, 0.0, 16, 16, 1, s, s, 1.0);
        this.gravelBuffer = this.perlinNoise2.getRegion(this.gravelBuffer, xOffs * 16, 109.0134, zOffs * 16, 16, 1, 16, s, 1.0, s);
        this.depthBuffer = this.perlinNoise3.getRegion(this.depthBuffer, xOffs * 16, zOffs * 16, 0.0, 16, 16, 1, s * 2.0, s * 2.0, s * 2.0);
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                final Biome b = biomes[x + z * 16];
                final int runDepth = (int)(this.depthBuffer[x + z * 16] / 3.0 + 3.0 + this.random.nextDouble() * 0.25);

                int run = -1;

                byte top = b.topMaterial;
                byte material = b.material;

                for (int y = (Level.MAX_HEIGHT - 1); y >= 0; --y) {
                    final int offs = (z * 16 + x) * Level.MAX_HEIGHT + y;

                    final byte old = blocks[offs];
                    if (old == 0) {
                        run = -1;
                    }
                    else if (old == Tile.rock.id) {
                        if (run == -1) {
                            if (runDepth <= 0) {
                                top = 0;
                                material = (byte)Tile.rock.id;
                            }
                            run = runDepth;
                            if (y >= waterHeight) blocks[offs] = top;
                            else blocks[offs] = material;
                        }
                        else if (run > 0) {
                            run--;
                            blocks[offs] = material;

                            // place a few sandstone blocks beneath sand
                            // runs
                            if (run == 0 && material == Tile.sand.id) {
                                run = this.random.nextInt(4);
                                material = (byte)Tile.sandStone.id;
                            }
                        }
                    }
                }
            }
        }
    }
    
    public LevelChunk create(final int x, final int z) {
        return this.getChunk(x, z);
    }
    
    public LevelChunk getChunk(final int x, final int z) {
        this.random.setSeed(x * 341873128712L + z * 132897987541L);

        final byte[] blocks = new byte[Level.MAX_HEIGHT * 16 * 16];

        final LevelChunk levelChunk = new LevelChunk(this.level, blocks, x, z);

        this.prepareHeights(x, z, blocks, this.biomes = this.level.getBiomeSource().getBiomeBlock(this.biomes, x * 16, z * 16, 16, 16), this.level.getBiomeSource().temperatures);

        this.buildSurfaces(x, z, blocks, this.biomes);

        this.caveFeature.apply(this, this.level, x, z, blocks);

        levelChunk.recalcHeightmap();
        return levelChunk;
    }
    
    private double[] getHeights(double[] buffer, final int x, final int y, final int z, final int xSize, final int ySize, final int zSize) {
        if (buffer == null) buffer = new double[xSize * ySize * zSize];

        double s = 684.412;
        s *= 2.0;
        double hs = 684.412;
        double[] temperatures = this.level.getBiomeSource().temperatures;
        double[] downfalls = this.level.getBiomeSource().downfalls;

        // Useless - b1.2 and LCE floating island code
        if (FLOATING_ISLANDS)
        {
            this.fis = this.floatingIslandScale.getRegion(this.fis, x, y, z, xSize, 1, zSize, 1.0, 0, 1.0);
            this.fi = this.floatingIslandNoise.getRegion(this.fi, x, y, z, xSize, 1, zSize, 500.0, 0, 500.0);
        }

        this.sr = this.scaleNoise.getRegion(this.sr, x, z, xSize, zSize, 1.121, 1.121, 0.5);
        this.dr = this.depthNoise.getRegion(this.dr, x, z, xSize, zSize, 200.0, 200.0, 0.5);
        this.pnr = this.perlinNoise1.getRegion(this.pnr, x, y, z, xSize, ySize, zSize, s / 80.0, hs / 160.0, s / 80.0);
        this.ar = this.lperlinNoise1.getRegion(this.ar, x, y, z, xSize, ySize, zSize, s, hs, s);
        this.br = this.lperlinNoise2.getRegion(this.br, x, y, z, xSize, ySize, zSize, s, hs, s);

        int p = 0;
        int pp = 0;
        final int step = 16 / xSize;

        for (int xx = 0; xx < xSize; ++xx) {
            final int _x = xx * step + step / 2;

            for (int zz = 0; zz < zSize; ++zz) {
                final int _z = zz * step + step / 2;
                double temp = temperatures[_x * 16 + _z];
                double rain = downfalls[_x * 16 + _z];
                double intensity = 1.0 - rain * temp; // Useless - TODO I could not find what this local var would be called, naming it intensity for now as it seems to vary the height intensity of terrain based on rain/temp value
                intensity *= intensity;
                intensity *= intensity;
                intensity = 1 - intensity;
                double scale = (this.sr[pp] + 256.0) / 512.0;
                scale = scale * intensity;
                if (scale > 1.0) scale = 1.0;

                double depth = this.dr[pp] / 8000.0;
                if (depth < 0.0) depth = -depth * 0.3;
                depth = depth * 3.0 - 2.0;
                if (depth > 1.0) depth = 1.0;
                depth = 0.0;

                if (scale < 0.0) {
                    scale = 0.0;
                }
                scale += 0.5;
                depth = depth * ySize / 16.0;

                final double yCenter = ySize / 2.0;
                pp++;

                for (int yy = 0; yy < ySize; ++yy) {
                    double val;

                    double yOffs = (yy - yCenter) * (8.0 * 128 / Level.MAX_HEIGHT) / scale;

                    if (yOffs < 0.0) yOffs *= 4.0;
                    yOffs = 8.0;

                    final double bb = this.ar[p] / 512.0;
                    final double cc = this.br[p] / 512.0;

                    final double v = (this.pnr[p] / 10.0 + 1.0) / 2.0;
                    if (v < 0.0) val = bb;
                    else if (v > 1.0) val = cc;
                    else val = bb + (cc - bb) * v;
                    val -= yOffs;

                    int slideRange = 32;
                    if (yy > ySize - slideRange) {
                        final double slide = (yy - (ySize - slideRange)) / (slideRange - 1.0f);
                        val = val * (1.0 - slide) + -30.0 * slide;
                    }

                    slideRange = 8;
                    if (yy < slideRange) {
                        final double slide = (slideRange - yy) / (slideRange - 1.0f);
                        val = val * (1.0 - slide) + -30.0 * slide;
                    }

                    buffer[p] = val;
                    ++p;
                }
            }
        }
        return buffer;
    }
    
    public boolean hasChunk(final int x, final int z) {
        return true;
    }

    // Useless - Exists in b1.2 and LCE leaks
    private void calcWaterDepths(ChunkSource parent, int xt, int zt) {
        int xo = xt * 16;
        int zo = zt * 16;
        for (int x = 0; x < 16; x++) {
            int y = this.level.getSeaLevel();
            for (int z = 0; z < 16; z++) {
                int xp = xo + x + 7;
                int zp = zo + z + 7;
                int h = this.level.getHeightmap(xp, zp);
                if (h <= 0) {
                    if (this.level.getHeightmap(xp - 1, zp) > 0 || this.level.getHeightmap(xp + 1, zp) > 0 || this.level.getHeightmap(xp, zp - 1) > 0 || this.level.getHeightmap(xp, zp + 1) > 0) {
                        boolean hadWater = false;
                        if (hadWater || this.level.getTile(xp - 1, y, zp) == Tile.calmWater.id && this.level.getData(xp - 1, y, zp) < 7) hadWater = true;
                        if (hadWater || this.level.getTile(xp + 1, y, zp) == Tile.calmWater.id && this.level.getData(xp + 1, y, zp) < 7) hadWater = true;
                        if (hadWater || this.level.getTile(xp, y, zp - 1) == Tile.calmWater.id && this.level.getData(xp, y, zp - 1) < 7) hadWater = true;
                        if (hadWater || this.level.getTile(xp, y, zp + 1) == Tile.calmWater.id && this.level.getData(xp, y, zp + 1) < 7) hadWater = true;
                        if (hadWater) {
                            for (int x2 = -5; x2 <= 5; x2++) {
                                for (int z2 = -5; z2 <= 5; z2++) {
                                    int d = (x2 > 0 ? x2 : -x2) + (z2 > 0 ? z2 : -z2);

                                    if (d <= 5) {
                                        d = 6 - d;
                                        if (this.level.getTile(xp + x2, y, zp + z2) == Tile.calmWater.id) {
                                            int od = this.level.getData(xp + x2, y, zp + z2);
                                            if (od < 7 && od < d) {
                                                this.level.setData(xp + x2, y, zp + z2, d);
                                            }
                                        }
                                    }
                                }
                            }

                            if (hadWater) {
                                this.level.setTileAndDataNoUpdate(xp, y, zp, Tile.calmWater.id, 7);

                                for (int y2 = 0; y2 < y; y2++) {
                                    this.level.setTileAndDataNoUpdate(xp, y2, zp, Tile.calmWater.id, 8);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void postProcess(final ChunkSource parent, final int xt, final int zt) {
        SandTile.instaFall = true;
        final int xo = xt * 16;
        final int zo = zt * 16;

        final Biome biome = this.level.getBiomeSource().getBiome(xo + 16, zo + 16);

        // Useless - Code from LCE that calls calcWaterDepths, a method which also existed in b1.2 and a constant that also existed in b1.2 so presumably this was here in b1.7.3
        if (FLOATING_ISLANDS)
        {
            calcWaterDepths(parent, xt, zt);
        }

        this.random.setSeed(this.level.getSeed());
        long xScale = this.random.nextLong() / 2L * 2L + 1L;
        long zScale = this.random.nextLong() / 2L * 2L + 1L;
        this.random.setSeed(xt * xScale + zt * zScale ^ this.level.getSeed());

        if (this.random.nextInt(4) == 0) {
            int x = xo + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(Level.MAX_HEIGHT);
            int z = zo + this.random.nextInt(16) + 8;

            LakeFeature calmWater = new LakeFeature(Tile.calmWater.id);
            calmWater.place(this.level, this.random, x, y, z);
        }

        if (this.random.nextInt(8) == 0) {
            final int x = xo + this.random.nextInt(16) + 8;
            final int y = this.random.nextInt(this.random.nextInt(Level.MAX_HEIGHT - 8) + 8);
            final int z = zo + this.random.nextInt(16) + 8;
            if (y < (Level.SEA_LEVEL + 1) || this.random.nextInt(10) == 0) {
                LakeFeature calmLava = new LakeFeature(Tile.calmLava.id);
                calmLava.place(this.level, this.random, x, y, z);
            }
        }

        for (int i = 0; i < 8; ++i) {
            int x = xo + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(Level.MAX_HEIGHT);
            int z = zo + this.random.nextInt(16) + 8;
            MonsterRoomFeature mrf = new MonsterRoomFeature();
            mrf.place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 10; ++i) {
            int x = xo + this.random.nextInt(16);
            int y = this.random.nextInt(Level.MAX_HEIGHT);
            int z = zo + this.random.nextInt(16);
            ClayFeature clayFeature = new ClayFeature(32);
            clayFeature.place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 20; ++i) {
            int x = xo + this.random.nextInt(16);
            int y = this.random.nextInt(Level.MAX_HEIGHT);
            int z = zo + this.random.nextInt(16);
            OreFeature dirtOreFeature = new OreFeature(Tile.dirt.id, 32);
            dirtOreFeature.place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 10; ++i) {
            int x = xo + this.random.nextInt(16);
            int y = this.random.nextInt(Level.MAX_HEIGHT);
            int z = zo + this.random.nextInt(16);
            OreFeature gravelOreFeature = new OreFeature(Tile.gravel.id, 32);
            gravelOreFeature.place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 20; ++i) {
            int x = xo + this.random.nextInt(16);
            int y = this.random.nextInt(Level.MAX_HEIGHT);
            int z = zo + this.random.nextInt(16);
            OreFeature coalOreFeature = new OreFeature(Tile.coalOre.id, 16);
            coalOreFeature.place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 20; ++i) {
            int x = xo + this.random.nextInt(16);
            int y = this.random.nextInt(Level.MAX_HEIGHT / 2);
            int z = zo + this.random.nextInt(16);
            OreFeature ironOreFeature = new OreFeature(Tile.ironOre.id, 8);
            ironOreFeature.place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 2; ++i) {
            int x = xo + this.random.nextInt(16);
            int y = this.random.nextInt(Level.MAX_HEIGHT / 4);
            int z = zo + this.random.nextInt(16);
            OreFeature goldOreFeature = new OreFeature(Tile.goldOre.id, 8);
            goldOreFeature.place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 8; ++i) {
            int x = xo + this.random.nextInt(16);
            int y = this.random.nextInt(Level.MAX_HEIGHT / 8);
            int z = zo + this.random.nextInt(16);
            OreFeature redStoneOreFeature = new OreFeature(Tile.redStoneOre.id, 7);
            redStoneOreFeature.place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 1; ++i) {
            int x = xo + this.random.nextInt(16);
            int y = this.random.nextInt(Level.MAX_HEIGHT / 8);
            int z = zo + this.random.nextInt(16);
            OreFeature diamondOreFeature = new OreFeature(Tile.diamondOre.id, 7);
            diamondOreFeature.place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 1; ++i) {
            int x = xo + this.random.nextInt(16);
            int y = this.random.nextInt(Level.MAX_HEIGHT / 8) + this.random.nextInt(Level.MAX_HEIGHT / 8);
            int z = zo + this.random.nextInt(16);
            OreFeature lapisOreFeature = new OreFeature(Tile.lapisOre.id, 6);
            lapisOreFeature.place(this.level, this.random, x, y, z);
        }

        final double forestScale = 0.5;
        final int forestOffset = (int)((this.forestNoise.getValue(xo * forestScale, zo * forestScale) / 8.0 + this.random.nextDouble() * 4.0 + 4.0) / 3.0);
        int forests = 0;
        if (this.random.nextInt(10) == 0) ++forests;

        if (biome == Biome.forest) forests += forestOffset + 5;
        if (biome == Biome.rainForest) forests += forestOffset + 5;
        if (biome == Biome.seasonalForest) forests += forestOffset + 2;
        if (biome == Biome.taiga) forests += forestOffset + 5;
        if (biome == Biome.desert) forests -= 20;
        if (biome == Biome.tunfra) forests -= 20;
        if (biome == Biome.plains) forests -= 20;

        for (int i = 0; i < forests; ++i) {
            final int x = xo + this.random.nextInt(16) + 8;
            final int z = zo + this.random.nextInt(16) + 8;
            final Feature tree = biome.getTreeFeature(this.random);
            tree.init(1.0, 1.0, 1.0);
            tree.place(this.level, this.random, x, this.level.getHeightmap(x, z), z);
        }

        for (int i = 0; i < 2; ++i) {
            int x = xo + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(Level.MAX_HEIGHT);
            int z = zo + this.random.nextInt(16) + 8;
            FlowerFeature yellowFlowerFeature = new FlowerFeature(Tile.flower.id);
            yellowFlowerFeature.place(this.level, this.random, x, y, z);
        }

        if (this.random.nextInt(2) == 0) {
            int x = xo + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(Level.MAX_HEIGHT);
            int z = zo + this.random.nextInt(16) + 8;
            FlowerFeature roseFlowerFeature = new FlowerFeature(Tile.rose.id);
            roseFlowerFeature.place(this.level, this.random, x, y, z);
        }

        if (this.random.nextInt(4) == 0) {
            int x = xo + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(Level.MAX_HEIGHT);
            int z = zo + this.random.nextInt(16) + 8;
            FlowerFeature brownMushroomFeature = new FlowerFeature(Tile.mushroom1.id);
            brownMushroomFeature.place(this.level, this.random, x, y, z);
        }

        if (this.random.nextInt(8) == 0) {
            int x = xo + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(Level.MAX_HEIGHT);
            int z = zo + this.random.nextInt(16) + 8;
            FlowerFeature redMushroomFeature = new FlowerFeature(Tile.mushroom2.id);
            redMushroomFeature.place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 10; ++i) {
            int x = xo + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(Level.MAX_HEIGHT);
            int z = zo + this.random.nextInt(16) + 8;
            ReedsFeature reedsFeature = new ReedsFeature();
            reedsFeature.place(this.level, this.random, x, y, z);
        }

        if (this.random.nextInt(32) == 0) {
            int x = xo + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(Level.MAX_HEIGHT);
            int z = zo + this.random.nextInt(16) + 8;
            PumpkinFeature pumpkinFeature = new PumpkinFeature();
            pumpkinFeature.place(this.level, this.random, x, y, z);
        }

        int cactusCount = 0;
        if (biome == Biome.desert) cactusCount += 10;
        for (int i = 0; i < cactusCount; ++i) {
            int x = xo + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(Level.MAX_HEIGHT);
            int z = zo + this.random.nextInt(16) + 8;
            CactusFeature cactusFeature = new CactusFeature();
            cactusFeature.place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 50; ++i) {
            int x = xo + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(this.random.nextInt(Level.MAX_HEIGHT - 8) + 8);
            int z = zo + this.random.nextInt(16) + 8;
            SpringFeature waterSpringFeature = new SpringFeature(Tile.water.id);
            waterSpringFeature.place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 20; ++i) {
            int x = xo + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(this.random.nextInt(this.random.nextInt(Level.MAX_HEIGHT - 16) + 8) + 8);
            int z = zo + this.random.nextInt(16) + 8;
            SpringFeature lavaSpringFeature = new SpringFeature(Tile.lava.id);
            lavaSpringFeature.place(this.level, this.random, x, y, z);
        }

        this.temperatures = this.level.getBiomeSource().getTemperatureBlock(this.temperatures, xo + 8, zo + 8, 16, 16);
        for (int x = xo + 8; x < xo + 8 + 16; ++x) {
            for (int z = zo + 8; z < zo + 8 + 16; ++z) {
                final int xx = x - (xo + 8);
                final int zz = z - (zo + 8);
                final int y = this.level.getTopRainBlock(x, z);
                double snow = this.temperatures[xx * 16 + zz] - (y - (Level.SEA_LEVEL + 1.0)) / (Level.SEA_LEVEL + 1.0) * SNOW_SCALE;
                if (snow < SNOW_CUTOFF
                        && y > 0
                        && y < Level.MAX_HEIGHT
                        && this.level.isEmptyTile(x, y, z)
                        && this.level.getMaterial(x, y - 1, z).blocksMotion()
                        && this.level.getMaterial(x, y - 1, z) != Material.ice) {
                    this.level.setTile(x, y, z, Tile.topSnow.id);
                }
            }
        }
        SandTile.instaFall = false;
    }
    
    public boolean save(final boolean force, final ProgressListener progressListener) {
        return true;
    }
    
    public boolean tick() {
        return false;
    }
    
    public boolean shouldSave() {
        return true;
    }
    
    public String gatherStats() {
        return "RandomLevelSource";
    }
}
