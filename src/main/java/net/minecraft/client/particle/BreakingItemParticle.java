// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class BreakingItemParticle extends Particle
{
    public BreakingItemParticle(final Level level, final double x, final double y, final double z, final Item irem) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.tex = irem.getIcon(0);
        final float rCol = 1.0f;
        this.bCol = rCol;
        this.gCol = rCol;
        this.rCol = rCol;
        this.gravity = Tile.snow.gravity;
        this.size /= 2.0f;
    }
    
    @Override
    public int getParticleTexture() {
        return 2;
    }
    
    @Override
    public void render(final Tesselator t, final float partialTick, final float xa, final float ya, final float za, final float xa2, final float za2) {
        final float n = (this.tex % 16 + this.uo / 4.0f) / 16.0f;
        final float n2 = n + 0.015609375f;
        final float n3 = (this.tex / 16 + this.vo / 4.0f) / 16.0f;
        final float n4 = n3 + 0.015609375f;
        final float n5 = 0.1f * this.size;
        final float n6 = (float)(this.xo + (this.x - this.xo) * partialTick - BreakingItemParticle.xOff);
        final float n7 = (float)(this.yo + (this.y - this.yo) * partialTick - BreakingItemParticle.yOff);
        final float n8 = (float)(this.zo + (this.z - this.zo) * partialTick - BreakingItemParticle.zOff);
        final float brightness = this.getBrightness(partialTick);
        t.color(brightness * this.rCol, brightness * this.gCol, brightness * this.bCol);
        t.vertexUV(n6 - xa * n5 - xa2 * n5, n7 - ya * n5, n8 - za * n5 - za2 * n5, n, n4);
        t.vertexUV(n6 - xa * n5 + xa2 * n5, n7 + ya * n5, n8 - za * n5 + za2 * n5, n, n3);
        t.vertexUV(n6 + xa * n5 + xa2 * n5, n7 + ya * n5, n8 + za * n5 + za2 * n5, n2, n3);
        t.vertexUV(n6 + xa * n5 - xa2 * n5, n7 - ya * n5, n8 + za * n5 - za2 * n5, n2, n4);
    }
}
