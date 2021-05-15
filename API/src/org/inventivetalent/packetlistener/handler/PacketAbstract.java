package org.inventivetalent.packetlistener.handler;

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
		fieldResolver.resolveAccessor(field).set(getPacket(), value);
	}

	/**
	 * Modify a value of the packet (without throwing an exception)
	 *
	 * @param field Name of the field to modify
	 * @param value Value to be assigned to the field
	 */
	public void setPacketValueSilent(String field, Object value) {
		try {
			setPacketValue(field, value);
		} catch (Exception e) {
		}
	}

	/**
	 * Modify a value of the packet
	 *
	 * @param index field-index in the packet class
	 * @param value value to be assigned to the field
	 */
	public void setPacketValue(int index, Object value) {
		try {
			fieldResolver.resolveIndexAccessor(index).set(getPacket(), value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Modify a value of the packet (without throwing an exception)
	 *
	 * @param index field-index in the packet class
	 * @param value value to be assigned to the field
	 */
	public void setPacketValueSilent(int index, Object value) {
		try {
			setPacketValue(index, value);
		} catch (Exception e) {
		}
	}

	/**
	 * Get a value of the packet
	 *
	 * @param field Name of the field
	 * @return current value of the field
	 */
	public Object getPacketValue(String field) {
		return fieldResolver.resolveAccessor(field).get(getPacket());
	}

	/**
	 * Get a value of the packet (without throwing an exception)
	 *
	 * @param field Name of the field
	 * @return current value of the field
	 */
	public Object getPacketValueSilent(String field) {
		try {
			return getPacketValue(field);
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * Get a value of the packet
	 *
	 * @param index field-index in the packet class
	 * @return value of the field
	 */
	public Object getPacketValue(int index) {
		return fieldResolver.resolveIndexAccessor(index).get(getPacket());
	}

	/**
	 * Get a value of the packet (without throwing an exception)
	 *
	 * @param index field-index in the packet class
	 * @return value of the field
	 */
	public Object getPacketValueSilent(int index) {
		try {
			return getPacketValue(index);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public FieldResolver getFieldResolver() {
		return fieldResolver;
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
