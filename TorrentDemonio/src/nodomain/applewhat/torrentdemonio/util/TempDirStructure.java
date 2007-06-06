/**
 * 
 */
package nodomain.applewhat.torrentdemonio.util;

import java.io.File;
import java.io.IOException;

/**
 * @author Alberto Manzaneque Garcia
 *
 */
public class TempDirStructure {
	
	private File tempDir;
	private File torrentFile;
	private File dataFile;
	private File stateFile;
	
	private TempDirStructure() { }
	
	public static TempDirStructure createFileSystem(String name) throws IOException {
		TempDirStructure result = new TempDirStructure();
		
		
		result.tempDir = new File(ConfigManager.getTempDir()+"/"+name);
		if(!(result.tempDir.exists() && result.tempDir.isDirectory()) && !result.tempDir.mkdirs())
			throw new IOException("Directory "+result.tempDir.getPath()+" can not be created");
		
		result.torrentFile = new File(result.tempDir, "torrent");
		result.dataFile = new File(result.tempDir, "data");
		result.stateFile = new File(result.tempDir, "state");
		
		return result;
	}

	public File getDataFile() {
		return dataFile;
	}

	public File getStateFile() {
		return stateFile;
	}

	public File getTempDir() {
		return tempDir;
	}

	public File getTorrentFile() {
		return torrentFile;
	}

}
