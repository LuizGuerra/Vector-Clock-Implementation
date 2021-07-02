# Vector Clock Implementation

This is a distributed vector clock implementation using java language, with multicast and singlecast (UDP) protocols.

To learn more about Vector Clocks, [click here](https://en.wikipedia.org/wiki/Vector_clock).

Documentation
- [Usage](##Usage)
  - [How to execute:](###How to execute:)
  - [Update configuration file](###Update configuration file)
- [Artifacts](##Artifacts)
  - [Output example with 3 processes and its diagram representation](### Output example with 3 processes and its diagram representation)
  - [Interaction Diagram](###Interaction Diagram)


##Usage

###How to execute:

```
  cd src
  javac Node.java
  java Node config.csv <line number>
```

To compile the project you must enter first the /src/ folder. After that, you compile it using javac.

The current example have 3 lines and are all set to run in the same machine (localhost), so you need to say to each terminal who will he learn. It is important to note that you must pass a different line to each on of them.

The code will not start if all lines of the configuration file are running in different terminals.

To run the software in different machines/VMs, please read ["Update configuration file"](### Update configuration file)

###Update configuration file

To run the software in different machines/VMs, the [configuration file](src/config.csv) contains all necessary data for each node. Each line contains the following data:
```
  id host port chance events min_delay max_delay
```
The configuration file contains 2 types of data, the first is how to recognize and reach the process:
- Id is the node identifier;
- Host is the node IP address;
- Port is the computer port that will be accessed to receive messages;

And the second half is project-specific variables:
- Chance is the probability that the node will send a message. Otherwise, he will do a local event;
- For every loop, the process must wait a little bit. Min_delay (milliseconds) is the very minimum it will wait;
- While Max_delay (milliseconds) is the maximum time he will wait.

Consequently, each line represents one node. In the current implementation, it is used 3 lines, so 3 different machines. The network used to allow such message exchanges was NatNetwork.

##Artifacts

###Output example with 3 processes and its diagram representation

![Output table and Diagram Image](/output_example_and_representation.png)

###Interaction Diagram

![Interaction Diagram Image](/interaction-diagram.png)
