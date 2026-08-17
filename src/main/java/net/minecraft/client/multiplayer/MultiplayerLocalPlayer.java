// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.multiplayer;

import net.minecraft.stats.Stat;
import net.minecraft.network.packet.ContainerClosePacket;
import net.minecraft.network.packet.RespawnPacket;
import net.minecraft.network.packet.AnimatePacket;
import net.minecraft.network.packet.ChatPacket;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.network.packet.PlayerActionPacket;
import net.minecraft.network.packet.MovePlayerPacket;
import net.minecraft.network.packet.PlayerCommandPacket;
import util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.User;
import net.minecraft.world.level.Level;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class MultiplayerLocalPlayer extends LocalPlayer
{
    public ClientConnection connection;
    private int lastInventorySendTime = 0;
    private boolean flashOnSetHealth = false;
    private double xLast;
    private double yLast1;
    private double yLast2;
    private double zLast;
    private float yRotLast;
    private float xRotLast;
    private boolean lastOnGround = false;
    private boolean lastSneaked = false;
    private int noSendTime = 0;
    
    public MultiplayerLocalPlayer(final Minecraft minecraft, final Level level, final User user, final ClientConnection connection) {
        super(minecraft, level, user, 0);
        this.connection = connection;
    }
    
    @Override
    public boolean hurt(final Entity source, final int dmg) {
        return false;
    }
    
    @Override
    public void heal(final int heal) {
    }
    
    @Override
    public void tick() {
        if (!this.level.hasChunkAt(Mth.floor(this.x), 64, Mth.floor(this.z))) return;

        super.tick();
        this.sendPosition();
    }
    
    public void sendPosition() {
        if (this.lastInventorySendTime++ == 20) {
            this.ensureHasSentInventory();
            this.lastInventorySendTime = 0;
        }

        final boolean sneaking = this.isSneaking();
        if (sneaking != this.lastSneaked) {
            if (sneaking) this.connection.send(new PlayerCommandPacket(this, PlayerCommandPacket.START_SNEAKING));
            else this.connection.send(new PlayerCommandPacket(this, PlayerCommandPacket.STOP_SNEAKING));

            this.lastSneaked = sneaking;
        }

        final double xdd = this.x - this.xLast;
        final double ydd1 = this.bb.y0 - this.yLast1;
        final double ydd2 = this.y - this.yLast2;
        final double zdd = this.z - this.zLast;

        final double rydd = this.yRot - this.yRotLast;
        final double rxdd = this.xRot - this.xRotLast;

        boolean move = ydd1 != 0.0 || ydd2 != 0.0 || xdd != 0.0 || zdd != 0.0;
        final boolean rot = rydd != 0.0 || rxdd != 0.0;
        if (this.riding != null) {
            if (rot) {
                this.connection.send(new MovePlayerPacket.Pos(this.xd, -999.0, -999.0, this.zd, this.onGround));
            }
            else {
                this.connection.send(new MovePlayerPacket.PosRot(this.xd, -999.0, -999.0, this.zd, this.yRot, this.xRot, this.onGround));
            }
            move = false;
        }
        else if (move && rot) {
            this.connection.send(new MovePlayerPacket.PosRot(this.x, this.bb.y0, this.y, this.z, this.yRot, this.xRot, this.onGround));
            this.noSendTime = 0;
        }
        else if (move) {
            this.connection.send(new MovePlayerPacket.Pos(this.x, this.bb.y0, this.y, this.z, this.onGround));
            this.noSendTime = 0;
        }
        else if (rot) {
            this.connection.send(new MovePlayerPacket.Rot(this.yRot, this.xRot, this.onGround));
            this.noSendTime = 0;
        }
        else {
            this.connection.send(new MovePlayerPacket(this.onGround));
            if (this.lastOnGround != this.onGround || this.noSendTime > 200) {
                this.noSendTime = 0;
            }
            else {
                ++this.noSendTime;
            }
        }

        this.lastOnGround = this.onGround;

        if (move) {
            this.xLast = this.x;
            this.yLast1 = this.bb.y0;
            this.yLast2 = this.y;
            this.zLast = this.z;
        }
        if (rot) {
            this.yRotLast = this.yRot;
            this.xRotLast = this.xRot;
        }
    }
    
    @Override
    public void drop() {
        this.connection.send(new PlayerActionPacket(PlayerActionPacket.DROP_ITEM, 0, 0, 0, 0));
    }
    
    private void ensureHasSentInventory() {
    }
    
    @Override
    protected void reallyDrop(final ItemEntity itemEntity) {
    }
    
    @Override
    public void chat(final String message) {
        this.connection.send(new ChatPacket(message));
    }
    
    @Override
    public void swing() {
        super.swing();
        this.connection.send(new AnimatePacket(this, AnimatePacket.SWING));
    }
    
    @Override
    public void respawn() {
        this.ensureHasSentInventory();
        this.connection.send(new RespawnPacket((byte)this.dimension));
    }
    
    @Override
    protected void actuallyHurt(final int dmg) {
        this.health -= dmg;
    }
    
    @Override
    public void closeContainer() {
        this.connection.send(new ContainerClosePacket(this.containerMenu.containerId));
        this.inventory.setCarried(null);
        super.closeContainer();
    }
    
    @Override
    public void hurtTo(final int newHealth) {
        if (this.flashOnSetHealth) {
            super.hurtTo(newHealth);
        }
        else {
            this.health = newHealth;
            this.flashOnSetHealth = true;
        }
    }
    
    @Override
    public void awardStat(final Stat stat, final int count) {
        if (stat == null) return;
        if (stat.awardLocallyOnly) super.awardStat(stat, count);
    }
    
    public void awardStatFromServer(final Stat stat, final int count) {
        if (stat != null && !stat.awardLocallyOnly) {
            super.awardStat(stat, count);
        }
    }
}
