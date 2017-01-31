package de.inventivegames.packetlistener.handler;

import de.inventivegames.packetlistener.Cancellable;
import org.bukkit.entity.Player;

/**
 * @deprecated Please use {@link org.inventivetalent.packetlistener.handler.ReceivedPacket}
 */
@Deprecated public class ReceivedPacket extends Packet {

	@Deprecated
	public ReceivedPacket(Object packet, Cancellable cancel, Player player) {
		super(packet, cancel, player);
	}

}
