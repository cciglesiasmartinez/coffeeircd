package io.github.cciglesiasmartinez.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.nio.channels.SocketChannel;

@AllArgsConstructor
@Getter
@Setter
public class IrcCommand {

    private SocketChannel channel;
    private String rawCommand;

}
