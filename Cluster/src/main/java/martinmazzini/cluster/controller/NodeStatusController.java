package martinmazzini.cluster.controller;

import martinmazzini.cluster.cluster.ClusterManager;
import martinmazzini.cluster.model.NodeStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NodeStatusController {



    @Autowired
    ClusterManager clusterManager;

    @GetMapping("/health")
    ResponseEntity<String> heatlth() {
        return ResponseEntity.ok("Healthy instance");
    }


    @GetMapping("/node/status")
    ResponseEntity<NodeStatus> nodeStatus() {
        return ResponseEntity.ok(clusterManager.getNodeStatus());
    }


    @GetMapping("/kill")
    void kill() {
        System.exit(0);
    }





}
