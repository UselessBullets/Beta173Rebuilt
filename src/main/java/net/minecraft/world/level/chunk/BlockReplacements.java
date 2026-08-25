// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.chunk;

import net.minecraft.world.level.tile.Tile;

public class BlockReplacements
{
    private static byte[] replacements = new byte[256];
    
    public static void replace(final byte[] blocks) {
        for (int i = 0; i < blocks.length; ++i) {
            blocks[i] = BlockReplacements.replacements[blocks[i] & 0xFF];
        }
    }
    
    static {
        try {
            for (int i = 0; i < 256; ++i) {
                byte b = (byte)i;
                if (b != 0 && Tile.tiles[b & 0xFF] == null) {
                    b = 0;
                }
                BlockReplacements.replacements[i] = b;
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
