package io.github.cciglesiasmartinez.core;

import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class IrcServerState {

    private final ConcurrentHashMap<SocketChannel, IrcSession> sessions = new ConcurrentHashMap<>();

    public void addSession(IrcSession session) {
        this.sessions.putIfAbsent(session.getChannel(), session);
    }

    public void removeSession(IrcSession session) {
        this.sessions.remove(session.getChannel());
    }

    public IrcSession getSession(SocketChannel channel) {
        return this.sessions.get(channel);
    }

    public Collection<IrcSession> getAllSessions() {
        return sessions.values();
    }

}
