// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.level;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.TileUpdatePacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.entity.player.Player;

public class ServerPlayerGameMode
{ // TODO somehow forgot to deobf this class
    private ServerLevel b;
    public Player player;
    private float c;
    private int d;
    private int e;
    private int f;
    private int g;
    private int h;
    private boolean i;
    private int j;
    private int k;
    private int l;
    private int m;
    
    public ServerPlayerGameMode(final ServerLevel dp) {
        this.c = 0.0f;
        this.b = dp;
    }

    public void tick() {
        ++this.h;
        if (this.i) {
            final int n = this.h - this.m;
            final int tile = this.b.getTile(this.j, this.k, this.l);
            if (tile != 0) {
                if (Tile.tiles[tile].getDestroyProgress(this.player) * (n + 1) >= 1.0f) {
                    this.i = false;
                    this.c(this.j, this.k, this.l);
                }
            }
            else {
                this.i = false;
            }
        }
    }
    
    public void startDestroyBlock(final int integer1, final int integer2, final int integer3, final int integer4) {
        this.b.extinguishFire(null, integer1, integer2, integer3, integer4);
        this.d = this.h;
        final int tile = this.b.getTile(integer1, integer2, integer3);
        if (tile > 0) {
            Tile.tiles[tile].attack(this.b, integer1, integer2, integer3, this.player);
        }
        if (tile > 0 && Tile.tiles[tile].getDestroyProgress(this.player) >= 1.0f) {
            this.c(integer1, integer2, integer3);
        }
        else {
            this.e = integer1;
            this.f = integer2;
            this.g = integer3;
        }
    }
    
    public void stopDestroyBlock(final int integer1, final int integer2, final int integer3) {
        if (integer1 == this.e && integer2 == this.f && integer3 == this.g) {
            final int n = this.h - this.d;
            final int tile = this.b.getTile(integer1, integer2, integer3);
            if (tile != 0) {
                if (Tile.tiles[tile].getDestroyProgress(this.player) * (n + 1) >= 0.7f) {
                    this.c(integer1, integer2, integer3);
                }
                else if (!this.i) {
                    this.i = true;
                    this.j = integer1;
                    this.k = integer2;
                    this.l = integer3;
                    this.m = this.d;
                }
            }
        }
        this.c = 0.0f;
    }
    
    public boolean b(final int integer1, final int integer2, final int integer3) {
        final Tile tile = Tile.tiles[this.b.getTile(integer1, integer2, integer3)];
        final int data = this.b.getData(integer1, integer2, integer3);
        final boolean setTile = this.b.setTile(integer1, integer2, integer3, 0);
        if (tile != null && setTile) {
            tile.destroy(this.b, integer1, integer2, integer3, data);
        }
        return setTile;
    }
    
    public boolean c(final int integer1, final int integer2, final int integer3) {
        final int tile = this.b.getTile(integer1, integer2, integer3);
        final int data = this.b.getData(integer1, integer2, integer3);
        this.b.levelEvent(this.player, 2001, integer1, integer2, integer3, tile + this.b.getData(integer1, integer2, integer3) * 256);
        final boolean b = this.b(integer1, integer2, integer3);
        final ItemInstance selectedItem = this.player.getSelectedItem();
        if (selectedItem != null) {
            selectedItem.mineBlock(tile, integer1, integer2, integer3, this.player);
            if (selectedItem.count == 0) {
                selectedItem.snap(this.player);
                this.player.removeSelectedItem();
            }
        }
        if (b && this.player.canDestroy(Tile.tiles[tile])) {
            Tile.tiles[tile].playerDestroy(this.b, this.player, integer1, integer2, integer3, data);
            ((ServerPlayer)this.player).connection.send(new TileUpdatePacket(integer1, integer2, integer3, this.b));
        }
        return b;
    }
    
    public boolean useItem(final Player em, final Level dj, final ItemInstance fy) {
        final int count = fy.count;
        final ItemInstance use = fy.use(dj, em);
        if (use != fy || (use != null && use.count != count)) {
            em.inventory.items[em.inventory.selected] = use;
            if (use.count == 0) {
                em.inventory.items[em.inventory.selected] = null;
            }
            return true;
        }
        return false;
    }
    
    public boolean useItemOn(final Player em, final Level dj, final ItemInstance fy, final int integer4, final int integer5, final int integer6, final int integer7) {
        final int tile = dj.getTile(integer4, integer5, integer6);
        return (tile > 0 && Tile.tiles[tile].use(dj, integer4, integer5, integer6, em)) || (fy != null && fy.useOn(em, dj, integer4, integer5, integer6, integer7));
    }
}
