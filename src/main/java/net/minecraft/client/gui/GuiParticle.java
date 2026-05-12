// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import java.util.Random;

public class GuiParticle
{
    private static Random random;
    public double x;
    public double y;
    public double xo;
    public double yo;
    public double xa;
    public double ya;
    public double friction;
    public boolean removed;
    public int life;
    public int lifeTime;
    public double r;
    public double g;
    public double b;
    public double a;
    public double or;
    public double og;
    public double ob;
    public double oa;
    
    public void tick(final GuiParticles guiParticles) {
        this.x += this.xa;
        this.y += this.ya;
        this.xa *= this.friction;
        this.ya *= this.friction;
        this.ya += 0.1;
        if (++this.life > this.lifeTime) {
            this.remove();
        }
        this.a = 2.0 - this.life / (double)this.lifeTime * 2.0;
        if (this.a > 1.0) {
            this.a = 1.0;
        }
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
    
    static {
        GuiParticle.random = new Random();
    }
}
