package io.github.cciglesiasmartinez.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.nio.channels.SocketChannel;

/**
 * This class represents an inbound TCP request meant to be placed in a {@link java.util.concurrent.BlockingDeque}
 * pending to be processed by any of the threads available.
 *
 * This class is thought to have two fields, be:
 * <ul>
 *     <li><b>client</b> The instance from {@link SocketChannel}.</li>
 *     <li><b>rawCommand</b> An {@link String} object containing the raw line.</li>
 * </ul>
 * <p>
 *     Objects from this class are though to be parsed into a fully parsed an ready to dispatch {@link IrcCommand}.
 * </p>
 */
record IrcRequest(SocketChannel client, String rawCommand) {}