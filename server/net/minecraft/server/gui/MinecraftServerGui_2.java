// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.gui;

import net.minecraft.server.ConsoleInputSource;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import java.awt.event.ActionListener;

class MinecraftServerGui_2 implements ActionListener
{
    final /* synthetic */ JTextField jtf;
    final /* synthetic */ MinecraftServerGui gui;
    
    MinecraftServerGui_2(final MinecraftServerGui gui, final JTextField jtf) {
        this.gui = gui;
        this.jtf = jtf;
    }
    
    public void actionPerformed(final ActionEvent actionEvent) {
        final String trim = this.jtf.getText().trim();
        if (trim.length() > 0) {
            this.gui.server.handleConsoleInput(trim, this.gui);
        }
        this.jtf.setText("");
    }
}
