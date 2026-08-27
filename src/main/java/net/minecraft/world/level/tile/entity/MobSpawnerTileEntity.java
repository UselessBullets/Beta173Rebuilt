// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile.entity;

import com.mojang.nbt.CompoundTag;
import net.minecraft.SharedConstants;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.EntityIO;
import net.minecraft.world.entity.Mob;

public class MobSpawnerTileEntity extends TileEntity
{
    private static final int MAX_DIST = 16;
    public int spawnDelay = -1;
    private String entityId;
    public double spin, oSpin;
    
    public MobSpawnerTileEntity() {
        this.entityId = "Pig";
        this.spawnDelay = SharedConstants.TICKS_PER_SECOND * 1;
    }
    
    public String getEntityId() {
        return this.entityId;
    }
    
    public void setEntityId(final String entityId) {
        this.entityId = entityId;
    }
    
    public boolean isNearPlayer() {
        return this.level.getNearestPlayer(this.x + 0.5, this.y + 0.5, this.z + 0.5, MAX_DIST) != null;
    }
    
    @Override
    public void tick() {
        this.oSpin = this.spin;
        if (!this.isNearPlayer()) {
            return;
        }

        double xP = this.x + this.level.random.nextFloat();
        double yP = this.y + this.level.random.nextFloat();
        double zP = this.z + this.level.random.nextFloat();
        this.level.addParticle("smoke", xP, yP, zP, 0.0, 0.0, 0.0);
        this.level.addParticle("flame", xP, yP, zP, 0.0, 0.0, 0.0);

        this.spin += 1000.0f / (this.spawnDelay + 200.0f);
        while (this.spin > 360.0) {
            this.spin -= 360.0;
            this.oSpin -= 360.0;
        }

        if (!this.level.isClientSide) {
            if (this.spawnDelay == -1) this.delay();

            if (this.spawnDelay > 0) {
                this.spawnDelay--;
                return;
            }

            int spawnCount = 4;
            for (int c = 0; c < spawnCount; ++c) {
                final Mob mob = (Mob)EntityIO.newEntity(this.entityId, this.level);
                if (mob == null) return;

                int nearBy = this.level.getEntitiesOfClass(mob.getClass(), AABB.newTemp(this.x, this.y, this.z, this.x + 1, this.y + 1, this.z + 1).grow(8.0, 4.0, 8.0)).size();
                if (nearBy >= 6) {
                    this.delay();
                    return;
                }

                if (mob != null) {
                    double xp = this.x + (this.level.random.nextDouble() - this.level.random.nextDouble()) * 4.0;
                    double yp = this.y + this.level.random.nextInt(3) - 1;
                    double zp = this.z + (this.level.random.nextDouble() - this.level.random.nextDouble()) * 4.0;
                    mob.moveTo(xp, yp, zp, this.level.random.nextFloat() * 360.0f, 0.0f);

                    if (mob.canSpawn()) {
                        this.level.addEntity(mob);

                        for (int i = 0; i < 20; ++i) {
                            xP = this.x + 0.5 + (this.level.random.nextFloat() - 0.5) * 2.0;
                            yP = this.y + 0.5 + (this.level.random.nextFloat() - 0.5) * 2.0;
                            zP = this.z + 0.5 + (this.level.random.nextFloat() - 0.5) * 2.0;

                            this.level.addParticle("smoke", xP, yP, zP, 0.0, 0.0, 0.0);
                            this.level.addParticle("flame", xP, yP, zP, 0.0, 0.0, 0.0);
                        }

                        mob.spawnAnim();
                        this.delay();
                    }
                }
            }
        }
        super.tick();
    }
    
    private void delay() {
        this.spawnDelay = (SharedConstants.TICKS_PER_SECOND * 10) + this.level.random.nextInt(SharedConstants.TICKS_PER_SECOND * 40 - (SharedConstants.TICKS_PER_SECOND * 10)); // Useless - Numerically equivalent to b173 represents Max spawnDelay minus min spawn delay
    }
    
    @Override
    public void load(final CompoundTag tag) {
        super.load(tag);
        this.entityId = tag.getString("EntityId");
        this.spawnDelay = tag.getShort("Delay");
    }
    
    @Override
    public void save(final CompoundTag tag) {
        super.save(tag);
        tag.putString("EntityId", this.entityId);
        tag.putShort("Delay", (short)this.spawnDelay);
    }
}
