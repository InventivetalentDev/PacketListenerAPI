package org.inventivetalent.packetlistener.channel;

import java.net.SocketAddress;

/**
 * Wraps io.nettty.Channel or net.minecraft.util.io.netty.Channel
 */
public class ChannelWrapper<T> {

	private T channel;

	public ChannelWrapper(T channel) {
		this.channel = channel;
	}

	/**
	 * @return the raw channel object
	 */
	public T channel() {
		return this.channel;
	}

	/**
	 * @return the remote {@link SocketAddress}
	 */
	public SocketAddress getRemoteAddress() {
		return null;
	}

	/**
	 * @return the local {@link SocketAddress}
	 */
	public SocketAddress getLocalAddress() {
		return null;
	}

}
