// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity;

import com.mojang.nbt.CompoundTag;
import java.util.List;
import net.minecraft.world.level.material.Material;
import util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.Item;
import java.util.ArrayList;
import net.minecraft.world.level.Level;

public class Painting extends Entity
{
    private int checkInterval;
    public int dir;
    public int xTile;
    public int yTile;
    public int zTile;
    public Painting_Motive motive;
    
    public Painting(final Level level) {
        super(level);
        this.checkInterval = 0;
        this.dir = 0;
        this.heightOffset = 0.0f;
        this.setSize(0.5f, 0.5f);
    }
    
    public Painting(final Level level, final int xTile, final int yTile, final int zTile, final int dir) {
        this(level);
        this.xTile = xTile;
        this.yTile = yTile;
        this.zTile = zTile;
        final ArrayList list = new ArrayList();
        for (final Painting_Motive motive : Painting_Motive.values()) {
            this.motive = motive;
            this.setDir(dir);
            if (this.survives()) {
                list.add(motive);
            }
        }
        if (list.size() > 0) {
            this.motive = (Painting_Motive)list.get(this.random.nextInt(list.size()));
        }
        this.setDir(dir);
    }
    
    public Painting(final Level level, final int x, final int y, final int z, final int dir, final String motiveName) {
        this(level);
        this.xTile = x;
        this.yTile = y;
        this.zTile = z;
        for (final Painting_Motive motive : Painting_Motive.values()) {
            if (motive.name.equals(motiveName)) {
                this.motive = motive;
                break;
            }
        }
        this.setDir(dir);
    }
    
    @Override
    protected void definedSynchedData() {
    }
    
    public void setDir(final int dir) {
        this.dir = dir;
        final float n = (float)(dir * 90);
        this.yRot = n;
        this.yRotO = n;
        float n2 = (float)this.motive.w;
        final float n3 = (float)this.motive.h;
        float n4 = (float)this.motive.w;
        if (dir == 0 || dir == 2) {
            n4 = 0.5f;
        }
        else {
            n2 = 0.5f;
        }
        final float n5 = n2 / 32.0f;
        final float n6 = n3 / 32.0f;
        final float n7 = n4 / 32.0f;
        float n8 = this.xTile + 0.5f;
        final float n9 = this.yTile + 0.5f;
        float n10 = this.zTile + 0.5f;
        final float n11 = 0.5625f;
        if (dir == 0) {
            n10 -= n11;
        }
        if (dir == 1) {
            n8 -= n11;
        }
        if (dir == 2) {
            n10 += n11;
        }
        if (dir == 3) {
            n8 += n11;
        }
        if (dir == 0) {
            n8 -= this.offs(this.motive.w);
        }
        if (dir == 1) {
            n10 += this.offs(this.motive.w);
        }
        if (dir == 2) {
            n8 += this.offs(this.motive.w);
        }
        if (dir == 3) {
            n10 -= this.offs(this.motive.w);
        }
        final float n12 = n9 + this.offs(this.motive.h);
        this.setPos(n8, n12, n10);
        final float n13 = -0.00625f;
        this.bb.set(n8 - n5 - n13, n12 - n6 - n13, n10 - n7 - n13, n8 + n5 + n13, n12 + n6 + n13, n10 + n7 + n13);
    }
    
    private float offs(final int w) {
        if (w == 32) {
            return 0.5f;
        }
        if (w == 64) {
            return 0.5f;
        }
        return 0.0f;
    }
    
    @Override
    public void tick() {
        if (this.checkInterval++ == 100 && !this.level.isClientSide) {
            this.checkInterval = 0;
            if (!this.survives()) {
                this.remove();
                this.level.addEntity(new ItemEntity(this.level, this.x, this.y, this.z, new ItemInstance(Item.painting)));
            }
        }
    }
    
    public boolean survives() {
        if (this.level.getCubes(this, this.bb).size() > 0) {
            return false;
        }
        final int n = this.motive.w / 16;
        final int n2 = this.motive.h / 16;
        int n3 = this.xTile;
        final int yTile = this.yTile;
        int n4 = this.zTile;
        if (this.dir == 0) {
            n3 = Mth.floor(this.x - this.motive.w / 32.0f);
        }
        if (this.dir == 1) {
            n4 = Mth.floor(this.z - this.motive.w / 32.0f);
        }
        if (this.dir == 2) {
            n3 = Mth.floor(this.x - this.motive.w / 32.0f);
        }
        if (this.dir == 3) {
            n4 = Mth.floor(this.z - this.motive.w / 32.0f);
        }
        final int floor = Mth.floor(this.y - this.motive.h / 32.0f);
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n2; ++j) {
                Material material;
                if (this.dir == 0 || this.dir == 2) {
                    material = this.level.getMaterial(n3 + i, floor + j, this.zTile);
                }
                else {
                    material = this.level.getMaterial(this.xTile, floor + j, n4 + i);
                }
                if (!material.isSolid()) {
                    return false;
                }
            }
        }
        final List entities = this.level.getEntities(this, this.bb);
        for (int k = 0; k < entities.size(); ++k) {
            if (entities.get(k) instanceof Painting) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean isPickable() {
        return true;
    }
    
    @Override
    public boolean hurt(final Entity source, final int dmg) {
        if (!this.removed && !this.level.isClientSide) {
            this.remove();
            this.markHurt();
            this.level.addEntity(new ItemEntity(this.level, this.x, this.y, this.z, new ItemInstance(Item.painting)));
        }
        return true;
    }
    
    public void addAdditionalSaveData(final CompoundTag compoundTag) {
        compoundTag.putByte("Dir", (byte)this.dir);
        compoundTag.putString("Motive", this.motive.name);
        compoundTag.putInt("TileX", this.xTile);
        compoundTag.putInt("TileY", this.yTile);
        compoundTag.putInt("TileZ", this.zTile);
    }
    
    public void readAdditionalSaveData(final CompoundTag compoundTag) {
        this.dir = compoundTag.getByte("Dir");
        this.xTile = compoundTag.getInt("TileX");
        this.yTile = compoundTag.getInt("TileY");
        this.zTile = compoundTag.getInt("TileZ");
        final String string = compoundTag.getString("Motive");
        for (final Painting_Motive motive : Painting_Motive.values()) {
            if (motive.name.equals(string)) {
                this.motive = motive;
            }
        }
        if (this.motive == null) {
            this.motive = Painting_Motive.Kebab;
        }
        this.setDir(this.dir);
    }
    
    @Override
    public void move(final double xa, final double ya, final double za) {
        if (!this.level.isClientSide && xa * xa + ya * ya + za * za > 0.0) {
            this.remove();
            this.level.addEntity(new ItemEntity(this.level, this.x, this.y, this.z, new ItemInstance(Item.painting)));
        }
    }
    
    @Override
    public void push(final double xa, final double ya, final double za) {
        if (!this.level.isClientSide && xa * xa + ya * ya + za * za > 0.0) {
            this.remove();
            this.level.addEntity(new ItemEntity(this.level, this.x, this.y, this.z, new ItemInstance(Item.painting)));
        }
    }
}
