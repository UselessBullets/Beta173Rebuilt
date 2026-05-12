// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gamemode;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.client.User;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;

public class CreativeMode extends GameMode
{
    public CreativeMode(final Minecraft minecraft) {
        super(minecraft);
        this.instaBuild = true;
    }
    
    @Override
    public void adjustPlayer(final Player player) {
        for (int i = 0; i < 9; ++i) {
            if (player.inventory.items[i] == null) {
                this.minecraft.player.inventory.items[i] = new ItemInstance((Tile)User.allowedTiles.get(i));
            }
            else {
                this.minecraft.player.inventory.items[i].count = 1;
            }
        }
    }
    
    @Override
    public boolean canHurtPlayer() {
        return false;
    }
    
    @Override
    public void initLevel(final Level level) {
        super.initLevel(level);
    }
    
    @Override
    public void tick() {
    }
}
