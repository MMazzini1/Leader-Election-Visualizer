package martinmazzini.frontend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NodeStatus {

    private String leaderElectionZnode;
    private String predecessorLeaderElectionZnode;
    private String address;
    private String clusterStatus;





}
