// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.Tile;

public class TerrainParticle extends Particle
{
    private Tile tile;
    private int face = 0;
    
    public TerrainParticle(final Level level, final double x, final double y, final double z, final double xa, final double ya, final double za, final Tile tile, final int face, final int data) {
        super(level, x, y, z, xa, ya, za);
        this.tile = tile;
        this.tex = tile.getTexture(0, data);
        this.gravity = tile.gravity;
        this.rCol = this.gCol = this.bCol = 0.6f;
        this.size /= 2.0f;
        this.face = face;
    }
    
    public TerrainParticle init(final int x, final int y, final int z) {
        if (this.tile == Tile.grass) return this;
        final int col = this.tile.getColor(this.level, x, y, z);
        this.rCol *= (col >> 16 & 0xFF) / 255.0f;
        this.gCol *= (col >> 8 & 0xFF) / 255.0f;
        this.bCol *= (col & 0xFF) / 255.0f;
        return this;
    }
    
    @Override
    public int getParticleTexture() {
        return ParticleEngine.TERRAIN_TEXTURE;
    }
    
    @Override
    public void render(final Tesselator t, final float partialTick, final float xa, final float ya, final float za, final float xa2, final float za2) {
        final float u0 = (this.tex % 16 + this.uo / 4.0f) / 16.0f;
        final float u1 = u0 + 0.999f / 16.0f / 4;
        final float v0 = (this.tex / 16 + this.vo / 4.0f) / 16.0f;
        final float v1 = v0 + 0.999f / 16.0f / 4;
        final float r = 0.1f * this.size;

        final float x = (float)(this.xo + (this.x - this.xo) * partialTick - TerrainParticle.xOff);
        final float y = (float)(this.yo + (this.y - this.yo) * partialTick - TerrainParticle.yOff);
        final float z = (float)(this.zo + (this.z - this.zo) * partialTick - TerrainParticle.zOff);

        final float br = this.getBrightness(partialTick);
        t.color(br * this.rCol, br * this.gCol, br * this.bCol);

        t.vertexUV(x - xa * r - xa2 * r, y - ya * r, z - za * r - za2 * r, u0, v1);
        t.vertexUV(x - xa * r + xa2 * r, y + ya * r, z - za * r + za2 * r, u0, v0);
        t.vertexUV(x + xa * r + xa2 * r, y + ya * r, z + za * r + za2 * r, u1, v0);
        t.vertexUV(x + xa * r - xa2 * r, y - ya * r, z + za * r - za2 * r, u1, v1);
    }
}
