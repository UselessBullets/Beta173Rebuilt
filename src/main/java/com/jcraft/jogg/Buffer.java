// 
// Decompiled by Procyon v0.6.0
// 

package com.jcraft.jogg;

public class Buffer
{
    private static final int BUFFER_INCREMENT = 256;
    private static final int[] mask;
    int ptr;
    byte[] buffer;
    int endbit;
    int endbyte;
    int storage;
    
    public Buffer() {
        this.ptr = 0;
        this.buffer = null;
        this.endbit = 0;
        this.endbyte = 0;
        this.storage = 0;
    }
    
    public void writeinit() {
        this.buffer = new byte[256];
        this.ptr = 0;
        this.buffer[0] = 0;
        this.storage = 256;
    }
    
    public void write(final byte[] arr) {
        for (int n = 0; n < arr.length && arr[n] != 0; ++n) {
            this.write(arr[n], 8);
        }
    }
    
    public void read(final byte[] arr, int integer) {
        int n = 0;
        while (integer-- != 0) {
            arr[n++] = (byte)this.read(8);
        }
    }
    
    void reset() {
        this.ptr = 0;
        this.buffer[0] = 0;
        final int n = 0;
        this.endbyte = n;
        this.endbit = n;
    }
    
    public void writeclear() {
        this.buffer = null;
    }
    
    public void readinit(final byte[] arr, final int integer) {
        this.readinit(arr, 0, integer);
    }
    
    public void readinit(final byte[] arr, final int integer2, final int integer3) {
        this.ptr = integer2;
        this.buffer = arr;
        final int n = 0;
        this.endbyte = n;
        this.endbit = n;
        this.storage = integer3;
    }
    
    public void write(int integer1, int integer2) {
        if (this.endbyte + 4 >= this.storage) {
            final byte[] buffer = new byte[this.storage + 256];
            System.arraycopy(this.buffer, 0, buffer, 0, this.storage);
            this.buffer = buffer;
            this.storage += 256;
        }
        integer1 &= Buffer.mask[integer2];
        integer2 += this.endbit;
        final byte[] buffer2 = this.buffer;
        final int ptr = this.ptr;
        buffer2[ptr] |= (byte)(integer1 << this.endbit);
        if (integer2 >= 8) {
            this.buffer[this.ptr + 1] = (byte)(integer1 >>> 8 - this.endbit);
            if (integer2 >= 16) {
                this.buffer[this.ptr + 2] = (byte)(integer1 >>> 16 - this.endbit);
                if (integer2 >= 24) {
                    this.buffer[this.ptr + 3] = (byte)(integer1 >>> 24 - this.endbit);
                    if (integer2 >= 32) {
                        if (this.endbit > 0) {
                            this.buffer[this.ptr + 4] = (byte)(integer1 >>> 32 - this.endbit);
                        }
                        else {
                            this.buffer[this.ptr + 4] = 0;
                        }
                    }
                }
            }
        }
        this.endbyte += integer2 / 8;
        this.ptr += integer2 / 8;
        this.endbit = (integer2 & 0x7);
    }
    
    public int look(int integer) {
        final int n = Buffer.mask[integer];
        integer += this.endbit;
        if (this.endbyte + 4 >= this.storage && this.endbyte + (integer - 1) / 8 >= this.storage) {
            return -1;
        }
        int n2 = (this.buffer[this.ptr] & 0xFF) >>> this.endbit;
        if (integer > 8) {
            n2 |= (this.buffer[this.ptr + 1] & 0xFF) << 8 - this.endbit;
            if (integer > 16) {
                n2 |= (this.buffer[this.ptr + 2] & 0xFF) << 16 - this.endbit;
                if (integer > 24) {
                    n2 |= (this.buffer[this.ptr + 3] & 0xFF) << 24 - this.endbit;
                    if (integer > 32 && this.endbit != 0) {
                        n2 |= (this.buffer[this.ptr + 4] & 0xFF) << 32 - this.endbit;
                    }
                }
            }
        }
        return n & n2;
    }
    
    public int look1() {
        if (this.endbyte >= this.storage) {
            return -1;
        }
        return this.buffer[this.ptr] >> this.endbit & 0x1;
    }
    
    public void adv(int integer) {
        integer += this.endbit;
        this.ptr += integer / 8;
        this.endbyte += integer / 8;
        this.endbit = (integer & 0x7);
    }
    
    public void adv1() {
        ++this.endbit;
        if (this.endbit > 7) {
            this.endbit = 0;
            ++this.ptr;
            ++this.endbyte;
        }
    }
    
    public int read(int integer) {
        final int n = Buffer.mask[integer];
        integer += this.endbit;
        if (this.endbyte + 4 >= this.storage) {
            final int n2 = -1;
            if (this.endbyte + (integer - 1) / 8 >= this.storage) {
                this.ptr += integer / 8;
                this.endbyte += integer / 8;
                this.endbit = (integer & 0x7);
                return n2;
            }
        }
        int n3 = (this.buffer[this.ptr] & 0xFF) >>> this.endbit;
        if (integer > 8) {
            n3 |= (this.buffer[this.ptr + 1] & 0xFF) << 8 - this.endbit;
            if (integer > 16) {
                n3 |= (this.buffer[this.ptr + 2] & 0xFF) << 16 - this.endbit;
                if (integer > 24) {
                    n3 |= (this.buffer[this.ptr + 3] & 0xFF) << 24 - this.endbit;
                    if (integer > 32 && this.endbit != 0) {
                        n3 |= (this.buffer[this.ptr + 4] & 0xFF) << 32 - this.endbit;
                    }
                }
            }
        }
        final int n4 = n3 & n;
        this.ptr += integer / 8;
        this.endbyte += integer / 8;
        this.endbit = (integer & 0x7);
        return n4;
    }
    
    public int readB(int integer) {
        final int n = 32 - integer;
        integer += this.endbit;
        if (this.endbyte + 4 >= this.storage) {
            final int n2 = -1;
            if (this.endbyte * 8 + integer > this.storage * 8) {
                this.ptr += integer / 8;
                this.endbyte += integer / 8;
                this.endbit = (integer & 0x7);
                return n2;
            }
        }
        int n3 = (this.buffer[this.ptr] & 0xFF) << 24 + this.endbit;
        if (integer > 8) {
            n3 |= (this.buffer[this.ptr + 1] & 0xFF) << 16 + this.endbit;
            if (integer > 16) {
                n3 |= (this.buffer[this.ptr + 2] & 0xFF) << 8 + this.endbit;
                if (integer > 24) {
                    n3 |= (this.buffer[this.ptr + 3] & 0xFF) << this.endbit;
                    if (integer > 32 && this.endbit != 0) {
                        n3 |= (this.buffer[this.ptr + 4] & 0xFF) >> 8 - this.endbit;
                    }
                }
            }
        }
        final int n4 = n3 >>> (n >> 1) >>> (n + 1 >> 1);
        this.ptr += integer / 8;
        this.endbyte += integer / 8;
        this.endbit = (integer & 0x7);
        return n4;
    }
    
    public int read1() {
        if (this.endbyte >= this.storage) {
            final int n = -1;
            ++this.endbit;
            if (this.endbit > 7) {
                this.endbit = 0;
                ++this.ptr;
                ++this.endbyte;
            }
            return n;
        }
        final int n2 = this.buffer[this.ptr] >> this.endbit & 0x1;
        ++this.endbit;
        if (this.endbit > 7) {
            this.endbit = 0;
            ++this.ptr;
            ++this.endbyte;
        }
        return n2;
    }
    
    public int bytes() {
        return this.endbyte + (this.endbit + 7) / 8;
    }
    
    public int bits() {
        return this.endbyte * 8 + this.endbit;
    }
    
    public byte[] buffer() {
        return this.buffer;
    }
    
    public static int ilog(int integer) {
        int n = 0;
        while (integer > 0) {
            ++n;
            integer >>>= 1;
        }
        return n;
    }
    
    public static void report(final String string) {
        System.err.println(string);
        System.exit(1);
    }
    
    static {
        mask = new int[] { 0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535, 131071, 262143, 524287, 1048575, 2097151, 4194303, 8388607, 16777215, 33554431, 67108863, 134217727, 268435455, 536870911, 1073741823, Integer.MAX_VALUE, -1 };
    }
}
