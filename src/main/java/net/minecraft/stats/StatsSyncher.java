// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.stats;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import net.minecraft.client.User;
import java.io.File;
import java.util.Map;

public class StatsSyncher
{
    private volatile boolean busy;
    private volatile Map serverStats;
    private volatile Map failedSentStats;
    private StatsCounter statsCounter;
    private File unsentFile;
    private File lastServerFile;
    private File unsentFileTmp;
    private File lastServerFileTmp;
    private File unsentFileOld;
    private File lastServerFileOld;
    private User user;
    private int noSaveIn;
    private int noSendIn;
    
    public StatsSyncher(final User user, final StatsCounter statsCounter, final File dir) {
        this.busy = false;
        this.serverStats = null;
        this.failedSentStats = null;
        this.noSaveIn = 0;
        this.noSendIn = 0;
        this.unsentFile = new File(dir, "stats_" + user.name.toLowerCase() + "_unsent.dat");
        this.lastServerFile = new File(dir, "stats_" + user.name.toLowerCase() + ".dat");
        this.unsentFileOld = new File(dir, "stats_" + user.name.toLowerCase() + "_unsent.old");
        this.lastServerFileOld = new File(dir, "stats_" + user.name.toLowerCase() + ".old");
        this.unsentFileTmp = new File(dir, "stats_" + user.name.toLowerCase() + "_unsent.tmp");
        this.lastServerFileTmp = new File(dir, "stats_" + user.name.toLowerCase() + ".tmp");
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
        if (this.unsentFile.exists()) {
            statsCounter.loadStats(this.loadStatsFromDisk(this.unsentFile, this.unsentFileTmp, this.unsentFileOld));
        }
        this.getStatsFromServer();
    }
    
    private void attemptRename(final File dir, final String name, final File to) {
        final File file = new File(dir, name);
        if (file.exists() && !file.isDirectory() && !to.exists()) {
            file.renameTo(to);
        }
    }
    
    private Map loadStatsFromDisk(final File file, final File tmp, final File old) {
        if (file.exists()) {
            return this.loadStatsFromDisk(file);
        }
        if (old.exists()) {
            return this.loadStatsFromDisk(old);
        }
        if (tmp.exists()) {
            return this.loadStatsFromDisk(tmp);
        }
        return null;
    }
    
    private Map loadStatsFromDisk(final File file) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            final StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return StatsCounter.loadStatsFromString(sb.toString());
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                }
                catch (final Exception ex2) {
                    ex2.printStackTrace();
                }
            }
        }
        finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                }
                catch (final Exception ex3) {
                    ex3.printStackTrace();
                }
            }
        }
        return null;
    }
    
    private void doSave(final Map stats, final File file, final File tmp, final File old) {
        final PrintWriter printWriter = new PrintWriter(new FileWriter(tmp, false));
        try {
            printWriter.print(StatsCounter.saveStatsToString(this.user.name, "local", stats));
        }
        finally {
            printWriter.close();
        }
        if (old.exists()) {
            old.delete();
        }
        if (file.exists()) {
            file.renameTo(old);
        }
        tmp.renameTo(file);
    }
    
    public void getStatsFromServer() {
        if (this.busy) {
            throw new IllegalStateException("Can't get stats from server while StatsSyncher is busy!");
        }
        this.noSaveIn = 100;
        this.busy = true;
        new StatsSyncher_GetStatsFromServerThread(this).start();
    }
    
    public void saveUnsent(final Map stats) {
        if (this.busy) {
            throw new IllegalStateException("Can't save stats while StatsSyncher is busy!");
        }
        this.noSaveIn = 100;
        this.busy = true;
        new StatsSyncher_SaveUnsentThread(this, stats).start();
    }
    
    public void forceSaveUnsent(final Map stats) {
        int n = 30;
        while (this.busy && --n > 0) {
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
        catch (final Exception ex2) {
            ex2.printStackTrace();
        }
        finally {
            this.busy = false;
        }
    }
    
    public boolean maySave() {
        return this.noSaveIn <= 0 && !this.busy && this.failedSentStats == null;
    }
    
    public void tick() {
        if (this.noSaveIn > 0) {
            --this.noSaveIn;
        }
        if (this.noSendIn > 0) {
            --this.noSendIn;
        }
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
