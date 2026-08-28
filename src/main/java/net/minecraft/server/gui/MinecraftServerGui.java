// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.gui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;

import net.minecraft.server.MinecraftServer;

import java.awt.event.FocusAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;
import net.minecraft.server.ConsoleInputSource;

public class MinecraftServerGui extends JComponent implements ConsoleInputSource
{
    public static Logger logger = Logger.getLogger("Minecraft");
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
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent windowEvent) {
                server.halt();
                while (!server.stopped) {
                    try {
                        Thread.sleep(100L);
                    }
                    catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.exit(0);
            }
        });
    }
    
    public MinecraftServerGui(final MinecraftServer server) {
        this.server = server;
        this.setPreferredSize(new Dimension(854, 480));
        this.setLayout(new BorderLayout());

        try {
            this.add(this.buildChatPanel(), "Center");
            this.add(this.buildInfoPanel(), "West");
        }
        catch (final Exception e) {
            e.printStackTrace();
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
        final JScrollPane scrollPane = new JScrollPane(new PlayerListComponent(this.server), ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
        return scrollPane;
    }
    
    private JComponent buildChatPanel() {
        final JPanel panel = new JPanel(new BorderLayout());
        final JTextArea textArea = new JTextArea();
        MinecraftServerGui.logger.addHandler(new LoggerHandler(textArea));
        final JScrollPane comp = new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        textArea.setEditable(false);
        final JTextField textField = new JTextField();
        textField.addActionListener(actionEvent -> {
            final String trim = textField.getText().trim();
            if (!trim.isEmpty()) {
                server.handleConsoleInput(trim, MinecraftServerGui.this);
            }
            textField.setText("");
        });
        textArea.addFocusListener(new FocusAdapter() {});
        panel.add(comp, "Center");
        panel.add(textField, "South");
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
        return panel;
    }
    
    public void info(final String string) {
        MinecraftServerGui.logger.info(string);
    }

    @Override
    public void warn(String string) {
        MinecraftServerGui.logger.warning(string);
    }

    public String getConsoleName() {
        return "CONSOLE";
    }

}
