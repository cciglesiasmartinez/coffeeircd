package io.github.cciglesiasmartinez.core;

import java.util.concurrent.BlockingDeque;

public class IrcCommandWorker implements Runnable{

    private final BlockingDeque<IrcCommand> queue;
    private final IrcServerState state;

    public IrcCommandWorker(BlockingDeque<IrcCommand> queue, IrcServerState state) {
        this.queue = queue;
        this.state = state;
    }

    @Override
    public void run() {
        while (true) {
            try {
                IrcCommand command = queue.take();
                System.out.println("Thread proccessing >> " + command.getRawCommand());
                String raWcommand = command.getRawCommand();
                String[] tokens = raWcommand.split(" ");
                IrcSession session = state.getSession(command.getChannel());
                switch (tokens[0]) {
                    case "NICK":
                        session.setNick(tokens[1]);
                        break;
                    case "USER":
                        session.setUsername(tokens[1]);
                        break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

}
