/**
 * 
 */
package nodomain.applewhat.torrentdemonio.protocol.messages;

import java.nio.ByteBuffer;

/**
 * @author Alberto Manzaneque Garcia
 *
 */
public class InterestedMessage extends Message {
	
	private static InterestedMessage instance = new InterestedMessage();

	protected InterestedMessage() {
		super(Type.INTERESTED);
	}

	/* (non-Javadoc)
	 * @see nodomain.applewhat.torrentdemonio.protocol.messages.Message#writeRaw(java.nio.ByteBuffer)
	 */
	@Override
	public void write(ByteBuffer buf) {
		buf.putInt(1);
		buf.put(type.getId());
	}
	
	public static InterestedMessage create() {
		return instance;
	}

}
