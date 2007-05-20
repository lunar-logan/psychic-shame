/**
 * 
 */
package nodomain.applewhat.torrentdemonio.protocol.tracker;

import nodomain.applewhat.torrentdemonio.protocol.PeerInfo;

/**
 * @author Alberto Manzaneque
 *
 */
public interface TrackerEventListener {
	public void peerAddedEvent(PeerInfo peer);

}
