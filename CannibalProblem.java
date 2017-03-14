/* Provided by Prof. Devin Balkcom
 * CS76, Fall 2016
 * Tuesday, 20 September 2016
 * Raunak Bhojwani
 */


package cannibals;

import java.util.ArrayList;
import java.util.Arrays;


// for the first part of the assignment, you might not extend UUSearchProblem,
//  since UUSearchProblem is incomplete until you finish it.

public class CannibalProblem extends UUSearchProblem {

	// the following are the only instance variables you should need.
	//  (some others might be inherited from UUSearchProblem, but worry
	//  about that later.)

	private int goalm, goalc, goalb;
	private int totalMissionaries, totalCannibals; 

	public CannibalProblem(int sm, int sc, int sb, int gm, int gc, int gb) {
		// I (djb) wrote the constructor; nothing for you to do here.

		startNode = new CannibalNode(sm, sc, sb, 0);
		goalm = gm;
		goalc = gc;
		goalb = gb;
		totalMissionaries = sm;
		totalCannibals = sc;
		
	}
	
	// node class used by searches.  Searches themselves are implemented
	//  in UUSearchProblem.
	private class CannibalNode implements UUSearchNode {

		// do not change BOAT_SIZE without considering how it affect
		// getSuccessors. 
		
		private final static int BOAT_SIZE = 2;
	
		// how many missionaries, cannibals, and boats
		// are on the starting shore
		private int[] state; 
		
		// how far the current node is from the start.  Not strictly required
		//  for search, but useful information for debugging, and for comparing paths
		private int depth;  

		public CannibalNode(int m, int c, int b, int d) {
			state = new int[3];
			this.state[0] = m;
			this.state[1] = c;
			this.state[2] = b;
			
			depth = d;

		}

		public ArrayList<UUSearchNode> getSuccessors() {

			// add actions (denoted by how many missionaries and cannibals to put
			// in the boat) to current state.

			// You write this method.  Factoring is usually worthwhile.  In my
			//  implementation, I wrote an additional private method 'isSafeState',
			//  that I made use of in getSuccessors.  You may write any method
			//  you like in support of getSuccessors.

			ArrayList<UUSearchNode> successors = new ArrayList<UUSearchNode>();
			
			// Extract the currect state out of the node for further usage
			int currentMissionaries = state[0];
			int currentCannibals = state[1];
			int currentBoat = state[2];
			int currentDepth = depth;
			
			// Prepare variables to be used in the next level of nodes
			int futureMissionaries;
			int futureCannibals;
			int futureBoat = 1;
			
			// Declare variable for the next node
			UUSearchNode nextNode;
			
			// If the boat is at the source, we know that the boat will be at the target in the next node
			if (currentBoat == 1) {
//				System.out.println("Current boat is 1");
				futureBoat = 0;
				
				// The lower of the remaining missionaries/cannibals and the boat size can board the boat
				futureMissionaries = Math.min(currentMissionaries, BOAT_SIZE);
				futureCannibals = Math.min(currentCannibals, BOAT_SIZE);
				
				for (int missionary = 0; missionary <= futureMissionaries; missionary ++) {
//					System.out.println("Missionary " + missionary);
					for (int cannibal = 0; cannibal <= futureCannibals; cannibal ++) {
//						System.out.println("Cannibal " + cannibal);
						// This condition ensures that only the correct combinations of missionaries and cannibals proceed
						if (0 < (cannibal + missionary) && (cannibal + missionary) <= BOAT_SIZE) {
							// Create a new CannibalNode for the new state.
							nextNode = new CannibalNode(currentMissionaries - missionary, currentCannibals - cannibal, futureBoat, currentDepth);
							if (nextNode.isSafeState()) {
								// Add state to successors if it is a legal state
								successors.add(nextNode);
//								System.out.println(nextNode);
							}
						}
					}
				}	
			}
			
			// If boat is at the target, it will be at source in the next level of nodes.
			else if (currentBoat == 0) {
//				System.out.println("Current boat is 0");
				futureBoat = 1;
				futureMissionaries = Math.min(totalMissionaries - currentMissionaries, BOAT_SIZE);
				futureCannibals = Math.min(totalCannibals - currentCannibals, BOAT_SIZE);
				
				currentDepth++;
				

				for (int missionary = 0; missionary <= futureMissionaries; missionary++) {
//					System.out.println("Missionary " + missionary);
					for (int cannibal = 0; cannibal <= futureCannibals; cannibal++) {
//						System.out.println("Cannibal " + cannibal);
						if (0 < (cannibal + missionary) && (cannibal + missionary) <= BOAT_SIZE) {
							nextNode = new CannibalNode(currentMissionaries + missionary, currentCannibals + cannibal, futureBoat, currentDepth);
							if (nextNode.isSafeState()) {
								successors.add(nextNode);
//								System.out.println(nextNode);
							}
						}
					}
				}
			}
//			System.out.println(successors);
			return successors;
		}

		public boolean isSafeState() {
			// The state depends on the location of the boat, check for all possible illegal states
			if (state[0] == 0) {
				return (totalMissionaries - state[0]) >= (totalCannibals - state[1]);
			}
			else if ((totalMissionaries - state[0]) == 0) {
				return (state[0] >= state[1]);
			}
			return (state[0] >= state[1]) && (totalMissionaries - state[0]) >= (totalCannibals - state[1]);
		}
		
		public boolean goalTest() {
			// you write this method.  (It should be only one line long.)
			return (state[0] == goalm) && (state[1] == goalc) && (state[2] == goalb);

		}

		

		// an equality test is required so that visited lists in searches
		// can check for containment of states
		@Override
		public boolean equals(Object other) {
			return Arrays.equals(state, ((CannibalNode) other).state);
		}

		@Override
		public int hashCode() {
			return state[0] * 100 + state[1] * 10 + state[2];
		}

		@Override
		public String toString() {
			// you write this method
			return ("(" + state[0] + ", " + state[1] + ", " + state[2] + ", " + depth + ")");
		}
		
		
        // You might need this method when you start writing 
        // (and debugging) UUSearchProblem.
        
		public int getDepth() {
			return depth;
		}

	}
	
	public static void main(String args[]) {
		CannibalProblem mcProblem = new CannibalProblem(3, 3, 1, 0, 0, 0);
		ArrayList<UUSearchNode> firstLevelNodes = mcProblem.startNode.getSuccessors();
		
		for (int i=0; i < firstLevelNodes.size(); i++) {
			firstLevelNodes.get(i).getSuccessors();
		}	
	}
}
