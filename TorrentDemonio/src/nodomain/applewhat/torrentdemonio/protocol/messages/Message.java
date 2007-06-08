/**
 * 
 */
package nodomain.applewhat.torrentdemonio.protocol.messages;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * @author Alberto Manzaneque Garcia
 *
 */
public class Message {
	
	public static enum Type {
		CHOKE(0), UNCHOKE(1), INTERESTED(2), NOT_INTERESTED(3), HAVE(4),
		BITFIELD(5), REQUEST(6), PIECE(7), CANCEL(8), RAW(-1);
		
		private byte id;
		private Type(int id) {
			this.id = (byte)id;
		}
		public byte getId() { return id; }
	}

	protected Type type;
	protected ByteBuffer rawData;
	
	/**
	 * 
	 */
	protected Message(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	
	
	/**
	 * @param out
	 * @throws BufferOverflowException
	 */
	public void write(ByteBuffer out) {
		if(rawData)
		out.putInt(rawData.length+1);
		out.put(type.getId());
		out.put(rawData);
	}
	
	public static Message createRaw(ByteBuffer in) {
		ByteBuffer data = ByteBuffer.allocate(in.limit()-in.position());
		data.put(in);
		Message msg = new Message(Type.RAW);
		msg.rawData = data;
		return msg;
	}
	
	public static Message create(ByteBuffer in) throws MalformedMessageException {
		try {
			int length = in.getInt();
			Type tmp;
			byte kk = in.get();
			switch(kk) {
			case 0: tmp = Type.CHOKE; break;
			case 1: tmp = Type.UNCHOKE; break;
			case 2: tmp = Type.INTERESTED; break;
			case 3: tmp = Type.NOT_INTERESTED; break;
			case 4: tmp = Type.HAVE; break;
			case 5: tmp = Type.BITFIELD; break;
			case 6: tmp = Type.REQUEST; break;
			case 7: tmp = Type.PIECE; break;
			case 8: tmp = Type.CANCEL; break;
			default: throw new MalformedMessageException("Bad message type: "+kk);
			}
			Message msg = new Message(tmp);
			msg.rawData = new byte[length-1];
			in.get(msg.rawData);
			msg.type = null;
			return msg;
		} catch(BufferUnderflowException e) {
			throw new MalformedMessageException("Not enough bytes in message");
		}
	}

}
