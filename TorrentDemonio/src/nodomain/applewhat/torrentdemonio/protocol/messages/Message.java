/**
 * 
 */
package nodomain.applewhat.torrentdemonio.protocol.messages;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

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
	protected byte[] rawData;
	private ByteBuffer buf, header;
	
	/**
	 * 
	 */
	protected Message(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	public ByteBuffer payload() {
		if(buf == null)
			buf = ByteBuffer.wrap(rawData);
		return buf;
	}
	
	/**
	 * @param out
	 * @return true if the whole message has been written
	 * @throws IOException 
	 */
	public boolean writeTo(ByteChannel out) throws IOException {
		if(type != Type.RAW) {
			out.write(header);
			if(header.hasRemaining()) return false;
		}
		out.write(payload());
		if(payload().hasRemaining()) return false;
		return true;
	}
	
	public void reset() {
		if(header != null) header.reset();
		payload().reset();
	}
	
	public static Message createRaw(ByteBuffer in) {
		Message msg = new Message(Type.RAW);
		msg.rawData = new byte[in.limit()-in.position()];
		for(int i=0; i<msg.rawData.length; i++) {
			msg.rawData[i] = in.get();
		}
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
			for(int i=0; in.hasRemaining() && i<length-1; i++) {
				msg.rawData[i] = in.get();
			}
			msg.header = ByteBuffer.allocate(5).putInt(length).put(tmp.getId());
			msg.header.clear();
			return msg;
		} catch(BufferUnderflowException e) {
			throw new MalformedMessageException("Not enough bytes in message");
		}
	}

}
