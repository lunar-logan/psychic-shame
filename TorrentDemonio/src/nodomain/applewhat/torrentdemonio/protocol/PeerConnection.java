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
public class PeerConnection {
	
	private ByteChannel channel;
	private boolean amInterested, amChoking, peerInterested, peerChoking;
	private boolean handshakeSent, waitingForMessageCompletion;
	
	public PeerConnection(ByteChannel channel) {
		this.channel = channel;
		amInterested = false;
		peerInterested = false;
		amChoking = true;
		peerChoking = true;
		handshakeSent = false;
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
	

}
