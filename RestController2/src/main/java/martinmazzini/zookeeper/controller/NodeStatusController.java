package martinmazzini.zookeeper.controller;

import martinmazzini.zookeeper.management.ClusterManager;
import martinmazzini.zookeeper.model.NodeStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NodeStatusController {



    @Autowired
    ClusterManager clusterManagmentCoordinator;

    @GetMapping("/health")
    ResponseEntity<String> heatlth() {
        return ResponseEntity.ok("Healthy3");
    }


    @GetMapping("/node/status")
    ResponseEntity<NodeStatus> nodeNumber() {
        return ResponseEntity.ok(clusterManagmentCoordinator.getNodeStatus());
    }


    @GetMapping("/kill")
    void kill() {
        System.exit(0);
    }





}
