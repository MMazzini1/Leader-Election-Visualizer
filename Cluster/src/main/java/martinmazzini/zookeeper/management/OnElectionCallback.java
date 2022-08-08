

package martinmazzini.zookeeper.management;

public interface OnElectionCallback {

    void onElectedToBeLeader();

    void onWorker();

}
