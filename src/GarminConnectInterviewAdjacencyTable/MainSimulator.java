package GarminConnectInterviewAdjacencyTable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
/***
 * According to the question, only those logic gates without outgoing wire are considered as the output of a circuit.
 * That is, I can assume that all input pins are connected with at least one logic gate.
 * If there is any input pin not connecting with any gates, I simply ignore it, which means even if an input pin doesn't connected with any gate, it won't be treated as an output pin after operation.
 * Besides, if there's any format error or wrong linkage, I just indicate error msg in the output file.
 * In terms of data structure, I tried two versions. 
 * In the first version, graph is implemented by using Node class and Edge class, and Edge instances might be delete during BSO.
 * But the states of these bunch of instances need to be reset before another input combination comes in, which might take so much time.
 * Instead, I used adjacency matrix which runs faster than the former version but there's still way to improve performance.
 */
public class MainSimulator {
	/***
     * ALGORITHM
     * Step1:  - Read input file
     * Step2:  - For each circuit:
     * 				- Construct a two-dimensional adjacency table in size of (# of gates and input pins) x (# of gates and input pins)
     * 				  eg: adjacencyTable[n][m] = 1, which means that node n points to node m 
     * 				- Construct a directed acyclic graph (Each node in the graph represents either an input pin or a logic gate, connected with zero to multiple directed edges)
     * 				- For each combination(row) of truth value:
     * 					Perform BFS to make logic operation
     * Step3:  - write file 					
	 * @param args[0]: input file name from cmd
	 * 		  args[1]: output file name from cmd
	 */
	
	public static void main(String[] args){
//Step1: read file and each circuit is a string element in arrayList，
			ArrayList<String> circuits = new ArrayList<String>();		//ArrayList containing many circuits in form of string
			circuits = readFile(args[0]);
	
//Step2: For each circuit in ArrayList, create a truth table and a directed acyclic graph, then perform breadth first operation
			String content = "";
			for(int circuitIndx = 0; circuitIndx < circuits.size(); ++circuitIndx){
				String title = "Circuit "  + (circuitIndx+1) + ": ";				//For file writing
				String body = "";										//For file writing
				int numOfInputPin = 0, numOfOutput = 0;					//numOfInput = # of input pins, numOfOutput = # of output pins	
				Boolean[][] truthTable;
				
				try{
					numOfInputPin = Integer.valueOf(circuits.get(circuitIndx).split("\n")[0]);
					truthTable = createTruthTable(numOfInputPin);			//create truth table
					ArrayList<Object> DAGandTable = constructDAGandAdjacencyTable(circuits.get(circuitIndx));	//construct DAG and adjacencyTable
					ArrayList<Node> graph = (ArrayList<Node>)DAGandTable.get(0);
					Integer[][] adjacencyTable =(Integer[][])DAGandTable.get(1);	
					for(Boolean[] row: truthTable){//for each row in truth table, perform BFO
						Integer[][] adjacencyTableCopy = deepCopyAdjacencyTable(adjacencyTable);
						for(Boolean bit: row)
							body += ((bit==false ? "0" : "1") + " ");
						graph = BreadthFirstOperation.operation(row, graph, adjacencyTableCopy, numOfInputPin);	//nullPointerException happens if: 1. file format error, 2. wrong linkage assignment, 3. against size restriction
						ArrayList<Node> outputPins = new ArrayList<Node>();	//ArrayList only containing output pins of a circuit, and it updates w.r.t row
						for(Node n: graph){
							if(n.isEndNode && n.type > 0)
								outputPins.add(n);
						}	
						numOfOutput = outputPins.size();	//output string process	
						body += "|";
						for(Node node: outputPins)
							body += " " + (node.outBit==true ? 1 : 0);	
						body += "\n";
						
						for(Node n: graph)
							n.clear();
					}//end each row in truth table
					title += numOfOutput + " output pin" + (numOfOutput>1 ? "s" : "") + "\nTruth table:\n";	//output string process(combine title, heads, and body)
					String head1 = "", head2 = "", head3 = "";
					for(int tmp = 0; tmp < numOfInputPin; ++tmp){
						head1 += "i ";
						head2 += (tmp+1) +" ";
						head3 += "--";
					}
					head1 += "|";
					head2 += "|";
					head3 += "+";
					for(int tmp = 0; tmp < numOfOutput; ++tmp){
						head1 += " o";
						head2 += " " + (tmp+1);
						head3 += "--";
					}
					title += head1 +"\n" + head2 + "\n" + head3 + "\n" + body +"\n";
					content += title;					
				}catch(NumberFormatException e){
					System.out.println("Circuit " + (circuitIndx+1) + " error: Wrong input file format in Circuit " + (circuitIndx+1) + "!");
					content += "Circuit " + (circuitIndx+1) + " error: Wrong input file format!\n\n";
				}catch(NullPointerException e){
					System.out.println("Circuit " + (circuitIndx+1) + " error: There might be wrong linkage assignment or against size restriction in Circuit " + (circuitIndx+1) + "!");
					content += "Circuit " + (circuitIndx+1) + " error: There might be wrong linkage assignment or against size restriction!\n\n";
				}	
			}//for each circuit in circuits
//step3: write output of each circuit into file			
		writeFile(args[1], content);

	}
	
	/***
	 * Return a deep copy of adjacencyTable
	 * @param t
	 * @return
	 */
	public static Integer[][] deepCopyAdjacencyTable(Integer[][] t){
		Integer[][] copy = new Integer[t.length][t[0].length];
		for(int i = 0; i < t.length; ++i){
			copy[i] = new Integer[t[0].length];
			for(int j = 0; j < t[i].length; ++j)
				copy[i][j] = new Integer(t[i][j]);
		}
		return copy;
	}
	
	/***
	 * ALGORITHM - Parse String circuit line by line, word by word, and return a directed acyclic graph and an adjacency table.
	 * @param circuit: 	eg:
	 * 						3
	 *						3
	 *						1 -1 2 3 0
	 *						3 -2 0
	 *						2 2 -3 0
	 * @return result.get(0) is a DAG in form of ArrayList<Node>
	 * 		   result.get(1) is an adjacency table in form of Integer[][]
	 */
	public static ArrayList<Object> constructDAGandAdjacencyTable(String circuit){
		
		ArrayList<Node> nis = new ArrayList<Node>();	//nis: ArrayList for all input pins
		ArrayList<Node> ngs = new ArrayList<Node>();	//ngs: ArrayList for all logic gates
		Integer[][] adjacencyTable = null;
		int numOfGate = 0;
		try{
			String[] docSplit = circuit.split("\n");
			for(int i=0; i < docSplit.length; ++i){		//for each line in circuit doc
				String line = docSplit[i];
				String[] lineSplit = line.split(" ");   //split a line into array of words
				if(i==0){
					if(Integer.valueOf(line)<=0 || Integer.valueOf(line)>16)	//NI should be larger than 0 and no more than 16.
						return null;
					else{
						for(int j = 0; j < Integer.valueOf(line); ++j)	//initialize ArrayList<Node> for input pins
							nis.add(new Node("x" + (j+1), j));
					}
				}else if (i == 1){
					if(Integer.valueOf(line)<=0 || Integer.valueOf(line)>1000)	//NG should be larger than 0 and no more than 1000.
						return null;
					else{
						for(int j = 0; j < Integer.valueOf(line); ++j)	//initialize ArrayList<Node> for logic gates 
							ngs.add(new Node("y"+(j+1), j+nis.size()));
						int tableSize = nis.size() + ngs.size();
						adjacencyTable = new Integer[tableSize][tableSize];
						for(int x=0; x < adjacencyTable.length; ++x){
							adjacencyTable[x] = new Integer[tableSize];
							for(int y = 0; y < adjacencyTable[x].length; ++y)
								adjacencyTable[x][y] = new Integer(0);
						}
					}
				}else{	//set link between logic gate and input pin		 
					for(int j = 0; j < lineSplit.length-1; ++j){  //for each word in line
						int word = Integer.valueOf(lineSplit[j]);
						if(j == 0){ 	//set gate type(1:AND, 2:OR, 3:NOT)
							if(word < 0 || word > 3)
								return null;
							else
								ngs.get(numOfGate).setType(word);
						}
						else{
							if((-word) > nis.size() || word > ngs.size() || word == 0)	//in case that link assignment index exceeds the number of gates or input pins
								return null;
							else{
								if(word < 0) 			//eg: -1 means nis.get(0) addEdge to ngs.get(numOfGate)
									adjacencyTable[-word-1][nis.size()+numOfGate] = 1;
								else if(word > 0)		//eg: 2 means nis.get(1) addEdge to ngs.get(numOfGate)
									adjacencyTable[word-1+nis.size()][nis.size()+numOfGate] = 1;
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
		
		ArrayList<Node> allNodes = new ArrayList<Node>(nis);	//Combine nis and ngs
		allNodes.addAll(ngs);
		
		ArrayList<Object> result = new ArrayList<Object>();
		result.add(allNodes);
		result.add(adjacencyTable);
		return result;
	}
	
	/***
	 * input number of bits, then return a two-dimensional boolean truth table
	 * @param numOfBit
	 * @return
	 */
	public static Boolean[][] createTruthTable(int numOfBit) {
		int rows = (int) Math.pow(2, numOfBit);
		Boolean[][] truthTable = new Boolean[rows][numOfBit];     
        for (int i=0; i<rows; i++) {
        	truthTable[i] = new Boolean[numOfBit];
            for (int j = numOfBit - 1; j>=0; j--) 
            	truthTable[i][(numOfBit-1)-j] = ((i/(int) Math.pow(2, j)) % 2==1) ? true : false ;
        }     
        return truthTable;
    }

	
	/***
	 * Given a file name, read file, and return a list of circuits in form of string
	 * @param fileName
	 * @return
	 */
	public static ArrayList<String> readFile(String fileName){		
		String doc="", line="";
//step1: read file as a string <- doc
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			while((line = br.readLine()) != null){
					doc += line + "\n";
			}
		}catch (IOException e){System.out.println("File read error!");}		
//step2: split doc into ArrayList<String> where each element represents a circuit
		ArrayList<String> circuits = new ArrayList<String>(); 
		String[] lines = doc.split("\n");
		String circuit = "";
		for(int i = 0; i < lines.length - 1; i++){
			if(lines[i].contains(" ") && !lines[i+1].contains(" ")){
				circuit += lines[i];
				circuits.add(circuit);
				circuit = "";
			}else
				circuit += lines[i] + "\n";
		}
		return circuits;
	}
	
	/***
	 * Given a file name and formatted output string, and then write a output file
	 * @param fileName
	 * @param content
	 */
	
	public static void writeFile(String fileName, String content){
		try{
			FileWriter fr = new FileWriter(fileName, false);
			fr.write(content);
			fr.close();
		}catch(IOException e){System.out.println("File IOException");}
	} 

}
