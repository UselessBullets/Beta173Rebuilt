// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.item.DyePowderItem;
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
        final int hr = 3;
        final int xr = random.nextInt(2) + 2;
        final int zr = random.nextInt(2) + 2;

        int holeCount = 0;
        for (int xx = x - xr - 1; xx <= x + xr + 1; ++xx) {
            for (int yy = y - 1; yy <= y + hr + 1; ++yy) {
                for (int zz = z - zr - 1; zz <= z + zr + 1; ++zz) {
                    final Material m = level.getMaterial(xx, yy, zz);
                    if (yy == y - 1 && !m.isSolid()) return false;
                    if (yy == y + hr + 1 && !m.isSolid()) return false;

                    if (xx == x - xr - 1 || xx == x + xr + 1 || zz == z - zr - 1 || zz == z + zr + 1) {
                        if (yy == y && level.isEmptyTile(xx, yy, zz) && level.isEmptyTile(xx, yy + 1, zz)) {
                            ++holeCount;
                        }
                    }
                }
            }
        }

        if (holeCount < 1 || holeCount > 5) return false;

        for (int xx = x - xr - 1; xx <= x + xr + 1; ++xx) {
            for (int yy = y + hr; yy >= y - 1; --yy) {
                for (int zz = z - zr - 1; zz <= z + zr + 1; ++zz) {
                    if (xx == x - xr - 1 || yy == y - 1 || zz == z - zr - 1 || xx == x + xr + 1 || yy == y + hr + 1 || zz == z + zr + 1) {
                        if (yy >= 0 && !level.getMaterial(xx, yy - 1, zz).isSolid()) {
                            level.setTile(xx, yy, zz, 0);
                        }
                        else if (level.getMaterial(xx, yy, zz).isSolid()) {
                            if (yy == y - 1 && random.nextInt(4) != 0) {
                                level.setTile(xx, yy, zz, Tile.mossStone.id);
                            }
                            else {
                                level.setTile(xx, yy, zz, Tile.stoneBrick.id);
                            }
                        }
                    }
                    else {
                        level.setTile(xx, yy, zz, 0);
                    }
                }
            }
        }

        for (int cc = 0; cc < 2; ++cc) {
            for (int i = 0; i < 3; ++i) {
                final int xc = x + random.nextInt(xr * 2 + 1) - xr;
                final int yc = y;
                final int zc = z + random.nextInt(zr * 2 + 1) - zr;
                if (!level.isEmptyTile(xc, yc, zc)) continue;

                int count = 0;
                if (level.getMaterial(xc - 1, yc, zc).isSolid()) count++;
                if (level.getMaterial(xc + 1, yc, zc).isSolid()) count++;
                if (level.getMaterial(xc, yc, zc - 1).isSolid()) count++;
                if (level.getMaterial(xc, yc, zc + 1).isSolid()) count++;

                if (count != 1) continue;

                level.setTile(xc, yc, zc, Tile.chest.id);
                final ChestTileEntity chest = (ChestTileEntity)level.getTileEntity(xc, yc, zc);
                for (int j = 0; j < 8; ++j) {
                    final ItemInstance item = this.randomItem(random);
                    if (item != null) chest.setItem(random.nextInt(chest.getContainerSize()), item);
                }

                break;
            }
        }
        level.setTile(x, y, z, Tile.mobSpawner.id);
        MobSpawnerTileEntity entity = ((MobSpawnerTileEntity)level.getTileEntity(x, y, z));
        entity.setEntityId(this.randomEntityId(random));
        return true;
    }
    
    private ItemInstance randomItem(final Random random) {
        final int type = random.nextInt(11);
        if (type == 0) return new ItemInstance(Item.saddle);
        if (type == 1) return new ItemInstance(Item.ironIngot, random.nextInt(4) + 1);
        if (type == 2) return new ItemInstance(Item.bread);
        if (type == 3) return new ItemInstance(Item.wheat, random.nextInt(4) + 1);
        if (type == 4) return new ItemInstance(Item.sulphur, random.nextInt(4) + 1);
        if (type == 5) return new ItemInstance(Item.string, random.nextInt(4) + 1);
        if (type == 6) return new ItemInstance(Item.bucket_empty);
        if (type == 7 && random.nextInt(100) == 0) return new ItemInstance(Item.apple_gold);
        if (type == 8 && random.nextInt(2) == 0) return new ItemInstance(Item.redStone, random.nextInt(4) + 1);
        if (type == 9 && random.nextInt(10) == 0) return new ItemInstance(Item.items[Item.record_01.id + random.nextInt(2)]);
        if (type == 10) return new ItemInstance(Item.dye_powder, 1, DyePowderItem.BROWN);

        return null;
    }
    
    private String randomEntityId(final Random random) {
        final int nextInt = random.nextInt(4);
        if (nextInt == 0) return "Skeleton";
        if (nextInt == 1) return "Zombie";
        if (nextInt == 2) return "Zombie";
        if (nextInt == 3) return "Spider";
        return "";
    }
}
