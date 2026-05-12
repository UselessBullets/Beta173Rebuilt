// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

public class Timer
{
    public float ticksPerSecond;
    private double lastTime;
    public int ticks;
    public float partialTick;
    public float timeScale;
    public float passedTime;
    private long lastMs;
    private long lastMsSysTime;
    private long passedMs;
    private double adjustTime;
    
    public Timer(final float ticksPerSecond) {
        this.timeScale = 1.0f;
        this.passedTime = 0.0f;
        this.adjustTime = 1.0;
        this.ticksPerSecond = ticksPerSecond;
        this.lastMs = System.currentTimeMillis();
        this.lastMsSysTime = System.nanoTime() / 1000000L;
    }
    
    public void advanceTime() {
        final long currentTimeMillis = System.currentTimeMillis();
        final long n = currentTimeMillis - this.lastMs;
        final long n2 = System.nanoTime() / 1000000L;
        final double lastTime = n2 / 1000.0;
        if (n > 1000L) {
            this.lastTime = lastTime;
        }
        else if (n < 0L) {
            this.lastTime = lastTime;
        }
        else {
            this.passedMs += n;
            if (this.passedMs > 1000L) {
                this.adjustTime += (this.passedMs / (double)(n2 - this.lastMsSysTime) - this.adjustTime) * 0.20000000298023224;
                this.lastMsSysTime = n2;
                this.passedMs = 0L;
            }
            if (this.passedMs < 0L) {
                this.lastMsSysTime = n2;
            }
        }
        this.lastMs = currentTimeMillis;
        double n3 = (lastTime - this.lastTime) * this.adjustTime;
        this.lastTime = lastTime;
        if (n3 < 0.0) {
            n3 = 0.0;
        }
        if (n3 > 1.0) {
            n3 = 1.0;
        }
        this.passedTime += (float)(n3 * this.timeScale * this.ticksPerSecond);
        this.ticks = (int)this.passedTime;
        this.passedTime -= this.ticks;
        if (this.ticks > 10) {
            this.ticks = 10;
        }
        this.partialTick = this.passedTime;
    }
}
