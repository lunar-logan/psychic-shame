package nodomain.applewhat.torrentdemonio.util;


public class ConfigManager {
	
	//private static ResourceBundle configFile = ResourceBundle.getBundle("config");
	
	public static String getMetafilesDirectory() {
		return ".";//configFile.getString("directory.metafiles");
	}
	
	public static String getClientId() {
		return "AZ206123456789012345"; // TODO generar un identificador valido
	}
	
	public static int getPort() {
		return 6881;
	}
	
	public static String getTempDir() {
		return "temp";
	}
	
	public static String getIncomingDir() {
		return "incoming";
	}

}
