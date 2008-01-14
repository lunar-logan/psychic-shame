/**
 * 
 */
package nodomain.applewhat.torrentdemonio.protocol;

/**
 * @author Alberto Manzaneque
 *
 */
public interface PeerMessageProducer {
	
	void addMessageListener(PeerMessageAdapter listener);
	void removeMessageListener(PeerMessageAdapter listener);

}
