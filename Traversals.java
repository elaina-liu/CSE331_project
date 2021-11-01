package ub.cse.algo;

import ub.cse.algo.util.Pair;

import java.util.*;

class Traversals {

    /**
     * Finds the length of the shortest path to all client nodes
     * using BFS. The result is a HashMap with the keys being client
     * IDs and the values being the path length
     *
     * @param graph: Graph object representing the network
     * @param clients: List of Clients
     * @return Map of Client IDs to the length of the shortest path
     */
    static HashMap<Integer, Integer> bfs(Graph graph, List<Client> clients){
        // Initialize the distances array for all nodes in the graph
        int[] distances = new int[graph.size()];
        Arrays.fill(distances, -1);
        distances[graph.contentProvider] = 0;

        // Run BFS
        Queue<Integer> queue = new LinkedList<>();
        queue.add(graph.contentProvider);
        while (!queue.isEmpty()) {
            int node = queue.poll();

            for (int neighbor : graph.get(node)) {
                if (distances[neighbor] == -1) {
                    distances[neighbor] = distances[node] + 1;
                    queue.add(neighbor);
                }
            }
        }

        // Get all the distances to the clients only
        HashMap<Integer, Integer> clientDistances = new HashMap<>(clients.size());
        for (Client client : clients) {
            clientDistances.put(client.id, distances[client.id]);
        }
        return clientDistances;
    }

    /**
     * Finds the shortest path to all the clients using BFS
     *
     * @param graph: Graph object representing the network
     * @param clients: List of the Clients
     * @return Map of Client IDs to an ArrayList representing
     *          the path from ISP to Client
     */
    static HashMap<Integer, ArrayList<Integer>> bfsPaths(Graph graph, ArrayList<Client> clients){
        /*
            Initialize the prior array with -1 for storing the node
            that is before the current one in the shortest paths
         */
        int[] priors = new int[graph.size()];
        Arrays.fill(priors, -1);

        // Run BFS, finding the nodes parent in the shortest path
        Queue<Integer> searchQueue = new LinkedList<>();
        searchQueue.add(graph.contentProvider);
        while (!searchQueue.isEmpty()) {
            int node = searchQueue.poll();
            for (int neighbor : graph.get(node)) {
                if (priors[neighbor] == -1 && neighbor != graph.contentProvider) {
                    priors[neighbor] = node;
                    searchQueue.add(neighbor);
                }
            }
        }

        // Get the final shortest paths
        return pathsFromPriors(clients, priors);
    }

    /**
     * Helper function to turn prior array to a Map of Paths
     *
     * @param clients: List of Clients
     * @param priors: Array of prior IDs
     * @return Map of Client IDs to an ArrayList representing
     *          the path created from the priors
     */
    private static HashMap<Integer, ArrayList<Integer>> pathsFromPriors(ArrayList<Client> clients, int[] priors) {
        HashMap<Integer, ArrayList<Integer>> paths = new HashMap<>(clients.size());
        // For every client, traverse the prior array, creating the path
        for (Client client : clients) {
            ArrayList<Integer> path = new ArrayList<>();
            int currentNode = client.id;
            while (currentNode != -1) {
                /*
                    Add this ID to the beginning of the
                    path so the path ends with the client
                 */
                path.add(0, currentNode);
                currentNode = priors[currentNode];
            }
            paths.put(client.id, path);
        }
        return paths;
    }
}
