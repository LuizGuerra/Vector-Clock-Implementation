# Vector Clock Implementation

This is a distributed vector clock implementation using java language, with multicast and singlecast (UDP) protocols.

To learn more about Vector Clocks, [click here](https://en.wikipedia.org/wiki/Vector_clock).

## Table of Contents
<details open>
<summary>Click to expand or hide</summary>

1. [Usage](#usage-anchor)
    1. [How to execute:](#execute-anchor)
    1. [Update configuration file](#config-file-anchor)
1. [Artifacts](#articats-anchor)
    1. [Output example with 3 processes and its diagram representation](#output-anchor)
    1. [Interaction Diagram](#interaction-diagram-anchor)

</details>

<a id="usage-anchor"></a>
## Usage

<a id="execute-anchor"></a>
### How to execute:

```
  cd src
  javac Node.java
  java Node config.csv <line number>
```

To compile the project you must enter first the /src/ folder. After that, you compile it using javac.

The current example have 3 lines and are all set to run in the same machine (localhost), so you need to say to each terminal who will he be. It is important to note that you must pass a different line to each on of them.

The code will not start if all lines of the configuration file are running in different terminals.

To run the software in different machines/VMs, please read ["Update configuration file"](#config-file-anchor).

<a id="config-file-anchor"></a>
### Update configuration file

To run the software in different machines/VMs, the [configuration file](src/config.csv) contains all necessary data for each node. Each line contains the following data:
```
  id host port chance events min_delay max_delay
```
The configuration file contains 2 types of data. The first one is how to recognize (id) and send a message to another process (host and port):
- ```Id``` is the node identifier;
- ```Host``` is the node IP address;
- ```Port``` is the computer port that will be accessed to receive messages;

And the second half is project-specific variables:
- ```Chance``` is the probability that the node will send a message. Otherwise, he will do a local event;
- For every loop, the process must wait a little bit. ```min_delay``` (milliseconds) is the very minimum it will wait;
- While ```max_delay``` (milliseconds) is the maximum time he will wait.

Consequently, each line represents one node. In the current implementation, it is used 3 lines, so 3 different machines. The network used to allow such message exchanges was NatNetwork.

<a id="articats-anchor"></a>
## Artifacts

<a id="output-anchor"></a>
### Output example with 3 processes and its diagram representation

![Output table and Diagram Image](/output_example_and_representation.png)

<a id="interaction-diagram-anchor"></a>
### Interaction Diagram

![Interaction Diagram Image](/interaction-diagram.png)
