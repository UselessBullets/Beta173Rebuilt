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
    public int getColor(final int auxData) {
        if ((auxData & LEAF_TYPE_MASK) == EVERGREEN_LEAF) {
            return FoliageColor.getEvergreenColor();
        }
        if ((auxData & LEAF_TYPE_MASK) == BIRCH_LEAF) {
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
        return FoliageColor.get(level.getBiomeSource().temperatures[0], level.getBiomeSource().downfalls[0]);
    }
    
    @Override
    public void onRemove(final Level level, final int x, final int y, final int z) {
        final int n = 1;
        final int n2 = n + 1;
        if (level.hasChunksAt(x - n2, y - n2, z - n2, x + n2, y + n2, z + n2)) {
            for (int i = -n; i <= n; ++i) {
                for (int j = -n; j <= n; ++j) {
                    for (int k = -n; k <= n; ++k) {
                        if (level.getTile(x + i, y + j, z + k) == Tile.leaves.id) {
                            level.setDataNoUpdate(x + i, y + j, z + k, level.getData(x + i, y + j, z + k) | 0x8);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        if (level.isClientSide) {
            return;
        }
        final int data = level.getData(x, y, z);
        if ((data & 0x8) != 0x0) {
            final int n = 4;
            final int n2 = n + 1;
            final int n3 = 32;
            final int n4 = n3 * n3;
            final int n5 = n3 / 2;
            if (this.checkBuffer == null) {
                this.checkBuffer = new int[n3 * n3 * n3];
            }
            if (level.hasChunksAt(x - n2, y - n2, z - n2, x + n2, y + n2, z + n2)) {
                for (int i = -n; i <= n; ++i) {
                    for (int j = -n; j <= n; ++j) {
                        for (int k = -n; k <= n; ++k) {
                            final int tile = level.getTile(x + i, y + j, z + k);
                            if (tile == Tile.treeTrunk.id) {
                                this.checkBuffer[(i + n5) * n4 + (j + n5) * n3 + (k + n5)] = 0;
                            }
                            else if (tile == Tile.leaves.id) {
                                this.checkBuffer[(i + n5) * n4 + (j + n5) * n3 + (k + n5)] = -2;
                            }
                            else {
                                this.checkBuffer[(i + n5) * n4 + (j + n5) * n3 + (k + n5)] = -1;
                            }
                        }
                    }
                }
                for (int l = 1; l <= 4; ++l) {
                    for (int n6 = -n; n6 <= n; ++n6) {
                        for (int n7 = -n; n7 <= n; ++n7) {
                            for (int n8 = -n; n8 <= n; ++n8) {
                                if (this.checkBuffer[(n6 + n5) * n4 + (n7 + n5) * n3 + (n8 + n5)] == l - 1) {
                                    if (this.checkBuffer[(n6 + n5 - 1) * n4 + (n7 + n5) * n3 + (n8 + n5)] == -2) {
                                        this.checkBuffer[(n6 + n5 - 1) * n4 + (n7 + n5) * n3 + (n8 + n5)] = l;
                                    }
                                    if (this.checkBuffer[(n6 + n5 + 1) * n4 + (n7 + n5) * n3 + (n8 + n5)] == -2) {
                                        this.checkBuffer[(n6 + n5 + 1) * n4 + (n7 + n5) * n3 + (n8 + n5)] = l;
                                    }
                                    if (this.checkBuffer[(n6 + n5) * n4 + (n7 + n5 - 1) * n3 + (n8 + n5)] == -2) {
                                        this.checkBuffer[(n6 + n5) * n4 + (n7 + n5 - 1) * n3 + (n8 + n5)] = l;
                                    }
                                    if (this.checkBuffer[(n6 + n5) * n4 + (n7 + n5 + 1) * n3 + (n8 + n5)] == -2) {
                                        this.checkBuffer[(n6 + n5) * n4 + (n7 + n5 + 1) * n3 + (n8 + n5)] = l;
                                    }
                                    if (this.checkBuffer[(n6 + n5) * n4 + (n7 + n5) * n3 + (n8 + n5 - 1)] == -2) {
                                        this.checkBuffer[(n6 + n5) * n4 + (n7 + n5) * n3 + (n8 + n5 - 1)] = l;
                                    }
                                    if (this.checkBuffer[(n6 + n5) * n4 + (n7 + n5) * n3 + (n8 + n5 + 1)] == -2) {
                                        this.checkBuffer[(n6 + n5) * n4 + (n7 + n5) * n3 + (n8 + n5 + 1)] = l;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (this.checkBuffer[n5 * n4 + n5 * n3 + n5] >= 0) {
                level.setDataNoUpdate(x, y, z, data & 0xFFFFFFF7);
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
            this.popResource(level, x, y, z, new ItemInstance(Tile.leaves.id, 1, data & 0x3));
        }
        else {
            super.playerDestroy(level, player, x, y, z, data);
        }
    }
    
    @Override
    protected int getSpawnResourcesAuxValue(final int data) {
        return data & 0x3;
    }
    
    @Override
    public boolean isSolidRender() {
        return !this.allowSame;
    }
    
    @Override
    public int getTexture(final int face, final int data) {
        if ((data & 0x3) == 0x1) {
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
