// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.skins;

import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import net.minecraft.client.Minecraft;
import java.util.Map;
import java.util.List;

public class TexturePackRepository
{
    private List<TexturePack> texturePacks = new ArrayList<>();
    private TexturePack defaultTexturePack = new DefaultTexturePack();
    public TexturePack selected;
    private Map<String, TexturePack> skinCache = new HashMap<>();
    private Minecraft minecraft;
    private File workDir;
    private String chosenSkinName;
    
    public TexturePackRepository(final Minecraft minecraft, final File file) {
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
        if (skin == this.selected) return false;

        this.selected.deselect();
        this.chosenSkinName = skin.name;
        this.selected = skin;
        this.minecraft.options.skin = this.chosenSkinName;
        this.minecraft.options.save();
        this.selected.select();
        return true;
    }
    
    public void updateList() {
        final ArrayList<TexturePack> newSkins = new ArrayList<>();

        this.selected = null;
        newSkins.add(this.defaultTexturePack);

        if (this.workDir.exists() && this.workDir.isDirectory()) {
            for (final File file : this.workDir.listFiles()) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".zip")) {
                    final String id = file.getName() + ":" + file.length() + ":" + file.lastModified();

                    try {
                        if (!this.skinCache.containsKey(id)) {
                            TexturePack skin = new FileTexturePack(file);
                            skin.id = id;
                            this.skinCache.put(id, skin);
                            skin.load(this.minecraft);
                        }

                        TexturePack skin = this.skinCache.get(id);
                        if (skin.name.equals(this.chosenSkinName)) {
                            this.selected = skin;
                        }

                        newSkins.add(skin);
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

        this.texturePacks.removeAll(newSkins);

        for (final TexturePack texturePack : this.texturePacks) {
            texturePack.unload(this.minecraft);
            this.skinCache.remove(texturePack.id);
        }

        this.texturePacks = newSkins;
    }
    
    public List<TexturePack> getAll() {
        return new ArrayList<>(this.texturePacks);
    }
}
