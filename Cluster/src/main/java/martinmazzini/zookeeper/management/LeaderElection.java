package martinmazzini.zookeeper.management;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
@Component
public class LeaderElection implements Watcher {
    private static final String ELECTION_NAMESPACE = "/election";
    private String currentZnodeName;
    private ZooKeeperConnectionHelper zooKeeperHelper;
    private OnElectionCallback onElectionCallback;
    private String electionZnode = "/election";
    private String status;
    private String followingZnodeName;

    public LeaderElection(ZooKeeperConnectionHelper zooKeeperHelper, OnElectionCallback onElectionCallback) {
        this.zooKeeperHelper = zooKeeperHelper;
        this.onElectionCallback = onElectionCallback;
    }


    public void volunteerForLeadership() throws KeeperException, InterruptedException {
        String znodePrefix = ELECTION_NAMESPACE + "/c_";
        String znodeFullPath = zooKeeperHelper.getZooKeeper().create(znodePrefix,
                new byte[]{},
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL);

        System.out.println("znode name " + znodeFullPath);
        this.currentZnodeName = znodeFullPath.replace(ELECTION_NAMESPACE + "/", "");
    }


    public void createElectionNode() throws KeeperException {
        try {
            if (zooKeeperHelper.getZooKeeper().exists(electionZnode, false) == null) {
                zooKeeperHelper.getZooKeeper().create(electionZnode, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }


    public void reelectLeader() throws KeeperException, InterruptedException {
        Stat predecessorStat = null;
        String predecessorZnodeName = "";
        while (predecessorStat == null) {
            List<String> children = zooKeeperHelper.getZooKeeper().getChildren(ELECTION_NAMESPACE, false);

            Collections.sort(children);
            String smallestChild = children.get(0);

            if (smallestChild.equals(currentZnodeName)) {
                System.out.println("I am the leader");
                status = "Leader";
                followingZnodeName = predecessorZnodeName;
                onElectionCallback.onElectedToBeLeader();
                return;
            } else {
                System.out.println("I am not the leader");
                int predecessorIndex = Collections.binarySearch(children, currentZnodeName) - 1;
                predecessorZnodeName = children.get(predecessorIndex);
                status = "Worker";
                followingZnodeName = predecessorZnodeName;
                predecessorStat = zooKeeperHelper.getZooKeeper().exists(ELECTION_NAMESPACE + "/" + predecessorZnodeName, this);
            }
        }
        onElectionCallback.onWorker();
        System.out.println("Watching znode " + predecessorZnodeName);
        System.out.println();
    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case NodeDeleted:
                try {
                    reelectLeader();
                } catch (InterruptedException e) {
                } catch (KeeperException e) {
                }
        }
    }


    public String getCurrentZnodeName() {
        return currentZnodeName;
    }



    public String getStatus() {
        return status;
    }

    public String getFollowingZnodeName() {
        return followingZnodeName;
    }

}

