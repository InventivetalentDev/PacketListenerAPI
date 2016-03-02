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

package org.inventivetalent.packetlistener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.packetlistener.handler.PacketHandler;
import org.inventivetalent.packetlistener.handler.ReceivedPacket;
import org.inventivetalent.packetlistener.handler.SentPacket;
import org.mcstats.MetricsLite;

public class PacketListenerAPI extends JavaPlugin implements IPacketListener, Listener {

	private ChannelInjector channelInjector;
	private boolean injected = false;

	@Override
	public void onLoad() {
		channelInjector = new ChannelInjector();
		if (injected = channelInjector.inject(this)) {
			channelInjector.addServerChannel();
			getLogger().info("Injected custom channel handlers.");
		} else {
			getLogger().severe("Failed to inject channel handlers");
		}
	}

	@Override
	public void onEnable() {
		if (!injected) {
			getLogger().warning("Injection failed. Disabling...");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		Bukkit.getPluginManager().registerEvents(this, this);

		try {
			MetricsLite metrics = new MetricsLite(this);
			if (metrics.start()) {
				getLogger().info("Metrics started");
			}
		} catch (Exception e) {
		}

		getLogger().info("Adding channels for online players...");
		for (Player player : Bukkit.getOnlinePlayers()) {
			channelInjector.addChannel(player);
		}
	}

	@Override
	public void onDisable() {
		if (!injected) {
			return;//Not enabled
		}
		getLogger().info("Removing channels for online players...");
		for (Player player : Bukkit.getOnlinePlayers()) {
			channelInjector.removeChannel(player);
		}

		getLogger().info("Removing packet handlers (" + PacketHandler.getHandlers().size() + ")...");
		while (!PacketHandler.getHandlers().isEmpty()) {
			PacketHandler.removeHandler(PacketHandler.getHandlers().get(0));
		}
	}

	/**
	 * @see PacketHandler#addHandler(PacketHandler)
	 */
	public static boolean addPacketHandler(PacketHandler handler) {
		return PacketHandler.addHandler(handler);
	}

	/**
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
	public Object onPacketReceive(Player player, Object packet, Cancellable cancellable) {
		ReceivedPacket receivedPacket = new ReceivedPacket(packet, cancellable, player);
		PacketHandler.notifyHandlers(receivedPacket);
		if (receivedPacket.getPacket() != null) { return receivedPacket.getPacket(); }
		return packet;
	}

	@Override
	public Object onPacketSend(Player player, Object packet, Cancellable cancellable) {
		SentPacket sentPacket = new SentPacket(packet, cancellable, player);
		PacketHandler.notifyHandlers(sentPacket);
		if (sentPacket.getPacket() != null) { return sentPacket.getPacket(); }
		return packet;
	}
}
