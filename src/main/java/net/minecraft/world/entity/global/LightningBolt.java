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
    private int life;
    public long seed;
    private int flashes;
    
    public LightningBolt(final Level level, final double x, final double y, final double z) {
        super(level);
        this.seed = 0L;
        this.moveTo(x, y, z, 0.0f, 0.0f);
        this.life = 2;
        this.seed = this.random.nextLong();
        this.flashes = this.random.nextInt(3) + 1;
        if (level.difficulty >= 2 && level.hasChunksAt(Mth.floor(x), Mth.floor(y), Mth.floor(z), 10)) {
            final int floor = Mth.floor(x);
            final int floor2 = Mth.floor(y);
            final int floor3 = Mth.floor(z);
            if (level.getTile(floor, floor2, floor3) == 0 && Tile.fire.mayPlace(level, floor, floor2, floor3)) {
                level.setTile(floor, floor2, floor3, Tile.fire.id);
            }
            for (int i = 0; i < 4; ++i) {
                final int x2 = Mth.floor(x) + this.random.nextInt(3) - 1;
                final int y2 = Mth.floor(y) + this.random.nextInt(3) - 1;
                final int z2 = Mth.floor(z) + this.random.nextInt(3) - 1;
                if (level.getTile(x2, y2, z2) == 0 && Tile.fire.mayPlace(level, x2, y2, z2)) {
                    level.setTile(x2, y2, z2, Tile.fire.id);
                }
            }
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.life == 2) {
            this.level.playLocalSound(this.x, this.y, this.z, "ambient.weather.thunder", 10000.0f, 0.8f + this.random.nextFloat() * 0.2f);
            this.level.playLocalSound(this.x, this.y, this.z, "random.explode", 2.0f, 0.5f + this.random.nextFloat() * 0.2f);
        }
        --this.life;
        if (this.life < 0) {
            if (this.flashes == 0) {
                this.remove();
            }
            else if (this.life < -this.random.nextInt(10)) {
                --this.flashes;
                this.life = 1;
                this.seed = this.random.nextLong();
                if (this.level.hasChunksAt(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z), 10)) {
                    final int floor = Mth.floor(this.x);
                    final int floor2 = Mth.floor(this.y);
                    final int floor3 = Mth.floor(this.z);
                    if (this.level.getTile(floor, floor2, floor3) == 0 && Tile.fire.mayPlace(this.level, floor, floor2, floor3)) {
                        this.level.setTile(floor, floor2, floor3, Tile.fire.id);
                    }
                }
            }
        }
        if (this.life >= 0) {
            final double n = 3.0;
            final List entities = this.level.getEntities(this, AABB.newTemp(this.x - n, this.y - n, this.z - n, this.x + n, this.y + 6.0 + n, this.z + n));
            for (int i = 0; i < entities.size(); ++i) {
                ((Entity)entities.get(i)).thunderHit(this);
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
