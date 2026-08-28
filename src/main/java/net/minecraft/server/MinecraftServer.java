// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server;

import net.minecraft.SharedConstants;
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
    public static final String VERSION = SharedConstants.VERSION_STRING;
    public static Logger logger = Logger.getLogger("Minecraft");
    public static HashMap<String, Integer> ironTimers = new HashMap<>();
    private static final int DEFAULT_MINECRAFT_PORT = 25565;
    private static final int MS_PER_TICK = 1000 / SharedConstants.TICKS_PER_SECOND;
    public ServerConnection connection;
    public Settings settings;
    public ServerLevel[] levels;
    public PlayerList players;
    private ConsoleCommands commands;
    private boolean running = true;
    public boolean stopped = false;
    int tickCount = 0;
    public String progressStatus;
    public int progress;
    private List<Tickable> tickables = new ArrayList<>();
    private List<ConsoleInput> consoleInput = Collections.synchronizedList(new ArrayList<>());
    public EntityTracker[] trackers = new EntityTracker[2];
    public boolean onlineMode;
    public boolean isAnimals;
    public boolean pvp;
    public boolean isFlightAllowed;
    
    public MinecraftServer() {
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
                    catch (final InterruptedException e) {}
                }
            }
        };
    }
    
    private boolean initServer() throws UnknownHostException {
        this.commands = new ConsoleCommands(this);

        final Thread t = new Thread(() -> {
            final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try {
                String line;
                while (!this.stopped && this.running && (line = br.readLine()) != null) {
                    handleConsoleInput(line, MinecraftServer.this);
                }
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();

        LogConfigurator.initLogger();
        MinecraftServer.logger.info("Starting minecraft server version " + VERSION);

        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
            MinecraftServer.logger.warning("**** NOT ENOUGH RAM!");
            MinecraftServer.logger.warning("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }

        MinecraftServer.logger.info("Loading properties");
        this.settings = new Settings(new File("server.properties"));

        final String localIp = this.settings.getString("server-ip", "");
        this.onlineMode = this.settings.getBoolean("online-mode", true);
        this.isAnimals = this.settings.getBoolean("spawn-animals", true);
        this.pvp = this.settings.getBoolean("pvp", true);
        this.isFlightAllowed = this.settings.getBoolean("allow-flight", false);

        InetAddress localAddress = null;
        if (localIp.length() > 0) localAddress = InetAddress.getByName(localIp);
        final int port = this.settings.getInt("server-port", DEFAULT_MINECRAFT_PORT);

        MinecraftServer.logger.info("Starting Minecraft server on " + (localIp.isEmpty() ? "*" : localIp) + ":" + port);
        try {
            this.connection = new ServerConnection(this, localAddress, port);
        }
        catch (final IOException ex) {
            MinecraftServer.logger.warning("**** FAILED TO BIND TO PORT!");
            MinecraftServer.logger.log(Level.WARNING, "The exception was: " + ex);
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
        final long levelNanoTime = System.nanoTime();
        final String levelName = this.settings.getString("level-name", "world");
        final String levelSeed = this.settings.getString("level-seed", "");
        long seed = new Random().nextLong();
        if (!levelSeed.isEmpty()) {
            try {
                seed = Long.parseLong(levelSeed);
            }
            catch (final NumberFormatException e) {
                seed = levelSeed.hashCode();
            }
        }

        MinecraftServer.logger.info("Preparing level \"" + levelName + "\"");
        this.loadLevel(new McRegionLevelStorageSource(new File(".")), levelName, seed);
        MinecraftServer.logger.info("Done (" + (System.nanoTime() - levelNanoTime) + "ns)! For help, type \"help\" or \"?\"");
        return true;
    }
    
    private void loadLevel(final LevelStorageSource storageSource, final String name, final long seed) {
        if (storageSource.requiresConversion(name)) {
            MinecraftServer.logger.info("Converting map!");
            storageSource.convertLevel(name, new ProgressListener() {
                private long lastCheckTime = System.currentTimeMillis();

                public void progressStartNoAbort(final String string) {}
                @Override
                public void progressStart(String var1) {}

                public void progressStagePercentage(final int i) {
                    if (System.currentTimeMillis() - this.lastCheckTime >= 1000L) {
                        this.lastCheckTime = System.currentTimeMillis();
                        logger.info("Converting... " + i + "%");
                    }
                }
                public void progressStage(final String status) {}
            });
        }
        this.levels = new ServerLevel[2];

        File pSave = new File(".");
        final McRegionLevelStorage mcRegionLevelStorage = new McRegionLevelStorage(pSave, name, true);
        for (int i = 0; i < this.levels.length; ++i) {
            if (i == 0) this.levels[i] = new ServerLevel(this, mcRegionLevelStorage, name, (i == 0) ? 0 : -1, seed);
            else this.levels[i] = new DerivedServerLevel(this, mcRegionLevelStorage, name, (i == 0) ? 0 : -1, seed, this.levels[0]);

            this.levels[i].addListener(new ServerLevelListener(this, this.levels[i]));
            this.levels[i].difficulty = (this.settings.getBoolean("spawn-monsters", true) ? 1 : 0);
            this.levels[i].setSpawnSettings(this.settings.getBoolean("spawn-monsters", true), this.isAnimals);
            this.players.setLevel(this.levels);
        }

        final int r = 196;
        long lastTime = System.currentTimeMillis();
        for (int i = 0; i < this.levels.length; ++i) {
            MinecraftServer.logger.info("Preparing start region for level " + i);
            if (i == 0 || this.settings.getBoolean("allow-nether", true)) {
                final ServerLevel level = this.levels[i];
                final Pos spawnPos = level.getSharedSpawnPos();

                int twoRPlusOne = r * 2 + 1;
                int total = twoRPlusOne * twoRPlusOne;
                for (int x = -r; x <= r && this.running; x += 16) {
                    for (int z = -r; z <= r && this.running; z += 16) {
                        final long now = System.currentTimeMillis();
                        if (now < lastTime) lastTime = now;
                        if (now > lastTime + 1000L) {
                            int pos = (x + r) * twoRPlusOne + (z + 1);
                            this.setProgress("Preparing spawn area", pos * 100 / total);
                            lastTime = now;
                        }
                        level.cache.create(spawnPos.x + x >> 4, spawnPos.z + z >> 4);
                        while (level.updateLights() && this.running) {}
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
            final ServerLevel level = this.levels[i];
            level.save(true, null);
            level.closeLevelStorage();
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
                long lastTime = System.currentTimeMillis();
                long unprocessedTime = 0L;
                while (this.running) {
                    final long now = System.currentTimeMillis();
                    long passedTime = now - lastTime;
                    if (passedTime > MS_PER_TICK * 40) {
                        MinecraftServer.logger.warning("Can't keep up! Did the system time change, or is the server overloaded?");
                        passedTime = MS_PER_TICK * 40;
                    }
                    if (passedTime < 0L) {
                        MinecraftServer.logger.warning("Time ran backwards! Did the system time change?");
                        passedTime = 0L;
                    }
                    unprocessedTime += passedTime;
                    lastTime = now;

                    if (this.levels[0].allPlayersAreSleeping()) {
                        this.tick();
                        unprocessedTime = 0L;
                    }
                    else {
                        while (unprocessedTime > MS_PER_TICK) {
                            unprocessedTime -= MS_PER_TICK;
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
                    catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (final Throwable e) {
            e.printStackTrace();
            MinecraftServer.logger.log(Level.SEVERE, "Unexpected exception", e);
            while (this.running) {
                this.handleConsoleInputs();
                try {
                    Thread.sleep(10L);
                }
                catch (final InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        finally {
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
    }
    
    private void tick() {
        final ArrayList<String> toRemove = new ArrayList<>();
        for (final String s : MinecraftServer.ironTimers.keySet()) {
            final int t = MinecraftServer.ironTimers.get(s);
            if (t > 0) {
                MinecraftServer.ironTimers.put(s, t - 1);
            }
            else {
                toRemove.add(s);
            }
        }
        for (int i = 0; i < toRemove.size(); ++i) {
            MinecraftServer.ironTimers.remove(toRemove.get(i));
        }

        AABB.resetPool();
        Vec3.resetPool();

        this.tickCount++;

        for (int i = 0; i < this.levels.length; ++i) {
            if (i == 0 || this.settings.getBoolean("allow-nether", true)) {
                final ServerLevel level = this.levels[i];

                if (this.tickCount % 20 == 0) {
                    this.players.broadcastAll(new SetTimePacket(level.getTime()), level.dimension.id);
                }

                level.tick();
                while (level.updateLights()) {}
                level.tickEntities();
            }
        }
        this.connection.tick();
        this.players.tick();
        for (int i = 0; i < this.trackers.length; ++i) {
            this.trackers[i].tick();
        }
        for (int i = 0; i < this.tickables.size(); ++i) {
            this.tickables.get(i).tick();
        }
        try {
            this.handleConsoleInputs();
        }
        catch (final Exception e) {
            MinecraftServer.logger.log(Level.WARNING, "Unexpected exception while parsing console command", e);
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
            final MinecraftServer server = new MinecraftServer();
            if (!GraphicsEnvironment.isHeadless()) {
                if (args.length == 0 || !args[0].equals("nogui")) {
                    MinecraftServerGui.showFrameFor(server);
                }
            }
            new Thread(server).start();
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
        if (dimension == -1) return this.levels[1];
        return this.levels[0];
    }
    
    public EntityTracker getTracker(final int dimension) {
        if (dimension == -1) return this.trackers[1];
        return this.trackers[0];
    }
}
