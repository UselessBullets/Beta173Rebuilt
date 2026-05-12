// 
// Decompiled by Procyon v0.6.0
// 

package com.jcraft.jorbis;

import util.Mth;
import com.jcraft.jogg.Buffer;

class Floor0 extends FuncFloor
{
    float[] lsp;
    
    Floor0() {
        this.lsp = null;
    }
    
    @Override
    void pack(final Object object, final Buffer buffer) {
        final Floor0$InfoFloor0 floor0$InfoFloor0 = (Floor0$InfoFloor0)object;
        buffer.write(floor0$InfoFloor0.order, 8);
        buffer.write(floor0$InfoFloor0.rate, 16);
        buffer.write(floor0$InfoFloor0.barkmap, 16);
        buffer.write(floor0$InfoFloor0.ampbits, 6);
        buffer.write(floor0$InfoFloor0.ampdB, 8);
        buffer.write(floor0$InfoFloor0.numbooks - 1, 4);
        for (int i = 0; i < floor0$InfoFloor0.numbooks; ++i) {
            buffer.write(floor0$InfoFloor0.books[i], 8);
        }
    }
    
    @Override
    Object unpack(final Info info, final Buffer buffer) {
        final Floor0$InfoFloor0 floor0$InfoFloor0 = new Floor0$InfoFloor0(this);
        floor0$InfoFloor0.order = buffer.read(8);
        floor0$InfoFloor0.rate = buffer.read(16);
        floor0$InfoFloor0.barkmap = buffer.read(16);
        floor0$InfoFloor0.ampbits = buffer.read(6);
        floor0$InfoFloor0.ampdB = buffer.read(8);
        floor0$InfoFloor0.numbooks = buffer.read(4) + 1;
        if (floor0$InfoFloor0.order < 1 || floor0$InfoFloor0.rate < 1 || floor0$InfoFloor0.barkmap < 1 || floor0$InfoFloor0.numbooks < 1) {
            return null;
        }
        for (int i = 0; i < floor0$InfoFloor0.numbooks; ++i) {
            floor0$InfoFloor0.books[i] = buffer.read(8);
            if (floor0$InfoFloor0.books[i] < 0 || floor0$InfoFloor0.books[i] >= info.books) {
                return null;
            }
        }
        return floor0$InfoFloor0;
    }
    
    @Override
    Object look(final DspState dspState, final InfoMode infoMode, final Object object) {
        final Info vi = dspState.vi;
        final Floor0$InfoFloor0 vi2 = (Floor0$InfoFloor0)object;
        final Floor0$LookFloor0 floor0$LookFloor0 = new Floor0$LookFloor0(this);
        floor0$LookFloor0.m = vi2.order;
        floor0$LookFloor0.n = vi.blocksizes[infoMode.blockflag] / 2;
        floor0$LookFloor0.ln = vi2.barkmap;
        floor0$LookFloor0.vi = vi2;
        floor0$LookFloor0.lpclook.init(floor0$LookFloor0.ln, floor0$LookFloor0.m);
        final float n = floor0$LookFloor0.ln / toBARK((float)(vi2.rate / 2.0));
        floor0$LookFloor0.linearmap = new int[floor0$LookFloor0.n];
        for (int i = 0; i < floor0$LookFloor0.n; ++i) {
            int n2 = Mth.floor(toBARK((float)(vi2.rate / 2.0 / floor0$LookFloor0.n * i)) * n);
            if (n2 >= floor0$LookFloor0.ln) {
                n2 = floor0$LookFloor0.ln;
            }
            floor0$LookFloor0.linearmap[i] = n2;
        }
        return floor0$LookFloor0;
    }
    
    static float toBARK(final float float1) {
        return (float)(13.1 * Math.atan(7.4E-4 * float1) + 2.24 * Math.atan(float1 * float1 * 1.85E-8) + 1.0E-4 * float1);
    }
    
    Object state(final Object object) {
        final Floor0$EchstateFloor0 floor0$EchstateFloor0 = new Floor0$EchstateFloor0(this);
        final Floor0$InfoFloor0 floor0$InfoFloor0 = (Floor0$InfoFloor0)object;
        floor0$EchstateFloor0.codewords = new int[floor0$InfoFloor0.order];
        floor0$EchstateFloor0.curve = new float[floor0$InfoFloor0.barkmap];
        floor0$EchstateFloor0.frameno = -1L;
        return floor0$EchstateFloor0;
    }
    
    @Override
    void free_info(final Object object) {
    }
    
    @Override
    void free_look(final Object object) {
    }
    
    @Override
    void free_state(final Object object) {
    }
    
    @Override
    int forward(final Block block, final Object object2, final float[] arr3, final float[] arr4, final Object object5) {
        return 0;
    }
    
    int inverse(final Block block, final Object object, final float[] arr) {
        final Floor0$LookFloor0 floor0$LookFloor0 = (Floor0$LookFloor0)object;
        final Floor0$InfoFloor0 vi = floor0$LookFloor0.vi;
        final int read = block.opb.read(vi.ampbits);
        if (read > 0) {
            final float float7 = read / (float)((1 << vi.ampbits) - 1) * vi.ampdB;
            final int read2 = block.opb.read(Util.ilog(vi.numbooks));
            if (read2 != -1 && read2 < vi.numbooks) {
                synchronized (this) {
                    if (this.lsp == null || this.lsp.length < floor0$LookFloor0.m) {
                        this.lsp = new float[floor0$LookFloor0.m];
                    }
                    else {
                        for (int i = 0; i < floor0$LookFloor0.m; ++i) {
                            this.lsp[i] = 0.0f;
                        }
                    }
                    final CodeBook codeBook = block.vd.fullbooks[vi.books[read2]];
                    float n = 0.0f;
                    for (int j = 0; j < floor0$LookFloor0.m; ++j) {
                        arr[j] = 0.0f;
                    }
                    for (int k = 0; k < floor0$LookFloor0.m; k += codeBook.dim) {
                        if (codeBook.decodevs(this.lsp, k, block.opb, 1, -1) == -1) {
                            for (int l = 0; l < floor0$LookFloor0.n; ++l) {
                                arr[l] = 0.0f;
                            }
                            return 0;
                        }
                    }
                    int n2 = 0;
                    while (n2 < floor0$LookFloor0.m) {
                        for (int n3 = 0; n3 < codeBook.dim; ++n3, ++n2) {
                            final float[] lsp = this.lsp;
                            final int n4 = n2;
                            lsp[n4] += n;
                        }
                        n = this.lsp[n2 - 1];
                    }
                    Lsp.lsp_to_curve(arr, floor0$LookFloor0.linearmap, floor0$LookFloor0.n, floor0$LookFloor0.ln, this.lsp, floor0$LookFloor0.m, float7, (float)vi.ampdB);
                    return 1;
                }
            }
        }
        return 0;
    }
    
    @Override
    Object inverse1(final Block block, final Object object2, final Object object3) {
        final Floor0$LookFloor0 floor0$LookFloor0 = (Floor0$LookFloor0)object2;
        final Floor0$InfoFloor0 vi = floor0$LookFloor0.vi;
        float[] arr = null;
        if (object3 instanceof float[]) {
            arr = (float[])object3;
        }
        final int read = block.opb.read(vi.ampbits);
        if (read > 0) {
            final float n = read / (float)((1 << vi.ampbits) - 1) * vi.ampdB;
            final int read2 = block.opb.read(Util.ilog(vi.numbooks));
            if (read2 != -1 && read2 < vi.numbooks) {
                final CodeBook codeBook = block.vd.fullbooks[vi.books[read2]];
                float n2 = 0.0f;
                if (arr == null || arr.length < floor0$LookFloor0.m + 1) {
                    arr = new float[floor0$LookFloor0.m + 1];
                }
                else {
                    for (int i = 0; i < arr.length; ++i) {
                        arr[i] = 0.0f;
                    }
                }
                for (int j = 0; j < floor0$LookFloor0.m; j += codeBook.dim) {
                    if (codeBook.decodev_set(arr, j, block.opb, codeBook.dim) == -1) {
                        return null;
                    }
                }
                int k = 0;
                while (k < floor0$LookFloor0.m) {
                    for (int l = 0; l < codeBook.dim; ++l, ++k) {
                        final float[] array = arr;
                        final int n3 = k;
                        array[n3] += n2;
                    }
                    n2 = arr[k - 1];
                }
                arr[floor0$LookFloor0.m] = n;
                return arr;
            }
        }
        return null;
    }
    
    @Override
    int inverse2(final Block block, final Object object2, final Object object3, final float[] arr) {
        final Floor0$LookFloor0 floor0$LookFloor0 = (Floor0$LookFloor0)object2;
        final Floor0$InfoFloor0 vi = floor0$LookFloor0.vi;
        if (object3 != null) {
            final float[] arr2 = (float[])object3;
            Lsp.lsp_to_curve(arr, floor0$LookFloor0.linearmap, floor0$LookFloor0.n, floor0$LookFloor0.ln, arr2, floor0$LookFloor0.m, arr2[floor0$LookFloor0.m], (float)vi.ampdB);
            return 1;
        }
        for (int i = 0; i < floor0$LookFloor0.n; ++i) {
            arr[i] = 0.0f;
        }
        return 0;
    }
    
    static float fromdB(final float float1) {
        return (float)Math.exp(float1 * 0.11512925);
    }
    
    static void lsp_to_lpc(final float[] arr1, final float[] arr2, final int integer) {
        final int n = integer / 2;
        final float[] array = new float[n];
        final float[] array2 = new float[n];
        final float[] array3 = new float[n + 1];
        final float[] array4 = new float[n + 1];
        final float[] array5 = new float[n];
        final float[] array6 = new float[n];
        for (int i = 0; i < n; ++i) {
            array[i] = (float)(-2.0 * Math.cos(arr1[i * 2]));
            array2[i] = (float)(-2.0 * Math.cos(arr1[i * 2 + 1]));
        }
        int j;
        for (j = 0; j < n; ++j) {
            array3[j] = 0.0f;
            array4[j] = 1.0f;
            array5[j] = 0.0f;
            array6[j] = 1.0f;
        }
        array3[j] = (array4[j] = 1.0f);
        for (int k = 1; k < integer + 1; ++k) {
            float n3;
            float n2 = n3 = 0.0f;
            int l;
            for (l = 0; l < n; ++l) {
                final float n4 = array[l] * array4[l] + array3[l];
                array3[l] = array4[l];
                array4[l] = n3;
                n3 += n4;
                final float n5 = array2[l] * array6[l] + array5[l];
                array5[l] = array6[l];
                array6[l] = n2;
                n2 += n5;
            }
            arr2[k - 1] = (n3 + array4[l] + n2 - array3[l]) / 2.0f;
            array4[l] = n3;
            array3[l] = n2;
        }
    }
    
    static void lpc_to_curve(final float[] arr1, final float[] arr2, final float float3, final Floor0$LookFloor0 lookFloor0, final String string, final int integer) {
        final float[] arr3 = new float[Math.max(lookFloor0.ln * 2, lookFloor0.m * 2 + 2)];
        if (float3 == 0.0f) {
            for (int i = 0; i < lookFloor0.n; ++i) {
                arr1[i] = 0.0f;
            }
            return;
        }
        lookFloor0.lpclook.lpc_to_curve(arr3, arr2, float3);
        for (int j = 0; j < lookFloor0.n; ++j) {
            arr1[j] = arr3[lookFloor0.linearmap[j]];
        }
    }
}
