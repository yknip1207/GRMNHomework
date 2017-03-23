package GarminConnectInterviewAdjacencyTable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

public class BreadthFirstOperation {
	/***
	 * ALGORITHM - Perform BFS search, and return the updated graph in form of ArrayList.
	 * step1: Initialize value of each input pins based on the row of truth table  
	 * step2: Create a set S that contains all nodes without any incoming edges in the graph. 
	 * 		  For each node n in S: 
	 * 			  - Remove n from S. 
	 * 			  - Remove all out-edges of n, and pass its bit to all nodes pointed by it.
	 *        	  - For each node m originally pointed by n:
	 *        			- If m doesn't have any incoming edges, perform logic operation and then add m into S.   
	 * step3: Check if any node in the graph still has incoming edges.
	 * 		  	  - If yes, this circuit has cycle, and no output can be determined, return null.        
	 * 			  - If no, simply return the graph.
	 * 
	 * @param  rowInTruthTable: eg: rowIntruthTable=[1,1,0] when there are three input pins 
	 * @param  graph: 			ArrayList<Node> and each node records its input value, output value, and node type
	 * @param  adjacencyTable:  eg: adjacencyTable[0][1] means node 0 points to node 1, and it also updates while BFS 
	 * @param  indx: 			index of the circuit
	 * @return graph after breadth first operation
	 */
	public static ArrayList<Node> operation(Boolean[] rowInTruthTable, ArrayList<Node> graph, Integer[][] adjacencyTable, int numOfInput) {
	//Step1: Initialize value of each input pins in the graph
		for(int i = 0; i < numOfInput; ++i)
			graph.get(i).setInBit(rowInTruthTable[i]);
	//Step2: Update S by breadth first search
		//S: Set of all nodes with no incoming edges
		HashSet<Node> S = new HashSet<Node>();
		for(int i=0; i < graph.size(); ++i){
			Node n = graph.get(i);
			if(inEdgeSize(n, adjacencyTable) == 0)	//Add node who has no incoming edge into S	
				S.add(n);
			if(outEdgeSize(n, adjacencyTable) == 0)	//For those who has no outgoing edges, mark them as end nodes	
				n.isEndNode = true;
		}
		//while S is non-empty do
		while(!S.isEmpty()){
			Node n = S.iterator().next();	//remove a node n from S
			S.remove(n);
			for(int toInd = numOfInput; toInd < adjacencyTable[n.tableIndex].length; ++toInd){	//for each node m pointed by node n do
				int fromInd = n.tableIndex;
				if(toInd == fromInd) continue;			
				if(adjacencyTable[fromInd][toInd] == 1){
					Node m = graph.get(toInd);
					adjacencyTable[fromInd][toInd] = 0;			//remove edge from n to m
					m.addInBit(n);								//add the n's output to m
					if(inEdgeIsEmpty(m, adjacencyTable)){		//put m into S if m has no incoming edge
						m.operate();							//Since m has no incoming edge now, we can make logic operation on it
						S.add(m);
					}
				}		
			}//end for each node m pointed by n
		}//end while in S	
	//Step3: Check if all edges are removed, if not, there is a cycle
		for(Node n : graph){
			if(!inEdgeIsEmpty(n, adjacencyTable)){
				System.out.println("There is a cycle in the graph!");
				return null; //cycle = true;
			}
		}
		return graph;		
	}

	
	/***
	 * Lookup table to check if node n has any incoming links
	 * @param n
	 * @param table
	 * @return has or hasn't
	 */
	public static boolean inEdgeIsEmpty(Node n, Integer[][] table){
		int tableIndex = n.tableIndex;
		for(int i = 0; i < table[0].length; ++i){
			if(table[i][tableIndex] == 1)
				return false;
		}
		return true;
	}
	/***
	 * Lookup table to check if how many incoming links node n has
	 * @param n
	 * @param table
	 * @return # of incoming edges of node n
	 */
	public static int inEdgeSize(Node n, Integer[][] table){
		int tableIndex = n.tableIndex;
		int result = 0;
		for(int i = 0; i < table[0].length; ++i){
			if(table[i][tableIndex] == 1) result++;
		}
		return result;
	}
	/***
	 * Lookup table to check if how many outgoing links node n has
	 * @param node
	 * @param table
	 * @return # of outgoing edges
	 */
	public static int outEdgeSize(Node n, Integer[][] table){
		int tableIndex = n.tableIndex;
		int result = 0;
		for(int i = 0; i < table[0].length; ++i){
			if(table[tableIndex][i] == 1) result++;
		}
		return result;
	}

}
