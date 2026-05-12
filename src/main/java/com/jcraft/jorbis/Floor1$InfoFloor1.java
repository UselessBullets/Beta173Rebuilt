// 
// Decompiled by Procyon v0.6.0
// 

package com.jcraft.jorbis;

class Floor1$InfoFloor1
{
    static final int VIF_POSIT = 63;
    static final int VIF_CLASS = 16;
    static final int VIF_PARTS = 31;
    int partitions;
    int[] partitionclass;
    int[] class_dim;
    int[] class_subs;
    int[] class_book;
    int[][] class_subbook;
    int mult;
    int[] postlist;
    float maxover;
    float maxunder;
    float maxerr;
    int twofitminsize;
    int twofitminused;
    int twofitweight;
    float twofitatten;
    int unusedminsize;
    int unusedmin_n;
    int n;
    final /* synthetic */ Floor1 this$0;
    
    Floor1$InfoFloor1(final Floor1 floor1) {
        this.this$0 = floor1;
        this.partitionclass = new int[31];
        this.class_dim = new int[16];
        this.class_subs = new int[16];
        this.class_book = new int[16];
        this.class_subbook = new int[16][];
        this.postlist = new int[65];
        for (int i = 0; i < this.class_subbook.length; ++i) {
            this.class_subbook[i] = new int[8];
        }
    }
    
    void free() {
        this.partitionclass = null;
        this.class_dim = null;
        this.class_subs = null;
        this.class_book = null;
        this.class_subbook = null;
        this.postlist = null;
    }
    
    Object copy_info() {
        final Floor1$InfoFloor1 floor1$InfoFloor1 = new Floor1$InfoFloor1(this.this$0);
        floor1$InfoFloor1.partitions = this.partitions;
        System.arraycopy(this.partitionclass, 0, floor1$InfoFloor1.partitionclass, 0, 31);
        System.arraycopy(this.class_dim, 0, floor1$InfoFloor1.class_dim, 0, 16);
        System.arraycopy(this.class_subs, 0, floor1$InfoFloor1.class_subs, 0, 16);
        System.arraycopy(this.class_book, 0, floor1$InfoFloor1.class_book, 0, 16);
        for (int i = 0; i < 16; ++i) {
            System.arraycopy(this.class_subbook[i], 0, floor1$InfoFloor1.class_subbook[i], 0, 8);
        }
        floor1$InfoFloor1.mult = this.mult;
        System.arraycopy(this.postlist, 0, floor1$InfoFloor1.postlist, 0, 65);
        floor1$InfoFloor1.maxover = this.maxover;
        floor1$InfoFloor1.maxunder = this.maxunder;
        floor1$InfoFloor1.maxerr = this.maxerr;
        floor1$InfoFloor1.twofitminsize = this.twofitminsize;
        floor1$InfoFloor1.twofitminused = this.twofitminused;
        floor1$InfoFloor1.twofitweight = this.twofitweight;
        floor1$InfoFloor1.twofitatten = this.twofitatten;
        floor1$InfoFloor1.unusedminsize = this.unusedminsize;
        floor1$InfoFloor1.unusedmin_n = this.unusedmin_n;
        floor1$InfoFloor1.n = this.n;
        return floor1$InfoFloor1;
    }
}
