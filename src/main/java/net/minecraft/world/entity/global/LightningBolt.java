// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.global;

import net.minecraft.world.phys.Vec3;
import com.mojang.nbt.CompoundTag;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.tile.Tile;
import util.Mth;
import net.minecraft.world.level.Level;

public class LightningBolt extends GlobalEntity
{
    private static final int START_LIFE = 2;
    private int life;
    public long seed = 0L;
    private int flashes;
    
    public LightningBolt(final Level level, final double x, final double y, final double z) {
        super(level);

        this.moveTo(x, y, z, 0.0f, 0.0f);
        this.life = START_LIFE;
        this.seed = this.random.nextLong();
        this.flashes = this.random.nextInt(3) + 1;

        if (level.difficulty >= 2 && level.hasChunksAt(Mth.floor(x), Mth.floor(y), Mth.floor(z), 10)) {
            {
                final int xt = Mth.floor(x);
                final int yt = Mth.floor(y);
                final int zt = Mth.floor(z);

                if (level.getTile(xt, yt, zt) == 0 && Tile.fire.mayPlace(level, xt, yt, zt)) level.setTile(xt, yt, zt, Tile.fire.id);
            }

            for (int i = 0; i < 4; ++i) {
                final int xt = Mth.floor(x) + this.random.nextInt(3) - 1;
                final int yt = Mth.floor(y) + this.random.nextInt(3) - 1;
                final int zt = Mth.floor(z) + this.random.nextInt(3) - 1;

                if (level.getTile(xt, yt, zt) == 0 && Tile.fire.mayPlace(level, xt, yt, zt)) level.setTile(xt, yt, zt, Tile.fire.id);
            }
        }
    }
    
    @Override
    public void tick() {
        super.tick();

        if (this.life == START_LIFE) {
            this.level.playSound(this.x, this.y, this.z, "ambient.weather.thunder", 10000.0f, 0.8f + this.random.nextFloat() * 0.2f);
            this.level.playSound(this.x, this.y, this.z, "random.explode", 2.0f, 0.5f + this.random.nextFloat() * 0.2f);
        }

        this.life--;
        if (this.life < 0) {
            if (this.flashes == 0) {
                this.remove();
            }
            else if (this.life < -this.random.nextInt(10)) {
                this.flashes--;
                this.life = 1;
                this.seed = this.random.nextLong();
                if (this.level.hasChunksAt(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z), 10)) {
                    final int xt = Mth.floor(this.x);
                    final int yt = Mth.floor(this.y);
                    final int zt = Mth.floor(this.z);

                    if (this.level.getTile(xt, yt, zt) == 0 && Tile.fire.mayPlace(this.level, xt, yt, zt)) this.level.setTile(xt, yt, zt, Tile.fire.id);
                }
            }
        }

        if (this.life >= 0) {
            final double r = 3.0;
            final List<Entity> entities = this.level.getEntities(this, AABB.newTemp(this.x - r, this.y - r, this.z - r, this.x + r, this.y + 6.0 + r, this.z + r));
            for (int i = 0; i < entities.size(); ++i) {
                Entity e = entities.get(i);
                e.thunderHit(this);
            }

            this.level.lightningBoltTime = 2;
        }
    }
    
    @Override
    protected void definedSynchedData() {
    }
    
    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {
    }
    
    @Override
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {
    }
    
    @Override
    public boolean shouldRender(final Vec3 c) {
        return this.life >= 0;
    }
}
