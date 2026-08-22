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
    private Random random = new Random();
    private Map<String, List<Sound>> urls = new HashMap<>();
    private List<Sound> all = new ArrayList<>();
    public int count = 0;
    public boolean trimDigits = true;
    
    public Sound add(String name, final File file) {
        try {
            final String orgName = name;
            name = name.substring(0, name.indexOf("."));
            if (this.trimDigits) {
                while (Character.isDigit(name.charAt(name.length() - 1))) {
                    name = name.substring(0, name.length() - 1);
                }
            }

            name = name.replaceAll("/", ".");
            if (!this.urls.containsKey(name)) {
                this.urls.put(name, new ArrayList<>());
            }

            final Sound sound = new Sound(orgName, file.toURI().toURL());
            this.urls.get(name).add(sound);
            this.all.add(sound);
            this.count++;
            return sound;
        }
        catch (final MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public Sound get(final String name) {
        final List<Sound> values = this.urls.get(name);
        return values == null ? null : values.get(this.random.nextInt(values.size()));
    }
    
    public Sound any() {
        return this.all.size() == 0 ? null : this.all.get(this.random.nextInt(this.all.size()));
    }
}
