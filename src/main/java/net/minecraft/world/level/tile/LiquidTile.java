// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.Facing;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import java.util.Random;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;

public abstract class LiquidTile extends Tile
{
    protected LiquidTile(final int id, final Material material) {
        super(id, ((material == Material.lava) ? 14 : 12) * 16 + 13, material);
        final float yo = 0.0f;
        final float e = 0.0f;

        this.setShape(0.0f + e, 0.0f + yo, 0.0f + e, 1.0f + e, 1.0f + yo, 1.0f + e);
        this.setTicking(true);
    }
    
    @Override
    public int getColor(final LevelSource level, final int x, final int y, final int z) {
        return 0xffffff;
    }
    
    public static float getHeight(int d) {
        if (d >= 8) d = 0;
        return (d + 1) / 9.0f;
    }
    
    @Override
    public int getTexture(final int face) {
        if (face == Facing.DOWN || face == Facing.UP) {
            return this.tex;
        } else {
            return this.tex + 1;
        }
    }
    
    protected int getDepth(final Level level, final int x, final int y, final int z) {
        if (level.getMaterial(x, y, z) == this.material) return level.getData(x, y, z);
        else return -1;
    }
    
    protected int getRenderedDepth(final LevelSource level, final int x, final int y, final int z) {
        if (level.getMaterial(x, y, z) != this.material) return -1;
        int d = level.getData(x, y, z);
        if (d >= 8) d = 0;
        return d;
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
    public boolean mayPick(final int data, final boolean liquid) {
        return liquid && data == 0;
    }
    
    @Override
    public boolean isSolidFace(final LevelSource level, final int x, final int y, final int z, final int face) {
        final Material m = level.getMaterial(x, y, z);
        if (m == this.material) return false;
        if (face == Facing.UP) return true;
        if (m == Material.ice) return false;

        return super.isSolidFace(level, x, y, z, face);
    }
    
    @Override
    public boolean isFaceVisible(final LevelSource level, final int x, final int y, final int z, final int f) {
        final Material m = level.getMaterial(x, y, z);
        if (m == this.material) return false;
        if (f == Facing.UP) return true;
        if (m == Material.ice) return false;
        return super.isFaceVisible(level, x, y, z, f);
    }
    
    @Override
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        return null;
    }
    
    @Override
    public int getRenderShape() {
        return Tile.SHAPE_WATER;
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        return 0;
    }
    
    @Override
    public int getResourceCount(final Random random) {
        return 0;
    }
    
    private Vec3 getFlow(final LevelSource level, final int x, final int y, final int z) {
        Vec3 flow = Vec3.newTemp(0.0, 0.0, 0.0);
        final int mid = this.getRenderedDepth(level, x, y, z);
        for (int d = 0; d < 4; ++d) {
            int xt = x;
            int yt = y;
            int zt = z;

            if (d == 0) xt--;
            if (d == 1) zt--;
            if (d == 2) xt++;
            if (d == 3) zt++;

            int t = this.getRenderedDepth(level, xt, yt, zt);
            if (t < 0) {
                if (!level.getMaterial(xt, yt, zt).blocksMotion()) {
                    t = this.getRenderedDepth(level, xt, y - 1, zt);
                    if (t >= 0) {
                        final int dir = t - (mid - 8);
                        flow = flow.add((xt - x) * dir, (yt - y) * dir, (zt - z) * dir);
                    }
                }
            }
            else {
                if (t >= 0) {
                    final int dir = t - mid;
                    flow = flow.add((xt - x) * dir, (yt - y) * dir, (zt - z) * dir);
                }
            }
        }
        if (level.getData(x, y, z) >= 8) {
            boolean ok = false;
            if (ok || this.isSolidFace(level, x, y, z - 1, 2)) ok = true;
            if (ok || this.isSolidFace(level, x, y, z + 1, 3)) ok = true;
            if (ok || this.isSolidFace(level, x - 1, y, z, 4)) ok = true;
            if (ok || this.isSolidFace(level, x + 1, y, z, 5)) ok = true;
            if (ok || this.isSolidFace(level, x, y + 1, z - 1, 2)) ok = true;
            if (ok || this.isSolidFace(level, x, y + 1, z + 1, 3)) ok = true;
            if (ok || this.isSolidFace(level, x - 1, y + 1, z, 4)) ok = true;
            if (ok || this.isSolidFace(level, x + 1, y + 1, z, 5)) ok = true;
            if (ok) flow = flow.normalize().add(0.0, -6.0, 0.0);
        }
        flow = flow.normalize();
        return flow;
    }
    
    @Override
    public void handleEntityInside(final Level level, final int x, final int y, final int z, final Entity e, final Vec3 current) {
        final Vec3 flow = this.getFlow(level, x, y, z);
        current.x += flow.x;
        current.y += flow.y;
        current.z += flow.z;
    }
    
    @Override
    public int getTickDelay() {
        if (this.material == Material.water) return 5;
        if (this.material == Material.lava) return 30;
        return 0;
    }
    
    @Override
    public float getBrightness(final LevelSource level, final int x, final int y, final int z) {
        final float a = level.getBrightness(x, y, z);
        final float b = level.getBrightness(x, y + 1, z);
        return (a > b) ? a : b;
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        super.tick(level, x, y, z, random);
    }
    
    @Override
    public int getRenderLayer() {
        return (this.material == Material.water) ? 1 : 0;
    }
    
    @Override
    public void animateTick(final Level level, final int x, final int y, final int z, final Random random) {
        if (this.material == Material.water) {
            if (random.nextInt(64) == 0) {
                final int d = level.getData(x, y, z);
                if (d > 0 && d < 8) {
                    level.playLocalSound(x + 0.5f, y + 0.5f, z + 0.5f, "liquid.water", random.nextFloat() * 0.25f + 0.75f, random.nextFloat() * 1.0f + 0.5f);
                }
            }
            // Useless - was in LCE leak, is set in such a way where it'd never run
//            for (int i = 0; i < 0; i++)
//            {	// This was an attempt to add foam to
//                // the bottoms of waterfalls. It
//                // didn't went ok.
//                int dir = random.nextInt(4);
//                int xt = x;
//                int zt = z;
//                if (dir == 0) xt--;
//                if (dir == 1) xt++;
//                if (dir == 2) zt--;
//                if (dir == 3) zt++;
//                if (level.getMaterial(xt, y, zt) == Material.air && (level.getMaterial(xt, y - 1, zt).blocksMotion() || level.getMaterial(xt, y - 1, zt).isLiquid()))
//                {
//                    float r = 1 / 16.0f;
//                    double xx = x + random.nextFloat();
//                    double yy = y + random.nextFloat();
//                    double zz = z + random.nextFloat();
//                    if (dir == 0) xx = x - r;
//                    if (dir == 1) xx = x + 1 + r;
//                    if (dir == 2) zz = z - r;
//                    if (dir == 3) zz = z + 1 + r;
//
//                    double xd = 0;
//                    double zd = 0;
//
//                    if (dir == 0) xd = -r;
//                    if (dir == 1) xd = +r;
//                    if (dir == 2) zd = -r;
//                    if (dir == 3) zd = +r;
//
//                    level.addParticle("splash", xx, yy, zz, xd, 0, zd);
//                }
//            }
        }
        if (this.material == Material.lava) {
            if (level.getMaterial(x, y + 1, z) == Material.air && !level.isSolidTile(x, y + 1, z)) {
                if (random.nextInt(100) == 0) {
                    level.addParticle("lava", x + random.nextFloat(), y + this.yy1, z + random.nextFloat(), 0.0, 0.0, 0.0);
                }
            }
        }
    }
    
    public static double getSlopeAngle(final LevelSource level, final int x, final int y, final int z, final Material m) {
        Vec3 flow = null;
        if (m == Material.water) flow = ((LiquidTile) Tile.water).getFlow(level, x, y, z);
        if (m == Material.lava) flow = ((LiquidTile) Tile.lava).getFlow(level, x, y, z);
        if (flow.x == 0.0 && flow.z == 0.0) return -1000.0;
        return Math.atan2(flow.z, flow.x) - Math.PI / 2;
    }
    
    @Override
    public void onPlace(final Level level, final int x, final int y, final int z) {
        this.updateLiquid(level, x, y, z);
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        this.updateLiquid(level, x, y, z);
    }
    
    private void updateLiquid(final Level level, final int x, final int y, final int z) {
        if (level.getTile(x, y, z) != this.id) return;

        if (this.material == Material.lava) {
            boolean water = false;
            if (water || level.getMaterial(x, y, z - 1) == Material.water) water = true;
            if (water || level.getMaterial(x, y, z + 1) == Material.water) water = true;
            if (water || level.getMaterial(x - 1, y, z) == Material.water) water = true;
            if (water || level.getMaterial(x + 1, y, z) == Material.water) water = true;
            if (water || level.getMaterial(x, y + 1, z) == Material.water) water = true;
            if (water) {
                final int data = level.getData(x, y, z);
                if (data == 0) {
                    level.setTile(x, y, z, Tile.obsidian.id);
                }
                else if (data <= 4) {
                    level.setTile(x, y, z, Tile.stoneBrick.id);
                }
                this.fizz(level, x, y, z);
            }
        }
    }
    
    protected void fizz(final Level level, final int x, final int y, final int z) {
        level.playLocalSound(x + 0.5f, y + 0.5f, z + 0.5f, "random.fizz", 0.5f, 2.6f + (level.random.nextFloat() - level.random.nextFloat()) * 0.8f);
        for (int i = 0; i < 8; ++i) {
            level.addParticle("largesmoke", x + Math.random(), y + 1.2, z + Math.random(), 0.0, 0.0, 0.0);
        }
    }
}
