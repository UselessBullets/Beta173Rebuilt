// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server;

import net.minecraft.server.gui.MinecraftServerGui;
import java.awt.GraphicsEnvironment;
import net.minecraft.stats.Stats;
import net.minecraft.network.packet.SetTimePacket;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.Pos;
import net.minecraft.server.level.ServerLevelListener;
import net.minecraft.server.level.DerivedServerLevel;
import net.minecraft.world.level.storage.McRegionLevelStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.McRegionLevelStorageSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.Random;
import java.io.IOException;
import java.util.logging.Level;
import java.net.InetAddress;
import java.io.File;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerConnection;
import util.ProgressListener;

import java.util.HashMap;
import java.util.logging.Logger;

public class MinecraftServer implements Runnable, ConsoleInputSource
{
    public static Logger logger;
    public static HashMap<String, Integer> ironTimers;
    public ServerConnection connection;
    public Settings settings;
    public ServerLevel[] levels;
    public PlayerList players;
    private ConsoleCommands commands;
    private boolean running;
    public boolean stopped;
    int tickCount;
    public String progressStatus;
    public int progress;
    private List<Tickable> tickables;
    private List<ConsoleInput> consoleInput;
    public EntityTracker[] trackers;
    public boolean onlineMode;
    public boolean isAnimals;
    public boolean pvp;
    public boolean isFlightAllowed;
    
    public MinecraftServer() {
        this.running = true;
        this.stopped = false;
        this.tickCount = 0;
        this.tickables = new ArrayList<>();
        this.consoleInput = Collections.synchronizedList(new ArrayList<>());
        this.trackers = new EntityTracker[2];
        new Thread() {
            {
                this.setDaemon(true);
                this.start();
            }

            @Override
            public void run() {
                while (true) {
                    try {
                        while (true) {
                            Thread.sleep(2147483647L);
                        }
                    }
                    catch (final InterruptedException ex) {}
                }
            }
        };
    }
    
    private boolean initServer() throws UnknownHostException {
        this.commands = new ConsoleCommands(this);
        final Thread t = new Thread(() -> {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            try {
                String line;
                while (!this.stopped && this.running && (line = bufferedReader.readLine()) != null) {
                    handleConsoleInput(line, MinecraftServer.this);
                }
            }
            catch (final IOException ex) {
                ex.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
        LogConfigurator.initLogger();
        MinecraftServer.logger.info("Starting minecraft server version Beta 1.7.3");
        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
            MinecraftServer.logger.warning("**** NOT ENOUGH RAM!");
            MinecraftServer.logger.warning("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }
        MinecraftServer.logger.info("Loading properties");
        this.settings = new Settings(new File("server.properties"));
        final String string = this.settings.getString("server-ip", "");
        this.onlineMode = this.settings.getBoolean("online-mode", true);
        this.isAnimals = this.settings.getBoolean("spawn-animals", true);
        this.pvp = this.settings.getBoolean("pvp", true);
        this.isFlightAllowed = this.settings.getBoolean("allow-flight", false);
        InetAddress byName = null;
        if (string.length() > 0) {
            byName = InetAddress.getByName(string);
        }
        final int int1 = this.settings.getInt("server-port", 25565);
        MinecraftServer.logger.info("Starting Minecraft server on " + ((string.length() == 0) ? "*" : string) + ":" + int1);
        try {
            this.connection = new ServerConnection(this, byName, int1);
        }
        catch (final IOException ex) {
            MinecraftServer.logger.warning("**** FAILED TO BIND TO PORT!");
            MinecraftServer.logger.log(Level.WARNING, "The exception was: " + ex.toString());
            MinecraftServer.logger.warning("Perhaps a server is already running on that port?");
            return false;
        }
        if (!this.onlineMode) {
            MinecraftServer.logger.warning("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
            MinecraftServer.logger.warning("The server will make no attempt to authenticate usernames. Beware.");
            MinecraftServer.logger.warning("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
            MinecraftServer.logger.warning("To change this, set \"online-mode\" to \"true\" in the server.settings file.");
        }
        this.players = new PlayerList(this);
        this.trackers[0] = new EntityTracker(this, 0);
        this.trackers[1] = new EntityTracker(this, -1);
        final long nanoTime = System.nanoTime();
        final String string2 = this.settings.getString("level-name", "world");
        final String string3 = this.settings.getString("level-seed", "");
        long seed = new Random().nextLong();
        if (string3.length() > 0) {
            try {
                seed = Long.parseLong(string3);
            }
            catch (final NumberFormatException ex2) {
                seed = string3.hashCode();
            }
        }
        MinecraftServer.logger.info("Preparing level \"" + string2 + "\"");
        this.loadLevel(new McRegionLevelStorageSource(new File(".")), string2, seed);
        MinecraftServer.logger.info("Done (" + (System.nanoTime() - nanoTime) + "ns)! For help, type \"help\" or \"?\"");
        return true;
    }
    
    private void loadLevel(final LevelStorageSource storageSource, final String name, final long seed) {
        if (storageSource.requiresConversion(name)) {
            MinecraftServer.logger.info("Converting map!");
            storageSource.convertLevel(name, new ProgressListener() {
                private long lastCheckTime = System.currentTimeMillis();

                public void progressStartNoAbort(final String string) {
                }

                @Override
                public void progressStart(String var1) {

                }

                public void progressStagePercentage(final int i) {
                    if (System.currentTimeMillis() - this.lastCheckTime >= 1000L) {
                        this.lastCheckTime = System.currentTimeMillis();
                        logger.info("Converting... " + i + "%");
                    }
                }

                public void progressStage(final String status) {
                }
            });
        }
        this.levels = new ServerLevel[2];
        final McRegionLevelStorage mcRegionLevelStorage = new McRegionLevelStorage(new File("."), name, true);
        for (int i = 0; i < this.levels.length; ++i) {
            if (i == 0) {
                this.levels[i] = new ServerLevel(this, mcRegionLevelStorage, name, (i == 0) ? 0 : -1, seed);
            }
            else {
                this.levels[i] = new DerivedServerLevel(this, mcRegionLevelStorage, name, (i == 0) ? 0 : -1, seed, this.levels[0]);
            }
            this.levels[i].addListener(new ServerLevelListener(this, this.levels[i]));
            this.levels[i].difficulty = (this.settings.getBoolean("spawn-monsters", true) ? 1 : 0);
            this.levels[i].setSpawnSettings(this.settings.getBoolean("spawn-monsters", true), this.isAnimals);
            this.players.setLevel(this.levels);
        }
        final int n = 196;
        long currentTimeMillis = System.currentTimeMillis();
        for (int j = 0; j < this.levels.length; ++j) {
            MinecraftServer.logger.info("Preparing start region for level " + j);
            if (j == 0 || this.settings.getBoolean("allow-nether", true)) {
                final ServerLevel serverLevel = this.levels[j];
                final Pos sharedSpawnPos = serverLevel.getSharedSpawnPos();
                for (int n2 = -n; n2 <= n && this.running; n2 += 16) {
                    for (int n3 = -n; n3 <= n && this.running; n3 += 16) {
                        final long currentTimeMillis2 = System.currentTimeMillis();
                        if (currentTimeMillis2 < currentTimeMillis) {
                            currentTimeMillis = currentTimeMillis2;
                        }
                        if (currentTimeMillis2 > currentTimeMillis + 1000L) {
                            this.setProgress("Preparing spawn area", ((n2 + n) * (n * 2 + 1) + (n3 + 1)) * 100 / ((n * 2 + 1) * (n * 2 + 1)));
                            currentTimeMillis = currentTimeMillis2;
                        }
                        serverLevel.cache.create(sharedSpawnPos.x + n2 >> 4, sharedSpawnPos.z + n3 >> 4);
                        while (serverLevel.updateLights() && this.running) {}
                    }
                }
            }
        }
        this.endProgress();
    }
    
    private void setProgress(final String status, final int progress) {
        this.progressStatus = status;
        this.progress = progress;
        MinecraftServer.logger.info(status + ": " + progress + "%");
    }
    
    private void endProgress() {
        this.progressStatus = null;
        this.progress = 0;
    }
    
    private void saveAllChunks() {
        MinecraftServer.logger.info("Saving chunks");
        for (int i = 0; i < this.levels.length; ++i) {
            final ServerLevel serverLevel = this.levels[i];
            serverLevel.save(true, null);
            serverLevel.closeLevelStorage();
        }
    }
    
    private void stopServer() {
        MinecraftServer.logger.info("Stopping server");
        if (this.players != null) {
            this.players.saveAll();
        }
        for (int i = 0; i < this.levels.length; ++i) {
            if (this.levels[i] != null) {
                this.saveAllChunks();
            }
        }
    }
    
    public void halt() {
        this.running = false;
    }
    
    public void run() {
        try {
            if (this.initServer()) {
                long currentTimeMillis = System.currentTimeMillis();
                long n = 0L;
                while (this.running) {
                    final long currentTimeMillis2 = System.currentTimeMillis();
                    long n2 = currentTimeMillis2 - currentTimeMillis;
                    if (n2 > 2000L) {
                        MinecraftServer.logger.warning("Can't keep up! Did the system time change, or is the server overloaded?");
                        n2 = 2000L;
                    }
                    if (n2 < 0L) {
                        MinecraftServer.logger.warning("Time ran backwards! Did the system time change?");
                        n2 = 0L;
                    }
                    n += n2;
                    currentTimeMillis = currentTimeMillis2;
                    if (this.levels[0].allPlayersAreSleeping()) {
                        this.tick();
                        n = 0L;
                    }
                    else {
                        while (n > 50L) {
                            n -= 50L;
                            this.tick();
                        }
                    }
                    Thread.sleep(1L);
                }
            }
            else {
                while (this.running) {
                    this.handleConsoleInputs();
                    try {
                        Thread.sleep(10L);
                    }
                    catch (final InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        catch (final Throwable thrown) {
            thrown.printStackTrace();
            MinecraftServer.logger.log(Level.SEVERE, "Unexpected exception", thrown);
            while (this.running) {
                this.handleConsoleInputs();
                try {
                    Thread.sleep(10L);
                }
                catch (final InterruptedException ex2) {
                    ex2.printStackTrace();
                }
            }
            try {
                this.stopServer();
                this.stopped = true;
            }
            catch (final Throwable t) {
                t.printStackTrace();
            }
            finally {
                System.exit(0);
            }
        }
        finally {
            try {
                this.stopServer();
                this.stopped = true;
            }
            catch (final Throwable t2) {
                t2.printStackTrace();
                System.exit(0);
            }
            finally {
                System.exit(0);
            }
        }
    }
    
    private void tick() {
        final ArrayList list = new ArrayList();
        for (final String s : MinecraftServer.ironTimers.keySet()) {
            final int intValue = MinecraftServer.ironTimers.get(s);
            if (intValue > 0) {
                MinecraftServer.ironTimers.put(s, intValue - 1);
            }
            else {
                list.add(s);
            }
        }
        for (int i = 0; i < list.size(); ++i) {
            MinecraftServer.ironTimers.remove(list.get(i));
        }
        AABB.resetPool();
        Vec3.resetPool();
        ++this.tickCount;
        for (int j = 0; j < this.levels.length; ++j) {
            if (j == 0 || this.settings.getBoolean("allow-nether", true)) {
                final ServerLevel serverLevel = this.levels[j];
                if (this.tickCount % 20 == 0) {
                    this.players.broadcastAll(new SetTimePacket(serverLevel.getTime()), serverLevel.dimension.id);
                }
                serverLevel.tick();
                while (serverLevel.updateLights()) {}
                serverLevel.tickEntities();
            }
        }
        this.connection.tick();
        this.players.tick();
        for (int k = 0; k < this.trackers.length; ++k) {
            this.trackers[k].tick();
        }
        for (int l = 0; l < this.tickables.size(); ++l) {
            ((Tickable)this.tickables.get(l)).tick();
        }
        try {
            this.handleConsoleInputs();
        }
        catch (final Exception thrown) {
            MinecraftServer.logger.log(Level.WARNING, "Unexpected exception while parsing console command", thrown);
        }
    }
    
    public void handleConsoleInput(final String msg, final ConsoleInputSource source) {
        this.consoleInput.add(new ConsoleInput(msg, source));
    }
    
    public void handleConsoleInputs() {
        while (this.consoleInput.size() > 0) {
            this.commands.handleCommand(this.consoleInput.remove(0));
        }
    }
    
    public void addTickable(final Tickable tickable) {
        this.tickables.add(tickable);
    }
    
    public static void main(final String[] args) {
        Stats.init();
        try {
            final MinecraftServer minecraftServer = new MinecraftServer();
            if (!GraphicsEnvironment.isHeadless()) {
                if (args.length <= 0 || !args[0].equals("nogui")) {
                    MinecraftServerGui.showFrameFor(minecraftServer);
                }
            }
            new Thread(minecraftServer).start();
        }
        catch (final Exception thrown) {
            MinecraftServer.logger.log(Level.SEVERE, "Failed to start the minecraft server", thrown);
        }
    }
    
    public File getFile(final String name) {
        return new File(name);
    }
    
    public void info(final String string) {
        MinecraftServer.logger.info(string);
    }
    
    public void warn(final String string) {
        MinecraftServer.logger.warning(string);
    }
    
    public String getConsoleName() {
        return "CONSOLE";
    }
    
    public ServerLevel getLevel(final int dimension) {
        if (dimension == -1) {
            return this.levels[1];
        }
        return this.levels[0];
    }
    
    public EntityTracker getTracker(final int dimension) {
        if (dimension == -1) {
            return this.trackers[1];
        }
        return this.trackers[0];
    }
    
    static {
        MinecraftServer.logger = Logger.getLogger("Minecraft");
        MinecraftServer.ironTimers = new HashMap();
    }
}
