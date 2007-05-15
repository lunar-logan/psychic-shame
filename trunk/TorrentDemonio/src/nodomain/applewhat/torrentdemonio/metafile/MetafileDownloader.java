/**
 * 
 */
package nodomain.applewhat.torrentdemonio.metafile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import nodomain.applewhat.torrentdemonio.util.ConfigManager;

/**
 * @author Alberto Manzaneque
 *
 */
public class MetafileDownloader {
	
	private MetafileDownloader() { }
	
	public static File download(URL url) throws IOException {
		if(!url.getProtocol().equals("http"))
			throw new IOException("Unsupported protocol: "+url.getProtocol());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("User-Agent", "TorrentDemonio");
		conn.connect();
		int response = conn.getResponseCode();
		if(response != HttpURLConnection.HTTP_ACCEPTED && response != HttpURLConnection.HTTP_OK) {
			throw new IOException(conn.getResponseMessage());
		}
		
		// FIXME encontrar bien el nombre del fichero que bajamos
		String filename = conn.getHeaderField("Content-Disposition");
		if(filename.length() == 0) {
			filename = url.getFile();
		}
        if (filename == null || filename.length() == 0 || filename.equals("/")) {
        	filename = url.getHost();
        }
		
        byte[] buffer = new byte[1024];
		int bytesRead = 0;
		File save = new File(ConfigManager.getMetafilesDirectory()+File.separator+filename);
		InputStream in = conn.getInputStream();
		FileOutputStream out = new FileOutputStream(save);
		
		while((bytesRead=in.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
		}
		
		out.flush();
		out.close();
		in.close();
		
		return save;
		
	}

}
