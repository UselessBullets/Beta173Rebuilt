// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gamemode;

import net.minecraft.world.entity.Entity;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.LevelListener;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.client.Minecraft;

public class GameMode
{
    protected final Minecraft minecraft;
    public boolean instaBuild = false;
    
    public GameMode(final Minecraft minecraft) {
        this.minecraft = minecraft;
    }
    
    public void initLevel(final Level level) {
    }
    
    public void startDestroyBlock(final int x, final int y, final int z, final int face) {
        this.minecraft.level.extinguishFire(this.minecraft.player, x, y, z, face);
        this.destroyBlock(x, y, z, face);
    }
    
    public boolean destroyBlock(final int x, final int y, final int z, final int face) {
        final Level level = this.minecraft.level;
        final Tile oldTile = Tile.tiles[level.getTile(x, y, z)];

        level.levelEvent(LevelListener.PARTICLES_DESTROY_BLOCK, x, y, z, oldTile.id + level.getData(x, y, z) * Tile.TILE_NUM_COUNT);
        final int data = level.getData(x, y, z);
        final boolean changed = level.setTile(x, y, z, 0);

        if (oldTile != null && changed) {
            oldTile.destroy(level, x, y, z, data);
        }
        return changed;
    }
    
    public void continueDestroyBlock(final int x, final int y, final int z, final int face) {
    }
    
    public void stopDestroyBlock() {
    }
    
    public void render(final float a) {
    }
    
    public float getPickRange() {
        return 5.0f;
    }
    
    public boolean useItem(final Player player, final Level level, final ItemInstance item) {
        final int oldCount = item.count;
        final ItemInstance itemInstance = item.use(level, player);
        if (itemInstance != item || (itemInstance != null && itemInstance.count != oldCount)) {
            player.inventory.items[player.inventory.selected] = itemInstance;
            if (itemInstance.count == 0) {
                player.inventory.items[player.inventory.selected] = null;
            }
            return true;
        }
        return false;
    }
    
    public void initPlayer(final Player player) {
    }
    
    public void tick() {
    }
    
    public boolean canHurtPlayer() {
        return true;
    }
    
    public void adjustPlayer(final Player player) {
    }
    
    public boolean useItemOn(final Player player, final Level level, final ItemInstance item, final int x, final int y, final int z, final int face) {
        final int t = level.getTile(x, y, z);
        if (t > 0) {
            if (Tile.tiles[t].use(level, x, y, z, player)) {
                return true;
            }
        }

        if (item == null) return false;
        return item.useOn(player, level, x, y, z, face);
    }
    
    public Player createPlayer(final Level level) {
        return new LocalPlayer(this.minecraft, level, this.minecraft.user, level.dimension.id);
    }
    
    public void interact(final Player player, final Entity entity) {
        player.interact(entity);
    }
    
    public void attack(final Player player, final Entity entity) {
        player.attack(entity);
    }
    
    public ItemInstance handleInventoryMouseClick(final int containerId, final int slotNum, final int buttonNum, final boolean quickKeyHeld, final Player player) {
        return player.containerMenu.clicked(slotNum, buttonNum, quickKeyHeld, player);
    }
    
    public void handleCloseInventory(final int containerId, final Player player) {
        player.containerMenu.removed(player);
        player.containerMenu = player.inventoryMenu;
    }
}
