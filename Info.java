package ub.cse.algo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This Object handles all the information parsed from
 * the input file to be passed to the solutions and to
 * be returned to the grader.
 */
class Info implements Serializable {
    // The graph of the network
    Graph graph;
    // List of the clients in the network
    ArrayList<Client> clients;
    // List of Bandwidths where the index is the node ID
    ArrayList<Integer> bandwidths;
    /*
    The shortest delays for this network to the clients
    found by using the lengths of the BFS paths
     */
    HashMap<Integer, Integer> shortestDelays;
    /*
    Object to store the node bandwidths and paths to
    the clients found to be the optimal solution
     */
    SolutionObject solutionObject;
    // Multipliers parsed from the input
    float rho1;
    float rho2;
    // Fines parsed from the input
    float lawsuit;
    float fccFine;
    float costBandwidth;

    /**
     * Basic constructor
     *
     * @param graph: The network graph
     */
    Info(Graph graph) {
        this.graph = graph;
        this.clients = new ArrayList<>();
        this.bandwidths = new ArrayList<>();
        this.shortestDelays = new HashMap<>();
        this.rho1 = Float.NaN;
        this.rho2 = Float.NaN;
        this.lawsuit = Float.NaN;
        this.fccFine = Float.NaN;
        this.costBandwidth = Float.NaN;
        solutionObject = null;
    }

    /**
     * This function creates a new Info object
     * that is a replica of the current one
     *
     * @return Clone of this object
     */
    @Override
    protected Object clone() {
        Info clone = new Info((Graph) this.graph.clone());
        clone.bandwidths = (ArrayList<Integer>) this.bandwidths.clone();
        clone.clients = (ArrayList<Client>) this.clients.clone();
        clone.shortestDelays = (HashMap<Integer, Integer>) this.shortestDelays.clone();
        clone.rho1 = this.rho1;
        clone.rho2 = this.rho2;
        clone.lawsuit = this.lawsuit;
        clone.fccFine = this.fccFine;
        clone.costBandwidth = this.costBandwidth;
        clone.solutionObject = this.solutionObject;
        return clone;
    }
}
