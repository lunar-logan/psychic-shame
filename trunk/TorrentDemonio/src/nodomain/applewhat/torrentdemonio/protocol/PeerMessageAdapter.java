package nodomain.applewhat.torrentdemonio.protocol;

import java.nio.ByteBuffer;

import nodomain.applewhat.torrentdemonio.protocol.messages.PendingMessage;

public abstract class PeerMessageAdapter {
	
	public void onHandshake(byte[] infoHash, byte[] peerId) {}
	public void onKeepAlive() {}
	public void onChoke() {}
	public void onUnchoke() {}
	public void onInterested() {}
	public void onNotInterested() {}
	public void onHave(int chunkIndex) {}
	public void onBitfield(byte[] bitfield) {}
	public void onRequest(int chunkIndex, int offset, int length) {}
	public void onPiece(int chunkIndex, int offset, ByteBuffer data) {}
	public void onCancel(int chunkIndex, int offset, int length) {}
	public void onMessageSent(PendingMessage msg) {}

}
