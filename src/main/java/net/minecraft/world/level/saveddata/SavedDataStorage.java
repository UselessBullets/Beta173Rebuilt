// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.saveddata;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.util.Iterator;
import com.mojang.nbt.ShortTag;
import com.mojang.nbt.Tag;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import com.mojang.nbt.CompoundTag;
import java.io.File;
import java.io.InputStream;
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
    private Map cache;
    private List savedDatas;
    private Map usedAuxIds;
    
    public SavedDataStorage(final LevelStorage levelStorage) {
        this.cache = new HashMap();
        this.savedDatas = new ArrayList();
        this.usedAuxIds = new HashMap();
        this.levelStorage = levelStorage;
        this.loadAuxValues();
    }
    
    public SavedData get(final Class clazz, final String id) {
        SavedData savedData = this.cache.get(id);
        if (savedData != null) {
            return savedData;
        }
        if (this.levelStorage != null) {
            try {
                final File dataFile = this.levelStorage.getDataFile(id);
                if (dataFile != null && dataFile.exists()) {
                    try {
                        savedData = clazz.getConstructor(String.class).newInstance(id);
                    }
                    catch (final Exception cause) {
                        throw new RuntimeException("Failed to instantiate " + clazz.toString(), cause);
                    }
                    final FileInputStream in = new FileInputStream(dataFile);
                    final CompoundTag compressed = NbtIo.readCompressed(in);
                    in.close();
                    savedData.load(compressed.getCompound("data"));
                }
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
        if (savedData != null) {
            this.cache.put(id, savedData);
            this.savedDatas.add(savedData);
        }
        return savedData;
    }
    
    public void set(final String id, final SavedData data) {
        if (data == null) {
            throw new RuntimeException("Can't set null data");
        }
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
        if (this.levelStorage == null) {
            return;
        }
        try {
            final File dataFile = this.levelStorage.getDataFile(data.id);
            if (dataFile != null) {
                final CompoundTag compoundTag = new CompoundTag();
                data.save(compoundTag);
                final CompoundTag tag = new CompoundTag();
                tag.putCompound("data", compoundTag);
                final FileOutputStream out = new FileOutputStream(dataFile);
                NbtIo.writeCompressed(tag, out);
                out.close();
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void loadAuxValues() {
        try {
            this.usedAuxIds.clear();
            if (this.levelStorage == null) {
                return;
            }
            final File dataFile = this.levelStorage.getDataFile("idcounts");
            if (dataFile != null && dataFile.exists()) {
                final DataInputStream dis = new DataInputStream(new FileInputStream(dataFile));
                final CompoundTag read = NbtIo.read(dis);
                dis.close();
                for (final Tag tag : read.getAllTags()) {
                    if (tag instanceof ShortTag) {
                        final ShortTag shortTag = (ShortTag)tag;
                        this.usedAuxIds.put(shortTag.getName(), shortTag.data);
                    }
                }
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public int getFreeAuxValueFor(final String id) {
        final Short n = this.usedAuxIds.get(id);
        Short n2;
        if (n == null) {
            n2 = 0;
        }
        else {
            n2 = (short)(n + 1);
        }
        this.usedAuxIds.put(id, n2);
        if (this.levelStorage == null) {
            return n2;
        }
        try {
            final File dataFile = this.levelStorage.getDataFile("idcounts");
            if (dataFile != null) {
                final CompoundTag tag = new CompoundTag();
                for (final String name : this.usedAuxIds.keySet()) {
                    tag.putShort(name, (short)this.usedAuxIds.get(name));
                }
                final DataOutputStream dos = new DataOutputStream(new FileOutputStream(dataFile));
                NbtIo.write(tag, dos);
                dos.close();
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return n2;
    }
}
