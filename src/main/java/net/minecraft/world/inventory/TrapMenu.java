// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.level.tile.entity.DispenserTileEntity;

public class TrapMenu extends AbstractContainerMenu
{
    private static final int INV_SLOT_START = 9;
    private static final int INV_SLOT_END = INV_SLOT_START + 9 * 3;
    private static final int USE_ROW_SLOT_START = INV_SLOT_END;
    private static final int USE_ROW_SLOT_END = USE_ROW_SLOT_START + 9;
    private DispenserTileEntity trap;
    
    public TrapMenu(final Container container, final DispenserTileEntity trap) {
        this.trap = trap;

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                this.addSlot(new Slot(trap, x + y * 3, 62 + x * 18, 17 + y * 18));
            }
        }

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlot(new Slot(container, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }
        for (int x = 0; x < 9; ++x) {
            this.addSlot(new Slot(container, x, 8 + x * 18, 70 + 4 * 18));
        }
    }
    
    @Override
    public boolean stillValid(final Player player) {
        return this.trap.stillValid(player);
    }
}
