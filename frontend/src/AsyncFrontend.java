/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Chetan
 */

import frontend.Frontend;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chetan
 */
public class AsyncFrontend {
    /**
     * @param args the command line arguments
     * 
     */
    public static Map<Long,Long> myMap = new HashMap<>();
    public static Map<SocketChannel, ArrayList<String>> pendingRequest= new HashMap<>();


    

    public static void main(String[] args) throws IOException {
        
        class writeInfo {
            SocketChannel client = null;
            String value = null;
            writeInfo(SocketChannel client, String value) {
                this.client = client;
                this.value = value;
            }
        }
        
        Queue<writeInfo> myQueue = new LinkedList<writeInfo>();        

        
        Selector selector = Selector.open();
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        InetSocketAddress socketAddress = new InetSocketAddress("localhost", 10030);
        
        socketChannel.bind(socketAddress);
        socketChannel.configureBlocking(false); // For non-blocking
        
        int operations = socketChannel.validOps();
        SelectionKey sKey = socketChannel.register(selector, operations, null);
        
        while (true) {
            selector.select();
            
            Set<SelectionKey> selectKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectKeys.iterator();
            int i = 1;
            while(keyIterator.hasNext()){
                SelectionKey key = keyIterator.next();
                keyIterator.remove();

                if (key.isAcceptable()){
                    // to check if key's channel is ready to accept new connection
                    Logger.getLogger(Frontend.class.getName()).log(Level.SEVERE, Integer.toString(i), "r");

                    SocketChannel client = socketChannel.accept();
                    Socket socket = client.socket();

                    client.configureBlocking(false); // Adjust channel's blocking mode
                    client.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
                } 
                else if (key.isReadable()) {    					
                    SocketChannel client = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(256);
                    client.read(buffer);
                    String result = new String(buffer.array()).trim();
                    buffer.clear();
                    
                    String [] parts = result.split(":");
                    Logger.getLogger(Frontend.class.getName()).log(Level.SEVERE, result, "r");

                    if (parts[0].equals("STOP")) {
                        key.cancel();
                        client.close();

                        continue;
                    }

                    Long mapkey = Long.parseLong(parts[0], 10);
                    Long mapValue = null;
                    if (!parts[1].equals("null")) {
                        mapValue = Long.parseLong(parts[1], 10);                    
                    }
                    
                    // Output 
                    String output = "";
                    if (mapValue != null) {
                        myMap.put(mapkey, mapValue);
                        output = "0";
                    } else if (!myMap.containsKey(mapkey)) {
                        output = "-1";
                    } else {
                        output = Long.toString(myMap.get(mapkey));
                    }
                   
                   if (pendingRequest.containsKey(client)) {
                       ArrayList<String> clientRequest = pendingRequest.get(client);
                       clientRequest.add(result);
                       pendingRequest.put(client, clientRequest);

                   }
                   
                   ArrayList<String> newString = new ArrayList<String>();
                   newString.add(output);
                   pendingRequest.put(client, newString);
                   
                } 
                else if (key.isWritable()) {
                    SocketChannel channel = (SocketChannel) key.channel();

                    if (!pendingRequest.containsKey(channel)) {
                        continue;
                    }
                    
                    if(pendingRequest.get(channel).isEmpty()) {
                        continue;
                    }
                    int a = 0;

                    ArrayList<String> request = pendingRequest.get(channel);
                    int flag = 0;
                    for (String s: request) {
                        if (s.equals("STOP")) {
                            key.cancel();
                            channel.close();
                            pendingRequest.put(channel, new ArrayList());
                            flag = 1;
                            break;
                            
                        }
                        if (flag == 1) {
                            continue;
                        }
                        ByteBuffer b = ByteBuffer.allocate(32);
                        byte[] bytes = s.getBytes( Charset.forName("UTF-8" ));
                        b = ByteBuffer.wrap(bytes);
                        b.compact();
                        b.flip();
                        a = channel.write(b);
                    }
                    
                    pendingRequest.put(channel, new ArrayList());           
                    break;
                    
                } 
            }
            
        }
    } 
}

