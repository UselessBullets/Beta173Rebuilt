// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile.entity;

import java.util.HashMap;
import net.minecraft.world.level.tile.Tile;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import java.util.Map;

public class TileEntity
{
    private static Map idClassMap;
    private static Map classIdMap;
    public Level level;
    public int x;
    public int y;
    public int z;
    protected boolean remove;
    
    private static void setId(final Class clazz, final String id) {
        if (TileEntity.classIdMap.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate id: " + id);
        }
        TileEntity.idClassMap.put(id, clazz);
        TileEntity.classIdMap.put(clazz, id);
    }
    
    public void load(final CompoundTag compoundTag) {
        this.x = compoundTag.getInt("x");
        this.y = compoundTag.getInt("y");
        this.z = compoundTag.getInt("z");
    }
    
    public void save(final CompoundTag compoundTag) {
        final String value = TileEntity.classIdMap.get(this.getClass());
        if (value == null) {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        }
        compoundTag.putString("id", value);
        compoundTag.putInt("x", this.x);
        compoundTag.putInt("y", this.y);
        compoundTag.putInt("z", this.z);
    }
    
    public void tick() {
    }
    
    public static TileEntity loadStatic(final CompoundTag compoundTag) {
        TileEntity tileEntity = null;
        try {
            final Class clazz = TileEntity.idClassMap.get(compoundTag.getString("id"));
            if (clazz != null) {
                tileEntity = (TileEntity)clazz.newInstance();
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        if (tileEntity != null) {
            tileEntity.load(compoundTag);
        }
        else {
            System.out.println("Skipping TileEntity with id " + compoundTag.getString("id"));
        }
        return tileEntity;
    }
    
    public int getData() {
        return this.level.getData(this.x, this.y, this.z);
    }
    
    public void setChanged() {
        if (this.level != null) {
            this.level.tileEntityChanged(this.x, this.y, this.z, this);
        }
    }
    
    public double distanceToSqr(final double x, final double y, final double z) {
        final double n = this.x + 0.5 - x;
        final double n2 = this.y + 0.5 - y;
        final double n3 = this.z + 0.5 - z;
        return n * n + n2 * n2 + n3 * n3;
    }
    
    public Tile getTile() {
        return Tile.tiles[this.level.getTile(this.x, this.y, this.z)];
    }
    
    public boolean isRemoved() {
        return this.remove;
    }
    
    public void setRemoved() {
        this.remove = true;
    }
    
    public void clearRemoved() {
        this.remove = false;
    }
    
    static {
        TileEntity.idClassMap = new HashMap();
        TileEntity.classIdMap = new HashMap();
        setId(FurnaceTileEntity.class, "Furnace");
        setId(ChestTileEntity.class, "Chest");
        setId(RecordPlayerTileEntity.class, "RecordPlayer");
        setId(DispenserTileEntity.class, "Trap");
        setId(SignTileEntity.class, "Sign");
        setId(MobSpawnerTileEntity.class, "MobSpawner");
        setId(MusicTileEntity.class, "Music");
        setId(PistonPieceEntity.class, "Piston");
    }
}
