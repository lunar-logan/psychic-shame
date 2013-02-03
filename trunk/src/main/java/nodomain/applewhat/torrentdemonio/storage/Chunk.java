/**
 * 
 */
package nodomain.applewhat.torrentdemonio.storage;

/**
 * @author Alberto Manzaneque
 *
 */
public class Chunk {
	
	private int index, length, completion;
	private boolean locked;
	private byte[] checksum;
	
	public Chunk(int index, int length, byte[] checksum) {
		this.index = index;
		this.length = length;
		this.checksum = checksum;
		this.completion = 0;
		locked = false;
	}

	public int getCompletion() {
		return completion;
	}

	void markCompleted(int nBytes) {
		this.completion += nBytes;
	}

	public int getLength() {
		return length;
	}

	public byte[] getChecksum() {
		return checksum;
	}
	
	public boolean isComplete() {
		return length == completion;
	}

	public int getIndex() {
		return index;
	}
	
	public void setLocked(boolean l) {
		this.locked = l;
	}
	
	public boolean isLocked() {
		return locked;
	}
	
}
