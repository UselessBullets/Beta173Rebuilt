// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import java.awt.Dimension;
import java.awt.Canvas;

class CrashInfoPanel_Border extends Canvas
{
    public CrashInfoPanel_Border(final int size) {
        this.setPreferredSize(new Dimension(size, size));
        this.setMinimumSize(new Dimension(size, size));
    }
}
