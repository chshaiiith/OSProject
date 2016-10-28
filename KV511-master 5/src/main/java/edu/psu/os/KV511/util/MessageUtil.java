package edu.psu.os.KV511.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import edu.psu.os.KV511.model.Message;

public class MessageUtil {

	private DataOutputStream out;
	private DataInputStream in;
	
	public MessageUtil(Socket socket) throws IOException {
		out = new DataOutputStream(socket.getOutputStream());
		in = new DataInputStream(socket.getInputStream());
	}
	
	public void getRequest(long key) throws IOException {
		Message msg = new Message("GET", key, key);
		System.out.println("GET Request "+ key);
		out.writeUTF(msg.getString());
		out.flush();
                
                byte[] data = new byte[32];
                
                int some = in.read(data, 0, 32);
                System.out.println(some);
                byte[] re = new byte[some];

                for (int i = 0; i < some; i ++) {
                    re[i] = data[i];
                }
                String str = new String(re);
		System.out.println("Get Response " + key + "response: "+ str);
	}
	
	public void putRequest(long key, long value) throws IOException {
		Message msg = new Message("PUT", key, key);
		System.out.println("Put Request "+ key);
		out.writeUTF(msg.toString());
		out.flush();
                byte[] data = new byte[32];
                
                int some = in.read(data, 0, 32);
                System.out.println(some);
                byte[] re = new byte[some];
                
                for (int i = 0; i < some; i ++) {
                    re[i] = data[i];
                }
                String str = new String(re);

		System.out.println("Put Response " + key + " response: " + str);
	}
	
	public void stop() throws IOException {
		out.writeUTF("STOP");
		out.flush();
	}
	
	public void close() throws IOException {
		in.close();
		out.close();
	}
	
}
