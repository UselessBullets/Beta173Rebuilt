// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

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
        final float n = 0.0f;
        final float n2 = 0.0f;
        this.setShape(0.0f + n2, 0.0f + n, 0.0f + n2, 1.0f + n2, 1.0f + n, 1.0f + n2);
        this.setTicking(true);
    }
    
    @Override
    public int getColor(final LevelSource level, final int x, final int y, final int z) {
        return 16777215;
    }
    
    public static float getHeight(int d) {
        if (d >= 8) {
            d = 0;
        }
        return (d + 1) / 9.0f;
    }
    
    @Override
    public int getTexture(final int face) {
        if (face == 0 || face == 1) {
            return this.tex;
        }
        return this.tex + 1;
    }
    
    protected int getDepth(final Level level, final int x, final int y, final int z) {
        if (level.getMaterial(x, y, z) != this.material) {
            return -1;
        }
        return level.getData(x, y, z);
    }
    
    protected int getRenderedDepth(final LevelSource level, final int x, final int y, final int z) {
        if (level.getMaterial(x, y, z) != this.material) {
            return -1;
        }
        int data = level.getData(x, y, z);
        if (data >= 8) {
            data = 0;
        }
        return data;
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
        final Material material = level.getMaterial(x, y, z);
        return material != this.material && material != Material.ice && (face == 1 || super.isSolidFace(level, x, y, z, face));
    }
    
    @Override
    public boolean isFaceVisible(final LevelSource level, final int x, final int y, final int z, final int f) {
        final Material material = level.getMaterial(x, y, z);
        return material != this.material && material != Material.ice && (f == 1 || super.isFaceVisible(level, x, y, z, f));
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
        Vec3 vec3 = Vec3.newTemp(0.0, 0.0, 0.0);
        final int renderedDepth = this.getRenderedDepth(level, x, y, z);
        for (int i = 0; i < 4; ++i) {
            int x2 = x;
            int z2 = z;
            if (i == 0) {
                --x2;
            }
            if (i == 1) {
                --z2;
            }
            if (i == 2) {
                ++x2;
            }
            if (i == 3) {
                ++z2;
            }
            final int renderedDepth2 = this.getRenderedDepth(level, x2, y, z2);
            if (renderedDepth2 < 0) {
                if (!level.getMaterial(x2, y, z2).blocksMotion()) {
                    final int renderedDepth3 = this.getRenderedDepth(level, x2, y - 1, z2);
                    if (renderedDepth3 >= 0) {
                        final int n = renderedDepth3 - (renderedDepth - 8);
                        vec3 = vec3.add((x2 - x) * n, (y - y) * n, (z2 - z) * n);
                    }
                }
            }
            else if (renderedDepth2 >= 0) {
                final int n2 = renderedDepth2 - renderedDepth;
                vec3 = vec3.add((x2 - x) * n2, (y - y) * n2, (z2 - z) * n2);
            }
        }
        if (level.getData(x, y, z) >= 8) {
            int n3 = 0;
            if (n3 != 0 || this.isSolidFace(level, x, y, z - 1, 2)) {
                n3 = 1;
            }
            if (n3 != 0 || this.isSolidFace(level, x, y, z + 1, 3)) {
                n3 = 1;
            }
            if (n3 != 0 || this.isSolidFace(level, x - 1, y, z, 4)) {
                n3 = 1;
            }
            if (n3 != 0 || this.isSolidFace(level, x + 1, y, z, 5)) {
                n3 = 1;
            }
            if (n3 != 0 || this.isSolidFace(level, x, y + 1, z - 1, 2)) {
                n3 = 1;
            }
            if (n3 != 0 || this.isSolidFace(level, x, y + 1, z + 1, 3)) {
                n3 = 1;
            }
            if (n3 != 0 || this.isSolidFace(level, x - 1, y + 1, z, 4)) {
                n3 = 1;
            }
            if (n3 != 0 || this.isSolidFace(level, x + 1, y + 1, z, 5)) {
                n3 = 1;
            }
            if (n3 != 0) {
                vec3 = vec3.normalize().add(0.0, -6.0, 0.0);
            }
        }
        return vec3.normalize();
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
        if (this.material == Material.water) {
            return 5;
        }
        if (this.material == Material.lava) {
            return 30;
        }
        return 0;
    }
    
    @Override
    public float getBrightness(final LevelSource level, final int x, final int y, final int z) {
        final float brightness = level.getBrightness(x, y, z);
        final float brightness2 = level.getBrightness(x, y + 1, z);
        return (brightness > brightness2) ? brightness : brightness2;
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
        if (this.material == Material.water && random.nextInt(64) == 0) {
            final int data = level.getData(x, y, z);
            if (data > 0 && data < 8) {
                level.playLocalSound(x + 0.5f, y + 0.5f, z + 0.5f, "liquid.water", random.nextFloat() * 0.25f + 0.75f, random.nextFloat() * 1.0f + 0.5f);
            }
        }
        if (this.material == Material.lava && level.getMaterial(x, y + 1, z) == Material.air && !level.isSolidTile(x, y + 1, z) && random.nextInt(100) == 0) {
            level.addParticle("lava", x + random.nextFloat(), y + this.yy1, z + random.nextFloat(), 0.0, 0.0, 0.0);
        }
    }
    
    public static double getSlopeAngle(final LevelSource level, final int x, final int y, final int z, final Material m) {
        Vec3 vec3 = null;
        if (m == Material.water) {
            vec3 = ((LiquidTile)Tile.water).getFlow(level, x, y, z);
        }
        if (m == Material.lava) {
            vec3 = ((LiquidTile)Tile.lava).getFlow(level, x, y, z);
        }
        if (vec3.x == 0.0 && vec3.z == 0.0) {
            return -1000.0;
        }
        return Math.atan2(vec3.z, vec3.x) - 1.5707963267948966;
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
        if (level.getTile(x, y, z) != this.id) {
            return;
        }
        if (this.material == Material.lava) {
            int n = 0;
            if (n != 0 || level.getMaterial(x, y, z - 1) == Material.water) {
                n = 1;
            }
            if (n != 0 || level.getMaterial(x, y, z + 1) == Material.water) {
                n = 1;
            }
            if (n != 0 || level.getMaterial(x - 1, y, z) == Material.water) {
                n = 1;
            }
            if (n != 0 || level.getMaterial(x + 1, y, z) == Material.water) {
                n = 1;
            }
            if (n != 0 || level.getMaterial(x, y + 1, z) == Material.water) {
                n = 1;
            }
            if (n != 0) {
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
