/*
 * Copyright 2015-2016 inventivetalent. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and contributors and should not be interpreted as representing official policies,
 *  either expressed or implied, of anybody else.
 */

package org.inventivetalent.packetlistener.handler;

import de.inventivegames.packetlistener.handler.SentPacket;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.inventivetalent.packetlistener.channel.ChannelWrapper;
import org.inventivetalent.reflection.resolver.FieldResolver;

public abstract class PacketAbstract {

	private Player         player;
	private ChannelWrapper channelWrapper;

	private Object      packet;
	private Cancellable cancellable;

	protected FieldResolver fieldResolver;

	public PacketAbstract(Object packet, Cancellable cancellable, Player player) {
		this.player = player;

		this.packet = packet;
		this.cancellable = cancellable;

		fieldResolver = new FieldResolver(packet.getClass());
	}

	public PacketAbstract(Object packet, Cancellable cancellable, ChannelWrapper channelWrapper) {
		this.channelWrapper = channelWrapper;

		this.packet = packet;
		this.cancellable = cancellable;

		fieldResolver = new FieldResolver(packet.getClass());
	}

	/**
	 * Modify a value of the packet
	 *
	 * @param field Name of the field to modify
	 * @param value Value to be assigned to the field
	 */
	public void setPacketValue(String field, Object value) {
		try {
			fieldResolver.resolve(field).set(getPacket(), value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get a value of the packet
	 *
	 * @param field Name of the field
	 * @return current value of the field
	 */
	public Object getPacketValue(String field) {
		Object value = null;
		try {
			value = fieldResolver.resolve(field).get(getPacket());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * @param b if set to <code>true</code> the packet will be cancelled
	 */
	public void setCancelled(boolean b) {
		this.cancellable.setCancelled(b);
	}

	/**
	 * @return <code>true</code> if the packet has been cancelled
	 */
	public boolean isCancelled() {
		return this.cancellable.isCancelled();
	}

	/**
	 * @return The receiving or sending player of the packet
	 * @see #hasPlayer()
	 * @see #getChannel()
	 */
	public Player getPlayer() {
		return this.player;
	}

	/**
	 * @return <code>true</code> if the packet has a player
	 */
	public boolean hasPlayer() {
		return this.player != null;
	}

	/**
	 * @return The receiving or sending channel (wrapped in a {@link ChannelWrapper})
	 * @see #hasChannel()
	 * @see #getPlayer()
	 */
	public ChannelWrapper<?> getChannel() {
		return this.channelWrapper;
	}

	/**
	 * @return <code>true</code> if the packet has a channel
	 */
	public boolean hasChannel() {
		return this.channelWrapper != null;
	}

	/**
	 * @return The name of the receiving or sending player
	 * @see #hasPlayer()
	 * @see #getPlayer()
	 */
	public String getPlayername() {
		if (!this.hasPlayer()) { return null; }
		return this.player.getName();
	}

	/**
	 * Change the packet that is sent
	 *
	 * @param packet new packet
	 */
	public void setPacket(Object packet) {
		this.packet = packet;
	}

	/**
	 * @return the sent or received packet as an Object
	 */
	public Object getPacket() {
		return this.packet;
	}

	/**
	 * @return the class name of the sent or received packet
	 */
	public String getPacketName() {
		return this.packet.getClass().getSimpleName();
	}

	@Override
	public String toString() {
		return "Packet{ " + (this.getClass().equals(SentPacket.class) ? "[> OUT >]" : "[< IN <]") + " " + this.getPacketName() + " " + (this.hasPlayer() ? this.getPlayername() : this.hasChannel() ? this.getChannel().channel() : "#server#") + " }";
	}

}
