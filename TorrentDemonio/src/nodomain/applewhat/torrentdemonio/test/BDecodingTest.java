package nodomain.applewhat.torrentdemonio.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import nodomain.applewhat.torrentdemonio.bencoding.BDecoder;
import nodomain.applewhat.torrentdemonio.bencoding.BEncodingException;

public class BDecodingTest {

	/**
	 * @param args
	 * @throws BEncodingException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, BEncodingException {
		File torrent = new File("/var/lib/mldonkey/torrents/downloads/taste.torrent");
		FileInputStream in = new FileInputStream(torrent);
		BDecoder decoder = new BDecoder(in);
		System.out.println(decoder.decodeNext());

	}

}
