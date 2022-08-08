package martinmazzini.frontend.controller;

import lombok.extern.slf4j.Slf4j;
import martinmazzini.frontend.cluster.ServiceRegistry;
import martinmazzini.frontend.model.NodeStatus;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Slf4j
@Controller
public class ClusterSummaryController {


    @Autowired
    ServiceRegistry coordinatorsServiceRegistry;


    @CrossOrigin
    @GetMapping("/cluster/summary")
    ResponseEntity<List<NodeStatus>> getClusterStatus() throws InterruptedException, KeeperException {

        String coordinatorAddress = coordinatorsServiceRegistry.getCoordinatorAddress();
        RestTemplate restTemplate = new RestTemplate();
        List<NodeStatus> clusterStatus = new ArrayList<>();

        String url = "http://" + coordinatorAddress + "/cluster/status";
        try {
            ResponseEntity<List<NodeStatus>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
            clusterStatus.addAll(response.getBody());
        } catch (Exception e) {
            //coordinator node not available or reelection is in progress
            return ResponseEntity.notFound().build();
        }


        Collections.sort(clusterStatus, Comparator.comparing(node -> node.getLeaderElectionZnode()));
        return ResponseEntity.ok(clusterStatus);
    }


    @CrossOrigin
    @PostMapping("/kill")
    public ResponseEntity kill(@RequestParam String address) throws InterruptedException, KeeperException {
        String coordinatorAddress = coordinatorsServiceRegistry.getCoordinatorAddress();

        if (coordinatorAddress.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://" + coordinatorAddress + "/kill?address=" + address;
        log.info("Killing node on: " + url);
        restTemplate.postForEntity(url, HttpMethod.POST, String.class);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/index")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index.html");
        return modelAndView;
    }
}
