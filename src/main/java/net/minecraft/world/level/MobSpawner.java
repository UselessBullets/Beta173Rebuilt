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

public final class MobSpawner {
    private static final int MIN_SPAWN_DISTANCE = 24;
    private static Set<ChunkPos> chunksToPoll = new HashSet<>();
    protected static final Class<? extends Mob>[] bedEnemies = new Class[]{Spider.class, Zombie.class, Skeleton.class};

    protected static TilePos getRandomPosWithin(final Level level, final int cx, final int cz) {
        int x = cx + level.random.nextInt(16);
        int y = level.random.nextInt(Level.MAX_HEIGHT);
        int z = cz + level.random.nextInt(16);

        return new TilePos(x, y, z);
    }

    public static final int tick(final Level level, final boolean spawnEnemies, final boolean spawnFriendlies) {
        if (!spawnEnemies && !spawnFriendlies) {
            return 0;
        }
        MobSpawner.chunksToPoll.clear();
        for (int i = 0; i < level.players.size(); ++i) {
            final Player player = level.players.get(i);
            final int xx = Mth.floor(player.x / 16.0);
            final int zz = Mth.floor(player.z / 16.0);

            int r = 128 / 16;
            for (int x = -r; x <= r; ++x) {
                for (int z = -r; z <= r; ++z) {
                    MobSpawner.chunksToPoll.add(new ChunkPos(x + xx, z + zz));
                }
            }
        }
        int count = 0;
        final Pos spawnPos = level.getSharedSpawnPos();
        categoryLoop:
        for (final MobCategory mobCategory : MobCategory.values()) {
            if (mobCategory.isFriendly() && !spawnFriendlies || !mobCategory.isFriendly() && !spawnEnemies) continue;

            int categoryCount = level.countInstanceOf(mobCategory.getBaseClass());
            if (categoryCount > mobCategory.getMaxInstancesPerChunk() * MobSpawner.chunksToPoll.size() / 256) continue;

            chunkLoop:
            for (final ChunkPos cp : MobSpawner.chunksToPoll) {
                final List<Biome.MobSpawnerData> mobs = level.getBiomeSource().getBiome(cp).getMobs(mobCategory);
                if (mobs == null) continue;
                if (mobs.isEmpty()) continue categoryLoop;

                int bound = 0;
                for (Biome.MobSpawnerData spawnerData : mobs) {
                    bound += spawnerData.probabilityWeight;
                }

                int nextInt = level.random.nextInt(bound);
                Biome.MobSpawnerData mobSpawnerData = mobs.get(0);
                for (final Biome.MobSpawnerData spawnerData : mobs) {
                    nextInt -= spawnerData.probabilityWeight;
                    if (nextInt < 0) {
                        mobSpawnerData = spawnerData;
                        break;
                    }
                }

                final TilePos start = getRandomPosWithin(level, cp.x * 16, cp.z * 16);
                final int xStart = start.x;
                final int yStart = start.y;
                final int zStart = start.z;

                if (level.isSolidBlockingTile(xStart, yStart, zStart)) continue categoryLoop;
                if (level.getMaterial(xStart, yStart, zStart) != mobCategory.getSpawnPositionMaterial())
                    continue categoryLoop;
                int clusterSize = 0;

                for (int dd = 0; dd < 3; ++dd) {
                    int x = xStart;
                    int y = yStart;
                    int z = zStart;
                    final int ss = 6;

                    for (int ll = 0; ll < 4; ++ll) {
                        x += level.random.nextInt(ss) - level.random.nextInt(ss);
                        y += level.random.nextInt(1) - level.random.nextInt(1);
                        z += level.random.nextInt(ss) - level.random.nextInt(ss);
                        if (isSpawnPositionOk(mobCategory, level, x, y, z)) {
                            final float xx = x + 0.5f;
                            final float yy = (float) y;
                            final float zz = z + 0.5f;
                            if (level.getNearestPlayer(xx, yy, zz, MIN_SPAWN_DISTANCE) != null) continue;

                            final float xd = xx - spawnPos.x;
                            final float yd = yy - spawnPos.y;
                            final float zd = zz - spawnPos.z;
                            if (xd * xd + yd * yd + zd * zd < MIN_SPAWN_DISTANCE * MIN_SPAWN_DISTANCE) continue;

                            Mob mob;
                            try {
                                mob = mobSpawnerData.mobClass.getConstructor(Level.class).newInstance(level);
                            } catch (final Exception e) {
                                e.printStackTrace();
                                return count;
                            }

                            mob.moveTo(xx, yy, zz, level.random.nextFloat() * 360.0f, 0.0f);

                            if (mob.canSpawn()) {
                                clusterSize++;
                                level.addEntity(mob);
                                finalizeMobSettings(mob, level, xx, yy, zz);
                                if (clusterSize >= mob.getMaxSpawnClusterSize()) continue chunkLoop;
                            }
                            count += clusterSize;
                        }
                    }
                }
            }
        }
        return count;
    }

    private static boolean isSpawnPositionOk(final MobCategory category, final Level level, final int x, final int y, final int z) {
        if (category.getSpawnPositionMaterial() == Material.water) {
            return level.getMaterial(x, y, z).isLiquid() && !level.isSolidBlockingTile(x, y + 1, z);
        }
        return level.isSolidBlockingTile(x, y - 1, z) && !level.isSolidBlockingTile(x, y, z) && !level.getMaterial(x, y, z).isLiquid() && !level.isSolidBlockingTile(x, y + 1, z);
    }

    private static void finalizeMobSettings(final Mob mob, final Level level, final float xx, final float yy, final float zz) {
        if (mob instanceof Spider && level.random.nextInt(100) == 0) {
            final Skeleton skeleton = new Skeleton(level);
            skeleton.moveTo(xx, yy, zz, mob.yRot, 0.0f);
            level.addEntity(skeleton);
            skeleton.ride(mob);
        } else if (mob instanceof Sheep) {
            ((Sheep) mob).setColor(Sheep.getSheepColor(level.random));
        }
    }

    public static boolean attackSleepingPlayers(final Level level, final List<Player> players) {
        boolean somebodyWokeUp = false;

        final PathFinder finder = new PathFinder(level);

        for (final Player player : players) {
            final Class<? extends Mob>[] bedEnemies = MobSpawner.bedEnemies;
            if (bedEnemies == null || bedEnemies.length == 0) continue;

            boolean nextPlayer = false;
            for (int attemptCount = 0; attemptCount < 20 && !nextPlayer; ++attemptCount) {
                // limit position within the range of the player
                final int x = Mth.floor(player.x) + level.random.nextInt(32) - level.random.nextInt(32);
                final int z = Mth.floor(player.z) + level.random.nextInt(32) - level.random.nextInt(32);
                int yStart = Mth.floor(player.y) + level.random.nextInt(16) - level.random.nextInt(16);
                if (yStart < 1) {
                    yStart = 1;
                } else if (yStart > Level.MAX_HEIGHT) {
                    yStart = Level.MAX_HEIGHT;
                }

                final int type = level.random.nextInt(bedEnemies.length);
                int y = yStart;
                while (y > 2 && !level.isSolidBlockingTile(x, y - 1, z)) {
                    y--;
                }

                while (!isSpawnPositionOk(MobCategory.monster, level, x, y, z) && y < yStart + 16 && y < Level.MAX_HEIGHT) {
                    y++;
                }

                if (y >= yStart + 16 || y >= Level.MAX_HEIGHT) {
                    y = yStart;
                    continue;
                }

                final float xx = x + 0.5f;
                final float yy = (float) y;
                final float zz = z + 0.5f;
                Mob mob;
                try {
                    mob = bedEnemies[type].getConstructor(Level.class).newInstance(level);
                } catch (final Exception e) {
                    e.printStackTrace();
                    return somebodyWokeUp;
                }

                // System.out.println("Placing night mob");
                mob.moveTo(xx, yy, zz, level.random.nextFloat() * 360.0f, 0.0f);
                // check if the mob can spawn at this location
                if (!mob.canSpawn()) {
                    continue;
                }

                final Path path = finder.findPath(mob, player, 32.0f);
                if (path != null && path.length > 1) {
                    final Node last = path.last();
                    if (Math.abs(last.x - player.x) < 1.5 && Math.abs(last.z - player.z) < 1.5 && Math.abs(last.y - player.y) < 1.5) {
                        // System.out.println("Found path!");

                        Pos bedPos = BedTile.findStandUpPosition(level, Mth.floor(player.x), Mth.floor(player.y), Mth.floor(player.z), 1);
                        if (bedPos == null) {
                            // an unlikely case where the bed is
                            // completely blocked
                            bedPos = new Pos(x, y + 1, z);
                        }

                        mob.moveTo(bedPos.x + 0.5f, bedPos.y, bedPos.z + 0.5f, 0.0f, 0.0f);
                        // the mob would maybe not be able to
                        // spawn here, but we ignore that now (we assume
                        // it walked here)
                        {
                            level.addEntity(mob);
                            finalizeMobSettings(mob, level, bedPos.x + 0.5f, (float) bedPos.y, bedPos.z + 0.5f);
                            player.stopSleepInBed(true, false, false);
                            // play a sound effect to scare the player
                            mob.playAmbientSound();
                            somebodyWokeUp = true;
                            nextPlayer = true;
                        }
                    }
                }
            }
        }
        
        return somebodyWokeUp;
    }

}
