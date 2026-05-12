// 
// Decompiled by Procyon v0.6.0
// 

package paulscode.sound;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.ListIterator;
import java.util.LinkedList;

public class SoundSystemConfig
{
    public static final Object THREAD_SYNC;
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_STREAMING = 1;
    public static final int ATTENUATION_NONE = 0;
    public static final int ATTENUATION_ROLLOFF = 1;
    public static final int ATTENUATION_LINEAR = 2;
    public static final String EXTENSION_MIDI = ".*[mM][iI][dD][iI]?$";
    public static final String PREFIX_URL = "^[hH][tT][tT][pP]://.*";
    private static SoundSystemLogger logger;
    private static LinkedList libraries;
    private static LinkedList codecs;
    private static int numberNormalChannels;
    private static int numberStreamingChannels;
    private static float masterGain;
    private static int defaultAttenuationModel;
    private static float defaultRolloffFactor;
    private static float defaultFadeDistance;
    private static String soundFilesPackage;
    private static int streamingBufferSize;
    private static int numberStreamingBuffers;
    private static int maxFileSize;
    private static int fileChunkSize;
    private static boolean midiCodec;
    
    public static void addLibrary(final Class class1) {
        if (class1 == null) {
            throw new SoundSystemException("Parameter null in method 'addLibrary'", 2);
        }
        if (!Library.class.isAssignableFrom(class1)) {
            throw new SoundSystemException("The specified class does not extend class 'Library' in method 'addLibrary'");
        }
        if (SoundSystemConfig.libraries == null) {
            SoundSystemConfig.libraries = new LinkedList();
        }
        if (!SoundSystemConfig.libraries.contains(class1)) {
            SoundSystemConfig.libraries.add(class1);
        }
    }
    
    public static void removeLibrary(final Class class1) {
        if (SoundSystemConfig.libraries == null || class1 == null) {
            return;
        }
        SoundSystemConfig.libraries.remove(class1);
    }
    
    public static LinkedList getLibraries() {
        return SoundSystemConfig.libraries;
    }
    
    public static boolean libraryCompatible(final Class class1) {
        if (class1 == null) {
            errorMessage("Parameter 'libraryClass' null in method'librayCompatible'");
            return false;
        }
        if (!Library.class.isAssignableFrom(class1)) {
            errorMessage("The specified class does not extend class 'Library' in method 'libraryCompatible'");
            return false;
        }
        final Object runMethod = runMethod(class1, "libraryCompatible", new Class[0], new Object[0]);
        if (runMethod == null) {
            errorMessage("Method 'Library.libraryCompatible' returned 'null' in method 'libraryCompatible'");
            return false;
        }
        return (boolean)runMethod;
    }
    
    public static String getLibraryTitle(final Class class1) {
        if (class1 == null) {
            errorMessage("Parameter 'libraryClass' null in method'getLibrayTitle'");
            return null;
        }
        if (!Library.class.isAssignableFrom(class1)) {
            errorMessage("The specified class does not extend class 'Library' in method 'getLibraryTitle'");
            return null;
        }
        final Object runMethod = runMethod(class1, "getTitle", new Class[0], new Object[0]);
        if (runMethod == null) {
            errorMessage("Method 'Library.getTitle' returned 'null' in method 'getLibraryTitle'");
            return null;
        }
        return (String)runMethod;
    }
    
    public static String getLibraryDescription(final Class class1) {
        if (class1 == null) {
            errorMessage("Parameter 'libraryClass' null in method'getLibrayDescription'");
            return null;
        }
        if (!Library.class.isAssignableFrom(class1)) {
            errorMessage("The specified class does not extend class 'Library' in method 'getLibraryDescription'");
            return null;
        }
        final Object runMethod = runMethod(class1, "getDescription", new Class[0], new Object[0]);
        if (runMethod == null) {
            errorMessage("Method 'Library.getDescription' returned 'null' in method 'getLibraryDescription'");
            return null;
        }
        return (String)runMethod;
    }
    
    public static void setLogger(final SoundSystemLogger soundSystemLogger) {
        SoundSystemConfig.logger = soundSystemLogger;
    }
    
    public static SoundSystemLogger getLogger() {
        return SoundSystemConfig.logger;
    }
    
    public static synchronized void setNumberNormalChannels(final int integer) {
        SoundSystemConfig.numberNormalChannels = integer;
    }
    
    public static synchronized int getNumberNormalChannels() {
        return SoundSystemConfig.numberNormalChannels;
    }
    
    public static synchronized void setNumberStreamingChannels(final int integer) {
        SoundSystemConfig.numberStreamingChannels = integer;
    }
    
    public static synchronized int getNumberStreamingChannels() {
        return SoundSystemConfig.numberStreamingChannels;
    }
    
    public static synchronized void setMasterGain(final float float1) {
        SoundSystemConfig.masterGain = float1;
    }
    
    public static synchronized float getMasterGain() {
        return SoundSystemConfig.masterGain;
    }
    
    public static synchronized void setDefaultAttenuation(final int integer) {
        SoundSystemConfig.defaultAttenuationModel = integer;
    }
    
    public static synchronized int getDefaultAttenuation() {
        return SoundSystemConfig.defaultAttenuationModel;
    }
    
    public static synchronized void setDefaultRolloff(final float float1) {
        SoundSystemConfig.defaultRolloffFactor = float1;
    }
    
    public static synchronized float getDefaultRolloff() {
        return SoundSystemConfig.defaultRolloffFactor;
    }
    
    public static synchronized void setDefaultFadeDistance(final float float1) {
        SoundSystemConfig.defaultFadeDistance = float1;
    }
    
    public static synchronized float getDefaultFadeDistance() {
        return SoundSystemConfig.defaultFadeDistance;
    }
    
    public static synchronized void setSoundFilesPackage(final String string) {
        SoundSystemConfig.soundFilesPackage = string;
    }
    
    public static synchronized String getSoundFilesPackage() {
        return SoundSystemConfig.soundFilesPackage;
    }
    
    public static synchronized void setStreamingBufferSize(final int integer) {
        SoundSystemConfig.streamingBufferSize = integer;
    }
    
    public static synchronized int getStreamingBufferSize() {
        return SoundSystemConfig.streamingBufferSize;
    }
    
    public static synchronized void setNumberStreamingBuffers(final int integer) {
        SoundSystemConfig.numberStreamingBuffers = integer;
    }
    
    public static synchronized int getNumberStreamingBuffers() {
        return SoundSystemConfig.numberStreamingBuffers;
    }
    
    public static synchronized void setMaxFileSize(final int integer) {
        SoundSystemConfig.maxFileSize = integer;
    }
    
    public static synchronized int getMaxFileSize() {
        return SoundSystemConfig.maxFileSize;
    }
    
    public static synchronized void setFileChunkSize(final int integer) {
        SoundSystemConfig.fileChunkSize = integer;
    }
    
    public static synchronized int getFileChunkSize() {
        return SoundSystemConfig.fileChunkSize;
    }
    
    public static synchronized void setCodec(final String string, final Class class2) {
        if (string == null) {
            throw new SoundSystemException("Parameter 'extension' null in method 'setCodec'.", 2);
        }
        if (class2 == null) {
            throw new SoundSystemException("Parameter 'iCodecClass' null in method 'setCodec'.", 2);
        }
        if (!ICodec.class.isAssignableFrom(class2)) {
            throw new SoundSystemException("The specified class does not implement interface 'ICodec' in method 'setCodec'", 3);
        }
        if (SoundSystemConfig.codecs == null) {
            SoundSystemConfig.codecs = new LinkedList();
        }
        final ListIterator listIterator = SoundSystemConfig.codecs.listIterator();
        while (listIterator.hasNext()) {
            if (string.matches(((SoundSystemConfig$Codec)listIterator.next()).extensionRegX)) {
                listIterator.remove();
            }
        }
        SoundSystemConfig.codecs.add(new SoundSystemConfig$Codec(string, class2));
        if (string.matches(".*[mM][iI][dD][iI]?$")) {
            SoundSystemConfig.midiCodec = true;
        }
    }
    
    public static synchronized ICodec getCodec(final String string) {
        if (SoundSystemConfig.codecs == null) {
            return null;
        }
        final ListIterator listIterator = SoundSystemConfig.codecs.listIterator();
        while (listIterator.hasNext()) {
            final SoundSystemConfig$Codec soundSystemConfig$Codec = (SoundSystemConfig$Codec)listIterator.next();
            if (string.matches(soundSystemConfig$Codec.extensionRegX)) {
                return soundSystemConfig$Codec.getInstance();
            }
        }
        return null;
    }
    
    public static boolean midiCodec() {
        return SoundSystemConfig.midiCodec;
    }
    
    private static void errorMessage(final String string) {
        if (SoundSystemConfig.logger != null) {
            SoundSystemConfig.logger.errorMessage("SoundSystemConfig", string, 0);
        }
    }
    
    private static Object runMethod(final Class class1, final String string, final Class[] arr, final Object[] arr) {
        Method method;
        try {
            method = class1.getMethod(string, (Class[])arr);
        }
        catch (final NoSuchMethodException ex) {
            errorMessage("NoSuchMethodException thrown when attempting to call method '" + string + "' in " + "method 'runMethod'");
            return null;
        }
        catch (final SecurityException ex2) {
            errorMessage("Access denied when attempting to call method '" + string + "' in method 'runMethod'");
            return null;
        }
        catch (final NullPointerException ex3) {
            errorMessage("NullPointerException thrown when attempting to call method '" + string + "' in " + "method 'runMethod'");
            return null;
        }
        if (method == null) {
            errorMessage("Method '" + string + "' not found for the class " + "specified in method 'runMethod'");
            return null;
        }
        Object invoke;
        try {
            invoke = method.invoke(null, arr);
        }
        catch (final IllegalAccessException ex4) {
            errorMessage("IllegalAccessException thrown when attempting to invoke method '" + string + "' in " + "method 'runMethod'");
            return null;
        }
        catch (final IllegalArgumentException ex5) {
            errorMessage("IllegalArgumentException thrown when attempting to invoke method '" + string + "' in " + "method 'runMethod'");
            return null;
        }
        catch (final InvocationTargetException ex6) {
            errorMessage("InvocationTargetException thrown while attempting to invoke method 'Library.getTitle' in method 'getLibraryTitle'");
            return null;
        }
        catch (final NullPointerException ex7) {
            errorMessage("NullPointerException thrown when attempting to invoke method '" + string + "' in " + "method 'runMethod'");
            return null;
        }
        catch (final ExceptionInInitializerError exceptionInInitializerError) {
            errorMessage("ExceptionInInitializerError thrown when attempting to invoke method '" + string + "' in " + "method 'runMethod'");
            return null;
        }
        return invoke;
    }
    
    static {
        THREAD_SYNC = new Object();
        SoundSystemConfig.logger = null;
        SoundSystemConfig.codecs = null;
        SoundSystemConfig.numberNormalChannels = 28;
        SoundSystemConfig.numberStreamingChannels = 4;
        SoundSystemConfig.masterGain = 1.0f;
        SoundSystemConfig.defaultAttenuationModel = 1;
        SoundSystemConfig.defaultRolloffFactor = 0.03f;
        SoundSystemConfig.defaultFadeDistance = 1000.0f;
        SoundSystemConfig.soundFilesPackage = "Sounds/";
        SoundSystemConfig.streamingBufferSize = 131072;
        SoundSystemConfig.numberStreamingBuffers = 3;
        SoundSystemConfig.maxFileSize = 268435456;
        SoundSystemConfig.fileChunkSize = 1048576;
        SoundSystemConfig.midiCodec = false;
    }
}
