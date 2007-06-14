/**
 * 
 */
package nodomain.applewhat.torrentdemonio.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

import nodomain.applewhat.torrentdemonio.protocol.messages.Message;
import nodomain.applewhat.torrentdemonio.util.ConfigManager;


/**
 * @author Alberto Manzaneque
 *
 */
public class Peer {
	
	private String address;
	private int port;
	private String id;
	private byte[] infoHash;
	
	private ByteChannel channel;
	private ByteBuffer readBuffer, writeBuffer;
	private boolean amInterested, amChoking, peerInterested, peerChoking;
	private boolean handshakeSent, handshakeReceived;
	
	private static int READ_BUFFER_SIZE = 100;
	private static int WRITE_BUFFERE_SIZE = 100;
	
	private Queue<Message> pendingWrites;
	
	
	
	public Peer(String address, int port, byte[] infoHash) {
		this(address, port);
		this.infoHash = infoHash;
	}
	
	/** Constructs a Peer without the info_hash. It must be read from the handshake
	 * or setted by the method setInfoHash
	 */
	public Peer(String address, int port) {
		if(address == null) throw new NullPointerException();
		this.address = address;
		this.port = port;
		this.id = null;
		this.channel = null;
		this.infoHash = null;
		amInterested = false;
		peerInterested = false;
		amChoking = true;
		peerChoking = true;
		handshakeReceived = handshakeSent = false;
		readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
		writeBuffer = ByteBuffer.allocate(WRITE_BUFFERE_SIZE);
		pendingWrites = new LinkedList<Message>();
	}
	
	@Override
	public boolean equals(Object obj) {
		Peer comp = (Peer) obj;
		return address.equals(comp.address) && port == comp.port ;
	}
	
	@Override
	public String toString() {
		return "IP: "+address+", port: "+port+", id: "+id;
	}

	public String getAddress() {
		return address;
	}

	public String getId() {
		return id;
	}

	public int getPort() {
		return port;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public void setInfoHash(byte[] infoHash) {
		this.infoHash = infoHash;
	}

	public byte[] getInfoHash() {
		return infoHash;
	}
	
	private void sendHandshake() throws TorrentProtocolException {
		String myId = ConfigManager.getClientId();
		if(infoHash.length!=20 && myId.getBytes().length!=20)
			throw new TorrentProtocolException("Bad info_hash or peer_id");
		String pstr = "BitTorrent protocol";
		ByteBuffer buf = ByteBuffer.allocate(68);
		buf.put((byte)pstr.length());
		buf.put(pstr.getBytes());
		buf.put(infoHash);
		buf.put(myId.getBytes());
		Message handshake = Message.createRaw(buf);
		pendingWrites.offer(handshake);
		handshakeSent = true;
	}
	
	public void sendKeepAlive() throws IOException {
		channel.write(ByteBuffer.wrap(new byte[]{0,0,0,0}));
	}
	
	public void disconnect() throws IOException {
		if(channel!=null)
			channel.close();
		channel = null;
	}
	
	public boolean isConnected() throws IOException {
		return channel != null && channel.isOpen();
	}
	
	public void setChannel(ByteChannel chan) {
		this.channel = chan;
	}
	
	public void performPending() throws IOException {
		readPendingData();
		writePendingData();
		if(infoHash == null) 
		if(!handshakeSent) {
//			sendHandshake(infoHash, peerId)
		}
		
	}
	
	private void readPendingData() {
		// TODO
	}
	
	private void writePendingData() throws IOException {
		try {
			boolean full = false;
			while (!full) {
				Message pending = pendingWrites.element();
				full = !pending.writeTo(channel);
				if(!full) pendingWrites.remove();
			}
		} catch (NoSuchElementException e) {
			// nothing pending
		}
	}
	
	protected void onHandshake(Message msg) {
		if(infoHash == null) {
			ByteBuffer data = msg.payload();
			int length = data.get();
			for(int i=0; i<length; i++) {
				data.get();
			}
			byte[] ih = new byte[20];
			data.get(ih);
			this.infoHash = ih;
		}
		handshakeReceived = true;
		// TODO some error handling?
	}



}
