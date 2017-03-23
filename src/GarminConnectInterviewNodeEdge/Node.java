package GarminConnectInterviewNodeEdge;
import java.util.ArrayList;
import java.util.HashSet;

/***
 * 
 * @author Chrisyknip
 * A node instance is used to represent either an input pin or a logic gate  
 * @param  name:      Name of the node, eg: i1 for first input pin while g1 for first logic gate 
 *		   inEdges:   A hash set which contains all in-flow edges of this node
 *		   outEdges:  A hash set which contains all out-flow edges of this node
 *		   inBits:    A hash set which contains all input bits
 *		   outBit:    A Boolean value which stands for the operation result of the gate
 *         type:      1:AND gate, 2:OR gate, 3:NOT gate, -1:input pin    
 *         isEndNode  Boolean value used to check if a node has any out-flow
 * @method addEdge(Node):	 	 construct an connecting edge between two nodes, this -> n
 *         addInBit(Node):	 	 add the boolean value of incoming node to this.inBits
 *         setInBit(Boolean):	 this method is designed for input pin node,     
 *         operate():		     perform logic gate operation and save the result in this.outBit
 *         setType(int):		 set node type (1:AND gate, 2:OR gate, 3:NOT gate, -1:input pin)
 */

class Node{
		public final String name;
		public final HashSet<Edge> inEdges;
		public final HashSet<Edge> outEdges;
		public ArrayList<Boolean> inBits;
		public Boolean outBit;
		public int type = -1;
		public Boolean isEndNode = false;
		public Node(String name) {
			this.name = name;
			inEdges = new HashSet<Edge>();
			outEdges = new HashSet<Edge>();
			this.inBits = new ArrayList<Boolean>();
		}
		public void addEdge(Node node){
			Edge e = new Edge(this, node);
			outEdges.add(e);
			node.inEdges.add(e);
		}
		

		public void addInBit(Node n){
			this.inBits.add(new Boolean(n.outBit));
		}
		public void setInBit(Boolean in){
			if(this.inBits.size() == 0)
				this.inBits.add(new Boolean(in));
			else
				this.inBits.set(0, in);
			this.outBit = new Boolean(in);
		}


		public void operate(){
			switch(this.type){
				case 1://AND gate
					this.outBit = new Boolean(!this.inBits.contains(false));
					break;
				case 2://OR gate
					this.outBit = new Boolean(this.inBits.contains(true));
					break;
				case 3://NOT gate
					this.outBit = new Boolean(!this.inBits.get(0));
					break;
				default:
					this.outBit = new Boolean(this.inBits.get(0));
					break;
			}
			
		}
		
		public void setType(int type){this.type = type;}
	}