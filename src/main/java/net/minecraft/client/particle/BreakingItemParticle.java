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
    public BreakingItemParticle(final Level level, final double x, final double y, final double z, final Item item) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.tex = item.getIcon(0);
        this.rCol = this.gCol = this.bCol = 1.0f;
        this.gravity = Tile.snow.gravity;
        this.size /= 2.0f;
    }
    
    @Override
    public int getParticleTexture() {
        return ParticleEngine.ITEM_TEXTURE;
    }
    
    @Override
    public void render(final Tesselator t, final float partialTick, final float xa, final float ya, final float za, final float xa2, final float za2) {
        final float u0 = (this.tex % 16 + this.uo / 4.0f) / 16.0f;
        final float u1 = u0 + 0.999f / 16.0f / 4;
        final float v0 = (this.tex / 16 + this.vo / 4.0f) / 16.0f;
        final float v1 = v0 + 0.999f / 16.0f / 4;
        final float r = 0.1f * this.size;

        final float x = (float)(this.xo + (this.x - this.xo) * partialTick - BreakingItemParticle.xOff);
        final float y = (float)(this.yo + (this.y - this.yo) * partialTick - BreakingItemParticle.yOff);
        final float z = (float)(this.zo + (this.z - this.zo) * partialTick - BreakingItemParticle.zOff);
        final float br = this.getBrightness(partialTick);
        t.color(br * this.rCol, br * this.gCol, br * this.bCol);

        t.vertexUV(x - xa * r - xa2 * r, y - ya * r, z - za * r - za2 * r, u0, v1);
        t.vertexUV(x - xa * r + xa2 * r, y + ya * r, z - za * r + za2 * r, u0, v0);
        t.vertexUV(x + xa * r + xa2 * r, y + ya * r, z + za * r + za2 * r, u1, v0);
        t.vertexUV(x + xa * r - xa2 * r, y - ya * r, z + za * r - za2 * r, u1, v1);
    }
}
