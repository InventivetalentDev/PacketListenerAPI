package org.inventivetalent.packetlistener;

public interface IPacketListener {

	Object onPacketSend(Object receiver, Object packet, org.bukkit.event.Cancellable cancellable);

	Object onPacketReceive(Object sender, Object packet, org.bukkit.event.Cancellable cancellable);
}
