package edu.psu.os.KV511;

import java.io.IOException;
import java.net.UnknownHostException;

import edu.psu.os.KV511.session.ClientType1;

public class AppType1 
{
    public static void main( String[] args ) {
    	ClientType1 client = new ClientType1();
        try {
			client.insertForTesting();
			client.doRequests();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    }
}
