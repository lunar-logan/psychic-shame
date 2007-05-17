package nodomain.applewhat.torrentdemonio.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import nodomain.applewhat.torrentdemonio.metafile.MalformedMetadataException;
import nodomain.applewhat.torrentdemonio.metafile.MetafileDownloader;
import nodomain.applewhat.torrentdemonio.metafile.TorrentMetadata;
import nodomain.applewhat.torrentdemonio.protocol.TorrentDownloadManager;

public class ManagerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws MalformedURLException, IOException, MalformedMetadataException {
		System.out.println("start");
		TorrentMetadata torrent = TorrentMetadata.createFromFile(MetafileDownloader.download(new URL("http://www.mininova.org/get/708922")));
		new TorrentDownloadManager(torrent).start();
		System.out.println("end");
	}

}
