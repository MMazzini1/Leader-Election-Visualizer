package martinmazzini.zookeeper.management;

import martinmazzini.zookeeper.model.NodeStatus;
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
    AdressService adressService;


    public void initializeCluster() throws IOException, InterruptedException, KeeperException {
        zookeperConnectionHelper.connectToZookeeper();
        workerServiceRegistry.createServiceRegistryNode();
        coordinatorServiceRegistry.createServiceRegistryNode();
        leaderElection.createElectionNode();
        leaderElection.volunteerForLeadership();
        leaderElection.reelectLeader();
    }


    public  NodeStatus getNodeStatus(){
        NodeStatus nodeStatus = new NodeStatus(leaderElection.getCurrentZnodeName(),
                leaderElection.getFollowingZnodeName(),
                adressService.getNodeAdress(),
                leaderElection.getStatus());
        return nodeStatus;
    }


    public List<String> getWorkerAdressess() throws InterruptedException, KeeperException {
        List<String> workerAdressess = workerServiceRegistry.getAllServiceAddresses();
        return workerAdressess;

    }

    public String getAdress() {
        return adressService.getNodeAdress();
    }


    public boolean isLeader() {
        return getNodeStatus().getClusterStatus().equals("Leader");
    }
}
