/**
 * 
 */
package nodomain.applewhat.torrentdemonio.bencoding;

/**
 * @author Alberto Manzaneque
 *
 */
public class BInteger implements BElement {
	
	private int value;
	
	public BInteger(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return Integer.toString(value);
	}

}
