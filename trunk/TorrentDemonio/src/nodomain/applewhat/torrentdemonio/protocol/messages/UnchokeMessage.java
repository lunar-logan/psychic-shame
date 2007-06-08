package nodomain.applewhat.torrentdemonio.protocol.messages;

import java.nio.ByteBuffer;

public class UnchokeMessage extends Message {
	
	private static UnchokeMessage instance = new UnchokeMessage();
	
	protected UnchokeMessage() {
		super(Type.UNCHOKE);
	}

	@Override
	public void write(ByteBuffer buf) {
		buf.putInt(1);
		buf.put(type.getId());
	}
	
	public static UnchokeMessage create() {
		return instance;
	}

}
