package ub.cse.algo;

import java.util.HashMap;

/**
 * Class for running the grader. Will take in a command line argument specifying
 * the number of testcases to run.
 */
public class Driver { // testing in process! 

    private static String filename;
    private static int problem = 2;

    public static void main(String[] args) {
        /*if (args.length != 1) {
			System.out.println("Please provide the testcase filepath as a command line argument");
			return;
		}*/
		Driver.filename= "MPJava2/testcases/input1.txt";
        
        MPUtility mpu = new MPUtility(Driver.problem);
        Graph graph = mpu.readFile(Driver.filename);
        Info info = mpu.readInfo(Driver.filename + "-info");
 
        Solution student = new Solution(info);
		SolutionObject solObj = student.outputPaths();
		info.solutionObject = solObj;
        
        if (solObj.bandwidths == null || solObj.bandwidths.isEmpty()) {
            solObj.bandwidths = info.bandwidths;
        }

        if(solObj == null){
			System.out.println("null Solution Object returned");
		} else if (solObj.paths == null) {
			System.out.println("null paths returned");
		} else if (solObj.paths.isEmpty()) {
			System.out.println("Paths are empty");
		} else {
			float revenue = Driver.runHelper(info, solObj);

			System.out.println("Your Solution");
			System.out.println("=============================================");
			System.out.println("Revenue: " + revenue);
		}
	}
	
    /**
     * This helper method will run the solution throught the simulator
     * and return the revenue that the solution generates
     *
     * @param info: Info object containing parsed data
     * @param solutionObject: The solution object to be calculated
     * @return
     */
    static float runHelper(Info info, SolutionObject solutionObject) {
        // Run the solution and get the paths
        HashMap<Integer, Integer> delays = Simulator.run(info.graph, info.clients, solutionObject);
        // Do the penalties need to be applied?
        boolean pen_1 = false, pen_2 = false;
        if (Driver.problem == 3 || Driver.problem == 4) {
            pen_1 = true;
            pen_2 = true;
        }
        // Does the bandwidth penalty need to be applied
        boolean pen_bandwidth = !info.bandwidths.equals(solutionObject.bandwidths);
        // Calculate and return the revenue
        return Revenue.revenue(info, solutionObject, delays, pen_1, pen_2, pen_bandwidth, problem);
    }
}
