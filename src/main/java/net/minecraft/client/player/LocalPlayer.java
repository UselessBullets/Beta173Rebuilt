// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.player;

import util.Mth;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.Stat;
import net.minecraft.client.particle.TakeAnimationParticle;
import net.minecraft.client.gui.inventory.TrapScreen;
import net.minecraft.world.level.tile.entity.DispenserTileEntity;
import net.minecraft.client.gui.inventory.FurnaceScreen;
import net.minecraft.world.level.tile.entity.FurnaceTileEntity;
import net.minecraft.client.gui.inventory.CraftingScreen;
import net.minecraft.client.gui.inventory.ContainerScreen;
import net.minecraft.world.Container;
import net.minecraft.client.gui.inventory.TextEditScreen;
import net.minecraft.world.level.tile.entity.SignTileEntity;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.stats.Achievements;
import net.minecraft.client.User;
import net.minecraft.world.level.Level;
import util.SmoothFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class LocalPlayer extends Player
{
    public Input input;
    protected Minecraft minecraft;
    private float flyX, flyY, flyZ;
    private SmoothFloat smoothFlyX = new SmoothFloat();
    private SmoothFloat smoothFlyY = new SmoothFloat();
    private SmoothFloat smoothFlyZ = new SmoothFloat();
    
    public LocalPlayer(final Minecraft minecraft, final Level level, final User user, final int dimension) {
        super(level);
        this.minecraft = minecraft;
        this.dimension = dimension;
        if (user != null && user.name != null && user.name.length() > 0) {
            this.customTextureUrl = "http://s3.amazonaws.com/MinecraftSkins/" + user.name + ".png";
        }
        this.name = user.name;
    }
    
    @Override
    public void move(final double xa, final double ya, final double za) {
        // Useless - recovered from LCE, presumably is the usage of the flying stuff stored in option
        if (Minecraft.DEADMAU5_CAMERA_CHEATS) {
            if (this == this.minecraft.player && this.minecraft.options.isFlying) {
                this.noPhysics = true;
                float tmp = this.walkDist; // update
                calculateFlight((float) xa, (float) ya, (float) za);
                this.fallDistance = 0.0f;
                this.yd = 0.0f;
                super.move(this.flyX, this.flyY, this.flyZ);
                this.onGround = true;
                this.walkDist = tmp;
            } else {
                this.noPhysics = false;
                super.move(xa, ya, za);
            }
        } else {
            super.move(xa, ya, za);
        }
    }

    // Useless - recovered from LCE, presumably is the usage of the flying stuff stored in option
    private void calculateFlight(float xa, float ya, float za)
    {
        xa = xa * this.minecraft.options.flySpeed;
        ya = ((this.input.jumping ? 1 : 0) + (this.input.sneaking ? -1 : 0)) * this.minecraft.options.flySpeed / 5;
        za = za * this.minecraft.options.flySpeed;

        this.flyX = this.smoothFlyX.getNewDeltaValue(xa, .35f * this.minecraft.options.sensitivity);
        this.flyY = this.smoothFlyY.getNewDeltaValue(ya, .35f * this.minecraft.options.sensitivity);
        this.flyZ = this.smoothFlyZ.getNewDeltaValue(za, .35f * this.minecraft.options.sensitivity);

    }
    
    public void updateAi() {
        super.updateAi();
        this.xxa = this.input.xa;
        this.yya = this.input.ya;
        this.jumping = this.input.jumping;
    }
    
    @Override
    public void aiStep() {
        if (!this.minecraft.stats.hasTaken(Achievements.openInventory)) {
            this.minecraft.achievementPopup.permanent(Achievements.openInventory);
        }
        this.oPortalTime = this.portalTime;
        if (this.isInsidePortal) {
            if (!this.level.isClientSide) {
                if (this.riding != null) this.ride(null);
            }
            if (this.minecraft.screen != null) this.minecraft.setScreen(null);

            if (this.portalTime == 0.0f) {
                this.minecraft.soundEngine.playUI("portal.trigger", 1.0f, this.random.nextFloat() * 0.4f + 0.8f);
            }
            this.portalTime += 1 / 80.0f;
            if (this.portalTime >= 1.0f) {
                this.portalTime = 1.0f;
                if (!this.level.isClientSide) {
                    this.changingDimensionDelay = 10;
                    this.minecraft.soundEngine.playUI("portal.travel", 1.0f, this.random.nextFloat() * 0.4f + 0.8f);
                    this.minecraft.toggleDimension();
                }
            }
            this.isInsidePortal = false;
        }
        else {
            if (this.portalTime > 0.0f) this.portalTime -= 1 / 20.0f;
            if (this.portalTime < 0.0f) this.portalTime = 0.0f;
        }
        if (this.changingDimensionDelay > 0) --this.changingDimensionDelay;

        this.input.tick(this);
        if (this.input.sneaking) {
            if (this.ySlideOffset < 0.2f) this.ySlideOffset = 0.2f;
        }
        this.checkInTile(this.x - this.bbWidth * 0.35, this.bb.y0 + 0.5, this.z + this.bbWidth * 0.35);
        this.checkInTile(this.x - this.bbWidth * 0.35, this.bb.y0 + 0.5, this.z - this.bbWidth * 0.35);
        this.checkInTile(this.x + this.bbWidth * 0.35, this.bb.y0 + 0.5, this.z - this.bbWidth * 0.35);
        this.checkInTile(this.x + this.bbWidth * 0.35, this.bb.y0 + 0.5, this.z + this.bbWidth * 0.35);

        super.aiStep();
    }
    
    public void releaseAllKeys() {
        this.input.releaseAllKeys();
    }
    
    public void setKey(final int eventKey, final boolean eventKeyState) {
        this.input.setKey(eventKey, eventKeyState);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("Score", this.score);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag entityTag) {
        super.readAdditionalSaveData(entityTag);
        this.score = entityTag.getInt("Score");
    }
    
    public void closeContainer() {
        super.closeContainer();
        this.minecraft.setScreen(null);
    }
    
    @Override
    public void openTextEdit(final SignTileEntity sign) {
        this.minecraft.setScreen(new TextEditScreen(sign));
    }
    
    @Override
    public void openContainer(final Container container) {
        this.minecraft.setScreen(new ContainerScreen(this.inventory, container));
    }
    
    @Override
    public void startCrafting(final int x, final int y, final int z) {
        this.minecraft.setScreen(new CraftingScreen(this.inventory, this.level, x, y, z));
    }
    
    @Override
    public void openFurnace(final FurnaceTileEntity furnace) {
        this.minecraft.setScreen(new FurnaceScreen(this.inventory, furnace));
    }
    
    @Override
    public void openTrap(final DispenserTileEntity trap) {
        this.minecraft.setScreen(new TrapScreen(this.inventory, trap));
    }
    
    @Override
    public void take(final Entity e, final int orgCount) {
        this.minecraft.particleEngine.add(new TakeAnimationParticle(this.minecraft.level, e, this, -0.5f));
    }
    
    public int getArmor() {
        return this.inventory.getArmorValue();
    }
    
    public void chat(final String message) {
    }
    
    @Override
    public boolean isSneaking() {
        return this.input.sneaking && !this.isSleeping;
    }
    
    public void hurtTo(final int newHealth) {
        final int dmg = this.health - newHealth;
        if (dmg <= 0) {
            this.health = newHealth;
            if (dmg < 0) {
                this.invulnerableTime = this.invulnerableDuration / 2;
            }
        }
        else {
            this.lastHurt = dmg;
            this.lastHealth = this.health;
            this.invulnerableTime = this.invulnerableDuration;
            this.actuallyHurt(dmg);
            this.hurtTime = this.hurtDuration = 10;
        }
    }
    
    @Override
    public void respawn() {
        this.minecraft.respawnPlayer(false, 0);
    }
    
    @Override
    public void animateRespawn() {
//        Player.animateRespawn(this, this.level); // Useless - As far as I can tell this was commented out in the source codebase from the java code left in the LCE leak
    }
    
    @Override
    public void displayClientMessage(final String message) {
        this.minecraft.gui.displayClientMessage(message);
    }
    
    @Override
    public void awardStat(final Stat stat, final int count) {
        if (stat == null) return;

        if (stat.isAchievement()) {
            final Achievement ach = (Achievement)stat;
            if (ach.requires == null || this.minecraft.stats.hasTaken(ach.requires)) {
                if (!this.minecraft.stats.hasTaken(ach)) {
                    this.minecraft.achievementPopup.popup(ach);
                }
                this.minecraft.stats.award(stat, count);
            }
        }
        else {
            this.minecraft.stats.award(stat, count);
        }
    }
    
    private boolean isSolidBlock(final int x, final int y, final int z) {
        return this.level.isSolidBlockingTile(x, y, z);
    }
    
    @Override
    protected boolean checkInTile(final double x, final double y, final double z) {
        final int xTile = Mth.floor(x);
        final int yTile = Mth.floor(y);
        final int zTile = Mth.floor(z);

        final double xd = x - xTile;
        final double zd = z - zTile;

        if (this.isSolidBlock(xTile, yTile, zTile) || this.isSolidBlock(xTile, yTile + 1, zTile)) {
            final boolean west = !this.isSolidBlock(xTile - 1, yTile, zTile) && !this.isSolidBlock(xTile - 1, yTile + 1, zTile);
            final boolean east = !this.isSolidBlock(xTile + 1, yTile, zTile) && !this.isSolidBlock(xTile + 1, yTile + 1, zTile);
            final boolean north = !this.isSolidBlock(xTile, yTile, zTile - 1) && !this.isSolidBlock(xTile, yTile + 1, zTile - 1);
            final boolean south = !this.isSolidBlock(xTile, yTile, zTile + 1) && !this.isSolidBlock(xTile, yTile + 1, zTile + 1);

            int dir = -1;
            double closest = 9999.0;
            if (west && xd < closest) {
                closest = xd;
                dir = 0;
            }
            if (east && 1.0 - xd < closest) {
                closest = 1.0 - xd;
                dir = 1;
            }
            if (north && zd < closest) {
                closest = zd;
                dir = 4;
            }
            if (south && 1.0 - zd < closest) {
                dir = 5;
            }

            final float speed = 0.1f;
            if (dir == 0) this.xd = -speed;
            if (dir == 1) this.xd = speed;
            if (dir == 4) this.zd = -speed;
            if (dir == 5) this.zd = speed;
        }
        return false;
    }
}
