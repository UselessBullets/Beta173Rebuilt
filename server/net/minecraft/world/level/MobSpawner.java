// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

import net.minecraft.world.entity.monster.Zombie;
import java.util.HashSet;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import util.Mth;
import net.minecraft.world.entity.player.Player;
import java.util.Set;

public final class MobSpawner
{
    private static Set b;
    protected static final Class[] a;
    
    protected static TilePos a(final Level dj, final int integer2, final int integer3) {
        return new TilePos(integer2 + dj.random.nextInt(16), dj.random.nextInt(128), integer3 + dj.random.nextInt(16));
    }
    
    public static final int tick(final Level dj, final boolean boolean2, final boolean boolean3) {
        if (!boolean2 && !boolean3) {
            return 0;
        }
        MobSpawner.b.clear();
        for (int i = 0; i < dj.players.size(); ++i) {
            final Player player = dj.players.get(i);
            final int floor = Mth.floor(player.x / 16.0);
            final int floor2 = Mth.floor(player.z / 16.0);
            for (int n = 8, j = -n; j <= n; ++j) {
                for (int k = -n; k <= n; ++k) {
                    MobSpawner.b.add(new ChunkPos(j + floor, k + floor2));
                }
            }
        }
        int n2 = 0;
        final Pos sharedSpawnPos = dj.getSharedSpawnPos();
    Label_0253_Outer:
        for (final MobCategory mobCategory : MobCategory.values()) {
            if (!mobCategory.isFriendly() || boolean3) {
                if (mobCategory.isFriendly() || boolean2) {
                    if (dj.countInstanceOf(mobCategory.getBaseClass()) <= mobCategory.getMaxInstancesPerChunk() * MobSpawner.b.size() / 256) {
                    Label_0253:
                        while (true) {
                            for (final ChunkPos chunkPos : MobSpawner.b) {
                                final List mobs = dj.getBiomeSource().getBiome(chunkPos).getMobs(mobCategory);
                                if (mobs != null) {
                                    if (mobs.isEmpty()) {
                                        continue Label_0253_Outer;
                                    }
                                    int bound = 0;
                                    final Iterator iterator2 = mobs.iterator();
                                    while (iterator2.hasNext()) {
                                        bound += ((MobSpawnerData)iterator2.next()).b;
                                    }
                                    int nextInt = dj.random.nextInt(bound);
                                    MobSpawnerData mobSpawnerData = mobs.get(0);
                                    for (final MobSpawnerData mobSpawnerData2 : mobs) {
                                        nextInt -= mobSpawnerData2.b;
                                        if (nextInt < 0) {
                                            mobSpawnerData = mobSpawnerData2;
                                            break;
                                        }
                                    }
                                    final TilePos a = a(dj, chunkPos.x * 16, chunkPos.z * 16);
                                    final int x = a.x;
                                    final int y = a.y;
                                    final int z = a.z;
                                    if (dj.isSolidBlockingTile(x, y, z)) {
                                        continue Label_0253_Outer;
                                    }
                                    if (dj.getMaterial(x, y, z) != mobCategory.getSpawnPositionMaterial()) {
                                        continue Label_0253_Outer;
                                    }
                                    int n3 = 0;
                                    for (int n4 = 0; n4 < 3; ++n4) {
                                        int integer3 = x;
                                        int integer4 = y;
                                        int integer5 = z;
                                        final int n5 = 6;
                                        for (int n6 = 0; n6 < 4; ++n6) {
                                            integer3 += dj.random.nextInt(n5) - dj.random.nextInt(n5);
                                            integer4 += dj.random.nextInt(1) - dj.random.nextInt(1);
                                            integer5 += dj.random.nextInt(n5) - dj.random.nextInt(n5);
                                            if (a(mobCategory, dj, integer3, integer4, integer5)) {
                                                final float float3 = integer3 + 0.5f;
                                                final float float4 = (float)integer4;
                                                final float float5 = integer5 + 0.5f;
                                                if (dj.getNearestPlayer(float3, float4, float5, 24.0) == null) {
                                                    final float n7 = float3 - sharedSpawnPos.x;
                                                    final float n8 = float4 - sharedSpawnPos.y;
                                                    final float n9 = float5 - sharedSpawnPos.z;
                                                    if (n7 * n7 + n8 * n8 + n9 * n9 >= 576.0f) {
                                                        Mob mob;
                                                        try {
                                                            mob = mobSpawnerData.a.getConstructor(Level.class).newInstance(dj);
                                                        }
                                                        catch (final Exception ex) {
                                                            ex.printStackTrace();
                                                            return n2;
                                                        }
                                                        mob.moveTo(float3, float4, float5, dj.random.nextFloat() * 360.0f, 0.0f);
                                                        if (mob.canSpawn()) {
                                                            ++n3;
                                                            dj.addEntity(mob);
                                                            a(mob, dj, float3, float4, float5);
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
    
    private static boolean a(final MobCategory hh, final Level dj, final int integer3, final int integer4, final int integer5) {
        if (hh.getSpawnPositionMaterial() == Material.water) {
            return dj.getMaterial(integer3, integer4, integer5).isLiquid() && !dj.isSolidBlockingTile(integer3, integer4 + 1, integer5);
        }
        return dj.isSolidBlockingTile(integer3, integer4 - 1, integer5) && !dj.isSolidBlockingTile(integer3, integer4, integer5) && !dj.getMaterial(integer3, integer4, integer5).isLiquid() && !dj.isSolidBlockingTile(integer3, integer4 + 1, integer5);
    }
    
    private static void a(final Mob hl, final Level dj, final float float3, final float float4, final float float5) {
        if (hl instanceof Spider && dj.random.nextInt(100) == 0) {
            final Skeleton e = new Skeleton(dj);
            e.moveTo(float3, float4, float5, hl.yRot, 0.0f);
            dj.addEntity(e);
            e.ride(hl);
        }
        else if (hl instanceof Sheep) {
            ((Sheep)hl).setColor(Sheep.getSheepColor(dj.random));
        }
    }
    
    public static boolean attackSleepingPlayers(final Level dj, final List list) {
        boolean b = false;
        final PathFinder pathFinder = new PathFinder(dj);
        for (final Player to : list) {
            final Class[] a = MobSpawner.a;
            if (a != null) {
                if (a.length == 0) {
                    continue;
                }
                for (int n = 0, n2 = 0; n2 < 20 && n == 0; ++n2) {
                    final int x = Mth.floor(to.x) + dj.random.nextInt(32) - dj.random.nextInt(32);
                    final int n3 = Mth.floor(to.z) + dj.random.nextInt(32) - dj.random.nextInt(32);
                    int n4 = Mth.floor(to.y) + dj.random.nextInt(16) - dj.random.nextInt(16);
                    if (n4 < 1) {
                        n4 = 1;
                    }
                    else if (n4 > 128) {
                        n4 = 128;
                    }
                    final int nextInt = dj.random.nextInt(a.length);
                    int integer4;
                    for (integer4 = n4; integer4 > 2 && !dj.isSolidBlockingTile(x, integer4 - 1, n3); --integer4) {}
                    while (!a(MobCategory.monster, dj, x, integer4, n3) && integer4 < n4 + 16 && integer4 < 128) {
                        ++integer4;
                    }
                    if (integer4 < n4 + 16 && integer4 < 128) {
                        final float n5 = x + 0.5f;
                        final float n6 = (float)integer4;
                        final float n7 = n3 + 0.5f;
                        Mob hl;
                        try {
                            hl = a[nextInt].getConstructor(Level.class).newInstance(dj);
                        }
                        catch (final Exception ex) {
                            ex.printStackTrace();
                            return b;
                        }
                        hl.moveTo(n5, n6, n7, dj.random.nextFloat() * 360.0f, 0.0f);
                        if (hl.canSpawn()) {
                            final Path path = pathFinder.findPath(hl, to, 32.0f);
                            if (path != null && path.length > 1) {
                                final Node last = path.last();
                                if (Math.abs(last.x - to.x) < 1.5 && Math.abs(last.z - to.z) < 1.5 && Math.abs(last.y - to.y) < 1.5) {
                                    Pos standUpPosition = BedTile.findStandUpPosition(dj, Mth.floor(to.x), Mth.floor(to.y), Mth.floor(to.z), 1);
                                    if (standUpPosition == null) {
                                        standUpPosition = new Pos(x, integer4 + 1, n3);
                                    }
                                    hl.moveTo(standUpPosition.x + 0.5f, standUpPosition.y, standUpPosition.z + 0.5f, 0.0f, 0.0f);
                                    dj.addEntity(hl);
                                    a(hl, dj, standUpPosition.x + 0.5f, (float)standUpPosition.y, standUpPosition.z + 0.5f);
                                    to.stopSleepInBed(true, false, false);
                                    hl.playAmbientSound();
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
        MobSpawner.b = new HashSet();
        a = new Class[] { Spider.class, Zombie.class, Skeleton.class };
    }
}
