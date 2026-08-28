// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.level.LevelSource;
import java.util.Random;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class FireTile extends Tile
{
    public static final int FLAME_INSTANT = 60;
    public static final int FLAME_EASY = 30;
    public static final int FLAME_MEDIUM = 15;
    public static final int FLAME_HARD = 5;

    public static final int BURN_INSTANT = 100;
    public static final int BURN_EASY = 60;
    public static final int BURN_MEDIUM = 20;
    public static final int BURN_HARD = 5;
    public static final int BURN_NEVER = 0;
    private int[] flameOdds = new int[Tile.TILE_NUM_COUNT];
    private int[] burnOdds = new int[Tile.TILE_NUM_COUNT];
    
    protected FireTile(final int id, final int tex) {
        super(id, tex, Material.fire);
        this.setTicking(true);
    }
    
    public void init() {
        this.setFlammable(Tile.wood.id, FLAME_HARD, BURN_MEDIUM);
        this.setFlammable(Tile.fence.id, FLAME_HARD, BURN_MEDIUM);
        this.setFlammable(Tile.stairs_wood.id, FLAME_HARD, BURN_MEDIUM);
        this.setFlammable(Tile.treeTrunk.id, FLAME_HARD, BURN_HARD);
        this.setFlammable(Tile.leaves.id, FLAME_EASY, BURN_EASY);
        this.setFlammable(Tile.bookshelf.id, FLAME_EASY, BURN_MEDIUM);
        this.setFlammable(Tile.tnt.id, FLAME_MEDIUM, BURN_INSTANT);
        this.setFlammable(Tile.tallgrass.id, FLAME_INSTANT, BURN_INSTANT);
        this.setFlammable(Tile.cloth.id, FLAME_EASY, BURN_EASY);
    }
    
    private void setFlammable(final int id, final int flame, final int burn) {
        this.flameOdds[id] = flame;
        this.burnOdds[id] = burn;
    }
    
    @Override
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        return null;
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
        return Tile.SHAPE_FIRE;
    }
    
    @Override
    public int getResourceCount(final Random random) {
        return 0;
    }
    
    @Override
    public int getTickDelay() {
        return 40;
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        final boolean infiniBurn = level.getTile(x, y - 1, z) == Tile.hellRock.id;

        if (!this.mayPlace(level, x, y, z)) {
            level.setTile(x, y, z, 0);
        }

        if (!infiniBurn && level.isRaining()) {
            if (level.isRainingAt(x, y, z) || level.isRainingAt(x - 1, y, z) || level.isRainingAt(x + 1, y, z) || level.isRainingAt(x, y, z - 1) || level.isRainingAt(x, y, z + 1)) {
                level.setTile(x, y, z, 0);
                return;
            }
        }

        final int age = level.getData(x, y, z);
        if (age < 15) {
            level.setDataNoUpdate(x, y, z, age + random.nextInt(3) / 2);
        }
        level.addToTickNextTick(x, y, z, this.id, this.getTickDelay());

        if (!infiniBurn && !this.isValidFireLocation(level, x, y, z)) {
            if (!level.isSolidBlockingTile(x, y - 1, z) || age > 3) level.setTile(x, y, z, 0);
            return;
        }

        if (!infiniBurn && !this.canBurn(level, x, y - 1, z)) {
            if (age == 15 && random.nextInt(4) == 0) {
                level.setTile(x, y, z, 0);
                return;
            }
        }

        this.checkBurnOut(level, x + 1, y, z, 300, random, age);
        this.checkBurnOut(level, x - 1, y, z, 300, random, age);
        this.checkBurnOut(level, x, y - 1, z, 250, random, age);
        this.checkBurnOut(level, x, y + 1, z, 250, random, age);
        this.checkBurnOut(level, x, y, z - 1, 300, random, age);
        this.checkBurnOut(level, x, y, z + 1, 300, random, age);

        for (int xx = x - 1; xx <= x + 1; ++xx) {
            for (int zz = z - 1; zz <= z + 1; ++zz) {
                for (int yy = y - 1; yy <= y + 4; ++yy) {
                    if (xx == x && yy == y && zz == z) continue;

                    int rate = 100;
                    if (yy > y + 1) {
                        rate += (yy - (y + 1)) * 100;
                    }

                    final int fodds = this.getFireOdds(level, xx, yy, zz);
                    if (fodds > 0) {
                        final int odds = (fodds + 40) / (age + 30);
                        if (odds > 0 && random.nextInt(rate) <= odds) {
                            if ((level.isRaining() && level.isRainingAt(xx, yy, zz))
                                    || level.isRainingAt(xx - 1, yy, z)
                                    || level.isRainingAt(xx + 1, yy, zz)
                                    || level.isRainingAt(xx, yy, zz - 1)
                                    || level.isRainingAt(xx, yy, zz + 1)) {
                                // DO NOTHING, rain!

                            } else {
                                int tAge = age + random.nextInt(5) / 4;
                                if (tAge > 15) tAge = 15;
                                level.setTileAndData(xx, yy, zz, this.id, tAge);

                            }
                        }
                    }
                }
            }
        }
    }
    
    private void checkBurnOut(final Level level, final int x, final int y, final int z, final int chance, final Random random, final int age) {
        int odds = this.burnOdds[level.getTile(x, y, z)];
        if (random.nextInt(chance) < odds) {
            final boolean wasTnt = level.getTile(x, y, z) == Tile.tnt.id;
            if (random.nextInt(age + 10) < 5 && !level.isRainingAt(x, y, z)) {
                int tAge = age + random.nextInt(5) / 4;
                if (tAge > 15) tAge = 15;
                level.setTileAndData(x, y, z, this.id, tAge);
            }
            else {
                level.setTile(x, y, z, 0);
            }
            if (wasTnt) {
                Tile.tnt.destroy(level, x, y, z, TntTile.EXPLODE_BIT);
            }
        }
    }
    
    private boolean isValidFireLocation(final Level level, final int x, final int y, final int z) {
        if (this.canBurn(level, x + 1, y, z)) return true;
        if (this.canBurn(level, x - 1, y, z)) return true;
        if (this.canBurn(level, x, y - 1, z)) return true;
        if (this.canBurn(level, x, y + 1, z)) return true;
        if (this.canBurn(level, x, y, z - 1)) return true;
        if (this.canBurn(level, x, y, z + 1)) return true;

        return false;
    }
    
    private int getFireOdds(final Level level, final int x, final int y, final int z) {
        int odds = 0;
        if (!level.isEmptyTile(x, y, z)) return 0;

        odds = this.getFlammability(level, x + 1, y, z, odds);
        odds = this.getFlammability(level, x - 1, y, z, odds);
        odds = this.getFlammability(level, x, y - 1, z, odds);
        odds = this.getFlammability(level, x, y + 1, z, odds);
        odds = this.getFlammability(level, x, y, z - 1, odds);
        odds = this.getFlammability(level, x, y, z + 1, odds);

        return odds;
    }
    
    @Override
    public boolean mayPick() {
        return false;
    }
    
    public boolean canBurn(final LevelSource level, final int x, final int y, final int z) {
        return this.flameOdds[level.getTile(x, y, z)] > 0;
    }
    
    public int getFlammability(final Level level, final int x, final int y, final int z, final int odds) {
        final int f = this.flameOdds[level.getTile(x, y, z)];
        if (f > odds) return f;
        return odds;
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        return level.isSolidBlockingTile(x, y - 1, z) || this.isValidFireLocation(level, x, y, z);
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        if (!level.isSolidBlockingTile(x, y - 1, z) && !this.isValidFireLocation(level, x, y, z)) {
            level.setTile(x, y, z, 0);
        }
    }
    
    @Override
    public void onPlace(final Level level, final int x, final int y, final int z) {
        if (level.getTile(x, y - 1, z) == Tile.obsidian.id) {
            if (Tile.portalTile.trySpawnPortal(level, x, y, z)) {
                return;
            }
        }
        if (!level.isSolidBlockingTile(x, y - 1, z) && !this.isValidFireLocation(level, x, y, z)) {
            level.setTile(x, y, z, 0);
            return;
        }
        level.addToTickNextTick(x, y, z, this.id, this.getTickDelay());
    }
    
    @Override
    public void animateTick(final Level level, final int x, final int y, final int z, final Random random) {
        if (random.nextInt(24) == 0) {
            level.playSound(x + 0.5f, y + 0.5f, z + 0.5f, "fire.fire", 1.0f + random.nextFloat(), random.nextFloat() * 0.7f + 0.3f);
        }

        if (level.isSolidBlockingTile(x, y - 1, z) || Tile.fire.canBurn(level, x, y - 1, z)) {
            for (int i = 0; i < 3; ++i) {
                float xx = x + random.nextFloat();
                float yy = y + random.nextFloat() * 0.5f + 0.5f;
                float zz = z + random.nextFloat();
                level.addParticle("largesmoke", xx, yy, zz, 0.0, 0.0, 0.0);
            }
        }
        else {
            if (Tile.fire.canBurn(level, x - 1, y, z)) {
                for (int i = 0; i < 2; ++i) {
                    float xx = x + random.nextFloat() * 0.1f;
                    float yy = y + random.nextFloat();
                    float zz = z + random.nextFloat();
                    level.addParticle("largesmoke", xx, yy, zz, 0.0, 0.0, 0.0);
                }
            }
            if (Tile.fire.canBurn(level, x + 1, y, z)) {
                for (int i = 0; i < 2; ++i) {
                    float xx = x + 1 - random.nextFloat() * 0.1f;
                    float yy = y + random.nextFloat();
                    float zz = z + random.nextFloat();
                    level.addParticle("largesmoke", xx, yy, zz, 0.0, 0.0, 0.0);
                }
            }
            if (Tile.fire.canBurn(level, x, y, z - 1)) {
                for (int i = 0; i < 2; ++i) {
                    float xx = x + random.nextFloat();
                    float yy = y + random.nextFloat();
                    float zz = z + random.nextFloat() * 0.1f;
                    level.addParticle("largesmoke", xx, yy, zz, 0.0, 0.0, 0.0);
                }
            }
            if (Tile.fire.canBurn(level, x, y, z + 1)) {
                for (int i = 0; i < 2; ++i) {
                    float xx = x + random.nextFloat();
                    float yy = y + random.nextFloat();
                    float zz = z + 1 - random.nextFloat() * 0.1f;
                    level.addParticle("largesmoke", xx, yy, zz, 0.0, 0.0, 0.0);
                }
            }
            if (Tile.fire.canBurn(level, x, y + 1, z)) {
                for (int i = 0; i < 2; ++i) {
                    float xx = x + random.nextFloat();
                    float yy = y + 1 - random.nextFloat() * 0.1f;
                    float zz = z + random.nextFloat();
                    level.addParticle("largesmoke", xx, yy, zz, 0.0, 0.0, 0.0);
                }
            }
        }
    }
}
