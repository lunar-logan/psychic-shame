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
	
	public static NotInterestedMessage create() {
		return instance;
	}

}
