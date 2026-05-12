// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.player;

import net.minecraft.world.item.ItemInstance;
import util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;

public class RemotePlayer extends Player
{
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    float fallTime;
    
    public RemotePlayer(final Level level, final String name) {
        super(level);
        this.fallTime = 0.0f;
        this.name = name;
        this.heightOffset = 0.0f;
        this.footSize = 0.0f;
        if (name != null && name.length() > 0) {
            this.customTextureUrl = "http://s3.amazonaws.com/MinecraftSkins/" + name + ".png";
        }
        this.noPhysics = true;
        this.bedOffsetY = 0.25f;
        this.viewScale = 10.0;
    }
    
    @Override
    protected void setDefaultHeadHeight() {
        this.heightOffset = 0.0f;
    }
    
    @Override
    public boolean hurt(final Entity source, final int dmg) {
        return true;
    }
    
    @Override
    public void lerpTo(final double x, final double y, final double z, final float yRot, final float xRot, final int steps) {
        this.lx = x;
        this.ly = y;
        this.lz = z;
        this.lyr = yRot;
        this.lxr = xRot;
        this.lSteps = steps;
    }
    
    @Override
    public void tick() {
        this.bedOffsetY = 0.0f;
        super.tick();
        this.walkAnimSpeedO = this.walkAnimSpeed;
        final double n = this.x - this.xo;
        final double n2 = this.z - this.zo;
        float n3 = Mth.sqrt(n * n + n2 * n2) * 4.0f;
        if (n3 > 1.0f) {
            n3 = 1.0f;
        }
        this.walkAnimSpeed += (n3 - this.walkAnimSpeed) * 0.4f;
        this.walkAnimPos += this.walkAnimSpeed;
    }
    
    @Override
    public float getShadowHeightOffs() {
        return 0.0f;
    }
    
    @Override
    public void aiStep() {
        super.updateAi();
        if (this.lSteps > 0) {
            final double x = this.x + (this.lx - this.x) / this.lSteps;
            final double y = this.y + (this.ly - this.y) / this.lSteps;
            final double z = this.z + (this.lz - this.z) / this.lSteps;
            double n;
            for (n = this.lyr - this.yRot; n < -180.0; n += 360.0) {}
            while (n >= 180.0) {
                n -= 360.0;
            }
            this.yRot += (float)(n / this.lSteps);
            this.xRot += (float)((this.lxr - this.xRot) / this.lSteps);
            --this.lSteps;
            this.setPos(x, y, z);
            this.setRot(this.yRot, this.xRot);
        }
        this.oBob = this.bob;
        float sqrt = Mth.sqrt(this.xd * this.xd + this.zd * this.zd);
        float n2 = (float)Math.atan(-this.yd * 0.20000000298023224) * 15.0f;
        if (sqrt > 0.1f) {
            sqrt = 0.1f;
        }
        if (!this.onGround || this.health <= 0) {
            sqrt = 0.0f;
        }
        if (this.onGround || this.health <= 0) {
            n2 = 0.0f;
        }
        this.bob += (sqrt - this.bob) * 0.4f;
        this.tilt += (n2 - this.tilt) * 0.8f;
    }
    
    @Override
    public void setEquippedSlot(final int slot, final int itemId, final int auxValue) {
        ItemInstance itemInstance = null;
        if (itemId >= 0) {
            itemInstance = new ItemInstance(itemId, 1, auxValue);
        }
        if (slot == 0) {
            this.inventory.items[this.inventory.selected] = itemInstance;
        }
        else {
            this.inventory.armor[slot - 1] = itemInstance;
        }
    }
    
    @Override
    public void animateRespawn() {
    }
}
