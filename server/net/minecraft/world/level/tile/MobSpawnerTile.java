// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import java.util.Random;
import net.minecraft.world.level.tile.entity.MobSpawnerTileEntity;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.level.material.Material;

public class MobSpawnerTile extends EntityTile
{
    protected MobSpawnerTile(final int id, final int tex) {
        super(id, tex, Material.stone);
    }
    
    @Override
    protected TileEntity newTileEntity() {
        return new MobSpawnerTileEntity();
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        return 0;
    }
    
    @Override
    public int getResourceCount(final Random random) {
        return 0;
    }
    
    @Override
    public boolean isSolidRender() {
        return false;
    }
}
