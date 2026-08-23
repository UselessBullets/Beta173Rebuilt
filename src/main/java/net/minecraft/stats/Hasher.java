// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.stats;

import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.security.MessageDigest;

public class Hasher
{
    private String salt;
    
    public Hasher(final String salt) {
        this.salt = salt;
    }
    
    public String getHash(final String name) {
        try {
            String s = this.salt + name;
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            return new BigInteger(1, m.digest()).toString(16);
        }
        catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
