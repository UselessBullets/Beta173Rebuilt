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
    private int[] flameOdds;
    private int[] burnOdds;
    
    protected FireTile(final int id, final int tex) {
        super(id, tex, Material.fire);
        this.flameOdds = new int[256];
        this.burnOdds = new int[256];
        this.setTicking(true);
    }
    
    public void init() {
        this.setFlammable(Tile.wood.id, 5, 20);
        this.setFlammable(Tile.fence.id, 5, 20);
        this.setFlammable(Tile.stairs_wood.id, 5, 20);
        this.setFlammable(Tile.treeTrunk.id, 5, 5);
        this.setFlammable(Tile.leaves.id, 30, 60);
        this.setFlammable(Tile.bookshelf.id, 30, 20);
        this.setFlammable(Tile.tnt.id, 15, 100);
        this.setFlammable(Tile.tallgrass.id, 60, 100);
        this.setFlammable(Tile.cloth.id, 30, 60);
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
        return 3;
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
        final boolean b = level.getTile(x, y - 1, z) == Tile.hellRock.id;
        if (!this.mayPlace(level, x, y, z)) {
            level.setTile(x, y, z, 0);
        }
        if (!b && level.isRaining() && (level.isRainingAt(x, y, z) || level.isRainingAt(x - 1, y, z) || level.isRainingAt(x + 1, y, z) || level.isRainingAt(x, y, z - 1) || level.isRainingAt(x, y, z + 1))) {
            level.setTile(x, y, z, 0);
            return;
        }
        final int data = level.getData(x, y, z);
        if (data < 15) {
            level.setDataNoUpdate(x, y, z, data + random.nextInt(3) / 2);
        }
        level.addToTickNextTick(x, y, z, this.id, this.getTickDelay());
        if (!b && !this.isValidFireLocation(level, x, y, z)) {
            if (!level.isSolidBlockingTile(x, y - 1, z) || data > 3) {
                level.setTile(x, y, z, 0);
            }
            return;
        }
        if (!b && !this.canBurn(level, x, y - 1, z) && data == 15 && random.nextInt(4) == 0) {
            level.setTile(x, y, z, 0);
            return;
        }
        this.checkBurnOut(level, x + 1, y, z, 300, random, data);
        this.checkBurnOut(level, x - 1, y, z, 300, random, data);
        this.checkBurnOut(level, x, y - 1, z, 250, random, data);
        this.checkBurnOut(level, x, y + 1, z, 250, random, data);
        this.checkBurnOut(level, x, y, z - 1, 300, random, data);
        this.checkBurnOut(level, x, y, z + 1, 300, random, data);
        for (int i = x - 1; i <= x + 1; ++i) {
            for (int j = z - 1; j <= z + 1; ++j) {
                for (int k = y - 1; k <= y + 4; ++k) {
                    if (i != x || k != y || j != z) {
                        int bound = 100;
                        if (k > y + 1) {
                            bound += (k - (y + 1)) * 100;
                        }
                        final int fireOdds = this.getFireOdds(level, i, k, j);
                        if (fireOdds > 0) {
                            final int n = (fireOdds + 40) / (data + 30);
                            if (n > 0 && random.nextInt(bound) <= n && (!level.isRaining() || !level.isRainingAt(i, k, j)) && !level.isRainingAt(i - 1, k, z) && !level.isRainingAt(i + 1, k, j) && !level.isRainingAt(i, k, j - 1)) {
                                if (!level.isRainingAt(i, k, j + 1)) {
                                    int data2 = data + random.nextInt(5) / 4;
                                    if (data2 > 15) {
                                        data2 = 15;
                                    }
                                    level.setTileAndData(i, k, j, this.id, data2);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void checkBurnOut(final Level level, final int x, final int y, final int z, final int chance, final Random random, final int age) {
        if (random.nextInt(chance) < this.burnOdds[level.getTile(x, y, z)]) {
            final boolean b = level.getTile(x, y, z) == Tile.tnt.id;
            if (random.nextInt(age + 10) < 5 && !level.isRainingAt(x, y, z)) {
                int data = age + random.nextInt(5) / 4;
                if (data > 15) {
                    data = 15;
                }
                level.setTileAndData(x, y, z, this.id, data);
            }
            else {
                level.setTile(x, y, z, 0);
            }
            if (b) {
                Tile.tnt.destroy(level, x, y, z, 1);
            }
        }
    }
    
    private boolean isValidFireLocation(final Level level, final int x, final int y, final int z) {
        return this.canBurn(level, x + 1, y, z) || this.canBurn(level, x - 1, y, z) || this.canBurn(level, x, y - 1, z) || this.canBurn(level, x, y + 1, z) || this.canBurn(level, x, y, z - 1) || this.canBurn(level, x, y, z + 1);
    }
    
    private int getFireOdds(final Level level, final int x, final int y, final int z) {
        final int odds = 0;
        if (!level.isEmptyTile(x, y, z)) {
            return 0;
        }
        return this.getFlammability(level, x, y, z + 1, this.getFlammability(level, x, y, z - 1, this.getFlammability(level, x, y + 1, z, this.getFlammability(level, x, y - 1, z, this.getFlammability(level, x - 1, y, z, this.getFlammability(level, x + 1, y, z, odds))))));
    }
    
    @Override
    public boolean mayPick() {
        return false;
    }
    
    public boolean canBurn(final LevelSource level, final int x, final int y, final int z) {
        return this.flameOdds[level.getTile(x, y, z)] > 0;
    }
    
    public int getFlammability(final Level level, final int x, final int y, final int z, final int odds) {
        final int n = this.flameOdds[level.getTile(x, y, z)];
        if (n > odds) {
            return n;
        }
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
        if (level.getTile(x, y - 1, z) == Tile.obsidian.id && Tile.portalTile.trySpawnPortal(level, x, y, z)) {
            return;
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
            level.playLocalSound(x + 0.5f, y + 0.5f, z + 0.5f, "fire.fire", 1.0f + random.nextFloat(), random.nextFloat() * 0.7f + 0.3f);
        }
        if (level.isSolidBlockingTile(x, y - 1, z) || Tile.fire.canBurn(level, x, y - 1, z)) {
            for (int i = 0; i < 3; ++i) {
                level.addParticle("largesmoke", x + random.nextFloat(), y + random.nextFloat() * 0.5f + 0.5f, z + random.nextFloat(), 0.0, 0.0, 0.0);
            }
        }
        else {
            if (Tile.fire.canBurn(level, x - 1, y, z)) {
                for (int j = 0; j < 2; ++j) {
                    level.addParticle("largesmoke", x + random.nextFloat() * 0.1f, y + random.nextFloat(), z + random.nextFloat(), 0.0, 0.0, 0.0);
                }
            }
            if (Tile.fire.canBurn(level, x + 1, y, z)) {
                for (int k = 0; k < 2; ++k) {
                    level.addParticle("largesmoke", x + 1 - random.nextFloat() * 0.1f, y + random.nextFloat(), z + random.nextFloat(), 0.0, 0.0, 0.0);
                }
            }
            if (Tile.fire.canBurn(level, x, y, z - 1)) {
                for (int l = 0; l < 2; ++l) {
                    level.addParticle("largesmoke", x + random.nextFloat(), y + random.nextFloat(), z + random.nextFloat() * 0.1f, 0.0, 0.0, 0.0);
                }
            }
            if (Tile.fire.canBurn(level, x, y, z + 1)) {
                for (int n = 0; n < 2; ++n) {
                    level.addParticle("largesmoke", x + random.nextFloat(), y + random.nextFloat(), z + 1 - random.nextFloat() * 0.1f, 0.0, 0.0, 0.0);
                }
            }
            if (Tile.fire.canBurn(level, x, y + 1, z)) {
                for (int n2 = 0; n2 < 2; ++n2) {
                    level.addParticle("largesmoke", x + random.nextFloat(), y + 1 - random.nextFloat() * 0.1f, z + random.nextFloat(), 0.0, 0.0, 0.0);
                }
            }
        }
    }
}
