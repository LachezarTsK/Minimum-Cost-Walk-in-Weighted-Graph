
package main

import (
    "fmt"
    "math"
)

const NO_PATH_FOUND = -1

func minimumCost(numberOfNodes int, edges [][]int, queries [][]int) []int {
    unionFind := NewUnionFind(numberOfNodes)
    minCostToReachParent := createMinCostToReachParent(unionFind, numberOfNodes, edges)
    return createMinCostPerQuery(unionFind, minCostToReachParent, queries)
}

func createMinCostToReachParent(unionFind UnionFind, numberOfNodes int, edges [][]int) []int {
    minCostToReachParent := make([]int, numberOfNodes)
    for node := 0; node < numberOfNodes; node++ {
        minCostToReachParent[node] = math.MaxInt32
    }

    for _, edge := range edges {
        parentFirst := unionFind.findParent(edge[0])
        parentSecond := unionFind.findParent(edge[1])
        cost := edge[2]

        minCostToReachParent[parentFirst] &= cost & minCostToReachParent[parentSecond]
        minCostToReachParent[parentSecond] &= cost & minCostToReachParent[parentFirst]

        if parentFirst != parentSecond {
            unionFind.joinByRank(parentFirst, parentSecond)
        }
    }
    return minCostToReachParent
}

func createMinCostPerQuery(unionFind UnionFind, minCostToReachParent []int, queries [][]int) []int {
    minCostPerQuery := make([]int, len(queries))

    for i := range queries {
        parentFirst := unionFind.findParent(queries[i][0])
        parentSecond := unionFind.findParent(queries[i][1])

        if parentFirst != parentSecond {
            minCostPerQuery[i] = NO_PATH_FOUND
        } else {
            minCostPerQuery[i] = minCostToReachParent[parentFirst]
        }
    }

    return minCostPerQuery
}

type UnionFind struct {
    rank   []int
    parent []int
}

func NewUnionFind(numberOfNodes int) UnionFind {
    unionFind := UnionFind{
        rank:   make([]int, numberOfNodes),
        parent: make([]int, numberOfNodes),
    }

    for node := 0; node < numberOfNodes; node++ {
        unionFind.parent[node] = node
    }

    return unionFind
}

func (this *UnionFind) findParent(node int) int {
    for this.parent[node] != node {
        node = this.parent[node]
    }
    return this.parent[node]
}

func (this *UnionFind) joinByRank(firstNode int, secondNode int) {
    if this.rank[firstNode] >= this.rank[secondNode] {
        this.parent[secondNode] = firstNode
        this.rank[firstNode] += this.rank[secondNode] + 1
    } else {
        this.parent[firstNode] = secondNode
        this.rank[secondNode] += this.rank[firstNode] + 1
    }
}
