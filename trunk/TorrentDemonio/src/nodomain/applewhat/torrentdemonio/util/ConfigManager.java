package nodomain.applewhat.torrentdemonio.util;



public class ConfigManager {
	
	//private static ResourceBundle configFile = ResourceBundle.getBundle("config");
	
	public static String getMetafilesDirectory() {
		return System.getProperty("user.home")+"/prueba";//configFile.getString("directory.metafiles");
	}
	
	public static String getClientId() {
		return "AZ206123456789012345"; // TODO generar un identificador valido
	}
	
	public static int getPort() {
		return 6881;
	}
	
	public static String getTempDir() {
		return System.getProperty("user.home")+"/prueba/temp";
	}
	
	public static String getIncomingDir() {
		return System.getProperty("user.home")+"/prueba/incoming";
	}

	public static int getMaxConnections() {
		return 10;
	}
	
}
