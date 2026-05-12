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
        final double n = 0.0625;
        for (int i = 0; i < 6; ++i) {
            double x2 = x + random.nextFloat();
            double y2 = y + random.nextFloat();
            double z2 = z + random.nextFloat();
            if (i == 0 && !level.isSolidTile(x, y + 1, z)) {
                y2 = y + 1 + n;
            }
            if (i == 1 && !level.isSolidTile(x, y - 1, z)) {
                y2 = y + 0 - n;
            }
            if (i == 2 && !level.isSolidTile(x, y, z + 1)) {
                z2 = z + 1 + n;
            }
            if (i == 3 && !level.isSolidTile(x, y, z - 1)) {
                z2 = z + 0 - n;
            }
            if (i == 4 && !level.isSolidTile(x + 1, y, z)) {
                x2 = x + 1 + n;
            }
            if (i == 5 && !level.isSolidTile(x - 1, y, z)) {
                x2 = x + 0 - n;
            }
            if (x2 < x || x2 > x + 1 || y2 < 0.0 || y2 > y + 1 || z2 < z || z2 > z + 1) {
                level.addParticle("reddust", x2, y2, z2, 0.0, 0.0, 0.0);
            }
        }
    }
}
