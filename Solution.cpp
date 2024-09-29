
#include <span>
#include <limits>
#include <ranges>
#include <vector>
using namespace std;

/*
 The code will run faster with ios::sync_with_stdio(0).
 However, this should not be used in production code and interactive problems.
 In this particular problem, it is ok to apply ios::sync_with_stdio(0).

 Many of the top-ranked C++ solutions for time on leetcode apply this trick,
 so, for a fairer assessment of the time percentile of my code
 you could outcomment the lambda expression below for a faster run.
*/

/*
const static auto speedup = [] {
    ios::sync_with_stdio(0);
    return nullptr;
}();
*/

class UnionFind {

    vector<int> rank;
    vector<int> parent;

public:
    UnionFind(int numberOfNodes) {
        rank.resize(numberOfNodes);
        parent.resize(numberOfNodes);

        // Alternatively: ranges::iota(parent,0);
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
        }
        else {
            parent[firstNode] = secondNode;
            rank[secondNode] += rank[firstNode] + 1;
        }
    }
};

class Solution {

    static const int NO_PATH_FOUND = -1;

public:
    vector<int> minimumCost(int numberOfNodes, const vector<vector<int>>& edges, const vector<vector<int>>& queries) const {
        UnionFind unionFind(numberOfNodes);
        vector<int> minCostToReachParent = createMinCostToReachParent(unionFind, numberOfNodes, edges);
        return createMinCostPerQuery(unionFind, minCostToReachParent, queries);
    }

private:
    vector<int> createMinCostToReachParent(UnionFind& unionFind, int numberOfNodes, span<const vector<int>> edges) const {
        vector<int> minCostToReachParent(numberOfNodes, numeric_limits<int>::max());

        for (const auto& edge : edges) {
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

    vector<int> createMinCostPerQuery(UnionFind& unionFind, span<const int> minCostToReachParent, span<const vector<int>> queries) const {
        vector<int> minCostPerQuery(queries.size());

        for (size_t i = 0; i < queries.size(); ++i) {
            int parentFirst = unionFind.findParent(queries[i][0]);
            int parentSecond = unionFind.findParent(queries[i][1]);

            if (parentFirst != parentSecond) {
                minCostPerQuery[i] = NO_PATH_FOUND;
            }
            else {
                minCostPerQuery[i] = minCostToReachParent[parentFirst];
            }
        }

        return minCostPerQuery;
    }
};
