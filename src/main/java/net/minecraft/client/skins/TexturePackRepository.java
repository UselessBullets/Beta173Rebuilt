// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.skins;

import java.util.Iterator;
import java.util.Collection;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import net.minecraft.client.Minecraft;
import java.util.Map;
import java.util.List;

public class TexturePackRepository
{
    private List<TexturePack> texturePacks;
    private TexturePack defaultTexturePack;
    public TexturePack selected;
    private Map<String, TexturePack> skinCache;
    private Minecraft minecraft;
    private File workDir;
    private String chosenSkinName;
    
    public TexturePackRepository(final Minecraft minecraft, final File file) {
        this.texturePacks = new ArrayList();
        this.defaultTexturePack = new DefaultTexturePack();
        this.skinCache = new HashMap();
        this.minecraft = minecraft;
        this.workDir = new File(file, "texturepacks");
        if (!this.workDir.exists()) {
            this.workDir.mkdirs();
        }
        this.chosenSkinName = minecraft.options.skin;
        this.updateList();
        this.selected.select();
    }
    
    public boolean selectSkin(final TexturePack skin) {
        if (skin == this.selected) {
            return false;
        }
        this.selected.deselect();
        this.chosenSkinName = skin.name;
        this.selected = skin;
        this.minecraft.options.skin = this.chosenSkinName;
        this.minecraft.options.save();
        this.selected.select();
        return true;
    }
    
    public void updateList() {
        final ArrayList texturePacks = new ArrayList();
        this.selected = null;
        texturePacks.add(this.defaultTexturePack);
        if (this.workDir.exists() && this.workDir.isDirectory()) {
            for (final File file : this.workDir.listFiles()) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".zip")) {
                    final String string = file.getName() + ":" + file.length() + ":" + file.lastModified();
                    try {
                        if (!this.skinCache.containsKey(string)) {
                            final FileTexturePack fileTexturePack = new FileTexturePack(file);
                            fileTexturePack.id = string;
                            this.skinCache.put(string, fileTexturePack);
                            fileTexturePack.load(this.minecraft);
                        }
                        final TexturePack selected = this.skinCache.get(string);
                        if (selected.name.equals(this.chosenSkinName)) {
                            this.selected = selected;
                        }
                        texturePacks.add(selected);
                    }
                    catch (final IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        if (this.selected == null) {
            this.selected = this.defaultTexturePack;
        }
        this.texturePacks.removeAll(texturePacks);
        for (final TexturePack texturePack : this.texturePacks) {
            texturePack.unload(this.minecraft);
            this.skinCache.remove(texturePack.id);
        }
        this.texturePacks = texturePacks;
    }
    
    public List<TexturePack> getAll() {
        return new ArrayList<>(this.texturePacks);
    }
}
