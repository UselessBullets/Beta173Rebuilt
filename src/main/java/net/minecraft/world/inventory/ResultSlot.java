// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.inventory;

import net.minecraft.world.item.Item;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Achievements;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;

public class ResultSlot extends Slot
{
    private final Container craftSlots;
    private Player player;
    
    public ResultSlot(final Player player, final Container craftSlots, final Container container, final int id, final int x, final int y) {
        super(container, id, x, y);
        this.player = player;
        this.craftSlots = craftSlots;
    }
    
    @Override
    public boolean mayPlace(final ItemInstance item) {
        return false;
    }
    
    @Override
    public void onTake(final ItemInstance carried) {
        carried.onCraftedBy(this.player.level, this.player);

        if (carried.id == Tile.workBench.id) this.player.awardStat(Achievements.buildWorkbench, 1);
        else if (carried.id == Item.pickAxe_wood.id) this.player.awardStat(Achievements.buildPickaxe, 1);
        else if (carried.id == Tile.furnace.id) this.player.awardStat(Achievements.buildFurnace, 1);
        else if (carried.id == Item.hoe_wood.id) this.player.awardStat(Achievements.buildHoe, 1);
        else if (carried.id == Item.bread.id) this.player.awardStat(Achievements.makeBread, 1);
        else if (carried.id == Item.cake.id) this.player.awardStat(Achievements.bakeCake, 1);
        else if (carried.id == Item.pickAxe_stone.id) this.player.awardStat(Achievements.buildBetterPickaxe, 1);
        else if (carried.id == Item.sword_wood.id) this.player.awardStat(Achievements.buildSword, 1);

        for (int i = 0; i < this.craftSlots.getContainerSize(); ++i) {
            final ItemInstance item = this.craftSlots.getItem(i);
            if (item != null) {
                this.craftSlots.removeItem(i, 1);
                if (item.getItem().hasCraftingRemainingItem()) {
                    this.craftSlots.setItem(i, new ItemInstance(item.getItem().getCraftingRemainingItem()));
                }
            }
        }
    }
}
