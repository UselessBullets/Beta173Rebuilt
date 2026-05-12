// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.item.Item;
import java.util.Random;
import net.minecraft.world.level.material.Material;

public class LightGemTile extends Tile
{
    public LightGemTile(final int id, final int tex, final Material material) {
        super(id, tex, material);
    }
    
    @Override
    public int getResourceCount(final Random random) {
        return 2 + random.nextInt(3);
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        return Item.yellowDust.id;
    }
}
