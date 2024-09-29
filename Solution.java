
import java.util.Arrays;

public class Solution {

    private static final int NO_PATH_FOUND = -1;

    public int[] minimumCost(int numberOfNodes, int[][] edges, int[][] queries) {
        UnionFind unionFind = new UnionFind(numberOfNodes);
        int[] minCostToReachParent = createMinCostToReachParent(unionFind, numberOfNodes, edges);
        return createMinCostPerQuery(unionFind, minCostToReachParent, queries);
    }

    private int[] createMinCostToReachParent(UnionFind unionFind, int numberOfNodes, int[][] edges) {
        int[] minCostToReachParent = new int[numberOfNodes];
        Arrays.fill(minCostToReachParent, Integer.MAX_VALUE);

        for (int[] edge : edges) {
            int parentFirst = unionFind.findParent(edge[0]);
            int parentSecond = unionFind.findParent(edge[1]);
            int cost = edge[2];

            minCostToReachParent[parentFirst] &= cost & minCostToReachParent[parentSecond];
            minCostToReachParent[parentSecond] &= cost & minCostToReachParent[parentFirst];

            if (parentFirst != parentSecond) {
                unionFind.joinByRank(parentFirst, parentSecond);
            }
        }
        return minCostToReachParent;
    }

    private int[] createMinCostPerQuery(UnionFind unionFind, int[] minCostToReachParent, int[][] queries) {
        int[] minCostPerQuery = new int[queries.length];

        for (int i = 0; i < queries.length; ++i) {
            int parentFirst = unionFind.findParent(queries[i][0]);
            int parentSecond = unionFind.findParent(queries[i][1]);

            if (parentFirst != parentSecond) {
                minCostPerQuery[i] = NO_PATH_FOUND;
            } else {
                minCostPerQuery[i] = minCostToReachParent[parentFirst];
            }
        }

        return minCostPerQuery;
    }
}

class UnionFind {

    private final int[] rank;
    private final int[] parent;

    UnionFind(int numberOfNodes) {
        rank = new int[numberOfNodes];
        parent = new int[numberOfNodes];

        // Alternatively: parent = IntStream.range(0, numberOfNodes).toArray();
        for (int node = 0; node < numberOfNodes; ++node) {
            parent[node] = node;
        }
    }

    int findParent(int node) {
        while (parent[node] != node) {
            node = parent[node];
        }
        return parent[node];
    }

    void joinByRank(int firstNode, int secondNode) {
        if (rank[firstNode] >= rank[secondNode]) {
            parent[secondNode] = firstNode;
            rank[firstNode] += rank[secondNode] + 1;
        } else {
            parent[firstNode] = secondNode;
            rank[secondNode] += rank[firstNode] + 1;
        }
    }
}
