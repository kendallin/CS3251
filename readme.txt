CS 3251 Project Milestone 3
4/18/18

Michael Goodell - mdgoodell3@gatech.edu
Kendal Lin - klin78@gatech.edu


FILES:
Ringo.java: the basic class that all nodes utilize. This is where all
information about the ringo is stored as well as the any data manipulating
functions go such as any and all rtt computation and storage.
All header compilation happens in here as well.

sender.java: The class designated for sending information to the specified
nodes. This class currently only has the two options designated by the keep
alive texts as well as the rtt vectors being sent.

Listener.java: This class gets called when the ringo is trying to listen to
ports. There are multiple different settings for length of listening before
timeout as well as different functionality directions with input.

Neighbors: designed to be the the holding container for all known information
about other nodes and there reported neighbors.

HeapAlgo.java: This class computes the optimal ring formation of a 2D array.
It takes a 2D array and creates a list of indices. It finds all of the permutations of
that list that start with 0. Then calculates the shortest ring and returns the index of that permutation.
It then returns the permutation that has the shortest ring.
If a negative value is passed through the array to indicate that a ringo has gone offline,
it will recalculate the optimal ring formation without that ringo.

story.txt: This is a short story that is used as the file transfer

We used this algorithm to find all permutations
https://www.geeksforgeeks.org/heaps-algorithm-for-generating-permutations/



COMPILING:
All ringos have a short amount of time before they kick out of the "searching"
phase. In order to circumvent this you must have all ringo's ready to go and the
terminal prompt waiting and prewritten. The Sender node is necessary as all changes
start with that one. Starting with the Forwarders quickly hit enter on all
terminals finishing with the sender.

The sender will ping its poc and then that will chain down and each new ringo will ping its poc
(BUG: currently we don't have any support for ringos that aren't down stream
from anyone initially, a ringo that is not pinged by another will swap into long
term listening and remain idle.)

When a ringo is hit by one of its neighbors it does not re-ping it to save time
it simply grabs its address information and stores it for future use. The default rtt
for this is -1

After a 12 second period, the sender sends its RTT to all of its known neighbors
with its neighbor information and time. This RTT forces the ringo to store the
address information of the node it represents and then pass it on to its neightbors.
If it gets an RTT header from a source it already has it does not restore or retransmit

After another cool down period the forwarders go into long term wait and hold open
their sockets.

To check the optimal ring algorithm:
	determine which case you would like to test (a: without churn or b: with churn)
	$ javac HeapAlgo.java
	$ java HeapAlgo

To check the closest thing to a working algorithm we currently have you need to run on only 2 nodes in order to see data transfer working.
In the working directory type:
	$ javac *.java
	on sender: $ java ringo S [SENDER PORT] [POC NAME] [POC PORT] 2
	on receiver: $ java ringo R [RECEIVER PORT] [SENDER NAME] [SENDER PORT] 2
	

ISSUES:
We do not have accurate RTT information being passed around. Because of this our RTT matrices do not compile at individual nodes. In order to prove the existence of some functionality we have segmented the data sending process and the optimal ring formation. A file is automatically read from and sent by the sender to whoever its POC is. The POC then prints it out, proving correct reception. There is an issue with the second packet of the file getting lost due to the receiver trying to send something back that we could not resolve.


The HeapAlgo.java file has not been implemented in the actual communication between nodes.
We have included test cases to show that it works with and without churn. 
Test case a: calculates the optimal ring without churn and returns the indices [0, 3, 4, 1, 2, 0]
Test case b: calculates the optimal ring when the second ringo goes offline. 
It returns [0, 1, 2, 3, 0], taking the second ringo completely out of the list.