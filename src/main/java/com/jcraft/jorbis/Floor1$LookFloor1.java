// 
// Decompiled by Procyon v0.6.0
// 

package com.jcraft.jorbis;

class Floor1$LookFloor1
{
    static final int VIF_POSIT = 63;
    int[] sorted_index;
    int[] forward_index;
    int[] reverse_index;
    int[] hineighbor;
    int[] loneighbor;
    int posts;
    int n;
    int quant_q;
    Floor1$InfoFloor1 vi;
    int phrasebits;
    int postbits;
    int frames;
    final /* synthetic */ Floor1 this$0;
    
    Floor1$LookFloor1(final Floor1 floor1) {
        this.this$0 = floor1;
        this.sorted_index = new int[65];
        this.forward_index = new int[65];
        this.reverse_index = new int[65];
        this.hineighbor = new int[63];
        this.loneighbor = new int[63];
    }
    
    void free() {
        this.sorted_index = null;
        this.forward_index = null;
        this.reverse_index = null;
        this.hineighbor = null;
        this.loneighbor = null;
    }
}
