/* Template provided by Prof. Devin Balkcom
 * CS76, Fall 2016
 * Tuesday, 20 September 2016
 * author: Raunak Bhojwani
 */
package cannibals;
import java.util.*;

public abstract class UUSearchProblem {
	
	// used to store performance information about search runs.
	//  these should be updated during the process of searches

	// see methods later in this class to update these values
	protected int nodesExplored;
	protected int maxMemory;

	protected UUSearchNode startNode;
	
	protected interface UUSearchNode {
		public ArrayList<UUSearchNode> getSuccessors();
		public boolean goalTest();
		public int getDepth();
		public boolean isSafeState();
	}

	// breadthFirstSearch:  return a list of connecting Nodes, or null
	// no parameters, since start and goal descriptions are problem-dependent.
	//  therefore, constructor of specific problems should set up start
	//  and goal conditions, etc.
	
	public List<UUSearchNode> breadthFirstSearch() {
		
		resetStats();
		
		// Use HashMap to store visited nodes and their backpointers
		HashMap<UUSearchNode, UUSearchNode> explored = new HashMap<>();
		
		// Use a linked list to implement a queue.
		Queue<UUSearchNode> frontier = new LinkedList<UUSearchNode>();
		frontier.add(startNode);
		
		while (!frontier.isEmpty()) {
			// Pop the top node from the queue
			UUSearchNode currentNode = frontier.remove();
			
			// Check if the current node is the goal state
			if (currentNode.goalTest()) {
				updateMemory(explored.size());
				return backchain(currentNode, explored);
			}

			// If not, continue the search through the current node's successors
			List<UUSearchNode> childNodes = currentNode.getSuccessors();
			for (UUSearchNode childNode : childNodes) {
				// If the node is unvisited, add it to the explored HashMap, and add it to the queue
				if (!explored.containsValue(childNode)) {
					explored.put(childNode, currentNode);
					incrementNodeCount();
					frontier.add(childNode);
				}
			}
		}
		// If failure, return null
		return null;
	}
	
	// backchain should only be used by bfs, not the recursive dfs
	private List<UUSearchNode> backchain(UUSearchNode node, HashMap<UUSearchNode, UUSearchNode> visited) {
		// you will write this method
		List<UUSearchNode> finalPath = new ArrayList<UUSearchNode>();
		finalPath.add(node);
		
		// Loop through the HashMap (visited) until you reach the start node
		while (node != startNode) {
			node = visited.get(node);
			finalPath.add(node);
		}
		return finalPath;
	}

	public List<UUSearchNode> depthFirstMemoizingSearch(int maxDepth) {
		resetStats();
		
		// set up a explored hashmap, with integer values for depth
		HashMap<UUSearchNode, Integer> explored = new HashMap<UUSearchNode, Integer>();
		return dfsrm(startNode, explored, 0, maxDepth);	

	}

	// recursive memoizing dfs. Private, because it has the extra
	// parameters needed for recursion.  
	private List<UUSearchNode> dfsrm(UUSearchNode currentNode, HashMap<UUSearchNode, Integer> visited, int depth, int maxDepth) {
		
		// keep track of stats; these calls charge for the current node
		updateMemory(visited.size());
		incrementNodeCount();

		// you write this method. Comments *must* clearly show the
		// "base case" and "recursive case" that any recursive function has.
		
		// Set up a current, and a final path to build recursively
		List<UUSearchNode> finalPath = new ArrayList<UUSearchNode>(Arrays.asList(currentNode));
		List<UUSearchNode> currentPath = new ArrayList<UUSearchNode>();
		
		visited.put(currentNode, depth);
		
		// Base case if depth too large
		if (depth > maxDepth) {
			return null;	
		}
		// Base case if goal reached
		if (currentNode.goalTest()) {
			return finalPath;
		} 
		// Recursive case
		else {
			// For each child node, check if it is visited, and has the appropriate depth
			List<UUSearchNode> childNodes = currentNode.getSuccessors();
			for (UUSearchNode childNode : childNodes) {
				if(!visited.containsKey(childNode) || visited.get(childNode) >= depth + 1) {
					currentPath = dfsrm(childNode, visited, depth + 1, maxDepth);
					if (currentPath != null) {
						// Build final Path
						finalPath.addAll(currentPath);
						return finalPath;
					}
				}
			}
		}
		return null;
	}
	
	
	// set up the iterative deepening search, and make use of dfsrpc
	public List<UUSearchNode> IDSearch(int maxDepth) {
		resetStats();
		
		// Use a hashset to denote the current path, a list for the final path
		HashSet<UUSearchNode> currentPath = new HashSet<UUSearchNode>();
		List<UUSearchNode> finalPath;

		for (int currentDepth = 0; currentDepth <= maxDepth; currentDepth++) {
			// for each depth, use path checking dfs
			finalPath = dfsrpc(startNode, currentPath, 0, currentDepth);
			if (finalPath != null) {
				return finalPath;
			}
		}
		return null;
		
	}

	// set up the depth-first-search (path-checking version), 
	//  but call dfspc to do the real work
	public List<UUSearchNode> depthFirstPathCheckingSearch(int maxDepth) {
		resetStats();
		
		// I wrote this method for you.  Nothing to do.
		HashSet<UUSearchNode> currentPath = new HashSet<UUSearchNode>();
		return dfsrpc(startNode, currentPath, 0, maxDepth);
	}

	// recursive path-checking dfs. Private, because it has the extra
	// parameters needed for recursion.
	private List<UUSearchNode> dfsrpc(UUSearchNode currentNode, HashSet<UUSearchNode> currentPath, int depth, int maxDepth) {
		
		// keep track of stats; these calls charge for the current node
		updateMemory(currentPath.size());
		incrementNodeCount();

		// you write this method. Comments *must* clearly show the
		// "base case" and "recursive case" that any recursive function has.
		
		// Set up lists for current and final paths
		List<UUSearchNode> finalPath = new ArrayList<UUSearchNode>(Arrays.asList(currentNode));
		List<UUSearchNode> path = new ArrayList<UUSearchNode>();
		
		// Base case: depth too large
		if (depth > maxDepth) {
			return null;
		}
		
		currentPath.add(currentNode);
		
		// Base case: if goal reached
		if (currentNode.goalTest()) {
			return finalPath;
		} 
		// Recursive case
		else {
			List<UUSearchNode> childNodes = currentNode.getSuccessors();
			for (UUSearchNode childNode : childNodes) {
				if (!currentPath.contains(childNode)) {
					path = dfsrpc(childNode, currentPath, depth + 1, maxDepth); // for each child, if not already on the path, build path recursively
					if (path != null) {
						finalPath.addAll(path);
						return finalPath;
					}
				}
			}
		}
		currentPath.remove(currentNode);
		return null;
	}

	protected void resetStats() {
		nodesExplored = 0;
		maxMemory = 0;
	}
	
	protected void printStats() {
		System.out.println("Nodes explored during last search:  " + nodesExplored);
		System.out.println("Maximum memory usage during last search " + maxMemory);
	}
	
	protected void updateMemory(int currentMemory) {
		maxMemory = Math.max(currentMemory, maxMemory);
	}
	
	protected void incrementNodeCount() {
		nodesExplored++;
	}

}
