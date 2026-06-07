package io.github.cciglesiasmartinez.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class IrcServer {

    private final int port;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private final ConcurrentHashMap<SocketChannel, IrcSession> sessions = new ConcurrentHashMap<>();

    public IrcServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server listening on port " + port);
        loop();
    }

    private void loop() throws IOException {
        while (true) {
            selector.select(); // Blocks til something happens
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while(keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove(); // Important to remove processed key
                if(key.isAcceptable()) {
                    handleAccept();
                }
               else if (key.isReadable()) {
                    handleReadable(key);
                }
            }
        }
    }

    private void handleAccept() throws IOException {
        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        IrcSession ircSession = new IrcSession(client);
        this.sessions.put(client, ircSession);
        System.out.println("Client connected: " + client.getLocalAddress());
    }

    private void handleReadable(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        IrcSession session = (IrcSession) key.attachment();
        session =this.sessions.get(client);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = client.read(buffer);
        if (bytesRead == -1) {
            System.out.println("Client disconnected " + client.getRemoteAddress());
            key.cancel();
            client.close();

            return;
        }
        buffer.flip();
        String message = new String(buffer.array(), 0, bytesRead);
        System.out.println("Received: " + message);
        List<String> lines = session.onDataReceived(buffer, bytesRead);
        for (String line: lines) {
            System.out.println("IRC message -> " + line);
        }
    }
}
