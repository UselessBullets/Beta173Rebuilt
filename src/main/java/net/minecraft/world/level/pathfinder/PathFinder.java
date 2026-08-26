// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.pathfinder;

import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.DoorTile;
import net.minecraft.world.level.tile.Tile;
import util.Mth;
import net.minecraft.world.entity.Entity;
import util.IntHashMap;
import net.minecraft.world.level.LevelSource;

public class PathFinder
{
    private LevelSource level;
    private BinaryHeap openSet = new BinaryHeap();
    private IntHashMap<Node> nodes = new IntHashMap<>();
    private Node[] neighbors = new Node[32];
    
    public PathFinder(final LevelSource level) {
        this.level = level;
    }
    
    public Path findPath(final Entity from, final Entity to, final float maxDist) {
        return this.findPath(from, to.x, to.bb.y0, to.z, maxDist);
    }
    
    public Path findPath(final Entity from, final int x, final int y, final int z, final float maxDist) {
        return this.findPath(from, x + 0.5f, y + 0.5f, z + 0.5f, maxDist);
    }
    
    private Path findPath(final Entity e, final double xt, final double yt, final double zt, final float maxDist) {
        this.openSet.clear();
        this.nodes.clear();

        Node from = this.getNode(Mth.floor(e.bb.x0), Mth.floor(e.bb.y0), Mth.floor(e.bb.z0));
        Node to = this.getNode(Mth.floor(xt - e.bbWidth / 2.0f), Mth.floor(yt), Mth.floor(zt - e.bbWidth / 2.0f));

        Node size = new Node(Mth.floor(e.bbWidth + 1.0f), Mth.floor(e.bbHeight + 1.0f), Mth.floor(e.bbWidth + 1.0f));
        return this.findPath(e, from, to, size, maxDist);
    }

    // function A*(start,goal)
    private Path findPath(final Entity e, final Node from, final Node to, final Node size, final float maxDist) {
        from.g = 0.0f;
        from.h = from.distanceTo(to);
        from.f = from.h;

        this.openSet.clear();
        this.openSet.insert(from);

        Node closest = from;
        while (!this.openSet.isEmpty()) {
            final Node x = this.openSet.pop();
            if (x.equals(to)) {
                return this.reconstruct_path(from, to);
            }

            if (x.distanceTo(to) < closest.distanceTo(to)) {
                closest = x;
            }
            x.closed = true;

            int neighborCount = this.getNeighbors(e, x, size, to, maxDist);
            for (int i = 0; i < neighborCount; ++i) {
                final Node y = this.neighbors[i];

                final float tentative_g_score = x.g + x.distanceTo(y);
                if (!y.isOpenSet() || tentative_g_score < y.g) {
                    y.cameFrom = x;
                    y.g = tentative_g_score;
                    y.h = y.distanceTo(to);
                    if (y.isOpenSet()) {
                        this.openSet.changeCost(y, y.g + y.h);
                    }
                    else {
                        y.f = y.g + y.h;
                        this.openSet.insert(y);
                    }
                }
            }
        }

        if (closest == from) return null;
        return this.reconstruct_path(from, closest);
    }
    
    private int getNeighbors(final Entity entity, final Node pos, final Node size, final Node target, final float maxDist) {
        int p = 0;

        int jumpSize = 0;
        if (this.isFree(entity, pos.x, pos.y + 1, pos.z, size) == TYPE_OPEN) jumpSize = 1;

        final Node n = this.getNode(entity, pos.x, pos.y, pos.z + 1, size, jumpSize);
        final Node w = this.getNode(entity, pos.x - 1, pos.y, pos.z, size, jumpSize);
        final Node e = this.getNode(entity, pos.x + 1, pos.y, pos.z, size, jumpSize);
        final Node s = this.getNode(entity, pos.x, pos.y, pos.z - 1, size, jumpSize);

        if (n != null && !n.closed && n.distanceTo(target) < maxDist) this.neighbors[p++] = n;
        if (w != null && !w.closed && w.distanceTo(target) < maxDist) this.neighbors[p++] = w;
        if (e != null && !e.closed && e.distanceTo(target) < maxDist) this.neighbors[p++] = e;
        if (s != null && !s.closed && s.distanceTo(target) < maxDist) this.neighbors[p++] = s;

        return p;
    }
    
    private Node getNode(final Entity entity, final int x, int y, final int z, final Node size, final int jumpSize) {
        Node best = null;
        int pathType = this.isFree(entity, x, y, z, size);
        if (pathType == TYPE_OPEN) best = this.getNode(x, y, z);
        if (best == null && jumpSize > 0 && this.isFree(entity, x, y + jumpSize, z, size) == TYPE_OPEN) {
            best = this.getNode(x, y + jumpSize, z);
            y += jumpSize;
        }

        if (best != null) {
            int drop = 0;
            int cost = 0;
            while (y > 0) {
                cost = this.isFree(entity, x, y - 1, z, size);
                if (cost != TYPE_OPEN) break;
                // fell too far?
                if (++drop >= 4) return null;
                --y;

                if (y > 0) best = this.getNode(x, y, z);
            }
            // fell into lava?
            if (cost == TYPE_LAVA) return null;
        }

        return best;
    }
    
    private final Node getNode(final int x, final int y, final int z) {
        final int i = Node.createHash(x, y, z);
        Node node = this.nodes.get(i);
        if (node == null) {
            node = new Node(x, y, z);
            this.nodes.put(i, node);
        }
        return node;
    }

    public static final int TYPE_LAVA = -2;
    public static final int TYPE_WATER = -1;
    public static final int TYPE_BLOCKED = 0;
    public static final int TYPE_OPEN = 1;
    
    private int isFree(final Entity entity, final int x, final int y, final int z, final Node size) {
        for (int xx = x; xx < x + size.x; ++xx) {
            for (int yy = y; yy < y + size.y; ++yy) {
                for (int zz = z; zz < z + size.z; ++zz) {
                    final int tileId = this.level.getTile(xx, yy, zz);
                    if (tileId <= 0) continue;
                    if (tileId == Tile.door_iron.id || tileId == Tile.door_wood.id) {
                        if (!DoorTile.isOpen(this.level.getData(xx, yy, zz))) return TYPE_BLOCKED;
                    }
                    else {
                        final Material m = Tile.tiles[tileId].material;
                        if (m.blocksMotion()) return TYPE_BLOCKED;
                        if (m == Material.water) return TYPE_WATER;
                        if (m == Material.lava) return TYPE_LAVA;
                    }
                }
            }
        }
        return TYPE_OPEN;
    }

    // function reconstruct_path(came_from,current_node)
    private Path reconstruct_path(final Node from, final Node to) {
        int count = 1;
        Node n = to;
        while (n.cameFrom != null) {
            count++;
            n = n.cameFrom;
        }

        final Node[] nodes = new Node[count];
        n = to;
        nodes[--count] = n;
        while (n.cameFrom != null) {
            n = n.cameFrom;
            nodes[--count] = n;
        }
        return new Path(nodes);
    }
}
