

package martinmazzini.cluster.cluster;

public interface OnElectionCallback {

    void onElectedToBeLeader();

    void onWorker();

}
