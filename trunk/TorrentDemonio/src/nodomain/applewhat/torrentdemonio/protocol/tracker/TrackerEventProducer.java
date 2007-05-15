/**
 * 
 */
package nodomain.applewhat.torrentdemonio.protocol.tracker;

/**
 * @author Alberto Manzaneque
 *
 */
public interface TrackerEventProducer {
	public void addTrackerEventListener(TrackerEventListener l);
}
