// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.item.Item;
import java.util.Random;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class WebTile extends Tile
{
    public WebTile(final int id, final int tex) {
        super(id, tex, Material.web);
    }
    
    @Override
    public void entityInside(final Level level, final int x, final int y, final int z, final Entity entity) {
        entity.isStuckInWeb = true;
    }
    
    @Override
    public boolean isSolidRender() {
        return false;
    }
    
    @Override
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        return null;
    }
    
    @Override
    public boolean isCubeShaped() {
        return false;
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        return Item.string.id;
    }
}
