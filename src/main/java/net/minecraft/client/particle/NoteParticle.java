// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tesselator;
import util.Mth;
import net.minecraft.world.level.Level;

public class NoteParticle extends Particle
{
    float oSize;
    
    public NoteParticle(final Level level, final double x, final double y, final double z, final double xa, final double ya, final double za) {
        this(level, x, y, z, xa, ya, za, 2.0f);
    }
    
    public NoteParticle(final Level level, final double x, final double y, final double z, final double xa, final double ya, final double za, final float scale) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.xd *= 0.01f;
        this.yd *= 0.01f;
        this.zd *= 0.01f;
        this.yd += 0.2;

        // Useless - LCE implies the existances of a constant that defines the min/max colors for notes, this would be the hardcoded b1.7.3 equivalent
        final float rMin, gMin, bMin; rMin = gMin = bMin = 0.35f;
        final float rMax, gMax, bMax; rMax = gMax = bMax = 1.0f;

        this.rCol = Mth.sin(((float)xa + 0.0f / 3) * Mth.PI * 2.0f) * (rMax - rMin) + rMin;
        this.gCol = Mth.sin(((float)xa + 1.0f / 3) * Mth.PI * 2.0f) * (gMax - gMin) + gMin;
        this.bCol = Mth.sin(((float)xa + 2.0f / 3) * Mth.PI * 2.0f) * (bMax - bMin) + bMin;

        this.size *= 0.75f;
        this.size *= scale;
        this.oSize = this.size;

        this.lifetime = 6;
        this.noPhysics = false;

        this.tex = 16 * 4;
    }
    
    @Override
    public void render(final Tesselator t, final float a, final float xa, final float ya, final float za, final float xa2, final float za2) {
        float l = (this.age + a) / this.lifetime * 32.0f;
        if (l < 0.0f) l = 0.0f;
        if (l > 1.0f) l = 1.0f;

        this.size = this.oSize * l;
        super.render(t, a, xa, ya, za, xa2, za2);
    }
    
    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) this.remove();

        this.move(this.xd, this.yd, this.zd);
        if (this.y == this.yo) {
            this.xd *= 1.1;
            this.zd *= 1.1;
        }

        this.xd *= 0.66f;
        this.yd *= 0.66f;
        this.zd *= 0.66f;

        if (this.onGround) {
            this.xd *= 0.7f;
            this.zd *= 0.7f;
        }
    }
}
