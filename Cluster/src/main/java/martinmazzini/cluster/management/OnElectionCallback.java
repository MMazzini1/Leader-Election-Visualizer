

package martinmazzini.cluster.management;

public interface OnElectionCallback {

    void onElectedToBeLeader();

    void onWorker();

}
