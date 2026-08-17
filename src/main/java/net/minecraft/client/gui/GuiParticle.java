// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import java.awt.*;
import java.util.Random;

public class GuiParticle
{
    private static final Random random = new Random();
    public double x, y;
    public double xo, yo;
    public double xa, ya;
    public double friction;
    public boolean removed = false;
    public int life, lifeTime;
    public double r, g, b, a = 1;
    public double or, og, ob, oa;

    public GuiParticle(double x, double y, double xa, double ya) {
        this.xo = this.x = x;
        this.yo = this.y = y;
        this.xa = xa;
        this.ya = ya;

        int col = Color.HSBtoRGB(random.nextFloat(), 0.5f, 1);
        this.r = ((col >> 16) & 0xff) / 255.0;
        this.g = ((col >> 8) & 0xff) / 255.0;
        this.b = ((col) & 0xff) / 255.0;

        this.friction = 1.0 / (random.nextDouble() * 0.05 + 1.01);

        this.lifeTime = (int) (10.0 / (random.nextDouble() * 2 + 0.1));
    }
    
    public void tick(final GuiParticles guiParticles) {
        this.x += this.xa;
        this.y += this.ya;

        this.xa *= this.friction;
        this.ya *= this.friction;

        this.ya += 0.1;
        if (++this.life > this.lifeTime) this.remove();
        this.a = 2.0 - this.life / (double)this.lifeTime * 2.0;
        if (this.a > 1.0) this.a = 1.0;
        this.a *= this.a;
        this.a *= 0.5;
    }
    
    public void preTick() {
        this.or = this.r;
        this.og = this.g;
        this.ob = this.b;
        this.oa = this.a;

        this.xo = this.x;
        this.yo = this.y;
    }
    
    public void remove() {
        this.removed = true;
    }

}
