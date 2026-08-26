// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.pathfinder;

public class BinaryHeap
{
    private Node[] heap;
    private int size;
    
    public BinaryHeap() {
        this.heap = new Node[1024];
        this.size = 0;
    }
    
    public Node insert(final Node node) {
        if (node.heapIdx >= 0) throw new IllegalStateException("OW KNOWS!");

        // Expand if necessary.
        if (this.size == this.heap.length) {
            final Node[] newHeap = new Node[this.size << 1];

            System.arraycopy(this.heap, 0, newHeap, 0, this.size);

            this.heap = newHeap;
        }

        // Insert at end and bubble up.
        this.heap[this.size] = node;
        node.heapIdx = this.size;
        this.upHeap(this.size++);

        return node;
    }
    
    public void clear() {
        this.size = 0;
    }

    // Useless - In b1.2 and LCE leaks
    public Node peek() {
        return this.heap[0];
    }
    public Node pop() {
        final Node popped = this.heap[0];
        this.heap[0] = this.heap[--this.size];
        this.heap[this.size] = null;
        if (this.size > 0) this.downHeap(0);
        popped.heapIdx = -1;
        return popped;
    }

    // Useless - In b1.2 and LCE leaks
    public void remove(Node var1) {
        // This is what node.heapIdx is for.
        this.heap[var1.heapIdx] = this.heap[--this.size];
        this.heap[this.size] = null;
        if (this.size > var1.heapIdx) {
            if (this.heap[var1.heapIdx].f < var1.f) {
                this.upHeap(var1.heapIdx);
            } else {
                this.downHeap(var1.heapIdx);
            }
        }
        // Just as a precaution: should make stuff blow up if the node is abused.
        var1.heapIdx = -1;
    }
    public void changeCost(final Node node, final float newCost) {
        final float oldCost = node.f;
        node.f = newCost;
        if (newCost < oldCost) {
            this.upHeap(node.heapIdx);
        }
        else {
            this.downHeap(node.heapIdx);
        }
    }

    // Useless - In b1.2 and LCE leaks
    public int size() {
        return this.size;
    }
    
    private void upHeap(int idx) {
        final Node node = this.heap[idx];
        final float cost = node.f;
        while (idx > 0) {
            final int parentIdx = idx - 1 >> 1;
            final Node parent = this.heap[parentIdx];
            if (cost < parent.f) {
                this.heap[idx] = parent;
                parent.heapIdx = idx;
                idx = parentIdx;
            } else break;
        }
        this.heap[idx] = node;
        node.heapIdx = idx;
    }
    
    private void downHeap(int idx) {
        final Node node = this.heap[idx];
        final float cost = node.f;

        while (true) {
            final int leftIdx = 1 + (idx << 1);
            final int rightIdx = leftIdx + 1;

            if (leftIdx >= this.size) break;

            // We definitely have a left child.
            final Node leftNode = this.heap[leftIdx];
            final float leftCost = leftNode.f;

            // We may have a right child.
            Node rightNode;
            float rightCost;
            if (rightIdx >= this.size) {
                // Only need to compare with left.
                rightNode = null;
                rightCost = Float.POSITIVE_INFINITY;
            }
            else {
                rightNode = this.heap[rightIdx];
                rightCost = rightNode.f;
            }

            // Find the smallest of the three costs: the corresponding node
            // should be the parent.
            if (leftCost < rightCost) {
                if (leftCost < cost) {
                    this.heap[idx] = leftNode;
                    leftNode.heapIdx = idx;
                    idx = leftIdx;
                } else break;
            }
            else {
                if (rightCost < cost) {
                    this.heap[idx] = rightNode;
                    rightNode.heapIdx = idx;
                    idx = rightIdx;
                } else break;
            }
        }

        this.heap[idx] = node;
        node.heapIdx = idx;
    }
    
    public boolean isEmpty() {
        return this.size == 0;
    }
}
