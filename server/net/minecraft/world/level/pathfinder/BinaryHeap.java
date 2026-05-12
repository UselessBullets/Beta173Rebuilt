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
        if (node.heapIdx >= 0) {
            throw new IllegalStateException("OW KNOWS!");
        }
        if (this.size == this.heap.length) {
            final Node[] heap = new Node[this.size << 1];
            System.arraycopy(this.heap, 0, heap, 0, this.size);
            this.heap = heap;
        }
        this.heap[this.size] = node;
        node.heapIdx = this.size;
        this.upHeap(this.size++);
        return node;
    }
    
    public void clear() {
        this.size = 0;
    }
    
    public Node pop() {
        final Node node = this.heap[0];
        final Node[] heap = this.heap;
        final int n = 0;
        final Node[] heap2 = this.heap;
        final int size = this.size - 1;
        this.size = size;
        heap[n] = heap2[size];
        this.heap[this.size] = null;
        if (this.size > 0) {
            this.downHeap(0);
        }
        node.heapIdx = -1;
        return node;
    }
    
    public void changeCost(final Node node, final float newCost) {
        final float f = node.f;
        node.f = newCost;
        if (newCost < f) {
            this.upHeap(node.heapIdx);
        }
        else {
            this.downHeap(node.heapIdx);
        }
    }
    
    private void upHeap(int idx) {
        final Node node = this.heap[idx];
        final float f = node.f;
        while (idx > 0) {
            final int n = idx - 1 >> 1;
            final Node node2 = this.heap[n];
            if (f >= node2.f) {
                break;
            }
            this.heap[idx] = node2;
            node2.heapIdx = idx;
            idx = n;
        }
        this.heap[idx] = node;
        node.heapIdx = idx;
    }
    
    private void downHeap(int idx) {
        final Node node = this.heap[idx];
        final float f = node.f;
        while (true) {
            final int n = 1 + (idx << 1);
            final int n2 = n + 1;
            if (n >= this.size) {
                break;
            }
            final Node node2 = this.heap[n];
            final float f2 = node2.f;
            Node node3;
            float f3;
            if (n2 >= this.size) {
                node3 = null;
                f3 = Float.POSITIVE_INFINITY;
            }
            else {
                node3 = this.heap[n2];
                f3 = node3.f;
            }
            if (f2 < f3) {
                if (f2 >= f) {
                    break;
                }
                this.heap[idx] = node2;
                node2.heapIdx = idx;
                idx = n;
            }
            else {
                if (f3 >= f) {
                    break;
                }
                this.heap[idx] = node3;
                node3.heapIdx = idx;
                idx = n2;
            }
        }
        this.heap[idx] = node;
        node.heapIdx = idx;
    }
    
    public boolean isEmpty() {
        return this.size == 0;
    }
}
