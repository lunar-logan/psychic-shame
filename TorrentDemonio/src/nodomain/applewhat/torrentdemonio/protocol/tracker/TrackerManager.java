/**
 * 
 */
package nodomain.applewhat.torrentdemonio.protocol.tracker;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.List;
import java.util.Vector;

import nodomain.applewhat.torrentdemonio.metafile.TorrentMetadata;

/**
 * @author Alberto Manzaneque
 *
 */
public class TrackerManager extends Thread implements TrackerEventProducer {
	
	public enum State { INITIALIZED, STARTED, STOPPING, STOPPED, COMPLETED }; 
	
	private TorrentMetadata metadata;
	private URL url;
	private List<TrackerEventListener> eventListeners;
	private State state;
	private long lastRequest, waitTime;
	
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
	}
	
	@Override
	public void run() {
		while (state != State.STOPPED) {
			if(System.currentTimeMillis()-lastRequest >= waitTime) {
				try {
					switch(state) {
					case INITIALIZED:
						TrackerRequest req = new TrackerRequest(url);
						req.setCompactAllowed(true);
						req.setEvent("started");
						// TODO req.setInfoHash()
						TrackerResponse response = TrackerResponse.createFromStream(req.make());
						break;
					case STARTED:
						break;
					case COMPLETED:
						break;
					case STOPPING:
						state = State.STOPPED;
						break;
					}

				} catch(TrackerProtocolException e) {
					// TODO poner un log
					end();
				} catch (IOException e) {
					// TODO poner un log
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
		state = State.STOPPING;
		url.notify();
	}

	public void addTrackerEventListener(TrackerEventListener l) {
		eventListeners.add(l);
	}

}
