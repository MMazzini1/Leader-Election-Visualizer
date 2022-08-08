
## What´s this?

This repository contains an example of a working implementation of **Leader Election** and **Service Registry** algorithms implementations with **ZooKeeper**. In addition, it has a web server serving a front-end that allows for the **visualization of the cluster's state**. The whole application, including a ZooKeeper instance, is dockerized and can be run with a single docker-compose command.

## How to run?

To run the application, download the project and run the following command in the root folder of the project.

*docker-compose up --build*

Next, navigate to http://localhost:8080/index (http://localhost:8080/index)
  and you will be presented with the following screen.

  
  <![endif]-->

## What exactly does the docker-compose.yml file include?

When you start the docker-compose.yml images, the following happens:

 - A web server is stared on port 8080, which serves the (very rudimentary) front-end assets. It only communicates with the leader node of the cluster, for which it uses ZooKeeper to dynamically discover it´s address (which of course would change if the leader node dies, after the reelection process). The source code of the web server is under the “Frontend” folder in the project.
 
- A ZooKeeper instance starts listening on the default ZooKeeper´s port (2181). ZooKeeper is used for Leader Election of the cluster, and also as a Service Registry for Service Discovery of both Leader and Worker nodes.

- Four cluster nodes instances are started. During start-up, one of these instances will be elected as a leader, and the remaining three will be elected as workers. If the leader dies, a reelection process will take place, and a new leader will be elected (see Leader Election process). The nodes will also dynamically add and remove themselves from leader and workers service registries, so that their addresses can be discovered by other services. It´s important to mention that the four nodes share the same source code (under the “Cluster” folder). It´s only the outcome of the Leader Election process (whether the node it´s elected as a Leader or a Worker) that will determine the runtime behavior of the nodes.

The following diagram shows the five containers that start when you run the docker-compose file, with some arrows to show the communication happening between them.

## What does the table show?

 
The table shows the state of the cluster. Each row in the table corresponds to a node in the cluster, and has the following information:

-   **Znode Id:** the corresponding znode assigned by ZooKeeper during the Leader Election process (see **What is ZooKeeper** and **Leader Election** sections).
-   **Type:** whether the node it´s a Leader or Worker.
-   **Watching znode:** the predecessor node (see Leader Election section) which is being watched for updates, by the row´s node.
-   **Address:** it´s the address being used to communicate with this node in the docker-compose network.
-   **Kill instance:** it´s just a button to terminate the node. It executes a System.exit() inside the java process of the container. It´s an easier alternative to manually stopping the container.

## How to see the Leader Election process in action?



Just press the “Kill instance” button in one of the row´s (or stop one of the containers using the docker CLI or Docker Desktop). The behavior will differ depending on the node´s type. For deeper understanding see the Leader Election section.

**Important note**: the leader election takes some time (less than a minute) to happen. This time is the sum of the time that the Java process takes to exit, plus the time that the docker container takes to stop, plus the ZooKeeper session timeout threshold, plus the time of the Leader re-election process. It´s faster if you stop the containers manually with the docker CLI.

### Stopping a Worker Node

In this case the node that was listening for updates on this node, will start to listen to this node’s predecessor, closing the “gap” that would remain otherwise. The following images show this process taking place when node listening on port 8084 is killed.


State of cluster before stopping node listening on 8084.

Node listening on 8084 disappears of the table. The “Watching Znode” field of node listening on port 8083 is still outdated.

After a couple of seconds, when leader reelection ends, the “Watching Znode” field of node listening on port 8083 it´s updated to point to c_00000000000, closing the gap.

### Stopping a Leader Node 

State of cluster before stopping leader node.

When leader node is down, the front-end can no longer obtain the state of the cluster, and some error message is shown by the fron-end.

After Leader Election takes place, some other node (in this case, the one on port 8083), the following node in the znode´s succession takes it´s place and can start serving requests again.

<![endif]-->

## What is ZooKeeper?

It´s a service designed for coordination in distributed system, providing an abstraction layer for higher level distributed algorithms. It´s in itself a distributed system (typically running in an cluster of an odd number of nodes) for high availability, although for this project it´s just a single instance runninng.

<![endif]-->

### Data Model

The data model that ZooKeeper offers it´s similar to a file system. You can define znodes, and each of these can store both data and children znodes. Although it looks like a filesystem, the data it´s only stored in memory, not in disk.

Source: https://zookeeper.apache.org/doc/current/zookeeperOver.html

It´s also important to mention the two types of nodes available, ephemeral and persistent. The ephemereal type is deleted when a session ends (when the client is disconnected from ZooKeeper), and the persistent type persists between sessions. Both types are used in this example. You can also define nodes to be sequential, in which case they are assigned an strictly increasing number when they are created. This is important for the leader election process.

## How is the Leader Election algorithm implemented with ZooKeeper?

## How is the Service Registry implemented with ZooKeeper?
