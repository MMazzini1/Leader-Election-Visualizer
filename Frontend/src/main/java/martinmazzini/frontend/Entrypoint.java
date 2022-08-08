package martinmazzini.frontend;

import lombok.extern.slf4j.Slf4j;
import martinmazzini.frontend.clustermanagment.ClusterManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@Slf4j
public class Entrypoint {


    @Autowired
    ClusterManager clusterManager;

    public static void main(String[] args) {
        SpringApplication.run(Entrypoint.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeCluster() throws InterruptedException {

        
        boolean success = false;
        

        while (!success) {
            try {
                log.info("Initializing cluster");
                clusterManager.initialize();
                success = true;
                log.info("Cluster initilized succesfully");
            } catch (Exception e) {
                log.error("Cluster initialization failed, retrying.", e);
                Thread.sleep(1000);
            }
        }

    }


}
