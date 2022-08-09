



## What´s this?

This repository contains an example of a working implementation of **Leader Election** and **Service Discovery** implementations with **ZooKeeper**. In addition, it has a web server serving a front-end that allows for the **visualization of the cluster's state**. The whole application, including a ZooKeeper instance, is dockerized and can be run with a single docker-compose command.

## How to run?

To run the application, download the project and run the following command in the root folder of the project.

*docker-compose up --build*

Next, navigate to http://localhost:8080/index
  and you will be presented with the following table on the web browser.  
The succeeding sections offer a quick explanation of **ZooKeeper** service, **Leader Election**, and **Service Discovery** processes. If you would prefer to interact with the cluster and see it in action, you can go straight to **How to see the Leader Election process in action section**.



  
  ![image](https://user-images.githubusercontent.com/25701657/183554630-ea8eac2c-c9fe-4c8d-87cb-6f7370bb5b76.png)




## What exactly does the docker-compose.yml file include?

The below diagram shows a quick visualization of the components included in the docker-compose.yml file, to support the explanation in the following paragraphs.

![image](https://user-images.githubusercontent.com/25701657/183552354-02267f0a-d77e-487f-92a3-2bc95c47f8d1.png)

When you build and run the docker-compose.yml, the following happens. 

 - A web server is started on port 8080 (a Java Spring app, but could be anything), which serves the (very rudimentary) front end of the application. This web server only communicates with the leader node of the cluster. For this purpose, it uses ZooKeeper to dynamically discover the leader's address. If the leader dies, a reelection process will take place and the new leader´s address will be updated in the leader´s service registry. The source code of the web server is under the “Frontend” folder.
 
- A ZooKeeper instance starts listening on the default ZooKeeper´s port (2181). ZooKeeper is used for Leader Election by the cluster's nodes, and also as a Service Registry for Service Discovery of both Leader and Worker nodes (there´s one registry for each type).

- Four cluster node instances are started (also Java Spring apps). During start-up, one of these instances will be elected as a leader, and the remaining three will be elected as workers. If the leader dies, a reelection process will take place, and a new leader will be elected (see Leader Election process). The nodes will also dynamically add and remove themselves from the leader and worker's service registries so that their addresses can be discovered by other services. The four nodes share the same source code (under the “Cluster” folder). It is only the outcome of the Leader Election which determines the runtime behavior of the nodes (either as a Leader or a Worker).

- On startup, the first Leader Election takes place. For this, ZooKeeper's */election* znode is used. Also during this process, the Leader node registers itself in the */coordinators_service_registry* znode, and worker nodes register themselves in the */workers_service_registry* znode. See **ZooKeeper** section for a very basic understanding of how it works.

When the front-end needs to retrieve the state of the cluster, it communicates with the leader node. The arrows in the below diagram show the communication sequence happening between the components for this purpose. The numbers correspond to the relative order of each call. 

![image](https://user-images.githubusercontent.com/25701657/183554469-a06c6e04-f803-4e7a-8fcb-9a0b91a6bc41.png)


## What do the table's rows mean?
 
The table shows the state of the cluster. Each row in the table corresponds to a node in the cluster, and has the following information:

-   **Znode Id:** the corresponding znode assigned by ZooKeeper during the Leader Election process, under the /election znode (see **What is ZooKeeper** and **Leader Election** sections).
-   **Type:** whether the node it´s a Leader or a Worker.
-   **Watching znode:** the predecessor node being watched for updates by the corresponding row´s node (see **Leader Re-election** section)
-   **Address:** the address being used to communicate with this node in the docker-compose network.
-   **Kill instance:** a button to terminate the node. It executes a System.exit() inside the java app of the container. It´s just an easier alternative to manually stopping the container.

## What is ZooKeeper?

It´s a service designed for coordination in distributed systems, providing an abstraction layer for higher-level distributed algorithms. As an example, ZooKeeper it´s used by Kafka for tracking the status of nodes and to determine the leader of each partition (although it's supposedly about to be phased out in favor of internal technology). ZooKeeper is in itself a distributed system (typically running in a cluster of an odd number of nodes) for high availability, but for this project, it´s just a single instance running.


### Data Model

The data model offered by ZooKeeper it´s hierarchical, similar to a file system. You define so-called znodes, and each of these can store both data and child znodes. Although it looks like a filesystem, the data it´s only stored in memory.

![image](https://user-images.githubusercontent.com/25701657/183558180-474d65a9-7033-4e71-b2b9-f6528aa5c93e.png)

Source: https://zookeeper.apache.org/doc/current/zookeeperOver.html

For this project, we will use three znodes that will act as "folders" (meaning, we create other znodes under their path). The below image shows these three znodes when being queried with ZooKeeper's client.

![image](https://user-images.githubusercontent.com/25701657/183562788-89532a50-44ee-46e2-8752-be94a7874ccd.png)


Znodes can be one of two types, ephemeral and persistent. The ephemeral type is deleted when a session ends (when ZooKeeper determines that the client got disconnected). On the contrary, the persistent type can persist in between sessions. You can also define nodes to be sequential, in which case they are automatically assigned a strictly increasing number on creation time. This will be important for the leader election process. 

The other important feature offered by ZooKeeper and used in this project is Watchers. This allows the application to be notified when a change in a znode happens (if its data changes, or if a node is added/deleted under its path). From the (Java) programming perspective, if you want to subscribe to notifications, you need to pass an implementation of the Watcher interface when you call one of the corresponding methods. This implementation will receive a callback when the notification is triggered. These notifications are a one-time trigger, and you have to call the methods again if you want to re-subscribe. In this project, we are only concerned with the following methods:
- getChildren => returns a list of the children under a given path (znode) (optionally pass a Watcher)
- exists => to check if a znode exists  (optionally pass a Watcher)
- getData => Return the data of the znode of the given path  (optionally pass a Watcher)
- 
More info on ZooKeeper´s API: https://zookeeper.apache.org/doc/r3.3.3/api/org/apache/zookeeper/ZooKeeper.html


## How is the Leader Election algorithm implemented with ZooKeeper?

With all that being said, the Leader Election algorithm is pretty simple to understand. On startup, each node (Java process) will create an ephemeral, sequential znode under the */election* znode path of ZooKeeper. Because they are sequential, the created znodes will get assigned a strictly increasing number. The process that created the znode with the smallest sequence number is the leader, and the rest are workers.

In the following example from ZooKeeper's CLI, the process which created znode c_0000000012 it's the one elected as a leader.

![image](https://user-images.githubusercontent.com/25701657/183562914-e0624274-dc66-4fad-acfb-61a2df9acaec.png)

The below diagram shows another hypothetical configuration. The dashed line connects the znodes with the process that created them.

![image](https://user-images.githubusercontent.com/25701657/183563702-da6b15f7-1235-4c70-9c03-067a32345588.png)



## How does re-election happen?

For re-election to happen, all the workers need to watch for failures of the leader, so that one of the workers arises as the new leader in the case of the leader´s failure. To avoid herd effect (meaning, to avoid bombarding ZooKeeper at the same time when the leader fails) a better solution is for each node to just listen to its predecessor's znode. 
In the following diagram, the dashed arrow shows the znode being watched by each process.

![image](https://user-images.githubusercontent.com/25701657/183564016-e677248a-e7fc-465d-a2b9-08460922a85c.png)

When a node gets a notification (because its predecessor's ephemeral node got deleted after the process died) it will just re-run the leader election algorithm. If the node that just died had been the leader, the following configuration would result after re-election.

![image](https://user-images.githubusercontent.com/25701657/183565151-8de7e76c-0cb5-4d1e-bf55-0a967f9149d6.png)

If instead of the leader, node 3 would have failed, then the below configuration would result after the re-election.

![image](https://user-images.githubusercontent.com/25701657/183565833-3121b206-9b47-4e4b-8343-e6ffe9dc9b92.png)


## How is the Service Registry implemented with ZooKeeper?

The service registry is pretty easy to implement. During Leader Election, worker nodes register themselves in the */workers_service_registry*, and the leader node does the same under */coordinators_service_registry*. The newly elected leader also may need to de-register itself from the */workers_service_registry* (in case it was previously a worker and there´s re-election happening). The consumers of either registry have to watch for changes in these paths so that they get updated if a node is changed/removed/added from the registry.


## How to see the Leader Election process in action?


Just press the “Kill instance” button in one of the rows (or stop one of the containers using the docker CLI or Docker Desktop). The behavior will differ depending on the node´s type. 

**Note**: the Leader Election takes some time (less than a minute) to complete. This time is given by the sum of the time that the Java process takes to exit, plus the ZooKeeper session timeout threshold, plus the time of the Leader re-election process.


### If you stop a worker node

In this case, the leader node can still respond to the web server´s query for the state of the cluster. What will happen eventually, after the re-election process, it's just that the "Watching znode" column of the node listening to the deleted znodes trigger will get updated, closing the gap in the chain, in the same way as depicted in the third diagram under the **How does re-election happen? section**.  
The following is a succession of images showing this process when node 8084 is killed.

![image](https://user-images.githubusercontent.com/25701657/183573697-515dc4c3-6b3b-4134-af69-c2e6288de015.png)
The initial state of the cluster

![image](https://user-images.githubusercontent.com/25701657/183573726-6a5e2ad7-6e7c-4bd2-9699-5f405b8853b1.png)
Outdated state seconds after node4 got terminated. Node1 is watching zNode c_00...0014, which doesn´t exist anymore.

![image](https://user-images.githubusercontent.com/25701657/183573987-f093b77c-e6fa-44b6-a26e-0dd90e2837eb.png)
Leader re-lection completed after some more seconds. Node1 has been updated and is watching zNode c_00...0013, its new predecessor.

State of the cluster before stopping node listening on 8084.

Node listening on 8084 disappears from the table. The “Watching Znode” field of node listening on port 8083 is still outdated.

After a couple of seconds, when leader reelection ends, the “Watching Znode” field of node listening on port 8083 it´s updated to point to c_00000000000, closing the gap.

### If you stop a leader node  

When the Leader is down, there´s no one to respond to the front-end query for the state of the cluster. During the failover period (while re-election takes place) the front-end will receive 404 as a response and show a temporary error message. When re-election takes place, the new leader will start serving the requests, and it will show the new cluster configuration with the new leader (the same situation as the third diagram under the How does re-election happen? section).
The following images show this process when node 8084 is killed.

![image](https://user-images.githubusercontent.com/25701657/183574067-8a9a49fa-ece4-4fee-8ad6-77fdc1116bab.png)
The initial state of the cluster

![image](https://user-images.githubusercontent.com/25701657/183574105-fb04d540-b84b-415e-914d-719be230c70b.png)
Leader node down, some error message is shown.

![image](https://user-images.githubusercontent.com/25701657/183574217-187f854f-4fb6-4dbd-9903-742231bd77e6.png)
After Leader Election completes, the following node in the succession has taken the leader's place and starts responding to requests.

### If you add a node

It´s also possible to restart a container after having stopped it (with the Docker CLI / Docker Desktop), in which case the table will show a new row representing the new member of the cluster. It will join as the last node on the succession.


