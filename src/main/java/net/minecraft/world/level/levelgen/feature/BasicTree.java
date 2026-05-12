// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen.feature;

import util.Mth;
import net.minecraft.world.level.Level;
import java.util.Random;

public class BasicTree extends Feature
{
    static final byte[] axisConversionArray;
    Random rnd;
    Level thisLevel;
    int[] origin;
    int height;
    int trunkHeight;
    double trunkHeightScale;
    double branchDensity;
    double branchSlope;
    double widthScale;
    double foliageDensity;
    int trunkWidth;
    int heightVariance;
    int foliageHeight;
    int[][] foliageCoords;
    
    public BasicTree() {
        this.rnd = new Random();
        this.origin = new int[] { 0, 0, 0 };
        this.height = 0;
        this.trunkHeightScale = 0.618;
        this.branchDensity = 1.0;
        this.branchSlope = 0.381;
        this.widthScale = 1.0;
        this.foliageDensity = 1.0;
        this.trunkWidth = 1;
        this.heightVariance = 12;
        this.foliageHeight = 4;
    }
    
    void prepare() {
        this.trunkHeight = (int)(this.height * this.trunkHeightScale);
        if (this.trunkHeight >= this.height) {
            this.trunkHeight = this.height - 1;
        }
        int n = (int)(1.382 + Math.pow(this.foliageDensity * this.height / 13.0, 2.0));
        if (n < 1) {
            n = 1;
        }
        final int[][] array = new int[n * this.height][4];
        int n2 = this.origin[1] + this.height - this.foliageHeight;
        int n3 = 1;
        final int n4 = this.origin[1] + this.trunkHeight;
        int i = n2 - this.origin[1];
        array[0][0] = this.origin[0];
        array[0][1] = n2;
        array[0][2] = this.origin[2];
        array[0][3] = n4;
        --n2;
        while (i >= 0) {
            int j = 0;
            final float treeShape = this.treeShape(i);
            if (treeShape < 0.0f) {
                --n2;
                --i;
            }
            else {
                final double n5 = 0.5;
                while (j < n) {
                    final double n6 = this.widthScale * (treeShape * (this.rnd.nextFloat() + 0.328));
                    final double n7 = this.rnd.nextFloat() * 2.0 * 3.14159;
                    final int floor = Mth.floor(n6 * Math.sin(n7) + this.origin[0] + n5);
                    final int floor2 = Mth.floor(n6 * Math.cos(n7) + this.origin[2] + n5);
                    final int[] array2 = { floor, n2, floor2 };
                    if (this.checkLine(array2, new int[] { floor, n2 + this.foliageHeight, floor2 }) == -1) {
                        final int[] start = { this.origin[0], this.origin[1], this.origin[2] };
                        final double n8 = Math.sqrt(Math.pow(Math.abs(this.origin[0] - array2[0]), 2.0) + Math.pow(Math.abs(this.origin[2] - array2[2]), 2.0)) * this.branchSlope;
                        if (array2[1] - n8 > n4) {
                            start[1] = n4;
                        }
                        else {
                            start[1] = (int)(array2[1] - n8);
                        }
                        if (this.checkLine(start, array2) == -1) {
                            array[n3][0] = floor;
                            array[n3][1] = n2;
                            array[n3][2] = floor2;
                            array[n3][3] = start[1];
                            ++n3;
                        }
                    }
                    ++j;
                }
                --n2;
                --i;
            }
        }
        System.arraycopy(array, 0, this.foliageCoords = new int[n3][4], 0, n3);
    }
    
    void crossection(final int x, final int y, final int z, final float radius, final byte direction, final int material) {
        final int n = (int)(radius + 0.618);
        final byte b = BasicTree.axisConversionArray[direction];
        final byte b2 = BasicTree.axisConversionArray[direction + 3];
        final int[] array = { x, y, z };
        final int[] array2 = { 0, 0, 0 };
        int i = -n;
        array2[direction] = array[direction];
        while (i <= n) {
            array2[b] = array[b] + i;
            for (int j = -n; j <= n; ++j) {
                if (Math.sqrt(Math.pow(Math.abs(i) + 0.5, 2.0) + Math.pow(Math.abs(j) + 0.5, 2.0)) <= radius) {
                    array2[b2] = array[b2] + j;
                    final int tile = this.thisLevel.getTile(array2[0], array2[1], array2[2]);
                    if (tile == 0 || tile == 18) {
                        this.thisLevel.setTileNoUpdate(array2[0], array2[1], array2[2], material);
                    }
                }
            }
            ++i;
        }
    }
    
    float treeShape(final int y) {
        if (y < (float)this.height * 0.3) {
            return -1.618f;
        }
        final float a = this.height / 2.0f;
        final float n = this.height / 2.0f - y;
        float n2;
        if (n == 0.0f) {
            n2 = a;
        }
        else if (Math.abs(n) >= a) {
            n2 = 0.0f;
        }
        else {
            n2 = (float)Math.sqrt(Math.pow(Math.abs(a), 2.0) - Math.pow(Math.abs(n), 2.0));
        }
        return n2 * 0.5f;
    }
    
    float foliageShape(final int y) {
        if (y < 0 || y >= this.foliageHeight) {
            return -1.0f;
        }
        if (y == 0 || y == this.foliageHeight - 1) {
            return 2.0f;
        }
        return 3.0f;
    }
    
    void foliageCluster(final int x, final int y, final int z) {
        for (int i = y; i < y + this.foliageHeight; ++i) {
            this.crossection(x, i, z, this.foliageShape(i - y), (byte)1, 18);
        }
    }
    
    void limb(final int[] start, final int[] end, final int material) {
        final int[] array = { 0, 0, 0 };
        int i = 0;
        int n = 0;
        while (i < 3) {
            array[i] = end[i] - start[i];
            if (Math.abs(array[i]) > Math.abs(array[n])) {
                n = i;
            }
            i = (byte)(i + 1);
        }
        if (array[n] == 0) {
            return;
        }
        final byte b = BasicTree.axisConversionArray[n];
        final byte b2 = BasicTree.axisConversionArray[n + 3];
        int n2;
        if (array[n] > 0) {
            n2 = 1;
        }
        else {
            n2 = -1;
        }
        final double n3 = array[b] / (double)array[n];
        final double n4 = array[b2] / (double)array[n];
        final int[] array2 = { 0, 0, 0 };
        for (int j = 0; j != array[n] + n2; j += n2) {
            array2[n] = Mth.floor(start[n] + j + 0.5);
            array2[b] = Mth.floor(start[b] + j * n3 + 0.5);
            array2[b2] = Mth.floor(start[b2] + j * n4 + 0.5);
            this.thisLevel.setTileNoUpdate(array2[0], array2[1], array2[2], material);
        }
    }
    
    void makeFoliage() {
        for (int i = 0; i < this.foliageCoords.length; ++i) {
            this.foliageCluster(this.foliageCoords[i][0], this.foliageCoords[i][1], this.foliageCoords[i][2]);
        }
    }
    
    boolean trimBranches(final int localY) {
        return localY >= this.height * 0.2;
    }
    
    void makeTrunk() {
        final int n = this.origin[0];
        final int n2 = this.origin[1];
        final int n3 = this.origin[1] + this.trunkHeight;
        final int n4 = this.origin[2];
        final int[] array = { n, n2, n4 };
        final int[] array2 = { n, n3, n4 };
        this.limb(array, array2, 17);
        if (this.trunkWidth == 2) {
            final int[] array3 = array;
            final int n5 = 0;
            ++array3[n5];
            final int[] array4 = array2;
            final int n6 = 0;
            ++array4[n6];
            this.limb(array, array2, 17);
            final int[] array5 = array;
            final int n7 = 2;
            ++array5[n7];
            final int[] array6 = array2;
            final int n8 = 2;
            ++array6[n8];
            this.limb(array, array2, 17);
            final int[] array7 = array;
            final int n9 = 0;
            --array7[n9];
            final int[] array8 = array2;
            final int n10 = 0;
            --array8[n10];
            this.limb(array, array2, 17);
        }
    }
    
    void makeBranches() {
        int i = 0;
        final int length = this.foliageCoords.length;
        final int[] start = { this.origin[0], this.origin[1], this.origin[2] };
        while (i < length) {
            final int[] array = this.foliageCoords[i];
            final int[] end = { array[0], array[1], array[2] };
            start[1] = array[3];
            if (this.trimBranches(start[1] - this.origin[1])) {
                this.limb(start, end, 17);
            }
            ++i;
        }
    }
    
    int checkLine(final int[] start, final int[] end) {
        final int[] array = { 0, 0, 0 };
        int i = 0;
        int n = 0;
        while (i < 3) {
            array[i] = end[i] - start[i];
            if (Math.abs(array[i]) > Math.abs(array[n])) {
                n = i;
            }
            i = (byte)(i + 1);
        }
        if (array[n] == 0) {
            return -1;
        }
        final byte b = BasicTree.axisConversionArray[n];
        final byte b2 = BasicTree.axisConversionArray[n + 3];
        int n2;
        if (array[n] > 0) {
            n2 = 1;
        }
        else {
            n2 = -1;
        }
        final double n3 = array[b] / (double)array[n];
        final double n4 = array[b2] / (double)array[n];
        final int[] array2 = { 0, 0, 0 };
        int j;
        int n5;
        for (j = 0, n5 = array[n] + n2; j != n5; j += n2) {
            array2[n] = start[n] + j;
            array2[b] = Mth.floor(start[b] + j * n3);
            array2[b2] = Mth.floor(start[b2] + j * n4);
            final int tile = this.thisLevel.getTile(array2[0], array2[1], array2[2]);
            if (tile != 0 && tile != 18) {
                break;
            }
        }
        if (j == n5) {
            return -1;
        }
        return Math.abs(j);
    }
    
    boolean checkLocation() {
        final int[] start = { this.origin[0], this.origin[1], this.origin[2] };
        final int[] end = { this.origin[0], this.origin[1] + this.height - 1, this.origin[2] };
        final int tile = this.thisLevel.getTile(this.origin[0], this.origin[1] - 1, this.origin[2]);
        if (tile != 2 && tile != 3) {
            return false;
        }
        final int checkLine = this.checkLine(start, end);
        if (checkLine == -1) {
            return true;
        }
        if (checkLine < 6) {
            return false;
        }
        this.height = checkLine;
        return true;
    }
    
    @Override
    public void init(final double V1, final double V2, final double V3) {
        this.heightVariance = (int)(V1 * 12.0);
        if (V1 > 0.5) {
            this.foliageHeight = 5;
        }
        this.widthScale = V2;
        this.foliageDensity = V3;
    }
    
    @Override
    public boolean place(final Level level, final Random random, final int x, final int y, final int z) {
        this.thisLevel = level;
        this.rnd.setSeed(random.nextLong());
        this.origin[0] = x;
        this.origin[1] = y;
        this.origin[2] = z;
        if (this.height == 0) {
            this.height = 5 + this.rnd.nextInt(this.heightVariance);
        }
        if (!this.checkLocation()) {
            return false;
        }
        this.prepare();
        this.makeFoliage();
        this.makeTrunk();
        this.makeBranches();
        return true;
    }
    
    static {
        axisConversionArray = new byte[] { 2, 0, 0, 1, 2, 1 };
    }
}
