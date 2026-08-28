// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.item.Item;
import java.util.Random;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class RedStoneOreTile extends Tile
{
    private boolean lit;
    
    public RedStoneOreTile(final int id, final int tex, final boolean lit) {
        super(id, tex, Material.stone);
        if (lit) {
            this.setTicking(true);
        }
        this.lit = lit;
    }
    
    @Override
    public int getTickDelay() {
        return 30;
    }
    
    @Override
    public void attack(final Level level, final int x, final int y, final int z, final Player player) {
        this.interact(level, x, y, z);
        super.attack(level, x, y, z, player);
    }
    
    @Override
    public void stepOn(final Level level, final int x, final int y, final int z, final Entity entity) {
        this.interact(level, x, y, z);
        super.stepOn(level, x, y, z, entity);
    }
    
    @Override
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        this.interact(level, x, y, z);
        return super.use(level, x, y, z, player);
    }
    
    private void interact(final Level level, final int x, final int y, final int z) {
        this.poofParticles(level, x, y, z);
        if (this.id == Tile.redStoneOre.id) {
            level.setTile(x, y, z, Tile.redStoneOre_lit.id);
        }
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        if (this.id == Tile.redStoneOre_lit.id) {
            level.setTile(x, y, z, Tile.redStoneOre.id);
        }
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        return Item.redStone.id;
    }
    
    @Override
    public int getResourceCount(final Random random) {
        return 4 + random.nextInt(2);
    }
    
    @Override
    public void animateTick(final Level level, final int x, final int y, final int z, final Random random) {
        if (this.lit) {
            this.poofParticles(level, x, y, z);
        }
    }
    
    private void poofParticles(final Level level, final int x, final int y, final int z) {
        final Random random = level.random;
        final double r = 1 / 16.0f;
        for (int i = 0; i < 6; ++i) {
            double xx = x + random.nextFloat();
            double yy = y + random.nextFloat();
            double zz = z + random.nextFloat();
            if (i == 0 && !level.isSolidTile(x, y + 1, z)) yy = y + 1 + r;
            if (i == 1 && !level.isSolidTile(x, y - 1, z)) yy = y + 0 - r;
            if (i == 2 && !level.isSolidTile(x, y, z + 1)) zz = z + 1 + r;
            if (i == 3 && !level.isSolidTile(x, y, z - 1)) zz = z + 0 - r;
            if (i == 4 && !level.isSolidTile(x + 1, y, z)) xx = x + 1 + r;
            if (i == 5 && !level.isSolidTile(x - 1, y, z)) xx = x + 0 - r;
            if (xx < x || xx > x + 1 || yy < 0.0 || yy > y + 1 || zz < z || zz > z + 1) {
                level.addParticle("reddust", xx, yy, zz, 0.0, 0.0, 0.0);
            }
        }
    }
}
