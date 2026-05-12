// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.particle;

import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.LiquidTile;
import util.Mth;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.level.Level;

public class WaterDropParticle extends Particle
{
    public WaterDropParticle(final Level level, final double x, final double y, final double z) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.xd *= 0.30000001192092896;
        this.yd = (float)Math.random() * 0.2f + 0.1f;
        this.zd *= 0.30000001192092896;
        this.rCol = 1.0f;
        this.gCol = 1.0f;
        this.bCol = 1.0f;
        this.tex = 19 + this.random.nextInt(4);
        this.setSize(0.01f, 0.01f);
        this.gravity = 0.06f;
        this.lifetime = (int)(8.0 / (Math.random() * 0.8 + 0.2));
    }
    
    @Override
    public void render(final Tesselator t, final float partialTick, final float xa, final float ya, final float za, final float xa2, final float za2) {
        super.render(t, partialTick, xa, ya, za, xa2, za2);
    }
    
    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.yd -= this.gravity;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.9800000190734863;
        this.yd *= 0.9800000190734863;
        this.zd *= 0.9800000190734863;
        if (this.lifetime-- <= 0) {
            this.remove();
        }
        if (this.onGround) {
            if (Math.random() < 0.5) {
                this.remove();
            }
            this.xd *= 0.699999988079071;
            this.zd *= 0.699999988079071;
        }
        final Material material = this.level.getMaterial(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z));
        if ((material.isLiquid() || material.isSolid()) && this.y < Mth.floor(this.y) + 1 - LiquidTile.getHeight(this.level.getData(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z)))) {
            this.remove();
        }
    }
}
