package martinmazzini.cluster.cluster;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AddressService {




    @Value("${server.port}")
    private String port;

    @Value("${service.name}")
    private String serviceName;



    public String getNodeAddress() {
        return String.format(serviceName + ":" + port);
    }

}
