
function minimumCost(numberOfNodes: number, edges: number[][], queries: number[][]): number[] {
    this.NO_PATH_FOUND = -1;
    const unionFind = new UnionFind(numberOfNodes);
    const minCostToReachParent = createMinCostToReachParent(unionFind, numberOfNodes, edges);
    return createMinCostPerQuery(unionFind, minCostToReachParent, queries);
};

function createMinCostToReachParent(unionFind: UnionFind, numberOfNodes: number, edges: number[][]): number[] {
    const minCostToReachParent: number[] = new Array(numberOfNodes).fill(Number.MAX_SAFE_INTEGER);

    for (let edge of edges) {
        const parentFirst = unionFind.findParent(edge[0]);
        const parentSecond = unionFind.findParent(edge[1]);
        const cost = edge[2];

        minCostToReachParent[parentFirst] &= cost & minCostToReachParent[parentSecond];
        minCostToReachParent[parentSecond] &= cost & minCostToReachParent[parentFirst];

        if (parentFirst !== parentSecond) {
            unionFind.joinByRank(parentFirst, parentSecond);
        }
    }
    return minCostToReachParent;
}

function createMinCostPerQuery(unionFind: UnionFind, minCostToReachParent: number[], queries: number[][]): number[] {
    const minCostPerQuery: number[] = new Array(queries.length);

    for (let i = 0; i < queries.length; ++i) {
        const parentFirst = unionFind.findParent(queries[i][0]);
        const parentSecond = unionFind.findParent(queries[i][1]);

        if (parentFirst !== parentSecond) {
            minCostPerQuery[i] = this.NO_PATH_FOUND;
        } else {
            minCostPerQuery[i] = minCostToReachParent[parentFirst];
        }
    }

    return minCostPerQuery;
}

class UnionFind {

    private rank: number[];
    private parent: number[];

    constructor(numberOfNodes) {
        this.rank = new Array(numberOfNodes).fill(0);
        this.parent = new Array(numberOfNodes).fill(0);
        // Alternatively: this.parent = Array.from(Array(numberOfNodes).keys());
        for (let node = 0; node < numberOfNodes; ++node) {
            this.parent[node] = node;
        }
    }

    findParent(node: number): number {
        while (this.parent[node] !== node) {
            node = this.parent[node];
        }
        return this.parent[node];
    }

    joinByRank(firstNode: number, secondNode: number) {
        if (this.rank[firstNode] >= this.rank[secondNode]) {
            this.parent[secondNode] = firstNode;
            this.rank[firstNode] += this.rank[secondNode] + 1;
        } else {
            this.parent[firstNode] = secondNode;
            this.rank[secondNode] += this.rank[firstNode] + 1;
        }
    }
}
