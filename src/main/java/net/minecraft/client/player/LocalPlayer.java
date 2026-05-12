// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.player;

import util.Mth;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.Stat;
import net.minecraft.client.particle.Particle;
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
import net.minecraft.client.gui.Screen;
import net.minecraft.world.entity.Entity;
import net.minecraft.stats.Achievements;
import net.minecraft.client.User;
import net.minecraft.world.level.Level;
import net.minecraft.world.SmoothFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class LocalPlayer extends Player
{
    public Input input;
    protected Minecraft minecraft;
    private SmoothFloat smoothFlyX;
    private SmoothFloat smoothFlyY;
    private SmoothFloat smoothFlyZ;
    
    public LocalPlayer(final Minecraft minecraft, final Level level, final User user, final int dimension) {
        super(level);
        this.smoothFlyX = new SmoothFloat();
        this.smoothFlyY = new SmoothFloat();
        this.smoothFlyZ = new SmoothFloat();
        this.minecraft = minecraft;
        this.dimension = dimension;
        if (user != null && user.name != null && user.name.length() > 0) {
            this.customTextureUrl = "http://s3.amazonaws.com/MinecraftSkins/" + user.name + ".png";
        }
        this.name = user.name;
    }
    
    @Override
    public void move(final double xa, final double ya, final double za) {
        super.move(xa, ya, za);
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
            if (!this.level.isClientSide && this.riding != null) {
                this.ride(null);
            }
            if (this.minecraft.screen != null) {
                this.minecraft.setScreen(null);
            }
            if (this.portalTime == 0.0f) {
                this.minecraft.soundEngine.playUI("portal.trigger", 1.0f, this.random.nextFloat() * 0.4f + 0.8f);
            }
            this.portalTime += 0.0125f;
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
            if (this.portalTime > 0.0f) {
                this.portalTime -= 0.05f;
            }
            if (this.portalTime < 0.0f) {
                this.portalTime = 0.0f;
            }
        }
        if (this.changingDimensionDelay > 0) {
            --this.changingDimensionDelay;
        }
        this.input.tick(this);
        if (this.input.sneaking && this.ySlideOffset < 0.2f) {
            this.ySlideOffset = 0.2f;
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
    public void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.score = compoundTag.getInt("Score");
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
        final int n = this.health - newHealth;
        if (n <= 0) {
            this.health = newHealth;
            if (n < 0) {
                this.invulnerableTime = this.invulnerableDuration / 2;
            }
        }
        else {
            this.lastHurt = n;
            this.lastHealth = this.health;
            this.invulnerableTime = this.invulnerableDuration;
            this.actuallyHurt(n);
            final int n2 = 10;
            this.hurtDuration = n2;
            this.hurtTime = n2;
        }
    }
    
    @Override
    public void respawn() {
        this.minecraft.respawnPlayer(false, 0);
    }
    
    @Override
    public void animateRespawn() {
    }
    
    @Override
    public void displayClientMessage(final String message) {
        this.minecraft.gui.displayClientMessage(message);
    }
    
    @Override
    public void awardStat(final Stat stat, final int count) {
        if (stat == null) {
            return;
        }
        if (stat.isAchievement()) {
            final Achievement achievement = (Achievement)stat;
            if (achievement.requires == null || this.minecraft.stats.hasTaken(achievement.requires)) {
                if (!this.minecraft.stats.hasTaken(achievement)) {
                    this.minecraft.achievementPopup.popup(achievement);
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
        final int floor = Mth.floor(x);
        final int floor2 = Mth.floor(y);
        final int floor3 = Mth.floor(z);
        final double n = x - floor;
        final double n2 = z - floor3;
        if (this.isSolidBlock(floor, floor2, floor3) || this.isSolidBlock(floor, floor2 + 1, floor3)) {
            final boolean b = !this.isSolidBlock(floor - 1, floor2, floor3) && !this.isSolidBlock(floor - 1, floor2 + 1, floor3);
            final boolean b2 = !this.isSolidBlock(floor + 1, floor2, floor3) && !this.isSolidBlock(floor + 1, floor2 + 1, floor3);
            final boolean b3 = !this.isSolidBlock(floor, floor2, floor3 - 1) && !this.isSolidBlock(floor, floor2 + 1, floor3 - 1);
            final boolean b4 = !this.isSolidBlock(floor, floor2, floor3 + 1) && !this.isSolidBlock(floor, floor2 + 1, floor3 + 1);
            int n3 = -1;
            double n4 = 9999.0;
            if (b && n < n4) {
                n4 = n;
                n3 = 0;
            }
            if (b2 && 1.0 - n < n4) {
                n4 = 1.0 - n;
                n3 = 1;
            }
            if (b3 && n2 < n4) {
                n4 = n2;
                n3 = 4;
            }
            if (b4 && 1.0 - n2 < n4) {
                n3 = 5;
            }
            final float n5 = 0.1f;
            if (n3 == 0) {
                this.xd = -n5;
            }
            if (n3 == 1) {
                this.xd = n5;
            }
            if (n3 == 4) {
                this.zd = -n5;
            }
            if (n3 == 5) {
                this.zd = n5;
            }
        }
        return false;
    }
}
