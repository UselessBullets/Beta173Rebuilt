// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class StatsComponent_TickTimer implements ActionListener
{
    final /* synthetic */ StatsComponent sc;
    
    StatsComponent_TickTimer(final StatsComponent sc) {
        this.sc = sc;
    }
    
    public void actionPerformed(final ActionEvent actionEvent) {
        this.sc.tick();
    }
}
