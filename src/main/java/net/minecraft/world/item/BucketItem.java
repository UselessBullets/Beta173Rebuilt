// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.phys.HitResult;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.HitResult_Type;
import util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class BucketItem extends Item
{
    private int content;
    
    public BucketItem(final int id, final int content) {
        super(id);
        this.maxStackSize = 1;
        this.content = content;
    }
    
    @Override
    public ItemInstance use(final ItemInstance itemInstance, final Level level, final Player player) {
        final float n = 1.0f;
        final float n2 = player.xRotO + (player.xRot - player.xRotO) * n;
        final float n3 = player.yRotO + (player.yRot - player.yRotO) * n;
        final double x = player.xo + (player.x - player.xo) * n;
        final double y = player.yo + (player.y - player.yo) * n + 1.62 - player.heightOffset;
        final double z = player.zo + (player.z - player.zo) * n;
        final Vec3 temp = Vec3.newTemp(x, y, z);
        final float cos = Mth.cos(-n3 * 0.017453292f - 3.1415927f);
        final float sin = Mth.sin(-n3 * 0.017453292f - 3.1415927f);
        final float n4 = -Mth.cos(-n2 * 0.017453292f);
        final float sin2 = Mth.sin(-n2 * 0.017453292f);
        final float n5 = sin * n4;
        final float n6 = sin2;
        final float n7 = cos * n4;
        final double n8 = 5.0;
        final HitResult clip = level.clip(temp, temp.add(n5 * n8, n6 * n8, n7 * n8), this.content == 0);
        if (clip == null) {
            return itemInstance;
        }
        if (clip.type == HitResult_Type.TILE) {
            int x2 = clip.x;
            int y2 = clip.y;
            int z2 = clip.z;
            if (!level.mayInteract(player, x2, y2, z2)) {
                return itemInstance;
            }
            if (this.content == 0) {
                if (level.getMaterial(x2, y2, z2) == Material.water && level.getData(x2, y2, z2) == 0) {
                    level.setTile(x2, y2, z2, 0);
                    return new ItemInstance(Item.bucket_water);
                }
                if (level.getMaterial(x2, y2, z2) == Material.lava && level.getData(x2, y2, z2) == 0) {
                    level.setTile(x2, y2, z2, 0);
                    return new ItemInstance(Item.bucket_lava);
                }
            }
            else {
                if (this.content < 0) {
                    return new ItemInstance(Item.bucket_empty);
                }
                if (clip.f == 0) {
                    --y2;
                }
                if (clip.f == 1) {
                    ++y2;
                }
                if (clip.f == 2) {
                    --z2;
                }
                if (clip.f == 3) {
                    ++z2;
                }
                if (clip.f == 4) {
                    --x2;
                }
                if (clip.f == 5) {
                    ++x2;
                }
                if (level.isEmptyTile(x2, y2, z2) || !level.getMaterial(x2, y2, z2).isSolid()) {
                    if (level.dimension.ultraWarm && this.content == Tile.water.id) {
                        level.playLocalSound(x + 0.5, y + 0.5, z + 0.5, "random.fizz", 0.5f, 2.6f + (level.random.nextFloat() - level.random.nextFloat()) * 0.8f);
                        for (int i = 0; i < 8; ++i) {
                            level.addParticle("largesmoke", x2 + Math.random(), y2 + Math.random(), z2 + Math.random(), 0.0, 0.0, 0.0);
                        }
                    }
                    else {
                        level.setTileAndData(x2, y2, z2, this.content, 0);
                    }
                    return new ItemInstance(Item.bucket_empty);
                }
            }
        }
        else if (this.content == 0 && clip.entity instanceof Cow) {
            return new ItemInstance(Item.milk);
        }
        return itemInstance;
    }
}
