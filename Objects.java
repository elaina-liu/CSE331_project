package ub.cse.algo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

class Client implements Serializable {

    int id, payment, priority;
    float alpha, beta;
    boolean isRural, isFcc;


    Client(int id, float alpha, float beta, int payment, boolean isRural, boolean isFcc) {
        this.id = id;
        this.alpha = alpha;
        this.beta = beta;
        this.payment = payment;
        this.isRural = isRural;
        this.isFcc = isFcc;
        this.priority = 0;
    }

    Client(int id, float alpha, float beta, int payment, boolean isRural, boolean isFcc, int priority) {
        this(id, alpha, beta, payment, isRural, isFcc);
        this.priority = priority;
    }

    @Override
    public String toString() {
        return String.format("%s(ID: %d, alpha: %f, beta: %f, isRural: %b, isFcc: %b, priority: %d)",
                this.getClass().getName(), this.id, this.alpha, this.beta, this.isRural, this.isFcc, this.priority);
    }
}

/**
 * Packet object, not actually "forwarded" int the technical sense of the word
 * but its location variable helps keep track of how far along in its path it is
 */
class Packet {

    // Client ID of the packet destination
    int client;
    // Delay that the packet has before reaching the client
    int delay;
    // Current location of the packet in the path
    int location;
    // Might not need this since we sort the clients initially, but for the sake of completeness
    int priority;
    // The path that the solution has deemed is best to get to the client
    ArrayList<Integer> path;

    /**
     * @param client: Client identifier. Not a reference to the client.
     * @param path: List of Nodes in the path determined by the student.
     * @param priority: Priority of the packet
     */
    private Packet(int client, ArrayList<Integer> path, int priority){
        this.client = client;
        this.delay = 0;
        this.location = 0;
        this.priority = priority;
        this.path = path;
    }

    /**
     * @param client: Client identifier. Not a reference to the client.
     * @param path: List of Nodes in the path determined by the student.
     */
    Packet(int client, ArrayList<Integer> path){
        this(client, path, 0);
    }

    @Override
    public String toString() {
        return String.format("%s (Client: %d, Delay: %d, Location: %d, Priority: %d, Path: %s)",
                this.getClass().getName(), this.client, this.delay, this.location, this.priority, this.path);
    }
}

/**
 * Solution Object for storing the solution's paths, bandwidths and priorities
 * to be simulated and revenue calculated for.
 */
class SolutionObject implements Serializable {
    // The Solution's "Optimal" paths, priorities and bandwidths
    HashMap<Integer, ArrayList<Integer>> paths;
    HashMap<Integer, Integer> priorities;
    ArrayList<Integer> bandwidths;

    /**
     * Default Constructor
     */
    SolutionObject() {
        this.paths = new HashMap<>();
        this.priorities = new HashMap<>();
        this.bandwidths = new ArrayList<>();
    }

    /**
     * Constructor to assign the all the solution structures
     *
     * @param paths: Paths to all of the clients from the ISP
     * @param priorities: Priorities of all the Clients
     * @param bandwidths: Bandwidths of all the Nodes
     */
    SolutionObject (HashMap<Integer, ArrayList<Integer>> paths, HashMap<Integer, Integer> priorities, ArrayList<Integer> bandwidths){
        this.paths = paths;
        this.priorities = priorities;
        this.bandwidths = bandwidths;
    }
}
