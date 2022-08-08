package martinmazzini.zookeeper.clustermanagment;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class ServiceRegistry implements Watcher {

    public static final String COORDINATORS_REGISTRY_ZNODE = "/coordinators_service_registry";
    private ZooKeeperConnectionHelper zooKeeperHelper;
    private List<String> allServiceAddresses = null;
    private String currentZnode = null;
    private String serviceRegistryZnode = COORDINATORS_REGISTRY_ZNODE;
    AdressService adressService;

    public ServiceRegistry(AdressService adressService, ZooKeeperConnectionHelper zooKeeperHelper) {
        this.adressService = adressService;
        this.zooKeeperHelper = zooKeeperHelper;
    }

    public void registerForUpdates() {
        try {
            updateAddresses();
        } catch (KeeperException e) {
        } catch (InterruptedException e) {
        }
    }


    void createServiceRegistryNode() {
        try {
            if (zooKeeperHelper.getZooKeeper().exists(serviceRegistryZnode, false) == null) {
                zooKeeperHelper.getZooKeeper().create(serviceRegistryZnode, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    synchronized List<String> getAllServiceAddresses() throws KeeperException, InterruptedException {
        if (allServiceAddresses == null) {
            updateAddresses();
        }
        return allServiceAddresses;
    }

    public String getCoordinatorAdress() throws KeeperException, InterruptedException {
        return getAllServiceAddresses().isEmpty() ? null : getAllServiceAddresses().get(0);
    }

    private synchronized void updateAddresses() throws KeeperException, InterruptedException {
        List<String> znodes = zooKeeperHelper.getZooKeeper().getChildren(serviceRegistryZnode, this);

        List<String> addresses = new ArrayList<>(znodes.size());

        for (String znode : znodes) {
            //TODO RACE COND
            String serviceFullpath = serviceRegistryZnode + "/" + znode;
            Stat stat = zooKeeperHelper.getZooKeeper().exists(serviceFullpath, false);
            if (stat == null) {
                continue;
            }
            //TODO watch false?
            byte[] addressBytes = zooKeeperHelper.getZooKeeper().getData(serviceFullpath, false, stat);
            String address = new String(addressBytes);
            addresses.add(address);
        }

        this.allServiceAddresses = Collections.unmodifiableList(addresses);
        log.info("Updating addresses in service registry + " + serviceRegistryZnode + ". The cluster addresses are: " + this.allServiceAddresses);
    }

    @Override
    public void process(WatchedEvent event) {
        try {
            log.info("Event triggered in Service Registry " + serviceRegistryZnode + " : " + event.getType());
            updateAddresses();
        } catch (KeeperException e) {
        } catch (InterruptedException e) {
        }
    }
}
