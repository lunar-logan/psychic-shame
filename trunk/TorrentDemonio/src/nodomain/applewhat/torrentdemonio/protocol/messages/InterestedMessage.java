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

	public static InterestedMessage create() {
		return instance;
	}

}
