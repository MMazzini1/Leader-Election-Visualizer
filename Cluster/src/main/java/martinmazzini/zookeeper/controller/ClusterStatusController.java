package martinmazzini.zookeeper.controller;

import lombok.extern.slf4j.Slf4j;
import martinmazzini.zookeeper.management.ClusterManager;
import martinmazzini.zookeeper.management.ServiceRegistry;
import martinmazzini.zookeeper.model.NodeStatus;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class ClusterStatusController {

    @Autowired
    ClusterManager clusterManagmentCoordinator;



    @PostMapping("/kill")
    ResponseEntity killNode(@RequestParam String address) throws InterruptedException, KeeperException {
        if (!clusterManagmentCoordinator.isLeader()) {
            //only leader supposed to serve this request
            return ResponseEntity.badRequest().build();
        }


        if (clusterManagmentCoordinator.getAdress().equals(address)){
            //kill leader
            System.exit(0);
        }

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://" + address+ "/kill";
        log.info("Killing follower: " + url);
        try{
            return restTemplate.getForEntity(url, NodeStatus.class);
        } catch (Exception e){
            log.info("node killed");
            return ResponseEntity.ok().build();
        }



    }

    @GetMapping("/cluster/status")
    ResponseEntity<List<NodeStatus>> getClusterStatus() throws InterruptedException, KeeperException {

        if (!clusterManagmentCoordinator.isLeader()) {
            //only leader supposed to serve this request
            return ResponseEntity.badRequest().build();
        }

        List<String> workerAdressess = clusterManagmentCoordinator.getWorkerAdressess();

        RestTemplate restTemplate = new RestTemplate();

        List<NodeStatus> clusterStatus = new ArrayList<>();
        for (String adress : workerAdressess) {
            String url = "http://" + adress + "/node/status";
            try {
                ResponseEntity<NodeStatus> response
                        = restTemplate.getForEntity(url, NodeStatus.class);
                clusterStatus.add(response.getBody());
            }catch (Exception e){
                log.error("Call failed for address: " + adress, e);
            }
        }

        clusterStatus.add(clusterManagmentCoordinator.getNodeStatus());

        return ResponseEntity.ok(clusterStatus);
    }


}
