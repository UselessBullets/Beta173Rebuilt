// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.material.Material;

public class LeafTile extends TransparentTile
{
    public static final int REQUIRED_WOOD_RANGE = 4;

    public static final int UPDATE_LEAF_BIT = 0x8;

    public static final int NORMAL_LEAF = 0;
    public static final int EVERGREEN_LEAF = 1;
    public static final int BIRCH_LEAF = 2;

    public static final int LEAF_TYPE_MASK = 0x3;
    private int oTex;
    int[] checkBuffer;
    
    protected LeafTile(final int id, final int tex) {
        super(id, tex, Material.leaves, false);
        this.oTex = tex;
        this.setTicking(true);
    }
    
    @Override
    public int getColor(final int data) {
        if ((data & LEAF_TYPE_MASK) == EVERGREEN_LEAF) {
            return FoliageColor.getEvergreenColor();
        }
        if ((data & LEAF_TYPE_MASK) == BIRCH_LEAF) {
            return FoliageColor.getBirchColor();
        }

        return FoliageColor.getDefaultColor();
    }
    
    @Override
    public int getColor(final LevelSource level, final int x, final int y, final int z) {
        final int data = level.getData(x, y, z);
        if ((data & LEAF_TYPE_MASK) == EVERGREEN_LEAF) {
            return FoliageColor.getEvergreenColor();
        }
        if ((data & LEAF_TYPE_MASK) == BIRCH_LEAF) {
            return FoliageColor.getBirchColor();
        }

        level.getBiomeSource().getBiomeBlock(x, z, 1, 1);
        double temp = level.getBiomeSource().temperatures[0];
        double rain = level.getBiomeSource().downfalls[0];
        return FoliageColor.get(temp, rain);
    }
    
    @Override
    public void onRemove(final Level level, final int x, final int y, final int z) {
        final int r = 1;
        final int r2 = r + 1;

        if (level.hasChunksAt(x - r2, y - r2, z - r2, x + r2, y + r2, z + r2)) {
            for (int xo = -r; xo <= r; ++xo) {
                for (int yo = -r; yo <= r; ++yo) {
                    for (int zo = -r; zo <= r; ++zo) {
                        int t = level.getTile(x + xo, y + yo, z + zo);
                        if (t == Tile.leaves.id) {
                            int currentData = level.getData(x + xo, y + yo, z + zo);
                            level.setDataNoUpdate(x + xo, y + yo, z + zo, currentData | UPDATE_LEAF_BIT);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        if (level.isClientSide) return;

        final int currentData = level.getData(x, y, z);
        if ((currentData & UPDATE_LEAF_BIT) != 0x0) {
            final int r = REQUIRED_WOOD_RANGE;
            final int r2 = r + 1;

            final int W = 32;
            final int WW = W * W;
            final int WO = W / 2;
            if (this.checkBuffer == null) {
                this.checkBuffer = new int[W * W * W];
            }

            if (level.hasChunksAt(x - r2, y - r2, z - r2, x + r2, y + r2, z + r2)) {
                for (int xo = -r; xo <= r; ++xo) {
                    for (int zo = -r; zo <= r; ++zo) {
                        for (int yo = -r; yo <= r; ++yo) {
                            final int t = level.getTile(x + xo, y + zo, z + yo);
                            if (t == Tile.treeTrunk.id) {
                                this.checkBuffer[(xo + WO) * WW + (zo + WO) * W + (yo + WO)] = 0;
                            }
                            else if (t == Tile.leaves.id) {
                                this.checkBuffer[(xo + WO) * WW + (zo + WO) * W + (yo + WO)] = -2;
                            }
                            else {
                                this.checkBuffer[(xo + WO) * WW + (zo + WO) * W + (yo + WO)] = -1;
                            }
                        }
                    }
                }

                for (int i = 1; i <= REQUIRED_WOOD_RANGE; ++i) {
                    for (int xo = -r; xo <= r; ++xo) {
                        for (int yo = -r; yo <= r; ++yo) {
                            for (int zo = -r; zo <= r; ++zo) {
                                if (this.checkBuffer[(xo + WO) * WW + (yo + WO) * W + (zo + WO)] == i - 1) {
                                    if (this.checkBuffer[(xo + WO - 1) * WW + (yo + WO) * W + (zo + WO)] == -2) {
                                        this.checkBuffer[(xo + WO - 1) * WW + (yo + WO) * W + (zo + WO)] = i;
                                    }
                                    if (this.checkBuffer[(xo + WO + 1) * WW + (yo + WO) * W + (zo + WO)] == -2) {
                                        this.checkBuffer[(xo + WO + 1) * WW + (yo + WO) * W + (zo + WO)] = i;
                                    }
                                    if (this.checkBuffer[(xo + WO) * WW + (yo + WO - 1) * W + (zo + WO)] == -2) {
                                        this.checkBuffer[(xo + WO) * WW + (yo + WO - 1) * W + (zo + WO)] = i;
                                    }
                                    if (this.checkBuffer[(xo + WO) * WW + (yo + WO + 1) * W + (zo + WO)] == -2) {
                                        this.checkBuffer[(xo + WO) * WW + (yo + WO + 1) * W + (zo + WO)] = i;
                                    }
                                    if (this.checkBuffer[(xo + WO) * WW + (yo + WO) * W + (zo + WO - 1)] == -2) {
                                        this.checkBuffer[(xo + WO) * WW + (yo + WO) * W + (zo + WO - 1)] = i;
                                    }
                                    if (this.checkBuffer[(xo + WO) * WW + (yo + WO) * W + (zo + WO + 1)] == -2) {
                                        this.checkBuffer[(xo + WO) * WW + (yo + WO) * W + (zo + WO + 1)] = i;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            int mid = this.checkBuffer[WO * WW + WO * W + WO];
            if (mid >= 0) {
                level.setDataNoUpdate(x, y, z, currentData & ~UPDATE_LEAF_BIT);
            }
            else {
                this.die(level, x, y, z);
            }
        }
    }
    
    private void die(final Level level, final int x, final int y, final int z) {
        this.spawnResources(level, x, y, z, level.getData(x, y, z));
        level.setTile(x, y, z, 0);
    }
    
    @Override
    public int getResourceCount(final Random random) {
        return (random.nextInt(20) == 0) ? 1 : 0;
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        return Tile.sapling.id;
    }
    
    @Override
    public void playerDestroy(final Level level, final Player player, final int x, final int y, final int z, final int data) {
        if (!level.isClientSide && player.getSelectedItem() != null && player.getSelectedItem().id == Item.shears.id) {
            player.awardStat(Stats.blockMined[this.id], 1);
            // drop leaf block instead of sapling
            this.popResource(level, x, y, z, new ItemInstance(Tile.leaves.id, 1, data & LEAF_TYPE_MASK));
        }
        else {
            super.playerDestroy(level, player, x, y, z, data);
        }
    }
    
    @Override
    protected int getSpawnResourcesAuxValue(final int data) {
        return data & LEAF_TYPE_MASK;
    }
    
    @Override
    public boolean isSolidRender() {
        return !this.allowSame;
    }
    
    @Override
    public int getTexture(final int face, final int data) {
        if ((data & LEAF_TYPE_MASK) == EVERGREEN_LEAF) {
            return this.tex + 80;
        }
        return this.tex;
    }
    
    public void setFancy(final boolean fancyGraphics) {
        this.allowSame = fancyGraphics;
        this.tex = this.oTex + (fancyGraphics ? 0 : 1);
    }
    
    @Override
    public void stepOn(final Level level, final int x, final int y, final int z, final Entity entity) {
        super.stepOn(level, x, y, z, entity);
    }
}
