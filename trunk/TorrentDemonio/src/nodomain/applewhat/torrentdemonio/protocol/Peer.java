/**
 * 
 */
package nodomain.applewhat.torrentdemonio.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;


/**
 * @author Alberto Manzaneque
 *
 */
public class Peer {
	
	private String address;
	private int port;
	private String id;
	
	private ByteChannel channel;
	private ByteBuffer readBuffer, writeBuffer;
	private boolean amInterested, amChoking, peerInterested, peerChoking;
	private boolean handshakeSent, waitingForMessageCompletion;
	
	private static int READ_BUFFER_SIZE = 100;
	private static int WRITE_BUFFERE_SIZE = 100;
	
	
	public Peer(String address, int port, String id) {
		if(address == null) throw new NullPointerException();
		this.address = address;
		this.port = port;
		this.id = id;
		this.channel = null;
		amInterested = false;
		peerInterested = false;
		amChoking = true;
		peerChoking = true;
		handshakeSent = false;
		readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
		writeBuffer = ByteBuffer.allocate(WRITE_BUFFERE_SIZE);
	}
	
	public Peer(String address, int port) {
		this(address, port, null);
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
	
	public void sendHandshake(byte[] infoHash, String peerId) throws TorrentProtocolException, IOException {
		if(handshakeSent)
			throw new TorrentProtocolException("Handshake already sent");
		
		if(infoHash.length!=20 && peerId.getBytes().length!=20)
			throw new IllegalArgumentException("Bad info_hash or peer_id");
		String pstr = "BitTorrent protocol";
		ByteBuffer buf = ByteBuffer.allocate(68);
		buf.put((byte)pstr.length());
		buf.put(pstr.getBytes());
		buf.put(infoHash);
		buf.put(peerId.getBytes());
		channel.write(buf);
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
	
	public void perform() {
		if(!handshakeSent) {
//			sendHandshake(infoHash, peerId)
		}
		
	}
}
