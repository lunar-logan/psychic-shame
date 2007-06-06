package nodomain.applewhat.torrentdemonio.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import nodomain.applewhat.torrentdemonio.metafile.MalformedMetadataException;
import nodomain.applewhat.torrentdemonio.metafile.MetafileDownloader;
import nodomain.applewhat.torrentdemonio.metafile.TorrentMetadata;
import nodomain.applewhat.torrentdemonio.protocol.TorrentDownloadManager;
import nodomain.applewhat.torrentdemonio.util.TempDirStructure;

public class ManagerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws MalformedURLException, IOException, MalformedMetadataException {
		System.out.println("start");
		TorrentMetadata torrent = TorrentMetadata.createFromFile(MetafileDownloader.download(new URL(args[0])));
		TempDirStructure files = null;
		// more initialization code
		try {
			files = TempDirStructure.createFileSystem(torrent.getInfoHashHex());
			if(!files.getTorrentFile().exists()) {
				// TODO serialize metadata into a file
			}
		} catch (IOException e1) {
			System.err.println("Error creating temp directory for torrent "+torrent.getName()+": "+e1.getMessage());
		}
		new TorrentDownloadManager(torrent, files).start();
		System.out.println("end");
	}

}
