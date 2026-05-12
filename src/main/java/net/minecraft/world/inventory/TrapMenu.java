// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.level.tile.entity.DispenserTileEntity;

public class TrapMenu extends AbstractContainerMenu
{
    private DispenserTileEntity trap;
    
    public TrapMenu(final Container container, final DispenserTileEntity trap) {
        this.trap = trap;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                this.addSlot(new Slot(trap, j + i * 3, 62 + j * 18, 17 + i * 18));
            }
        }
        for (int k = 0; k < 3; ++k) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(container, l + k * 9 + 9, 8 + l * 18, 84 + k * 18));
            }
        }
        for (int slot = 0; slot < 9; ++slot) {
            this.addSlot(new Slot(container, slot, 8 + slot * 18, 142));
        }
    }
    
    @Override
    public boolean stillValid(final Player player) {
        return this.trap.stillValid(player);
    }
}
