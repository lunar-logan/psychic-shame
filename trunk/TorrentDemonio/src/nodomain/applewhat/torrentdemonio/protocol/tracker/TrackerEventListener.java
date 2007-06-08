/**
 * 
 */
package nodomain.applewhat.torrentdemonio.protocol.tracker;

import nodomain.applewhat.torrentdemonio.protocol.Peer;

/**
 * @author Alberto Manzaneque
 *
 */
public interface TrackerEventListener {
	public void peerAddedEvent(Peer peer);

}
