/**
 * 
 */
package nodomain.applewhat.torrentdemonio.protocol;


/**
 * @author Alberto Manzaneque
 *
 */
public class Peer {
	
	private String address;
	private int port;
	private String id;
	
	private boolean isChokingMe, iAmChokingTo;
	private boolean isInterestedInMe, iAmInterestedIn;
	
	public Peer(String address, int port, String id) {
		if(address == null) throw new NullPointerException();
		this.address = address;
		this.port = port;
		this.id = id;
		isChokingMe = iAmChokingTo = true;
		isInterestedInMe = iAmInterestedIn = false;
	}
	
	public Peer(String address, int port) {
		this(address, port, null);
	}
	
	@Override
	public boolean equals(Object obj) {
		Peer comp = (Peer) obj;
		return address.equals(comp.address) && port == comp.port && id != null && id.equals(comp.id);
	}
	
}
