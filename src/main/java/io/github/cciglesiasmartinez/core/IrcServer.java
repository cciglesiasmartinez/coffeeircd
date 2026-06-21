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
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 *  This is class represents the main server loop. It encloses primarily a NIO {@link Selector} that manages TCP
 *  connections and an {@link ExecutorService} worker thread pool.
 */
public class IrcServer {

    private static final int THREADS = 2;

    private final int port;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private final IrcServerState ircServerState = new IrcServerState();
    private final BlockingDeque<IrcRequest> ircRequestQueue = new LinkedBlockingDeque<>();

    public IrcServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        ExecutorService workerPool = Executors.newFixedThreadPool(THREADS);
        for (int i = 0; i < THREADS; i++) {
            workerPool.submit(new IrcCommandWorker(ircRequestQueue, ircServerState));
        }
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
        IrcSession ircSession = new IrcSession(client);
        this.ircServerState.addSession(ircSession);
        client.register(selector, SelectionKey.OP_READ, ircSession);
        System.out.println("Client connected: " + client.getLocalAddress());
    }

    private void handleReadable(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        IrcSession session = (IrcSession) key.attachment();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = client.read(buffer);
        if (bytesRead == -1) {
            System.out.println("Client disconnected " + client.getRemoteAddress());
            key.cancel();
            this.ircServerState.removeSession(session);
            client.close();
            return;
        }
//        buffer.flip();
        String message = new String(buffer.array(), 0, bytesRead);
        System.out.println("Received: " + message);
        List<String> lines = session.onDataReceived(buffer, bytesRead);
        for (String line: lines) {
            System.out.println("IRC message -> " + line);
            this.ircRequestQueue.add(new IrcRequest(session.getClient(), line));
            System.out.println(this.ircRequestQueue.getLast().getRawCommand());
        }
    }
}
