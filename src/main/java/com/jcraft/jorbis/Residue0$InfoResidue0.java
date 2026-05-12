// 
// Decompiled by Procyon v0.6.0
// 

package com.jcraft.jorbis;

class Residue0$InfoResidue0
{
    int begin;
    int end;
    int grouping;
    int partitions;
    int groupbook;
    int[] secondstages;
    int[] booklist;
    float[] entmax;
    float[] ampmax;
    int[] subgrp;
    int[] blimit;
    final /* synthetic */ Residue0 this$0;
    
    Residue0$InfoResidue0(final Residue0 residue0) {
        this.this$0 = residue0;
        this.secondstages = new int[64];
        this.booklist = new int[256];
        this.entmax = new float[64];
        this.ampmax = new float[64];
        this.subgrp = new int[64];
        this.blimit = new int[64];
    }
}
