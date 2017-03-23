package GarminConnectInterviewAdjacencyTable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/***
 * 
 * @author Chrisyknip
 * A node instance is used to represent either an input pin or a logic gate  
 * @param  name:      Name of the node, eg: i1 for first input pin while g1 for first logic gate 
 *		   inBits:    A hash set which contains all kinds of input bits
 *		   outBit:    A Boolean value which stands for the operation result of the gate
 *         type:      1:AND gate, 2:OR gate, 3:NOT gate, -1:input pin    
 *         isEndNode  Boolean value used to check if a node has any out-flow
 * @method 
 *         addInBit(Node):	 	 add the boolean value of incoming node to this.inBits
 *         setInBit(Boolean):	 this method is designed for input pin node,     
 *         operate():		     perform logic gate operation and save the result in this.outBit
 *         setType(int):		 set node type (1:AND gate, 2:OR gate, 3:NOT gate, -1:input pin)
 *         clear():				 Reset inBits and outBit
 */

class Node{
		public final String name;
		public HashSet<Boolean> inBits;
		public Boolean outBit;
		public int type = -1;
		public Boolean isEndNode = false;
		public int tableIndex;
		public Node(String name, int tableIndex) {
			this.name = name;
			this.tableIndex = tableIndex;
			this.inBits = new HashSet<Boolean>();
		}
		
		/***
		 * Add node n's output bit into this.inData hashset 
		 * @param n
		 */
		public void addInBit(Node n){
			//if mistakenly input more than two wires to NOT gate or assign NI for more than two times
			//the later value will override the former one
			if(this.type == 3 || this.type == -1)	
				this.inBits = new HashSet<Boolean>();
			this.inBits.add(new Boolean(n.outBit));
		}
		
		/***
		 * Used to initialize input pin nodes
		 * @param in
		 */
		public void setInBit(Boolean in){	
			this.inBits.add(new Boolean(in));
			this.outBit = new Boolean(in);
		}
		
		/***
		 * Perform AND/OR/NOT operation
		 */
		public void operate(){
			switch(this.type){
				case 1://AND gate
					this.outBit = new Boolean(!this.inBits.contains(false));
					break;
				case 2://OR gate
					this.outBit = new Boolean(this.inBits.contains(true));
					break;
				case 3://NOT gate
					if(this.inBits.iterator().hasNext())
						this.outBit = new Boolean(!this.inBits.iterator().next());
					break;
				default:
					if(this.inBits.iterator().hasNext())
						this.outBit = new Boolean(this.inBits.iterator().next());
					break;
			}
			
		}
		/**
		 * type: 1=AND, 2=OR, 3=NOT, -1=input pin
		 * @param type
		 */
		public void setType(int type){this.type = type;}

		public void clear(){
			this.inBits.clear();
			this.outBit = null;
		}
	}