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
    private double lx, ly, lz, lyr, lxr;
    float fallTime = 0.0f;
    
    public RemotePlayer(final Level level, final String name) {
        super(level);
        this.name = name;

        this.heightOffset = 0.0f;
        this.footSize = 0.0f;
        if (name != null && name.length() > 0) {
            this.customTextureUrl = "http://s3.amazonaws.com/MinecraftSkins/" + name + ".png";
        }

        this.noPhysics = true;

        this.bedOffsetY = 4 / 16.0f;

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
//        heightOffset = 0;  // Useless - Source Comment
        this.lx = x;
        this.ly = y;
        this.lz = z;
        this.lyr = yRot;
        this.lxr = xRot;

        this.lSteps = steps;
    }
    
    @Override
    public void tick() {
        this.bedOffsetY = 0 / 16.0f;
        super.tick();

        this.walkAnimSpeedO = this.walkAnimSpeed;
        final double xxd = this.x - this.xo;
        final double zzd = this.z - this.zo;
        float wst = Mth.sqrt(xxd * xxd + zzd * zzd) * 4.0f;
        if (wst > 1.0f) wst = 1.0f;
        this.walkAnimSpeed += (wst - this.walkAnimSpeed) * 0.4f;
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
            final double xt = this.x + (this.lx - this.x) / this.lSteps;
            final double yt = this.y + (this.ly - this.y) / this.lSteps;
            final double zt = this.z + (this.lz - this.z) / this.lSteps;

            double yrd = this.lyr - this.yRot;
            while (yrd < -180.0) yrd += 360.0;
            while (yrd >= 180.0) yrd -= 360.0;

            this.yRot += (float)(yrd / this.lSteps);
            this.xRot += (float)((this.lxr - this.xRot) / this.lSteps);

            --this.lSteps;
            this.setPos(xt, yt, zt);
            this.setRot(this.yRot, this.xRot);
        }
        this.oBob = this.bob;

        float tBob = Mth.sqrt(this.xd * this.xd + this.zd * this.zd);
        float tTile = (float)Math.atan(-this.yd * 0.2f) * 15.0f;
        if (tBob > 0.1f) tBob = 0.1f;
        if (!this.onGround || this.health <= 0) tBob = 0.0f;
        if (this.onGround || this.health <= 0) tTile = 0.0f;
        this.bob += (tBob - this.bob) * 0.4f;
        this.tilt += (tTile - this.tilt) * 0.8f;
    }
    
    @Override
    public void setEquippedSlot(final int slot, final int itemId, final int auxValue) {
        ItemInstance item = null;
        if (itemId >= 0) item = new ItemInstance(itemId, 1, auxValue);

        if (slot == 0) {
            this.inventory.items[this.inventory.selected] = item;
        }
        else {
            this.inventory.armor[slot - 1] = item;
        }
    }
    
    @Override
    public void animateRespawn() {
//        Player.animateRespawn(this, this.level); // Useless - Source Comment
    }
}
