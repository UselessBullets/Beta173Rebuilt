// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile.entity;

import com.mojang.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.EntityIO;
import net.minecraft.world.entity.Mob;

public class MobSpawnerTileEntity extends TileEntity
{
    public int spawnDelay;
    private String entityId;
    public double spin;
    public double oSpin;
    
    public MobSpawnerTileEntity() {
        this.spawnDelay = -1;
        this.oSpin = 0.0;
        this.entityId = "Pig";
        this.spawnDelay = 20;
    }
    
    public void setEntityId(final String entityId) {
        this.entityId = entityId;
    }
    
    public boolean isNearPlayer() {
        return this.level.getNearestPlayer(this.x + 0.5, this.y + 0.5, this.z + 0.5, 16.0) != null;
    }
    
    @Override
    public void tick() {
        this.oSpin = this.spin;
        if (!this.isNearPlayer()) {
            return;
        }
        final double n = this.x + this.level.random.nextFloat();
        final double n2 = this.y + this.level.random.nextFloat();
        final double n3 = this.z + this.level.random.nextFloat();
        this.level.addParticle("smoke", n, n2, n3, 0.0, 0.0, 0.0);
        this.level.addParticle("flame", n, n2, n3, 0.0, 0.0, 0.0);
        this.spin += 1000.0f / (this.spawnDelay + 200.0f);
        while (this.spin > 360.0) {
            this.spin -= 360.0;
            this.oSpin -= 360.0;
        }
        if (!this.level.isClientSide) {
            if (this.spawnDelay == -1) {
                this.delay();
            }
            if (this.spawnDelay > 0) {
                --this.spawnDelay;
                return;
            }
            for (int n4 = 4, i = 0; i < n4; ++i) {
                final Mob e = (Mob)EntityIO.newEntity(this.entityId, this.level);
                if (e == null) {
                    return;
                }
                if (this.level.getEntitiesOfClass(e.getClass(), AABB.newTemp(this.x, this.y, this.z, this.x + 1, this.y + 1, this.z + 1).grow(8.0, 4.0, 8.0)).size() >= 6) {
                    this.delay();
                    return;
                }
                if (e != null) {
                    e.moveTo(this.x + (this.level.random.nextDouble() - this.level.random.nextDouble()) * 4.0, this.y + this.level.random.nextInt(3) - 1, this.z + (this.level.random.nextDouble() - this.level.random.nextDouble()) * 4.0, this.level.random.nextFloat() * 360.0f, 0.0f);
                    if (e.canSpawn()) {
                        this.level.addEntity(e);
                        for (int j = 0; j < 20; ++j) {
                            final double n5 = this.x + 0.5 + (this.level.random.nextFloat() - 0.5) * 2.0;
                            final double n6 = this.y + 0.5 + (this.level.random.nextFloat() - 0.5) * 2.0;
                            final double n7 = this.z + 0.5 + (this.level.random.nextFloat() - 0.5) * 2.0;
                            this.level.addParticle("smoke", n5, n6, n7, 0.0, 0.0, 0.0);
                            this.level.addParticle("flame", n5, n6, n7, 0.0, 0.0, 0.0);
                        }
                        e.spawnAnim();
                        this.delay();
                    }
                }
            }
        }
        super.tick();
    }
    
    private void delay() {
        this.spawnDelay = 200 + this.level.random.nextInt(600);
    }
    
    @Override
    public void load(final CompoundTag compoundTag) {
        super.load(compoundTag);
        this.entityId = compoundTag.getString("EntityId");
        this.spawnDelay = compoundTag.getShort("Delay");
    }
    
    @Override
    public void save(final CompoundTag compoundTag) {
        super.save(compoundTag);
        compoundTag.putString("EntityId", this.entityId);
        compoundTag.putShort("Delay", (short)this.spawnDelay);
    }
}
