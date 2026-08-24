// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.Facing;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.material.Material;
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
        final float a = 1.0f;

        final float xRot = player.xRotO + (player.xRot - player.xRotO) * a;
        final float yRot = player.yRotO + (player.yRot - player.yRotO) * a;

        final double x = player.xo + (player.x - player.xo) * a;
        final double y = player.yo + (player.y - player.yo) * a + 1.62 - player.heightOffset;
        final double z = player.zo + (player.z - player.zo) * a;

        final Vec3 from = Vec3.newTemp(x, y, z);

        final float yCos = Mth.cos(-yRot * Mth.DEGRAD - Mth.PI);
        final float ySin = Mth.sin(-yRot * Mth.DEGRAD - Mth.PI);
        final float xCos = -Mth.cos(-xRot * Mth.DEGRAD);
        final float xSin = Mth.sin(-xRot * Mth.DEGRAD);

        final float xa = ySin * xCos;
        final float ya = xSin;
        final float za = yCos * xCos;

        final double range = 5.0;
        Vec3 to = from.add(xa * range, ya * range, za * range);
        boolean pickLiquid = this.content == 0;
        final HitResult hr = level.clip(from, to, pickLiquid);
        if (hr == null) return itemInstance;

        if (hr.type == HitResult.Type.TILE) {
            int xt = hr.x;
            int yt = hr.y;
            int zt = hr.z;

            if (!level.mayInteract(player, xt, yt, zt)) return itemInstance;

            if (this.content == 0) {
                if (level.getMaterial(xt, yt, zt) == Material.water && level.getData(xt, yt, zt) == 0) {
                    level.setTile(xt, yt, zt, 0);
                    return new ItemInstance(Item.bucket_water);
                }
                if (level.getMaterial(xt, yt, zt) == Material.lava && level.getData(xt, yt, zt) == 0) {
                    level.setTile(xt, yt, zt, 0);
                    return new ItemInstance(Item.bucket_lava);
                }
            }
            else {
                if (this.content < 0) return new ItemInstance(Item.bucket_empty);

                if (hr.f == Facing.DOWN) --yt;
                if (hr.f == Facing.UP) ++yt;
                if (hr.f == Facing.NORTH) --zt;
                if (hr.f == Facing.SOUTH) ++zt;
                if (hr.f == Facing.WEST) --xt;
                if (hr.f == Facing.EAST) ++xt;

                if (level.isEmptyTile(xt, yt, zt) || !level.getMaterial(xt, yt, zt).isSolid()) {
                    if (level.dimension.ultraWarm && this.content == Tile.water.id) {
                        level.playLocalSound(x + 0.5, y + 0.5, z + 0.5, "random.fizz", 0.5f, 2.6f + (level.random.nextFloat() - level.random.nextFloat()) * 0.8f);

                        for (int i = 0; i < 8; ++i) {
                            level.addParticle("largesmoke", xt + Math.random(), yt + Math.random(), zt + Math.random(), 0.0, 0.0, 0.0);
                        }
                    }
                    else {
                        level.setTileAndData(xt, yt, zt, this.content, 0);
                    }

                    return new ItemInstance(Item.bucket_empty);
                }
            }
        }
        else {
            if (this.content == 0) {
                if (hr.entity instanceof Cow) {
                    return new ItemInstance(Item.milk);
                }
            }
        }
        return itemInstance;
    }
}
