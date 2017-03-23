package GarminConnectInterviewNodeEdge;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;



public class TopologicalSort {
	
	/***
	 * ALGORITHM - Parse String circuit, receive input signals, and return a directed acyclic graph.
	 *  
	 * @param rowInTruthTable
	 * @param circuit: 	eg:
	 * 						3
	 *						3
	 *						1 -1 2 3 0
	 *						3 -2 0
	 *						2 2 -3 0
	 * @return allNodes in DAG
	 */
	public static ArrayList<Node> constructDAG(Boolean[] rowInTruthTable, String circuit){
		//nis: ArrayList for input pins
		ArrayList<Node> nis = new ArrayList<Node>();
		//ngs: ArrayList for logic gates
		ArrayList<Node> ngs = new ArrayList<Node>();
		int numOfGate = 0;
		try{
			String[] docSplit = circuit.split("\n");
			for(int i=0; i < docSplit.length; ++i){		//for each line in circuit doc
				String line = docSplit[i];
				String[] lineSplit = line.split(" ");   //split a line into array of words
				if(i==0){
					//NI should be larger than 0 and no more than 16.
					if(Integer.valueOf(line)<=0 || Integer.valueOf(line)>16)
						return null;
					else{
						//initialize ArrayList<Node> for input pins
						for(int j = 0; j < Integer.valueOf(line); ++j){
							nis.add(new Node("i" + (j+1)));
							nis.get(j).setInBit(rowInTruthTable[j]);
						}
					}
				}else if (i == 1){
					//NG should be larger than 0 and no more than 1000.
					if(Integer.valueOf(line)<=0 || Integer.valueOf(line)>1000)
						return null;
					else{
						//initialize ArrayList<Node> for logic gates
						for(int j = 0; j < Integer.valueOf(line); ++j) 
							ngs.add(new Node("g" + (j+1)));
					}
				}else{
					//set link between logic gate and input pin
					for(int j = 0; j < lineSplit.length-1; ++j){  //for each word in line
						int word = Integer.valueOf(lineSplit[j]);
						//in case that link assignment index exceeds the number of gates or input pins
						if( (-word) > nis.size() || word > ngs.size()){
							System.out.println("in case that link assignment index exceeds the number of gates or input pins");
							return null;
							
						}
						else{
							if(j == 0) //set gate type(1:AND, 2:OR, 3:NOT)
								ngs.get(numOfGate).setType(word);
							else{
								if(word < 0)
									//eg: -1 means nis.get(0) addEdge to ngs.get(numOfGate)
									nis.get(-word-1).addEdge(ngs.get(numOfGate));
								else if(word > 0)
									//eg: 2 means nis.get(1) addEdge to ngs.get(numOfGate)
									ngs.get(word-1).addEdge(ngs.get(numOfGate));
							}
						}
					}//for each word in line
					numOfGate++;
				}
			}//for each line in doc
		}catch(NumberFormatException e){
			return null;
		}catch(IndexOutOfBoundsException e){
			return null;
		}
		//Combine nis and ngs
		ArrayList<Node> allNodes = new ArrayList<Node>(nis);
		allNodes.addAll(ngs);
		return allNodes;
	}
	
	/***
	 * ALGORITHM - Perform topological sort, and return output pins.
	 * step1: Construct a directed acyclic graph (Each node in the graph represents either an input pin or a logic gate, connected with zero to multiple directed edges)	  
	 * step2: Create a set S that contains all nodes without any incoming edges in the graph. 
	 * 		  For each node n in S: 
	 * 			  - Remove n from S. 
	 * 			  - Add n to L. (L will eventually contain all sorted nodes in topological order)
	 * 			  - Remove all out-edges of n, and pass its bit to all nodes pointed by it.
	 *        	  - For each node m originally pointed by n:
	 *        			- If m doesn't have any incoming edges, perform logic operation and then add m into S.   
	 * step3: Check if any node in the graph still has incoming edges.
	 * 		  	  - If yes, this circuit has cycle, and no output can be determined.        
	 * 			  - If no, simply return outputPins.
	 * 
	 * @param  rowInTruthTable: eg: rowIntruthTable=[1,1,0] when there are three input pins 
	 * @param  circuit: 
	 * @param  indx: index of the circuit
	 * @return outputPins: Nodes who have no outEdges, which means the end nodes in the graph. (output pins in the circuit)
	 */
	public static ArrayList<Node> sort(Boolean[] rowInTruthTable, String circuit, int indx) {
	//Step1: construct a directed acyclic graph
		ArrayList<Node> graph = constructDAG(rowInTruthTable, circuit);
		if(graph == null) return null;
		//L: This Empty ArrayList will contain all sorted nodes in topological order,
		ArrayList<Node> L = new ArrayList<Node>();
		//outPutPins: But what we actually need is those nodes marked as inEndNode in L, therefore we extract them from L to outputPins
		ArrayList<Node> outputPins = new ArrayList<Node>();	
		//S: Set of all nodes with no incoming edges
		HashSet<Node> S = new HashSet<Node>();
		
	//Step2: Update S, L by breadth first search
		for(Node n : graph){
			if(n.inEdges.size() == 0)	
				S.add(n);
			//For those who has no outgoing edges, mark them as end nodes
			if(n.outEdges.size() == 0)	
				n.isEndNode = true;
		}
		//while S is non-empty do
		while(!S.isEmpty()){
			//remove a node n from S
			Node n = S.iterator().next();
			S.remove(n);
			//insert n into L
			L.add(n);

			//for each node m with an edge e from n to m do
			for(Iterator<Edge> it = n.outEdges.iterator();it.hasNext();){
				//remove edge e from the graph
				Edge e = it.next();
				Node m = e.to;
				it.remove();					//Remove edge from n
				m.inEdges.remove(e);			//Remove edge from m
				m.addInBit(n);					//add output of n to m
				//if m has no other incoming edges then insert m into S
				if(m.inEdges.isEmpty()){
					m.operate();				//Since m has no incoming edge which means its input won't change any more, we can perform logic operation
					S.add(m);					//Update S by adding nodes without incoming edges to it
				}
			}
		}//end while
		
	//Step3: Check if all edges are removed, if not, there is a cycle
		for(Node n : graph){
			if(!n.inEdges.isEmpty()){
				System.out.println("There is a cycle in the graph!");
				return null; //cycle = true;
			}
		}

		Collections.sort(L, new NameComparator());	//Sort L by name
		for(Node node: L){
			//We only needs gate nodes without outgoing edges in L
			if(node.isEndNode && node.type > 0)
				outputPins.add(node);
		}
		
		return outputPins;		
	}
}
