/**
 * 
 */
package nodomain.applewhat.torrentdemonio.protocol.wire;

import nodomain.applewhat.torrentdemonio.protocol.PeerInfo;

/**
 * @author Alberto Manzaneque
 *
 */
public class Peer {
	
	private PeerInfo info;
	
	private boolean amInterested, amChoking, peerInterested, peerChoking;
	
	public Peer(PeerInfo info) {
		this.info = info;
		amInterested = false;
		peerInterested = false;
		amChoking = true;
		peerChoking = true;
	}

}
