CS 3251 Project Milestone 2
3/16/18

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

TSP.java:
The TSP takes in an argument of a nested array.
It iterates through the distances by checking the nearest neighbor and
determining which path is the shortest. An issue that we had was creating a
brute force algorithm with recursion. The algorithm that we decided to use, is
using dynamic programming. This prioritizes efficiency over accuracy, so the end
result is not always the optimal. We researched many different options and
attempted to program our own brute force method, but could not find an algorithm that worked.

Had we not ran into this issue, we would have created a recursive function that
calls each path from the first ringo called. Within the function it would
recursively call all paths from each of the paths called in the first function call.
This would eventually lead to the last ringo. In that base case, it would be
called on the path back to the first ringo.After this, the function would recurse
back through all of the paths, every iteration, it would add all of the paths together.
A variable would hold the shortest path, comparing it to each new path,
Once the function recursed all the way back to the starting ringo,
it would return that path.

Below is the source that we based our program on. It was edited to take in the
nested array from our RTT vector instead of using a scanner to take in terminal arguments.

https://www.sanfoundry.com/java-program-solve-travelling-salesman-problem-unweighted-graph/



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

ISSUES:
We ran into a lot of issues with this project and struggled to get support with it.
Our clearest issue is that InetAddress.getByAddress() would only return null when
trying to be placed in the neighbors constructor and store RTT vector information.
This is necessary in order for nodes to know who the RTT came from. This issue
turned into a critical failure we weren't able to figure out.

The amount of time it takes for out nodes to form up is also painfully long.
A lot of this is precautionary but this is not an optimal solution.

Currently our system does not meet the requirments for the project because of
constant issues with java class methods. We will reasess in order to deliver a
satisfactory assignment for the last checkpoint.
