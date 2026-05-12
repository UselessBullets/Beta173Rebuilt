// 
// Decompiled by Procyon v0.6.0
// 

package com.jcraft.jorbis;

class Util
{
    static int ilog(int integer) {
        int n = 0;
        while (integer != 0) {
            ++n;
            integer >>>= 1;
        }
        return n;
    }
    
    static int ilog2(int integer) {
        int n = 0;
        while (integer > 1) {
            ++n;
            integer >>>= 1;
        }
        return n;
    }
    
    static int icount(int integer) {
        int n = 0;
        while (integer != 0) {
            n += (integer & 0x1);
            integer >>>= 1;
        }
        return n;
    }
}
