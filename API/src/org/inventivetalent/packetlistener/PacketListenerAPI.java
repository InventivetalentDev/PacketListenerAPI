package org.inventivetalent.packetlistener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.inventivetalent.packetlistener.channel.ChannelWrapper;
import org.inventivetalent.packetlistener.handler.PacketHandler;
import org.inventivetalent.packetlistener.handler.ReceivedPacket;
import org.inventivetalent.packetlistener.handler.SentPacket;

import java.util.logging.Logger;

public class PacketListenerAPI /*extends JavaPlugin*/ implements IPacketListener, Listener {

	private ChannelInjector channelInjector;
	protected boolean injected = false;

	Logger logger = Logger.getLogger("PacketListenerAPI");

	//This gets called either by #registerAPI above, or by the API manager if another plugin requires this API
	public void load() {
		if (injected) {
			System.err.println("Already Injected! Restart your server, do not /reload");
		}

		channelInjector = new ChannelInjector();
		if (injected = channelInjector.inject(this)) {
			channelInjector.addServerChannel();
			logger.info("Injected custom channel handlers.");
		} else {
			logger.severe("Failed to inject channel handlers");
		}

	}

	//This gets called either by #initAPI above or #initAPI in one of the requiring plugins
	public void init(Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);

		logger.info("Adding channels for online players...");
		for (Player player : Bukkit.getOnlinePlayers()) {
			channelInjector.addChannel(player);
		}
	}

	//This gets called either by #disableAPI above or #disableAPI in one of the requiring plugins
	public void disable(Plugin plugin) {
		if (!injected) {
			return;//Not enabled
		}
		logger.info("Removing channels for online players...");
		for (Player player : Bukkit.getOnlinePlayers()) {
			channelInjector.removeChannel(player);
		}

		logger.info("Removing packet handlers (" + PacketHandler.getHandlers().size() + ")...");
		while (!PacketHandler.getHandlers().isEmpty()) {
			PacketHandler.removeHandler(PacketHandler.getHandlers().get(0));
		}
	}

	/**
	 * @param handler PacketHandler to add
	 * @return <code>true</code> if the handler was added
	 * @see PacketHandler#addHandler(PacketHandler)
	 */
	public static boolean addPacketHandler(PacketHandler handler) {
		return PacketHandler.addHandler(handler);
	}

	/**
	 * @param handler PacketHandler to remove
	 * @return <code>true</code> if the handler was removed
	 * @see PacketHandler#removeHandler(PacketHandler)
	 */
	public static boolean removePacketHandler(PacketHandler handler) {
		return PacketHandler.removeHandler(handler);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		channelInjector.addChannel(e.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		channelInjector.removeChannel(e.getPlayer());
	}

	@Override
	public Object onPacketReceive(Object sender, Object packet, Cancellable cancellable) {
		ReceivedPacket receivedPacket;
		if (sender instanceof Player) {
			receivedPacket = new ReceivedPacket(packet, cancellable, (Player) sender);
		} else {
			receivedPacket = new ReceivedPacket(packet, cancellable, (ChannelWrapper) sender);
		}
		PacketHandler.notifyHandlers(receivedPacket);
		if (receivedPacket.getPacket() != null) { return receivedPacket.getPacket(); }
		return packet;
	}

	@Override
	public Object onPacketSend(Object receiver, Object packet, Cancellable cancellable) {
		SentPacket sentPacket;
		if (receiver instanceof Player) {
			sentPacket = new SentPacket(packet, cancellable, (Player) receiver);
		} else {
			sentPacket = new SentPacket(packet, cancellable, (ChannelWrapper) receiver);
		}
		PacketHandler.notifyHandlers(sentPacket);
		if (sentPacket.getPacket() != null) { return sentPacket.getPacket(); }
		return packet;
	}
}
