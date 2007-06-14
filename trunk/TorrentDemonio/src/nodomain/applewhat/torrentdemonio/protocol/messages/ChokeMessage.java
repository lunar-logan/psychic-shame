/**
 * 
 */
package nodomain.applewhat.torrentdemonio.protocol.messages;

import java.nio.ByteBuffer;

/**
 * @author Alberto Manzaneque Garcia
 *
 */
public class ChokeMessage extends Message {

	private static ChokeMessage instance = new ChokeMessage();
	
	protected ChokeMessage() {
		super(Type.CHOKE);
	}
	
	public static ChokeMessage create() {
		return instance;
	}

}
