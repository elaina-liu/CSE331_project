package ub.cse.algo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class that contains formulas for computing the solution's revenue
 */
class Revenue {
    /**
     * Static method that will compute the revenue generated from
     * the given solution given the boolean variables
     *
     * @param info: data parsed from the input file
     * @param solutionObject: the "optimal" solution
     * @param delays: Map of the delays the packets took to reach the clients
     * @param pen_1: should the first penalty be applied?
     * @param pen_2: should the second penalty be applied?
     * @param updated_bandwidths: have the bandwidths been changed?
     * @return the calculated revenue
     */
    static float revenue(Info info, SolutionObject solutionObject, HashMap<Integer, Integer> delays,
                  boolean pen_1, boolean pen_2, boolean updated_bandwidths, int problem) {
        // List of clients who have complained to the FCC
        ArrayList<Client> complaints = new ArrayList<>();
        // Rolling sum of the revenue
        float rev = 0f;

        for (Client client : info.clients) {
            // Find the revenue of the client and add it to the current total
            float currRev = pen_0(client, delays.get(client.id), info.shortestDelays.get(client.id), complaints);
            rev += currRev;

            // For problem 5, if a single client had their packet delay, no revenue is made
            if (problem == 5 && currRev == 0) {
                return 0;
            }

            // If the client complains, add them to the list of those that have complained
            if ((pen_1 || pen_2) && currRev != 0) {
                if (delays.get(client.id) > (client.beta * info.shortestDelays.get(client.id))) {
                    complaints.add(client);
                }
            }
        }

        // Apply the first penalty if needed
        if (pen_1) {
            rev += pen_1(info.lawsuit, info.rho1, info.clients, complaints);
        }

        // Apply the second penalty if needed
        if (pen_2) {
            rev += pen_2(info.rho2, info.clients, info.fccFine, complaints);
        }

        // Apply the updated bandwidths penalty if needed
        if (updated_bandwidths) {
            rev += pen_bandwidth(info.bandwidths, solutionObject.bandwidths, info.costBandwidth);
        }

        return rev;
    }

    /**
     * Calculate the revenue gained from the current Client
     *
     * @param client: The Client to bill
     * @param delay: The delay the packet takes to get to the CLient
     * @param optimal: The optimal delay as found from BFS
     * @param complaints: The list of Clients who have complained
     * @return the revenue gained from this client
     */
    static private float pen_0(Client client, int delay, int optimal, ArrayList<Client> complaints) {
        // If the delay is than the threshold, the client complains and does not pay
        if (delay > (client.alpha * optimal)) {
            complaints.add(client);
            return 0;
        }
        // Otherwise, the client pays their bill
        return client.payment;
    }

    /**
     * @param lawsuit: fine to be applied if too many client complain
     * @param rho1: ratio of client that can complain before the penalty is applied
     * @param clients: list of Clients
     * @param complaints: list of Clients that have complained
     * @return the penalty to be applied to the revenue
     */
    static private float pen_1(float lawsuit, float rho1, ArrayList<Client> clients, ArrayList<Client> complaints) {
        // If too many client complained, then apply the penalty
        if (complaints.size() > (int) (rho1 * clients.size())) {
            return -lawsuit;
        }
        return 0;
    }

    /**
     * @param rho2: ratio of fcc clients that can complain before penalty is applied
     * @param clients: list of Clients
     * @param fccFine: the fine to be applied if too many complain
     * @param complaints: list of Clients that have complained
     * @return penalty to be applied to the revenue
     */
    static private float pen_2(float rho2, ArrayList<Client> clients, float fccFine, ArrayList<Client> complaints) {
        // Count the total clients that complained that are in cahoots with the FCC
        int count = (int) complaints.stream().filter(client -> client.isFcc).count();
        // Counts the number of clients in cahoots with the FCC
        int numFcc = (int) clients.stream().filter(client -> client.isFcc).count();
        // If the count is greater than threshold of those client that are in cahoots, apply the penalty
        if (count > (int)(numFcc * rho2)) {
            return -fccFine;
        }
        return 0;
    }

    /**
     * Calculate the penalty for changing the bandwidths
     *
     * @param original: the original bandwidths
     * @param updated: the updated bandwidths
     * @param updateCost: the cost to update a bandwidth
     * @return the penalty to be applied to the revenue
     */
    static private float pen_bandwidth(ArrayList<Integer> original, ArrayList<Integer> updated, float updateCost) {
        float cost = 0f;
        for (int node = 0; node < original.size(); ++node) {
            // Add the ratio of the difference between bandwidths and the update cost to the total cost
            int diff = updated.get(node) - original.get(node);
            if (diff > 0) {
                cost += (float) diff * updateCost;
            }
        }
        return -cost;
    }
}
