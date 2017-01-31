package de.inventivegames.packetlistener.handler;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @deprecated Please use {@link org.inventivetalent.packetlistener.handler.PacketHandler}
 */
@Deprecated public abstract class PacketHandler {

	static Map<PacketHandler, org.inventivetalent.packetlistener.handler.PacketHandler> compatibilityMap = new HashMap<>();

	@Deprecated
	public static boolean addHandler(final PacketHandler handler) {
		if (handler.getPlugin() != null) {
			System.out.println("[PacketListenerAPI] " + handler.getPlugin().getName() + " by " + handler.getPlugin().getDescription().getAuthors() + " uses a deprecated PacketHandler!");
		}
		org.inventivetalent.packetlistener.handler.PacketHandler compatibilityHandler = new org.inventivetalent.packetlistener.handler.PacketHandler() {
			@Override
			public void onSend(final org.inventivetalent.packetlistener.handler.SentPacket packet) {
				handler.onSend(new SentPacket(packet.getPacket(), new de.inventivegames.packetlistener.Cancellable() {
					@Override
					public boolean isCancelled() {
						return packet.isCancelled();
					}

					@Override
					public void setCancelled(boolean paramBoolean) {
						packet.setCancelled(paramBoolean);
					}
				}, packet.getPlayer()));
			}

			@Override
			public void onReceive(final org.inventivetalent.packetlistener.handler.ReceivedPacket packet) {
				handler.onReceive(new ReceivedPacket(packet.getPacket(), new de.inventivegames.packetlistener.Cancellable() {
					@Override
					public boolean isCancelled() {
						return packet.isCancelled();
					}

					@Override
					public void setCancelled(boolean paramBoolean) {
						packet.setCancelled(paramBoolean);
					}
				}, packet.getPlayer()));
			}
		};
		compatibilityMap.put(handler, compatibilityHandler);
		return org.inventivetalent.packetlistener.handler.PacketHandler.addHandler(compatibilityHandler);
	}

	@Deprecated
	public static boolean removeHandler(PacketHandler handler) {
		boolean b = org.inventivetalent.packetlistener.handler.PacketHandler.removeHandler(compatibilityMap.get(handler));
		compatibilityMap.remove(handler);
		return b;
	}

	@Deprecated
	public static List<PacketHandler> getHandlers() {
		return new ArrayList<>(compatibilityMap.keySet());
	}

	@Deprecated
	public void sendPacket(Player p, Object packet) {
		compatibilityMap.get(this).sendPacket(p, packet);
	}

	// //////////////////////////////////////////////////

	private Plugin plugin;

	@Deprecated
	public PacketHandler() {
	}

	@Deprecated
	public PacketHandler(Plugin plugin) {
		this.plugin = plugin;
	}

	public Plugin getPlugin() {
		return this.plugin;
	}

	@Deprecated
	public abstract void onSend(SentPacket packet);

	@Deprecated
	public abstract void onReceive(ReceivedPacket packet);

}
