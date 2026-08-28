// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import java.util.Random;
import java.util.ArrayList;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;

public class StairTile extends Tile
{
    private Tile base;
    
    protected StairTile(final int id, final Tile base) {
        super(id, base.tex, base.material);
        this.base = base;
        this.setDestroyTime(base.destroySpeed);
        this.setExplodeable(base.explosionResistance / 3.0f);
        this.setSoundType(base.soundType);
        this.setLightBlock(255);
    }
    
    @Override
    public void updateShape(final LevelSource level, final int x, final int y, final int z) {
        this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    }
    
    @Override
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        return super.getAABB(level, x, y, z);
    }
    
    @Override
    public boolean isSolidRender() {
        return false;
    }
    
    @Override
    public boolean isCubeShaped() {
        return false;
    }
    
    @Override
    public int getRenderShape() {
        return Tile.SHAPE_STAIRS;
    }
    
    @Override
    public boolean shouldRenderFace(final LevelSource level, final int x, final int y, final int z, final int f) {
        return super.shouldRenderFace(level, x, y, z, f);
    }
    
    @Override
    public void addAABBs(final Level level, final int x, final int y, final int z, final AABB box, final ArrayList boxes) {
        final int data = level.getData(x, y, z);
        if (data == 0) {
            this.setShape(0.0f, 0.0f, 0.0f, 0.5f, 0.5f, 1.0f);
            super.addAABBs(level, x, y, z, box, boxes);
            this.setShape(0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
            super.addAABBs(level, x, y, z, box, boxes);
        }
        else if (data == 1) {
            this.setShape(0.0f, 0.0f, 0.0f, 0.5f, 1.0f, 1.0f);
            super.addAABBs(level, x, y, z, box, boxes);
            this.setShape(0.5f, 0.0f, 0.0f, 1.0f, 0.5f, 1.0f);
            super.addAABBs(level, x, y, z, box, boxes);
        }
        else if (data == 2) {
            this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 0.5f, 0.5f);
            super.addAABBs(level, x, y, z, box, boxes);
            this.setShape(0.0f, 0.0f, 0.5f, 1.0f, 1.0f, 1.0f);
            super.addAABBs(level, x, y, z, box, boxes);
        }
        else if (data == 3) {
            this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.5f);
            super.addAABBs(level, x, y, z, box, boxes);
            this.setShape(0.0f, 0.0f, 0.5f, 1.0f, 0.5f, 1.0f);
            super.addAABBs(level, x, y, z, box, boxes);
        }
        this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    }
    
    @Override
    public void animateTick(final Level level, final int x, final int y, final int z, final Random random) {
        this.base.animateTick(level, x, y, z, random);
    }
    
    @Override
    public void attack(final Level level, final int x, final int y, final int z, final Player player) {
        this.base.attack(level, x, y, z, player);
    }
    
    @Override
    public void destroy(final Level level, final int x, final int y, final int z, final int data) {
        this.base.destroy(level, x, y, z, data);
    }
    
    @Override
    public float getBrightness(final LevelSource level, final int x, final int y, final int z) {
        return this.base.getBrightness(level, x, y, z);
    }
    
    @Override
    public float getExplosionResistance(final Entity source) {
        return this.base.getExplosionResistance(source);
    }
    
    @Override
    public int getRenderLayer() {
        return this.base.getRenderLayer();
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        return this.base.getResource(data, random);
    }
    
    @Override
    public int getResourceCount(final Random random) {
        return this.base.getResourceCount(random);
    }
    
    @Override
    public int getTexture(final int face, final int data) {
        return this.base.getTexture(face, data);
    }
    
    @Override
    public int getTexture(final int face) {
        return this.base.getTexture(face);
    }
    
    @Override
    public int getTexture(final LevelSource level, final int x, final int y, final int z, final int face) {
        return this.base.getTexture(level, x, y, z, face);
    }
    
    @Override
    public int getTickDelay() {
        return this.base.getTickDelay();
    }
    
    @Override
    public AABB getTileAABB(final Level level, final int x, final int y, final int z) {
        return this.base.getTileAABB(level, x, y, z);
    }
    
    @Override
    public void handleEntityInside(final Level level, final int x, final int y, final int z, final Entity e, final Vec3 current) {
        this.base.handleEntityInside(level, x, y, z, e, current);
    }
    
    @Override
    public boolean mayPick() {
        return this.base.mayPick();
    }
    
    @Override
    public boolean mayPick(final int data, final boolean liquid) {
        return this.base.mayPick(data, liquid);
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        return this.base.mayPlace(level, x, y, z);
    }
    
    @Override
    public void onPlace(final Level level, final int x, final int y, final int z) {
        this.neighborChanged(level, x, y, z, 0);
        this.base.onPlace(level, x, y, z);
    }
    
    @Override
    public void onRemove(final Level level, final int x, final int y, final int z) {
        this.base.onRemove(level, x, y, z);
    }
    
    @Override
    public void spawnResources(final Level level, final int x, final int y, final int z, final int data, final float odds) {
        this.base.spawnResources(level, x, y, z, data, odds);
    }
    
    @Override
    public void stepOn(final Level level, final int x, final int y, final int z, final Entity entity) {
        this.base.stepOn(level, x, y, z, entity);
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        this.base.tick(level, x, y, z, random);
    }
    
    @Override
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        return this.base.use(level, x, y, z, player);
    }
    
    @Override
    public void wasExploded(final Level level, final int x, final int y, final int z) {
        this.base.wasExploded(level, x, y, z);
    }
    
    @Override
    public void setPlacedBy(final Level level, final int x, final int y, final int z, final Mob by) {
        final int n = Mth.floor(by.yRot * 4.0f / 360.0f + 0.5) & 0x3;
        if (n == 0) {
            level.setData(x, y, z, 2);
        }
        if (n == 1) {
            level.setData(x, y, z, 1);
        }
        if (n == 2) {
            level.setData(x, y, z, 3);
        }
        if (n == 3) {
            level.setData(x, y, z, 0);
        }
    }
}
