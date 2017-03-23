package GarminConnectInterviewNodeEdge;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MainSimulator {
	/***
	 * According to the question, only those logic gates without outgoing wire are considered as the output of a circuit.
     * That is, I can assume that all input pins are connected with at least one logic gate.
     * If there is any input pin not connecting with any gates, I simply ignore it.
     * Besides, if there's any format error or wrong linkage, I just indicate error msg in the output file.
	 * @param args[0]: input file name
	 * 		  args[1]: output file name
	 */
	
	public static void main(String[] args){
		args[0] = "testcase";
		args[1] = "output1";
		
	//Step1: read file and each circuit is a string element in arrayList，
			ArrayList<String> circuits = new ArrayList<String>();	//ArrayList containing many circuits in form of string
			circuits = readFile(args[0]);
	
	//Step2: For each circuit in ArrayList, create a truth table
			String content = "";
			for(int i = 0; i < circuits.size(); ++i){
				String title = "Circuit "  + (i+1) + ": ";				//For file writing
				String body = "";							//For file writing
				int numOfInput = 0, numOfOutput = 0;					//numOfInput = # of input pins, numOfOutput = # of output pins
				ArrayList<Node> outputPins = new ArrayList<Node>();		//ArrayList containing output pins of a circuit
				Boolean[][] truthTable;
				
				try{
					numOfInput = Integer.valueOf(circuits.get(i).split("\n")[0]);
					truthTable = createTruthTable(numOfInput);
					for(Boolean[] row: truthTable){
						for(Boolean bit: row)
							body += ((bit==false ? "0" : "1") + " ");
						outputPins = TopologicalSort.sort(row, circuits.get(i), i);
						//nullPointerException happens if: 1. file format error, 2. wrong linkage assignment, 3. against size restriction 
						numOfOutput = outputPins.size();	
						body += "|";
						for(Node node: outputPins)
							body += " " + (node.outBit==true ? 1 : 0);
						body += "\n";
					}
					
					//output string process(combine title, heads, and body)
					title += numOfOutput + " output pin" + (numOfOutput>1 ? "s" : "") + "\nTruth table:\n";
					String head1 = "", head2 = "", head3 = "";
					for(int tmp = 0; tmp < numOfInput; ++tmp){
						head1 += "i ";
						head2 += (tmp+1) +" ";
						head3 += "--";
					}
					head1 += "|";
					head2 += "|";
					head3 += "+";
					for(int tmp = 0; tmp < numOfOutput; tmp++){
						head1 += " o";
						head2 += " " + (tmp+1);
						head3 += "--";
					}
					title += head1 +"\n" + head2 + "\n" + head3 + "\n" + body +"\n";
					content += title;	
						
				}catch(NumberFormatException e){
					System.out.println("Circuit " + (i+1) + " error: Wrong input file format in Circuit " + (i+1) + "!");
					content += "Circuit " + (i+1) + " error: Wrong input file format!\n\n";
				}catch(NullPointerException e){
					System.out.println("Circuit " + (i+1) + " error: There might be wrong linkage assignment or against size restriction in Circuit " + (i+1) + "!");
					content += "Circuit " + (i+1) + " error: There might be wrong linkage assignment or against size restriction!\n\n";
				}
	
			}//for each circuit in circuits
	//step3: write output of each circuit into file			
		writeFile(args[1], content);

	}
	
	
	
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

	public static ArrayList<String> readFile(String fileName){		
		String doc="", line="";
		//step1: read file as a string <- doc
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			while((line = br.readLine()) != null)
				doc += line + "\n";
		}catch (IOException e ) {
			e. printStackTrace();
		}
		
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
	
	public static void writeFile(String fileName, String content){
		try{
			FileWriter fr = new FileWriter(fileName, false);
			fr.write(content);
			fr.close();
		}catch(IOException e){System.out.println("File IOException");}
	} 

}
