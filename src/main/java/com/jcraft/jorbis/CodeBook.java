// 
// Decompiled by Procyon v0.6.0
// 

package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;

class CodeBook
{
    int dim;
    int entries;
    StaticCodeBook c;
    float[] valuelist;
    int[] codelist;
    CodeBook$DecodeAux decode_tree;
    private int[] t;
    
    CodeBook() {
        this.c = new StaticCodeBook();
        this.t = new int[15];
    }
    
    int encode(final int integer, final Buffer buffer) {
        buffer.write(this.codelist[integer], this.c.lengthlist[integer]);
        return this.c.lengthlist[integer];
    }
    
    int errorv(final float[] arr) {
        final int best = this.best(arr, 1);
        for (int i = 0; i < this.dim; ++i) {
            arr[i] = this.valuelist[best * this.dim + i];
        }
        return best;
    }
    
    int encodev(final int integer, final float[] arr, final Buffer buffer) {
        for (int i = 0; i < this.dim; ++i) {
            arr[i] = this.valuelist[integer * this.dim + i];
        }
        return this.encode(integer, buffer);
    }
    
    int encodevs(final float[] arr, final Buffer buffer, final int integer3, final int integer4) {
        return this.encode(this.besterror(arr, integer3, integer4), buffer);
    }
    
    synchronized int decodevs_add(final float[] arr, final int integer2, final Buffer buffer, final int integer4) {
        final int n = integer4 / this.dim;
        if (this.t.length < n) {
            this.t = new int[n];
        }
        for (int i = 0; i < n; ++i) {
            final int decode = this.decode(buffer);
            if (decode == -1) {
                return -1;
            }
            this.t[i] = decode * this.dim;
        }
        for (int j = 0, n2 = 0; j < this.dim; ++j, n2 += n) {
            for (int k = 0; k < n; ++k) {
                final int n3 = integer2 + n2 + k;
                arr[n3] += this.valuelist[this.t[k] + j];
            }
        }
        return 0;
    }
    
    int decodev_add(final float[] arr, final int integer2, final Buffer buffer, final int integer4) {
        if (this.dim > 8) {
            int i = 0;
            while (i < integer4) {
                final int decode = this.decode(buffer);
                if (decode == -1) {
                    return -1;
                }
                int n2;
                for (int n = decode * this.dim, j = 0; j < this.dim; arr[n2] += this.valuelist[n + j++]) {
                    n2 = integer2 + i++;
                }
            }
        }
        else {
            int k = 0;
            while (k < integer4) {
                final int decode2 = this.decode(buffer);
                if (decode2 == -1) {
                    return -1;
                }
                final int n3 = decode2 * this.dim;
                int n4 = 0;
                switch (this.dim) {
                    case 8: {
                        final int n5 = integer2 + k++;
                        arr[n5] += this.valuelist[n3 + n4++];
                    }
                    case 7: {
                        final int n6 = integer2 + k++;
                        arr[n6] += this.valuelist[n3 + n4++];
                    }
                    case 6: {
                        final int n7 = integer2 + k++;
                        arr[n7] += this.valuelist[n3 + n4++];
                    }
                    case 5: {
                        final int n8 = integer2 + k++;
                        arr[n8] += this.valuelist[n3 + n4++];
                    }
                    case 4: {
                        final int n9 = integer2 + k++;
                        arr[n9] += this.valuelist[n3 + n4++];
                    }
                    case 3: {
                        final int n10 = integer2 + k++;
                        arr[n10] += this.valuelist[n3 + n4++];
                    }
                    case 2: {
                        final int n11 = integer2 + k++;
                        arr[n11] += this.valuelist[n3 + n4++];
                    }
                    case 1: {
                        final int n12 = integer2 + k++;
                        arr[n12] += this.valuelist[n3 + n4++];
                        continue;
                    }
                }
            }
        }
        return 0;
    }
    
    int decodev_set(final float[] arr, final int integer2, final Buffer buffer, final int integer4) {
        int i = 0;
        while (i < integer4) {
            final int decode = this.decode(buffer);
            if (decode == -1) {
                return -1;
            }
            for (int n = decode * this.dim, j = 0; j < this.dim; arr[integer2 + i++] = this.valuelist[n + j++]) {}
        }
        return 0;
    }
    
    int decodevv_add(final float[][] arr, final int integer2, final int integer3, final Buffer buffer, final int integer5) {
        int n = 0;
        int i = integer2 / integer3;
        while (i < (integer2 + integer5) / integer3) {
            final int decode = this.decode(buffer);
            if (decode == -1) {
                return -1;
            }
            final int n2 = decode * this.dim;
            for (int j = 0; j < this.dim; ++j) {
                final float[] array = arr[n++];
                final int n3 = i;
                array[n3] += this.valuelist[n2 + j];
                if (n == integer3) {
                    n = 0;
                    ++i;
                }
            }
        }
        return 0;
    }
    
    int decode(final Buffer buffer) {
        int i = 0;
        final CodeBook$DecodeAux decode_tree = this.decode_tree;
        final int look = buffer.look(decode_tree.tabn);
        if (look >= 0) {
            i = decode_tree.tab[look];
            buffer.adv(decode_tree.tabl[look]);
            if (i <= 0) {
                return -i;
            }
        }
        do {
            switch (buffer.read1()) {
                case 0: {
                    i = decode_tree.ptr0[i];
                    continue;
                }
                case 1: {
                    i = decode_tree.ptr1[i];
                    continue;
                }
                default: {
                    return -1;
                }
            }
        } while (i > 0);
        return -i;
    }
    
    int decodevs(final float[] arr, final int integer2, final Buffer buffer, final int integer4, final int integer5) {
        final int decode = this.decode(buffer);
        if (decode == -1) {
            return -1;
        }
        switch (integer5) {
            case -1: {
                for (int i = 0, n = 0; i < this.dim; ++i, n += integer4) {
                    arr[integer2 + n] = this.valuelist[decode * this.dim + i];
                }
                break;
            }
            case 0: {
                for (int j = 0, n2 = 0; j < this.dim; ++j, n2 += integer4) {
                    final int n3 = integer2 + n2;
                    arr[n3] += this.valuelist[decode * this.dim + j];
                }
                break;
            }
            case 1: {
                for (int k = 0, n4 = 0; k < this.dim; ++k, n4 += integer4) {
                    final int n5 = integer2 + n4;
                    arr[n5] *= this.valuelist[decode * this.dim + k];
                }
                break;
            }
        }
        return decode;
    }
    
    int best(final float[] arr, final int integer) {
        int n = -1;
        float n2 = 0.0f;
        int integer2 = 0;
        for (int i = 0; i < this.entries; ++i) {
            if (this.c.lengthlist[i] > 0) {
                final float dist = dist(this.dim, this.valuelist, integer2, arr, integer);
                if (n == -1 || dist < n2) {
                    n2 = dist;
                    n = i;
                }
            }
            integer2 += this.dim;
        }
        return n;
    }
    
    int besterror(final float[] arr, final int integer2, final int integer3) {
        final int best = this.best(arr, integer2);
        switch (integer3) {
            case 0: {
                for (int i = 0, n = 0; i < this.dim; ++i, n += integer2) {
                    final int n2 = n;
                    arr[n2] -= this.valuelist[best * this.dim + i];
                }
                break;
            }
            case 1: {
                for (int j = 0, n3 = 0; j < this.dim; ++j, n3 += integer2) {
                    final float n4 = this.valuelist[best * this.dim + j];
                    if (n4 == 0.0f) {
                        arr[n3] = 0.0f;
                    }
                    else {
                        final int n5 = n3;
                        arr[n5] /= n4;
                    }
                }
                break;
            }
        }
        return best;
    }
    
    void clear() {
    }
    
    private static float dist(final int integer1, final float[] arr2, final int integer3, final float[] arr4, final int integer5) {
        float n = 0.0f;
        for (int i = 0; i < integer1; ++i) {
            final float n2 = arr2[integer3 + i] - arr4[i * integer5];
            n += n2 * n2;
        }
        return n;
    }
    
    int init_decode(final StaticCodeBook staticCodeBook) {
        this.c = staticCodeBook;
        this.entries = staticCodeBook.entries;
        this.dim = staticCodeBook.dim;
        this.valuelist = staticCodeBook.unquantize();
        this.decode_tree = this.make_decode_tree();
        if (this.decode_tree == null) {
            this.clear();
            return -1;
        }
        return 0;
    }
    
    static int[] make_words(final int[] arr, final int integer) {
        final int[] array = new int[33];
        final int[] array2 = new int[integer];
        for (int i = 0; i < integer; ++i) {
            final int n = arr[i];
            if (n > 0) {
                int n2 = array[n];
                if (n < 32 && n2 >>> n != 0) {
                    return null;
                }
                array2[i] = n2;
                int j = n;
                while (j > 0) {
                    if ((array[j] & 0x1) != 0x0) {
                        if (j == 1) {
                            final int[] array3 = array;
                            final int n3 = 1;
                            ++array3[n3];
                            break;
                        }
                        array[j] = array[j - 1] << 1;
                        break;
                    }
                    else {
                        final int[] array4 = array;
                        final int n4 = j;
                        ++array4[n4];
                        --j;
                    }
                }
                for (int n5 = n + 1; n5 < 33 && array[n5] >>> 1 == n2; n2 = array[n5], array[n5] = array[n5 - 1] << 1, ++n5) {}
            }
        }
        for (int k = 0; k < integer; ++k) {
            int n6 = 0;
            for (int l = 0; l < arr[k]; ++l) {
                n6 = (n6 << 1 | (array2[k] >>> l & 0x1));
            }
            array2[k] = n6;
        }
        return array2;
    }
    
    CodeBook$DecodeAux make_decode_tree() {
        int n = 0;
        final CodeBook$DecodeAux codeBook$DecodeAux2;
        final CodeBook$DecodeAux codeBook$DecodeAux = codeBook$DecodeAux2 = new CodeBook$DecodeAux(this);
        final int[] ptr0 = new int[this.entries * 2];
        codeBook$DecodeAux2.ptr0 = ptr0;
        final int[] array = ptr0;
        final CodeBook$DecodeAux codeBook$DecodeAux3 = codeBook$DecodeAux;
        final int[] ptr2 = new int[this.entries * 2];
        codeBook$DecodeAux3.ptr1 = ptr2;
        final int[] array2 = ptr2;
        final int[] make_words = make_words(this.c.lengthlist, this.c.entries);
        if (make_words == null) {
            return null;
        }
        codeBook$DecodeAux.aux = this.entries * 2;
        for (int i = 0; i < this.entries; ++i) {
            if (this.c.lengthlist[i] > 0) {
                int n2 = 0;
                int j;
                for (j = 0; j < this.c.lengthlist[i] - 1; ++j) {
                    if ((make_words[i] >>> j & 0x1) == 0x0) {
                        if (array[n2] == 0) {
                            array[n2] = ++n;
                        }
                        n2 = array[n2];
                    }
                    else {
                        if (array2[n2] == 0) {
                            array2[n2] = ++n;
                        }
                        n2 = array2[n2];
                    }
                }
                if ((make_words[i] >>> j & 0x1) == 0x0) {
                    array[n2] = -i;
                }
                else {
                    array2[n2] = -i;
                }
            }
        }
        codeBook$DecodeAux.tabn = Util.ilog(this.entries) - 4;
        if (codeBook$DecodeAux.tabn < 5) {
            codeBook$DecodeAux.tabn = 5;
        }
        final int n3 = 1 << codeBook$DecodeAux.tabn;
        codeBook$DecodeAux.tab = new int[n3];
        codeBook$DecodeAux.tabl = new int[n3];
        for (int k = 0; k < n3; ++k) {
            int n4;
            int n5;
            for (n4 = 0, n5 = 0; n5 < codeBook$DecodeAux.tabn && (n4 > 0 || n5 == 0); ++n5) {
                if ((k & 1 << n5) != 0x0) {
                    n4 = array2[n4];
                }
                else {
                    n4 = array[n4];
                }
            }
            codeBook$DecodeAux.tab[k] = n4;
            codeBook$DecodeAux.tabl[k] = n5;
        }
        return codeBook$DecodeAux;
    }
}
