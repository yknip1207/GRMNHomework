According to the question, only those logic gates without outgoing wire are considered as the output of a circuit.
That is, I can assume that all input pins are connected with at least one logic gate.
If there is any input pin not connecting with any gates, I simply ignore it.
Besides, if there's any format error or wrong linkage, I just indicate error msg in the output file.


input file name as first argument of main method
output file name as second argument of main method
eg:  javac MainSimulator.java
	 java MainSimulator ../testcase ../output