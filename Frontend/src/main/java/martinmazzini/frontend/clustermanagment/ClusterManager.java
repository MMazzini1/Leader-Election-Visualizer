package martinmazzini.frontend.clustermanagment;

import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ClusterManager {

    @Autowired
    ZooKeeperConnectionHelper zookeperConnectionHelper;


    @Autowired
    ServiceRegistry coordinatorServiceRegistry;


    @Autowired
    AdressService adressService;


    public void initialize() throws IOException, InterruptedException, KeeperException {
        zookeperConnectionHelper.connectToZookeeper();
        coordinatorServiceRegistry.registerForUpdates();
    }


}
