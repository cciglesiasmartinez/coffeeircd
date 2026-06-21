package io.github.cciglesiasmartinez.core;

import java.util.concurrent.BlockingDeque;

public class IrcCommandWorker implements Runnable {

    private final BlockingDeque<IrcRequest> queue;
    private final IrcServerState state;

    public IrcCommandWorker(BlockingDeque<IrcRequest> queue, IrcServerState state) {
        this.queue = queue;
        this.state = state;
    }

    @Override
    public void run() {
        while (true) {
            try {
                IrcRequest request = queue.take();
                System.out.println("Thread proccessing >> " + request.getRawCommand());
                parseIrcRequest(request);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void parseIrcRequest(IrcRequest request) {
        String raWcommand = request.getRawCommand();
        String[] tokens = raWcommand.split(" ");
        IrcSession session = state.getSession(request.getClient());
        if (session.isRegistered()) {
            switch (tokens[2]) {
                case "PRIVMSG":
                    System.out.println("process PRIVMSG command");
                    break;
            }
        } else {
            switch (tokens[0]) {
                case "NICK":
                    session.setNick(tokens[1]);
                    break;
                case "USER":
                    session.setUsername(tokens[1]);
                    session.setRegistered(true);
                    break;
            }
        }
    }

}
