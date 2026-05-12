// 
// Decompiled by Procyon v0.6.0
// 

package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;

class Residue0 extends FuncResidue
{
    private static int[][][] _01inverse_partword;
    static int[][] _2inverse_partword;
    
    @Override
    void pack(final Object object, final Buffer buffer) {
        final Residue0$InfoResidue0 residue0$InfoResidue0 = (Residue0$InfoResidue0)object;
        int n = 0;
        buffer.write(residue0$InfoResidue0.begin, 24);
        buffer.write(residue0$InfoResidue0.end, 24);
        buffer.write(residue0$InfoResidue0.grouping - 1, 24);
        buffer.write(residue0$InfoResidue0.partitions - 1, 6);
        buffer.write(residue0$InfoResidue0.groupbook, 8);
        for (int i = 0; i < residue0$InfoResidue0.partitions; ++i) {
            final int n2 = residue0$InfoResidue0.secondstages[i];
            if (Util.ilog(n2) > 3) {
                buffer.write(n2, 3);
                buffer.write(1, 1);
                buffer.write(n2 >>> 3, 5);
            }
            else {
                buffer.write(n2, 4);
            }
            n += Util.icount(n2);
        }
        for (int j = 0; j < n; ++j) {
            buffer.write(residue0$InfoResidue0.booklist[j], 8);
        }
    }
    
    @Override
    Object unpack(final Info info, final Buffer buffer) {
        int n = 0;
        final Residue0$InfoResidue0 residue0$InfoResidue0 = new Residue0$InfoResidue0(this);
        residue0$InfoResidue0.begin = buffer.read(24);
        residue0$InfoResidue0.end = buffer.read(24);
        residue0$InfoResidue0.grouping = buffer.read(24) + 1;
        residue0$InfoResidue0.partitions = buffer.read(6) + 1;
        residue0$InfoResidue0.groupbook = buffer.read(8);
        for (int i = 0; i < residue0$InfoResidue0.partitions; ++i) {
            int read = buffer.read(3);
            if (buffer.read(1) != 0) {
                read |= buffer.read(5) << 3;
            }
            residue0$InfoResidue0.secondstages[i] = read;
            n += Util.icount(read);
        }
        for (int j = 0; j < n; ++j) {
            residue0$InfoResidue0.booklist[j] = buffer.read(8);
        }
        if (residue0$InfoResidue0.groupbook >= info.books) {
            this.free_info(residue0$InfoResidue0);
            return null;
        }
        for (int k = 0; k < n; ++k) {
            if (residue0$InfoResidue0.booklist[k] >= info.books) {
                this.free_info(residue0$InfoResidue0);
                return null;
            }
        }
        return residue0$InfoResidue0;
    }
    
    @Override
    Object look(final DspState dspState, final InfoMode infoMode, final Object object) {
        final Residue0$InfoResidue0 info = (Residue0$InfoResidue0)object;
        final Residue0$LookResidue0 residue0$LookResidue0 = new Residue0$LookResidue0(this);
        int n = 0;
        int stages = 0;
        residue0$LookResidue0.info = info;
        residue0$LookResidue0.map = infoMode.mapping;
        residue0$LookResidue0.parts = info.partitions;
        residue0$LookResidue0.fullbooks = dspState.fullbooks;
        residue0$LookResidue0.phrasebook = dspState.fullbooks[info.groupbook];
        final int dim = residue0$LookResidue0.phrasebook.dim;
        residue0$LookResidue0.partbooks = new int[residue0$LookResidue0.parts][];
        for (int i = 0; i < residue0$LookResidue0.parts; ++i) {
            final int integer = info.secondstages[i];
            final int ilog = Util.ilog(integer);
            if (ilog != 0) {
                if (ilog > stages) {
                    stages = ilog;
                }
                residue0$LookResidue0.partbooks[i] = new int[ilog];
                for (int j = 0; j < ilog; ++j) {
                    if ((integer & 1 << j) != 0x0) {
                        residue0$LookResidue0.partbooks[i][j] = info.booklist[n++];
                    }
                }
            }
        }
        residue0$LookResidue0.partvals = (int)Math.rint(Math.pow(residue0$LookResidue0.parts, dim));
        residue0$LookResidue0.stages = stages;
        residue0$LookResidue0.decodemap = new int[residue0$LookResidue0.partvals][];
        for (int k = 0; k < residue0$LookResidue0.partvals; ++k) {
            int n2 = k;
            int n3 = residue0$LookResidue0.partvals / residue0$LookResidue0.parts;
            residue0$LookResidue0.decodemap[k] = new int[dim];
            for (int l = 0; l < dim; ++l) {
                final int n4 = n2 / n3;
                n2 -= n4 * n3;
                n3 /= residue0$LookResidue0.parts;
                residue0$LookResidue0.decodemap[k][l] = n4;
            }
        }
        return residue0$LookResidue0;
    }
    
    @Override
    void free_info(final Object object) {
    }
    
    @Override
    void free_look(final Object object) {
    }
    
    static synchronized int _01inverse(final Block block, final Object object, final float[][] arr, final int integer4, final int integer5) {
        final Residue0$LookResidue0 residue0$LookResidue0 = (Residue0$LookResidue0)object;
        final Residue0$InfoResidue0 info = residue0$LookResidue0.info;
        final int grouping = info.grouping;
        final int dim = residue0$LookResidue0.phrasebook.dim;
        final int n = (info.end - info.begin) / grouping;
        final int n2 = (n + dim - 1) / dim;
        if (Residue0._01inverse_partword.length < integer4) {
            Residue0._01inverse_partword = new int[integer4][][];
        }
        for (int i = 0; i < integer4; ++i) {
            if (Residue0._01inverse_partword[i] == null || Residue0._01inverse_partword[i].length < n2) {
                Residue0._01inverse_partword[i] = new int[n2][];
            }
        }
        for (int j = 0; j < residue0$LookResidue0.stages; ++j) {
            int k = 0;
            int n3 = 0;
            while (k < n) {
                if (j == 0) {
                    for (int l = 0; l < integer4; ++l) {
                        final int decode = residue0$LookResidue0.phrasebook.decode(block.opb);
                        if (decode == -1) {
                            return 0;
                        }
                        Residue0._01inverse_partword[l][n3] = residue0$LookResidue0.decodemap[decode];
                        if (Residue0._01inverse_partword[l][n3] == null) {
                            return 0;
                        }
                    }
                }
                for (int n4 = 0; n4 < dim && k < n; ++n4, ++k) {
                    for (int n5 = 0; n5 < integer4; ++n5) {
                        final int n6 = info.begin + k * grouping;
                        final int n7 = Residue0._01inverse_partword[n5][n3][n4];
                        if ((info.secondstages[n7] & 1 << j) != 0x0) {
                            final CodeBook codeBook = residue0$LookResidue0.fullbooks[residue0$LookResidue0.partbooks[n7][j]];
                            if (codeBook != null) {
                                if (integer5 == 0) {
                                    if (codeBook.decodevs_add(arr[n5], n6, block.opb, grouping) == -1) {
                                        return 0;
                                    }
                                }
                                else if (integer5 == 1 && codeBook.decodev_add(arr[n5], n6, block.opb, grouping) == -1) {
                                    return 0;
                                }
                            }
                        }
                    }
                }
                ++n3;
            }
        }
        return 0;
    }
    
    static synchronized int _2inverse(final Block block, final Object object, final float[][] arr, final int integer) {
        final Residue0$LookResidue0 residue0$LookResidue0 = (Residue0$LookResidue0)object;
        final Residue0$InfoResidue0 info = residue0$LookResidue0.info;
        final int grouping = info.grouping;
        final int dim = residue0$LookResidue0.phrasebook.dim;
        final int n = (info.end - info.begin) / grouping;
        final int n2 = (n + dim - 1) / dim;
        if (Residue0._2inverse_partword == null || Residue0._2inverse_partword.length < n2) {
            Residue0._2inverse_partword = new int[n2][];
        }
        for (int i = 0; i < residue0$LookResidue0.stages; ++i) {
            int j = 0;
            int n3 = 0;
            while (j < n) {
                if (i == 0) {
                    final int decode = residue0$LookResidue0.phrasebook.decode(block.opb);
                    if (decode == -1) {
                        return 0;
                    }
                    Residue0._2inverse_partword[n3] = residue0$LookResidue0.decodemap[decode];
                    if (Residue0._2inverse_partword[n3] == null) {
                        return 0;
                    }
                }
                for (int n4 = 0; n4 < dim && j < n; ++n4, ++j) {
                    final int integer2 = info.begin + j * grouping;
                    final int n5 = Residue0._2inverse_partword[n3][n4];
                    if ((info.secondstages[n5] & 1 << i) != 0x0) {
                        final CodeBook codeBook = residue0$LookResidue0.fullbooks[residue0$LookResidue0.partbooks[n5][i]];
                        if (codeBook != null && codeBook.decodevv_add(arr, integer2, integer, block.opb, grouping) == -1) {
                            return 0;
                        }
                    }
                }
                ++n3;
            }
        }
        return 0;
    }
    
    @Override
    int inverse(final Block block, final Object object, final float[][] arr, final int[] arr, final int integer) {
        int integer2 = 0;
        for (int i = 0; i < integer; ++i) {
            if (arr[i] != 0) {
                arr[integer2++] = arr[i];
            }
        }
        if (integer2 != 0) {
            return _01inverse(block, object, arr, integer2, 0);
        }
        return 0;
    }
    
    static {
        Residue0._01inverse_partword = new int[2][][];
        Residue0._2inverse_partword = null;
    }
}
