// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.item;

import com.mojang.nbt.CompoundTag;
import net.minecraft.world.level.tile.SandTile;
import util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;

public class FallingTile extends Entity
{
    public int tile;
    public int time;
    
    public FallingTile(final Level level) {
        super(level);
        this.time = 0;
    }
    
    public FallingTile(final Level level, final double xo, final double yo, final double zo, final int tile) {
        super(level);
        this.time = 0;
        this.tile = tile;
        this.blocksBuilding = true;
        this.setSize(0.98f, 0.98f);
        this.heightOffset = this.bbHeight / 2.0f;
        this.setPos(xo, yo, zo);
        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;
        this.xo = xo;
        this.yo = yo;
        this.zo = zo;
    }
    
    @Override
    protected boolean makeStepSound() {
        return false;
    }
    
    @Override
    protected void definedSynchedData() {
    }
    
    @Override
    public boolean isPickable() {
        return !this.removed;
    }
    
    @Override
    public void tick() {
        if (this.tile == 0) {
            this.remove();
            return;
        }
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        ++this.time;
        this.yd -= 0.03999999910593033;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.98f;
        this.yd *= 0.98f;
        this.zd *= 0.98f;
        final int floor = Mth.floor(this.x);
        final int floor2 = Mth.floor(this.y);
        final int floor3 = Mth.floor(this.z);
        if (this.level.getTile(floor, floor2, floor3) == this.tile) {
            this.level.setTile(floor, floor2, floor3, 0);
        }
        if (this.onGround) {
            this.xd *= 0.7f;
            this.zd *= 0.7f;
            this.yd *= -0.5;
            this.remove();
            if (!this.level.mayPlace(this.tile, floor, floor2, floor3, true, 1) || SandTile.isFree(this.level, floor, floor2 - 1, floor3) || !this.level.setTile(floor, floor2, floor3, this.tile)) {
                if (!this.level.isClientSide) {
                    this.spawnAtLocation(this.tile, 1);
                }
            }
        }
        else if (this.time > 100 && !this.level.isClientSide) {
            this.spawnAtLocation(this.tile, 1);
            this.remove();
        }
    }
    
    @Override
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {
        compoundTag.putByte("Tile", (byte)this.tile);
    }
    
    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {
        this.tile = (compoundTag.getByte("Tile") & 0xFF);
    }
    
    @Override
    public float getShadowHeightOffs() {
        return 0.0f;
    }
    
    public Level getLevel() {
        return this.level;
    }
}
