/**
 * 
 */
package nodomain.applewhat.torrentdemonio.protocol;

import java.net.MalformedURLException;

import nodomain.applewhat.torrentdemonio.metafile.TorrentMetadata;
import nodomain.applewhat.torrentdemonio.protocol.tracker.TrackerManager;
import nodomain.applewhat.torrentdemonio.protocol.wire.WireManager;
import nodomain.applewhat.torrentdemonio.util.PausableThread;

/**
 * @author Alberto Manzaneque
 *
 */
public class TorrentDownloadManager implements PausableThread {
	
	private TorrentMetadata metadata;
	private TrackerManager tracker;
	private WireManager wire;
	private DownloadProcess process;
	private boolean start, stop, destroy;
	private State state;
	
	private enum State { INITIALIZED, STARTED, STOPPED, DESTROYED };
	
	public TorrentDownloadManager(TorrentMetadata metadata) throws MalformedURLException {
		this.metadata = metadata;
		tracker = new TrackerManager(metadata);
		wire = new WireManager(metadata.getName());
		tracker.addTrackerEventListener(wire);
		process = new DownloadProcess();
		new Thread(process).start();
		start = false;
		stop = false;
		destroy = false;
		state = State.INITIALIZED;
	}
	
	private class DownloadProcess implements Runnable {
		public void run() {
			tracker.start();
			wire.start();
			try {
				while (state != State.DESTROYED) {
					switch(state) {
					case INITIALIZED:
						synchronized (this) {
							if(!start) wait();
							if(start) {
								state = State.STARTED;
								start = false;
							}
						}
						break;
					case STOPPED:
						synchronized (this) {
							if(!start && !destroy) wait();
							if(start) {
								state = State.STARTED;
								start = false;
							}
							if (destroy) {
								state = State.DESTROYED;
								destroy = false;
							}
						}
						break;
					case STARTED:
					
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
	
	private void createFileSystem() {
		
	}

}
