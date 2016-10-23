/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Chetan
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
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
    public static Map<Long,Long> myMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        
        Selector selector = Selector.open();
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        InetSocketAddress socketAddress = new InetSocketAddress("localhost", 9090);
       
        socketChannel.bind(socketAddress);
        socketChannel.configureBlocking(false); // For non-blocking
        
        int operations = socketChannel.validOps();
        SelectionKey sKey = socketChannel.register(selector, operations, null);
        
        while (true) {
            selector.select();
            
            Set<SelectionKey> selectKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectKeys.iterator();
            
            while(keyIterator.hasNext()){
                SelectionKey key = keyIterator.next();
                
                if (key.isAcceptable()){
                    // to check if key's channel is ready to accept new connection
                    SocketChannel client = socketChannel.accept();
                    
                    client.configureBlocking(false); // Adjust channel's blocking mode
                    client.register(selector, SelectionKey.OP_READ);
   
                } 
                if (key.isReadable()) {    					
                    SocketChannel client = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(256);
                    client.read(buffer);
                    String result = new String(buffer.array()).trim();
                    
                    String [] parts = result.split(":");
                    
                    if (parts[0].equals("STOP")) {
                        // close the client
                        client.close();
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
                    byte[] bytes = output.getBytes();
                    ByteBuffer source = ByteBuffer.wrap(bytes);
                    SelectionKey key2 = client.register(selector, SelectionKey.OP_WRITE);
                    key2.attach(source);
                }
                
                if (key.isWritable()) {
                    SocketChannel client = (SocketChannel) key.channel();
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    
                    client.write(buffer);
                    
                }
                keyIterator.remove();
            }
        }
    } 
}

