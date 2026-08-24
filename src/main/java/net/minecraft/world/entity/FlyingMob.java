// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity;

import net.minecraft.world.level.tile.Tile;
import util.Mth;
import net.minecraft.world.level.Level;

public class FlyingMob extends Mob
{
    public FlyingMob(final Level level) {
        super(level);
    }
    
    @Override
    protected void causeFallDamage(final float distance) {
    }
    
    @Override
    public void travel(final float xa, final float ya) {
        if (this.isInWater()) {
            this.moveRelative(xa, ya, 0.02f);
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.8f;
            this.yd *= 0.8f;
            this.zd *= 0.8f;
        }
        else if (this.isInLava()) {
            this.moveRelative(xa, ya, 0.02f);
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.5;
            this.yd *= 0.5;
            this.zd *= 0.5;
        }
        else {
            float friction = 0.91f;
            if (this.onGround) {
                friction = 0.6f * 0.91f;
                final int t = this.level.getTile(Mth.floor(this.x), Mth.floor(this.bb.y0) - 1, Mth.floor(this.z));
                if (t > 0) {
                    friction = Tile.tiles[t].friction * 0.91f;
                }
            }

            final float friction2 = (0.6f * 0.6f * 0.91f * 0.91f * 0.6f * 0.91f) / (friction * friction * friction);
            this.moveRelative(xa, ya, this.onGround ? (0.1f * friction2) : 0.02f);

            friction = 0.91f;
            if (this.onGround) {
                friction = 0.6f * 0.91f;
                final int t = this.level.getTile(Mth.floor(this.x), Mth.floor(this.bb.y0) - 1, Mth.floor(this.z));
                if (t > 0) {
                    friction = Tile.tiles[t].friction * 0.91f;
                }
            }

            this.move(this.xd, this.yd, this.zd);

            this.xd *= friction;
            this.yd *= friction;
            this.zd *= friction;
        }
        this.walkAnimSpeedO = this.walkAnimSpeed;
        final double xxd = this.x - this.xo;
        final double zzd = this.z - this.zo;
        float wst = Mth.sqrt(xxd * xxd + zzd * zzd) * 4.0f;
        if (wst > 1.0f) wst = 1.0f;
        this.walkAnimSpeed += (wst - this.walkAnimSpeed) * 0.4f;
        this.walkAnimPos += this.walkAnimSpeed;
    }
    
    @Override
    public boolean onLadder() {
        return false;
    }
}
