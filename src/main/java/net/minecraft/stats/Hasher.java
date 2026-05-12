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
            final String string = this.salt + name;
            final MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(string.getBytes(), 0, string.length());
            return new BigInteger(1, instance.digest()).toString(16);
        }
        catch (final NoSuchAlgorithmException cause) {
            throw new RuntimeException(cause);
        }
    }
}
