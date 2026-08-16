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
        final float n = 1.0f;
        final float n2 = player.xRotO + (player.xRot - player.xRotO) * n;
        final float n3 = player.yRotO + (player.yRot - player.yRotO) * n;
        final Vec3 temp = Vec3.newTemp(player.xo + (player.x - player.xo) * n, player.yo + (player.y - player.yo) * n + 1.62 - player.heightOffset, player.zo + (player.z - player.zo) * n);
        final float cos = Mth.cos(-n3 * Mth.DEGRAD - Mth.PI);
        final float sin = Mth.sin(-n3 * Mth.DEGRAD - Mth.PI);
        final float n4 = -Mth.cos(-n2 * Mth.DEGRAD);
        final float sin2 = Mth.sin(-n2 * Mth.DEGRAD);
        final float n5 = sin * n4;
        final float n6 = sin2;
        final float n7 = cos * n4;
        final double n8 = 5.0;
        final HitResult clip = level.clip(temp, temp.add(n5 * n8, n6 * n8, n7 * n8), true);
        if (clip == null) {
            return itemInstance;
        }
        if (clip.type == HitResult.Type.TILE) {
            final int x = clip.x;
            int y = clip.y;
            final int z = clip.z;
            if (!level.isClientSide) {
                if (level.getTile(x, y, z) == Tile.topSnow.id) {
                    --y;
                }
                level.addEntity(new Boat(level, x + 0.5f, y + 1.0f, z + 0.5f));
            }
            --itemInstance.count;
        }
        return itemInstance;
    }
}
