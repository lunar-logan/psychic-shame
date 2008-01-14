/**
 * 
 */
package nodomain.applewhat.torrentdemonio.protocol;

/**
 * @author Alberto Manzaneque
 *
 */
public interface IncomingConnectionListener {
	
	byte[] getInfoHash();
	void incomingConnectionReceived(PeerConnection peer);

}
