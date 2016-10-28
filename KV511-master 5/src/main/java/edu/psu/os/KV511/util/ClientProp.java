package edu.psu.os.KV511.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ClientProp {
	
	private final static String PROP_FILE = "SessionProps.properties";
	private final static String IP = "frontend_ip";
	private final static String PORT = "frontend_port";
	private Properties prop;
	private InputStream input;
	
	public ClientProp() {
		prop = new Properties();
		try {
			input = getClass().getClassLoader()
			        .getResourceAsStream(PROP_FILE);
			prop.load(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String getIP() {
		return prop.getProperty(IP);
	} 
	
	public int getPort() {
		return Integer.parseInt(prop.getProperty(PORT));
	}
	
	public String get(String property) {
		return prop.getProperty(property);
	}

}
