// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.item.Item;
import java.util.Random;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class SignTile extends EntityTile
{
    private Class clas;
    private boolean onGround;
    
    protected SignTile(final int id, final Class clas, final boolean onGround) {
        super(id, Material.wood);
        this.onGround = onGround;
        this.tex = 4;
        this.clas = clas;
        final float n = 0.25f;
        this.setShape(0.5f - n, 0.0f, 0.5f - n, 0.5f + n, 1.0f, 0.5f + n);
    }
    
    @Override
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        return null;
    }
    
    @Override
    public void updateShape(final LevelSource level, final int x, final int y, final int z) {
        if (this.onGround) {
            return;
        }
        final int data = level.getData(x, y, z);
        final float n = 0.28125f;
        final float n2 = 0.78125f;
        final float n3 = 0.0f;
        final float n4 = 1.0f;
        final float n5 = 0.125f;
        this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        if (data == 2) {
            this.setShape(n3, n, 1.0f - n5, n4, n2, 1.0f);
        }
        if (data == 3) {
            this.setShape(n3, n, 0.0f, n4, n2, n5);
        }
        if (data == 4) {
            this.setShape(1.0f - n5, n, n3, 1.0f, n2, n4);
        }
        if (data == 5) {
            this.setShape(0.0f, n, n3, n5, n2, n4);
        }
    }
    
    @Override
    public boolean isCubeShaped() {
        return false;
    }
    
    @Override
    public boolean isSolidRender() {
        return false;
    }
    
    @Override
    protected TileEntity newTileEntity() {
        try {
            return this.clas.newInstance();
        }
        catch (final Exception cause) {
            throw new RuntimeException(cause);
        }
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        return Item.sign.id;
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        boolean b = false;
        if (this.onGround) {
            if (!level.getMaterial(x, y - 1, z).isSolid()) {
                b = true;
            }
        }
        else {
            final int data = level.getData(x, y, z);
            b = true;
            if (data == 2 && level.getMaterial(x, y, z + 1).isSolid()) {
                b = false;
            }
            if (data == 3 && level.getMaterial(x, y, z - 1).isSolid()) {
                b = false;
            }
            if (data == 4 && level.getMaterial(x + 1, y, z).isSolid()) {
                b = false;
            }
            if (data == 5 && level.getMaterial(x - 1, y, z).isSolid()) {
                b = false;
            }
        }
        if (b) {
            this.spawnResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
        }
        super.neighborChanged(level, x, y, z, type);
    }
}
