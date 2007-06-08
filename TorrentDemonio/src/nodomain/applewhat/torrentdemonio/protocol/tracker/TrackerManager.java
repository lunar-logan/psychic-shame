/**
 * 
 */
package nodomain.applewhat.torrentdemonio.protocol.tracker;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import nodomain.applewhat.torrentdemonio.metafile.TorrentMetadata;
import nodomain.applewhat.torrentdemonio.protocol.Peer;
import nodomain.applewhat.torrentdemonio.util.ConfigManager;

/**
 * @author Alberto Manzaneque
 *
 */
public class TrackerManager extends Thread implements TrackerEventProducer {
	
	public enum State { INITIALIZED, STARTED, STOPPING, STOPPED }; 
	
	private TorrentMetadata metadata;
	private URL url;
	private List<TrackerEventListener> eventListeners;
	private State state;
	private long lastRequest, waitTime;
	private boolean completed;
	
	private static Logger logger = Logger.getLogger(TrackerManager.class.getName());
	
	public TrackerManager(TorrentMetadata metadata) throws MalformedURLException {
		super("TrackerManager");
		this.metadata = metadata;
		this.url = new URL(metadata.getAnnounce());
		if(!url.getProtocol().equals("http"))
			throw new MalformedURLException("protocol "+url.getProtocol()+" not supported");
		eventListeners = new Vector<TrackerEventListener>();
		state = State.INITIALIZED;
		lastRequest = System.currentTimeMillis();
		waitTime = 0;
		completed = false;
	}
	
	@Override
	public void run() {
		TrackerRequest req = null;
		TrackerResponse response = null;
		logger.fine("Launching Tracker manager for torrent "+metadata.getName());
		while (state != State.STOPPED) {
			if(System.currentTimeMillis()-lastRequest >= waitTime) {
				try {
					switch(state) {
					case INITIALIZED:
						req = new TrackerRequest(url);
						req.setCompactAllowed(true);
						req.setEvent("started");
						req.setInfoHash(metadata.getInfoHash());
						req.setPeerId(ConfigManager.getClientId());
						req.setPort(ConfigManager.getPort());
						
						//TODO obtener datos reales
						req.setUploaded(0);
						req.setDownloaded(0);
						req.setLeft(10000000);
						response = TrackerResponse.createFromStream(req.make());
						logger.info("Request sent to tracker "+metadata.getAnnounce());
						logger.fine("Next request in "+response.getInterval()+" seconds");
						for (Peer peer : response.getPeers()) {
							for (TrackerEventListener listener : eventListeners) {
								listener.peerAddedEvent(peer);
							}
						}
						lastRequest = System.currentTimeMillis();
						waitTime = response.getInterval()*1000;
						state = State.STARTED;
						break;
					case STARTED:
						req = new TrackerRequest(url);
						req.setCompactAllowed(true);
						req.setInfoHash(metadata.getInfoHash());
						req.setPeerId(ConfigManager.getClientId());
						req.setPort(ConfigManager.getPort());
						if(completed) {
							req.setEvent("completed");
							completed = false;
						}
						
						//TODO obtener datos reales
						req.setUploaded(0);
						req.setDownloaded(0);
						req.setLeft(10000000);
						response = TrackerResponse.createFromStream(req.make());
						logger.info("Request sent to tracker "+metadata.getAnnounce());
						logger.fine("Next request in "+response.getInterval()+" seconds");
						for (Peer peer : response.getPeers()) {
							for (TrackerEventListener listener : eventListeners) {
								listener.peerAddedEvent(peer);
							}
						}
						lastRequest = System.currentTimeMillis();
						waitTime = response.getInterval()*1000;
						break;
					case STOPPING:
						req = new TrackerRequest(url);
						req.setCompactAllowed(true);
						req.setEvent("stopped");
						req.setInfoHash(metadata.getInfoHash());
						req.setPeerId(ConfigManager.getClientId());
						req.setPort(ConfigManager.getPort());
						
						//TODO obtener datos reales
						req.setUploaded(0);
						req.setDownloaded(0);
						req.setLeft(10000000);
						response = TrackerResponse.createFromStream(req.make());
						logger.info("Request sent to tracker "+metadata.getAnnounce());
						logger.fine("Next request in "+response.getInterval()+" seconds");
						for (Peer peer : response.getPeers()) {
							for (TrackerEventListener listener : eventListeners) {
								listener.peerAddedEvent(peer);
							}
						}
						lastRequest = System.currentTimeMillis();
						waitTime = response.getInterval()*1000;
						
						state = State.STOPPED;
						break;
					}

				} catch(TrackerProtocolException e) {
					logger.warning("Error sending request to tracker "+metadata.getAnnounce()+". "+e.getMessage());
					end();
				} catch (IOException e) {
					logger.warning("Error sending request to tracker "+metadata.getAnnounce()+". "+e.getMessage());
					end();
				}
			} else {
				synchronized (url) {
					try {
						url.wait(lastRequest+waitTime-System.currentTimeMillis());
					} catch (InterruptedException e) {
					} catch (IllegalArgumentException e) {}
				}
			}
		}
	}
	
	
	public void end() {
		synchronized (url) {
			state = State.STOPPING;
			url.notify();	
		}
	}

	public void addTrackerEventListener(TrackerEventListener l) {
		eventListeners.add(l);
	}

}
