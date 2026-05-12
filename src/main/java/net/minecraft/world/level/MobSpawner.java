// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

import net.minecraft.world.entity.monster.Zombie;
import java.util.HashSet;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.tile.BedTile;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.material.Material;
import java.util.List;
import java.util.Iterator;
import net.minecraft.Pos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import util.Mth;
import net.minecraft.world.entity.player.Player;
import java.util.Set;

public final class MobSpawner
{
    private static Set<ChunkPos> chunksToPoll;
    protected static final Class<? extends Mob>[] bedEnemies;
    
    protected static TilePos getRandomPosWithin(final Level level, final int cx, final int cz) {
        return new TilePos(cx + level.random.nextInt(16), level.random.nextInt(128), cz + level.random.nextInt(16));
    }
    
    public static final int tick(final Level level, final boolean spawnEnemies, final boolean spawnFriendlies) {
        if (!spawnEnemies && !spawnFriendlies) {
            return 0;
        }
        MobSpawner.chunksToPoll.clear();
        for (int i = 0; i < level.players.size(); ++i) {
            final Player player = level.players.get(i);
            final int floor = Mth.floor(player.x / 16.0);
            final int floor2 = Mth.floor(player.z / 16.0);
            for (int n = 8, j = -n; j <= n; ++j) {
                for (int k = -n; k <= n; ++k) {
                    MobSpawner.chunksToPoll.add(new ChunkPos(j + floor, k + floor2));
                }
            }
        }
        int n2 = 0;
        final Pos sharedSpawnPos = level.getSharedSpawnPos();
    Label_0253_Outer:
        for (final MobCategory mobCategory : MobCategory.values()) {
            if (!mobCategory.isFriendly() || spawnFriendlies) {
                if (mobCategory.isFriendly() || spawnEnemies) {
                    if (level.countInstanceOf(mobCategory.getBaseClass()) <= mobCategory.getMaxInstancesPerChunk() * MobSpawner.chunksToPoll.size() / 256) {
                    Label_0253:
                        while (true) {
                            for (final ChunkPos chunkPos : MobSpawner.chunksToPoll) {
                                final List<Biome.MobSpawnerData> mobs = level.getBiomeSource().getBiome(chunkPos).getMobs(mobCategory);
                                if (mobs != null) {
                                    if (mobs.isEmpty()) {
                                        continue Label_0253_Outer;
                                    }
                                    int bound = 0;
                                    final Iterator iterator2 = mobs.iterator();
                                    while (iterator2.hasNext()) {
                                        bound += ((Biome.MobSpawnerData)iterator2.next()).probabilityWeight;
                                    }
                                    int nextInt = level.random.nextInt(bound);
                                    Biome.MobSpawnerData mobSpawnerData = mobs.get(0);
                                    for (final Biome.MobSpawnerData mobSpawnerData2 : mobs) {
                                        nextInt -= mobSpawnerData2.probabilityWeight;
                                        if (nextInt < 0) {
                                            mobSpawnerData = mobSpawnerData2;
                                            break;
                                        }
                                    }
                                    final TilePos randomPosWithin = getRandomPosWithin(level, chunkPos.x * 16, chunkPos.z * 16);
                                    final int x = randomPosWithin.x;
                                    final int y = randomPosWithin.y;
                                    final int z = randomPosWithin.z;
                                    if (level.isSolidBlockingTile(x, y, z)) {
                                        continue Label_0253_Outer;
                                    }
                                    if (level.getMaterial(x, y, z) != mobCategory.getSpawnPositionMaterial()) {
                                        continue Label_0253_Outer;
                                    }
                                    int n3 = 0;
                                    for (int n4 = 0; n4 < 3; ++n4) {
                                        int x2 = x;
                                        int y2 = y;
                                        int z2 = z;
                                        final int n5 = 6;
                                        for (int n6 = 0; n6 < 4; ++n6) {
                                            x2 += level.random.nextInt(n5) - level.random.nextInt(n5);
                                            y2 += level.random.nextInt(1) - level.random.nextInt(1);
                                            z2 += level.random.nextInt(n5) - level.random.nextInt(n5);
                                            if (isSpawnPositionOk(mobCategory, level, x2, y2, z2)) {
                                                final float xx = x2 + 0.5f;
                                                final float yy = (float)y2;
                                                final float zz = z2 + 0.5f;
                                                if (level.getNearestPlayer(xx, yy, zz, 24.0) == null) {
                                                    final float n7 = xx - sharedSpawnPos.x;
                                                    final float n8 = yy - sharedSpawnPos.y;
                                                    final float n9 = zz - sharedSpawnPos.z;
                                                    if (n7 * n7 + n8 * n8 + n9 * n9 >= 576.0f) {
                                                        Mob mob;
                                                        try {
                                                            mob = mobSpawnerData.mobClass.getConstructor(Level.class).newInstance(level);
                                                        }
                                                        catch (final Exception ex) {
                                                            ex.printStackTrace();
                                                            return n2;
                                                        }
                                                        mob.moveTo(xx, yy, zz, level.random.nextFloat() * 360.0f, 0.0f);
                                                        if (mob.canSpawn()) {
                                                            ++n3;
                                                            level.addEntity(mob);
                                                            finalizeMobSettings(mob, level, xx, yy, zz);
                                                            if (n3 >= mob.getMaxSpawnClusterSize()) {
                                                                continue Label_0253;
                                                            }
                                                        }
                                                        n2 += n3;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        return n2;
    }
    
    private static boolean isSpawnPositionOk(final MobCategory category, final Level level, final int x, final int y, final int z) {
        if (category.getSpawnPositionMaterial() == Material.water) {
            return level.getMaterial(x, y, z).isLiquid() && !level.isSolidBlockingTile(x, y + 1, z);
        }
        return level.isSolidBlockingTile(x, y - 1, z) && !level.isSolidBlockingTile(x, y, z) && !level.getMaterial(x, y, z).isLiquid() && !level.isSolidBlockingTile(x, y + 1, z);
    }
    
    private static void finalizeMobSettings(final Mob mob, final Level level, final float xx, final float yy, final float zz) {
        if (mob instanceof Spider && level.random.nextInt(100) == 0) {
            final Skeleton e = new Skeleton(level);
            e.moveTo(xx, yy, zz, mob.yRot, 0.0f);
            level.addEntity(e);
            e.ride(mob);
        }
        else if (mob instanceof Sheep) {
            ((Sheep)mob).setColor(Sheep.getSheepColor(level.random));
        }
    }
    
    public static boolean attackSleepingPlayers(final Level level, final List<Player> players) {
        boolean b = false;
        final PathFinder pathFinder = new PathFinder(level);
        for (final Player to : players) {
            final Class<? extends Mob>[] bedEnemies = MobSpawner.bedEnemies;
            if (bedEnemies != null) {
                if (bedEnemies.length == 0) {
                    continue;
                }
                for (int n = 0, n2 = 0; n2 < 20 && n == 0; ++n2) {
                    final int x = Mth.floor(to.x) + level.random.nextInt(32) - level.random.nextInt(32);
                    final int z = Mth.floor(to.z) + level.random.nextInt(32) - level.random.nextInt(32);
                    int n3 = Mth.floor(to.y) + level.random.nextInt(16) - level.random.nextInt(16);
                    if (n3 < 1) {
                        n3 = 1;
                    }
                    else if (n3 > 128) {
                        n3 = 128;
                    }
                    final int nextInt = level.random.nextInt(bedEnemies.length);
                    int y;
                    for (y = n3; y > 2 && !level.isSolidBlockingTile(x, y - 1, z); --y) {}
                    while (!isSpawnPositionOk(MobCategory.monster, level, x, y, z) && y < n3 + 16 && y < 128) {
                        ++y;
                    }
                    if (y < n3 + 16 && y < 128) {
                        final float n4 = x + 0.5f;
                        final float n5 = (float)y;
                        final float n6 = z + 0.5f;
                        Mob mob;
                        try {
                            mob = bedEnemies[nextInt].getConstructor(Level.class).newInstance(level);
                        }
                        catch (final Exception ex) {
                            ex.printStackTrace();
                            return b;
                        }
                        mob.moveTo(n4, n5, n6, level.random.nextFloat() * 360.0f, 0.0f);
                        if (mob.canSpawn()) {
                            final Path path = pathFinder.findPath(mob, to, 32.0f);
                            if (path != null && path.length > 1) {
                                final Node last = path.last();
                                if (Math.abs(last.x - to.x) < 1.5 && Math.abs(last.z - to.z) < 1.5 && Math.abs(last.y - to.y) < 1.5) {
                                    Pos standUpPosition = BedTile.findStandUpPosition(level, Mth.floor(to.x), Mth.floor(to.y), Mth.floor(to.z), 1);
                                    if (standUpPosition == null) {
                                        standUpPosition = new Pos(x, y + 1, z);
                                    }
                                    mob.moveTo(standUpPosition.x + 0.5f, standUpPosition.y, standUpPosition.z + 0.5f, 0.0f, 0.0f);
                                    level.addEntity(mob);
                                    finalizeMobSettings(mob, level, standUpPosition.x + 0.5f, (float)standUpPosition.y, standUpPosition.z + 0.5f);
                                    to.stopSleepInBed(true, false, false);
                                    mob.playAmbientSound();
                                    b = true;
                                    n = 1;
                                }
                            }
                        }
                    }
                }
            }
        }
        return b;
    }
    
    static {
        MobSpawner.chunksToPoll = new HashSet<>();
        bedEnemies = new Class[] { Spider.class, Zombie.class, Skeleton.class };
    }
}
