package martinmazzini.cluster.cluster;


import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
public class OnElectionAction implements OnElectionCallback {
    private final ServiceRegistry workersServiceRegistry;
    private final ServiceRegistry coordinatorsServiceRegistry;
    private final AddressService addressService;


    public OnElectionAction(  @Qualifier("workerServiceRegistry") ServiceRegistry workersServiceRegistry,
                                @Qualifier("coordinatorServiceRegistry") ServiceRegistry coordinatorsServiceRegistry,
                            AddressService addressService) {
        this.workersServiceRegistry = workersServiceRegistry;
        this.coordinatorsServiceRegistry = coordinatorsServiceRegistry;
        this.addressService = addressService;
    }

    @Override
    public void onElectedToBeLeader() {
        
        workersServiceRegistry.unregisterFromCluster();
        workersServiceRegistry.registerForUpdates();

        try {
            String currentServerAddress = addressService.getNodeAddress();
            coordinatorsServiceRegistry.registerToCluster(currentServerAddress);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
            return;
        }
    }



    @Override
    public void onWorker() {
        try {
            String currentServerAddress = addressService.getNodeAddress();
            workersServiceRegistry.registerToCluster(currentServerAddress);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
            return;
        }
    }
}
