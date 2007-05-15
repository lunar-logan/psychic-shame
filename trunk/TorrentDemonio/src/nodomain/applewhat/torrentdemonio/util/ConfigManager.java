package nodomain.applewhat.torrentdemonio.util;

import java.util.ResourceBundle;

public class ConfigManager {
	
	//private static ResourceBundle configFile = ResourceBundle.getBundle("config");
	
	public static String getMetafilesDirectory() {
		return ".";//configFile.getString("directory.metafiles");
	}
	
	public static String getClientId() {
		return "meloinventotrikitri"; // TODO generar un identificador valido
	}

}
