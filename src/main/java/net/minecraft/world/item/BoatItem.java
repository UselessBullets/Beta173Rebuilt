// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.item;

import net.minecraft.world.phys.HitResult;
import net.minecraft.world.entity.item.Boat;
import net.minecraft.world.level.tile.Tile;
import util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class BoatItem extends Item
{
    public BoatItem(final int id) {
        super(id);
        this.maxStackSize = 1;
    }
    
    @Override
    public ItemInstance use(final ItemInstance itemInstance, final Level level, final Player player) {
        final float a = 1.0f;
        final float xRot = player.xRotO + (player.xRot - player.xRotO) * a;
        final float yRot = player.yRotO + (player.yRot - player.yRotO) * a;

        double x = player.xo + (player.x - player.xo) * a;
        double y = player.yo + (player.y - player.yo) * a + 1.62 - player.heightOffset;
        double z = player.zo + (player.z - player.zo) * a;

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
        final HitResult hr = level.clip(from, to, true);
        if (hr == null) return itemInstance;

        if (hr.type == HitResult.Type.TILE) {
            int xt = hr.x;
            int yt = hr.y;
            int zt = hr.z;

            if (!level.isClientSide) {
                if (level.getTile(xt, yt, zt) == Tile.topSnow.id) yt--;
                level.addEntity(new Boat(level, xt + 0.5f, yt + 1.0f, zt + 0.5f));
            }
            itemInstance.count--;
        }
        return itemInstance;
    }
}
