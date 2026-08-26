// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.saveddata;

import java.io.DataOutputStream;

import com.mojang.nbt.ShortTag;
import com.mojang.nbt.Tag;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import com.mojang.nbt.CompoundTag;
import java.io.File;

import com.mojang.nbt.NbtIo;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.level.storage.LevelStorage;

public class SavedDataStorage
{
    private LevelStorage levelStorage;
    private Map<String, SavedData> cache = new HashMap<>();
    private List<SavedData> savedDatas = new ArrayList<>();
    private Map<String, Short> usedAuxIds = new HashMap<>();
    
    public SavedDataStorage(final LevelStorage levelStorage) {
        this.levelStorage = levelStorage;
        this.loadAuxValues();
    }
    
    public SavedData get(final Class<? extends SavedData> clazz, final String id) {
        SavedData data = this.cache.get(id);
        if (data != null) return data;

        if (this.levelStorage != null) {
            try {
                final File file = this.levelStorage.getDataFile(id);
                if (file != null && file.exists()) {
                    try {
                        data = clazz.getConstructor(String.class).newInstance(id);
                    }
                    catch (final Exception e) {
                        throw new RuntimeException("Failed to instantiate " + clazz.toString(), e);
                    }

                    final FileInputStream fis = new FileInputStream(file);
                    final CompoundTag root = NbtIo.readCompressed(fis);
                    fis.close();

                    data.load(root.getCompound("data"));
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }

        if (data != null) {
            this.cache.put(id, data);
            this.savedDatas.add(data);
        }
        return data;
    }
    
    public void set(final String id, final SavedData data) {
        if (data == null) throw new RuntimeException("Can't set null data");

        if (this.cache.containsKey(id)) {
            this.savedDatas.remove(this.cache.remove(id));

        }
        this.cache.put(id, data);
        this.savedDatas.add(data);
    }
    
    public void save() {
        for (int i = 0; i < this.savedDatas.size(); ++i) {
            final SavedData data = this.savedDatas.get(i);
            if (data.isDirty()) {
                this.save(data);
                data.setDirty(false);
            }
        }
    }
    
    private void save(final SavedData data) {
        if (this.levelStorage == null) return;

        try {
            final File file = this.levelStorage.getDataFile(data.id);
            if (file != null) {
                final CompoundTag dataTag = new CompoundTag();
                data.save(dataTag);

                final CompoundTag tag = new CompoundTag();
                tag.putCompound("data", dataTag);

                final FileOutputStream fos = new FileOutputStream(file);
                NbtIo.writeCompressed(tag, fos);
                fos.close();
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadAuxValues() {
        try {
            this.usedAuxIds.clear();

            if (this.levelStorage == null) return;
            final File file = this.levelStorage.getDataFile("idcounts");
            if (file != null && file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                final DataInputStream dis = new DataInputStream(fis);
                final CompoundTag tags = NbtIo.read(dis);
                dis.close();

                for (final Tag tag : tags.getAllTags()) {
                    if (tag instanceof ShortTag) {
                        final ShortTag sTag = (ShortTag)tag;
                        this.usedAuxIds.put(sTag.getName(), sTag.data);
                    }
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public int getFreeAuxValueFor(final String id) {
        Short val = this.usedAuxIds.get(id);
        if (val == null) val = 0;
        else val++;

        this.usedAuxIds.put(id, val);
        if (this.levelStorage == null) return val;

        try {
            final File file = this.levelStorage.getDataFile("idcounts");
            if (file != null) {
                final CompoundTag tag = new CompoundTag();

                for (final String name : this.usedAuxIds.keySet()) {
                    short value = this.usedAuxIds.get(name);
                    tag.putShort(name, value);
                }

                FileOutputStream fos = new FileOutputStream(file);
                final DataOutputStream dos = new DataOutputStream(fos);
                NbtIo.write(tag, dos);
                dos.close();
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return val;
    }
}
