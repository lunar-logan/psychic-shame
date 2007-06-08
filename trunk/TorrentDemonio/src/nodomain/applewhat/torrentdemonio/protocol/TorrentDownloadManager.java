/**
 * 
 */
package nodomain.applewhat.torrentdemonio.protocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import nodomain.applewhat.torrentdemonio.metafile.TorrentMetadata;
import nodomain.applewhat.torrentdemonio.protocol.tracker.TrackerEventListener;
import nodomain.applewhat.torrentdemonio.protocol.tracker.TrackerManager;
import nodomain.applewhat.torrentdemonio.util.ConfigManager;
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
	private List<Peer> remainingPeers, connectedPeers;
	private TempDirStructure fileNames;
	private Selector sockSelector;
	
	private enum State { INITIALIZED, STARTED, STOPPED, DESTROYED };
	
	public TorrentDownloadManager(TorrentMetadata metadata, TempDirStructure files) throws MalformedURLException {
		this.metadata = metadata;
		this.fileNames = files;
		tracker = new TrackerManager(metadata);
		remainingPeers = new Vector<Peer>();
		connectedPeers = new Vector<Peer>();
		tracker.addTrackerEventListener(new PeerAdder());
		process = new DownloadProcess();
		start = false;
		stop = false;
		destroy = false;
		state = State.INITIALIZED;
		sockSelector = null;
		new Thread(process, metadata.getName()).start();
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
								// TODO sacar fuera del synchronized lo que pueda tardar
								doStart();
							}
						}
						break;
					case STOPPED:
						synchronized (this) {
							if(!start && !destroy) wait();
							if(start) {
								doStart();
							}
							if (destroy) {
								doDestroy();
							}
						}
						break;
					case STARTED:
						synchronized (this) {
							if(stop) doStop();
							if(destroy) {
								doStop();
								doDestroy();
							}
						}
						makeNewConnections();
						if(stop || destroy) break;
						processSocketEvents();
						if(stop || destroy) break;
						findNewActionsToDo();
						
						break;
					case DESTROYED:
						break;
					}
				}
			} catch (InterruptedException e) {
				logger.warning(e.getMessage());
				doStop();
				doDestroy();
			} catch (IOException e) {
				logger.severe("Unrecoverable IO exception in torrent "+metadata.getName()+". "+e.getMessage());
				doStop();
				doDestroy();
			}
			
		}		
	}
	
	protected void doStart() throws IOException {
		sockSelector = Selector.open();
		state = State.STARTED;
		start = false;
		logger.fine("Torrent "+metadata.getName()+" started");
	}
	
	protected void doStop() {
		for (Peer connectedPeer : connectedPeers) {
			try {
				connectedPeer.disconnect();
			} catch (IOException e) {
				logger.warning("Error when disconnecting from peer "+connectedPeer+". "+e.getMessage());
			}
		}
		try {
			sockSelector.close();
		} catch (IOException e) {
			logger.warning("Error closing socket selector for "+metadata.getName());
		}
		state = State.STOPPED;
		stop = false;
		logger.fine("Torrent "+metadata.getName()+" stopped");
	}
	
	protected void doDestroy() {
		state = State.DESTROYED;
		destroy = false;
		logger.fine("Torrent "+metadata.getName()+" destroyed");
	}
	
	public void start() {
		synchronized (process) {
			start = true;
			process.notify();
		}
	}
	
	public void stop() {
		synchronized (process) {
			stop = true;
			process.notify();
		}
	}
	
	public void destroy() {
		synchronized (process) {
			destroy = true;
			process.notify();
		}
	}
	
	private void makeNewConnections() {
		while(connectedPeers.size() < ConfigManager.getMaxConnections() && remainingPeers.size()>0) {
			Peer peer = null;
			peer = remainingPeers.get(0);
			remainingPeers.remove(peer);
			try {
				SocketChannel sock = SocketChannel.open();
				sock.configureBlocking(false);
				sock.connect(new InetSocketAddress(peer.getAddress(), peer.getPort()));
				sock.register(sockSelector, SelectionKey.OP_CONNECT, peer);
			} catch (IOException e) {
				logger.fine("Could not connect to peer "+peer);
			}
		}
	}
	
	private void processSocketEvents() {
		try {
			if(sockSelector.select(5000)>0) {
				Set<SelectionKey> keys = sockSelector.selectedKeys();
				Iterator<SelectionKey> i = keys.iterator();
				while(i.hasNext()) {
					SelectionKey key = i.next();
					i.remove();
					
					if(key.isConnectable()) {
						SocketChannel channel = (SocketChannel) key.channel();
						Peer peer = (Peer) key.attachment();
						try {
							if(channel.isConnectionPending()) {
								channel.finishConnect();
								peer.setChannel(channel);
								peer.sendHandshake(metadata.getInfoHash(), ConfigManager.getClientId());
								connectedPeers.add(peer);
								logger.fine("Connected to new peer: "+peer);
							}
						} catch (Exception e) {
							logger.fine("Can not connect to peer "+peer+". "+e.getMessage());
						}
					} else if (key.isWritable()) {
						
					} else if (key.isReadable()) {
						
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void findNewActionsToDo() {
		// TODO
	}
	

	
	private class PeerAdder implements TrackerEventListener {
		public void peerAddedEvent(Peer peer) {
			synchronized(remainingPeers) {
				if(!remainingPeers.contains(peer)) {
					logger.fine("New peer for download"+metadata.getName()+", "+peer);
					remainingPeers.add(peer);
				}
			}
		}
	}

}
