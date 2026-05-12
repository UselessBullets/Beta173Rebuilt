// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.gui;

import java.awt.event.FocusListener;
import java.awt.event.ActionListener;
import javax.swing.JTextField;
import java.util.logging.Handler;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.JPanel;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowListener;
import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.UIManager;
import net.minecraft.server.MinecraftServer;
import java.util.logging.Logger;
import net.minecraft.server.ConsoleInputSource;
import javax.swing.JComponent;

public class MinecraftServerGui extends JComponent implements ConsoleInputSource
{
    public static Logger logger;
    private MinecraftServer server;
    
    public static void showFrameFor(final MinecraftServer server) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (final Exception ex) {}
        final MinecraftServerGui comp = new MinecraftServerGui(server);
        final JFrame frame = new JFrame("Minecraft server");
        frame.add(comp);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.addWindowListener(new MinecraftServerGui_1(server));
    }
    
    public MinecraftServerGui(final MinecraftServer server) {
        this.server = server;
        this.setPreferredSize(new Dimension(854, 480));
        this.setLayout(new BorderLayout());
        try {
            this.add(this.buildChatPanel(), "Center");
            this.add(this.buildInfoPanel(), "West");
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private JComponent buildInfoPanel() {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(new StatsComponent(), "North");
        panel.add(this.buildPlayerPanel(), "Center");
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
        return panel;
    }
    
    private JComponent buildPlayerPanel() {
        final JScrollPane scrollPane = new JScrollPane(new PlayerListComponent(this.server), 22, 30);
        scrollPane.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
        return scrollPane;
    }
    
    private JComponent buildChatPanel() {
        final JPanel panel = new JPanel(new BorderLayout());
        final JTextArea textArea = new JTextArea();
        MinecraftServerGui.logger.addHandler(new LoggerHandler(textArea));
        final JScrollPane comp = new JScrollPane(textArea, 22, 30);
        textArea.setEditable(false);
        final JTextField textField = new JTextField();
        textField.addActionListener(new MinecraftServerGui_2(this, textField));
        textArea.addFocusListener(new MinecraftServerGui_3(this));
        panel.add(comp, "Center");
        panel.add(textField, "South");
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
        return panel;
    }
    
    public void info(final String string) {
        MinecraftServerGui.logger.info(string);
    }
    
    public String getConsoleName() {
        return "CONSOLE";
    }
    
    static {
        MinecraftServerGui.logger = Logger.getLogger("Minecraft");
    }
}
