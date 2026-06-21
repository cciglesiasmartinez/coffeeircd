package io.github.cciglesiasmartinez.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.nio.channels.SocketChannel;

/**
 * This class represents an inbound TCP request meant to be placed in a {@link java.util.concurrent.BlockingDeque}
 * pending to be processed by any of the threads available.
 */
record IrcRequest(SocketChannel client, String rawCommand) {}