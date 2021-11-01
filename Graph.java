package ub.cse.algo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Object to represent a graph. Key is the node ID
 * and the Value is the list the node's neighbors.
 * The Content Provider ID is also stored here
 */
public class Graph extends HashMap<Integer, ArrayList<Integer>> {
    int contentProvider;

    /**
     * @param contentProvider: The ID of the content provider
     * @param graph: The graph represented as a hashmap
     */
    public Graph (int contentProvider, HashMap<Integer, ArrayList<Integer>> graph){
        super (graph);
        this.contentProvider = contentProvider;
    }
}
