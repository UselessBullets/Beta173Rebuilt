// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.level.LevelEvent;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class TrapDoorTile extends Tile
{
    protected TrapDoorTile(final int id, final Material material) {
        super(id, material);
        this.tex = 84;
        if (material == Material.metal) this.tex++;
        float r = 0.5f;
        float h = 1.0f;
        this.setShape(0.5f - r, 0.0f, 0.5f - r, 0.5f + r, h, 0.5f + r);
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
        return Tile.SHAPE_BLOCK;
    }
    
    @Override
    public AABB getTileAABB(final Level level, final int x, final int y, final int z) {
        this.updateShape(level, x, y, z);
        return super.getTileAABB(level, x, y, z);
    }
    
    @Override
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        this.updateShape(level, x, y, z);
        return super.getAABB(level, x, y, z);
    }
    
    @Override
    public void updateShape(final LevelSource level, final int x, final int y, final int z) {
        this.setShape(level.getData(x, y, z));
    }
    
    @Override
    public void updateDefaultShape() {
        final float r = 3 / 16.0f;
        this.setShape(0.0f, 0.5f - r / 2.0f, 0.0f, 1.0f, 0.5f + r / 2.0f, 1.0f);
    }
    
    public void setShape(final int data) {
        final float r = 3 / 16.0f;
        this.setShape(0, 0, 0, 1, r, 1);
        if (isOpen(data)) {
            if ((data & 0x3) == 0) this.setShape(0, 0, 1 - r, 1, 1, 1);
            if ((data & 0x3) == 1) this.setShape(0, 0, 0, 1, 1, r);
            if ((data & 0x3) == 2) this.setShape(1 - r, 0, 0, 1, 1, 1);
            if ((data & 0x3) == 3) this.setShape(0, 0, 0, r, 1, 1);
        }
    }
    
    @Override
    public void attack(final Level level, final int x, final int y, final int z, final Player player) {
        this.use(level, x, y, z, player);
    }
    
    @Override
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        if (this.material == Material.metal) return true;

        int dir = level.getData(x, y, z);
        level.setData(x, y, z, dir ^ 0x4);

        level.levelEvent(player, LevelEvent.SOUND_OPEN_DOOR, x, y, z, 0);
        return true;
    }
    
    public void setOpen(final Level level, final int x, final int y, final int z, final boolean shouldOpen) {
        final int dir = level.getData(x, y, z);

        boolean wasOpen = (dir & 0x4) > 0;
        if (wasOpen == shouldOpen) return;

        level.setData(x, y, z, dir ^ 0x4);

        level.levelEvent(null, LevelEvent.SOUND_OPEN_DOOR, x, y, z, 0);
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        if (level.isClientSide) return;

        final int data = level.getData(x, y, z);
        int xt = x;
        int zt = z;
        if ((data & 0x3) == 0) zt++;
        if ((data & 0x3) == 1) zt--;
        if ((data & 0x3) == 2) xt++;
        if ((data & 0x3) == 3) xt--;

        if (!level.isSolidBlockingTile(xt, y, zt)) {
            level.setTile(x, y, z, 0);
            this.spawnResources(level, x, y, z, data);
        }

        if (type > 0 && Tile.tiles[type].isSignalSource()) {
            boolean signal = level.hasNeighborSignal(x, y, z);
            this.setOpen(level, x, y, z, signal);
        }
    }
    
    @Override
    public HitResult clip(final Level level, final int xt, final int yt, final int zt, final Vec3 a, final Vec3 b) {
        this.updateShape(level, xt, yt, zt);
        return super.clip(level, xt, yt, zt, a, b);
    }
    
    @Override
    public void setPlacedOnFace(final Level level, final int x, final int y, final int z, final int face) {
        int dir = 0;
        if (face == 2) dir = 0;
        if (face == 3) dir = 1;
        if (face == 4) dir = 2;
        if (face == 5) dir = 3;
        level.setData(x, y, z, dir);
    }
    
    @Override
    public boolean mayPlace(final Level level, int x, final int y, int z, final int face) {
        if (face == 0) return false;
        if (face == 1) return false;
        if (face == 2) z++;
        if (face == 3) z--;
        if (face == 4) x++;
        if (face == 5) x--;

        return level.isSolidBlockingTile(x, y, z);
    }
    
    public static boolean isOpen(final int data) {
        return (data & 0x4) != 0x0;
    }
}
