/**
 * 
 */
package nodomain.applewhat.torrentdemonio.protocol.wire;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import nodomain.applewhat.torrentdemonio.protocol.PeerInfo;
import nodomain.applewhat.torrentdemonio.protocol.tracker.TrackerEventListener;

/**
 * @author Alberto Manzaneque
 *
 */
public class WireManager extends Thread implements TrackerEventListener {

	private static Logger logger = Logger.getLogger(WireManager.class.getName());
	
	private List<PeerInfo> remainingPeers;
	private String name;
	
	public WireManager(String name) {
		this.name = name;
		remainingPeers = new Vector<PeerInfo>();
	}
	
	
	public void peerAddedEvent(PeerInfo peer) {
		if(!remainingPeers.contains(peer)) {
			logger.fine("New peer for download"+name+", "+peer);
			remainingPeers.add(peer);
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
	}

}
