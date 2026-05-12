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
    private int face;
    
    public TerrainParticle(final Level level, final double x, final double y, final double z, final double xa, final double ya, final double za, final Tile tile, final int face, final int data) {
        super(level, x, y, z, xa, ya, za);
        this.face = 0;
        this.tile = tile;
        this.tex = tile.getTexture(0, data);
        this.gravity = tile.gravity;
        final float rCol = 0.6f;
        this.bCol = rCol;
        this.gCol = rCol;
        this.rCol = rCol;
        this.size /= 2.0f;
        this.face = face;
    }
    
    public TerrainParticle init(final int x, final int y, final int z) {
        if (this.tile == Tile.grass) {
            return this;
        }
        final int color = this.tile.getColor(this.level, x, y, z);
        this.rCol *= (color >> 16 & 0xFF) / 255.0f;
        this.gCol *= (color >> 8 & 0xFF) / 255.0f;
        this.bCol *= (color & 0xFF) / 255.0f;
        return this;
    }
    
    @Override
    public int getParticleTexture() {
        return 1;
    }
    
    @Override
    public void render(final Tesselator t, final float partialTick, final float xa, final float ya, final float za, final float xa2, final float za2) {
        final float n = (this.tex % 16 + this.uo / 4.0f) / 16.0f;
        final float n2 = n + 0.015609375f;
        final float n3 = (this.tex / 16 + this.vo / 4.0f) / 16.0f;
        final float n4 = n3 + 0.015609375f;
        final float n5 = 0.1f * this.size;
        final float n6 = (float)(this.xo + (this.x - this.xo) * partialTick - TerrainParticle.xOff);
        final float n7 = (float)(this.yo + (this.y - this.yo) * partialTick - TerrainParticle.yOff);
        final float n8 = (float)(this.zo + (this.z - this.zo) * partialTick - TerrainParticle.zOff);
        final float brightness = this.getBrightness(partialTick);
        t.color(brightness * this.rCol, brightness * this.gCol, brightness * this.bCol);
        t.vertexUV(n6 - xa * n5 - xa2 * n5, n7 - ya * n5, n8 - za * n5 - za2 * n5, n, n4);
        t.vertexUV(n6 - xa * n5 + xa2 * n5, n7 + ya * n5, n8 - za * n5 + za2 * n5, n, n3);
        t.vertexUV(n6 + xa * n5 + xa2 * n5, n7 + ya * n5, n8 + za * n5 + za2 * n5, n2, n3);
        t.vertexUV(n6 + xa * n5 - xa2 * n5, n7 - ya * n5, n8 + za * n5 - za2 * n5, n2, n4);
    }
}
