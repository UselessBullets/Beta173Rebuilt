// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity;

import net.minecraft.world.entity.item.Boat;
import net.minecraft.world.entity.item.Minecart;
import net.minecraft.world.entity.item.FallingTile;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.PigZombie;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.Arrow;
import java.util.HashMap;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import java.util.Map;

public class EntityIO
{
    private static Map idClassMap;
    private static Map classIdMao;
    private static Map numClassMap;
    private static Map classNumMap;
    
    private static void setId(final Class clazz, final String id, final int idNum) {
        EntityIO.idClassMap.put(id, clazz);
        EntityIO.classIdMao.put(clazz, id);
        EntityIO.numClassMap.put(idNum, clazz);
        EntityIO.classNumMap.put(clazz, idNum);
    }
    
    public static Entity newEntity(final String id, final Level level) {
        Entity entity = null;
        try {
            final Class clazz = EntityIO.idClassMap.get(id);
            if (clazz != null) {
                entity = (Entity)clazz.getConstructor(Level.class).newInstance(level);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return entity;
    }
    
    public static Entity loadStatic(final CompoundTag tag, final Level level) {
        Entity entity = null;
        try {
            final Class clazz = EntityIO.idClassMap.get(tag.getString("id"));
            if (clazz != null) {
                entity = (Entity)clazz.getConstructor(Level.class).newInstance(level);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        if (entity != null) {
            entity.load(tag);
        }
        else {
            System.out.println("Skipping Entity with id " + tag.getString("id"));
        }
        return entity;
    }
    
    public static int getId(final Entity entity) {
        return EntityIO.classNumMap.get(entity.getClass());
    }
    
    public static String getEncodeId(final Entity entity) {
        return EntityIO.classIdMao.get(entity.getClass());
    }
    
    static {
        EntityIO.idClassMap = new HashMap();
        EntityIO.classIdMao = new HashMap();
        EntityIO.numClassMap = new HashMap();
        EntityIO.classNumMap = new HashMap();
        setId(Arrow.class, "Arrow", 10);
        setId(Snowball.class, "Snowball", 11);
        setId(ItemEntity.class, "Item", 1);
        setId(Painting.class, "Painting", 9);
        setId(Mob.class, "Mob", 48);
        setId(Monster.class, "Monster", 49);
        setId(Creeper.class, "Creeper", 50);
        setId(Skeleton.class, "Skeleton", 51);
        setId(Spider.class, "Spider", 52);
        setId(Giant.class, "Giant", 53);
        setId(Zombie.class, "Zombie", 54);
        setId(Slime.class, "Slime", 55);
        setId(Ghast.class, "Ghast", 56);
        setId(PigZombie.class, "PigZombie", 57);
        setId(Pig.class, "Pig", 90);
        setId(Sheep.class, "Sheep", 91);
        setId(Cow.class, "Cow", 92);
        setId(Chicken.class, "Chicken", 93);
        setId(Squid.class, "Squid", 94);
        setId(Wolf.class, "Wolf", 95);
        setId(PrimedTnt.class, "PrimedTnt", 20);
        setId(FallingTile.class, "FallingSand", 21);
        setId(Minecart.class, "Minecart", 40);
        setId(Boat.class, "Boat", 41);
    }
}
