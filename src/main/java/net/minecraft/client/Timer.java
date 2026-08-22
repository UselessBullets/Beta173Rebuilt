// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

public class Timer
{
    private static final int MAX_TICKS_PER_UPDATE = 10;
    public float ticksPerSecond;
    private double lastTime;
    public int ticks;
    public float a;
    public float timeScale = 1.0f;
    public float passedTime = 0.0f;
    private long lastMs;
    private long lastMsSysTime;
    private long accumMs;
    private double adjustTime = 1.0;
    
    public Timer(final float ticksPerSecond) {
        this.ticksPerSecond = ticksPerSecond;
        this.lastMs = System.currentTimeMillis();
        this.lastMsSysTime = System.nanoTime() / 1000000L;
    }
    
    public void advanceTime() {
        final long nowMs = System.currentTimeMillis();
        final long passedMs = nowMs - this.lastMs;
        final long msSysTim = System.nanoTime() / 1000000L;
        final double now = msSysTim / 1000.0;

        if (passedMs > 1000L) {
            this.lastTime = now;
        }
        else if (passedMs < 0L) {
            this.lastTime = now;
        }
        else {
            this.accumMs += passedMs;
            if (this.accumMs > 1000L) {
                long passedMySysTime = msSysTim - this.lastMsSysTime;

                double adjustTimeT = this.accumMs / (double)passedMySysTime;
                this.adjustTime += (adjustTimeT - this.adjustTime) * 0.2f;

                this.lastMsSysTime = msSysTim;
                this.accumMs = 0L;
            }
            if (this.accumMs < 0L) {
                this.lastMsSysTime = msSysTim;
            }
        }
        this.lastMs = nowMs;

        double passedSeconds = (now - this.lastTime) * this.adjustTime;
        this.lastTime = now;

        if (passedSeconds < 0.0) passedSeconds = 0.0;
        if (passedSeconds > 1.0) passedSeconds = 1.0;

        this.passedTime += (float)(passedSeconds * this.timeScale * this.ticksPerSecond);

        this.ticks = (int)this.passedTime;
        this.passedTime -= this.ticks;
        if (this.ticks > MAX_TICKS_PER_UPDATE) this.ticks = MAX_TICKS_PER_UPDATE;
        this.a = this.passedTime;
    }
}
