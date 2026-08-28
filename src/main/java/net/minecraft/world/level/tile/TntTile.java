// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class TntTile extends Tile
{
    public static final int EXPLODE_BIT = 1;
    public TntTile(final int id, final int tex) {
        super(id, tex, Material.explosive);
    }
    
    @Override
    public int getTexture(final int face) {
        if (face == 0) {
            return this.tex + 2;
        }
        if (face == 1) {
            return this.tex + 1;
        }
        return this.tex;
    }
    
    @Override
    public void onPlace(final Level level, final int x, final int y, final int z) {
        super.onPlace(level, x, y, z);
        if (level.hasNeighborSignal(x, y, z)) {
            this.destroy(level, x, y, z, 1);
            level.setTile(x, y, z, 0);
        }
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        if (type > 0 && Tile.tiles[type].isSignalSource() && level.hasNeighborSignal(x, y, z)) {
            this.destroy(level, x, y, z, 1);
            level.setTile(x, y, z, 0);
        }
    }
    
    @Override
    public int getResourceCount(final Random random) {
        return 0;
    }
    
    @Override
    public void wasExploded(final Level level, final int x, final int y, final int z) {
        final PrimedTnt e = new PrimedTnt(level, x + 0.5f, y + 0.5f, z + 0.5f);
        e.life = level.random.nextInt(e.life / 4) + e.life / 8;
        level.addEntity(e);
    }
    
    @Override
    public void destroy(final Level level, final int x, final int y, final int z, final int data) {
        if (level.isClientSide) {
            return;
        }
        if ((data & 0x1) == 0x0) {
            this.popResource(level, x, y, z, new ItemInstance(Tile.tnt.id, 1, 0));
        }
        else {
            final PrimedTnt primedTnt = new PrimedTnt(level, x + 0.5f, y + 0.5f, z + 0.5f);
            level.addEntity(primedTnt);
            level.playSound(primedTnt, "random.fuse", 1.0f, 1.0f);
        }
    }
    
    @Override
    public void attack(final Level level, final int x, final int y, final int z, final Player player) {
        if (player.getSelectedItem() != null && player.getSelectedItem().id == Item.flintAndSteel.id) {
            level.setDataNoUpdate(x, y, z, 1);
        }
        super.attack(level, x, y, z, player);
    }
    
    @Override
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        return super.use(level, x, y, z, player);
    }
}
