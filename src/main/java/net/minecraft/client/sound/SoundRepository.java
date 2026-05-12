// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.sound;

import java.net.MalformedURLException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SoundRepository
{
    private Random random;
    private Map urls;
    private List all;
    public int count;
    public boolean trimDigits;
    
    public SoundRepository() {
        this.random = new Random();
        this.urls = new HashMap();
        this.all = new ArrayList();
        this.count = 0;
        this.trimDigits = true;
    }
    
    public Sound add(String name, final File file) {
        try {
            final String name2 = name;
            name = name.substring(0, name.indexOf("."));
            if (this.trimDigits) {
                while (Character.isDigit(name.charAt(name.length() - 1))) {
                    name = name.substring(0, name.length() - 1);
                }
            }
            name = name.replaceAll("/", ".");
            if (!this.urls.containsKey(name)) {
                this.urls.put(name, new ArrayList());
            }
            final Sound sound = new Sound(name2, file.toURI().toURL());
            this.urls.get(name).add(sound);
            this.all.add(sound);
            ++this.count;
            return sound;
        }
        catch (final MalformedURLException cause) {
            cause.printStackTrace();
            throw new RuntimeException(cause);
        }
    }
    
    public Sound get(final String name) {
        final List list = this.urls.get(name);
        if (list == null) {
            return null;
        }
        return (Sound)list.get(this.random.nextInt(list.size()));
    }
    
    public Sound any() {
        if (this.all.size() == 0) {
            return null;
        }
        return this.all.get(this.random.nextInt(this.all.size()));
    }
}
