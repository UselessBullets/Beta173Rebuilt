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
    private BinaryHeap openSet;
    private IntHashMap nodes;
    private Node[] neighbors;
    
    public PathFinder(final LevelSource level) {
        this.openSet = new BinaryHeap();
        this.nodes = new IntHashMap();
        this.neighbors = new Node[32];
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
        return this.findPath(e, this.getNode(Mth.floor(e.bb.x0), Mth.floor(e.bb.y0), Mth.floor(e.bb.z0)), this.getNode(Mth.floor(xt - e.bbWidth / 2.0f), Mth.floor(yt), Mth.floor(zt - e.bbWidth / 2.0f)), new Node(Mth.floor(e.bbWidth + 1.0f), Mth.floor(e.bbHeight + 1.0f), Mth.floor(e.bbWidth + 1.0f)), maxDist);
    }
    
    private Path findPath(final Entity e, final Node from, final Node to, final Node size, final float maxDist) {
        from.g = 0.0f;
        from.h = from.distanceTo(to);
        from.f = from.h;
        this.openSet.clear();
        this.openSet.insert(from);
        Node to2 = from;
        while (!this.openSet.isEmpty()) {
            final Node pop = this.openSet.pop();
            if (pop.equals(to)) {
                return this.reconstruct_path(from, to);
            }
            if (pop.distanceTo(to) < to2.distanceTo(to)) {
                to2 = pop;
            }
            pop.closed = true;
            for (int neighbors = this.getNeighbors(e, pop, size, to, maxDist), i = 0; i < neighbors; ++i) {
                final Node node = this.neighbors[i];
                final float g = pop.g + pop.distanceTo(node);
                if (!node.isOpenSet() || g < node.g) {
                    node.cameFrom = pop;
                    node.g = g;
                    node.h = node.distanceTo(to);
                    if (node.isOpenSet()) {
                        this.openSet.changeCost(node, node.g + node.h);
                    }
                    else {
                        node.f = node.g + node.h;
                        this.openSet.insert(node);
                    }
                }
            }
        }
        if (to2 == from) {
            return null;
        }
        return this.reconstruct_path(from, to2);
    }
    
    private int getNeighbors(final Entity entity, final Node pos, final Node size, final Node target, final float maxDist) {
        int n = 0;
        int n2 = 0;
        if (this.isFree(entity, pos.x, pos.y + 1, pos.z, size) == 1) {
            n2 = 1;
        }
        final Node node = this.getNode(entity, pos.x, pos.y, pos.z + 1, size, n2);
        final Node node2 = this.getNode(entity, pos.x - 1, pos.y, pos.z, size, n2);
        final Node node3 = this.getNode(entity, pos.x + 1, pos.y, pos.z, size, n2);
        final Node node4 = this.getNode(entity, pos.x, pos.y, pos.z - 1, size, n2);
        if (node != null && !node.closed && node.distanceTo(target) < maxDist) {
            this.neighbors[n++] = node;
        }
        if (node2 != null && !node2.closed && node2.distanceTo(target) < maxDist) {
            this.neighbors[n++] = node2;
        }
        if (node3 != null && !node3.closed && node3.distanceTo(target) < maxDist) {
            this.neighbors[n++] = node3;
        }
        if (node4 != null && !node4.closed && node4.distanceTo(target) < maxDist) {
            this.neighbors[n++] = node4;
        }
        return n;
    }
    
    private Node getNode(final Entity entity, final int x, int y, final int z, final Node size, final int jumpSize) {
        Node node = null;
        if (this.isFree(entity, x, y, z, size) == 1) {
            node = this.getNode(x, y, z);
        }
        if (node == null && jumpSize > 0 && this.isFree(entity, x, y + jumpSize, z, size) == 1) {
            node = this.getNode(x, y + jumpSize, z);
            y += jumpSize;
        }
        if (node != null) {
            int n = 0;
            int free = 0;
            while (y > 0 && (free = this.isFree(entity, x, y - 1, z, size)) == 1) {
                if (++n >= 4) {
                    return null;
                }
                if (--y <= 0) {
                    continue;
                }
                node = this.getNode(x, y, z);
            }
            if (free == -2) {
                return null;
            }
        }
        return node;
    }
    
    private final Node getNode(final int x, final int y, final int z) {
        final int hash = Node.createHash(x, y, z);
        Node value = (Node)this.nodes.get(hash);
        if (value == null) {
            value = new Node(x, y, z);
            this.nodes.put(hash, value);
        }
        return value;
    }
    
    private int isFree(final Entity entity, final int x, final int y, final int z, final Node size) {
        for (int i = x; i < x + size.x; ++i) {
            for (int j = y; j < y + size.y; ++j) {
                for (int k = z; k < z + size.z; ++k) {
                    final int tile = this.level.getTile(i, j, k);
                    if (tile > 0) {
                        if (tile == Tile.door_iron.id || tile == Tile.door_wood.id) {
                            if (!DoorTile.isOpen(this.level.getData(i, j, k))) {
                                return 0;
                            }
                        }
                        else {
                            final Material material = Tile.tiles[tile].material;
                            if (material.blocksMotion()) {
                                return 0;
                            }
                            if (material == Material.water) {
                                return -1;
                            }
                            if (material == Material.lava) {
                                return -2;
                            }
                        }
                    }
                }
            }
        }
        return 1;
    }
    
    private Path reconstruct_path(final Node from, final Node to) {
        int n = 1;
        for (Node cameFrom = to; cameFrom.cameFrom != null; cameFrom = cameFrom.cameFrom) {
            ++n;
        }
        final Node[] nodes = new Node[n];
        Node cameFrom2 = to;
        nodes[--n] = cameFrom2;
        while (cameFrom2.cameFrom != null) {
            cameFrom2 = cameFrom2.cameFrom;
            nodes[--n] = cameFrom2;
        }
        return new Path(nodes);
    }
}
