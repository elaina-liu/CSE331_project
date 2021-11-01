package ub.cse.algo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Class used for parsing the Problem input files
 */
public class MPUtility {
    // Graph representing the network
    private Graph graph;
    // The problem number used for parsing
    private int problem;

    /**
     * Basic Constructor
     *
     * @param problem: problem number
     */
    MPUtility (int problem) {
        super();
        this.problem = problem;
    }

    /**
     * Reads the network from the input file and
     * creates the Graph object representing the network
     *
     * @param filename: the name of the input file
     * @return a Graph object representing the network
     */
    public Graph readFile(String filename) {
        HashMap<Integer, ArrayList<Integer>> graph = new HashMap<>();
        int contentProvider = 0;
        int node = 0;

        // Read and parse the file
        try {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);
            // Get the Content Provider
            contentProvider = Integer.parseInt(scanner.nextLine());

            // For every line in the file
            while (scanner.hasNextLine()) {
                // Get the node's neighbors
                String[] line = scanner.nextLine().split(" ");
                ArrayList<Integer> neighbors = new ArrayList<Integer>();
                for (String neighbor : line) {
                    neighbors.add(Integer.parseInt(neighbor));
                }
                // Add the node to the graph and increment the node counter
                graph.put(node++, neighbors);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        this.graph = new Graph(contentProvider, graph);
        return this.graph;
    }

    /**
     * Parses the input file to create the info object
     * to be passed to the solutions
     *
     * @param filename: the name of the input file
     * @return Info object containing all the problem data
     */
    public Info readInfo(String filename) {
        Info info = new Info(this.graph);
        // Read and parse the file
        try {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);

            // Parse network information based on problem
            if (this.problem == 3 || this.problem == 4) {
                info.rho1 = Float.parseFloat(scanner.nextLine());
                info.rho2 = Float.parseFloat(scanner.nextLine());
                info.lawsuit = Float.parseFloat(scanner.nextLine());
                info.fccFine = Float.parseFloat(scanner.nextLine());
            }
            if (this.problem >= 3 && this.problem <= 5) {
                info.costBandwidth = Float.parseFloat(scanner.nextLine());
            }
            for (int id = 0; scanner.hasNextLine(); ++id) {
                String[] line = scanner.nextLine().split(" ");

                // Parse this Node's bandwidth
                int bandwidth = Integer.parseInt(line[Globals.BANDWIDTHS]);
                bandwidth = (bandwidth == -1) ? Integer.MAX_VALUE : bandwidth;

                // This Node is a client
                if (Integer.parseInt(line[Globals.IS_CLIENT]) == 1) {
                    // Parse the node's info
                    float alpha = Float.parseFloat(line[Globals.ALPHAS]);
                    alpha = (alpha >= 1) ? alpha : Float.POSITIVE_INFINITY;
                    int payment = Integer.parseInt(line[Globals.PAYMENTS]);
                    float beta = Float.POSITIVE_INFINITY;
                    boolean isFcc = false;
                    boolean isRural = false;

                    if (this.problem == 3 || this.problem == 4) {
                        beta = Float.parseFloat(line[Globals.BETAS]);
                        beta = (beta >= 1) ? beta : Float.POSITIVE_INFINITY;
                        isFcc = line[Globals.IS_FCC].equals("1");
                    }

                    if (this.problem == 4) {
                        isRural = line[Globals.IS_RURAL].equals("1");
                    }

                    // If this is a rural client then the alpha is set to infinity
                    if (isRural) {
                        alpha = Float.POSITIVE_INFINITY;
                    }

                    info.clients.add(new Client(id, alpha, beta, payment, isRural, isFcc));
                }
                info.bandwidths.add(bandwidth);
            }

            // Find the shortest delays
            info.shortestDelays = Traversals.bfs(info.graph, info.clients);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return info;
    }
}
