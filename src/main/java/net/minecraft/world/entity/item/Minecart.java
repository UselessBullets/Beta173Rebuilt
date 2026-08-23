// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.item;

import net.minecraft.SharedConstants;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;
import com.mojang.nbt.ListTag;
import com.mojang.nbt.CompoundTag;
import java.util.List;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.tile.RailTile;
import util.Mth;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;

public class Minecart extends Entity implements Container
{
    public static final int RIDEABLE = 0;
    public static final int CHEST = 1;
    public static final int FURNACE = 2;
    private ItemInstance[] items = new ItemInstance[36];
    public int damage = 0;
    public int hurtTime = 0;
    public int hurtDir = 1;
    private boolean flipped = false;
    public int type;
    public int fuel;
    public double xPush, zPush;
    private static final int[][][] EXITS = new int[][][] { //
            //
            {{ +0, +0, -1 }, { +0, +0, +1 }}, // 0
            {{ -1, +0, +0 }, { +1, +0, +0 }}, // 1
            {{ -1, -1, +0 }, { +1, +0, +0 }}, // 2
            {{ -1, +0, +0 }, { +1, -1, +0 }}, // 3
            {{ +0, +0, -1 }, { +0, -1, +1 }}, // 4
            {{ +0, -1, -1 }, { +0, +0, +1 }}, // 5
            {{ +0, +0, +1 }, { +1, +0, +0 }}, // 6
            {{ +0, +0, +1 }, { -1, +0, +0 }}, // 7
            {{ +0, +0, -1 }, { -1, +0, +0 }}, // 8
            {{ +0, +0, -1 }, { +1, +0, +0 }}, // 9
    };
    private int lSteps;
    private double lx, ly, lz, lyr, lxr;
    private double lxd, lyd, lzd;
    
    public Minecart(final Level level) {
        super(level);
        this.blocksBuilding = true;
        this.setSize(0.98f, 0.7f);
        this.heightOffset = this.bbHeight / 2.0f;
    }
    
    @Override
    protected boolean makeStepSound() {
        return false;
    }
    
    @Override
    protected void definedSynchedData() {
    }
    
    @Override
    public AABB getCollideAgainstBox(final Entity entity) {
        return entity.bb;
    }
    
    @Override
    public AABB getCollideBox() {
        return null;
    }
    
    @Override
    public boolean isPushable() {
        return true;
    }
    
    public Minecart(final Level level, final double x, final double y, final double z, final int type) {
        this(level);

        this.setPos(x, y + this.heightOffset, z);

        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;

        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.type = type;
    }
    
    @Override
    public double getRideHeight() {
        return this.bbHeight * 0.0 - 0.3f;
    }
    
    @Override
    public boolean hurt(final Entity source, final int dmg) {
        if (this.level.isClientSide || this.removed) return true;

        this.hurtDir = -this.hurtDir;
        this.hurtTime = 10;
        this.markHurt();

        this.damage += dmg * 10;
        if (this.damage > 20 * 2) {
            if (this.rider != null) this.rider.ride(this);
            this.remove();

            this.spawnAtLocation(Item.minecart.id, 1, 0.0f);
            if (this.type == Minecart.CHEST) {
                for (int i = 0; i < this.getContainerSize(); ++i) {
                    final ItemInstance item = this.getItem(i);
                    if (item != null) {
                        final float xo = this.random.nextFloat() * 0.8f + 0.1f;
                        final float yo = this.random.nextFloat() * 0.8f + 0.1f;
                        final float zo = this.random.nextFloat() * 0.8f + 0.1f;

                        while (item.count > 0) {
                            int count = this.random.nextInt(21) + 10;
                            if (count > item.count) count = item.count;
                            item.count -= count;

                            final ItemEntity itemEntity = new ItemEntity(this.level, this.x + xo, this.y + yo, this.z + zo, new ItemInstance(item.id, count, item.getAuxValue()));
                            final float pow = 0.05f;
                            itemEntity.xd = (float)this.random.nextGaussian() * pow;
                            itemEntity.yd = (float)this.random.nextGaussian() * pow + 0.2f;
                            itemEntity.zd = (float)this.random.nextGaussian() * pow;
                            this.level.addEntity(itemEntity);
                        }
                    }
                }
                this.spawnAtLocation(Tile.chest.id, 1, 0.0f);
            }
            else if (this.type == Minecart.FURNACE) {
                this.spawnAtLocation(Tile.furnace.id, 1, 0.0f);
            }
        }
        return true;
    }
    
    @Override
    public void animateHurt() {
        System.out.println("Animating hurt");
        this.hurtDir = -this.hurtDir;
        this.hurtTime = 10;
        this.damage += this.damage * 10;
    }
    
    @Override
    public boolean isPickable() {
        return !this.removed;
    }
    
    @Override
    public void remove() {
        for (int i = 0; i < this.getContainerSize(); ++i) {
            final ItemInstance item = this.getItem(i);
            if (item != null) {
                final float xo = this.random.nextFloat() * 0.8f + 0.1f;
                final float yo = this.random.nextFloat() * 0.8f + 0.1f;
                final float zo = this.random.nextFloat() * 0.8f + 0.1f;

                while (item.count > 0) {
                    int count = this.random.nextInt(21) + 10;
                    if (count > item.count) count = item.count;
                    item.count -= count;

                    final ItemEntity itemEntity = new ItemEntity(this.level, this.x + xo, this.y + yo, this.z + zo, new ItemInstance(item.id, count, item.getAuxValue()));
                    final float pow = 0.05f;
                    itemEntity.xd = (float)this.random.nextGaussian() * pow;
                    itemEntity.yd = (float)this.random.nextGaussian() * pow + 0.2f;
                    itemEntity.zd = (float)this.random.nextGaussian() * pow;
                    this.level.addEntity(itemEntity);
                }
            }
        }
        super.remove();
    }
    
    @Override
    public void tick() {
        if (this.hurtTime > 0) this.hurtTime--;
        if (this.damage > 0) this.damage--;

        if (this.level.isClientSide && this.lSteps > 0) {
            if (this.lSteps > 0) {
                final double xt = this.x + (this.lx - this.x) / this.lSteps;
                final double yt = this.y + (this.ly - this.y) / this.lSteps;
                final double zt = this.z + (this.lz - this.z) / this.lSteps;

                double yrd = this.lyr - this.yRot;
                while (yrd < -180.0) yrd += 360.0;
                while (yrd >= 180.0) yrd -= 360.0;

                this.yRot += (float)(yrd / this.lSteps);
                this.xRot += (float)((this.lxr - this.xRot) / this.lSteps);

                this.lSteps--;
                this.setPos(xt, yt, zt);
                this.setRot(this.yRot, this.xRot);
            }
            else {
                this.setPos(this.x, this.y, this.z);
                this.setRot(this.yRot, this.xRot);
            }

            return;
        }
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        this.yd -= 0.04f;

        int xt = Mth.floor(this.x);
        int yt = Mth.floor(this.y);
        int zt = Mth.floor(this.z);
        if (RailTile.isRail(this.level, xt, yt - 1, zt)) {
            --yt;
        }

        final double max = 0.4;

        boolean isFurnacePowered = false; // Useless - Name is a guess, but this is true when the cart is a furnace that is actively moving, used to determine when it should decrement the fuel counter
        final double slideSpeed = 1 / 128.0;
        final int tile = this.level.getTile(xt, yt, zt);
        if (RailTile.isRail(tile)) {
            final Vec3 oldPos = this.getPos(this.x, this.y, this.z);
            int data = this.level.getData(xt, yt, zt);
            this.y = yt;

            boolean powerTrack = false;
            boolean haltTrack = false;
            if (tile == Tile.goldenRail.id) {
                powerTrack = (data & RailTile.RAIL_DATA_BIT) != 0x0;
                haltTrack = !powerTrack;
            }
            if (((RailTile)Tile.tiles[tile]).isUsesDataBit()) {
                data &= RailTile.RAIL_DIRECTION_MASK;
            }

            if (data >= 2 && data <= 5) {
                this.y = yt + 1;
            }

            if (data == 2) this.xd -= slideSpeed;
            if (data == 3) this.xd += slideSpeed;
            if (data == 4) this.zd += slideSpeed;
            if (data == 5) this.zd -= slideSpeed;

            final int[][] exits = Minecart.EXITS[data];
            double xD = exits[1][0] - exits[0][0];
            double zD = exits[1][2] - exits[0][2];
            final double dd = Math.sqrt(xD * xD + zD * zD);

            double flip = this.xd * xD + this.zd * zD;
            if (flip < 0.0) {
                xD = -xD;
                zD = -zD;
            }

            double pow = Math.sqrt(this.xd * this.xd + this.zd * this.zd);

            this.xd = pow * xD / dd;
            this.zd = pow * zD / dd;

            // on golden rails without power, stop the cart
            if (haltTrack) {
                double speedLength = Math.sqrt(this.xd * this.xd + this.zd * this.zd);
                if (speedLength < 0.03) {
                    this.xd *= 0.0;
                    this.yd *= 0.0;
                    this.zd *= 0.0;
                }
                else {
                    this.xd *= 0.5;
                    this.yd *= 0.0;
                    this.zd *= 0.5;
                }
            }

            double progress = 0;
            final double x0 = xt + 0.5 + exits[0][0] * 0.5;
            final double z0 = zt + 0.5 + exits[0][2] * 0.5;
            final double x1 = xt + 0.5 + exits[1][0] * 0.5;
            final double z1 = zt + 0.5 + exits[1][2] * 0.5;

            xD = x1 - x0;
            zD = z1 - z0;

            if (xD == 0.0) {
                this.x = xt + 0.5;
                progress = this.z - zt;
            }
            else if (zD == 0.0) {
                this.z = zt + 0.5;
                progress = this.x - xt;
            }
            else {
                double xx = this.x - x0;
                double zz = this.z - z0;

                progress = (xx * xD + zz * zD) * 2.0;
            }

            this.x = x0 + xD * progress;
            this.z = z0 + zD * progress;

            this.setPos(this.x, this.y + this.heightOffset, this.z);

            double xdd = this.xd;
            double zdd = this.zd;
            if (this.rider != null) {
                xdd *= 0.75;
                zdd *= 0.75;
            }
            if (xdd < -max) xdd = -max;
            if (xdd > max) xdd = max;
            if (zdd < -max) zdd = -max;
            if (zdd > max) zdd = max;
            this.move(xdd, 0.0, zdd);

            if (exits[0][1] != 0 && Mth.floor(this.x) - xt == exits[0][0] && Mth.floor(this.z) - zt == exits[0][2]) {
                this.setPos(this.x, this.y + exits[0][1], this.z);
            }
            else if (exits[1][1] != 0 && Mth.floor(this.x) - xt == exits[1][0] && Mth.floor(this.z) - zt == exits[1][2]) {
                this.setPos(this.x, this.y + exits[1][1], this.z);
            }

            if (this.rider != null) {
                this.xd *= 0.997f;
                this.yd *= 0.0;
                this.zd *= 0.997f;
            }
            else {
                if (this.type == Minecart.FURNACE) {
                    final double sd = Mth.sqrt(this.xPush * this.xPush + this.zPush * this.zPush);
                    if (sd > 0.01) {
                        isFurnacePowered = true;
                        this.xPush /= sd;
                        this.zPush /= sd;
                        final double speed = 0.04;
                        this.xd *= 0.8f;
                        this.yd *= 0.0;
                        this.zd *= 0.8f;
                        this.xd += this.xPush * speed;
                        this.zd += this.zPush * speed;
                    }
                    else {
                        this.xd *= 0.90f;
                        this.yd *= 0.0;
                        this.zd *= 0.90f;
                    }
                }
                this.xd *= 0.96f;
                this.yd *= 0.0;
                this.zd *= 0.96f;
            }

            final Vec3 newPos = this.getPos(this.x, this.y, this.z);
            if (newPos != null && oldPos != null) {
                final double speed = (oldPos.y - newPos.y) * 0.05;

                pow = Math.sqrt(this.xd * this.xd + this.zd * this.zd);
                if (pow > 0.0) {
                    this.xd = this.xd / pow * (pow + speed);
                    this.zd = this.zd / pow * (pow + speed);
                }
                this.setPos(this.x, newPos.y, this.z);
            }

            final int xn = Mth.floor(this.x);
            final int zn = Mth.floor(this.z);
            if (xn != xt || zn != zt) {
                pow = Math.sqrt(this.xd * this.xd + this.zd * this.zd);

                this.xd = pow * (xn - xt);
                this.zd = pow * (zn - zt);
            }

            if (this.type == Minecart.FURNACE) {
                final double sd = Mth.sqrt(this.xPush * this.xPush + this.zPush * this.zPush);
                if (sd > 0.01 && this.xd * this.xd + this.zd * this.zd > 0.001) {
                    this.xPush /= sd;
                    this.zPush /= sd;

                    if (this.xPush * this.xd + this.zPush * this.zd < 0.0) {
                        this.xPush = 0.0;
                        this.zPush = 0.0;
                    }
                    else {
                        this.xPush = this.xd;
                        this.zPush = this.zd;
                    }
                }
            }

            // if on golden rail with power, increase speed
            if (powerTrack) {
                final double speedLength = Math.sqrt(this.xd * this.xd + this.zd * this.zd);
                if (speedLength > 0.01) {
                    final double speed = 0.06;
                    this.xd += this.xd / speedLength * speed;
                    this.zd += this.zd / speedLength * speed;
                }
                else {
                    // if the minecart is standing still, accelerate it away
                    // from potentional walls
                    if (data == RailTile.DIR_FLAT_X) {
                        if (this.level.isSolidBlockingTile(xt - 1, yt, zt)) {
                            this.xd = 0.02;
                        } else if (this.level.isSolidBlockingTile(xt + 1, yt, zt)) {
                            this.xd = -0.02;
                        }
                    } else if (data == RailTile.DIR_FLAT_Z) {
                        if (this.level.isSolidBlockingTile(xt, yt, zt - 1)) {
                            this.zd = 0.02;
                        } else if (this.level.isSolidBlockingTile(xt, yt, zt + 1)) {
                            this.zd = -0.02;
                        }
                    }
                }
            }
        }
        else {
            if (this.xd < -max) this.xd = -max;
            if (this.xd > max) this.xd = max;
            if (this.zd < -max) this.zd = -max;
            if (this.zd > max) this.zd = max;
            if (this.onGround) {
                this.xd *= 0.5;
                this.yd *= 0.5;
                this.zd *= 0.5;
            }
            this.move(this.xd, this.yd, this.zd);

            if (!this.onGround) {
                this.xd *= 0.95f;
                this.yd *= 0.95f;
                this.zd *= 0.95f;
            }
        }

        this.xRot = 0.0f;
        final double xDiff = this.xo - this.x;
        final double zDiff = this.zo - this.z;
        if (xDiff * xDiff + zDiff * zDiff > 0.001) {
            this.yRot = (float)(Math.atan2(zDiff, xDiff) * 180.0 / Math.PI);
            if (this.flipped) this.yRot += 180.0f;
        }

        double rotDiff = this.yRot - this.yRotO;
        while (rotDiff >= 180.0) rotDiff -= 360.0;
        while (rotDiff < -180.0) rotDiff += 360.0;

        if (rotDiff < -170.0 || rotDiff >= 170.0) {
            this.yRot += 180.0f;
            this.flipped = !this.flipped;
        }
        this.setRot(this.yRot, this.xRot);

        final List<Entity> entities = this.level.getEntities(this, this.bb.grow(0.2f, 0.0, 0.2f));
        if (entities != null && entities.size() > 0) {
            for (int i = 0; i < entities.size(); ++i) {
                final Entity e = entities.get(i);
                if (e != this.rider && e.isPushable() && e instanceof Minecart) {
                    e.push(this);
                }
            }
        }

        if (this.rider != null && this.rider.removed) {
            this.rider = null;
        }

        if (isFurnacePowered && this.random.nextInt(4) == 0) {
            this.fuel--;
            if (this.fuel < 0) this.xPush = this.zPush = 0.0;
            this.level.addParticle("largesmoke", this.x, this.y + 0.8, this.z, 0.0, 0.0, 0.0);
        }
    }
    
    public Vec3 getPosOffs(double x, double y, double z, final double offs) {
        int xt = Mth.floor(x);
        int yt = Mth.floor(y);
        int zt = Mth.floor(z);
        if (RailTile.isRail(this.level, xt, yt - 1, zt)) {
            --yt;
        }

        final int tile = this.level.getTile(xt, yt, zt);
        if (RailTile.isRail(tile)) {
            int data = this.level.getData(xt, yt, zt);

            if (((RailTile)Tile.tiles[tile]).isUsesDataBit()) {
                data &= RailTile.RAIL_DIRECTION_MASK;
            }

            y = yt;
            if (data >= 2 && data <= 5) {
                y = yt + 1;
            }

            int[][] exits = Minecart.EXITS[data];
            double xD = exits[1][0] - exits[0][0];
            double zD = exits[1][2] - exits[0][2];
            double dd = Math.sqrt(xD * xD + zD * zD);
            xD /= dd;
            zD /= dd;

            x += xD * offs;
            z += zD * offs;

            if (exits[0][1] != 0 && Mth.floor(x) - xt == exits[0][0] && Mth.floor(z) - zt == exits[0][2]) {
                y += exits[0][1];
            }
            else if (exits[1][1] != 0 && Mth.floor(x) - xt == exits[1][0] && Mth.floor(z) - zt == exits[1][2]) {
                y += exits[1][1];
            }

            return this.getPos(x, y, z);
        }
        return null;
    }
    
    public Vec3 getPos(double x, double y, double z) {
        int xt = Mth.floor(x);
        int yt = Mth.floor(y);
        int zt = Mth.floor(z);
        if (RailTile.isRail(this.level, xt, yt - 1, zt)) {
            --yt;
        }

        final int tile = this.level.getTile(xt, yt, zt);
        if (RailTile.isRail(tile)) {
            int data = this.level.getData(xt, yt, zt);
            y = yt;

            if (((RailTile)Tile.tiles[tile]).isUsesDataBit()) {
                data &= RailTile.RAIL_DIRECTION_MASK;
            }

            if (data >= 2 && data <= 5) {
                y = yt + 1;
            }

            final int[][] exits = Minecart.EXITS[data];

            double progress = 0;
            final double x0 = xt + 0.5 + exits[0][0] * 0.5;
            final double y0 = yt + 0.5 + exits[0][1] * 0.5;
            final double z0 = zt + 0.5 + exits[0][2] * 0.5;
            final double x1 = xt + 0.5 + exits[1][0] * 0.5;
            final double y1 = yt + 0.5 + exits[1][1] * 0.5;
            final double z1 = zt + 0.5 + exits[1][2] * 0.5;

            final double xD = x1 - x0;
            final double yD = (y1 - y0) * 2.0;
            final double zD = z1 - z0;

            if (xD == 0.0) {
                x = xt + 0.5;
                progress = z - zt;
            }
            else if (zD == 0.0) {
                z = zt + 0.5;
                progress = x - xt;
            }
            else {
                double xx = (x - x0);
                double zz = (z - z0);

                progress = (xx * xD + zz * zD) * 2.0;
            }

            x = x0 + xD * progress;
            y = y0 + yD * progress;
            z = z0 + zD * progress;
            if (yD < 0.0) ++y;
            if (yD > 0.0) y += 0.5;
            return Vec3.newTemp(x, y, z);
        }
        return null;
    }
    
    @Override
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {
        compoundTag.putInt("Type", this.type);

        if (this.type == Minecart.FURNACE) {
            compoundTag.putDouble("PushX", this.xPush);
            compoundTag.putDouble("PushZ", this.zPush);
            compoundTag.putShort("Fuel", (short)this.fuel);
        }
        else if (this.type == Minecart.CHEST) {
            final ListTag<CompoundTag> listTag = new ListTag<>();

            for (int i = 0; i < this.items.length; ++i) {
                if (this.items[i] != null) {
                    final CompoundTag tag = new CompoundTag();
                    tag.putByte("Slot", (byte)i);
                    this.items[i].save(tag);
                    listTag.add(tag);
                }
            }
            compoundTag.put("Items", listTag);
        }
    }
    
    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {
        this.type = compoundTag.getInt("Type");
        if (this.type == Minecart.FURNACE) {
            this.xPush = compoundTag.getDouble("PushX");
            this.zPush = compoundTag.getDouble("PushZ");
            this.fuel = compoundTag.getShort("Fuel");
        }
        else if (this.type == Minecart.CHEST) {
            final ListTag<CompoundTag> inventoryList = (ListTag<CompoundTag>) compoundTag.getList("Items");
            this.items = new ItemInstance[this.getContainerSize()];
            for (int i = 0; i < inventoryList.size(); ++i) {
                final CompoundTag tag = inventoryList.get(i);
                final int slot = tag.getByte("Slot") & 0xFF;
                if (slot >= 0 && slot < this.items.length) this.items[slot] = new ItemInstance(tag);
            }
        }
    }
    
    @Override
    public float getShadowHeightOffs() {
        return 0.0f;
    }
    
    @Override
    public void push(final Entity e) {
        if (this.level.isClientSide) return;

        if (e == this.rider) return;
        if (e instanceof Mob && !(e instanceof Player) && this.type == 0 && this.xd * this.xd + this.zd * this.zd > 0.01) {
            if (this.rider == null && e.riding == null) {
                e.ride(this);
            }
        }

        double xa = e.x - this.x;
        double za = e.z - this.z;

        double dd = xa * xa + za * za;
        if (dd >= 0.0001f) {
            dd = Mth.sqrt(dd);
            xa /= dd;
            za /= dd;

            double pow = 1.0 / dd;
            if (pow > 1.0) pow = 1.0;
            xa *= pow;
            za *= pow;
            xa *= 0.1f;
            za *= 0.1f;

            xa *= (1.0f - this.pushthrough);
            za *= (1.0f - this.pushthrough);
            xa *= 0.5;
            za *= 0.5;

            if (e instanceof Minecart) {
                double xo = e.x - this.x;
                double zo = e.z - this.z;

                final double n15 = xo * e.zd + zo * e.xo; // Useless - unsure what a name for this variable should be
                if (n15 * n15 > 5.0) {
                    return;
                }

                double xdd = e.xd + this.xd;
                double zdd = e.zd + this.zd;
                if (((Minecart)e).type == Minecart.FURNACE && this.type != Minecart.FURNACE) {
                    this.xd *= 0.2f;
                    this.zd *= 0.2f;
                    this.push(e.xd - xa, 0.0, e.zd - za);
                    e.xd *= 0.7f;
                    e.zd *= 0.7f;
                }
                else if (((Minecart)e).type != Minecart.FURNACE && this.type == Minecart.FURNACE) {
                    e.xd *= 0.2f;
                    e.zd *= 0.2f;
                    e.push(this.xd + xa, 0.0, this.zd + za);
                    this.xd *= 0.7f;
                    this.zd *= 0.7f;
                }
                else {
                    xdd /= 2.0;
                    zdd /= 2.0;
                    this.xd *= 0.2f;
                    this.zd *= 0.2f;
                    this.push(xdd - xa, 0.0, zdd - za);
                    e.xd *= 0.2f;
                    e.zd *= 0.2f;
                    e.push(xdd + xa, 0.0, zdd + za);
                }
            }
            else {
                this.push(-xa, 0.0, -za);
                e.push(xa / 4.0, 0.0, za / 4.0);
            }
        }
    }
    
    public int getContainerSize() {
        return 9 * 3;
    }
    
    public ItemInstance getItem(final int slot) {
        return this.items[slot];
    }
    
    public ItemInstance removeItem(final int slot, final int count) {
        if (this.items[slot] == null) return null;

        if (this.items[slot].count <= count) {
            final ItemInstance item = this.items[slot];
            this.items[slot] = null;
            return item;
        } else {
            final ItemInstance i = this.items[slot].remove(count);
            if (this.items[slot].count == 0) this.items[slot] = null;
            return i;
        }
    }
    
    public void setItem(final int slot, final ItemInstance item) {
        this.items[slot] = item;
        if (item != null && item.count > this.getMaxStackSize()) item.count = this.getMaxStackSize();
    }
    
    public String getName() {
        return "Minecart";
    }
    
    public int getMaxStackSize() {
        return Container.LARGE_MAX_STACK_SIZE;
    }
    
    public void setChanged() {
    }
    
    @Override
    public boolean interact(final Player player) {
        if (this.type == Minecart.RIDEABLE) {
            if (this.rider != null && this.rider instanceof Player && this.rider != player) return true;
            if (!this.level.isClientSide) {
                player.ride(this);
            }
        }
        else if (this.type == Minecart.CHEST) {
            if (!this.level.isClientSide) {
                player.openContainer(this);
            }
        }
        else if (this.type == Minecart.FURNACE) {
            final ItemInstance selected = player.inventory.getSelected();
            if (selected != null && selected.id == Item.coal.id) {
                if (--selected.count == 0) player.inventory.setItem(player.inventory.selected, null);
                this.fuel += SharedConstants.TICKS_PER_SECOND * 90;
            }
            this.xPush = this.x - player.x;
            this.zPush = this.z - player.z;
        }
        return true;
    }
    
    @Override
    public void lerpTo(final double x, final double y, final double z, final float yRot, final float xRot, final int steps) {
        this.lx = x;
        this.ly = y;
        this.lz = z;
        this.lyr = yRot;
        this.lxr = xRot;

        this.lSteps = steps + 2;

        this.xd = this.lxd;
        this.yd = this.lyd;
        this.zd = this.lzd;
    }
    
    @Override
    public void lerpMotion(final double xd, final double yd, final double zd) {
        this.lxd = this.xd = xd;
        this.lyd = this.yd = yd;
        this.lzd = this.zd = zd;
    }
    
    public boolean stillValid(final Player player) {
        return !this.removed && player.distanceToSqr(this) <= 8 * 8;
    }

}
