/**
 * 
 */
package nodomain.applewhat.torrentdemonio.protocol;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import nodomain.applewhat.torrentdemonio.metafile.TorrentMetadata;
import nodomain.applewhat.torrentdemonio.protocol.tracker.TrackerEventListener;
import nodomain.applewhat.torrentdemonio.protocol.tracker.TrackerManager;
import nodomain.applewhat.torrentdemonio.util.TempDirStructure;

/**
 * @author Alberto Manzaneque
 *
 */
public class TorrentDownloadManager {
	
	private static Logger logger = Logger.getLogger(TorrentDownloadManager.class.getName());
	
	private TorrentMetadata metadata;
	private TrackerManager tracker;
	private DownloadProcess process;
	private boolean start, stop, destroy;
	private State state;
	private List<PeerInfo> remainingPeers;
	private TempDirStructure files;
	
	private enum State { INITIALIZED, STARTED, STOPPED, DESTROYED };
	
	public TorrentDownloadManager(TorrentMetadata metadata, TempDirStructure files) throws MalformedURLException {
		this.metadata = metadata;
		this.files = files;
		tracker = new TrackerManager(metadata);
		remainingPeers = new Vector<PeerInfo>();
		tracker.addTrackerEventListener(new PeerAdder());
		process = new DownloadProcess();
		start = false;
		stop = false;
		destroy = false;
		state = State.INITIALIZED;
		new Thread(process).start();
	}
	
	private class DownloadProcess implements Runnable {
		public void run() {
			tracker.start();
			// state machine
			try {
				while (state != State.DESTROYED) {
					switch(state) {
					case INITIALIZED:
						logger.fine("Torrent "+metadata.getName()+" initialized");
						synchronized (this) {
							if(!start) wait();
							if(start) {
								state = State.STARTED;
								start = false;
								logger.fine("Torrent "+metadata.getName()+" started");
							}
						}
						break;
					case STOPPED:
						synchronized (this) {
							if(!start && !destroy) wait();
							if(start) {
								state = State.STARTED;
								start = false;
								logger.fine("Torrent "+metadata.getName()+" started");
							}
							if (destroy) {
								state = State.DESTROYED;
								destroy = false;
								logger.fine("Torrent "+metadata.getName()+" destroyed");
							}
						}
						break;
					case STARTED:
						
						break;
					case DESTROYED:
						break;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}		
	}
	
	/* (non-Javadoc)
	 * @see nodomain.applewhat.torrentdemonio.protocol.PausableThread#start()
	 */
	public void start() {
		synchronized (process) {
			start = true;
			process.notify();
		}
	}
	
	/* (non-Javadoc)
	 * @see nodomain.applewhat.torrentdemonio.protocol.PausableThread#stop()
	 */
	public void stop() {
		synchronized (process) {
			stop = true;
			process.notify();
		}
	}
	
	/* (non-Javadoc)
	 * @see nodomain.applewhat.torrentdemonio.protocol.PausableThread#destroy()
	 */
	public void destroy() {
		synchronized (process) {
			destroy = true;
			process.notify();
		}
	}
	

	
	private class PeerAdder implements TrackerEventListener {
		public void peerAddedEvent(PeerInfo peer) {
			if(!remainingPeers.contains(peer)) {
				logger.fine("New peer for download"+metadata.getName()+", "+peer);
				remainingPeers.add(peer);
			}
		}
	}

}
