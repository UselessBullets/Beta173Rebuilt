// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.applet.Applet;

public class MinecraftApplet extends Applet
{
    private Canvas canvas;
    private Minecraft minecraft;
    private Thread thread;
    
    public MinecraftApplet() {
        this.thread = null;
    }
    
    @Override
    public void init() {
        this.canvas = new Canvas() {

            @Override
            public synchronized void addNotify() {
                super.addNotify();
                startGameThread();
            }

            @Override
            public synchronized void removeNotify() {
                stopGameThread();
                super.removeNotify();
            }
        };
        boolean fullscreen = false;
        if (this.getParameter("fullscreen") != null) {
            fullscreen = this.getParameter("fullscreen").equalsIgnoreCase("true");
        }
        this.minecraft = new Minecraft(this, this.canvas, this, this.getWidth(), this.getHeight(), fullscreen) {
            @Override
            public void onCrash(final CrashReport crashReport) {
                removeAll();
                setLayout(new BorderLayout());
                add(new CrashInfoPanel(crashReport), "Center");
                validate();
            }
        };
        this.minecraft.serverDomain = this.getDocumentBase().getHost();
        if (this.getDocumentBase().getPort() > 0) {
            final StringBuilder sb = new StringBuilder();
            final Minecraft minecraft = this.minecraft;
            minecraft.serverDomain = sb.append(minecraft.serverDomain).append(":").append(this.getDocumentBase().getPort()).toString();
        }
        if (this.getParameter("username") != null && this.getParameter("sessionid") != null) {
            this.minecraft.user = new User(this.getParameter("username"), this.getParameter("sessionid"));
            System.out.println("Setting user: " + this.minecraft.user.name + ", " + this.minecraft.user.sessionId);
            if (this.getParameter("mppass") != null) {
                this.minecraft.user.mpPassword = this.getParameter("mppass");
            }
        }
        else {
            this.minecraft.user = new User("Player", "");
        }
        if (this.getParameter("server") != null && this.getParameter("port") != null) {
            this.minecraft.connectTo(this.getParameter("server"), Integer.parseInt(this.getParameter("port")));
        }
        this.minecraft.appletMode = true;
        this.setLayout(new BorderLayout());
        this.add(this.canvas, "Center");
        this.canvas.setFocusable(true);
        this.validate();
    }
    
    public void startGameThread() {
        if (this.thread != null) {
            return;
        }
        (this.thread = new Thread(this.minecraft, "Minecraft main thread")).start();
    }
    
    @Override
    public void start() {
        if (this.minecraft != null) {
            this.minecraft.pause = false;
        }
    }
    
    @Override
    public void stop() {
        if (this.minecraft != null) {
            this.minecraft.pause = true;
        }
    }
    
    @Override
    public void destroy() {
        this.stopGameThread();
    }
    
    public void stopGameThread() {
        if (this.thread == null) {
            return;
        }
        this.minecraft.stop();
        try {
            this.thread.join(10000L);
        }
        catch (final InterruptedException ex) {
            try {
                this.minecraft.destroy();
            }
            catch (final Exception ex2) {
                ex2.printStackTrace();
            }
        }
        this.thread = null;
    }
    
    public void clearMemory() {
        this.canvas = null;
        this.minecraft = null;
        this.thread = null;
        try {
            this.removeAll();
            this.validate();
        }
        catch (final Exception ex) {}
    }
}
