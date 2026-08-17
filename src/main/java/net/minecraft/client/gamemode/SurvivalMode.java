// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gamemode;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;

public class SurvivalMode extends GameMode
{
    private int xDestroyBlock = -1;
    private int yDestroyBlock = -1;
    private int zDestroyBlock = -1;
    private float destroyProgress = 0.0f;
    private float oDestroyProgress = 0.0f;
    private float destroyTicks = 0.0f;
    private int destroyDelay = 0;
    
    public SurvivalMode(final Minecraft minecraft) {
        super(minecraft);
    }
    
    @Override
    public void initPlayer(final Player player) {
        player.yRot = -180.0f;
    }
    
    @Override
    public boolean destroyBlock(final int x, final int y, final int z, final int face) {
        final int tile = this.minecraft.level.getTile(x, y, z);
        final int data = this.minecraft.level.getData(x, y, z);
        final boolean destroyBlock = super.destroyBlock(x, y, z, face);
        final ItemInstance selectedItem = this.minecraft.player.getSelectedItem();
        final boolean canDestroy = this.minecraft.player.canDestroy(Tile.tiles[tile]);
        if (selectedItem != null) {
            selectedItem.mineBlock(tile, x, y, z, this.minecraft.player);
            if (selectedItem.count == 0) {
                selectedItem.snap(this.minecraft.player);
                this.minecraft.player.removeSelectedItem();
            }
        }
        if (destroyBlock && canDestroy) {
            Tile.tiles[tile].playerDestroy(this.minecraft.level, this.minecraft.player, x, y, z, data);
        }
        return destroyBlock;
    }
    
    @Override
    public void startDestroyBlock(final int x, final int y, final int z, final int face) {
        this.minecraft.level.extinguishFire(this.minecraft.player, x, y, z, face);
        final int tile = this.minecraft.level.getTile(x, y, z);
        if (tile > 0 && this.destroyProgress == 0.0f) {
            Tile.tiles[tile].attack(this.minecraft.level, x, y, z, this.minecraft.player);
        }
        if (tile > 0 && Tile.tiles[tile].getDestroyProgress(this.minecraft.player) >= 1.0f) {
            this.destroyBlock(x, y, z, face);
        }
    }
    
    @Override
    public void stopDestroyBlock() {
        this.destroyProgress = 0.0f;
        this.destroyDelay = 0;
    }
    
    @Override
    public void continueDestroyBlock(final int x, final int y, final int z, final int face) {
        if (this.destroyDelay > 0) {
            --this.destroyDelay;
            return;
        }
        if (x == this.xDestroyBlock && y == this.yDestroyBlock && z == this.zDestroyBlock) {
            final int tile = this.minecraft.level.getTile(x, y, z);
            if (tile == 0) {
                return;
            }
            final Tile tile2 = Tile.tiles[tile];
            this.destroyProgress += tile2.getDestroyProgress(this.minecraft.player);
            if (this.destroyTicks % 4.0f == 0.0f && tile2 != null) {
                this.minecraft.soundEngine.play(tile2.soundType.getStepSound(), x + 0.5f, y + 0.5f, z + 0.5f, (tile2.soundType.getVolume() + 1.0f) / 8.0f, tile2.soundType.getPitch() * 0.5f);
            }
            ++this.destroyTicks;
            if (this.destroyProgress >= 1.0f) {
                this.destroyBlock(x, y, z, face);
                this.destroyProgress = 0.0f;
                this.oDestroyProgress = 0.0f;
                this.destroyTicks = 0.0f;
                this.destroyDelay = 5;
            }
        }
        else {
            this.destroyProgress = 0.0f;
            this.oDestroyProgress = 0.0f;
            this.destroyTicks = 0.0f;
            this.xDestroyBlock = x;
            this.yDestroyBlock = y;
            this.zDestroyBlock = z;
        }
    }
    
    @Override
    public void render(final float partialTick) {
        if (this.destroyProgress <= 0.0f) {
            this.minecraft.gui.progress = 0.0f;
            this.minecraft.levelRenderer.destroyProgress = 0.0f;
        }
        else {
            final float n = this.oDestroyProgress + (this.destroyProgress - this.oDestroyProgress) * partialTick;
            this.minecraft.gui.progress = n;
            this.minecraft.levelRenderer.destroyProgress = n;
        }
    }
    
    @Override
    public float getPickRange() {
        return 4.0f;
    }
    
    @Override
    public void initLevel(final Level level) {
        super.initLevel(level);
    }
    
    @Override
    public void tick() {
        this.oDestroyProgress = this.destroyProgress;
        this.minecraft.soundEngine.playMusicTick();
    }
}
