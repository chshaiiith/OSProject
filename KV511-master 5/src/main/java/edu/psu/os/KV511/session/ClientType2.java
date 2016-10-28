package edu.psu.os.KV511.session;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadLocalRandom;

import edu.psu.os.KV511.util.ClientProp;
import edu.psu.os.KV511.util.MessageUtil;

public class ClientType2 implements Client, Runnable {

private ClientProp prop = null;
	
	public ClientType2() {
		prop = new ClientProp();
	}
	
	public Socket initSocket() throws UnknownHostException, IOException {
		return new Socket(prop.getIP(), prop.getPort());
	}
	
	public void run() {
		try {
			int no_of_ses = 10000;	//Integer.parseInt(prop.get("type2.num_of_sessions"));
			int no_of_req = 10;		//Integer.parseInt(prop.get("type2.no_of_get"));
			
			for(int i = 0; i < no_of_ses; i++) {
				if( i < 3500 || i > 6500) {
					Thread.sleep(1000);
				}
				Socket socket = initSocket();
				MessageUtil msg = new MessageUtil(socket);
				
				for(int j = 0; j < no_of_req; j++) {
					int ran = ThreadLocalRandom.current().nextInt(1, 10 + 1);
					long key = ThreadLocalRandom.current().nextInt(1, 100000 + 1);
					if( ran % 2 == 0) {
						msg.putRequest(key, key);
					} else {
						msg.getRequest(key);
					}
				}

				msg.stop();
				msg.close();
				socket.close();
			}
		}
		catch( UnknownHostException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
