// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.entity.MobSpawnerTileEntity;
import net.minecraft.world.level.tile.entity.ChestTileEntity;
import net.minecraft.world.level.tile.Tile;
import java.util.Random;
import net.minecraft.world.level.Level;

public class MonsterRoomFeature extends Feature
{
    @Override
    public boolean place(final Level level, final Random random, final int x, final int y, final int z) {
        final int n = 3;
        final int n2 = random.nextInt(2) + 2;
        final int n3 = random.nextInt(2) + 2;
        int n4 = 0;
        for (int i = x - n2 - 1; i <= x + n2 + 1; ++i) {
            for (int j = y - 1; j <= y + n + 1; ++j) {
                for (int k = z - n3 - 1; k <= z + n3 + 1; ++k) {
                    final Material material = level.getMaterial(i, j, k);
                    if (j == y - 1 && !material.isSolid()) {
                        return false;
                    }
                    if (j == y + n + 1 && !material.isSolid()) {
                        return false;
                    }
                    if ((i == x - n2 - 1 || i == x + n2 + 1 || k == z - n3 - 1 || k == z + n3 + 1) && j == y && level.isEmptyTile(i, j, k) && level.isEmptyTile(i, j + 1, k)) {
                        ++n4;
                    }
                }
            }
        }
        if (n4 < 1 || n4 > 5) {
            return false;
        }
        for (int l = x - n2 - 1; l <= x + n2 + 1; ++l) {
            for (int y2 = y + n; y2 >= y - 1; --y2) {
                for (int n5 = z - n3 - 1; n5 <= z + n3 + 1; ++n5) {
                    if (l == x - n2 - 1 || y2 == y - 1 || n5 == z - n3 - 1 || l == x + n2 + 1 || y2 == y + n + 1 || n5 == z + n3 + 1) {
                        if (y2 >= 0 && !level.getMaterial(l, y2 - 1, n5).isSolid()) {
                            level.setTile(l, y2, n5, 0);
                        }
                        else if (level.getMaterial(l, y2, n5).isSolid()) {
                            if (y2 == y - 1 && random.nextInt(4) != 0) {
                                level.setTile(l, y2, n5, Tile.mossStone.id);
                            }
                            else {
                                level.setTile(l, y2, n5, Tile.stoneBrick.id);
                            }
                        }
                    }
                    else {
                        level.setTile(l, y2, n5, 0);
                    }
                }
            }
        }
        for (int n6 = 0; n6 < 2; ++n6) {
            for (int n7 = 0; n7 < 3; ++n7) {
                final int x2 = x + random.nextInt(n2 * 2 + 1) - n2;
                final int z2 = z + random.nextInt(n3 * 2 + 1) - n3;
                if (level.isEmptyTile(x2, y, z2)) {
                    int n8 = 0;
                    if (level.getMaterial(x2 - 1, y, z2).isSolid()) {
                        ++n8;
                    }
                    if (level.getMaterial(x2 + 1, y, z2).isSolid()) {
                        ++n8;
                    }
                    if (level.getMaterial(x2, y, z2 - 1).isSolid()) {
                        ++n8;
                    }
                    if (level.getMaterial(x2, y, z2 + 1).isSolid()) {
                        ++n8;
                    }
                    if (n8 == 1) {
                        level.setTile(x2, y, z2, Tile.chest.id);
                        final ChestTileEntity chestTileEntity = (ChestTileEntity)level.getTileEntity(x2, y, z2);
                        for (int n9 = 0; n9 < 8; ++n9) {
                            final ItemInstance randomItem = this.randomItem(random);
                            if (randomItem != null) {
                                chestTileEntity.setItem(random.nextInt(chestTileEntity.getContainerSize()), randomItem);
                            }
                        }
                        break;
                    }
                }
            }
        }
        level.setTile(x, y, z, Tile.mobSpawner.id);
        ((MobSpawnerTileEntity)level.getTileEntity(x, y, z)).setEntityId(this.randomEntityId(random));
        return true;
    }
    
    private ItemInstance randomItem(final Random random) {
        final int nextInt = random.nextInt(11);
        if (nextInt == 0) {
            return new ItemInstance(Item.saddle);
        }
        if (nextInt == 1) {
            return new ItemInstance(Item.ironIngot, random.nextInt(4) + 1);
        }
        if (nextInt == 2) {
            return new ItemInstance(Item.bread);
        }
        if (nextInt == 3) {
            return new ItemInstance(Item.wheat, random.nextInt(4) + 1);
        }
        if (nextInt == 4) {
            return new ItemInstance(Item.sulphur, random.nextInt(4) + 1);
        }
        if (nextInt == 5) {
            return new ItemInstance(Item.string, random.nextInt(4) + 1);
        }
        if (nextInt == 6) {
            return new ItemInstance(Item.bucket_empty);
        }
        if (nextInt == 7 && random.nextInt(100) == 0) {
            return new ItemInstance(Item.apple_gold);
        }
        if (nextInt == 8 && random.nextInt(2) == 0) {
            return new ItemInstance(Item.redStone, random.nextInt(4) + 1);
        }
        if (nextInt == 9 && random.nextInt(10) == 0) {
            return new ItemInstance(Item.items[Item.record_01.id + random.nextInt(2)]);
        }
        if (nextInt == 10) {
            return new ItemInstance(Item.dye_powder, 1, 3);
        }
        return null;
    }
    
    private String randomEntityId(final Random random) {
        final int nextInt = random.nextInt(4);
        if (nextInt == 0) {
            return "Skeleton";
        }
        if (nextInt == 1) {
            return "Zombie";
        }
        if (nextInt == 2) {
            return "Zombie";
        }
        if (nextInt == 3) {
            return "Spider";
        }
        return "";
    }
}
