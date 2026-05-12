// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.item.Item;
import java.util.Random;

public class TallGrass extends Bush
{
    protected TallGrass(final int id, final int tex) {
        super(id, tex);
        final float n = 0.4f;
        this.setShape(0.5f - n, 0.0f, 0.5f - n, 0.5f + n, 0.8f, 0.5f + n);
    }
    
    @Override
    public int getTexture(final int face, final int data) {
        if (data == 1) {
            return this.tex;
        }
        if (data == 2) {
            return this.tex + 16 + 1;
        }
        if (data == 0) {
            return this.tex + 16;
        }
        return this.tex;
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        if (random.nextInt(8) == 0) {
            return Item.seeds.id;
        }
        return -1;
    }
}
