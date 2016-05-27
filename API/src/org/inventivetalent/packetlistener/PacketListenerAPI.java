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
import org.bukkit.plugin.Plugin;
import org.inventivetalent.apihelper.API;
import org.inventivetalent.apihelper.APIManager;
import org.inventivetalent.packetlistener.channel.ChannelWrapper;
import org.inventivetalent.packetlistener.handler.PacketHandler;
import org.inventivetalent.packetlistener.handler.ReceivedPacket;
import org.inventivetalent.packetlistener.handler.SentPacket;

import java.util.logging.Logger;

public class PacketListenerAPI /*extends JavaPlugin*/ implements IPacketListener, Listener, API {

	private ChannelInjector channelInjector;
	protected boolean injected = false;

	Logger logger = Logger.getLogger("PacketListenerAPI");

	//This gets called either by #registerAPI above, or by the API manager if another plugin requires this API
	@Override
	public void load() {
		channelInjector = new ChannelInjector();
		if (injected = channelInjector.inject(this)) {
			channelInjector.addServerChannel();
			logger.info("Injected custom channel handlers.");
		} else {
			logger.severe("Failed to inject channel handlers");
		}

	}

	//This gets called either by #initAPI above or #initAPI in one of the requiring plugins
	@Override
	public void init(Plugin plugin) {
		//Register our events
		APIManager.registerEvents(this, this);

		logger.info("Adding channels for online players...");
		for (Player player : Bukkit.getOnlinePlayers()) {
			channelInjector.addChannel(player);
		}
	}

	//This gets called either by #disableAPI above or #disableAPI in one of the requiring plugins
	@Override
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
