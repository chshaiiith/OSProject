package edu.psu.os.KV511.session;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;

public interface Client {

	Socket initSocket() throws UnknownHostException, IOException;
}
