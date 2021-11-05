package ub.cse.algo;

import java.util.*;

public class Solution {

    private Info info;
    private Graph graph;
    private ArrayList<Client> clients;
    private ArrayList<Integer> bandwidths;

    //Router_delay contains unexplored clients with the ID of clients and router_delay value
    private PriorityQueue<int[]> Router_delay = new PriorityQueue<>(new Comparator<int[]>() {
        @Override
        public int compare(int[]o1, int[] o2) {
            if(o1[1]<o2[1]) return -1;
            return 1;
        }
    });
    private HashMap<Integer,Integer> unexplored_clients = new HashMap<>();

    //id_bandwidth will be sort by the bandwidth from lowest to highest
    private PriorityQueue<int[]> id_bandwidth = new PriorityQueue<>(new Comparator<int[]>() {
        @Override
        public int compare(int[]o1, int[] o2) {
            if(o1[1]<o2[1]) return -1;
            return 1;
        }
    });
    /**
     * Basic Constructor
     *
     * @param info: data parsed from input file
     */
    public Solution(Info info) {
        this.info = info;
        this.graph = info.graph;
        this.clients = info.clients;
        this.bandwidths = info.bandwidths;
    }

    /**
     * Method that returns the calculated 
     * SolutionObject as found by your algorithm
     *
     * @return SolutionObject containing the paths, priorities and bandwidths
     */
    public SolutionObject outputPaths() {
        HashMap<Integer, Integer> shortest_path_length = Traversals.bfs(graph,clients);
        SolutionObject sol = new SolutionObject();
        /* TODO: Your solution goes here */
        //get the delay for each client
        sol.paths =  Traversals.bfsPaths(graph,clients);

        //get the provider index
        int provider = sol.paths.get(clients.get(0).id).get(0);

        //put all the clients in the unexplored_client
        PriorityQueue<Integer> a = new PriorityQueue<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if(o1<o2) return -1;
                return 1;
            }
        });

        //check the path is available for this client
        for(Client c: clients){

        }

        ArrayList<Integer> nodes = graph.get(provider);
        //remove the unexplored clients, get all the nodes in the id_bandwidth map
        for(int n: nodes){
            if(unexplored_clients.containsKey(n)){
                unexplored_clients.remove(n);
            }
            else{
                id_bandwidth.add(new int[]{n,bandwidths.get(n) });
            }
        }


        return sol;
    }
    public ArrayList<Integer> convert_path (int client_id, int provider, ArrayList<ArrayList<Integer>> paths){
        //create a new path for this client
        HashMap<Integer, ArrayList<Integer>> new_path = new HashMap<>();
        //stores the explored nodes
        Queue<Integer> queue = new LinkedList<>();
        queue.add(provider);

        //priors is to check if the node has been explored
        int[] priors = new int[graph.size()];
        Arrays.fill(priors, -1);
        priors[provider] = 0;
        //put the path of the provider for the path map
        ArrayList<Integer> provider_path = new ArrayList<>();
        provider_path.add(provider);
        new_path.put(provider, provider_path );

        //explore nodes to find the client
        while (!queue.isEmpty()) {
            int node = queue.poll();
            //current level of this parent
            int level = priors[node];
            //get all the adjacent_node;
            ArrayList<Integer> adjacent_nodes = graph.get(node);
            ArrayList<Integer> nodes_path = new_path.get(node);
            for(int a_nodes: adjacent_nodes) {
                if(a_nodes == client_id){
                    nodes_path.add(client_id);
                    return nodes_path;
                }
                else{
                    //unexplored
                    if(priors[a_nodes]==-1){
                        //check if the node could go through in certain level
                        if(check_through(paths, level, a_nodes))
                        {
                            queue.add(a_nodes);
                            priors[a_nodes] =  level+1;
                            ArrayList<Integer> new_node_path = new ArrayList<>(nodes_path);
                            new_node_path.add(a_nodes);
                            new_path.put(a_nodes, new_node_path);
                        }
                    }
                }
            }
        }
        return new_path.get(client_id);
    }
    public boolean check_through(ArrayList<ArrayList<Integer>> paths, int level, int a_nodes){
        level++;
        for(ArrayList<Integer> p: paths){
            if(level < paths.size()){
                if(p.get(level)==a_nodes) return false;
            }
        }
        return true;
    }

}

