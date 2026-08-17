// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.particle;

import net.minecraft.world.level.material.Material;
import util.Mth;
import net.minecraft.world.level.Level;

public class BubbleParticle extends Particle
{
    public BubbleParticle(final Level level, final double x, final double y, final double z, final double xa, final double ya, final double za) {
        super(level, x, y, z, xa, ya, za);
        this.rCol = 1.0f;
        this.gCol = 1.0f;
        this.bCol = 1.0f;
        this.tex = 32;
        this.setSize(0.02f, 0.02f);

        this.size *= this.random.nextFloat() * 0.6f + 0.2f;

        this.xd = xa * 0.2f + (float)(Math.random() * 2.0 - 1.0) * 0.02f;
        this.yd = ya * 0.2f + (float)(Math.random() * 2.0 - 1.0) * 0.02f;
        this.zd = za * 0.2f + (float)(Math.random() * 2.0 - 1.0) * 0.02f;

        this.lifetime = (int)(8.0 / (Math.random() * 0.8 + 0.2));
    }
    
    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        this.yd += 0.002;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.85f;
        this.yd *= 0.85f;
        this.zd *= 0.85f;

        if (this.level.getMaterial(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z)) != Material.water) this.remove();

        if (this.lifetime-- <= 0) this.remove();
    }
}
