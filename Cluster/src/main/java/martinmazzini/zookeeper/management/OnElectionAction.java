package martinmazzini.zookeeper.management;


import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;




@Component
public class OnElectionAction implements OnElectionCallback {
    private final ServiceRegistry workersServiceRegistry;
    private final ServiceRegistry coordinatorsServiceRegistry;
    private final AdressService adressService;


    public OnElectionAction(  @Qualifier("workerServiceRegistry") ServiceRegistry workersServiceRegistry,
                                @Qualifier("coordinatorServiceRegistry") ServiceRegistry coordinatorsServiceRegistry,
                            AdressService adressService) {
        this.workersServiceRegistry = workersServiceRegistry;
        this.coordinatorsServiceRegistry = coordinatorsServiceRegistry;
        this.adressService = adressService;
    }

    public void initializeRegistries(){
        workersServiceRegistry.createServiceRegistryNode();;
        coordinatorsServiceRegistry.createServiceRegistryNode();;
    }

    @Override
    public void onElectedToBeLeader() {
        System.out.println("I am the leader");
        workersServiceRegistry.unregisterFromCluster();
        workersServiceRegistry.registerForUpdates();

        try {
            String currentServerAddress = adressService.getNodeAdress();
            coordinatorsServiceRegistry.registerToCluster(currentServerAddress);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
            return;
        }
    }



    @Override
    public void onWorker() {
        try {
            String currentServerAddress = adressService.getNodeAdress();
            workersServiceRegistry.registerToCluster(currentServerAddress);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
            return;
        }
    }
}
