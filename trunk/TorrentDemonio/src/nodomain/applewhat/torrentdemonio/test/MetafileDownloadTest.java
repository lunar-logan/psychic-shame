package nodomain.applewhat.torrentdemonio.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import nodomain.applewhat.torrentdemonio.metafile.MalformedMetadataException;
import nodomain.applewhat.torrentdemonio.metafile.MetafileDownloader;
import nodomain.applewhat.torrentdemonio.metafile.TorrentMetadata;

public class MetafileDownloadTest {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws MalformedMetadataException 
	 */
	public static void main(String[] args) throws MalformedURLException, IOException, MalformedMetadataException {
		System.out.println("downloading...");
		TorrentMetadata torrent = TorrentMetadata.createFromFile(MetafileDownloader.download(new URL("http://www.mininova.org/get/683454")));
		System.out.println("end...");

	}

}
