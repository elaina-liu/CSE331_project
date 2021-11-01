package ub.cse.algo;

import java.util.*;

/**
 * Class containing the function to Simulate the Solution on the
 * network to find the packet delays to each Client
 */
class Simulator {
    /**
     * Checks if the two nodes are neighbors
     *
     * @param graph: Graph object
     * @param node1: first node
     * @param node2: second node
     * @return true if the node2 has node1 in their neighbor list otherwise false
     */
    private static boolean validateEdge(Graph graph, int node1, int node2) {
        return graph.get(node2).contains(node1);
    }

    /**
     * Simulate the solution to find the delays to each
     * Client and return them in a HashMap
     *
     * @param graph: Graph Object representing the network
     * @param clientList: List of Client Objects
     * @param sol: Solution to Simulate
     * @return a map of Client IDs to packet delays
     */
    static HashMap<Integer, Integer> run(Graph graph, ArrayList<Client> clientList, SolutionObject sol) {
        // Creating shortest paths
        HashMap<Integer, Integer> shortestDistances = Traversals.bfs(graph, clientList);

        // Copy the clients over
        List<Client> clients = new LinkedList<>();
        for (Client c : clientList) {
            clients.add(new Client(c.id, c.alpha, c.beta, c.payment, c.isRural, c.isFcc));
        }

        // Sort the clients by priority in descending order
        Comparator<Client> comp = Comparator.comparingInt((c)->(sol.priorities.get(c.id)!=null)?sol.priorities.get(c.id):0);
        clients.sort(comp.reversed());

        // Mapping client ids to their corresponding packet objects
        HashMap<Integer, Packet> packets = new HashMap<>();
        for (Client client : clients) {
            packets.put(client.id, new Packet(client.id, sol.paths.get(client.id)));
        }

        ArrayList<Integer> bandwidths = (ArrayList<Integer>)sol.bandwidths.clone();
        // Keep track of what nodes are actively forwarding packets
        HashMap<Integer, Integer> delays = new HashMap<>();
        Set<Integer> active = new HashSet<>();

        // While there is a Client that has not received a Packet
        while (!clients.isEmpty()) {
            /*
                Iterate through the Clients and move the packets
                forward one more node
             */
            for (Iterator<Client> iter = clients.iterator(); iter.hasNext(); ) {
                Client current = iter.next();
                Packet packet = packets.get(current.id);

                // Make sure the path is valid. Starting with the ISP and ending with the client
                if (packet.path == null || packet.path.isEmpty() || packet.path.get(0) != graph.contentProvider) {
                    delays.put(packet.client, Integer.MAX_VALUE);
                    iter.remove();
                    continue;
                }

                int currentNode = packet.path.get(packet.location);
                if (packet.location == packet.path.size()-1) {
                    if (currentNode == packet.client && packet.location >= shortestDistances.get(packet.client)) {
                        delays.put(packet.client, packet.delay);
                    } else {
                        delays.put(packet.client, Integer.MAX_VALUE);
                    }
                    iter.remove();
                    continue;
                }

                // Packet has not reached yet
                packet.delay++;

                // Only forward packets when the bandwidth is exhausted
                if (bandwidths.get(currentNode) > 0) {
                    active.add(currentNode);
                    bandwidths.set(currentNode, bandwidths.get(currentNode)-1);
                    packet.location++;
                    if (!validateEdge(graph, currentNode, packet.path.get(packet.location))) {
                        delays.put(packet.client, Integer.MAX_VALUE);
                        iter.remove();
                    }
                }
            }

            // Reset the bandwidths and clear the active nodes
            for (int router : active) {
                bandwidths.set(router, sol.bandwidths.get(router));
            }
            active.clear();
        }
        return delays;
    }
}
