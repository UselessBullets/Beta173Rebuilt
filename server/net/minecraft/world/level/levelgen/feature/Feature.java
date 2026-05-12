// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import net.minecraft.world.level.Level;

public abstract class Feature
{
    public abstract boolean place(final Level level, final Random random, final int x, final int y, final int z);
    
    public void init(final double V1, final double V2, final double V3) {
    }
}
