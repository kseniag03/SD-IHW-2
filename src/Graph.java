import java.util.*;

/**
 * Class graph for cycles search and topological sort realization.
 */
public class Graph {
    /**
     * Number of vertices in the graph.
     */
    private final int vertices;

    /**
     * Adjacency list for the graph storage.
     */
    private final ArrayList<ArrayList<Integer>> adj;

    /**
     * Constructor for Graph.
     * @param vertices number of vertices in the graph
     */
    Graph(int vertices) {
        this.vertices = vertices;
        adj = new ArrayList<>(vertices);
        for (int v = 0; v < vertices; v++) {
            adj.add(new ArrayList<>());
        }
    }

    /**
     * Adds edge to the graph.
     * @param start the initial vertex of the arc
     * @param end the terminal vertex of the arc
     */
    void addEdge(int start, int end) {
        if (start >= vertices || end >= vertices) {
            System.out.println("Vertex number is out of bounds");
        } else {
            adj.get(start).add(end);
        }
    }

    /**
     * Prints the adjacency list of the graph.
     */
    void printGraph() {
        for (int v = 0; v < vertices; v++) {
            System.out.println("Adj-list of vertex " + v);
            for (var u: adj.get(v)) {
                System.out.println(v + " -> " + u);
            }
            System.out.println();
        }
    }

    /**
     * Searches for cycles using DFS.
     * @param v current vertex
     * @param used array of used vertices
     * @param cycleDetect array of "grey" vertices (for cycle detection)
     * @return true if the graph has cycles, false otherwise
     */
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

    /**
     * Checks if the graph has cycles.
     * @return true if the graph has cycles, false otherwise
     */
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

    /**
     * Sorts graph vertices using DFS.
     * @param v current vertex
     * @param used array of used vertices
     * @param order stack of current vertices order
     */
    void topologicalSortDfs(int v, boolean[] used, Stack<Integer> order) {
        used[v] = true;
        for (var u : adj.get(v)) {
            if (!used[u]) {
                topologicalSortDfs(u, used, order);
            }
        }
        order.push(v);
    }

    /**
     * Topological sort of the graph.
     * @return stack of sorted vertices order
     */
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
