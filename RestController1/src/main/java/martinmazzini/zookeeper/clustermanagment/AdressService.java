package martinmazzini.zookeeper.clustermanagment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AdressService {




    @Value("${server.port}")
    private String port;

    @Value("${service.name}")
    private String serviceName;



    public String getNodeAdress() {
        return String.format(serviceName + ":" + port);
    }

}
