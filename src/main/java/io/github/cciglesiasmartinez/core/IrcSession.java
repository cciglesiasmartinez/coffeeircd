package io.github.cciglesiasmartinez.core;

import lombok.AllArgsConstructor;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class IrcSession {

    private final SocketChannel channel;
    private final StringBuilder readBuffer = new StringBuilder();

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
