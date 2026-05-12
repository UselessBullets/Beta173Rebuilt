// 
// Decompiled by Procyon v0.6.0
// 

package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;

class Mapping0 extends FuncMapping
{
    static int seq;
    float[][] pcmbundle;
    int[] zerobundle;
    int[] nonzero;
    Object[] floormemo;
    
    Mapping0() {
        this.pcmbundle = null;
        this.zerobundle = null;
        this.nonzero = null;
        this.floormemo = null;
    }
    
    @Override
    void free_info(final Object object) {
    }
    
    @Override
    void free_look(final Object object) {
    }
    
    @Override
    Object look(final DspState dspState, final InfoMode infoMode, final Object object) {
        final Info vi = dspState.vi;
        final Mapping0$LookMapping0 mapping0$LookMapping2;
        final Mapping0$LookMapping0 mapping0$LookMapping0 = mapping0$LookMapping2 = new Mapping0$LookMapping0(this);
        final Mapping0$InfoMapping0 map = (Mapping0$InfoMapping0)object;
        mapping0$LookMapping2.map = map;
        final Mapping0$InfoMapping0 mapping0$InfoMapping0 = map;
        mapping0$LookMapping0.mode = infoMode;
        mapping0$LookMapping0.time_look = new Object[mapping0$InfoMapping0.submaps];
        mapping0$LookMapping0.floor_look = new Object[mapping0$InfoMapping0.submaps];
        mapping0$LookMapping0.residue_look = new Object[mapping0$InfoMapping0.submaps];
        mapping0$LookMapping0.time_func = new FuncTime[mapping0$InfoMapping0.submaps];
        mapping0$LookMapping0.floor_func = new FuncFloor[mapping0$InfoMapping0.submaps];
        mapping0$LookMapping0.residue_func = new FuncResidue[mapping0$InfoMapping0.submaps];
        for (int i = 0; i < mapping0$InfoMapping0.submaps; ++i) {
            final int n = mapping0$InfoMapping0.timesubmap[i];
            final int n2 = mapping0$InfoMapping0.floorsubmap[i];
            final int n3 = mapping0$InfoMapping0.residuesubmap[i];
            mapping0$LookMapping0.time_func[i] = FuncTime.time_P[vi.time_type[n]];
            mapping0$LookMapping0.time_look[i] = mapping0$LookMapping0.time_func[i].look(dspState, infoMode, vi.time_param[n]);
            mapping0$LookMapping0.floor_func[i] = FuncFloor.floor_P[vi.floor_type[n2]];
            mapping0$LookMapping0.floor_look[i] = mapping0$LookMapping0.floor_func[i].look(dspState, infoMode, vi.floor_param[n2]);
            mapping0$LookMapping0.residue_func[i] = FuncResidue.residue_P[vi.residue_type[n3]];
            mapping0$LookMapping0.residue_look[i] = mapping0$LookMapping0.residue_func[i].look(dspState, infoMode, vi.residue_param[n3]);
        }
        if (vi.psys == 0 || dspState.analysisp != 0) {}
        mapping0$LookMapping0.ch = vi.channels;
        return mapping0$LookMapping0;
    }
    
    @Override
    void pack(final Info info, final Object object, final Buffer buffer) {
        final Mapping0$InfoMapping0 mapping0$InfoMapping0 = (Mapping0$InfoMapping0)object;
        if (mapping0$InfoMapping0.submaps > 1) {
            buffer.write(1, 1);
            buffer.write(mapping0$InfoMapping0.submaps - 1, 4);
        }
        else {
            buffer.write(0, 1);
        }
        if (mapping0$InfoMapping0.coupling_steps > 0) {
            buffer.write(1, 1);
            buffer.write(mapping0$InfoMapping0.coupling_steps - 1, 8);
            for (int i = 0; i < mapping0$InfoMapping0.coupling_steps; ++i) {
                buffer.write(mapping0$InfoMapping0.coupling_mag[i], Util.ilog2(info.channels));
                buffer.write(mapping0$InfoMapping0.coupling_ang[i], Util.ilog2(info.channels));
            }
        }
        else {
            buffer.write(0, 1);
        }
        buffer.write(0, 2);
        if (mapping0$InfoMapping0.submaps > 1) {
            for (int j = 0; j < info.channels; ++j) {
                buffer.write(mapping0$InfoMapping0.chmuxlist[j], 4);
            }
        }
        for (int k = 0; k < mapping0$InfoMapping0.submaps; ++k) {
            buffer.write(mapping0$InfoMapping0.timesubmap[k], 8);
            buffer.write(mapping0$InfoMapping0.floorsubmap[k], 8);
            buffer.write(mapping0$InfoMapping0.residuesubmap[k], 8);
        }
    }
    
    @Override
    Object unpack(final Info info, final Buffer buffer) {
        final Mapping0$InfoMapping0 mapping0$InfoMapping0 = new Mapping0$InfoMapping0(this);
        if (buffer.read(1) != 0) {
            mapping0$InfoMapping0.submaps = buffer.read(4) + 1;
        }
        else {
            mapping0$InfoMapping0.submaps = 1;
        }
        if (buffer.read(1) != 0) {
            mapping0$InfoMapping0.coupling_steps = buffer.read(8) + 1;
            for (int i = 0; i < mapping0$InfoMapping0.coupling_steps; ++i) {
                final int[] coupling_mag = mapping0$InfoMapping0.coupling_mag;
                final int n = i;
                final int read = buffer.read(Util.ilog2(info.channels));
                coupling_mag[n] = read;
                final int n2 = read;
                final int[] coupling_ang = mapping0$InfoMapping0.coupling_ang;
                final int n3 = i;
                final int read2 = buffer.read(Util.ilog2(info.channels));
                coupling_ang[n3] = read2;
                final int n4 = read2;
                if (n2 < 0 || n4 < 0 || n2 == n4 || n2 >= info.channels || n4 >= info.channels) {
                    mapping0$InfoMapping0.free();
                    return null;
                }
            }
        }
        if (buffer.read(2) > 0) {
            mapping0$InfoMapping0.free();
            return null;
        }
        if (mapping0$InfoMapping0.submaps > 1) {
            for (int j = 0; j < info.channels; ++j) {
                mapping0$InfoMapping0.chmuxlist[j] = buffer.read(4);
                if (mapping0$InfoMapping0.chmuxlist[j] >= mapping0$InfoMapping0.submaps) {
                    mapping0$InfoMapping0.free();
                    return null;
                }
            }
        }
        for (int k = 0; k < mapping0$InfoMapping0.submaps; ++k) {
            mapping0$InfoMapping0.timesubmap[k] = buffer.read(8);
            if (mapping0$InfoMapping0.timesubmap[k] >= info.times) {
                mapping0$InfoMapping0.free();
                return null;
            }
            mapping0$InfoMapping0.floorsubmap[k] = buffer.read(8);
            if (mapping0$InfoMapping0.floorsubmap[k] >= info.floors) {
                mapping0$InfoMapping0.free();
                return null;
            }
            mapping0$InfoMapping0.residuesubmap[k] = buffer.read(8);
            if (mapping0$InfoMapping0.residuesubmap[k] >= info.residues) {
                mapping0$InfoMapping0.free();
                return null;
            }
        }
        return mapping0$InfoMapping0;
    }
    
    @Override
    synchronized int inverse(final Block block, final Object object) {
        final DspState vd = block.vd;
        final Info vi = vd.vi;
        final Mapping0$LookMapping0 mapping0$LookMapping0 = (Mapping0$LookMapping0)object;
        final Mapping0$InfoMapping0 map = mapping0$LookMapping0.map;
        final InfoMode mode = mapping0$LookMapping0.mode;
        final int pcmend = vi.blocksizes[block.W];
        block.pcmend = pcmend;
        final int n = pcmend;
        final float[] array = vd.window[block.W][block.lW][block.nW][mode.windowtype];
        if (this.pcmbundle == null || this.pcmbundle.length < vi.channels) {
            this.pcmbundle = new float[vi.channels][];
            this.nonzero = new int[vi.channels];
            this.zerobundle = new int[vi.channels];
            this.floormemo = new Object[vi.channels];
        }
        for (int i = 0; i < vi.channels; ++i) {
            final float[] array2 = block.pcm[i];
            final int n2 = map.chmuxlist[i];
            this.floormemo[i] = mapping0$LookMapping0.floor_func[n2].inverse1(block, mapping0$LookMapping0.floor_look[n2], this.floormemo[i]);
            if (this.floormemo[i] != null) {
                this.nonzero[i] = 1;
            }
            else {
                this.nonzero[i] = 0;
            }
            for (int j = 0; j < n / 2; ++j) {
                array2[j] = 0.0f;
            }
        }
        for (int k = 0; k < map.coupling_steps; ++k) {
            if (this.nonzero[map.coupling_mag[k]] != 0 || this.nonzero[map.coupling_ang[k]] != 0) {
                this.nonzero[map.coupling_mag[k]] = 1;
                this.nonzero[map.coupling_ang[k]] = 1;
            }
        }
        for (int l = 0; l < map.submaps; ++l) {
            int integer = 0;
            for (int n3 = 0; n3 < vi.channels; ++n3) {
                if (map.chmuxlist[n3] == l) {
                    if (this.nonzero[n3] != 0) {
                        this.zerobundle[integer] = 1;
                    }
                    else {
                        this.zerobundle[integer] = 0;
                    }
                    this.pcmbundle[integer++] = block.pcm[n3];
                }
            }
            mapping0$LookMapping0.residue_func[l].inverse(block, mapping0$LookMapping0.residue_look[l], this.pcmbundle, this.zerobundle, integer);
        }
        for (int n4 = map.coupling_steps - 1; n4 >= 0; --n4) {
            final float[] array3 = block.pcm[map.coupling_mag[n4]];
            final float[] array4 = block.pcm[map.coupling_ang[n4]];
            for (int n5 = 0; n5 < n / 2; ++n5) {
                final float n6 = array3[n5];
                final float n7 = array4[n5];
                if (n6 > 0.0f) {
                    if (n7 > 0.0f) {
                        array4[n5] = (array3[n5] = n6) - n7;
                    }
                    else {
                        array3[n5] = (array4[n5] = n6) + n7;
                    }
                }
                else if (n7 > 0.0f) {
                    array4[n5] = (array3[n5] = n6) + n7;
                }
                else {
                    array3[n5] = (array4[n5] = n6) - n7;
                }
            }
        }
        for (int n8 = 0; n8 < vi.channels; ++n8) {
            final float[] arr = block.pcm[n8];
            final int n9 = map.chmuxlist[n8];
            mapping0$LookMapping0.floor_func[n9].inverse2(block, mapping0$LookMapping0.floor_look[n9], this.floormemo[n8], arr);
        }
        for (int n10 = 0; n10 < vi.channels; ++n10) {
            final float[] array5 = block.pcm[n10];
            ((Mdct)vd.transform[block.W][0]).backward(array5, array5);
        }
        for (int n11 = 0; n11 < vi.channels; ++n11) {
            final float[] array6 = block.pcm[n11];
            if (this.nonzero[n11] != 0) {
                for (int n12 = 0; n12 < n; ++n12) {
                    final float[] array7 = array6;
                    final int n13 = n12;
                    array7[n13] *= array[n12];
                }
            }
            else {
                for (int n14 = 0; n14 < n; ++n14) {
                    array6[n14] = 0.0f;
                }
            }
        }
        return 0;
    }
    
    static {
        Mapping0.seq = 0;
    }
}
