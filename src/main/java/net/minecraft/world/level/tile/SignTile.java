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
    private Class<? extends TileEntity> clas;
    private boolean onGround;
    
    protected SignTile(final int id, final Class<? extends TileEntity> clas, final boolean onGround) {
        super(id, Material.wood);
        this.onGround = onGround;
        this.tex = 4;
        this.clas = clas;
        final float r = 4 / 16.0f;
        float h = 16 / 16.0f;
        this.setShape(0.5f - r, 0.0f, 0.5f - r, 0.5f + r, h, 0.5f + r);
    }
    
    @Override
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        return null;
    }
    
    @Override
    public AABB getTileAABB(final Level level, final int x, final int y, final int z) {
        this.updateShape(level, x, y, z);
        return super.getTileAABB(level, x, y, z);
    }
    
    @Override
    public void updateShape(final LevelSource level, final int x, final int y, final int z) {
        if (this.onGround) return;

        final int face = level.getData(x, y, z);

        final float h0 = (4 + 0.5f) / 16.0f;
        final float h1 = (12 + 0.5f) / 16.0f;
        final float w0 = 0 / 16.0f;
        final float w1 = 16 / 16.0f;

        final float d = 2 / 16.0f;

        this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        if (face == 2) this.setShape(w0, h0, 1.0f - d, w1, h1, 1.0f);
        if (face == 3) this.setShape(w0, h0, 0.0f, w1, h1, d);
        if (face == 4) this.setShape(1.0f - d, h0, w0, 1.0f, h1, w1);
        if (face == 5) this.setShape(0.0f, h0, w0, d, h1, w1);
    }
    
    @Override
    public int getRenderShape() {
        return Tile.SHAPE_INVISIBLE;
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
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        return Item.sign.id;
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        boolean remove = false;

        if (this.onGround) {
            if (!level.getMaterial(x, y - 1, z).isSolid()) remove = true;
        }
        else {
            final int face = level.getData(x, y, z);
            remove = true;
            if (face == 2 && level.getMaterial(x, y, z + 1).isSolid()) remove = false;
            if (face == 3 && level.getMaterial(x, y, z - 1).isSolid()) remove = false;
            if (face == 4 && level.getMaterial(x + 1, y, z).isSolid()) remove = false;
            if (face == 5 && level.getMaterial(x - 1, y, z).isSolid()) remove = false;
        }
        if (remove) {
            this.spawnResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
        }

        super.neighborChanged(level, x, y, z, type);
    }
}
