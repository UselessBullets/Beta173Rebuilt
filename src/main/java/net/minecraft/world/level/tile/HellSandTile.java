// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class HellSandTile extends Tile
{

    public HellSandTile(final int id, final int tex) {
        super(id, tex, Material.sand);
    }
    
    @Override
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        float r = 2 / 16.0f;
        return AABB.newTemp(x, y, z, x + 1, y + 1 - r, z + 1);
    }
    
    @Override
    public void entityInside(final Level level, final int x, final int y, final int z, final Entity entity) {
        entity.xd *= 0.4;
        entity.zd *= 0.4;
    }
}
