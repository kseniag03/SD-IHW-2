import java.util.*;

public class Graph {
    private final int vertices;

    private final ArrayList<ArrayList<Integer>> adj;

    Graph(int vertices) {
        this.vertices = vertices;
        adj = new ArrayList<>(vertices);
        for (int v = 0; v < vertices; v++) {
            adj.add(new ArrayList<>());
        }
    }

    void addEdge(int start, int end) {
        if (start >= vertices || end >= vertices) {
            System.out.println("Vertex number is out of bounds");
        } else {
            adj.get(start).add(end);
        }
    }

    void printGraph() {
        for (int v = 0; v < vertices; v++) {
            System.out.println("Adj-list of vertex " + v);
            for (var u: adj.get(v)) {
                System.out.println(v + " -> " + u);
            }
            System.out.println();
        }
    }

    private boolean hasCycleDfs(int v, boolean[] used, boolean[] cycleDetect)
    {
        if (cycleDetect[v]) {
            System.out.println("Vertex " + v);
            return true;
        }
        if (used[v]) {
            return false;
        }
        used[v] = true;
        cycleDetect[v] = true;
        for (var u: adj.get(v)) {
            if (hasCycleDfs(u, used, cycleDetect)) {
                return true;
            }
        }
        cycleDetect[v] = false;
        return false;
    }

    public boolean hasCycle()
    {
        boolean[] used = new boolean[vertices];
        boolean[] cycleDetect = new boolean[vertices];
        for (int v = 0; v < vertices; v++) {
            if (hasCycleDfs(v, used, cycleDetect)) {
                return true;
            }
        }
        return false;
    }

    void topologicalSortDfs(int v, boolean[] used, Stack<Integer> order) {
        used[v] = true;
        for (var u : adj.get(v)) {
            if (!used[u]) {
                topologicalSortDfs(u, used, order);
            }
        }
        order.push(v);
    }

    Stack<Integer> topologicalSort() {
        boolean[] used = new boolean[vertices];
        Stack<Integer> order = new Stack<>();
        for (int v = 0; v < vertices; v++) {
            if (!used[v]) {
                topologicalSortDfs(v, used, order);
            }
        }
        return order;
    }
}
