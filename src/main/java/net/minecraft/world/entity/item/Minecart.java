// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.item;

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
    public double xPush;
    public double zPush;
    private static final int[][][] EXITS;
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;
    
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
        if (this.level.isClientSide || this.removed) {
            return true;
        }
        this.hurtDir = -this.hurtDir;
        this.hurtTime = 10;
        this.markHurt();
        this.damage += dmg * 10;
        if (this.damage > 40) {
            if (this.rider != null) {
                this.rider.ride(this);
            }
            this.remove();
            this.spawnAtLocation(Item.minecart.id, 1, 0.0f);
            if (this.type == 1) {
                for (int i = 0; i < this.getContainerSize(); ++i) {
                    final ItemInstance item = this.getItem(i);
                    if (item != null) {
                        final float n = this.random.nextFloat() * 0.8f + 0.1f;
                        final float n2 = this.random.nextFloat() * 0.8f + 0.1f;
                        final float n3 = this.random.nextFloat() * 0.8f + 0.1f;
                        while (item.count > 0) {
                            int count = this.random.nextInt(21) + 10;
                            if (count > item.count) {
                                count = item.count;
                            }
                            final ItemInstance itemInstance = item;
                            itemInstance.count -= count;
                            final ItemEntity e = new ItemEntity(this.level, this.x + n, this.y + n2, this.z + n3, new ItemInstance(item.id, count, item.getAuxValue()));
                            final float n4 = 0.05f;
                            e.xd = (float)this.random.nextGaussian() * n4;
                            e.yd = (float)this.random.nextGaussian() * n4 + 0.2f;
                            e.zd = (float)this.random.nextGaussian() * n4;
                            this.level.addEntity(e);
                        }
                    }
                }
                this.spawnAtLocation(Tile.chest.id, 1, 0.0f);
            }
            else if (this.type == 2) {
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
                final float n = this.random.nextFloat() * 0.8f + 0.1f;
                final float n2 = this.random.nextFloat() * 0.8f + 0.1f;
                final float n3 = this.random.nextFloat() * 0.8f + 0.1f;
                while (item.count > 0) {
                    int count = this.random.nextInt(21) + 10;
                    if (count > item.count) {
                        count = item.count;
                    }
                    final ItemInstance itemInstance = item;
                    itemInstance.count -= count;
                    final ItemEntity e = new ItemEntity(this.level, this.x + n, this.y + n2, this.z + n3, new ItemInstance(item.id, count, item.getAuxValue()));
                    final float n4 = 0.05f;
                    e.xd = (float)this.random.nextGaussian() * n4;
                    e.yd = (float)this.random.nextGaussian() * n4 + 0.2f;
                    e.zd = (float)this.random.nextGaussian() * n4;
                    this.level.addEntity(e);
                }
            }
        }
        super.remove();
    }
    
    @Override
    public void tick() {
        if (this.hurtTime > 0) {
            --this.hurtTime;
        }
        if (this.damage > 0) {
            --this.damage;
        }
        if (this.level.isClientSide && this.lSteps > 0) {
            if (this.lSteps > 0) {
                final double x = this.x + (this.lx - this.x) / this.lSteps;
                final double y = this.y + (this.ly - this.y) / this.lSteps;
                final double z = this.z + (this.lz - this.z) / this.lSteps;
                double n;
                for (n = this.lyr - this.yRot; n < -180.0; n += 360.0) {}
                while (n >= 180.0) {
                    n -= 360.0;
                }
                this.yRot += (float)(n / this.lSteps);
                this.xRot += (float)((this.lxr - this.xRot) / this.lSteps);
                --this.lSteps;
                this.setPos(x, y, z);
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
        this.yd -= 0.03999999910593033;
        final int floor = Mth.floor(this.x);
        int floor2 = Mth.floor(this.y);
        final int floor3 = Mth.floor(this.z);
        if (RailTile.isRail(this.level, floor, floor2 - 1, floor3)) {
            --floor2;
        }
        final double n2 = 0.4;
        boolean b = false;
        final double n3 = 0.0078125;
        final int tile = this.level.getTile(floor, floor2, floor3);
        if (RailTile.isRail(tile)) {
            final Vec3 pos = this.getPos(this.x, this.y, this.z);
            int data = this.level.getData(floor, floor2, floor3);
            this.y = floor2;
            boolean b2 = false;
            int n4 = 0;
            if (tile == Tile.goldenRail.id) {
                b2 = ((data & 0x8) != 0x0);
                n4 = (b2 ? 0 : 1);
            }
            if (((RailTile)Tile.tiles[tile]).isUsesDataBit()) {
                data &= 0x7;
            }
            if (data >= 2 && data <= 5) {
                this.y = floor2 + 1;
            }
            if (data == 2) {
                this.xd -= n3;
            }
            if (data == 3) {
                this.xd += n3;
            }
            if (data == 4) {
                this.zd += n3;
            }
            if (data == 5) {
                this.zd -= n3;
            }
            final int[][] array = Minecart.EXITS[data];
            double n5 = array[1][0] - array[0][0];
            double n6 = array[1][2] - array[0][2];
            final double sqrt = Math.sqrt(n5 * n5 + n6 * n6);
            if (this.xd * n5 + this.zd * n6 < 0.0) {
                n5 = -n5;
                n6 = -n6;
            }
            final double sqrt2 = Math.sqrt(this.xd * this.xd + this.zd * this.zd);
            this.xd = sqrt2 * n5 / sqrt;
            this.zd = sqrt2 * n6 / sqrt;
            if (n4 != 0) {
                if (Math.sqrt(this.xd * this.xd + this.zd * this.zd) < 0.03) {
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
            final double n7 = floor + 0.5 + array[0][0] * 0.5;
            final double n8 = floor3 + 0.5 + array[0][2] * 0.5;
            final double n9 = floor + 0.5 + array[1][0] * 0.5;
            final double n10 = floor3 + 0.5 + array[1][2] * 0.5;
            final double n11 = n9 - n7;
            final double n12 = n10 - n8;
            double n13;
            if (n11 == 0.0) {
                this.x = floor + 0.5;
                n13 = this.z - floor3;
            }
            else if (n12 == 0.0) {
                this.z = floor3 + 0.5;
                n13 = this.x - floor;
            }
            else {
                n13 = ((this.x - n7) * n11 + (this.z - n8) * n12) * 2.0;
            }
            this.x = n7 + n11 * n13;
            this.z = n8 + n12 * n13;
            this.setPos(this.x, this.y + this.heightOffset, this.z);
            double xd = this.xd;
            double zd = this.zd;
            if (this.rider != null) {
                xd *= 0.75;
                zd *= 0.75;
            }
            if (xd < -n2) {
                xd = -n2;
            }
            if (xd > n2) {
                xd = n2;
            }
            if (zd < -n2) {
                zd = -n2;
            }
            if (zd > n2) {
                zd = n2;
            }
            this.move(xd, 0.0, zd);
            if (array[0][1] != 0 && Mth.floor(this.x) - floor == array[0][0] && Mth.floor(this.z) - floor3 == array[0][2]) {
                this.setPos(this.x, this.y + array[0][1], this.z);
            }
            else if (array[1][1] != 0 && Mth.floor(this.x) - floor == array[1][0] && Mth.floor(this.z) - floor3 == array[1][2]) {
                this.setPos(this.x, this.y + array[1][1], this.z);
            }
            if (this.rider != null) {
                this.xd *= 0.996999979019165;
                this.yd *= 0.0;
                this.zd *= 0.996999979019165;
            }
            else {
                if (this.type == 2) {
                    final double n14 = Mth.sqrt(this.xPush * this.xPush + this.zPush * this.zPush);
                    if (n14 > 0.01) {
                        b = true;
                        this.xPush /= n14;
                        this.zPush /= n14;
                        final double n15 = 0.04;
                        this.xd *= 0.8f;
                        this.yd *= 0.0;
                        this.zd *= 0.8f;
                        this.xd += this.xPush * n15;
                        this.zd += this.zPush * n15;
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
            final Vec3 pos2 = this.getPos(this.x, this.y, this.z);
            if (pos2 != null && pos != null) {
                final double n16 = (pos.y - pos2.y) * 0.05;
                final double sqrt3 = Math.sqrt(this.xd * this.xd + this.zd * this.zd);
                if (sqrt3 > 0.0) {
                    this.xd = this.xd / sqrt3 * (sqrt3 + n16);
                    this.zd = this.zd / sqrt3 * (sqrt3 + n16);
                }
                this.setPos(this.x, pos2.y, this.z);
            }
            final int floor4 = Mth.floor(this.x);
            final int floor5 = Mth.floor(this.z);
            if (floor4 != floor || floor5 != floor3) {
                final double sqrt4 = Math.sqrt(this.xd * this.xd + this.zd * this.zd);
                this.xd = sqrt4 * (floor4 - floor);
                this.zd = sqrt4 * (floor5 - floor3);
            }
            if (this.type == 2) {
                final double n17 = Mth.sqrt(this.xPush * this.xPush + this.zPush * this.zPush);
                if (n17 > 0.01 && this.xd * this.xd + this.zd * this.zd > 0.001) {
                    this.xPush /= n17;
                    this.zPush /= n17;
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
            if (b2) {
                final double sqrt5 = Math.sqrt(this.xd * this.xd + this.zd * this.zd);
                if (sqrt5 > 0.01) {
                    final double n18 = 0.06;
                    this.xd += this.xd / sqrt5 * n18;
                    this.zd += this.zd / sqrt5 * n18;
                }
                else if (data == 1) {
                    if (this.level.isSolidBlockingTile(floor - 1, floor2, floor3)) {
                        this.xd = 0.02;
                    }
                    else if (this.level.isSolidBlockingTile(floor + 1, floor2, floor3)) {
                        this.xd = -0.02;
                    }
                }
                else if (data == 0) {
                    if (this.level.isSolidBlockingTile(floor, floor2, floor3 - 1)) {
                        this.zd = 0.02;
                    }
                    else if (this.level.isSolidBlockingTile(floor, floor2, floor3 + 1)) {
                        this.zd = -0.02;
                    }
                }
            }
        }
        else {
            if (this.xd < -n2) {
                this.xd = -n2;
            }
            if (this.xd > n2) {
                this.xd = n2;
            }
            if (this.zd < -n2) {
                this.zd = -n2;
            }
            if (this.zd > n2) {
                this.zd = n2;
            }
            if (this.onGround) {
                this.xd *= 0.5;
                this.yd *= 0.5;
                this.zd *= 0.5;
            }
            this.move(this.xd, this.yd, this.zd);
            if (!this.onGround) {
                this.xd *= 0.949999988079071;
                this.yd *= 0.949999988079071;
                this.zd *= 0.949999988079071;
            }
        }
        this.xRot = 0.0f;
        final double x2 = this.xo - this.x;
        final double y2 = this.zo - this.z;
        if (x2 * x2 + y2 * y2 > 0.001) {
            this.yRot = (float)(Math.atan2(y2, x2) * 180.0 / Math.PI);
            if (this.flipped) {
                this.yRot += 180.0f;
            }
        }
        double n19;
        for (n19 = this.yRot - this.yRotO; n19 >= 180.0; n19 -= 360.0) {}
        while (n19 < -180.0) {
            n19 += 360.0;
        }
        if (n19 < -170.0 || n19 >= 170.0) {
            this.yRot += 180.0f;
            this.flipped = !this.flipped;
        }
        this.setRot(this.yRot, this.xRot);
        final List<Entity> entities = this.level.getEntities(this, this.bb.grow(0.2f, 0.0, 0.2f));
        if (entities != null && entities.size() > 0) {
            for (int i = 0; i < entities.size(); ++i) {
                final Entity entity = entities.get(i);
                if (entity != this.rider && entity.isPushable() && entity instanceof Minecart) {
                    entity.push(this);
                }
            }
        }
        if (this.rider != null && this.rider.removed) {
            this.rider = null;
        }
        if (b && this.random.nextInt(4) == 0) {
            --this.fuel;
            if (this.fuel < 0) {
                final double n20 = 0.0;
                this.zPush = n20;
                this.xPush = n20;
            }
            this.level.addParticle("largesmoke", this.x, this.y + 0.8, this.z, 0.0, 0.0, 0.0);
        }
    }
    
    public Vec3 getPosOffs(double x, double y, double z, final double offs) {
        final int floor = Mth.floor(x);
        int floor2 = Mth.floor(y);
        final int floor3 = Mth.floor(z);
        if (RailTile.isRail(this.level, floor, floor2 - 1, floor3)) {
            --floor2;
        }
        final int tile = this.level.getTile(floor, floor2, floor3);
        if (RailTile.isRail(tile)) {
            int data = this.level.getData(floor, floor2, floor3);
            if (((RailTile)Tile.tiles[tile]).isUsesDataBit()) {
                data &= 0x7;
            }
            y = floor2;
            if (data >= 2 && data <= 5) {
                y = floor2 + 1;
            }
            final int[][] array = Minecart.EXITS[data];
            final double n = array[1][0] - array[0][0];
            final double n2 = array[1][2] - array[0][2];
            final double sqrt = Math.sqrt(n * n + n2 * n2);
            final double n3 = n / sqrt;
            final double n4 = n2 / sqrt;
            x += n3 * offs;
            z += n4 * offs;
            if (array[0][1] != 0 && Mth.floor(x) - floor == array[0][0] && Mth.floor(z) - floor3 == array[0][2]) {
                y += array[0][1];
            }
            else if (array[1][1] != 0 && Mth.floor(x) - floor == array[1][0] && Mth.floor(z) - floor3 == array[1][2]) {
                y += array[1][1];
            }
            return this.getPos(x, y, z);
        }
        return null;
    }
    
    public Vec3 getPos(double x, double y, double z) {
        final int floor = Mth.floor(x);
        int floor2 = Mth.floor(y);
        final int floor3 = Mth.floor(z);
        if (RailTile.isRail(this.level, floor, floor2 - 1, floor3)) {
            --floor2;
        }
        final int tile = this.level.getTile(floor, floor2, floor3);
        if (RailTile.isRail(tile)) {
            int data = this.level.getData(floor, floor2, floor3);
            y = floor2;
            if (((RailTile)Tile.tiles[tile]).isUsesDataBit()) {
                data &= 0x7;
            }
            if (data >= 2 && data <= 5) {
                y = floor2 + 1;
            }
            final int[][] array = Minecart.EXITS[data];
            final double n = floor + 0.5 + array[0][0] * 0.5;
            final double n2 = floor2 + 0.5 + array[0][1] * 0.5;
            final double n3 = floor3 + 0.5 + array[0][2] * 0.5;
            final double n4 = floor + 0.5 + array[1][0] * 0.5;
            final double n5 = floor2 + 0.5 + array[1][1] * 0.5;
            final double n6 = floor3 + 0.5 + array[1][2] * 0.5;
            final double n7 = n4 - n;
            final double n8 = (n5 - n2) * 2.0;
            final double n9 = n6 - n3;
            double n10;
            if (n7 == 0.0) {
                x = floor + 0.5;
                n10 = z - floor3;
            }
            else if (n9 == 0.0) {
                z = floor3 + 0.5;
                n10 = x - floor;
            }
            else {
                n10 = ((x - n) * n7 + (z - n3) * n9) * 2.0;
            }
            x = n + n7 * n10;
            y = n2 + n8 * n10;
            z = n3 + n9 * n10;
            if (n8 < 0.0) {
                ++y;
            }
            if (n8 > 0.0) {
                y += 0.5;
            }
            return Vec3.newTemp(x, y, z);
        }
        return null;
    }
    
    @Override
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {
        compoundTag.putInt("Type", this.type);
        if (this.type == 2) {
            compoundTag.putDouble("PushX", this.xPush);
            compoundTag.putDouble("PushZ", this.zPush);
            compoundTag.putShort("Fuel", (short)this.fuel);
        }
        else if (this.type == 1) {
            final ListTag tag = new ListTag();
            for (int i = 0; i < this.items.length; ++i) {
                if (this.items[i] != null) {
                    final CompoundTag compoundTag2 = new CompoundTag();
                    compoundTag2.putByte("Slot", (byte)i);
                    this.items[i].save(compoundTag2);
                    tag.add(compoundTag2);
                }
            }
            compoundTag.put("Items", tag);
        }
    }
    
    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {
        this.type = compoundTag.getInt("Type");
        if (this.type == 2) {
            this.xPush = compoundTag.getDouble("PushX");
            this.zPush = compoundTag.getDouble("PushZ");
            this.fuel = compoundTag.getShort("Fuel");
        }
        else if (this.type == 1) {
            final ListTag list = compoundTag.getList("Items");
            this.items = new ItemInstance[this.getContainerSize()];
            for (int i = 0; i < list.size(); ++i) {
                final CompoundTag itemTag = (CompoundTag)list.get(i);
                final int n = itemTag.getByte("Slot") & 0xFF;
                if (n >= 0 && n < this.items.length) {
                    this.items[n] = new ItemInstance(itemTag);
                }
            }
        }
    }
    
    @Override
    public float getShadowHeightOffs() {
        return 0.0f;
    }
    
    @Override
    public void push(final Entity e) {
        if (this.level.isClientSide) {
            return;
        }
        if (e == this.rider) {
            return;
        }
        if (e instanceof Mob && !(e instanceof Player) && this.type == 0 && this.xd * this.xd + this.zd * this.zd > 0.01 && this.rider == null && e.riding == null) {
            e.ride(this);
        }
        final double n = e.x - this.x;
        final double n2 = e.z - this.z;
        final double x = n * n + n2 * n2;
        if (x >= 9.999999747378752E-5) {
            final double n3 = Mth.sqrt(x);
            final double n4 = n / n3;
            final double n5 = n2 / n3;
            double n6 = 1.0 / n3;
            if (n6 > 1.0) {
                n6 = 1.0;
            }
            final double n7 = n4 * n6;
            final double n8 = n5 * n6;
            final double n9 = n7 * 0.1f;
            final double n10 = n8 * 0.1f;
            final double n11 = n9 * (1.0f - this.pushthrough);
            final double n12 = n10 * (1.0f - this.pushthrough);
            final double n13 = n11 * 0.5;
            final double n14 = n12 * 0.5;
            if (e instanceof Minecart) {
                final double n15 = (e.x - this.x) * e.zd + (e.z - this.z) * e.xo;
                if (n15 * n15 > 5.0) {
                    return;
                }
                final double n16 = e.xd + this.xd;
                final double n17 = e.zd + this.zd;
                if (((Minecart)e).type == 2 && this.type != 2) {
                    this.xd *= 0.2f;
                    this.zd *= 0.2f;
                    this.push(e.xd - n13, 0.0, e.zd - n14);
                    e.xd *= 0.7f;
                    e.zd *= 0.7f;
                }
                else if (((Minecart)e).type != 2 && this.type == 2) {
                    e.xd *= 0.2f;
                    e.zd *= 0.2f;
                    e.push(this.xd + n13, 0.0, this.zd + n14);
                    this.xd *= 0.7f;
                    this.zd *= 0.7f;
                }
                else {
                    final double n18 = n16 / 2.0;
                    final double n19 = n17 / 2.0;
                    this.xd *= 0.2f;
                    this.zd *= 0.2f;
                    this.push(n18 - n13, 0.0, n19 - n14);
                    e.xd *= 0.2f;
                    e.zd *= 0.2f;
                    e.push(n18 + n13, 0.0, n19 + n14);
                }
            }
            else {
                this.push(-n13, 0.0, -n14);
                e.push(n13 / 4.0, 0.0, n14 / 4.0);
            }
        }
    }
    
    public int getContainerSize() {
        return 27;
    }
    
    public ItemInstance getItem(final int slot) {
        return this.items[slot];
    }
    
    public ItemInstance removeItem(final int slot, final int count) {
        if (this.items[slot] == null) {
            return null;
        }
        if (this.items[slot].count <= count) {
            final ItemInstance itemInstance = this.items[slot];
            this.items[slot] = null;
            return itemInstance;
        }
        final ItemInstance remove = this.items[slot].remove(count);
        if (this.items[slot].count == 0) {
            this.items[slot] = null;
        }
        return remove;
    }
    
    public void setItem(final int slot, final ItemInstance item) {
        this.items[slot] = item;
        if (item != null && item.count > this.getMaxStackSize()) {
            item.count = this.getMaxStackSize();
        }
    }
    
    public String getName() {
        return "Minecart";
    }
    
    public int getMaxStackSize() {
        return 64;
    }
    
    public void setChanged() {
    }
    
    @Override
    public boolean interact(final Player player) {
        if (this.type == 0) {
            if (this.rider != null && this.rider instanceof Player && this.rider != player) {
                return true;
            }
            if (!this.level.isClientSide) {
                player.ride(this);
            }
        }
        else if (this.type == 1) {
            if (!this.level.isClientSide) {
                player.openContainer(this);
            }
        }
        else if (this.type == 2) {
            final ItemInstance selected = player.inventory.getSelected();
            if (selected != null && selected.id == Item.coal.id) {
                final ItemInstance itemInstance = selected;
                if (--itemInstance.count == 0) {
                    player.inventory.setItem(player.inventory.selected, null);
                }
                this.fuel += 1200;
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
        this.xd = xd;
        this.lxd = xd;
        this.yd = yd;
        this.lyd = yd;
        this.zd = zd;
        this.lzd = zd;
    }
    
    public boolean stillValid(final Player player) {
        return !this.removed && player.distanceToSqr(this) <= 64.0;
    }
    
    static {
        EXITS = new int[][][] { { { 0, 0, -1 }, { 0, 0, 1 } }, { { -1, 0, 0 }, { 1, 0, 0 } }, { { -1, -1, 0 }, { 1, 0, 0 } }, { { -1, 0, 0 }, { 1, -1, 0 } }, { { 0, 0, -1 }, { 0, -1, 1 } }, { { 0, -1, -1 }, { 0, 0, 1 } }, { { 0, 0, 1 }, { 1, 0, 0 } }, { { 0, 0, 1 }, { -1, 0, 0 } }, { { 0, 0, -1 }, { -1, 0, 0 } }, { { 0, 0, -1 }, { 1, 0, 0 } } };
    }
}
