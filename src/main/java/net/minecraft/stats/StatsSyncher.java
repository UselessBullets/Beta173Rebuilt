// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.stats;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import net.minecraft.client.User;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.util.Map;

// Useless - Theres very little on the local variable names and method structures of this class, alot of it will be guesses
public class StatsSyncher
{
    private static final int SAVE_INTERVAL = 20 * 5;
    private static final int SEND_INTERVAL = 20 * 60;
    private volatile boolean busy = false;
    private volatile Map<Stat, Integer> serverStats = null;
    private volatile Map<Stat, Integer> failedSentStats = null;

    private final StatsCounter statsCounter;
    private final File unsentFile, lastServerFile;
    private final File unsentFileTmp, lastServerFileTmp;
    private final File unsentFileOld, lastServerFileOld;
    private final User user;

    private int noSaveIn = 0, noSendIn = 0;
    
    public StatsSyncher(final User user, final StatsCounter statsCounter, final File dir) {
        this.unsentFile = new File(dir, "stats_" + user.name.toLowerCase() + "_unsent.dat");
        this.lastServerFile = new File(dir, "stats_" + user.name.toLowerCase() + ".dat");
        this.unsentFileOld = new File(dir, "stats_" + user.name.toLowerCase() + "_unsent.old");
        this.lastServerFileOld = new File(dir, "stats_" + user.name.toLowerCase() + ".old");
        this.unsentFileTmp = new File(dir, "stats_" + user.name.toLowerCase() + "_unsent.tmp");
        this.lastServerFileTmp = new File(dir, "stats_" + user.name.toLowerCase() + ".tmp");

        // Useless - appears to be a converstion step from a time where user names in stats files were case sensitive
        if (!user.name.toLowerCase().equals(user.name)) {
            this.attemptRename(dir, "stats_" + user.name + "_unsent.dat", this.unsentFile);
            this.attemptRename(dir, "stats_" + user.name + ".dat", this.lastServerFile);
            this.attemptRename(dir, "stats_" + user.name + "_unsent.old", this.unsentFileOld);
            this.attemptRename(dir, "stats_" + user.name + ".old", this.lastServerFileOld);
            this.attemptRename(dir, "stats_" + user.name + "_unsent.tmp", this.unsentFileTmp);
            this.attemptRename(dir, "stats_" + user.name + ".tmp", this.lastServerFileTmp);
        }

        this.statsCounter = statsCounter;
        this.user = user;

        if (this.unsentFile.exists()) statsCounter.loadStats(this.loadStatsFromDisk(this.unsentFile, this.unsentFileTmp, this.unsentFileOld));
        this.getStatsFromServer();
    }
    
    private void attemptRename(final File dir, final String name, final File to) {
        final File from = new File(dir, name);
        if (from.exists() && !from.isDirectory() && !to.exists()) {
            from.renameTo(to);
        }
    }
    
    private Map<Stat, Integer> loadStatsFromDisk(final File file, final File tmp, final File old) {
        if (file.exists()) return this.loadStatsFromDisk(file);
        if (old.exists()) return this.loadStatsFromDisk(old);
        if (tmp.exists()) return this.loadStatsFromDisk(tmp);
        return null;
    }
    
    private Map<Stat, Integer> loadStatsFromDisk(final File file) {
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            // Useless - Read all lines from file
            final StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return StatsCounter.loadStatsFromString(sb.toString());
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void doSend(Map<Stat, Integer> stats) {
        // Useless - Known to exist from LCE StatsSyncher header file, but method contents are unknown
    }
    
    private void doSave(final Map<Stat, Integer> stats, final File file, final File tmp, final File old) throws IOException {
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(tmp, false))) {
            printWriter.print(StatsCounter.saveStatsToString(this.user.name, "local", stats));
        }

        if (old.exists()) old.delete();
        if (file.exists()) file.renameTo(old);
        tmp.renameTo(file);
    }

    protected Map<Stat, Integer> doGetStats() {
        // Useless - Known to exist from LCE StatsSyncher header file, but method contents are unknown
        throw new NotImplementedException();
    }
    
    public void getStatsFromServer() {
        if (this.busy) throw new IllegalStateException("Can't get stats from server while StatsSyncher is busy!");

        this.noSaveIn = SAVE_INTERVAL;
        this.busy = true;
        new Thread(() -> {
            try {
                if (this.serverStats != null) {
                    doSave(this.serverStats, this.lastServerFile, this.lastServerFileTmp, this.lastServerFileOld);
                }
                else if (this.lastServerFile.exists()) {
                    this.serverStats = loadStatsFromDisk(this.lastServerFile, this.lastServerFileTmp, this.lastServerFileOld);
                }
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
            finally {
                this.busy = false;
            }
        }).start();
    }
    
    public void saveUnsent(final Map<Stat, Integer> stats) {
        if (this.busy) throw new IllegalStateException("Can't save stats while StatsSyncher is busy!");

        this.noSaveIn = SAVE_INTERVAL;
        this.busy = true;
        new Thread(() -> {
            try {
                doSave(stats, this.unsentFile, this.unsentFileTmp, this.unsentFileOld);
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
            finally {
                this.busy = false;
            }
        }).start();
    }

    public void sendUnsent(final Map<Stat, Integer> stats, final Map<Stat, Integer> fullStats) {
        // Useless - Known to exist from LCE StatsSyncher header file, but method contents are unknown
    }

    public void forceSendUnsent(final Map<Stat, Integer> stats) {
        // Useless - Known to exist from LCE StatsSyncher header file, but method contents are unknown
    }
    
    public void forceSaveUnsent(final Map<Stat, Integer> stats) {
        int decaseconds = 30;
        while (this.busy && --decaseconds > 0) {
            try {
                Thread.sleep(100L);
            }
            catch (final InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        this.busy = true;
        try {
            this.doSave(stats, this.unsentFile, this.unsentFileTmp, this.unsentFileOld);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        finally {
            this.busy = false;
        }
    }
    
    public boolean maySave() {
        return this.noSaveIn <= 0 && !this.busy && this.failedSentStats == null;
    }
    
    public void tick() {
        if (this.noSaveIn > 0) --this.noSaveIn;
        if (this.noSendIn > 0) --this.noSendIn;

        if (this.failedSentStats != null) {
            this.statsCounter.queueStats(this.failedSentStats);
            this.failedSentStats = null;
        }

        if (this.serverStats != null) {
            this.statsCounter.mergeStats(this.serverStats);
            this.serverStats = null;
        }
    }
}
