1. Please compile before running it.
2. According to the question, only those logic gates without outgoing wire are considered as the output of a circuit.
  That is, I can assume that all input pins are connected with at least one logic gate.
  If there is any input pin not connecting with any gates, I simply ignore it, which means even if an input pin doesn't connected with any gate, it won't be treated as an output pin after operation.
  Besides, if there's any format error or wrong linkage, I just indicate error msg in the output file.
  In terms of data structure, I tried two versions. 
  In the first version, graph is implemented by using Node class and Edge class, and Edge instances might be delete during BSO.
  But the states of these bunch of instances need to be reset before another input combination comes in, which might take so much time.
  Instead, I used adjacency matrix which runs faster than the former version but there's still way to improve performance.

Input file name as first argument of main method
output file name as second argument of main method
eg:  javac MainSimulator.java
	 java MainSimulator ../testcase ../output