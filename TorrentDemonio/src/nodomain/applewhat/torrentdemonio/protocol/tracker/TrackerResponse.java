/**
 * 
 */
package nodomain.applewhat.torrentdemonio.protocol.tracker;

import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

import nodomain.applewhat.torrentdemonio.bencoding.BDecoder;
import nodomain.applewhat.torrentdemonio.bencoding.BDictionary;
import nodomain.applewhat.torrentdemonio.bencoding.BElement;
import nodomain.applewhat.torrentdemonio.bencoding.BEncodingException;
import nodomain.applewhat.torrentdemonio.bencoding.BInteger;
import nodomain.applewhat.torrentdemonio.bencoding.BList;
import nodomain.applewhat.torrentdemonio.bencoding.BString;
import nodomain.applewhat.torrentdemonio.protocol.Peer;

/**
 * @author Alberto Manzaneque
 *
 */
public class TrackerResponse {
	
	private String warningMessage;
	private long interval;
	private long minInterval;
	private String trackerId;
	private long complete;
	private long incomplete;
	private List<Peer> peers;
	
	protected TrackerResponse() {
		peers = new ArrayList<Peer>();
	}
	
	public static TrackerResponse createFromStream(InputStream in) throws TrackerProtocolException {
		TrackerResponse response = null;
		try {
			response = new TrackerResponse();
			BDictionary root = (BDictionary) new BDecoder(in).decodeNext();
			
			BElement bDec = root.get(new BString("failure reason"));
			if(bDec != null) {
				throw new TrackerProtocolException("the tracker returned an error: "+bDec);
			}
			
			bDec = root.get(new BString("warning message"));
			if(bDec != null) {
				response.warningMessage = ((BString)bDec).getValue();			
			}
			
			bDec = root.get(new BString("interval"));
			if(bDec == null)
				throw new TrackerProtocolException("interval not present in tracker response");
			response.interval = ((BInteger)bDec).getValue();

			bDec = root.get(new BString("min interval"));
			if(bDec != null)
				response.minInterval = ((BInteger)bDec).getValue();
			
			bDec = root.get(new BString("tracker id"));
			if(bDec != null)
				response.trackerId = ((BString)bDec).getValue();

			bDec = root.get(new BString("complete"));
			if(bDec != null)
				response.complete = ((BInteger)bDec).getValue();
			
			bDec = root.get(new BString("incomplete"));
			if(bDec != null)
				response.incomplete = ((BInteger)bDec).getValue();

			bDec = root.get(new BString("peers"));
			if(bDec instanceof BList) { // normal mode
				for (BElement current : (BList)bDec) {
					BDictionary dict = (BDictionary) current;
					BString ip = (BString) dict.get(new BString("ip"));
					BInteger port = (BInteger) dict.get(new BString("port"));
					if(ip == null || port == null) {
						throw new TrackerProtocolException("malformed peer list");
					}
					BString peerId = (BString) dict.get(new BString("peer id"));
					Peer peer = null;
					if(peerId == null) {
						peer = new Peer(ip.getValue(), (int)port.getValue());
					} else {
						peer = new Peer(ip.getValue(), (int)port.getValue(), peerId.getValue());
					}
					response.peers.add(peer);
				}
			} else if (bDec instanceof BString) { // compact mode
				byte[] buf = ((BString)bDec).getBytes();
				if(buf.length % 6 != 0)
					throw new TrackerProtocolException("malformed peer list");
				for(int i=0; i<buf.length; i+=6) {
					StringBuffer ip = new StringBuffer(15);
					for(int j=0; j<4; j++) {
						int part = 0x000000FF & buf[i+j];
						ip.append(part);
						if(j != 3) ip.append(".");
					}
					int firstByte = 0x000000FF & (int)buf[i+4];
					int secondByte = 0x000000FF & (int)buf[i+5];
					int port = firstByte << 8 | secondByte & 0x0000FFFF;
					response.peers.add(new Peer(ip.toString(), port));
				}
				
			} else {
				throw new TrackerProtocolException("invalid peers field in tracker response");
			}
			
		} catch (IOException e) {
			throw new TrackerProtocolException(e.getMessage(), e);
		} catch (BEncodingException e) {
			throw new TrackerProtocolException("tracker response is not well encoded. "+e.getMessage(), e );
		} catch (ClassCastException e) {
			throw new TrackerProtocolException("field not expected in tracker response", e);
		}
		return response;
	}

	public long getComplete() {
		return complete;
	}

	public long getIncomplete() {
		return incomplete;
	}

	public long getInterval() {
		return interval;
	}

	public long getMinInterval() {
		return minInterval;
	}

	public List<Peer> getPeers() {
		return peers;
	}

	public String getTrackerId() {
		return trackerId;
	}

	public String getWarningMessage() {
		return warningMessage;
	}

}
