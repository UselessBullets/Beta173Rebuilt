// 
// Decompiled by Procyon v0.6.0
// 

package paulscode.sound;

public class CommandObject
{
    public static final int INITIALIZE = 1;
    public static final int LOAD_SOUND = 2;
    public static final int UNLOAD_SOUND = 4;
    public static final int QUEUE_SOUND = 5;
    public static final int DEQUEUE_SOUND = 6;
    public static final int FADE_OUT = 7;
    public static final int FADE_OUT_IN = 8;
    public static final int CHECK_FADE_VOLUMES = 9;
    public static final int NEW_SOURCE = 10;
    public static final int RAW_DATA_STREAM = 11;
    public static final int QUICK_PLAY = 12;
    public static final int SET_POSITION = 13;
    public static final int SET_VOLUME = 14;
    public static final int SET_PITCH = 15;
    public static final int SET_PRIORITY = 16;
    public static final int SET_LOOPING = 17;
    public static final int SET_ATTENUATION = 18;
    public static final int SET_DIST_OR_ROLL = 19;
    public static final int PLAY = 21;
    public static final int FEED_RAW_AUDIO_DATA = 22;
    public static final int PAUSE = 23;
    public static final int STOP = 24;
    public static final int REWIND = 25;
    public static final int FLUSH = 26;
    public static final int CULL = 27;
    public static final int ACTIVATE = 28;
    public static final int SET_TEMPORARY = 29;
    public static final int REMOVE_SOURCE = 30;
    public static final int MOVE_LISTENER = 31;
    public static final int SET_LISTENER_POSITION = 32;
    public static final int TURN_LISTENER = 33;
    public static final int SET_LISTENER_ANGLE = 34;
    public static final int SET_LISTENER_ORIENTATION = 35;
    public static final int SET_MASTER_VOLUME = 36;
    public static final int NEW_LIBRARY = 37;
    public byte[] buffer;
    public int[] intArgs;
    public float[] floatArgs;
    public long[] longArgs;
    public boolean[] boolArgs;
    public String[] stringArgs;
    public Class[] classArgs;
    public Object[] objectArgs;
    public int Command;
    
    public CommandObject(final int integer) {
        this.Command = integer;
    }
    
    public CommandObject(final int integer1, final int integer2) {
        this.Command = integer1;
        (this.intArgs = new int[1])[0] = integer2;
    }
    
    public CommandObject(final int integer, final Class class2) {
        this.Command = integer;
        (this.classArgs = new Class[1])[0] = class2;
    }
    
    public CommandObject(final int integer, final float float2) {
        this.Command = integer;
        (this.floatArgs = new float[1])[0] = float2;
    }
    
    public CommandObject(final int integer, final String string) {
        this.Command = integer;
        (this.stringArgs = new String[1])[0] = string;
    }
    
    public CommandObject(final int integer, final Object object) {
        this.Command = integer;
        (this.objectArgs = new Object[1])[0] = object;
    }
    
    public CommandObject(final int integer, final String string, final Object object) {
        this.Command = integer;
        (this.stringArgs = new String[1])[0] = string;
        (this.objectArgs = new Object[1])[0] = object;
    }
    
    public CommandObject(final int integer, final String string, final byte[] arr) {
        this.Command = integer;
        (this.stringArgs = new String[1])[0] = string;
        this.buffer = arr;
    }
    
    public CommandObject(final int integer, final String string, final Object object, final long long4) {
        this.Command = integer;
        (this.stringArgs = new String[1])[0] = string;
        (this.objectArgs = new Object[1])[0] = object;
        (this.longArgs = new long[1])[0] = long4;
    }
    
    public CommandObject(final int integer, final String string, final Object object, final long long4, final long long5) {
        this.Command = integer;
        (this.stringArgs = new String[1])[0] = string;
        (this.objectArgs = new Object[1])[0] = object;
        (this.longArgs = new long[2])[0] = long4;
        this.longArgs[1] = long5;
    }
    
    public CommandObject(final int integer, final String string2, final String string3) {
        this.Command = integer;
        (this.stringArgs = new String[2])[0] = string2;
        this.stringArgs[1] = string3;
    }
    
    public CommandObject(final int integer1, final String string, final int integer3) {
        this.Command = integer1;
        this.intArgs = new int[1];
        this.stringArgs = new String[1];
        this.intArgs[0] = integer3;
        this.stringArgs[0] = string;
    }
    
    public CommandObject(final int integer, final String string, final float float3) {
        this.Command = integer;
        this.floatArgs = new float[1];
        this.stringArgs = new String[1];
        this.floatArgs[0] = float3;
        this.stringArgs[0] = string;
    }
    
    public CommandObject(final int integer, final String string, final boolean boolean3) {
        this.Command = integer;
        this.boolArgs = new boolean[1];
        this.stringArgs = new String[1];
        this.boolArgs[0] = boolean3;
        this.stringArgs[0] = string;
    }
    
    public CommandObject(final int integer, final float float2, final float float3, final float float4) {
        this.Command = integer;
        (this.floatArgs = new float[3])[0] = float2;
        this.floatArgs[1] = float3;
        this.floatArgs[2] = float4;
    }
    
    public CommandObject(final int integer, final String string, final float float3, final float float4, final float float5) {
        this.Command = integer;
        this.floatArgs = new float[3];
        this.stringArgs = new String[1];
        this.floatArgs[0] = float3;
        this.floatArgs[1] = float4;
        this.floatArgs[2] = float5;
        this.stringArgs[0] = string;
    }
    
    public CommandObject(final int integer, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.Command = integer;
        (this.floatArgs = new float[6])[0] = float2;
        this.floatArgs[1] = float3;
        this.floatArgs[2] = float4;
        this.floatArgs[3] = float5;
        this.floatArgs[4] = float6;
        this.floatArgs[5] = float7;
    }
    
    public CommandObject(final int integer1, final boolean boolean2, final boolean boolean3, final boolean boolean4, final String string, final Object object, final float float7, final float float8, final float float9, final int integer10, final float float11) {
        this.Command = integer1;
        this.intArgs = new int[1];
        this.floatArgs = new float[4];
        this.boolArgs = new boolean[3];
        this.stringArgs = new String[1];
        this.objectArgs = new Object[1];
        this.intArgs[0] = integer10;
        this.floatArgs[0] = float7;
        this.floatArgs[1] = float8;
        this.floatArgs[2] = float9;
        this.floatArgs[3] = float11;
        this.boolArgs[0] = boolean2;
        this.boolArgs[1] = boolean3;
        this.boolArgs[2] = boolean4;
        this.stringArgs[0] = string;
        this.objectArgs[0] = object;
    }
    
    public CommandObject(final int integer1, final boolean boolean2, final boolean boolean3, final boolean boolean4, final String string, final Object object, final float float7, final float float8, final float float9, final int integer10, final float float11, final boolean boolean12) {
        this.Command = integer1;
        this.intArgs = new int[1];
        this.floatArgs = new float[4];
        this.boolArgs = new boolean[4];
        this.stringArgs = new String[1];
        this.objectArgs = new Object[1];
        this.intArgs[0] = integer10;
        this.floatArgs[0] = float7;
        this.floatArgs[1] = float8;
        this.floatArgs[2] = float9;
        this.floatArgs[3] = float11;
        this.boolArgs[0] = boolean2;
        this.boolArgs[1] = boolean3;
        this.boolArgs[2] = boolean4;
        this.boolArgs[3] = boolean12;
        this.stringArgs[0] = string;
        this.objectArgs[0] = object;
    }
    
    public CommandObject(final int integer1, final Object object, final boolean boolean3, final String string, final float float5, final float float6, final float float7, final int integer8, final float float9) {
        this.Command = integer1;
        this.intArgs = new int[1];
        this.floatArgs = new float[4];
        this.boolArgs = new boolean[1];
        this.stringArgs = new String[1];
        this.objectArgs = new Object[1];
        this.intArgs[0] = integer8;
        this.floatArgs[0] = float5;
        this.floatArgs[1] = float6;
        this.floatArgs[2] = float7;
        this.floatArgs[3] = float9;
        this.boolArgs[0] = boolean3;
        this.stringArgs[0] = string;
        this.objectArgs[0] = object;
    }
}
