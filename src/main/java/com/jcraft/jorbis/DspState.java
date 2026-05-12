// 
// Decompiled by Procyon v0.6.0
// 

package com.jcraft.jorbis;

public class DspState
{
    static final float M_PI = 3.1415927f;
    static final int VI_TRANSFORMB = 1;
    static final int VI_WINDOWB = 1;
    int analysisp;
    Info vi;
    int modebits;
    float[][] pcm;
    int pcm_storage;
    int pcm_current;
    int pcm_returned;
    float[] multipliers;
    int envelope_storage;
    int envelope_current;
    int eofflag;
    int lW;
    int W;
    int nW;
    int centerW;
    long granulepos;
    long sequence;
    long glue_bits;
    long time_bits;
    long floor_bits;
    long res_bits;
    float[][][][][] window;
    Object[][] transform;
    CodeBook[] fullbooks;
    Object[] mode;
    byte[] header;
    byte[] header1;
    byte[] header2;
    
    public DspState() {
        this.transform = new Object[2][];
        this.window = new float[2][][][][];
        (this.window[0] = new float[2][][][])[0] = new float[2][][];
        this.window[0][1] = new float[2][][];
        this.window[0][0][0] = new float[2][];
        this.window[0][0][1] = new float[2][];
        this.window[0][1][0] = new float[2][];
        this.window[0][1][1] = new float[2][];
        (this.window[1] = new float[2][][][])[0] = new float[2][][];
        this.window[1][1] = new float[2][][];
        this.window[1][0][0] = new float[2][];
        this.window[1][0][1] = new float[2][];
        this.window[1][1][0] = new float[2][];
        this.window[1][1][1] = new float[2][];
    }
    
    static float[] window(final int integer1, final int integer2, final int integer3, final int integer4) {
        final float[] array = new float[integer2];
        switch (integer1) {
            case 0: {
                final int n = integer2 / 4 - integer3 / 2;
                final int n2 = integer2 - integer2 / 4 - integer4 / 2;
                for (int i = 0; i < integer3; ++i) {
                    final float n3 = (float)Math.sin((float)((i + 0.5) / integer3 * 3.1415927410125732 / 2.0));
                    array[i + n] = (float)Math.sin((float)(n3 * n3 * 1.5707963705062866));
                }
                for (int j = n + integer3; j < n2; ++j) {
                    array[j] = 1.0f;
                }
                for (int k = 0; k < integer4; ++k) {
                    final float n4 = (float)Math.sin((float)((integer4 - k - 0.5) / integer4 * 3.1415927410125732 / 2.0));
                    array[k + n2] = (float)Math.sin((float)(n4 * n4 * 1.5707963705062866));
                }
                return array;
            }
            default: {
                return null;
            }
        }
    }
    
    int init(final Info info, final boolean boolean2) {
        this.vi = info;
        this.modebits = Util.ilog2(info.modes);
        this.transform[0] = new Object[1];
        this.transform[1] = new Object[1];
        this.transform[0][0] = new Mdct();
        this.transform[1][0] = new Mdct();
        ((Mdct)this.transform[0][0]).init(info.blocksizes[0]);
        ((Mdct)this.transform[1][0]).init(info.blocksizes[1]);
        this.window[0][0][0] = new float[1][];
        this.window[0][0][1] = this.window[0][0][0];
        this.window[0][1][0] = this.window[0][0][0];
        this.window[0][1][1] = this.window[0][0][0];
        this.window[1][0][0] = new float[1][];
        this.window[1][0][1] = new float[1][];
        this.window[1][1][0] = new float[1][];
        this.window[1][1][1] = new float[1][];
        for (int i = 0; i < 1; ++i) {
            this.window[0][0][0][i] = window(i, info.blocksizes[0], info.blocksizes[0] / 2, info.blocksizes[0] / 2);
            this.window[1][0][0][i] = window(i, info.blocksizes[1], info.blocksizes[0] / 2, info.blocksizes[0] / 2);
            this.window[1][0][1][i] = window(i, info.blocksizes[1], info.blocksizes[0] / 2, info.blocksizes[1] / 2);
            this.window[1][1][0][i] = window(i, info.blocksizes[1], info.blocksizes[1] / 2, info.blocksizes[0] / 2);
            this.window[1][1][1][i] = window(i, info.blocksizes[1], info.blocksizes[1] / 2, info.blocksizes[1] / 2);
        }
        this.fullbooks = new CodeBook[info.books];
        for (int j = 0; j < info.books; ++j) {
            (this.fullbooks[j] = new CodeBook()).init_decode(info.book_param[j]);
        }
        this.pcm_storage = 8192;
        this.pcm = new float[info.channels][];
        for (int k = 0; k < info.channels; ++k) {
            this.pcm[k] = new float[this.pcm_storage];
        }
        this.lW = 0;
        this.W = 0;
        this.centerW = info.blocksizes[1] / 2;
        this.pcm_current = this.centerW;
        this.mode = new Object[info.modes];
        for (int l = 0; l < info.modes; ++l) {
            final int mapping = info.mode_param[l].mapping;
            this.mode[l] = FuncMapping.mapping_P[info.map_type[mapping]].look(this, info.mode_param[l], info.map_param[mapping]);
        }
        return 0;
    }
    
    public int synthesis_init(final Info info) {
        this.init(info, false);
        this.pcm_returned = this.centerW;
        this.centerW -= info.blocksizes[this.W] / 4 + info.blocksizes[this.lW] / 4;
        this.granulepos = -1L;
        this.sequence = -1L;
        return 0;
    }
    
    DspState(final Info info) {
        this();
        this.init(info, false);
        this.pcm_returned = this.centerW;
        this.centerW -= info.blocksizes[this.W] / 4 + info.blocksizes[this.lW] / 4;
        this.granulepos = -1L;
        this.sequence = -1L;
    }
    
    public int synthesis_blockin(final Block block) {
        if (this.centerW > this.vi.blocksizes[1] / 2 && this.pcm_returned > 8192) {
            final int n = this.centerW - this.vi.blocksizes[1] / 2;
            final int n2 = (this.pcm_returned < n) ? this.pcm_returned : n;
            this.pcm_current -= n2;
            this.centerW -= n2;
            this.pcm_returned -= n2;
            if (n2 != 0) {
                for (int i = 0; i < this.vi.channels; ++i) {
                    System.arraycopy(this.pcm[i], n2, this.pcm[i], 0, this.pcm_current);
                }
            }
        }
        this.lW = this.W;
        this.W = block.W;
        this.nW = -1;
        this.glue_bits += block.glue_bits;
        this.time_bits += block.time_bits;
        this.floor_bits += block.floor_bits;
        this.res_bits += block.res_bits;
        if (this.sequence + 1L != block.sequence) {
            this.granulepos = -1L;
        }
        this.sequence = block.sequence;
        final int n3 = this.vi.blocksizes[this.W];
        int centerW = this.centerW + this.vi.blocksizes[this.lW] / 4 + n3 / 4;
        final int n4 = centerW - n3 / 2;
        final int pcm_current = n4 + n3;
        int n5 = 0;
        int n6 = 0;
        if (pcm_current > this.pcm_storage) {
            this.pcm_storage = pcm_current + this.vi.blocksizes[1];
            for (int j = 0; j < this.vi.channels; ++j) {
                final float[] array = new float[this.pcm_storage];
                System.arraycopy(this.pcm[j], 0, array, 0, this.pcm[j].length);
                this.pcm[j] = array;
            }
        }
        switch (this.W) {
            case 0: {
                n5 = 0;
                n6 = this.vi.blocksizes[0] / 2;
                break;
            }
            case 1: {
                n5 = this.vi.blocksizes[1] / 4 - this.vi.blocksizes[this.lW] / 4;
                n6 = n5 + this.vi.blocksizes[this.lW] / 2;
                break;
            }
        }
        for (int k = 0; k < this.vi.channels; ++k) {
            final int n7 = n4;
            int l;
            for (l = n5; l < n6; ++l) {
                final float[] array2 = this.pcm[k];
                final int n8 = n7 + l;
                array2[n8] += block.pcm[k][l];
            }
            while (l < n3) {
                this.pcm[k][n7 + l] = block.pcm[k][l];
                ++l;
            }
        }
        if (this.granulepos == -1L) {
            this.granulepos = block.granulepos;
        }
        else {
            this.granulepos += centerW - this.centerW;
            if (block.granulepos != -1L && this.granulepos != block.granulepos) {
                if (this.granulepos > block.granulepos && block.eofflag != 0) {
                    centerW -= (int)(this.granulepos - block.granulepos);
                }
                this.granulepos = block.granulepos;
            }
        }
        this.centerW = centerW;
        this.pcm_current = pcm_current;
        if (block.eofflag != 0) {
            this.eofflag = 1;
        }
        return 0;
    }
    
    public int synthesis_pcmout(final float[][][] arr, final int[] arr) {
        if (this.pcm_returned < this.centerW) {
            if (arr != null) {
                for (int i = 0; i < this.vi.channels; ++i) {
                    arr[i] = this.pcm_returned;
                }
                arr[0] = this.pcm;
            }
            return this.centerW - this.pcm_returned;
        }
        return 0;
    }
    
    public int synthesis_read(final int integer) {
        if (integer != 0 && this.pcm_returned + integer > this.centerW) {
            return -1;
        }
        this.pcm_returned += integer;
        return 0;
    }
    
    public void clear() {
    }
}
