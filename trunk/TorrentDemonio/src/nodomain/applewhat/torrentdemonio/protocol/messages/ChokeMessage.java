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
	
	@Override
	public void write(ByteBuffer buf) {
		buf.putInt(1);
		buf.put(type.getId());
	}
	
	public static ChokeMessage create() {
		return instance;
	}

}
