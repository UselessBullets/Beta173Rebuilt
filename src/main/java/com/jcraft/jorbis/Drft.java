// 
// Decompiled by Procyon v0.6.0
// 

package com.jcraft.jorbis;

class Drft
{
    int n;
    float[] trigcache;
    int[] splitcache;
    static int[] ntryh;
    static float tpi;
    static float hsqt2;
    static float taui;
    static float taur;
    static float sqrt2;
    
    void backward(final float[] arr) {
        if (this.n == 1) {
            return;
        }
        drftb1(this.n, arr, this.trigcache, this.trigcache, this.n, this.splitcache);
    }
    
    void init(final int integer) {
        this.n = integer;
        this.trigcache = new float[3 * integer];
        this.splitcache = new int[32];
        fdrffti(integer, this.trigcache, this.splitcache);
    }
    
    void clear() {
        if (this.trigcache != null) {
            this.trigcache = null;
        }
        if (this.splitcache != null) {
            this.splitcache = null;
        }
    }
    
    static void drfti1(final int integer1, final float[] arr, final int integer3, final int[] arr) {
        int n = 0;
        int n2 = -1;
        int n3 = integer1;
        int n4 = 0;
        int n5 = 101;
    Label_0197:
        while (true) {
            switch (n5) {
                case 101:
                    Label_0075: {
                        if (++n2 < 4) {
                            n = Drft.ntryh[n2];
                            break Label_0075;
                        }
                        n += 2;
                        break Label_0075;
                    }
                case 104: {
                    final int n6 = n3 / n;
                    if (n3 - n * n6 != 0) {
                        n5 = 101;
                        continue;
                    }
                    ++n4;
                    arr[n4 + 1] = n;
                    n3 = n6;
                    if (n != 2) {
                        n5 = 107;
                        continue;
                    }
                    if (n4 == 1) {
                        n5 = 107;
                        continue;
                    }
                    for (int i = 1; i < n4; ++i) {
                        final int n7 = n4 - i + 1;
                        arr[n7 + 1] = arr[n7];
                    }
                    arr[2] = 2;
                }
                case 107: {
                    if (n3 != 1) {
                        n5 = 104;
                        continue;
                    }
                    break Label_0197;
                }
            }
        }
        arr[0] = integer1;
        arr[1] = n4;
        final float n8 = Drft.tpi / integer1;
        int n9 = 0;
        final int n10 = n4 - 1;
        int n11 = 1;
        if (n10 == 0) {
            return;
        }
        for (int j = 0; j < n10; ++j) {
            final int n12 = arr[j + 2];
            int n13 = 0;
            final int n14 = n11 * n12;
            final int n15 = integer1 / n14;
            for (int n16 = n12 - 1, k = 0; k < n16; ++k) {
                n13 += n11;
                int n17 = n9;
                final float n18 = n13 * n8;
                float n19 = 0.0f;
                for (int l = 2; l < n15; l += 2) {
                    ++n19;
                    final float n20 = n19 * n18;
                    arr[integer3 + n17++] = (float)Math.cos(n20);
                    arr[integer3 + n17++] = (float)Math.sin(n20);
                }
                n9 += n15;
            }
            n11 = n14;
        }
    }
    
    static void fdrffti(final int integer, final float[] arr, final int[] arr) {
        if (integer == 1) {
            return;
        }
        drfti1(integer, arr, integer, arr);
    }
    
    static void dradf2(final int integer1, final int integer2, final float[] arr3, final float[] arr4, final float[] arr5, final int integer6) {
        int n = 0;
        final int n3;
        int n2 = n3 = integer2 * integer1;
        final int n4 = integer1 << 1;
        for (int i = 0; i < integer2; ++i) {
            arr4[n << 1] = arr3[n] + arr3[n2];
            arr4[(n << 1) + n4 - 1] = arr3[n] - arr3[n2];
            n += integer1;
            n2 += integer1;
        }
        if (integer1 < 2) {
            return;
        }
        if (integer1 != 2) {
            int n5 = 0;
            int n6 = n3;
            for (int j = 0; j < integer2; ++j) {
                int n7 = n6;
                int n8 = (n5 << 1) + (integer1 << 1);
                int n9 = n5;
                int n10 = n5 + n5;
                for (int k = 2; k < integer1; k += 2) {
                    n7 += 2;
                    n8 -= 2;
                    n9 += 2;
                    n10 += 2;
                    final float n11 = arr5[integer6 + k - 2] * arr3[n7 - 1] + arr5[integer6 + k - 1] * arr3[n7];
                    final float n12 = arr5[integer6 + k - 2] * arr3[n7] - arr5[integer6 + k - 1] * arr3[n7 - 1];
                    arr4[n10] = arr3[n9] + n12;
                    arr4[n8] = n12 - arr3[n9];
                    arr4[n10 - 1] = arr3[n9 - 1] + n11;
                    arr4[n8 - 1] = arr3[n9 - 1] - n11;
                }
                n5 += integer1;
                n6 += integer1;
            }
            if (integer1 % 2 == 1) {
                return;
            }
        }
        int n13 = integer1;
        int n15;
        int n14 = (n15 = integer1 - 1) + n3;
        for (int l = 0; l < integer2; ++l) {
            arr4[n13] = -arr3[n14];
            arr4[n13 - 1] = arr3[n15];
            n13 += integer1 << 1;
            n14 += integer1;
            n15 += integer1;
        }
    }
    
    static void dradf4(final int integer1, final int integer2, final float[] arr3, final float[] arr4, final float[] arr5, final int integer6, final float[] arr7, final int integer8, final float[] arr9, final int integer10) {
        int n2;
        final int n = n2 = integer2 * integer1;
        int n3 = n2 << 1;
        int n4 = n2 + (n2 << 1);
        int n5 = 0;
        for (int i = 0; i < integer2; ++i) {
            final float n6 = arr3[n2] + arr3[n4];
            final float n7 = arr3[n5] + arr3[n3];
            final int n8;
            arr4[n8 = n5 << 2] = n6 + n7;
            arr4[(integer1 << 2) + n8 - 1] = n7 - n6;
            final int n9;
            arr4[(n9 = n8 + (integer1 << 1)) - 1] = arr3[n5] - arr3[n3];
            arr4[n9] = arr3[n4] - arr3[n2];
            n2 += integer1;
            n4 += integer1;
            n5 += integer1;
            n3 += integer1;
        }
        if (integer1 < 2) {
            return;
        }
        if (integer1 != 2) {
            int n10 = 0;
            for (int j = 0; j < integer2; ++j) {
                int n11 = n10;
                int n12 = n10 << 2;
                final int n14;
                int n13 = (n14 = integer1 << 1) + n12;
                for (int k = 2; k < integer1; k += 2) {
                    n11 += 2;
                    final int n15 = n11;
                    n12 += 2;
                    n13 -= 2;
                    final int n16 = n15 + n;
                    final float n17 = arr5[integer6 + k - 2] * arr3[n16 - 1] + arr5[integer6 + k - 1] * arr3[n16];
                    final float n18 = arr5[integer6 + k - 2] * arr3[n16] - arr5[integer6 + k - 1] * arr3[n16 - 1];
                    final int n19 = n16 + n;
                    final float n20 = arr7[integer8 + k - 2] * arr3[n19 - 1] + arr7[integer8 + k - 1] * arr3[n19];
                    final float n21 = arr7[integer8 + k - 2] * arr3[n19] - arr7[integer8 + k - 1] * arr3[n19 - 1];
                    final int n22 = n19 + n;
                    final float n23 = arr9[integer10 + k - 2] * arr3[n22 - 1] + arr9[integer10 + k - 1] * arr3[n22];
                    final float n24 = arr9[integer10 + k - 2] * arr3[n22] - arr9[integer10 + k - 1] * arr3[n22 - 1];
                    final float n25 = n17 + n23;
                    final float n26 = n23 - n17;
                    final float n27 = n18 + n24;
                    final float n28 = n18 - n24;
                    final float n29 = arr3[n11] + n21;
                    final float n30 = arr3[n11] - n21;
                    final float n31 = arr3[n11 - 1] + n20;
                    final float n32 = arr3[n11 - 1] - n20;
                    arr4[n12 - 1] = n25 + n31;
                    arr4[n12] = n27 + n29;
                    arr4[n13 - 1] = n32 - n28;
                    arr4[n13] = n26 - n30;
                    arr4[n12 + n14 - 1] = n28 + n32;
                    arr4[n12 + n14] = n26 + n30;
                    arr4[n13 + n14 - 1] = n31 - n25;
                    arr4[n13 + n14] = n27 - n29;
                }
                n10 += integer1;
            }
            if ((integer1 & 0x1) != 0x0) {
                return;
            }
        }
        int n34;
        int n33 = (n34 = n + integer1 - 1) + (n << 1);
        final int n35 = integer1 << 2;
        int n36 = integer1;
        final int n37 = integer1 << 1;
        int n38 = integer1;
        for (int l = 0; l < integer2; ++l) {
            final float n39 = -Drft.hsqt2 * (arr3[n34] + arr3[n33]);
            final float n40 = Drft.hsqt2 * (arr3[n34] - arr3[n33]);
            arr4[n36 - 1] = n40 + arr3[n38 - 1];
            arr4[n36 + n37 - 1] = arr3[n38 - 1] - n40;
            arr4[n36] = n39 - arr3[n34 + n];
            arr4[n36 + n37] = n39 + arr3[n34 + n];
            n34 += integer1;
            n33 += integer1;
            n36 += n35;
            n38 += integer1;
        }
    }
    
    static void dradfg(final int integer1, final int integer2, final int integer3, final int integer4, final float[] arr5, final float[] arr6, final float[] arr7, final float[] arr8, final float[] arr9, final float[] arr10, final int integer11) {
        int n = 0;
        final float n2 = Drft.tpi / integer2;
        final float n3 = (float)Math.cos(n2);
        final float n4 = (float)Math.sin(n2);
        final int n5 = integer2 + 1 >> 1;
        final int n6 = integer1 - 1 >> 1;
        final int n7 = integer3 * integer1;
        final int n8 = integer2 * integer1;
        int n9 = 100;
        while (true) {
            int n10;
            int n11;
            int n12;
            int n13;
            int n14;
            int n16;
            int n17;
            int n20;
            int n21;
            int n23;
            int n25;
            int n26;
            int n28;
            int n29;
            int n31;
            int n32;
            int n35;
            int n36;
            int n38;
            int n39;
            switch (n9) {
                case 101:
                    Label_0917: {
                        if (integer1 == 1) {
                            n9 = 119;
                            continue;
                        }
                        for (int i = 0; i < integer4; ++i) {
                            arr9[i] = arr7[i];
                        }
                        n10 = 0;
                        for (int j = 1; j < integer2; ++j) {
                            n10 = (n11 = n10 + n7);
                            for (int k = 0; k < integer3; ++k) {
                                arr8[n11] = arr6[n11];
                                n11 += integer1;
                            }
                        }
                        n12 = -integer1;
                        n13 = 0;
                        if (n6 > integer3) {
                            for (int l = 1; l < integer2; ++l) {
                                n13 += n7;
                                n12 += integer1;
                                n14 = -integer1 + n13;
                                for (int n15 = 0; n15 < integer3; ++n15) {
                                    n16 = n12 - 1;
                                    n14 = (n17 = n14 + integer1);
                                    for (int n18 = 2; n18 < integer1; n18 += 2) {
                                        n16 += 2;
                                        n17 += 2;
                                        arr8[n17 - 1] = arr10[integer11 + n16 - 1] * arr6[n17 - 1] + arr10[integer11 + n16] * arr6[n17];
                                        arr8[n17] = arr10[integer11 + n16 - 1] * arr6[n17] - arr10[integer11 + n16] * arr6[n17 - 1];
                                    }
                                }
                            }
                        }
                        else {
                            for (int n19 = 1; n19 < integer2; ++n19) {
                                n12 += integer1;
                                n20 = n12 - 1;
                                n13 = (n21 = n13 + n7);
                                for (int n22 = 2; n22 < integer1; n22 += 2) {
                                    n20 += 2;
                                    n21 += 2;
                                    n23 = n21;
                                    for (int n24 = 0; n24 < integer3; ++n24) {
                                        arr8[n23 - 1] = arr10[integer11 + n20 - 1] * arr6[n23 - 1] + arr10[integer11 + n20] * arr6[n23];
                                        arr8[n23] = arr10[integer11 + n20 - 1] * arr6[n23] - arr10[integer11 + n20] * arr6[n23 - 1];
                                        n23 += integer1;
                                    }
                                }
                            }
                        }
                        n25 = 0;
                        n26 = integer2 * n7;
                        if (n6 < integer3) {
                            for (int n27 = 1; n27 < n5; ++n27) {
                                n25 += n7;
                                n26 -= n7;
                                n28 = n25;
                                n29 = n26;
                                for (int n30 = 2; n30 < integer1; n30 += 2) {
                                    n28 += 2;
                                    n29 += 2;
                                    n31 = n28 - integer1;
                                    n32 = n29 - integer1;
                                    for (int n33 = 0; n33 < integer3; ++n33) {
                                        n31 += integer1;
                                        n32 += integer1;
                                        arr6[n31 - 1] = arr8[n31 - 1] + arr8[n32 - 1];
                                        arr6[n32 - 1] = arr8[n31] - arr8[n32];
                                        arr6[n31] = arr8[n31] + arr8[n32];
                                        arr6[n32] = arr8[n32 - 1] - arr8[n31 - 1];
                                    }
                                }
                            }
                            break Label_0917;
                        }
                        for (int n34 = 1; n34 < n5; ++n34) {
                            n25 += n7;
                            n26 -= n7;
                            n35 = n25;
                            n36 = n26;
                            for (int n37 = 0; n37 < integer3; ++n37) {
                                n38 = n35;
                                n39 = n36;
                                for (int n40 = 2; n40 < integer1; n40 += 2) {
                                    n38 += 2;
                                    n39 += 2;
                                    arr6[n38 - 1] = arr8[n38 - 1] + arr8[n39 - 1];
                                    arr6[n39 - 1] = arr8[n38] - arr8[n39];
                                    arr6[n38] = arr8[n38] + arr8[n39];
                                    arr6[n39] = arr8[n39 - 1] - arr8[n38 - 1];
                                }
                                n35 += integer1;
                                n36 += integer1;
                            }
                        }
                        break Label_0917;
                    }
                case 119: {
                    for (int n41 = 0; n41 < integer4; ++n41) {
                        arr7[n41] = arr9[n41];
                    }
                    int n42 = 0;
                    int n43 = integer2 * integer4;
                    for (int n44 = 1; n44 < n5; ++n44) {
                        n42 += n7;
                        n43 -= n7;
                        int n45 = n42 - integer1;
                        int n46 = n43 - integer1;
                        for (int n47 = 0; n47 < integer3; ++n47) {
                            n45 += integer1;
                            n46 += integer1;
                            arr6[n45] = arr8[n45] + arr8[n46];
                            arr6[n46] = arr8[n46] - arr8[n45];
                        }
                    }
                    float n48 = 1.0f;
                    float n49 = 0.0f;
                    int n50 = 0;
                    n = integer2 * integer4;
                    final int n51 = (integer2 - 1) * integer4;
                    for (int n52 = 1; n52 < n5; ++n52) {
                        n50 += integer4;
                        n -= integer4;
                        final float n53 = n3 * n48 - n4 * n49;
                        n49 = n3 * n49 + n4 * n48;
                        n48 = n53;
                        int n54 = n50;
                        int n55 = n;
                        int n56 = n51;
                        int n57 = integer4;
                        for (int n58 = 0; n58 < integer4; ++n58) {
                            arr9[n54++] = arr7[n58] + n48 * arr7[n57++];
                            arr9[n55++] = n49 * arr7[n56++];
                        }
                        final float n59 = n48;
                        final float n60 = n49;
                        float n61 = n48;
                        float n62 = n49;
                        int n63 = integer4;
                        int n64 = (integer2 - 1) * integer4;
                        for (int n65 = 2; n65 < n5; ++n65) {
                            n63 += integer4;
                            n64 -= integer4;
                            final float n66 = n59 * n61 - n60 * n62;
                            n62 = n59 * n62 + n60 * n61;
                            n61 = n66;
                            int n67 = n50;
                            int n68 = n;
                            int n69 = n63;
                            int n70 = n64;
                            for (int n71 = 0; n71 < integer4; ++n71) {
                                final int n72 = n67++;
                                arr9[n72] += n61 * arr7[n69++];
                                final int n73 = n68++;
                                arr9[n73] += n62 * arr7[n70++];
                            }
                        }
                    }
                    int n74 = 0;
                    for (int n75 = 1; n75 < n5; ++n75) {
                        n74 = (n = n74 + integer4);
                        for (int n76 = 0; n76 < integer4; ++n76) {
                            final int n77 = n76;
                            arr9[n77] += arr7[n++];
                        }
                    }
                    if (integer1 < integer3) {
                        n9 = 132;
                        continue;
                    }
                    int n78 = 0;
                    n = 0;
                    for (int n79 = 0; n79 < integer3; ++n79) {
                        int n80 = n78;
                        int n81 = n;
                        for (int n82 = 0; n82 < integer1; ++n82) {
                            arr5[n81++] = arr8[n80++];
                        }
                        n78 += integer1;
                        n += n8;
                    }
                    n9 = 135;
                    continue;
                }
                case 132: {
                    for (int n83 = 0; n83 < integer1; ++n83) {
                        int n84 = n83;
                        int n85 = n83;
                        for (int n86 = 0; n86 < integer3; ++n86) {
                            arr5[n85] = arr8[n84];
                            n84 += integer1;
                            n85 += n8;
                        }
                    }
                }
                case 135: {
                    int n87 = 0;
                    n = integer1 << 1;
                    int n88 = 0;
                    int n89 = integer2 * n7;
                    for (int n90 = 1; n90 < n5; ++n90) {
                        n87 += n;
                        n88 += n7;
                        n89 -= n7;
                        int n91 = n87;
                        int n92 = n88;
                        int n93 = n89;
                        for (int n94 = 0; n94 < integer3; ++n94) {
                            arr5[n91 - 1] = arr8[n92];
                            arr5[n91] = arr8[n93];
                            n91 += n8;
                            n92 += integer1;
                            n93 += integer1;
                        }
                    }
                    if (integer1 == 1) {
                        return;
                    }
                    if (n6 < integer3) {
                        n9 = 141;
                        continue;
                    }
                    int n95 = -integer1;
                    int n96 = 0;
                    int n97 = 0;
                    int n98 = integer2 * n7;
                    for (int n99 = 1; n99 < n5; ++n99) {
                        n95 += n;
                        n96 += n;
                        n97 += n7;
                        n98 -= n7;
                        int n100 = n95;
                        int n101 = n96;
                        int n102 = n97;
                        int n103 = n98;
                        for (int n104 = 0; n104 < integer3; ++n104) {
                            for (int n105 = 2; n105 < integer1; n105 += 2) {
                                final int n106 = integer1 - n105;
                                arr5[n105 + n101 - 1] = arr8[n105 + n102 - 1] + arr8[n105 + n103 - 1];
                                arr5[n106 + n100 - 1] = arr8[n105 + n102 - 1] - arr8[n105 + n103 - 1];
                                arr5[n105 + n101] = arr8[n105 + n102] + arr8[n105 + n103];
                                arr5[n106 + n100] = arr8[n105 + n103] - arr8[n105 + n102];
                            }
                            n100 += n8;
                            n101 += n8;
                            n102 += integer1;
                            n103 += integer1;
                        }
                    }
                    return;
                }
                case 141: {
                    int n107 = -integer1;
                    int n108 = 0;
                    int n109 = 0;
                    int n110 = integer2 * n7;
                    for (int n111 = 1; n111 < n5; ++n111) {
                        n107 += n;
                        n108 += n;
                        n109 += n7;
                        n110 -= n7;
                        for (int n112 = 2; n112 < integer1; n112 += 2) {
                            int n113 = integer1 + n107 - n112;
                            int n114 = n112 + n108;
                            int n115 = n112 + n109;
                            int n116 = n112 + n110;
                            for (int n117 = 0; n117 < integer3; ++n117) {
                                arr5[n114 - 1] = arr8[n115 - 1] + arr8[n116 - 1];
                                arr5[n113 - 1] = arr8[n115 - 1] - arr8[n116 - 1];
                                arr5[n114] = arr8[n115] + arr8[n116];
                                arr5[n113] = arr8[n116] - arr8[n115];
                                n113 += n8;
                                n114 += n8;
                                n115 += integer1;
                                n116 += integer1;
                            }
                        }
                    }
                }
            }
        }
    }
    
    static void drftf1(final int integer, final float[] arr2, final float[] arr3, final float[] arr4, final int[] arr) {
        final int n = arr[1];
        int n2 = 1;
        int n3 = integer;
        int n4 = integer;
        for (int i = 0; i < n; ++i) {
            final int n5 = arr[n - i + 1];
            final int n6 = n3 / n5;
            final int n7 = integer / n3;
            final int n8 = n7 * n6;
            n4 -= (n5 - 1) * n7;
            n2 = 1 - n2;
            int n9 = 100;
        Label_0373:
            while (true) {
                switch (n9) {
                    case 100: {
                        if (n5 != 4) {
                            n9 = 102;
                            continue;
                        }
                        final int n10 = n4 + n7;
                        final int n11 = n10 + n7;
                        if (n2 != 0) {
                            dradf4(n7, n6, arr3, arr2, arr4, n4 - 1, arr4, n10 - 1, arr4, n11 - 1);
                        }
                        else {
                            dradf4(n7, n6, arr2, arr3, arr4, n4 - 1, arr4, n10 - 1, arr4, n11 - 1);
                        }
                        n9 = 110;
                        continue;
                    }
                    case 102: {
                        if (n5 != 2) {
                            n9 = 104;
                            continue;
                        }
                        if (n2 != 0) {
                            n9 = 103;
                            continue;
                        }
                        dradf2(n7, n6, arr2, arr3, arr4, n4 - 1);
                        n9 = 110;
                        continue;
                    }
                    case 103: {
                        dradf2(n7, n6, arr3, arr2, arr4, n4 - 1);
                    }
                    case 104: {
                        if (n7 == 1) {
                            n2 = 1 - n2;
                        }
                        if (n2 != 0) {
                            n9 = 109;
                            continue;
                        }
                        dradfg(n7, n5, n6, n8, arr2, arr2, arr2, arr3, arr3, arr4, n4 - 1);
                        n2 = 1;
                        n9 = 110;
                        continue;
                    }
                    case 109: {
                        dradfg(n7, n5, n6, n8, arr3, arr3, arr3, arr2, arr2, arr4, n4 - 1);
                        n2 = 0;
                    }
                    case 110: {
                        break Label_0373;
                    }
                }
            }
            n3 = n6;
        }
        if (n2 == 1) {
            return;
        }
        for (int j = 0; j < integer; ++j) {
            arr2[j] = arr3[j];
        }
    }
    
    static void dradb2(final int integer1, final int integer2, final float[] arr3, final float[] arr4, final float[] arr5, final int integer6) {
        final int n = integer2 * integer1;
        int n2 = 0;
        int n3 = 0;
        final int n4 = (integer1 << 1) - 1;
        for (int i = 0; i < integer2; ++i) {
            arr4[n2] = arr3[n3] + arr3[n4 + n3];
            arr4[n2 + n] = arr3[n3] - arr3[n4 + n3];
            n3 = (n2 += integer1) << 1;
        }
        if (integer1 < 2) {
            return;
        }
        if (integer1 != 2) {
            int n5 = 0;
            int n6 = 0;
            for (int j = 0; j < integer2; ++j) {
                int n7 = n5;
                int n9;
                int n8 = (n9 = n6) + (integer1 << 1);
                int n10 = n + n5;
                for (int k = 2; k < integer1; k += 2) {
                    n7 += 2;
                    n9 += 2;
                    n8 -= 2;
                    n10 += 2;
                    arr4[n7 - 1] = arr3[n9 - 1] + arr3[n8 - 1];
                    final float n11 = arr3[n9 - 1] - arr3[n8 - 1];
                    arr4[n7] = arr3[n9] - arr3[n8];
                    final float n12 = arr3[n9] + arr3[n8];
                    arr4[n10 - 1] = arr5[integer6 + k - 2] * n11 - arr5[integer6 + k - 1] * n12;
                    arr4[n10] = arr5[integer6 + k - 2] * n12 + arr5[integer6 + k - 1] * n11;
                }
                n6 = (n5 += integer1) << 1;
            }
            if (integer1 % 2 == 1) {
                return;
            }
        }
        int n13 = integer1 - 1;
        int n14 = integer1 - 1;
        for (int l = 0; l < integer2; ++l) {
            arr4[n13] = arr3[n14] + arr3[n14];
            arr4[n13 + n] = -(arr3[n14 + 1] + arr3[n14 + 1]);
            n13 += integer1;
            n14 += integer1 << 1;
        }
    }
    
    static void dradb3(final int integer1, final int integer2, final float[] arr3, final float[] arr4, final float[] arr5, final int integer6, final float[] arr7, final int integer8) {
        final int n = integer2 * integer1;
        int n2 = 0;
        final int n3 = n << 1;
        int n4 = integer1 << 1;
        final int n5 = integer1 + (integer1 << 1);
        int n6 = 0;
        for (int i = 0; i < integer2; ++i) {
            final float n7 = arr3[n4 - 1] + arr3[n4 - 1];
            final float n8 = arr3[n6] + Drft.taur * n7;
            arr4[n2] = arr3[n6] + n7;
            final float n9 = Drft.taui * (arr3[n4] + arr3[n4]);
            arr4[n2 + n] = n8 - n9;
            arr4[n2 + n3] = n8 + n9;
            n2 += integer1;
            n4 += n5;
            n6 += n5;
        }
        if (integer1 == 1) {
            return;
        }
        int n10 = 0;
        final int n11 = integer1 << 1;
        for (int j = 0; j < integer2; ++j) {
            int n12 = n10 + (n10 << 1);
            int n14;
            int n13 = n14 = n12 + n11;
            int n15 = n10;
            int n17;
            int n16 = (n17 = n10 + n) + n;
            for (int k = 2; k < integer1; k += 2) {
                n13 += 2;
                n14 -= 2;
                n12 += 2;
                n15 += 2;
                n17 += 2;
                n16 += 2;
                final float n18 = arr3[n13 - 1] + arr3[n14 - 1];
                final float n19 = arr3[n12 - 1] + Drft.taur * n18;
                arr4[n15 - 1] = arr3[n12 - 1] + n18;
                final float n20 = arr3[n13] - arr3[n14];
                final float n21 = arr3[n12] + Drft.taur * n20;
                arr4[n15] = arr3[n12] + n20;
                final float n22 = Drft.taui * (arr3[n13 - 1] - arr3[n14 - 1]);
                final float n23 = Drft.taui * (arr3[n13] + arr3[n14]);
                final float n24 = n19 - n23;
                final float n25 = n19 + n23;
                final float n26 = n21 + n22;
                final float n27 = n21 - n22;
                arr4[n17 - 1] = arr5[integer6 + k - 2] * n24 - arr5[integer6 + k - 1] * n26;
                arr4[n17] = arr5[integer6 + k - 2] * n26 + arr5[integer6 + k - 1] * n24;
                arr4[n16 - 1] = arr7[integer8 + k - 2] * n25 - arr7[integer8 + k - 1] * n27;
                arr4[n16] = arr7[integer8 + k - 2] * n27 + arr7[integer8 + k - 1] * n25;
            }
            n10 += integer1;
        }
    }
    
    static void dradb4(final int integer1, final int integer2, final float[] arr3, final float[] arr4, final float[] arr5, final int integer6, final float[] arr7, final int integer8, final float[] arr9, final int integer10) {
        final int n = integer2 * integer1;
        int n2 = 0;
        final int n3 = integer1 << 2;
        int n4 = 0;
        final int n5 = integer1 << 1;
        for (int i = 0; i < integer2; ++i) {
            final int n6 = n4 + n5;
            final int n7 = n2;
            final float n8 = arr3[n6 - 1] + arr3[n6 - 1];
            final float n9 = arr3[n6] + arr3[n6];
            final int n11;
            final float n10 = arr3[n4] - arr3[(n11 = n6 + n5) - 1];
            final float n12 = arr3[n4] + arr3[n11 - 1];
            arr4[n7] = n12 + n8;
            final int n13;
            arr4[n13 = n7 + n] = n10 - n9;
            final int n14;
            arr4[n14 = n13 + n] = n12 - n8;
            arr4[n14 + n] = n10 + n9;
            n2 += integer1;
            n4 += n3;
        }
        if (integer1 < 2) {
            return;
        }
        if (integer1 != 2) {
            int n15 = 0;
            for (int j = 0; j < integer2; ++j) {
                int n19;
                int n18;
                int n17;
                int n16 = (n17 = (n18 = (n19 = n15 << 2) + n5)) + n5;
                int n20 = n15;
                for (int k = 2; k < integer1; k += 2) {
                    n19 += 2;
                    n18 += 2;
                    n17 -= 2;
                    n16 -= 2;
                    n20 += 2;
                    final float n21 = arr3[n19] + arr3[n16];
                    final float n22 = arr3[n19] - arr3[n16];
                    final float n23 = arr3[n18] - arr3[n17];
                    final float n24 = arr3[n18] + arr3[n17];
                    final float n25 = arr3[n19 - 1] - arr3[n16 - 1];
                    final float n26 = arr3[n19 - 1] + arr3[n16 - 1];
                    final float n27 = arr3[n18 - 1] - arr3[n17 - 1];
                    final float n28 = arr3[n18 - 1] + arr3[n17 - 1];
                    arr4[n20 - 1] = n26 + n28;
                    final float n29 = n26 - n28;
                    arr4[n20] = n22 + n23;
                    final float n30 = n22 - n23;
                    final float n31 = n25 - n24;
                    final float n32 = n25 + n24;
                    final float n33 = n21 + n27;
                    final float n34 = n21 - n27;
                    final int n35;
                    arr4[(n35 = n20 + n) - 1] = arr5[integer6 + k - 2] * n31 - arr5[integer6 + k - 1] * n33;
                    arr4[n35] = arr5[integer6 + k - 2] * n33 + arr5[integer6 + k - 1] * n31;
                    final int n36;
                    arr4[(n36 = n35 + n) - 1] = arr7[integer8 + k - 2] * n29 - arr7[integer8 + k - 1] * n30;
                    arr4[n36] = arr7[integer8 + k - 2] * n30 + arr7[integer8 + k - 1] * n29;
                    final int n37;
                    arr4[(n37 = n36 + n) - 1] = arr9[integer10 + k - 2] * n32 - arr9[integer10 + k - 1] * n34;
                    arr4[n37] = arr9[integer10 + k - 2] * n34 + arr9[integer10 + k - 1] * n32;
                }
                n15 += integer1;
            }
            if (integer1 % 2 == 1) {
                return;
            }
        }
        int n38 = integer1;
        final int n39 = integer1 << 2;
        int n40 = integer1 - 1;
        int n41 = integer1 + (integer1 << 1);
        for (int l = 0; l < integer2; ++l) {
            final int n42 = n40;
            final float n43 = arr3[n38] + arr3[n41];
            final float n44 = arr3[n41] - arr3[n38];
            final float n45 = arr3[n38 - 1] - arr3[n41 - 1];
            final float n46 = arr3[n38 - 1] + arr3[n41 - 1];
            arr4[n42] = n46 + n46;
            final int n47;
            arr4[n47 = n42 + n] = Drft.sqrt2 * (n45 - n43);
            final int n48;
            arr4[n48 = n47 + n] = n44 + n44;
            arr4[n48 + n] = -Drft.sqrt2 * (n45 + n43);
            n40 += integer1;
            n38 += n39;
            n41 += n39;
        }
    }
    
    static void dradbg(final int integer1, final int integer2, final int integer3, final int integer4, final float[] arr5, final float[] arr6, final float[] arr7, final float[] arr8, final float[] arr9, final float[] arr10, final int integer11) {
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        float n5 = 0.0f;
        float n6 = 0.0f;
        int n7 = 0;
        int n8 = 100;
        while (true) {
            switch (n8) {
                case 100: {
                    n3 = integer2 * integer1;
                    n2 = integer3 * integer1;
                    final float n9 = Drft.tpi / integer2;
                    n5 = (float)Math.cos(n9);
                    n6 = (float)Math.sin(n9);
                    n4 = integer1 - 1 >>> 1;
                    n7 = integer2;
                    n = integer2 + 1 >>> 1;
                    if (integer1 < integer3) {
                        n8 = 103;
                        continue;
                    }
                    int n10 = 0;
                    int n11 = 0;
                    for (int i = 0; i < integer3; ++i) {
                        int n12 = n10;
                        int n13 = n11;
                        for (int j = 0; j < integer1; ++j) {
                            arr8[n12] = arr5[n13];
                            ++n12;
                            ++n13;
                        }
                        n10 += integer1;
                        n11 += n3;
                    }
                    n8 = 106;
                    continue;
                }
                case 103: {
                    int n14 = 0;
                    for (int k = 0; k < integer1; ++k) {
                        int n15 = n14;
                        int n16 = n14;
                        for (int l = 0; l < integer3; ++l) {
                            arr8[n15] = arr5[n16];
                            n15 += integer1;
                            n16 += n3;
                        }
                        ++n14;
                    }
                }
                case 106: {
                    int n17 = 0;
                    int n18 = n7 * n2;
                    final int n20;
                    int n19 = n20 = integer1 << 1;
                    for (int n21 = 1; n21 < n; ++n21) {
                        n17 += n2;
                        n18 -= n2;
                        int n22 = n17;
                        int n23 = n18;
                        int n24 = n19;
                        for (int n25 = 0; n25 < integer3; ++n25) {
                            arr8[n22] = arr5[n24 - 1] + arr5[n24 - 1];
                            arr8[n23] = arr5[n24] + arr5[n24];
                            n22 += integer1;
                            n23 += integer1;
                            n24 += n3;
                        }
                        n19 += n20;
                    }
                    if (integer1 == 1) {
                        n8 = 116;
                        continue;
                    }
                    if (n4 < integer3) {
                        n8 = 112;
                        continue;
                    }
                    int n26 = 0;
                    int n27 = n7 * n2;
                    int n28 = 0;
                    for (int n29 = 1; n29 < n; ++n29) {
                        n26 += n2;
                        n27 -= n2;
                        int n30 = n26;
                        int n31 = n27;
                        int n32;
                        n28 = (n32 = n28 + (integer1 << 1));
                        for (int n33 = 0; n33 < integer3; ++n33) {
                            int n34 = n30;
                            int n35 = n31;
                            int n36 = n32;
                            int n37 = n32;
                            for (int n38 = 2; n38 < integer1; n38 += 2) {
                                n34 += 2;
                                n35 += 2;
                                n36 += 2;
                                n37 -= 2;
                                arr8[n34 - 1] = arr5[n36 - 1] + arr5[n37 - 1];
                                arr8[n35 - 1] = arr5[n36 - 1] - arr5[n37 - 1];
                                arr8[n34] = arr5[n36] - arr5[n37];
                                arr8[n35] = arr5[n36] + arr5[n37];
                            }
                            n30 += integer1;
                            n31 += integer1;
                            n32 += n3;
                        }
                    }
                    n8 = 116;
                    continue;
                }
                case 112: {
                    int n39 = 0;
                    int n40 = n7 * n2;
                    int n41 = 0;
                    for (int n42 = 1; n42 < n; ++n42) {
                        n39 += n2;
                        n40 -= n2;
                        int n43 = n39;
                        int n44 = n40;
                        int n46;
                        int n45;
                        n41 = (n45 = (n46 = n41 + (integer1 << 1)));
                        for (int n47 = 2; n47 < integer1; n47 += 2) {
                            n43 += 2;
                            n44 += 2;
                            n46 += 2;
                            n45 -= 2;
                            int n48 = n43;
                            int n49 = n44;
                            int n50 = n46;
                            int n51 = n45;
                            for (int n52 = 0; n52 < integer3; ++n52) {
                                arr8[n48 - 1] = arr5[n50 - 1] + arr5[n51 - 1];
                                arr8[n49 - 1] = arr5[n50 - 1] - arr5[n51 - 1];
                                arr8[n48] = arr5[n50] - arr5[n51];
                                arr8[n49] = arr5[n50] + arr5[n51];
                                n48 += integer1;
                                n49 += integer1;
                                n50 += n3;
                                n51 += n3;
                            }
                        }
                    }
                }
                case 116: {
                    float n53 = 1.0f;
                    float n54 = 0.0f;
                    int n55 = 0;
                    final int n57;
                    int n56 = n57 = n7 * integer4;
                    final int n58 = (integer2 - 1) * integer4;
                    for (int n59 = 1; n59 < n; ++n59) {
                        n55 += integer4;
                        n56 -= integer4;
                        final float n60 = n5 * n53 - n6 * n54;
                        n54 = n5 * n54 + n6 * n53;
                        n53 = n60;
                        int n61 = n55;
                        int n62 = n56;
                        int n63 = 0;
                        int n64 = integer4;
                        int n65 = n58;
                        for (int n66 = 0; n66 < integer4; ++n66) {
                            arr7[n61++] = arr9[n63++] + n53 * arr9[n64++];
                            arr7[n62++] = n54 * arr9[n65++];
                        }
                        final float n67 = n53;
                        final float n68 = n54;
                        float n69 = n53;
                        float n70 = n54;
                        int n71 = integer4;
                        int n72 = n57 - integer4;
                        for (int n73 = 2; n73 < n; ++n73) {
                            n71 += integer4;
                            n72 -= integer4;
                            final float n74 = n67 * n69 - n68 * n70;
                            n70 = n67 * n70 + n68 * n69;
                            n69 = n74;
                            int n75 = n55;
                            int n76 = n56;
                            int n77 = n71;
                            int n78 = n72;
                            for (int n79 = 0; n79 < integer4; ++n79) {
                                final int n80 = n75++;
                                arr7[n80] += n69 * arr9[n77++];
                                final int n81 = n76++;
                                arr7[n81] += n70 * arr9[n78++];
                            }
                        }
                    }
                    int n82 = 0;
                    for (int n83 = 1; n83 < n; ++n83) {
                        int n84;
                        n82 = (n84 = n82 + integer4);
                        for (int n85 = 0; n85 < integer4; ++n85) {
                            final int n86 = n85;
                            arr9[n86] += arr9[n84++];
                        }
                    }
                    int n87 = 0;
                    int n88 = n7 * n2;
                    for (int n89 = 1; n89 < n; ++n89) {
                        n87 += n2;
                        n88 -= n2;
                        int n90 = n87;
                        int n91 = n88;
                        for (int n92 = 0; n92 < integer3; ++n92) {
                            arr8[n90] = arr6[n90] - arr6[n91];
                            arr8[n91] = arr6[n90] + arr6[n91];
                            n90 += integer1;
                            n91 += integer1;
                        }
                    }
                    if (integer1 == 1) {
                        n8 = 132;
                        continue;
                    }
                    if (n4 < integer3) {
                        n8 = 128;
                        continue;
                    }
                    int n93 = 0;
                    int n94 = n7 * n2;
                    for (int n95 = 1; n95 < n; ++n95) {
                        n93 += n2;
                        n94 -= n2;
                        int n96 = n93;
                        int n97 = n94;
                        for (int n98 = 0; n98 < integer3; ++n98) {
                            int n99 = n96;
                            int n100 = n97;
                            for (int n101 = 2; n101 < integer1; n101 += 2) {
                                n99 += 2;
                                n100 += 2;
                                arr8[n99 - 1] = arr6[n99 - 1] - arr6[n100];
                                arr8[n100 - 1] = arr6[n99 - 1] + arr6[n100];
                                arr8[n99] = arr6[n99] + arr6[n100 - 1];
                                arr8[n100] = arr6[n99] - arr6[n100 - 1];
                            }
                            n96 += integer1;
                            n97 += integer1;
                        }
                    }
                    n8 = 132;
                    continue;
                }
                case 128: {
                    int n102 = 0;
                    int n103 = n7 * n2;
                    for (int n104 = 1; n104 < n; ++n104) {
                        n102 += n2;
                        n103 -= n2;
                        int n105 = n102;
                        int n106 = n103;
                        for (int n107 = 2; n107 < integer1; n107 += 2) {
                            n105 += 2;
                            n106 += 2;
                            int n108 = n105;
                            int n109 = n106;
                            for (int n110 = 0; n110 < integer3; ++n110) {
                                arr8[n108 - 1] = arr6[n108 - 1] - arr6[n109];
                                arr8[n109 - 1] = arr6[n108 - 1] + arr6[n109];
                                arr8[n108] = arr6[n108] + arr6[n109 - 1];
                                arr8[n109] = arr6[n108] - arr6[n109 - 1];
                                n108 += integer1;
                                n109 += integer1;
                            }
                        }
                    }
                }
                case 132: {
                    if (integer1 == 1) {
                        return;
                    }
                    for (int n111 = 0; n111 < integer4; ++n111) {
                        arr7[n111] = arr9[n111];
                    }
                    int n112 = 0;
                    for (int n113 = 1; n113 < integer2; ++n113) {
                        int n114;
                        n112 = (n114 = n112 + n2);
                        for (int n115 = 0; n115 < integer3; ++n115) {
                            arr6[n114] = arr8[n114];
                            n114 += integer1;
                        }
                    }
                    if (n4 > integer3) {
                        n8 = 139;
                        continue;
                    }
                    int n116 = -integer1 - 1;
                    int n117 = 0;
                    for (int n118 = 1; n118 < integer2; ++n118) {
                        n116 += integer1;
                        n117 += n2;
                        int n119 = n116;
                        int n120 = n117;
                        for (int n121 = 2; n121 < integer1; n121 += 2) {
                            n120 += 2;
                            n119 += 2;
                            int n122 = n120;
                            for (int n123 = 0; n123 < integer3; ++n123) {
                                arr6[n122 - 1] = arr10[integer11 + n119 - 1] * arr8[n122 - 1] - arr10[integer11 + n119] * arr8[n122];
                                arr6[n122] = arr10[integer11 + n119 - 1] * arr8[n122] + arr10[integer11 + n119] * arr8[n122 - 1];
                                n122 += integer1;
                            }
                        }
                    }
                    return;
                }
                case 139: {
                    int n124 = -integer1 - 1;
                    int n125 = 0;
                    for (int n126 = 1; n126 < integer2; ++n126) {
                        n124 += integer1;
                        int n127;
                        n125 = (n127 = n125 + n2);
                        for (int n128 = 0; n128 < integer3; ++n128) {
                            int n129 = n124;
                            int n130 = n127;
                            for (int n131 = 2; n131 < integer1; n131 += 2) {
                                n129 += 2;
                                n130 += 2;
                                arr6[n130 - 1] = arr10[integer11 + n129 - 1] * arr8[n130 - 1] - arr10[integer11 + n129] * arr8[n130];
                                arr6[n130] = arr10[integer11 + n129 - 1] * arr8[n130] + arr10[integer11 + n129] * arr8[n130 - 1];
                            }
                            n127 += integer1;
                        }
                    }
                }
            }
        }
    }
    
    static void drftb1(final int integer1, final float[] arr2, final float[] arr3, final float[] arr4, final int integer5, final int[] arr) {
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        final int n5 = arr[1];
        int n6 = 0;
        int n7 = 1;
        int n8 = 1;
        for (int i = 0; i < n5; ++i) {
            int n9 = 100;
        Label_0462:
            while (true) {
                switch (n9) {
                    case 100: {
                        n2 = arr[i + 2];
                        n = n2 * n7;
                        n3 = integer1 / n;
                        n4 = n3 * n7;
                        if (n2 != 4) {
                            n9 = 103;
                            continue;
                        }
                        final int n10 = n8 + n3;
                        final int n11 = n10 + n3;
                        if (n6 != 0) {
                            dradb4(n3, n7, arr3, arr2, arr4, integer5 + n8 - 1, arr4, integer5 + n10 - 1, arr4, integer5 + n11 - 1);
                        }
                        else {
                            dradb4(n3, n7, arr2, arr3, arr4, integer5 + n8 - 1, arr4, integer5 + n10 - 1, arr4, integer5 + n11 - 1);
                        }
                        n6 = 1 - n6;
                        n9 = 115;
                        continue;
                    }
                    case 103: {
                        if (n2 != 2) {
                            n9 = 106;
                            continue;
                        }
                        if (n6 != 0) {
                            dradb2(n3, n7, arr3, arr2, arr4, integer5 + n8 - 1);
                        }
                        else {
                            dradb2(n3, n7, arr2, arr3, arr4, integer5 + n8 - 1);
                        }
                        n6 = 1 - n6;
                        n9 = 115;
                        continue;
                    }
                    case 106: {
                        if (n2 != 3) {
                            n9 = 109;
                            continue;
                        }
                        final int n12 = n8 + n3;
                        if (n6 != 0) {
                            dradb3(n3, n7, arr3, arr2, arr4, integer5 + n8 - 1, arr4, integer5 + n12 - 1);
                        }
                        else {
                            dradb3(n3, n7, arr2, arr3, arr4, integer5 + n8 - 1, arr4, integer5 + n12 - 1);
                        }
                        n6 = 1 - n6;
                        n9 = 115;
                        continue;
                    }
                    case 109: {
                        if (n6 != 0) {
                            dradbg(n3, n2, n7, n4, arr3, arr3, arr3, arr2, arr2, arr4, integer5 + n8 - 1);
                        }
                        else {
                            dradbg(n3, n2, n7, n4, arr2, arr2, arr2, arr3, arr3, arr4, integer5 + n8 - 1);
                        }
                        if (n3 == 1) {
                            n6 = 1 - n6;
                            break Label_0462;
                        }
                        break Label_0462;
                    }
                    case 115: {
                        break Label_0462;
                    }
                }
            }
            n7 = n;
            n8 += (n2 - 1) * n3;
        }
        if (n6 == 0) {
            return;
        }
        for (int j = 0; j < integer1; ++j) {
            arr2[j] = arr3[j];
        }
    }
    
    static {
        Drft.ntryh = new int[] { 4, 2, 3, 5 };
        Drft.tpi = 6.2831855f;
        Drft.hsqt2 = 0.70710677f;
        Drft.taui = 0.8660254f;
        Drft.taur = -0.5f;
        Drft.sqrt2 = 1.4142135f;
    }
}
