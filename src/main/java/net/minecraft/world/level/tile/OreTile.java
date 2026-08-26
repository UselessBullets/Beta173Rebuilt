// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.item.Item;
import java.util.Random;
import net.minecraft.world.level.material.Material;

public class OreTile extends Tile
{
    public OreTile(final int id, final int tex) {
        super(id, tex, Material.stone);
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        if (this.id == Tile.coalOre.id) {
            return Item.coal.id;
        }
        if (this.id == Tile.diamondOre.id) {
            return Item.diamond.id;
        }
        if (this.id == Tile.lapisOre.id) {
            return Item.dye_powder.id;
        }
        return this.id;
    }
    
    @Override
    public int getResourceCount(final Random random) {
        if (this.id == Tile.lapisOre.id) {
            return 4 + random.nextInt(5);
        }
        return 1;
    }
    
    @Override
    protected int getSpawnResourcesAuxValue(final int data) {
        if (this.id == Tile.lapisOre.id) {
            return 4;
        }
        return 0;
    }
}
