package nodomain.applewhat.torrentdemonio.protocol.messages;

import java.nio.ByteBuffer;

public class UnchokeMessage extends Message {
	
	private static UnchokeMessage instance = new UnchokeMessage();
	
	protected UnchokeMessage() {
		super(Type.UNCHOKE);
	}
	
	public static UnchokeMessage create() {
		return instance;
	}

}
