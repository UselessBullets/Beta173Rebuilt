// 
// Decompiled by Procyon v0.6.0
// 

package paulscode.sound;

import java.util.Iterator;
import java.util.HashMap;
import javax.sound.sampled.AudioFormat;
import java.net.URL;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.List;

public class SoundSystem
{
    private static final boolean GET = false;
    private static final boolean SET = true;
    private static final boolean XXX = false;
    protected SoundSystemLogger logger;
    protected Library soundLibrary;
    protected List commandQueue;
    private List sourcePlayList;
    protected CommandThread commandThread;
    public Random randomNumberGenerator;
    protected String className;
    private static Class currentLibrary;
    private static boolean initialized;
    private static SoundSystemException lastException;
    
    public SoundSystem() {
        this.className = "SoundSystem";
        this.logger = SoundSystemConfig.getLogger();
        if (this.logger == null) {
            SoundSystemConfig.setLogger(this.logger = new SoundSystemLogger());
        }
        this.linkDefaultLibrariesAndCodecs();
        final LinkedList libraries = SoundSystemConfig.getLibraries();
        if (libraries != null) {
            final ListIterator listIterator = libraries.listIterator();
            while (listIterator.hasNext()) {
                final Class libraryClass = (Class)listIterator.next();
                try {
                    this.init(libraryClass);
                    return;
                }
                catch (final SoundSystemException exception) {
                    this.logger.printExceptionMessage(exception, 1);
                    continue;
                }
                break;
            }
        }
        try {
            this.init(Library.class);
        }
        catch (final SoundSystemException exception2) {
            this.logger.printExceptionMessage(exception2, 1);
        }
    }
    
    public SoundSystem(final Class libraryClass) {
        this.className = "SoundSystem";
        this.logger = SoundSystemConfig.getLogger();
        if (this.logger == null) {
            SoundSystemConfig.setLogger(this.logger = new SoundSystemLogger());
        }
        this.linkDefaultLibrariesAndCodecs();
        this.init(libraryClass);
    }
    
    protected void linkDefaultLibrariesAndCodecs() {
    }
    
    protected void init(final Class libraryClass) {
        this.message("", 0);
        this.message("Starting up " + this.className + "...", 0);
        this.randomNumberGenerator = new Random();
        this.commandQueue = new LinkedList();
        this.sourcePlayList = new LinkedList();
        (this.commandThread = new CommandThread(this)).start();
        snooze(200L);
        this.newLibrary(libraryClass);
        this.message("", 0);
    }
    
    public void cleanup() {
        boolean b = false;
        this.message("", 0);
        this.message(this.className + " shutting down...", 0);
        try {
            this.commandThread.kill();
            this.commandThread.interrupt();
        }
        catch (final Exception ex) {
            b = true;
        }
        if (!b) {
            for (int i = 0; i < 50; ++i) {
                if (!this.commandThread.alive()) {
                    break;
                }
                snooze(100L);
            }
        }
        if (b || this.commandThread.alive()) {
            this.errorMessage("Command thread did not die!", 0);
            this.message("Ignoring errors... continuing clean-up.", 0);
        }
        initialized(true, false);
        currentLibrary(true, null);
        try {
            if (this.soundLibrary != null) {
                this.soundLibrary.cleanup();
            }
        }
        catch (final Exception ex2) {
            this.errorMessage("Problem during Library.cleanup()!", 0);
            this.message("Ignoring errors... continuing clean-up.", 0);
        }
        try {
            if (this.commandQueue != null) {
                this.commandQueue.clear();
            }
        }
        catch (final Exception ex3) {
            this.errorMessage("Unable to clear the command queue!", 0);
            this.message("Ignoring errors... continuing clean-up.", 0);
        }
        try {
            if (this.sourcePlayList != null) {
                this.sourcePlayList.clear();
            }
        }
        catch (final Exception ex4) {
            this.errorMessage("Unable to clear the source management list!", 0);
            this.message("Ignoring errors... continuing clean-up.", 0);
        }
        this.randomNumberGenerator = null;
        this.soundLibrary = null;
        this.commandQueue = null;
        this.sourcePlayList = null;
        this.commandThread = null;
        this.importantMessage("Author: Paul Lamb, www.paulscode.com", 1);
        this.message("", 0);
    }
    
    public void interruptCommandThread() {
        if (this.commandThread == null) {
            this.errorMessage("Command Thread null in method 'interruptCommandThread'", 0);
            return;
        }
        this.commandThread.interrupt();
    }
    
    public void loadSound(final String filename) {
        this.CommandQueue(new CommandObject(2, new FilenameURL(filename)));
        this.commandThread.interrupt();
    }
    
    public void loadSound(final URL url, final String identifier) {
        this.CommandQueue(new CommandObject(2, new FilenameURL(url, identifier)));
        this.commandThread.interrupt();
    }
    
    public void unloadSound(final String filename) {
        this.CommandQueue(new CommandObject(4, filename));
        this.commandThread.interrupt();
    }
    
    public void queueSound(final String sourcename, final String filename) {
        this.CommandQueue(new CommandObject(5, sourcename, new FilenameURL(filename)));
        this.commandThread.interrupt();
    }
    
    public void queueSound(final String sourcename, final URL url, final String identifier) {
        this.CommandQueue(new CommandObject(5, sourcename, new FilenameURL(url, identifier)));
        this.commandThread.interrupt();
    }
    
    public void dequeueSound(final String sourcename, final String filename) {
        this.CommandQueue(new CommandObject(6, sourcename, filename));
        this.commandThread.interrupt();
    }
    
    public void fadeOut(final String sourcename, final String filename, final long milis) {
        Object object = null;
        if (filename != null) {
            object = new FilenameURL(filename);
        }
        this.CommandQueue(new CommandObject(7, sourcename, object, milis));
        this.commandThread.interrupt();
    }
    
    public void fadeOut(final String sourcename, final URL url, final String identifier, final long milis) {
        Object object = null;
        if (url != null && identifier != null) {
            object = new FilenameURL(url, identifier);
        }
        this.CommandQueue(new CommandObject(7, sourcename, object, milis));
        this.commandThread.interrupt();
    }
    
    public void fadeOutIn(final String sourcename, final String filename, final long milisOut, final long milisIn) {
        this.CommandQueue(new CommandObject(8, sourcename, new FilenameURL(filename), milisOut, milisIn));
        this.commandThread.interrupt();
    }
    
    public void fadeOutIn(final String sourcename, final URL url, final String identifier, final long milisOut, final long milisIn) {
        this.CommandQueue(new CommandObject(8, sourcename, new FilenameURL(url, identifier), milisOut, milisIn));
        this.commandThread.interrupt();
    }
    
    public void checkFadeVolumes() {
        this.CommandQueue(new CommandObject(9));
        this.commandThread.interrupt();
    }
    
    public void backgroundMusic(final String sourcename, final String filename, final boolean toLoop) {
        this.CommandQueue(new CommandObject(12, true, true, toLoop, sourcename, new FilenameURL(filename), 0.0f, 0.0f, 0.0f, 0, 0.0f, false));
        this.CommandQueue(new CommandObject(21, sourcename));
        this.commandThread.interrupt();
    }
    
    public void backgroundMusic(final String sourcename, final URL url, final String identifer, final boolean toLoop) {
        this.CommandQueue(new CommandObject(12, true, true, toLoop, sourcename, new FilenameURL(url, identifer), 0.0f, 0.0f, 0.0f, 0, 0.0f, false));
        this.CommandQueue(new CommandObject(21, sourcename));
        this.commandThread.interrupt();
    }
    
    public void newSource(final boolean priority, final String sourcename, final String filename, final boolean toLoop, final float x, final float y, final float z, final int attmodel, final float distOrRoll) {
        this.CommandQueue(new CommandObject(10, priority, false, toLoop, sourcename, new FilenameURL(filename), x, y, z, attmodel, distOrRoll));
        this.commandThread.interrupt();
    }
    
    public void newSource(final boolean priority, final String sourcename, final URL url, final String identifier, final boolean toLoop, final float x, final float y, final float z, final int attmodel, final float distOrRoll) {
        this.CommandQueue(new CommandObject(10, priority, false, toLoop, sourcename, new FilenameURL(url, identifier), x, y, z, attmodel, distOrRoll));
        this.commandThread.interrupt();
    }
    
    public void newStreamingSource(final boolean priority, final String sourcename, final String identifier, final boolean toLoop, final float x, final float y, final float z, final int attmodel, final float distOrRoll) {
        this.CommandQueue(new CommandObject(10, priority, true, toLoop, sourcename, new FilenameURL(identifier), x, y, z, attmodel, distOrRoll));
        this.commandThread.interrupt();
    }
    
    public void newStreamingSource(final boolean priority, final String sourcename, final URL url, final String filename, final boolean toLoop, final float x, final float y, final float z, final int attmodel, final float distOrRoll) {
        this.CommandQueue(new CommandObject(10, priority, true, toLoop, sourcename, new FilenameURL(url, filename), x, y, z, attmodel, distOrRoll));
        this.commandThread.interrupt();
    }
    
    public void rawDataStream(final AudioFormat audioFormat, final boolean priority, final String sourcename, final float x, final float y, final float z, final int attmodel, final float distOrRoll) {
        this.CommandQueue(new CommandObject(11, audioFormat, priority, sourcename, x, y, z, attmodel, distOrRoll));
        this.commandThread.interrupt();
    }
    
    public String quickPlay(final boolean priority, final String filename, final boolean toLoop, final float x, final float y, final float z, final int attmodel, final float distOrRoll) {
        final String string = "Source_" + this.randomNumberGenerator.nextInt() + "_" + this.randomNumberGenerator.nextInt();
        this.CommandQueue(new CommandObject(12, priority, false, toLoop, string, new FilenameURL(filename), x, y, z, attmodel, distOrRoll, true));
        this.CommandQueue(new CommandObject(21, string));
        this.commandThread.interrupt();
        return string;
    }
    
    public String quickPlay(final boolean priority, final URL url, final String identifier, final boolean toLoop, final float x, final float y, final float z, final int attmodel, final float distOrRoll) {
        final String string = "Source_" + this.randomNumberGenerator.nextInt() + "_" + this.randomNumberGenerator.nextInt();
        this.CommandQueue(new CommandObject(12, priority, false, toLoop, string, new FilenameURL(url, identifier), x, y, z, attmodel, distOrRoll, true));
        this.CommandQueue(new CommandObject(21, string));
        this.commandThread.interrupt();
        return string;
    }
    
    public String quickStream(final boolean priority, final String filename, final boolean toLoop, final float x, final float y, final float z, final int attmodel, final float distOrRoll) {
        final String string = "Source_" + this.randomNumberGenerator.nextInt() + "_" + this.randomNumberGenerator.nextInt();
        this.CommandQueue(new CommandObject(12, priority, true, toLoop, string, new FilenameURL(filename), x, y, z, attmodel, distOrRoll, true));
        this.CommandQueue(new CommandObject(21, string));
        this.commandThread.interrupt();
        return string;
    }
    
    public String quickStream(final boolean priority, final URL url, final String identifier, final boolean toLoop, final float x, final float y, final float z, final int attmodel, final float distOrRoll) {
        final String string = "Source_" + this.randomNumberGenerator.nextInt() + "_" + this.randomNumberGenerator.nextInt();
        this.CommandQueue(new CommandObject(12, priority, true, toLoop, string, new FilenameURL(url, identifier), x, y, z, attmodel, distOrRoll, true));
        this.CommandQueue(new CommandObject(21, string));
        this.commandThread.interrupt();
        return string;
    }
    
    public void setPosition(final String sourcename, final float x, final float y, final float z) {
        this.CommandQueue(new CommandObject(13, sourcename, x, y, z));
        this.commandThread.interrupt();
    }
    
    public void setVolume(final String sourcename, final float value) {
        this.CommandQueue(new CommandObject(14, sourcename, value));
        this.commandThread.interrupt();
    }
    
    public float getVolume(final String sourcename) {
        synchronized (SoundSystemConfig.THREAD_SYNC) {
            if (this.soundLibrary != null) {
                return this.soundLibrary.getVolume(sourcename);
            }
            return 0.0f;
        }
    }
    
    public void setPitch(final String sourcename, final float value) {
        this.CommandQueue(new CommandObject(15, sourcename, value));
        this.commandThread.interrupt();
    }
    
    public float getPitch(final String sourcename) {
        if (this.soundLibrary != null) {
            return this.soundLibrary.getPitch(sourcename);
        }
        return 1.0f;
    }
    
    public void setPriority(final String sourcename, final boolean pri) {
        this.CommandQueue(new CommandObject(16, sourcename, pri));
        this.commandThread.interrupt();
    }
    
    public void setLooping(final String sourcename, final boolean lp) {
        this.CommandQueue(new CommandObject(17, sourcename, lp));
        this.commandThread.interrupt();
    }
    
    public void setAttenuation(final String sourcename, final int model) {
        this.CommandQueue(new CommandObject(18, sourcename, model));
        this.commandThread.interrupt();
    }
    
    public void setDistOrRoll(final String sourcename, final float dr) {
        this.CommandQueue(new CommandObject(19, sourcename, dr));
        this.commandThread.interrupt();
    }
    
    public void feedRawAudioData(final String sourcename, final byte[] buffer) {
        this.CommandQueue(new CommandObject(22, sourcename, buffer));
        this.commandThread.interrupt();
    }
    
    public void play(final String sourcename) {
        this.CommandQueue(new CommandObject(21, sourcename));
        this.commandThread.interrupt();
    }
    
    public void pause(final String sourcename) {
        this.CommandQueue(new CommandObject(23, sourcename));
        this.commandThread.interrupt();
    }
    
    public void stop(final String sourcename) {
        this.CommandQueue(new CommandObject(24, sourcename));
        this.commandThread.interrupt();
    }
    
    public void rewind(final String sourcename) {
        this.CommandQueue(new CommandObject(25, sourcename));
        this.commandThread.interrupt();
    }
    
    public void flush(final String sourcename) {
        this.CommandQueue(new CommandObject(26, sourcename));
        this.commandThread.interrupt();
    }
    
    public void cull(final String sourcename) {
        this.CommandQueue(new CommandObject(27, sourcename));
        this.commandThread.interrupt();
    }
    
    public void activate(final String sourcename) {
        this.CommandQueue(new CommandObject(28, sourcename));
        this.commandThread.interrupt();
    }
    
    public void setTemporary(final String sourcename, final boolean temporary) {
        this.CommandQueue(new CommandObject(29, sourcename, temporary));
        this.commandThread.interrupt();
    }
    
    public void removeSource(final String sourcename) {
        this.CommandQueue(new CommandObject(30, sourcename));
        this.commandThread.interrupt();
    }
    
    public void moveListener(final float x, final float y, final float z) {
        this.CommandQueue(new CommandObject(31, x, y, z));
        this.commandThread.interrupt();
    }
    
    public void setListenerPosition(final float x, final float y, final float z) {
        this.CommandQueue(new CommandObject(32, x, y, z));
        this.commandThread.interrupt();
    }
    
    public void turnListener(final float angle) {
        this.CommandQueue(new CommandObject(33, angle));
        this.commandThread.interrupt();
    }
    
    public void setListenerAngle(final float angle) {
        this.CommandQueue(new CommandObject(34, angle));
        this.commandThread.interrupt();
    }
    
    public void setListenerOrientation(final float lookX, final float lookY, final float lookZ, final float upX, final float upY, final float upZ) {
        this.CommandQueue(new CommandObject(35, lookX, lookY, lookZ, upX, upY, upZ));
        this.commandThread.interrupt();
    }
    
    public void setMasterVolume(final float value) {
        this.CommandQueue(new CommandObject(36, value));
        this.commandThread.interrupt();
    }
    
    public float getMasterVolume() {
        return SoundSystemConfig.getMasterGain();
    }
    
    public ListenerData getListenerData() {
        synchronized (SoundSystemConfig.THREAD_SYNC) {
            return this.soundLibrary.getListenerData();
        }
    }
    
    public boolean switchLibrary(final Class libraryClass) {
        synchronized (SoundSystemConfig.THREAD_SYNC) {
            initialized(true, false);
            HashMap copySources = null;
            ListenerData listenerData = null;
            boolean b = false;
            MidiChannel midiChannel = null;
            FilenameURL filenameURL = null;
            String sourcename = "";
            boolean looping = true;
            if (this.soundLibrary != null) {
                currentLibrary(true, null);
                copySources = this.copySources(this.soundLibrary.getSources());
                listenerData = this.soundLibrary.getListenerData();
                midiChannel = this.soundLibrary.getMidiChannel();
                if (midiChannel != null) {
                    b = true;
                    looping = midiChannel.getLooping();
                    sourcename = midiChannel.getSourcename();
                    filenameURL = midiChannel.getFilenameURL();
                }
                this.soundLibrary.cleanup();
                this.soundLibrary = null;
            }
            this.message("", 0);
            this.message("Switching to " + SoundSystemConfig.getLibraryTitle(libraryClass), 0);
            this.message("(" + SoundSystemConfig.getLibraryDescription(libraryClass) + ")", 1);
            try {
                this.soundLibrary = libraryClass.newInstance();
            }
            catch (final InstantiationException ex) {
                this.errorMessage("The specified library did not load properly", 1);
            }
            catch (final IllegalAccessException ex2) {
                this.errorMessage("The specified library did not load properly", 1);
            }
            catch (final ExceptionInInitializerError exceptionInInitializerError) {
                this.errorMessage("The specified library did not load properly", 1);
            }
            catch (final SecurityException ex3) {
                this.errorMessage("The specified library did not load properly", 1);
            }
            if (this.errorCheck(this.soundLibrary == null, "Library null after initialization in method 'switchLibrary'", 1)) {
                final SoundSystemException e = new SoundSystemException(this.className + " did not load properly.  " + "Library was null after initialization.", 4);
                lastException(true, e);
                initialized(true, true);
                throw e;
            }
            try {
                this.soundLibrary.init();
            }
            catch (final SoundSystemException e2) {
                lastException(true, e2);
                initialized(true, true);
                throw e2;
            }
            this.soundLibrary.setListenerData(listenerData);
            if (b) {
                if (midiChannel != null) {
                    midiChannel.cleanup();
                }
                this.soundLibrary.setMidiChannel(new MidiChannel(looping, sourcename, filenameURL));
            }
            this.soundLibrary.copySources(copySources);
            this.message("", 0);
            lastException(true, null);
            initialized(true, true);
            return true;
        }
    }
    
    public boolean newLibrary(final Class libraryClass) {
        initialized(true, false);
        this.CommandQueue(new CommandObject(37, libraryClass));
        this.commandThread.interrupt();
        for (int n = 0; !initialized(false, false) && n < 100; ++n) {
            snooze(400L);
            this.commandThread.interrupt();
        }
        if (!initialized(false, false)) {
            final SoundSystemException e = new SoundSystemException(this.className + " did not load after 30 seconds.", 4);
            lastException(true, e);
            throw e;
        }
        final SoundSystemException lastException = lastException(false, null);
        if (lastException != null) {
            throw lastException;
        }
        return true;
    }
    
    private void CommandNewLibrary(final Class libraryClass) {
        initialized(true, false);
        String str = "Initializing ";
        if (this.soundLibrary != null) {
            currentLibrary(true, null);
            str = "Switching to ";
            this.soundLibrary.cleanup();
            this.soundLibrary = null;
        }
        this.message(str + SoundSystemConfig.getLibraryTitle(libraryClass), 0);
        this.message("(" + SoundSystemConfig.getLibraryDescription(libraryClass) + ")", 1);
        try {
            this.soundLibrary = libraryClass.newInstance();
        }
        catch (final InstantiationException ex) {
            this.errorMessage("The specified library did not load properly", 1);
        }
        catch (final IllegalAccessException ex2) {
            this.errorMessage("The specified library did not load properly", 1);
        }
        catch (final ExceptionInInitializerError exceptionInInitializerError) {
            this.errorMessage("The specified library did not load properly", 1);
        }
        catch (final SecurityException ex3) {
            this.errorMessage("The specified library did not load properly", 1);
        }
        if (this.errorCheck(this.soundLibrary == null, "Library null after initialization in method 'newLibrary'", 1)) {
            lastException(true, new SoundSystemException(this.className + " did not load properly.  " + "Library was null after initialization.", 4));
            this.importantMessage("Switching to silent mode", 1);
            try {
                this.soundLibrary = new Library();
            }
            catch (final SoundSystemException ex4) {
                lastException(true, new SoundSystemException("Silent mode did not load properly.  Library was null after initialization.", 4));
                initialized(true, true);
                return;
            }
        }
        try {
            this.soundLibrary.init();
        }
        catch (final SoundSystemException e) {
            lastException(true, e);
            initialized(true, true);
            return;
        }
        lastException(true, null);
        initialized(true, true);
    }
    
    private void CommandInitialize() {
        try {
            if (this.errorCheck(this.soundLibrary == null, "Library null after initialization in method 'CommandInitialize'", 1)) {
                final SoundSystemException e = new SoundSystemException(this.className + " did not load properly.  " + "Library was null after initialization.", 4);
                lastException(true, e);
                throw e;
            }
            this.soundLibrary.init();
        }
        catch (final SoundSystemException e2) {
            lastException(true, e2);
            initialized(true, true);
        }
    }
    
    private void CommandLoadSound(final FilenameURL filenameURL) {
        if (this.soundLibrary != null) {
            this.soundLibrary.loadSound(filenameURL);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandLoadSound'", 0);
        }
    }
    
    private void CommandUnloadSound(final String filename) {
        if (this.soundLibrary != null) {
            this.soundLibrary.unloadSound(filename);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandLoadSound'", 0);
        }
    }
    
    private void CommandQueueSound(final String sourcename, final FilenameURL filenameURL) {
        if (this.soundLibrary != null) {
            this.soundLibrary.queueSound(sourcename, filenameURL);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandQueueSound'", 0);
        }
    }
    
    private void CommandDequeueSound(final String sourcename, final String filename) {
        if (this.soundLibrary != null) {
            this.soundLibrary.dequeueSound(sourcename, filename);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandDequeueSound'", 0);
        }
    }
    
    private void CommandFadeOut(final String sourcename, final FilenameURL filenameURL, final long milis) {
        if (this.soundLibrary != null) {
            this.soundLibrary.fadeOut(sourcename, filenameURL, milis);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandFadeOut'", 0);
        }
    }
    
    private void CommandFadeOutIn(final String sourcename, final FilenameURL filenameURL, final long milisOut, final long milisIn) {
        if (this.soundLibrary != null) {
            this.soundLibrary.fadeOutIn(sourcename, filenameURL, milisOut, milisIn);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandFadeOutIn'", 0);
        }
    }
    
    private void CommandCheckFadeVolumes() {
        if (this.soundLibrary != null) {
            this.soundLibrary.checkFadeVolumes();
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandCheckFadeVolumes'", 0);
        }
    }
    
    private void CommandNewSource(final boolean priority, final boolean toStreem, final boolean toLoop, final String sourcename, final FilenameURL filenameURL, final float x, final float y, final float z, final int attmodel, final float distOrRoll) {
        if (this.soundLibrary != null) {
            if (filenameURL.getFilename().matches(".*[mM][iI][dD][iI]?$") && !SoundSystemConfig.midiCodec()) {
                this.soundLibrary.loadMidi(toLoop, sourcename, filenameURL);
            }
            else {
                this.soundLibrary.newSource(priority, toStreem, toLoop, sourcename, filenameURL, x, y, z, attmodel, distOrRoll);
            }
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandNewSource'", 0);
        }
    }
    
    private void CommandRawDataStream(final AudioFormat audioFormat, final boolean priority, final String sourcename, final float x, final float y, final float z, final int attmodel, final float distOrRoll) {
        if (this.soundLibrary != null) {
            this.soundLibrary.rawDataStream(audioFormat, priority, sourcename, x, y, z, attmodel, distOrRoll);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandRawDataStream'", 0);
        }
    }
    
    private void CommandQuickPlay(final boolean priority, final boolean toStream, final boolean toLoop, final String sourcename, final FilenameURL filenameURL, final float x, final float y, final float z, final int attmodel, final float distOrRoll, final boolean temporary) {
        if (this.soundLibrary != null) {
            if (filenameURL.getFilename().matches(".*[mM][iI][dD][iI]?$") && !SoundSystemConfig.midiCodec()) {
                this.soundLibrary.loadMidi(toLoop, sourcename, filenameURL);
            }
            else {
                this.soundLibrary.quickPlay(priority, toStream, toLoop, sourcename, filenameURL, x, y, z, attmodel, distOrRoll, temporary);
            }
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandQuickPlay'", 0);
        }
    }
    
    private void CommandSetPosition(final String sourcename, final float x, final float y, final float z) {
        if (this.soundLibrary != null) {
            this.soundLibrary.setPosition(sourcename, x, y, z);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandMoveSource'", 0);
        }
    }
    
    private void CommandSetVolume(final String sourcename, final float value) {
        if (this.soundLibrary != null) {
            this.soundLibrary.setVolume(sourcename, value);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandSetVolume'", 0);
        }
    }
    
    private void CommandSetPitch(final String sourcename, final float value) {
        if (this.soundLibrary != null) {
            this.soundLibrary.setPitch(sourcename, value);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandSetPitch'", 0);
        }
    }
    
    private void CommandSetPriority(final String sourcename, final boolean pri) {
        if (this.soundLibrary != null) {
            this.soundLibrary.setPriority(sourcename, pri);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandSetPriority'", 0);
        }
    }
    
    private void CommandSetLooping(final String sourcename, final boolean lp) {
        if (this.soundLibrary != null) {
            this.soundLibrary.setLooping(sourcename, lp);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandSetLooping'", 0);
        }
    }
    
    private void CommandSetAttenuation(final String sourcename, final int model) {
        if (this.soundLibrary != null) {
            this.soundLibrary.setAttenuation(sourcename, model);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandSetAttenuation'", 0);
        }
    }
    
    private void CommandSetDistOrRoll(final String sourcename, final float dr) {
        if (this.soundLibrary != null) {
            this.soundLibrary.setDistOrRoll(sourcename, dr);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandSetDistOrRoll'", 0);
        }
    }
    
    private void CommandPlay(final String sourcename) {
        if (this.soundLibrary != null) {
            this.soundLibrary.play(sourcename);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandPlay'", 0);
        }
    }
    
    private void CommandFeedRawAudioData(final String sourcename, final byte[] buffer) {
        if (this.soundLibrary != null) {
            this.soundLibrary.feedRawAudioData(sourcename, buffer);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandFeedRawAudioData'", 0);
        }
    }
    
    private void CommandPause(final String sourcename) {
        if (this.soundLibrary != null) {
            this.soundLibrary.pause(sourcename);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandPause'", 0);
        }
    }
    
    private void CommandStop(final String sourcename) {
        if (this.soundLibrary != null) {
            this.soundLibrary.stop(sourcename);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandStop'", 0);
        }
    }
    
    private void CommandRewind(final String sourcename) {
        if (this.soundLibrary != null) {
            this.soundLibrary.rewind(sourcename);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandRewind'", 0);
        }
    }
    
    private void CommandFlush(final String sourcename) {
        if (this.soundLibrary != null) {
            this.soundLibrary.flush(sourcename);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandFlush'", 0);
        }
    }
    
    private void CommandSetTemporary(final String sourcename, final boolean temporary) {
        if (this.soundLibrary != null) {
            this.soundLibrary.setTemporary(sourcename, temporary);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandSetActive'", 0);
        }
    }
    
    private void CommandRemoveSource(final String sourcename) {
        if (this.soundLibrary != null) {
            this.soundLibrary.removeSource(sourcename);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandRemoveSource'", 0);
        }
    }
    
    private void CommandMoveListener(final float x, final float y, final float z) {
        if (this.soundLibrary != null) {
            this.soundLibrary.moveListener(x, y, z);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandMoveListener'", 0);
        }
    }
    
    private void CommandSetListenerPosition(final float x, final float y, final float z) {
        if (this.soundLibrary != null) {
            this.soundLibrary.setListenerPosition(x, y, z);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandSetListenerPosition'", 0);
        }
    }
    
    private void CommandTurnListener(final float angle) {
        if (this.soundLibrary != null) {
            this.soundLibrary.turnListener(angle);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandTurnListener'", 0);
        }
    }
    
    private void CommandSetListenerAngle(final float angle) {
        if (this.soundLibrary != null) {
            this.soundLibrary.setListenerAngle(angle);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandSetListenerAngle'", 0);
        }
    }
    
    private void CommandSetListenerOrientation(final float lookX, final float lookY, final float lookZ, final float upX, final float upY, final float upZ) {
        if (this.soundLibrary != null) {
            this.soundLibrary.setListenerOrientation(lookX, lookY, lookZ, upX, upY, upZ);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandSetListenerOrientation'", 0);
        }
    }
    
    private void CommandCull(final String sourcename) {
        if (this.soundLibrary != null) {
            this.soundLibrary.cull(sourcename);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandCull'", 0);
        }
    }
    
    private void CommandActivate(final String sourcename) {
        if (this.soundLibrary != null) {
            this.soundLibrary.activate(sourcename);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandActivate'", 0);
        }
    }
    
    private void CommandSetMasterVolume(final float value) {
        if (this.soundLibrary != null) {
            this.soundLibrary.setMasterVolume(value);
        }
        else {
            this.errorMessage("Variable 'soundLibrary' null in method 'CommandSetMasterVolume'", 0);
        }
    }
    
    protected void ManageSources() {
    }
    
    public boolean CommandQueue(final CommandObject newCommand) {
        synchronized (SoundSystemConfig.THREAD_SYNC) {
            if (newCommand == null) {
                boolean b = false;
                while (this.commandQueue != null && this.commandQueue.size() > 0) {
                    final CommandObject commandObject = this.commandQueue.remove(0);
                    if (commandObject != null) {
                        switch (commandObject.Command) {
                            case 1: {
                                this.CommandInitialize();
                                continue;
                            }
                            case 2: {
                                this.CommandLoadSound((FilenameURL)commandObject.objectArgs[0]);
                                continue;
                            }
                            case 4: {
                                this.CommandUnloadSound(commandObject.stringArgs[0]);
                                continue;
                            }
                            case 5: {
                                this.CommandQueueSound(commandObject.stringArgs[0], (FilenameURL)commandObject.objectArgs[0]);
                                continue;
                            }
                            case 6: {
                                this.CommandDequeueSound(commandObject.stringArgs[0], commandObject.stringArgs[1]);
                                continue;
                            }
                            case 7: {
                                this.CommandFadeOut(commandObject.stringArgs[0], (FilenameURL)commandObject.objectArgs[0], commandObject.longArgs[0]);
                                continue;
                            }
                            case 8: {
                                this.CommandFadeOutIn(commandObject.stringArgs[0], (FilenameURL)commandObject.objectArgs[0], commandObject.longArgs[0], commandObject.longArgs[1]);
                                continue;
                            }
                            case 9: {
                                this.CommandCheckFadeVolumes();
                                continue;
                            }
                            case 10: {
                                this.CommandNewSource(commandObject.boolArgs[0], commandObject.boolArgs[1], commandObject.boolArgs[2], commandObject.stringArgs[0], (FilenameURL)commandObject.objectArgs[0], commandObject.floatArgs[0], commandObject.floatArgs[1], commandObject.floatArgs[2], commandObject.intArgs[0], commandObject.floatArgs[3]);
                                continue;
                            }
                            case 11: {
                                this.CommandRawDataStream((AudioFormat)commandObject.objectArgs[0], commandObject.boolArgs[0], commandObject.stringArgs[0], commandObject.floatArgs[0], commandObject.floatArgs[1], commandObject.floatArgs[2], commandObject.intArgs[0], commandObject.floatArgs[3]);
                                continue;
                            }
                            case 12: {
                                this.CommandQuickPlay(commandObject.boolArgs[0], commandObject.boolArgs[1], commandObject.boolArgs[2], commandObject.stringArgs[0], (FilenameURL)commandObject.objectArgs[0], commandObject.floatArgs[0], commandObject.floatArgs[1], commandObject.floatArgs[2], commandObject.intArgs[0], commandObject.floatArgs[3], commandObject.boolArgs[3]);
                                continue;
                            }
                            case 13: {
                                this.CommandSetPosition(commandObject.stringArgs[0], commandObject.floatArgs[0], commandObject.floatArgs[1], commandObject.floatArgs[2]);
                                continue;
                            }
                            case 14: {
                                this.CommandSetVolume(commandObject.stringArgs[0], commandObject.floatArgs[0]);
                                continue;
                            }
                            case 15: {
                                this.CommandSetPitch(commandObject.stringArgs[0], commandObject.floatArgs[0]);
                                continue;
                            }
                            case 16: {
                                this.CommandSetPriority(commandObject.stringArgs[0], commandObject.boolArgs[0]);
                                continue;
                            }
                            case 17: {
                                this.CommandSetLooping(commandObject.stringArgs[0], commandObject.boolArgs[0]);
                                continue;
                            }
                            case 18: {
                                this.CommandSetAttenuation(commandObject.stringArgs[0], commandObject.intArgs[0]);
                                continue;
                            }
                            case 19: {
                                this.CommandSetDistOrRoll(commandObject.stringArgs[0], commandObject.floatArgs[0]);
                                continue;
                            }
                            case 21: {
                                this.sourcePlayList.add(commandObject);
                                continue;
                            }
                            case 22: {
                                this.sourcePlayList.add(commandObject);
                                continue;
                            }
                            case 23: {
                                this.CommandPause(commandObject.stringArgs[0]);
                                continue;
                            }
                            case 24: {
                                this.CommandStop(commandObject.stringArgs[0]);
                                continue;
                            }
                            case 25: {
                                this.CommandRewind(commandObject.stringArgs[0]);
                                continue;
                            }
                            case 26: {
                                this.CommandFlush(commandObject.stringArgs[0]);
                                continue;
                            }
                            case 27: {
                                this.CommandCull(commandObject.stringArgs[0]);
                                continue;
                            }
                            case 28: {
                                b = true;
                                this.CommandActivate(commandObject.stringArgs[0]);
                                continue;
                            }
                            case 29: {
                                this.CommandSetTemporary(commandObject.stringArgs[0], commandObject.boolArgs[0]);
                                continue;
                            }
                            case 30: {
                                this.CommandRemoveSource(commandObject.stringArgs[0]);
                                continue;
                            }
                            case 31: {
                                this.CommandMoveListener(commandObject.floatArgs[0], commandObject.floatArgs[1], commandObject.floatArgs[2]);
                                continue;
                            }
                            case 32: {
                                this.CommandSetListenerPosition(commandObject.floatArgs[0], commandObject.floatArgs[1], commandObject.floatArgs[2]);
                                continue;
                            }
                            case 33: {
                                this.CommandTurnListener(commandObject.floatArgs[0]);
                                continue;
                            }
                            case 34: {
                                this.CommandSetListenerAngle(commandObject.floatArgs[0]);
                                continue;
                            }
                            case 35: {
                                this.CommandSetListenerOrientation(commandObject.floatArgs[0], commandObject.floatArgs[1], commandObject.floatArgs[2], commandObject.floatArgs[3], commandObject.floatArgs[4], commandObject.floatArgs[5]);
                                continue;
                            }
                            case 36: {
                                this.CommandSetMasterVolume(commandObject.floatArgs[0]);
                                continue;
                            }
                            case 37: {
                                this.CommandNewLibrary(commandObject.classArgs[0]);
                                continue;
                            }
                            default: {
                                continue;
                            }
                        }
                    }
                }
                if (b) {
                    this.soundLibrary.replaySources();
                }
                while (this.sourcePlayList != null && this.sourcePlayList.size() > 0) {
                    final CommandObject commandObject2 = this.sourcePlayList.remove(0);
                    if (commandObject2 != null) {
                        switch (commandObject2.Command) {
                            case 21: {
                                this.CommandPlay(commandObject2.stringArgs[0]);
                                continue;
                            }
                            case 22: {
                                this.CommandFeedRawAudioData(commandObject2.stringArgs[0], commandObject2.buffer);
                                continue;
                            }
                        }
                    }
                }
                return this.commandQueue != null && this.commandQueue.size() > 0;
            }
            if (this.commandQueue == null) {
                return false;
            }
            this.commandQueue.add(newCommand);
            return true;
        }
    }
    
    public void removeTemporarySources() {
        synchronized (SoundSystemConfig.THREAD_SYNC) {
            if (this.soundLibrary != null) {
                this.soundLibrary.removeTemporarySources();
            }
        }
    }
    
    public boolean playing(final String sourcename) {
        synchronized (SoundSystemConfig.THREAD_SYNC) {
            if (this.soundLibrary == null) {
                return false;
            }
            final Source source = this.soundLibrary.getSources().get(sourcename);
            return source != null && source.playing();
        }
    }
    
    public boolean playing() {
        synchronized (SoundSystemConfig.THREAD_SYNC) {
            if (this.soundLibrary == null) {
                return false;
            }
            final HashMap sources = this.soundLibrary.getSources();
            if (sources == null) {
                return false;
            }
            final Iterator iterator = sources.keySet().iterator();
            while (iterator.hasNext()) {
                final Source source = sources.get(iterator.next());
                if (source != null && source.playing()) {
                    return true;
                }
            }
            return false;
        }
    }
    
    private HashMap copySources(final HashMap sourceMap) {
        final Iterator iterator = sourceMap.keySet().iterator();
        final HashMap hashMap = new HashMap();
        while (iterator.hasNext()) {
            final String s = (String)iterator.next();
            final Source source = sourceMap.get(s);
            if (source != null) {
                hashMap.put(s, new Source(source, null));
            }
        }
        return hashMap;
    }
    
    public static boolean libraryCompatible(final Class libraryClass) {
        SoundSystemLogger logger = SoundSystemConfig.getLogger();
        if (logger == null) {
            logger = new SoundSystemLogger();
            SoundSystemConfig.setLogger(logger);
        }
        logger.message("", 0);
        logger.message("Checking if " + SoundSystemConfig.getLibraryTitle(libraryClass) + " is compatible...", 0);
        final boolean libraryCompatible = SoundSystemConfig.libraryCompatible(libraryClass);
        if (libraryCompatible) {
            logger.message("...yes", 1);
        }
        else {
            logger.message("...no", 1);
        }
        return libraryCompatible;
    }
    
    public static Class currentLibrary() {
        return currentLibrary(false, null);
    }
    
    public static boolean initialized() {
        return initialized(false, false);
    }
    
    public static SoundSystemException getLastException() {
        return lastException(false, null);
    }
    
    public static void setException(final SoundSystemException e) {
        lastException(true, e);
    }
    
    private static boolean initialized(final boolean action, final boolean value) {
        synchronized (SoundSystemConfig.THREAD_SYNC) {
            if (action) {
                SoundSystem.initialized = value;
            }
            return SoundSystem.initialized;
        }
    }
    
    private static Class currentLibrary(final boolean action, final Class value) {
        synchronized (SoundSystemConfig.THREAD_SYNC) {
            if (action) {
                SoundSystem.currentLibrary = value;
            }
            return SoundSystem.currentLibrary;
        }
    }
    
    private static SoundSystemException lastException(final boolean action, final SoundSystemException e) {
        synchronized (SoundSystemConfig.THREAD_SYNC) {
            if (action) {
                SoundSystem.lastException = e;
            }
            return SoundSystem.lastException;
        }
    }
    
    protected static void snooze(final long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        }
        catch (final InterruptedException ex) {}
    }
    
    protected void message(final String message, final int indent) {
        this.logger.message(message, indent);
    }
    
    protected void importantMessage(final String message, final int indent) {
        this.logger.importantMessage(message, indent);
    }
    
    protected boolean errorCheck(final boolean error, final String message, final int indent) {
        return this.logger.errorCheck(error, this.className, message, indent);
    }
    
    protected void errorMessage(final String message, final int indent) {
        this.logger.errorMessage(this.className, message, indent);
    }
    
    static {
        SoundSystem.currentLibrary = null;
        SoundSystem.initialized = false;
        SoundSystem.lastException = null;
    }
}
