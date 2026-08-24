// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity;

import com.mojang.nbt.CompoundTag;
import java.util.List;

import net.minecraft.SharedConstants;
import net.minecraft.world.level.material.Material;
import util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.Item;
import java.util.ArrayList;
import net.minecraft.world.level.Level;

public class Painting extends Entity
{
    private int checkInterval = 0;
    public int dir = 0;
    public int xTile, yTile, zTile;
    public Motive motive;
    
    public Painting(final Level level) {
        super(level);
        this.heightOffset = 0.0f;
        this.setSize(0.5f, 0.5f);
    }
    
    public Painting(final Level level, final int xTile, final int yTile, final int zTile, final int dir) {
        this(level);
        this.xTile = xTile;
        this.yTile = yTile;
        this.zTile = zTile;

        List<Motive> survivableMotives = new ArrayList<>();
        for (final Motive motive : Motive.values()) {
            this.motive = motive;
            this.setDir(dir);
            if (this.survives()) {
                survivableMotives.add(motive);
            }
        }
        if (!survivableMotives.isEmpty()) {
            this.motive = survivableMotives.get(this.random.nextInt(survivableMotives.size()));
        }
        this.setDir(dir);
    }
    
    public Painting(final Level level, final int x, final int y, final int z, final int dir, final String motiveName) {
        this(level);
        this.xTile = x;
        this.yTile = y;
        this.zTile = z;

        for (final Motive motive : Motive.values()) {
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
        this.yRotO = this.yRot = (float)(dir * 90);

        float w = (float)this.motive.w;
        float h = (float)this.motive.h;
        float d = (float)this.motive.w;

        if (dir == 0 || dir == 2) {
            d = 0.5f;
        }
        else {
            w = 0.5f;
        }

        w /= 32.0f;
        h /= 32.0f;
        d /= 32.0f;

        float x = this.xTile + 0.5f;
        float y = this.yTile + 0.5f;
        float z = this.zTile + 0.5f;

        final float fOffs = 0.5f + 1.0f / 16.0f;

        if (dir == 0) z -= fOffs;
        if (dir == 1) x -= fOffs;
        if (dir == 2) z += fOffs;
        if (dir == 3) x += fOffs;

        if (dir == 0) x -= this.offs(this.motive.w);
        if (dir == 1) z += this.offs(this.motive.w);
        if (dir == 2) x += this.offs(this.motive.w);
        if (dir == 3) z -= this.offs(this.motive.w);
        y += this.offs(this.motive.h);

        this.setPos(x, y, z);

        final float ss = -(0.1f / 16.0f);

        double x0 = x - w - ss;
        double x1 = x + w + ss;
        double y0 = y - h - ss;
        double y1 = y + h + ss;
        double z0 = z - d - ss;
        double z1 = z + d + ss;
        this.bb.set(x0, y0, z0, x1, y1, z1);
    }
    
    private float offs(final int w) {
        if (w == 32) return 0.5f;
        if (w == 64) return 0.5f;
        return 0.0f;
    }
    
    @Override
    public void tick() {
        if (this.checkInterval++ == SharedConstants.TICKS_PER_SECOND * 5 && !this.level.isClientSide) {
            this.checkInterval = 0;
            if (!this.survives()) {
                this.remove();
                this.level.addEntity(new ItemEntity(this.level, this.x, this.y, this.z, new ItemInstance(Item.painting)));
            }
        }
    }
    
    public boolean survives() {
        if (!this.level.getCubes(this, this.bb).isEmpty()) return false;

        final int ws = this.motive.w / 16;
        final int hs = this.motive.h / 16;

        int xt = this.xTile;
        int yt = this.yTile;
        int zt = this.zTile;
        if (this.dir == 0) xt = Mth.floor(this.x - this.motive.w / 32.0f);
        if (this.dir == 1) zt = Mth.floor(this.z - this.motive.w / 32.0f);
        if (this.dir == 2) xt = Mth.floor(this.x - this.motive.w / 32.0f);
        if (this.dir == 3) zt = Mth.floor(this.z - this.motive.w / 32.0f);
        yt = Mth.floor(this.y - this.motive.h / 32.0f);

        for (int ss = 0; ss < ws; ++ss) {
            for (int yy = 0; yy < hs; ++yy) {
                Material m;
                if (this.dir == 0 || this.dir == 2) {
                    m = this.level.getMaterial(xt + ss, yt + yy, this.zTile);
                }
                else {
                    m = this.level.getMaterial(this.xTile, yt + yy, zt + ss);
                }
                if (!m.isSolid()) {
                    return false;
                }
            }
        }

        final List<Entity> entities = this.level.getEntities(this, this.bb);
        for (int i = 0; i < entities.size(); ++i) {
            Entity e = entities.get(i);
            if (e instanceof Painting) {
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
        final String motiveName = compoundTag.getString("Motive");
        for (final Motive motive : Motive.values()) {
            if (motive.name.equals(motiveName)) {
                this.motive = motive;
            }
        }
        if (this.motive == null) this.motive = Motive.Kebab;
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

    public enum Motive
    {
        Kebab("Kebab", 16, 16, 0, 0),
        Aztec("Aztec", 16, 16, 16, 0),
        Alban("Alban", 16, 16, 32, 0),
        Aztec2("Aztec2", 16, 16, 48, 0),
        Bomb("Bomb", 16, 16, 64, 0),
        Plant("Plant", 16, 16, 80, 0),
        Wasteland("Wasteland", 16, 16, 96, 0),
        Pool("Pool", 32, 16, 0, 32),
        Courbet("Courbet", 32, 16, 32, 32),
        Sea("Sea", 32, 16, 64, 32),
        Sunset("Sunset", 32, 16, 96, 32),
        Creebet("Creebet", 32, 16, 128, 32),
        Wanderer("Wanderer", 16, 32, 0, 64),
        Graham("Graham", 16, 32, 16, 64),
        Match("Match", 32, 32, 0, 128),
        Bust("Bust", 32, 32, 32, 128),
        Stage("Stage", 32, 32, 64, 128),
        Void("Void", 32, 32, 96, 128),
        SkullAndRoses("SkullAndRoses", 32, 32, 128, 128),
        Fighters("Fighters", 64, 32, 0, 96),
        Pointer("Pointer", 64, 64, 0, 192),
        Pigscene("Pigscene", 64, 64, 64, 192),
        BurningSkull("BurningSkull", 64, 64, 128, 192),
        Skeleton("Skeleton", 64, 48, 192, 64),
        DonkeyKong("DonkeyKong", 64, 48, 192, 112);

        public static final int MAX_MOTIVE_NAME_LENGTH = "SkullAndRoses".length();
        public final String name;
        public final int w, h;
        public final int uo, vo;

        Motive(final String name, final int w, final int h, final int uo, final int vo) {
            this.name = name;
            this.w = w;
            this.h = h;
            this.uo = uo;
            this.vo = vo;
        }
    }
}
