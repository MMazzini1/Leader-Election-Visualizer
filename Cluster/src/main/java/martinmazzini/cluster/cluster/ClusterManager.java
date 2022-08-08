package martinmazzini.cluster.cluster;

import martinmazzini.cluster.model.NodeStatus;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class ClusterManager {

    @Autowired
    ZooKeeperConnectionHelper zookeperConnectionHelper;

    @Autowired
    LeaderElection leaderElection;

    @Autowired
    @Qualifier("workerServiceRegistry")
    ServiceRegistry workerServiceRegistry;

    @Autowired
    @Qualifier("coordinatorServiceRegistry")
    ServiceRegistry coordinatorServiceRegistry;


    @Autowired
    AddressService addressService;


    public void initializeCluster() throws IOException, InterruptedException, KeeperException {
        zookeperConnectionHelper.connectToZookeeper();
        workerServiceRegistry.createServiceRegistryNode();
        coordinatorServiceRegistry.createServiceRegistryNode();
        leaderElection.createElectionNode();
        leaderElection.volunteerForLeadership();
        leaderElection.reelectLeader();
    }


    public NodeStatus getNodeStatus(){
        NodeStatus nodeStatus = new NodeStatus(leaderElection.getCurrentZnodeName(),
                leaderElection.getFollowingZnodeName(),
                addressService.getNodeAddress(),
                leaderElection.getStatus());
        return nodeStatus;
    }


    public List<String> getWorkerAddressess() throws InterruptedException, KeeperException {
        List<String> workerAddressess = workerServiceRegistry.getAllServiceAddresses();
        return workerAddressess;

    }

    public String getAddress() {
        return addressService.getNodeAddress();
    }


    public boolean isLeader() {
        return getNodeStatus().getClusterStatus().equals("Leader");
    }
}
