/**
 * 
 */
package nodomain.applewhat.torrentdemonio.protocol.messages;

import java.nio.ByteBuffer;

/**
 * @author Alberto Manzaneque Garcia
 *
 */
public class NotInterestedMessage extends Message {
	
	private static NotInterestedMessage instance = new NotInterestedMessage();

	protected NotInterestedMessage() {
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
	
	public static NotInterestedMessage create() {
		return instance;
	}

}
