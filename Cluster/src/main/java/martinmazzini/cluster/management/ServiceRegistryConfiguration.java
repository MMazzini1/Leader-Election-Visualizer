package martinmazzini.cluster.management;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ServiceRegistryConfiguration {



    public static final String WORKERS_REGISTRY_ZNODE = "/workers_service_registry";
    public static final String COORDINATORS_REGISTRY_ZNODE = "/coordinators_service_registry";


    @Bean
    @Qualifier("workerServiceRegistry")
    public ServiceRegistry workerServiceRegistry(ZooKeeperConnectionHelper zooKeeperhelper) {
        ServiceRegistry serviceRegistry = ServiceRegistry.of(zooKeeperhelper, WORKERS_REGISTRY_ZNODE);
        return serviceRegistry;
    }

    @Bean
    @Qualifier("coordinatorServiceRegistry")
    public ServiceRegistry coordinatorServiceRegistry(ZooKeeperConnectionHelper zooKeeperhelper) {
        ServiceRegistry serviceRegistry = ServiceRegistry.of(zooKeeperhelper, COORDINATORS_REGISTRY_ZNODE);
        return serviceRegistry;
    }



}
