
## What´s this?

This repository contains an example of a working implementation of **Leader Election** and **Service Registry** algorithms implementations with **ZooKeeper**. In addition, it has a web server serving a front-end that allows for the **visualization of the cluster's state**. The whole application, including a ZooKeeper instance, is dockerized and can be run with a single docker-compose command.

## How to run?

To run the application, download the project and run the following command in the root folder of the project.

*docker-compose up --build*

Next, navigate to http://localhost:8080/index (http://localhost:8080/index)
  and you will be presented with the following screen.

  

## What does the table show?

  

The table shows the state of the cluster. Each row in the table corresponds to a node in the cluster, and has the following information:

-   **Znode Id:** the corresponding znode assigned by ZooKeeper during the Leader Election process (see **What is ZooKeeper** and **Leader Election** sections).
-   **Type:** whether the node it´s a Leader or Worker.
-   **Watching znode:** the predecessor node (see Leader Election section) which is being watched for updates, by the row´s node.
-   **Address:** it´s the address being used to communicate with this node in the docker-compose network.
-   **Kill instance:** it´s just a button to terminate the node. It executes a System.exit() inside the java process of the container. It´s an easier alternative to manually stopping the container.

How to see the Leader Election process in action?

Just press the “Kill instance” button in one of the rows (or stop one of the containers using the docker CLI or Docker Desktop). The expected behavior will differ depending on the node´s type.
