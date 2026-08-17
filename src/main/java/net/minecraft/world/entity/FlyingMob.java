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
            float n = 0.91f;
            if (this.onGround) {
                n = 0.54600006f;
                final int tile = this.level.getTile(Mth.floor(this.x), Mth.floor(this.bb.y0) - 1, Mth.floor(this.z));
                if (tile > 0) {
                    n = Tile.tiles[tile].friction * 0.91f;
                }
            }
            final float n2 = 0.16277136f / (n * n * n);
            this.moveRelative(xa, ya, this.onGround ? (0.1f * n2) : 0.02f);
            float n3 = 0.91f;
            if (this.onGround) {
                n3 = 0.54600006f;
                final int tile2 = this.level.getTile(Mth.floor(this.x), Mth.floor(this.bb.y0) - 1, Mth.floor(this.z));
                if (tile2 > 0) {
                    n3 = Tile.tiles[tile2].friction * 0.91f;
                }
            }
            this.move(this.xd, this.yd, this.zd);
            this.xd *= n3;
            this.yd *= n3;
            this.zd *= n3;
        }
        this.walkAnimSpeedO = this.walkAnimSpeed;
        final double n4 = this.x - this.xo;
        final double n5 = this.z - this.zo;
        float n6 = Mth.sqrt(n4 * n4 + n5 * n5) * 4.0f;
        if (n6 > 1.0f) {
            n6 = 1.0f;
        }
        this.walkAnimSpeed += (n6 - this.walkAnimSpeed) * 0.4f;
        this.walkAnimPos += this.walkAnimSpeed;
    }
    
    @Override
    public boolean onLadder() {
        return false;
    }
}
