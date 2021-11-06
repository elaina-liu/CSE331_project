package ub.cse.algo;

import java.util.*;

public class Solution {

    private Info info;
    private Graph graph;
    private ArrayList<Client> clients;
    private ArrayList<Integer> bandwidths;

    //Router_delay contains unexplored clients with the ID of clients and router_delay value and payment
    private PriorityQueue<double[]> Client_information = new PriorityQueue<>(new Comparator<double[]>() {
        @Override
        public int compare(double[]o1, double[] o2) {
            if(o1[1]<o2[1]) return -1;
            else if(o1[1]==o2[1]){
                if (o1[2]< o2[2]) return -1;
                else return 1;
            }
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

        //order the client's Delay with payment
        for(Client c: clients){
            int paths_length = sol.paths.get(c.id).size();
            double[] inf = new double[]{c.id, paths_length * c.alpha, c.payment };
            Client_information.add(inf);
        }
        HashMap<Integer,ArrayList<Integer>> BAND_WIDTH = band_width(sol.paths);
        PriorityQueue<int[]> TheQueue = new PriorityQueue<>(new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                if(o1[1]>o2[1]) return 1;
                else return -1;
            }
        });

        for(int k: BAND_WIDTH.keySet()){
            TheQueue.add(new int[]{k, bandwidths.get(k)});
        }

        while(!TheQueue.isEmpty()){
            Client_information.clear();
            int band_node = TheQueue.poll()[0];
            ArrayList<Integer> clients_nodes =BAND_WIDTH.get(band_node);
            for(int c : clients_nodes){
                float Delay = clients.get(c).alpha;
                int path_length = sol.paths.get(c).size();
                Client_information.add(new double[]{c,(Delay*path_length),clients.get(c).payment});
            }
            int band_node_width = bandwidths.get(band_node);
            int i = 0;
            while(!Client_information.isEmpty()){
                double[] client_info = Client_information.poll();
                i++;
                if(i > band_node_width){
                    ArrayList<Integer> client_path = convert_path((int)client_info[0],provider,sol.paths.get((int)client_info[0]));
                    if(!client_path.isEmpty()) sol.paths.put((int)client_info[0], client_path);
                }
                int id =(int)client_info[0];
            }
        }
        //remove the unexplored clients, get all the nodes in the id_bandwidth map
        return sol;
    }

    private HashMap<Integer, ArrayList<Integer>> band_width(HashMap<Integer, ArrayList<Integer>> paths) {

        HashMap<Integer, ArrayList<Integer>> ret = new HashMap<Integer, ArrayList<Integer>>();
        ArrayList<Integer> containsNode = new ArrayList<Integer>();
        for(Map.Entry<Integer, ArrayList<Integer>> entry : paths.entrySet()){
            int node = entry.getValue().get(1);
            if(ret.containsKey(node)){
                ArrayList<Integer> a = ret.get(node);
                a.add(entry.getKey());
                ret.put(node,a);
            }
            else{
                ret.put(node, new ArrayList<>(){
                    {
                        entry.getKey();
                    }
                });
            }
        }
        return ret;
    }

    public ArrayList<Integer> convert_path (int client_id, int provider, ArrayList<Integer> paths){
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
        if(!new_path.containsKey(client_id)) return new ArrayList<>();
        return new_path.get(client_id);
    }
    public boolean check_through(ArrayList<Integer> paths, int level, int a_nodes){
        level++;
        if(level < paths.size()-1){
            int band1 = bandwidths.get(a_nodes);
            int band2 = bandwidths.get(paths.get(level));
            if(band1<=band2) return false;
        }
        return true;
    }

}