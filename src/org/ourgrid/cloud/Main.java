package org.ourgrid.cloud;

import java.io.FileInputStream;
import java.util.Properties;

import org.ourgrid.cloud.broker.Configuration;
import org.ourgrid.cloud.rest.BrokerApplication;
import org.restlet.Component;
import org.restlet.data.Protocol;

public class Main {

	public static void main(String[] args) throws Exception {
		Properties properties = new Properties();
		properties.load(new FileInputStream("broker.properties"));
		
		String port = properties.getProperty(Configuration.REST_PORT);
		
		Component component = new Component(); 
	    component.getServers().add(Protocol.HTTP, Integer.valueOf(port));  
	    component.getClients().add(Protocol.FILE);
	    component.getDefaultHost().attach(new BrokerApplication(properties));
	    
	    component.start();
	}
	
}
