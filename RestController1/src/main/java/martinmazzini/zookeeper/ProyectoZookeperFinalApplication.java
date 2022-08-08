package martinmazzini.zookeeper;

import lombok.extern.slf4j.Slf4j;
import martinmazzini.zookeeper.clustermanagment.ClusterManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@Slf4j
public class ProyectoZookeperFinalApplication {


    @Autowired
    ClusterManager clusterManagmentCoordinator;

    public static void main(String[] args) {
        SpringApplication.run(ProyectoZookeperFinalApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeCluster() throws InterruptedException {

        System.out.println("hi updated 2");
        boolean success = false;
        System.out.println("lets try connect");

        while (!success) {
            try {
                log.info("Initializing cluster");
                clusterManagmentCoordinator.initialize();
                success = true;
                log.info("Cluster initilized succesfully");
            } catch (Exception e) {
                log.error("Cluster initialization failed, retrying.", e);
                Thread.sleep(1000);
            }
        }

    }


}
