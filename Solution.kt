
import kotlin.collections.*

class Solution {

    private companion object {
        const val NO_PATH_FOUND = -1
    }

    fun minimumCost(numberOfNodes: Int, edges: Array<IntArray>, queries: Array<IntArray>): IntArray {
        val unionFind = UnionFind(numberOfNodes)
        val minCostToReachParent = createMinCostToReachParent(unionFind, numberOfNodes, edges)
        return createMinCostPerQuery(unionFind, minCostToReachParent, queries)
    }

    private fun createMinCostToReachParent(unionFind: UnionFind, numberOfNodes: Int, edges: Array<IntArray>): IntArray {
        val minCostToReachParent = IntArray(numberOfNodes).apply { fill(Int.MAX_VALUE) }
        
        for (edge in edges) {
            val parentFirst = unionFind.findParent(edge[0])
            val parentSecond = unionFind.findParent(edge[1])
            val cost = edge[2]

            minCostToReachParent[parentFirst] =
                minCostToReachParent[parentFirst] and cost and minCostToReachParent[parentSecond]
            minCostToReachParent[parentSecond] =
                minCostToReachParent[parentSecond] and cost and minCostToReachParent[parentFirst]

            if (parentFirst != parentSecond) {
                unionFind.joinByRank(parentFirst, parentSecond)
            }
        }
        return minCostToReachParent
    }

    private fun createMinCostPerQuery(unionFind: UnionFind, minCostToReachParent: IntArray, queries: Array<IntArray>): IntArray {
        val minCostPerQuery = IntArray(queries.size)

        for (i in queries.indices) {
            val parentFirst = unionFind.findParent(queries[i][0])
            val parentSecond = unionFind.findParent(queries[i][1])

            if (parentFirst != parentSecond) {
                minCostPerQuery[i] = NO_PATH_FOUND
            } else {
                minCostPerQuery[i] = minCostToReachParent[parentFirst]
            }
        }

        return minCostPerQuery
    }
}

class UnionFind(numberOfNodes: Int) {

    private var rank = IntArray(numberOfNodes)
    private var parent = IntArray(numberOfNodes) { i -> i }

    /*
    Alternatively for array parent:
     init {
        for (node in 0..<numberOfNodes) {
            parent[node] = node
        }
     }
    */

    fun findParent(node: Int): Int {
        var node = node
        while (parent[node] != node) {
            node = parent[node]
        }
        return parent[node]
    }

    fun joinByRank(firstNode: Int, secondNode: Int) {
        if (rank[firstNode] >= rank[secondNode]) {
            parent[secondNode] = firstNode;
            rank[firstNode] += rank[secondNode] + 1
        } else {
            parent[firstNode] = secondNode
            rank[secondNode] += rank[firstNode] + 1
        }
    }
}
