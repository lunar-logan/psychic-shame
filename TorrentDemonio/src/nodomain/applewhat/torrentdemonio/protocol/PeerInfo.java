/**
 * 
 */
package nodomain.applewhat.torrentdemonio.protocol;


/**
 * @author Alberto Manzaneque
 *
 */
public class PeerInfo {
	
	private String address;
	private int port;
	private String id;
	
	
	public PeerInfo(String address, int port, String id) {
		if(address == null) throw new NullPointerException();
		this.address = address;
		this.port = port;
		this.id = id;
	}
	
	public PeerInfo(String address, int port) {
		this(address, port, null);
	}
	
	@Override
	public boolean equals(Object obj) {
		PeerInfo comp = (PeerInfo) obj;
		return address.equals(comp.address) && port == comp.port ;
	}
	
	@Override
	public String toString() {
		return "IP: "+address+", port: "+port+", id: "+id;
	}

	public String getAddress() {
		return address;
	}

	public String getId() {
		return id;
	}

	public int getPort() {
		return port;
	}
	
}
