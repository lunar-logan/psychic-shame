/**
 * 
 */
package nodomain.applewhat.torrentdemonio.protocol;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import nodomain.applewhat.torrentdemonio.metafile.TorrentMetadata;
import nodomain.applewhat.torrentdemonio.protocol.tracker.TrackerEventListener;
import nodomain.applewhat.torrentdemonio.protocol.tracker.TrackerManager;

/**
 * @author Alberto Manzaneque
 *
 */
public class TorrentDownloadManager extends Thread implements TrackerEventListener {
	
	private TorrentMetadata metadata;
	private List<Peer> peers;
	private TrackerManager tracker;
	
	public TorrentDownloadManager(TorrentMetadata metadata) throws MalformedURLException {
		this.metadata = metadata;
		peers = new Vector<Peer>();
		tracker = new TrackerManager(metadata);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
	}

	public void peerAddedEvent(Peer peer) {
		peers.add(peer);
		
	}

	public void peerLostEvent(Peer peer) {
		peers.remove(peer);
	}

}
