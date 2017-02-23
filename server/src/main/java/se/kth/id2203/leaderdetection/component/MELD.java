package se.kth.id2203.leaderdetection.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.common.port.GroupTopology;
import se.kth.id2203.epfd.event.Restore;
import se.kth.id2203.epfd.event.Suspect;
import se.kth.id2203.epfd.port.EventuallyPerfectFailureDetector;
import se.kth.id2203.leaderdetection.event.Trust;
import se.kth.id2203.leaderdetection.port.MonarchicalEventualLeaderDetection;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by 62maxime on 22/02/2017.
 */
public class MELD extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(MELD.class);

    //******* Ports ******
    private final Negative<MonarchicalEventualLeaderDetection> meld = provides(MonarchicalEventualLeaderDetection.class);
    private final Positive<EventuallyPerfectFailureDetector> epfd = requires(EventuallyPerfectFailureDetector.class);
    private final Positive<Network> net = requires(Network.class);

    //****** Fields ******
    private Set<NetAddress> topology;
    private Set<NetAddress> suspected;
    private NetAddress leader;

    //****** Handler ******
    Handler<Suspect> suspectHandler = new Handler<Suspect>() {
        @Override
        public void handle(Suspect suspect) {
            suspected.add(suspect.getSource());
            changeLeader();
        }
    };

    Handler<Restore> restoreHandler = new Handler<Restore>() {
        @Override
        public void handle(Restore restore) {
            suspected.remove(restore.getSource());
            changeLeader();
        }
    };

    protected final ClassMatchedHandler<GroupTopology, Message> topologyMessageHandler = new ClassMatchedHandler<GroupTopology, Message>() {
        @Override
        public void handle(GroupTopology group, Message message) {
            LOG.debug("Received Topology " + group.getTopology().toString());
            topology.clear();
            topology.addAll(group.getTopology());
            changeLeader();
        }
    };

    private void changeLeader() {
        Set<NetAddress> remainNodes = new HashSet<>(topology);
        remainNodes.removeAll(suspected);
        LOG.debug("remainNodes {} Topology {} Suspected {}", remainNodes, topology, suspected);
        if (remainNodes.isEmpty()) {
            return;
        }
        NetAddress newLeader = Collections.max(remainNodes);
        if (newLeader != leader) {
            LOG.debug("Old Leader {} New Leader {}", leader, newLeader);
            leader = newLeader;
            trigger(new Trust(newLeader), meld);
        }
    }

    public MELD(MELDInit init) {
        this.topology = init.getTopology();
        this.suspected = new HashSet<>();
        this.leader = null;
        changeLeader();
    }

    {
        subscribe(suspectHandler, epfd);
        subscribe(restoreHandler, epfd);
        subscribe(topologyMessageHandler, net);
    }
}
