package martinmazzini.frontend.clustermanagment;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ZooKeeperConnectionHelper implements Watcher {
    @Value("${zookeper.host}")
    private String ZOOKEEPER_ADDRESS;
    private static final int SESSION_TIMEOUT = 1000;
    private  ZooKeeper zooKeeper;

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void run() throws InterruptedException {
        synchronized (zooKeeper) {
            zooKeeper.wait();
        }
    }

    public void close() throws InterruptedException {
        this.zooKeeper.close();
    }


    public ZooKeeper connectToZookeeper() throws IOException {
        
        this.zooKeeper = new ZooKeeper(ZOOKEEPER_ADDRESS + ":2181", SESSION_TIMEOUT, this);
        return zooKeeper;
    }



    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    
                } else {
                    synchronized (zooKeeper) {
                        
                        zooKeeper.notifyAll();
                    }
                }
        }
    }
}
