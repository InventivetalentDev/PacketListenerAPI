package de.inventivegames.packetlistener;

import de.inventivegames.packetlistener.handler.PacketHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Deprecated
public class PacketListenerAPI extends JavaPlugin implements Listener {

	@Deprecated
	public static PacketListenerAPI getInstance() {
		return null;
	}

	@Deprecated
	public static boolean addPacketHandler(PacketHandler handler) {
		return PacketHandler.addHandler(handler);
	}

	@Deprecated
	public static boolean removePacketHandler(PacketHandler handler) {
		return PacketHandler.removeHandler(handler);
	}

}
