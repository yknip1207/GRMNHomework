package GarminConnectInterviewNodeEdge;
import java.util.Comparator;

class NameComparator implements Comparator<Node> {
	    @Override
	    public int compare(Node a, Node b) {
	        return a.name.compareToIgnoreCase(b.name);
	    }
	}