package io.github.cciglesiasmartinez.core;

import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class IrcSession {

    private final SocketChannel channel;
    private final StringBuilder readBuffer = new StringBuilder();

    private volatile String nick;
    private volatile String username;

    public IrcSession(SocketChannel channel) {
        this.channel = channel;
    }

    public List<String> onDataReceived(ByteBuffer buffer, int bytesRead) {
        String chunk = new String(buffer.array(), 0, bytesRead);
        readBuffer.append(chunk);
        List<String> lines = new ArrayList<>();
        int index;
        while ((index = readBuffer.indexOf("\r\n")) != -1) {
            lines.add(readBuffer.substring(0, index));
            readBuffer.delete(0, index + 2);
        }
        return lines;
    }
}
