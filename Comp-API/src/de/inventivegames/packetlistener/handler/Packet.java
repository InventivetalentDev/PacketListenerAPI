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

package de.inventivegames.packetlistener.handler;

import de.inventivegames.packetlistener.Cancellable;
import org.bukkit.entity.Player;
import org.inventivetalent.reflection.resolver.FieldResolver;

/**
 * @deprecated Please use {@link org.inventivetalent.packetlistener.handler.PacketAbstract}
 */
@Deprecated public abstract class Packet {

	private Player      player;
	private Object      packet;
	private Cancellable cancel;

	protected FieldResolver fieldResolver;

	public Packet(Object packet, Cancellable cancel, Player player) {
		this.player = player;
		this.packet = packet;
		this.cancel = cancel;

		fieldResolver = new FieldResolver(packet.getClass());
	}

	/**
	 * Modify a value of the packet
	 *
	 * @param field Name of the field to modify
	 * @param value Value to be assigned to the field
	 */
	@Deprecated
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
	@Deprecated
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
	@Deprecated
	public void setCancelled(boolean b) {
		this.cancel.setCancelled(b);
	}

	/**
	 * @return <code>true</code> if the packet has been cancelled
	 */
	@Deprecated
	public boolean isCancelled() {
		return this.cancel.isCancelled();
	}

	/**
	 * @return The receiving or sending player of the packet
	 * @see #hasPlayer()
	 */
	@Deprecated
	public Player getPlayer() {
		return this.player;
	}

	/**
	 * @return <code>true</code> if the packet has a player
	 */
	@Deprecated
	public boolean hasPlayer() {
		return this.player != null;
	}

	/**
	 * @return The name of the receiving or sending player
	 * @see #hasPlayer()
	 * @see #getPlayer()
	 */
	@Deprecated
	public String getPlayername() {
		if (!this.hasPlayer()) { return null; }
		return this.player.getName();
	}

	/**
	 * Change the packet that is sent
	 *
	 * @param packet new packet
	 */
	@Deprecated
	public void setPacket(Object packet) {
		this.packet = packet;
	}

	/**
	 * @return the sent or received packet as an Object
	 */
	@Deprecated
	public Object getPacket() {
		return this.packet;
	}

	/**
	 * @return the class name of the sent or received packet
	 */
	@Deprecated
	public String getPacketName() {
		return this.packet.getClass().getSimpleName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.cancel == null ? 0 : this.cancel.hashCode());
		result = prime * result + (this.packet == null ? 0 : this.packet.hashCode());
		result = prime * result + (this.player == null ? 0 : this.player.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (this.getClass() != obj.getClass()) { return false; }
		Packet other = (Packet) obj;
		if (this.cancel == null) {
			if (other.cancel != null) { return false; }
		} else if (!this.cancel.equals(other.cancel)) { return false; }
		if (this.packet == null) {
			if (other.packet != null) { return false; }
		} else if (!this.packet.equals(other.packet)) { return false; }
		if (this.player == null) {
			if (other.player != null) { return false; }
		} else if (!this.player.equals(other.player)) { return false; }
		return true;
	}

	@Override
	public String toString() {
		return "Packet{ " + (this.getClass().equals(SentPacket.class) ? "[> OUT >]" : "[< IN <]") + " " + this.getPacketName() + " " + (this.hasPlayer() ? this.getPlayername() : "#server#") + " }";
	}

}
