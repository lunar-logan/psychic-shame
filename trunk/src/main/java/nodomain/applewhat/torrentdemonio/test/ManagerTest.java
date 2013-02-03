package nodomain.applewhat.torrentdemonio.test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import nodomain.applewhat.torrentdemonio.metafile.MalformedMetadataException;
import nodomain.applewhat.torrentdemonio.metafile.MetafileDownloader;
import nodomain.applewhat.torrentdemonio.protocol.IncomingConnectionsManager;
import nodomain.applewhat.torrentdemonio.protocol.TorrentDownloadManager;
import nodomain.applewhat.torrentdemonio.util.ConfigManager;

public class ManagerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws MalformedURLException, IOException, MalformedMetadataException {
		System.out.println("start");
		File torrentFile = MetafileDownloader.download(new URL(args[0]));
		// more initialization code
		IncomingConnectionsManager incoming = new IncomingConnectionsManager(ConfigManager.getPort());
		TorrentDownloadManager dm = new TorrentDownloadManager(torrentFile);
		incoming.addIncomingConnectionListener(dm);
		dm.start();
		incoming.start();
		System.out.println("end");
	}

}
