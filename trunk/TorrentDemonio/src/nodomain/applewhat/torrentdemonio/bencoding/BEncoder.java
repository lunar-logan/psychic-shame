/**
 * 
 */
package nodomain.applewhat.torrentdemonio.bencoding;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Alberto Manzaneque
 *
 */
public class BEncoder {
	// TODO: acabar esta clase, solo si hace falta
	private OutputStream out;
	
	private static String charset = "ISO-8859-1";
	
	public BEncoder(OutputStream output) {
		this.out = output;
	}
	
	public void encode(BString s) throws IOException {
		String enc = s.getValue();
		String length = String.valueOf(enc.length());
		out.write(length.getBytes(charset));
		out.write(':');
	}

}
